/**
 * Copyright (C) 2013 CLXY Studio.
 * This content is released under the (Link Goes Here) MIT License.
 * http://en.wikipedia.org/wiki/MIT_License
 */
package cn.clxy.upload;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import cn.clxy.upload.UploadFileService.Part;

public class UploadTask implements Callable<String> {

	private String name;
	private Uploader uploader;
	private BlockingQueue<Part> parts;

	public UploadTask(String name, Uploader uploader, BlockingQueue<Part> parts) {
		this.name = name;
		this.uploader = uploader;
		this.parts = parts;
	}

	@Override
	public String call() throws Exception {
		while (true) {
			Part part = parts.take();
			if (part == Part.NULL) {
				parts.add(Part.NULL);// notify others to stop.
				break;
			} else {
				uploader.upload(part);
			}
		}
		return name;
	}
}