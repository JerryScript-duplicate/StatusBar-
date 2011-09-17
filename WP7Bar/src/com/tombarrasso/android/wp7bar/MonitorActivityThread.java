package com.tombarrasso.android.wp7bar;

/*
 * MonitorActivityThread.java
 *
 * Copyright (C) Andy Tsui
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Process;
import java.lang.Thread;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;

// Android Packages
import android.util.Log;

/**
 * A {@link Thread} used for monitoring the system log. It
 * listens for a particular pattern used for when an {@link Activity}
 * has started. When it has it notifies its {@link ActivityStartingListener}.
 *
 * @author		Andy Tsui (modified by Thomas James Barrasso)
 * @version		1.0
 * @since		09-16-2011
 * @category	{@link Thread}
 */

public final class MonitorActivityThread extends Thread
{
	public static final String TAG = MonitorActivityThread.class.getSimpleName(),
							   PACKAGE = MonitorActivityThread.class.getPackage().getName();

	private static final Pattern ActivityNamePattern = Pattern.compile( ".*Starting activity.*cmp=((\\w+(\\.\\w+)*\\.\\w+)/(\\.?\\w+(\\.\\w+)*))");
	private static final String LogCatCommand = "logcat ActivityManager:I *:S";
	private static final String ClearLogCatCommand = "logcat -c";

	/**
	 * Listener to be notified when an {@link Activity} is starting.
	 */
	public static interface ActivityStartingListener
	{
		public void onActivityStarting(String mPackageName, String mActivityName);
	}

	private final ActivityStartingListener mListener;

    public MonitorActivityThread(ActivityStartingListener mListener)
	{
            this.mListener = mListener;
    }
    
    private BufferedReader mReader;

    @Override
    public void run()
	{
        try
		{
			Process mProcess =
				Runtime.getRuntime().exec(ClearLogCatCommand);
			mProcess = Runtime.getRuntime().exec(LogCatCommand);

			if (mProcess == null) return;
			mReader = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
			if (mReader == null) return;

			String line;

			// Check if it matches the pattern for an Activity starting.
			while(((line = mReader.readLine()) != null) &&
				!this.isInterrupted())
			{     
                    final Matcher mMatcher =
						ActivityNamePattern.matcher(line);
                    if (!mMatcher.lookingAt() ||
						mMatcher.groupCount() < 5) continue;

                    if (mListener != null)
						mListener.onActivityStarting(mMatcher.group(2), mMatcher.group(4));
			}
		}
		catch (IOException e)
		{
			Log.e(TAG, "An error occured while determining if an Activity started.", e);
		}
	}
}
