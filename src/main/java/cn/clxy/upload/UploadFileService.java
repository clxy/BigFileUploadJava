/**
 * Copyright (C) 2013 CLXY Studio.
 * This content is released under the (Link Goes Here) MIT License.
 * http://en.wikipedia.org/wiki/MIT_License
 */
package cn.clxy.upload;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UploadFileService {

	private File file;
	private BlockingQueue<Part> parts;
	private List<Integer> indexes;

	private Uploader uploader = new ApacheHCUploader();
	private ExecutorService executor = Executors.newFixedThreadPool(Config.maxUpload);

	private static final Log log = LogFactory.getLog(UploadFileService.class);

	public UploadFileService(String fileName) {
		file = new File(fileName);
		if (!file.exists() || !file.isFile()) {
			throw new RuntimeException("File:" + file + " isn't correct!");
		}
	}

	public void upload() {
		try {
			doUpload();
		} finally {
			stop();
		}
	}

	public void retry(Integer... array) {

		// sort first.
		indexes = Arrays.asList(array);
		Collections.sort(indexes);

		try {
			doUpload();
		} finally {
			stop();
		}
	}

	public void stop() {
		if (executor != null) {
			executor.shutdown();
		}
	}

	private void doUpload() {

		log.debug("Start! ===--------------------");

		parts = new ArrayBlockingQueue<Part>(Config.maxRead);
		CompletionService<String> cs = new ExecutorCompletionService<String>(executor);

		log.debug("Reading started.");
		cs.submit(readTask);

		log.debug("Uploading started.");

		for (int i = 0; i < Config.maxUpload; i++) {
			cs.submit(new UploadTask("upload." + i));
		}

		// Wait all done. total count = maxUpload + 1.
		for (int i = 0; i <= Config.maxUpload; i++) {
			Future<String> future = null;
			try {
				future = cs.take();
				checkFuture(future);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		// Notify sever all done.
		Future<String> result = executor.submit(notifyTask);
		checkFuture(result);

		log.debug("End! ===--------------------");
	}

	private static String checkFuture(Future<String> future) {

		try {
			String result = future.get();
			log.debug(result + " is done.");
			return result;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return null;
		} catch (ExecutionException e) {
			throw new RuntimeException(e.getCause());
		}
	}

	private Callable<String> readTask = new Callable<String>() {

		@Override
		public String call() throws Exception {

			FileInputStream fis = null;
			String fileName = file.getName();
			int partSize = Config.partSize;

			try {
				fis = new FileInputStream(file);
				FileChannel fc = fis.getChannel();
				for (int i = 0;; i++) {
					ReadStatus status = getReadStatus(i, indexes);
					if (status == ReadStatus.stop) {
						break;
					}

					if (status == ReadStatus.skip) {
						fc.position(fc.position() + partSize);
						continue;
					}

					if (status == ReadStatus.read) {
						ByteBuffer bb = ByteBuffer.allocate(partSize);
						int bytesRead = fc.read(bb);
						if (bytesRead == -1) {
							break;
						}
						byte[] bytes = bb.array();
						if (bytesRead != partSize) {// trim
							bytes = Arrays.copyOf(bytes, bytesRead);
						}
						parts.put(new Part(createFileName(fileName, i), bytes));
					}
				}
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (Exception e) {
					}
				}
				parts.put(Part.NULL);// put end signal.
			}

			return "read";
		}

		/**
		 * Create file name of part. <br>
		 * bigfile.avi = [bigfile.avi<strong>.0</strong>, bigfile.avi.1, bigfile.avi.2 ...]
		 * @param fileName
		 * @param i
		 * @return
		 */
		protected String createFileName(String fileName, int i) {
			return fileName + "." + i;// start by 0.
			// return fileName + (i == 0 ? "" : ("." + i));
		}

		private ReadStatus getReadStatus(int i, List<Integer> indexes) {

			if (indexes == null || indexes.contains(i)) {
				return ReadStatus.read;
			}

			if (i > indexes.get(indexes.size() - 1)) {
				return ReadStatus.stop;
			}

			return ReadStatus.skip;
		}
	};

	private static enum ReadStatus {
		stop, skip, read
	}

	private class UploadTask implements Callable<String> {

		private String name;

		public UploadTask(String name) {
			this.name = name;
		}

		@Override
		public String call() throws Exception {
			while (true) {
				Part part = parts.take();
				if (part == Part.NULL) {
					parts.add(Part.NULL);// notify others to stop.
					break;
				}
				uploader.upload(part);
			}
			return name;
		}
	}

	private Callable<String> notifyTask = new Callable<String>() {
		@Override
		public String call() throws Exception {
			long length = file.length();
			long partCount = (length / Config.partSize) + (length % Config.partSize == 0 ? 0 : 1);
			uploader.done(file.getName(), partCount);
			return "notify";
		}
	};
}
