package com.tombarrasso.android.wp7bar;

/*
 * Preferences.java
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
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.graphics.Color;
import android.app.ActivityManager;

// Java Packages
import java.util.List;
import java.util.ArrayList;

/**
 * Static, singleton utility for maintaining preferences. This is just
 * meant to keep things simple, clean, and in one location. Keep things
 * <abbr title="Don't Repeat Yourself">DRY</abbr> by doing so.<br /><br />
 * <u>Change Log:</u>
 * <ul>
 * 	<li>Using {@link ActivityManager} to check if the {@link Service} is running instead of preferences.</li>
 * </ul>
 *
 * @author		Thomas James Barrasso <contact @ tombarrasso.com>
 * @since		09-11-2011
 * @version		1.01
 * @category	Static Utility
 */

public final class Preferences
{
	public static final String TAG = Preferences.class.getSimpleName(),
							   PACKAGE = Preferences.class.getPackage().getName();

	// Keys used to store values.
	public static final String  KEY_BOOT = "service_onboot",
								KEY_ICON = "color_icons",
								KEY_BACKGROUND = "color_background",
								KEY_EXPAND = "service_expand",
								KEY_DROP = "service_drop",
								KEY_BLACKLIST = "service_use_blacklist",
								KEY_ICON_SIGNAL = "icon_signal",
								KEY_ICON_DATA = "icon_signal",
								KEY_ICON_ROAMING = "icon_roaming",
								KEY_ICON_WIFI = "icon_wifi",
								KEY_ICON_BLUETOOTH = "icon_bluetooth",
								KEY_ICON_LANGUAGE = "icon_language",
								KEY_ICON_BATTERY = "icon_battery",
								KEY_ICON_TIME = "icon_time";

	// ArrayList containing the keys to all icons.
	private static final ArrayList<String> mIcons = new ArrayList<String>();
	static {
		mIcons.add(KEY_ICON_SIGNAL);
		mIcons.add(KEY_ICON_DATA);
		mIcons.add(KEY_ICON_ROAMING);
		mIcons.add(KEY_ICON_WIFI);
		mIcons.add(KEY_ICON_BLUETOOTH);
		mIcons.add(KEY_ICON_LANGUAGE);
		mIcons.add(KEY_ICON_BATTERY);
		mIcons.add(KEY_ICON_TIME);
	};

	/**
	 * @return An {@link ArrayList} containing the String
	 * keys for the settings of whether or not each icon
	 * should be displayed or hidden in the status bar.
	 */
	public static final ArrayList<String> getIconKeys()
	{
		return mIcons;
	}

	private static Preferences mInstance;
	private final Context mContext;
	private final ActivityManager mActivityManager;

	public Preferences(Context mContext)
	{
		this.mContext = mContext;
		mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
	}

	/**
	 * Lazily-load an instance of {@link Preferences} statically
	 * in a Singleton fashion.
	 */
	public static final Preferences getInstance(Context mContext)
	{
		if (mInstance == null)
			mInstance = new Preferences(mContext);

		return mInstance;
	}

	/**
	 * @return An {@link ObscuredSharedPreferences} for this
	 * application to store preferences/ settings.
	 */
	private final ObscuredSharedPreferences getPrefs()
	{
		return new ObscuredSharedPreferences(mContext, PreferenceManager.getDefaultSharedPreferences(mContext));
	}

	/**
	 * @return True if the background service is running.
	 * The default is false.
	 */
    public final boolean isServiceRunning()
	{
		final List<ActivityManager.RunningServiceInfo> mServices =
			mActivityManager.getRunningServices(Integer.MAX_VALUE);
		final String mBarService = BarService.PACKAGE + "." + BarService.TAG;

		// Check all services to see if ours is running.
		for (ActivityManager.RunningServiceInfo mService : mServices)
        	if (mBarService.equals(mService.service.getClassName()))
            	return true;
		return false;
	}

	/**
	 * @return Get a boolean value with a default.
	 */
    public final boolean getBoolean(String mKey, boolean mDefault)
	{
		return getPrefs().getBoolean(mKey, mDefault);
    }

	/**
	 * Set the boolean value for a given key.
	 */
    public final void setBoolean(String mKey, boolean mValue)
	{
		final Editor mEditor = getPrefs().edit();
		mEditor.putBoolean(mKey, mValue);
		mEditor.commit();
    }

	/**
	 * @return True if the background service is
	 * turned on after a boot completion. The
	 * default is false.
	 */
    public final boolean isSetOnBoot()
	{
		return getPrefs().getBoolean(KEY_BOOT, false);
    }

	/**
	 * Set whether or not the service is should
	 * be automatically enabled after a boot completion.
	 */
	public final void setOnBoot(boolean boot)
	{
		final Editor mEditor = getPrefs().edit();
		mEditor.putBoolean(KEY_BOOT, boot);
		mEditor.commit();
	}

	/**
	 * @return True if the background service will
	 * monitor when {@link Activity}s are opened and
	 * check against a blacklist for auto-hiding.
	 * Default is false.
	 */
    public final boolean isUsingBlacklist()
	{
		return getPrefs().getBoolean(KEY_BLACKLIST, false);
    }

	/**
	 * Set whether or not the background service should
	 * monitor when {@link Activity}s are opened and
	 * check against a blacklist for auto-hiding.
	 */
	public final void setUsingBlacklist(boolean use)
	{
		final Editor mEditor = getPrefs().edit();
		mEditor.putBoolean(KEY_BLACKLIST, use);
		mEditor.commit();
	}

	/**
	 * Set whether or not the background service will
	 * disable expansion when the screen turns off.
	 */
	public final void setExpandDisabled(boolean expand)
	{
		final Editor mEditor = getPrefs().edit();
		mEditor.putBoolean(KEY_EXPAND, expand);
		mEditor.commit();
	}

	/**
	 * @return True if the background service will
	 * disable expansion when the screen turns off.
	 * The default is false.
	 */
    public final boolean isExpandDisabled()
	{
		return getPrefs().getBoolean(KEY_EXPAND, false);
    }

	/**
	 * Set whether or not the status
	 * bar will drop when clicked.
	 */
	public final void setDrop(boolean drop)
	{
		final Editor mEditor = getPrefs().edit();
		mEditor.putBoolean(KEY_DROP, drop);
		mEditor.commit();
	}

	/**
	 * @return True if the status
	 * bar will drop when clicked.
	 * The default is true.
	 */
    public final boolean isDropEnabled()
	{
		return getPrefs().getBoolean(KEY_DROP, true);
    }

	/**
	 * @return The background color of the {@link StatusBarView}.
	 * The default value is {@link Color.BLACK}.
	 */
	public final int getBackgroundColor()
	{
		return getPrefs().getInt(KEY_BACKGROUND, Color.BLACK);
	}

	/**
	 * Set the background color of the {@link StatusBarView}.
	 */
	public final void setBackgroundColor(int color)
	{
		final Editor mEditor = getPrefs().edit();
		mEditor.putInt(KEY_BACKGROUND, color);
		mEditor.commit();
	}

	/**
	 * @return The color of the {@link StatusBarView} icons.
	 * The default value is {@link Color.WHITE}.
	 */
	public final int getIconColor()
	{
		return getPrefs().getInt(KEY_ICON, Color.WHITE);
	}

	/**
	 * Set the color of the {@link StatusBarView} icons.
	 */
	public final void setIconColor(int color)
	{
		final Editor mEditor = getPrefs().edit();
		mEditor.putInt(KEY_ICON, color);
		mEditor.commit();
	}
}
