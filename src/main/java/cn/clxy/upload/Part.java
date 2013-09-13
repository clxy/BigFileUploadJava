/**
 * Copyright (C) 2013 CLXY Studio.
 * This content is released under the (Link Goes Here) MIT License.
 * http://en.wikipedia.org/wiki/MIT_License
 */
package cn.clxy.upload;

public class Part {

	private byte[] content;
	private String fileName;
	public static final Part NULL = new Part();

	public Part() {
		this(null, null);
	}

	public Part(String fileName, byte[] content) {
		this.content = content;
		this.fileName = fileName;
	}

	public byte[] getContent() {
		return content;
	}

	public String getFileName() {
		return fileName;
	}
}
