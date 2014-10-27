package com.duckduckgo.mobile.android.util;

public enum CLEAR_INTERVAL_TYPE {
    EVERY_LAUNCH(0), WEEKLY(1), MONTHLY(2), NEVER(3);

    private int code;

    private CLEAR_INTERVAL_TYPE(int c) {
        code = c;
    }

    public int getCode() {
        return code;
    }

    public static CLEAR_INTERVAL_TYPE getCodeBy(int code) {
        switch(code) {
            case 0:
                return EVERY_LAUNCH;
            case 1:
                return WEEKLY;
            case 2:
                return MONTHLY;
            case 3:
                return NEVER;
            default:
                return NEVER;
        }
    }

}
