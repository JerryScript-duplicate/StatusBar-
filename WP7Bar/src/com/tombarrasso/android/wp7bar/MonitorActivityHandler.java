package com.tombarrasso.android.wp7bar;

/*
 * MonitorActivityThread.java
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

// Java Packages
import java.util.List;
import java.util.ArrayList;

// Android Packages
import android.content.Context;
import android.content.res.Resources;
import android.app.Activity;
import android.app.Service;
import android.os.Handler;
import android.app.ActivityManager;
import android.util.Log;

// App Packages
import com.tombarrasso.android.wp7bar.MonitorActivityThread.ActivityStartingListener;

/**
 * 
 *
 * @author		Thomas James Barrasso <contact @ tombarrasso.com>
 * @version		1.0
 * @since		09-16-2011
 * @category	{@link Handler}
 */

public final class MonitorActivityHandler extends Handler
	implements ActivityStartingListener
{
	public static final String TAG = MonitorActivityHandler.class.getSimpleName(),
							   PACKAGE = MonitorActivityHandler.class.getPackage().getName();

	private final Context mContext;
	private final Preferences mPrefs;
	private final ActivityManager mAM;

	public MonitorActivityHandler(Context mContext)
	{
		this.mContext = mContext;

		// Get an instance of the preferences.
		mPrefs = Preferences.getInstance(mContext);

		mAM = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
	}

	@Override
	public void onActivityStarting(String mPackageName, String mActivityName)
	{
		
	}
}
