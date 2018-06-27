package com.scrapper.scrapper;

public class HTMLConst {

	public static final class Tag {
		public static final String A = "a";
		public static final String SPAN = "span";
		public static final String DIV = "div";
	}

	public static final class Attribute {
		public static final String HREF = "href";

	}

	public static final String CLASS = ".";

	public static String combineByClass(String tag, String name) {
		StringBuilder sb = new StringBuilder();
		return sb.append(tag).append(CLASS).append(name).toString();
	}
}
