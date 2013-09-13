/**
 * Copyright (C) 2013 CLXY Studio.
 * This content is released under the (Link Goes Here) MIT License.
 * http://en.wikipedia.org/wiki/MIT_License
 */
package cn.clxy.upload;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
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
			doUpload(null);
		} finally {
			stop();
		}
	}

	public void retry(Integer... indexes) {

		// sort first.
		List<Integer> list = Arrays.asList(indexes);
		Collections.sort(list);

		try {
			doUpload(list);
		} finally {
			stop();
		}
	}

	public void stop() {
		if (executor != null) {
			executor.shutdown();
		}
	}

	private void doUpload(final List<Integer> indexes) {

		log.debug("Start! ===--------------------");

		BlockingQueue<Part> parts = new ArrayBlockingQueue<Part>(Config.maxRead);
		CompletionService<String> cs = new ExecutorCompletionService<String>(executor);

		log.debug("Reading started.");
		cs.submit(new ReadTask(file, indexes, parts));

		log.debug("Uploading started.");

		for (int i = 0; i < Config.maxUpload; i++) {
			cs.submit(new UploadTask("upload." + i, uploader, parts));
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
		Future<String> result = executor.submit(new NotifyTask(file, uploader));
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
}
