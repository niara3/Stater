package com.niara3.stater;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;

public class StaterReceiver extends BroadcastReceiver {

	private static final String BASE = StaterReceiver.class.getName();
	public static final String ACTION_STATER = BASE + "@action";

	public static final String EXTRA_WIFI_STATE = BASE + "@extra_wifi_state";
	private static final int INVALID_WIFI_STATE = WifiManager.WIFI_STATE_UNKNOWN;

	public static final String EXTRA_WIFI_RSSI = BASE + "@extra_wifi_rssi";
	private static final int INVALID_WIFI_RSSI = Integer.MAX_VALUE;
	private static final int INVALID_SUPPLICANT_ERROR = Integer.MAX_VALUE;

	private Logger mLgr;

	public IntentFilter getFilter() {
		Logger lgr = getLogger();
		lgr.d("getFilter");
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);	// EXTRA_WIFI_STATE, EXTRA_PREVIOUS_WIFI_STATE
		filter.addAction(WifiManager.RSSI_CHANGED_ACTION);	// EXTRA_NEW_RSSI
		filter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);	// no extras ?
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);	// EXTRA_NETWORK_INFO / CONNECTEDなら？→ EXTRA_BSSID, EXTRA_WIFI_INFO
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);	// getScanResults()
		filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);	// EXTRA_SUPPLICANT_CONNECTED
		filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);	// EXTRA_NEW_STATE, EXTRA_SUPPLICANT_ERROR
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);	// ?
		return filter;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		mLgr = getLogger();
		try {
			onReceiveMain(context, intent);
		} catch (Exception e) {
			mLgr.e("onReceiveMain", e);
		}
		mLgr = null;
	}

	private void onReceiveMain(Context context, Intent intent) {
		Logger lgr = getLogger();
		StringBuffer sb = new StringBuffer();
		if (context == null) {
			sb.append("content = null");
			lgr.w(sb);
			return;
		}
		if (intent == null) {
			sb.append("intent = null");
			lgr.w(sb);
			return;
		}
		String act = intent.getAction();
		if (TextUtils.isEmpty(act)) {
			sb.append("act = empty");
			lgr.w(sb);
			return;
		}
		sb.append(act);

		if (act.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
			int previous_state = intent.getIntExtra(WifiManager.EXTRA_PREVIOUS_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
			sb.append(" state : ");
			sb.append(previous_state);
			sb.append(" -> ");
			sb.append(state);
			lgr.d(sb);
			Intent staterIntent = getStaterIntent(context);
			staterIntent.putExtra(EXTRA_WIFI_STATE, state);
			context.sendStickyBroadcast(staterIntent);
			return;
		}

		if (act.equals(WifiManager.RSSI_CHANGED_ACTION)) {
			int dBm = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, INVALID_WIFI_RSSI);
			int level = WifiManager.calculateSignalLevel(dBm, 5);
			sb.append(" dBm = ");
			sb.append(dBm);
			sb.append(" / level = ");
			sb.append(level);
			sb.append(" of 5");
			lgr.d(sb);
			Intent staterIntent = getStaterIntent(context);
			staterIntent.putExtra(EXTRA_WIFI_RSSI, dBm);
			context.sendStickyBroadcast(staterIntent);
			/*
			EXTRA_NEW_RSSIは下記と同値
			WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = manager.getConnectionInfo();	// 要android.permission.ACCESS_WIFI_STATE
			int rssi = info.getRssi();
			lgr.d("rssi = " + rssi);
			*/
			return;
		}

		if (act.equals(WifiManager.NETWORK_IDS_CHANGED_ACTION)) {
			lgr.d(sb);
			return;
		}

		if (act.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			lgr.d(sb);
			return;
		}

		if (act.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
			lgr.d(sb);
			return;
		}

		if (act.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
			if (intent.hasExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED)) {
				boolean connected = intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false);
				sb.append(" connection = ");
				sb.append(connected);
				lgr.d(sb);
			} else {
				sb.append(" connection unknown");
				lgr.w(sb);
			}
			return;
		}

		if (act.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
			SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
			if (state != null && state instanceof SupplicantState) {
				sb.append(" state = ");
				sb.append(state.name());
				lgr.d(sb);
			} else {
				int error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, INVALID_SUPPLICANT_ERROR);
				sb.append(" state error = ");
				sb.append(error);
				lgr.w(sb);
			}
			return;
		}

		if (act.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			Bundle extras = intent.getExtras();
			if (extras != null) {
				for (String key : extras.keySet()) {
					sb.append(" [");
					sb.append(key);
					sb.append("] = ");
					sb.append(extras.get(key));
				}
			}
			lgr.d(sb);
			return;
		}
	}

	private Intent getStaterIntent(Context context) {
		try {
			IntentFilter staterFilter = new IntentFilter(ACTION_STATER);
			Intent staterIntent = context.registerReceiver(null, staterFilter);
			if (staterIntent != null) {
				return staterIntent;
			}
		} catch (Exception e) {
			getLogger().e("getStaterIntent", e);
		}
		return  new Intent(ACTION_STATER);
	}

	private Logger getLogger() {
		Logger lgr = mLgr;
		if (lgr != null) {
			return lgr;
		}
		return new Logger(this);
	}

	public static void dumpStaterIntent(Context context) {
		Logger lgr = new Logger();
		lgr.d("dumpStaterIntent");
		try {
			Intent staterIntent = context.registerReceiver(null, new IntentFilter(ACTION_STATER));
			if (staterIntent == null) {
				return;
			}

			int wifi_state = staterIntent.getIntExtra(EXTRA_WIFI_STATE, INVALID_WIFI_STATE);
			lgr.d("wifi_state = " + wifi_state);
			int wifi_rssi = staterIntent.getIntExtra(EXTRA_WIFI_RSSI, INVALID_WIFI_RSSI);
			lgr.d("wifi_rssi = " + wifi_rssi);
		} catch (Exception e) {
			lgr.e("getStaterIntent", e);
		}
	}
}
