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
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import cn.clxy.upload.UploadFileService.Part;

/**
 * Read big file into several parts.
 * @author clxy
 */
public class ReadTask implements Callable<String> {

	private File file;
	private BlockingQueue<Part> parts;

	/**
	 * Limited part indexes. Only these parts will be read if specified.
	 */
	private List<Integer> indexes;

	public ReadTask(File file, List<Integer> indexes, BlockingQueue<Part> parts) {
		this.file = file;
		this.parts = parts;
		this.indexes = indexes;
	}

	@Override
	public String call() throws Exception {

		FileInputStream fis = null;
		String fileName = file.getName();
		int partSize = Config.PART_SIZE;

		try {
			fis = new FileInputStream(file);
			FileChannel fc = fis.getChannel();
			for (int i = 0;; i++) {
				Status status = getReadStatus(i, indexes);
				if (status == Status.stop) {
					break;
				}

				if (status == Status.skip) {
					fc.position(fc.position() + partSize);
					continue;
				}

				if (status == Status.read) {
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
	 * Create file name of part. bigfile.avi = [bigfile.avi, bigfile.avi.1, bigfile.avi.2 ...]
	 * @param fileName
	 * @param i
	 * @return
	 */
	protected String createFileName(String fileName, int i) {
		return fileName + (i == 0 ? "" : ("." + i));
	}

	private Status getReadStatus(int i, List<Integer> indexes) {

		if (indexes == null || indexes.contains(i)) {
			return Status.read;
		}

		if (i > indexes.get(indexes.size() - 1)) {
			return Status.stop;
		}

		return Status.skip;
	}

	private static enum Status {
		stop, skip, read
	}
}
