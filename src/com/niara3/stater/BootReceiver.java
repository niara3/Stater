package com.niara3.stater;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (context == null || intent == null) {
			return;
		}
		if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			return;
		}
		context.startService(new Intent(context, StaterService.class));
	}
}
