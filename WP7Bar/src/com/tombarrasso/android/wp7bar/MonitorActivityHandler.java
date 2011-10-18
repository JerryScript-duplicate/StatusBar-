package com.tombarrasso.android.wp7bar;

/*
 * MonitorActivityThread.java
 *
 * Copyright (C) 2011 Thomas James Barrasso
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

// Java Packages
import java.util.List;
import java.util.ArrayList;

// Android Packages
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.app.Activity;
import android.app.Service;
import android.os.Handler;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.util.Log;
import android.os.RemoteException;

// App Packages
import com.tombarrasso.android.wp7bar.HomeActivity.BarServiceConnection;

// UI Packages
import com.tombarrasso.android.wp7ui.extras.MonitorActivityThread.ActivityStartingListener;

/**
 * {@link ActivityStartingListener} that is notified when a new {@link Activity}
 * is opened by {@link MonitorActivityThread}. If one is found the status bar is
 * hidden or shown based on the current black list used.
 *
 * @author		Thomas James Barrasso <contact @ tombarrasso.com>
 * @version		1.0
 * @since		09-25-2011
 * @category	{@link Handler}
 */

public final class MonitorActivityHandler
	implements ActivityStartingListener, Runnable
{
	public static final String TAG = MonitorActivityHandler.class.getSimpleName(),
							   PACKAGE = MonitorActivityHandler.class.getPackage().getName();

	private static final BarServiceConnection mConnection =
		new BarServiceConnection();

	private static final Intent mServiceIntent = new Intent();
	static {
		mServiceIntent.setClassName(BarService.PACKAGE, BarService.PACKAGE + "." + BarService.TAG);
	};

	private final Context mContext;
	private final Preferences mPrefs;
	private String mPackageName, mActivityName;
	private final Handler mHandler = new Handler();

	public MonitorActivityHandler(Context mContext)
	{
		this.mContext = mContext;

		// Get an instance of the preferences.
		mPrefs = Preferences.getInstance(mContext);
	}

	@Override
	public void run()
	{
		// Don't hide for ourself.
		if (mContext.getPackageName().equals(mPackageName)) return;

		// Don't bother if we are not using the blacklist.
		if (!mPrefs.isUsingBlacklist()) return;

		// If the application is set to be automatically hidden.
		final boolean mShouldHide = mPrefs.getBoolean(mPackageName, false);
		if (!mContext.bindService(mServiceIntent, mConnection, 0)) return;
	
		final IStatusBarService mService = mConnection.getService();
		if (mService == null) return;
	
		try
		{
			// Show/ hide the status bar based on the app.
			if (mShouldHide) mService.hide();
			else			 mService.show();
		}
		catch (RemoteException e)
		{
			Log.w(TAG, "StatusBar+ could not be hidden.");
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityStarting(String mPackageName, String mActivityName)
	{
		this.mPackageName = mPackageName;
		this.mActivityName = mActivityName;
		mHandler.post(this);
	}
}
