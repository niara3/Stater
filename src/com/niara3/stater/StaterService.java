package com.niara3.stater;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class StaterService extends Service {

	private static final int ID_FOREGROUND = 1;
	private StaterReceiver mReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		Logger lgr = new Logger(this);
		lgr.d("onBind");
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Logger lgr = new Logger(this);
		lgr.d("onStartCommand");
		return Service.START_NOT_STICKY;	// debug
	}

	@Override
	public void onCreate() {
		Logger lgr = new Logger(this);
		lgr.d("onCreate");
		super.onCreate();
		try {
			startForeground();
			startReceive();
		} catch (Exception e) {
			lgr.e("onCreate", e);
		}
	}

	@Override
	public void onDestroy() {
		Logger lgr = new Logger(this);
		lgr.d("onDestroy");
		super.onDestroy();
		try {
			stopReceive();
		} catch (Exception e) {
			lgr.e("stopReceive", e);
		}
		try {
			stopForeground();
		} catch (Exception e) {
			lgr.e("stopForeground", e);
		}
	}

	private void startForeground() {
		Logger lgr = new Logger(this);
		lgr.d("startForeground");
		Notification notification = new Notification();
		startForeground(ID_FOREGROUND, notification);
	}

	private void stopForeground() {
		Logger lgr = new Logger(this);
		lgr.d("stopForeground");
		stopForeground(true);
	}

	private void startReceive() {
		Logger lgr = new Logger(this);
		lgr.d("startReceive");
		if (mReceiver != null) {
			return;
		}
		mReceiver = new StaterReceiver();
		lgr.d("registerReceiver");
		registerReceiver(mReceiver, mReceiver.getFilter());
	}

	private void stopReceive() {
		Logger lgr = new Logger(this);
		lgr.d("stopReceive");
		if (mReceiver == null) {
			return;
		}
		lgr.d("unregisterReceiver");
		unregisterReceiver(mReceiver);
		mReceiver = null;
	}
}
