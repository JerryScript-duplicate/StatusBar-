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

/**
 * Static, singleton utility for maintaining preferences. This is just
 * meant to keep things simple, clean, and in one location. Keep things
 * <abbr title="Don't Repeat Yourself">DRY</abbr> by doing so. 
 *
 * @author		Thomas James Barrasso <contact @ tombarrasso.com>
 * @since		09-08-2011
 * @version		1.0
 * @category	Static Utility
 */

public final class Preferences
{
	public static final String TAG = Preferences.class.getSimpleName(),
							   PACKAGE = Preferences.class.getPackage().getName();

	// Keys used to store values.
	private static final String KEY_RUNNING = "service_running",
								KEY_BOOT = "service_onboot",
								KEY_ICON = "color_icons",
								KEY_BACKGROUND = "color_background",
								KEY_EXPAND = "service_expand",
								KEY_DROP = "service_drop";

	private static Preferences mInstance;
	private final Context mContext;

	public Preferences(Context mContext)
	{
		this.mContext = mContext;
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
		return getPrefs().getBoolean(KEY_RUNNING, false);
    }

	/**
	 * Set whether or not the service is running.
	 */
	public final void setServiceRunning(boolean running)
	{
		final Editor mEditor = getPrefs().edit();
		mEditor.putBoolean(KEY_RUNNING, running);
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
