/**
 * Copyright (C) 2013 CLXY Studio.
 * This content is released under the (Link Goes Here) MIT License.
 * http://en.wikipedia.org/wiki/MIT_License
 */
package cn.clxy.upload;

import cn.clxy.upload.UploadFileService.Part;

public interface Uploader {

	/**
	 * Upload a part.
	 * @param part
	 */
	void upload(Part part);

	/**
	 * Notify server all uploading is done.
	 * @param fileName
	 * @param partCount
	 */
	void done(String fileName, long partCount);
}
