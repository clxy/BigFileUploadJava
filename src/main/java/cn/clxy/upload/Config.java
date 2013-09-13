/**
 * Copyright (C) 2013 CLXY Studio.
 * This content is released under the (Link Goes Here) MIT License.
 * http://en.wikipedia.org/wiki/MIT_License
 */
package cn.clxy.upload;

import java.io.InputStream;
import java.util.Properties;

public class Config {

	public static String url = "http://192.168.1.99:8080/ssm/common/upload";

	// Keys used by server.
	public static String keyFile = "file";
	public static String keyFileName = "fileName";
	public static String keyPartCount = "partCount";

	// Upload threads and timeout per thread. Should be adjusted by network condition.
	public static int maxUpload = 5;
	public static int timeOut = 120 * 1000;

	// The size of part.
	public static int partSize = 100 * 1024;
	public static int maxRead = 5;

	static {
		// load properties
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		try {
			InputStream is = cl.getResourceAsStream("bigFileUpload.properties");
			if (is != null) {
				Properties p = new Properties();
				p.load(is);
				url = p.getProperty("url");
				keyFile = p.getProperty("keyFile");
				keyFileName = p.getProperty("keyFileName");
				keyPartCount = p.getProperty("keyPartCount");

				String s = p.getProperty("maxUpload");
				maxUpload = Integer.parseInt(s);
				s = p.getProperty("timeOut");
				timeOut = Integer.parseInt(s) * 1000;
				s = p.getProperty("partSize");
				partSize = Integer.parseInt(s) * 1024;
				s = p.getProperty("maxRead");
				maxRead = Integer.parseInt(s);

			}
		} catch (Exception e) {
			// do nothing.
		}

	}

	private Config() {
	}
}
