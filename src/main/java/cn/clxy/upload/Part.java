/**
 * Copyright (C) 2013 CLXY Studio.
 * This content is released under the (Link Goes Here) MIT License.
 * http://en.wikipedia.org/wiki/MIT_License
 */
package cn.clxy.upload;

public class Part {

	private byte[] content;
	private String name;
	public static final Part NULL = new Part();

	public Part() {
		this(null, null);
	}

	public Part(String name, byte[] content) {
		this.content = content;
		this.name = name;
	}

	public byte[] getContent() {
		return content;
	}

	public String getName() {
		return name;
	}
}
