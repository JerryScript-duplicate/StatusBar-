package com.tombarrasso.android.wp7bar;

/*
 * BootReceiver.java
 *
 * Copyright (C) Thomas James Barrasso
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Android Packages
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This receiver is notified when the user's device has finished
 * booting. It is used to restart the status bar based on the
 * user's preference.
 * 
 * @author		Thomas James Barrasso <contact @ tombarrasso.com>
 * @since		09-09-2011
 * @version		1.0
 * @category	{@link BroadcastReceiver}
 */

public class BootReceiver extends BroadcastReceiver
{
	public static final String TAG = BootReceiver.class.getSimpleName(),
							   PACKAGE = BootReceiver.class.getPackage().getName();

	@Override
	public void onReceive(Context context, Intent intent)
	{
		// Check preferences to see if we should start on boot.
		final Preferences mPrefs = Preferences.getInstance(context);

		// Get be safe.
		if (intent == null) return;
		final String mAction = intent.getAction();
		if (mAction == null) return;

		// Get the intent to start the service.
		final Intent mServiceIntent = new Intent();
		mServiceIntent.setClassName(BarService.PACKAGE, BarService.PACKAGE + "." + BarService.TAG);

		// Start on boot if set to do so.
		if (mPrefs.isSetOnBoot() &&
			mAction.equals(Intent.ACTION_BOOT_COMPLETED))
			context.startService(mServiceIntent);
	}
}
