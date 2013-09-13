/**
 * Copyright (C) 2013 CLXY Studio.
 * This content is released under the (Link Goes Here) MIT License.
 * http://en.wikipedia.org/wiki/MIT_License
 */
package cn.clxy.upload;

import java.io.File;
import java.util.concurrent.Callable;

public class NotifyTask implements Callable<String> {

	private File file;
	private Uploader uploader;

	public NotifyTask(File file, Uploader uploader) {
		this.file = file;
		this.uploader = uploader;
	}

	@Override
	public String call() throws Exception {

		long length = file.length();
		long partCount = (length / Config.partSize) + (length % Config.partSize == 0 ? 0 : 1);
		uploader.done(file.getName(), partCount);
		return "notify";
	}
}
