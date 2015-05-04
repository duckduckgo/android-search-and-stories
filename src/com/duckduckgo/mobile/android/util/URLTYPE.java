package com.duckduckgo.mobile.android.util;

public enum URLTYPE {

	FEED(0), SERP(1), WEBPAGE(2);

	private int code;

	private URLTYPE(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static URLTYPE getByCode(int code) {
		switch(code) {
			case 0:
				return FEED;
			case 1:
				return SERP;
			case 2:
				return WEBPAGE;
			default:
				return WEBPAGE;
		}
	}
}
