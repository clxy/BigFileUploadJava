/**
 * Copyright (C) 2013 CLXY Studio.
 * This content is released under the (Link Goes Here) MIT License.
 * http://en.wikipedia.org/wiki/MIT_License
 */
package cn.clxy.upload;

public class Config {

	public static final String URL = "http://192.168.1.101:8080/ssm/common/upload";

	// Keys used by server.
	public static final String KEY_FILE = "file";
	public static final String KEY_FILE_NAME = "fileName";
	public static final String PART_COUNT = "partCount";

	// Upload threads and timeout per thread. Should be adjusted by network condition.
	public static final int MAX_UPLOAD = 5;
	public static final int PART_UPLOAD_TIMEOUT = 120 * 1000;

	// The size of part.
	public static final int PART_SIZE = 100 * 1024;
	public static final int MAX_READ = 5;
}
