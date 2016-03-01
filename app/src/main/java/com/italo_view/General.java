package com.italo_view;

import java.lang.reflect.Method;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

public class General {

	public static String getDeviceID(Context pContext) {
		TelephonyManager manager = (TelephonyManager) pContext.getSystemService(Context.TELEPHONY_SERVICE);
		String serial = manager.getDeviceId();

		Log.i("info", "serial1." + serial);
		if (serial == null || serial.equals("") || serial.equals("000000000000000")) {
			serial = null;
			try {
				Class<?> c = Class.forName("android.os.SystemProperties");
				Method get = c.getMethod("get", String.class, String.class);
				serial = (String) (get.invoke(c, "ro.serialno", "unknown"));
				Log.i("info", "serial2." + serial);

			} catch (Exception ignored) {
			}

			if (serial.equals("") || serial.equals("unknown")) {
				serial = Settings.Secure.getString(	pContext.getContentResolver(),	Settings.Secure.ANDROID_ID);
				Log.i("info", "serial3." + serial + "." + serial.length());
			}

		}

		serial = serial.toUpperCase();

		Log.i("info", "serial4." + serial + "." + serial.length());
		return serial;
	}

}
