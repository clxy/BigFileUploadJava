/**
 * Copyright (C) 2013 CLXY Studio.
 * This content is released under the (Link Goes Here) MIT License.
 * http://en.wikipedia.org/wiki/MIT_License
 */
package cn.clxy.upload;

public class UploadFileServiceTest {

	public static void main(String[] args) {

		UploadFileService service = new UploadFileService("D:\\test.mp3");
		try {
			service.retry(1, 2);
			// service.upload();
		} catch (Exception e) {
			service.retry(1, 2);
			e.printStackTrace();
		}
	}
}
