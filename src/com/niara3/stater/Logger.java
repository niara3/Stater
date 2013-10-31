package com.niara3.stater;

import android.util.Log;

public class Logger {
	private static final String LOGGER_DEBUG = "LoggerDebug";
	private static final String UNKNOWN_CLASS = "UnknownCalss";
	private String mTag;

	public Logger() {
		try {
			Throwable t = new Throwable();
			// [0]は自分自身、[1]が呼び出し元。
			StackTraceElement element = t.getStackTrace()[1];
			Class<? extends StackTraceElement> c = element.getClass();
			mTag = c.getSimpleName();
		} catch (Exception e) {
			Log.w(LOGGER_DEBUG, e);
			mTag = UNKNOWN_CLASS;
		}
	}
	public Logger(Object obj) {
		try {
			Class<? extends Object> c = obj.getClass();
			mTag = c.getSimpleName();
		} catch (Exception e) {
			Log.w(LOGGER_DEBUG, e);
			mTag = UNKNOWN_CLASS;
		}
	}

	public void i(Object obj) {
		Log.i(mTag, String.valueOf(obj));
	}

	public void d(Object obj) {
		Log.d(mTag, String.valueOf(obj));
	}

	public void w(Object obj) {
		Log.w(mTag, String.valueOf(obj));
	}

	public void e(Object obj, Throwable t) {
		Log.e(mTag, String.valueOf(obj), t);
	}
}
