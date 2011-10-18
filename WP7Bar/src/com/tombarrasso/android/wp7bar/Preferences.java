package com.tombarrasso.android.wp7bar;

/*
 * Preferences.java
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

// Android Packages
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.graphics.Color;
import android.app.ActivityManager;
import android.os.Handler;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

// Java Packages
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

// UI Packages
import com.tombarrasso.android.wp7ui.statusbar.StatusBarView;

/**
 * Static, singleton utility for maintaining preferences. This is just
 * meant to keep things simple, clean, and in one location. Keep things
 * <abbr title="Don't Repeat Yourself">DRY</abbr> by doing so.<br /><br />
 * <u>Change Log:</u>
 * <b>Version 1.01</b>
 * <ul>
 * 	<li>Using {@link ActivityManager} to check if the {@link Service} is running instead of preferences.</li>
 * </ul>
 * <b>Version 1.02</b>
 * <ul>
 *	<li>Branching out to include {@link PackageManager} shortcuts to retrieve a list of applications, packages, etc.</li>
 * </ul>
 *
 * @author		Thomas James Barrasso <contact @ tombarrasso.com>
 * @since		10-13-2011
 * @version		1.02
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
								KEY_SWIPE = "service_swipe",
								KEY_ICON_SIGNAL = "icon_signal",
								KEY_ICON_DATA = "icon_data",
								KEY_ICON_ROAMING = "icon_roaming",
								KEY_ICON_WIFI = "icon_wifi",
								KEY_ICON_BLUETOOTH = "icon_bluetooth",
								KEY_ICON_LANGUAGE = "icon_language",
								KEY_ICON_BATTERY = "icon_battery",
								KEY_ICON_TIME = "icon_time",
								KEY_ICON_BATTERY_PERCENT = "icon_battery_percent",
								KEY_ICON_CARRIER = "icon_carrier",
								KEY_ICON_RINGER = "icon_ringer",
								KEY_DROP_DURATION = "service_drop_duration";

	// ArrayList containing the keys to all icons.
	private static final ArrayList<String> mIcons = new ArrayList<String>();
	static {
		mIcons.add(KEY_ICON_SIGNAL);
		mIcons.add(KEY_ICON_DATA);
		mIcons.add(KEY_ICON_CARRIER);
		mIcons.add(KEY_ICON_ROAMING);
		mIcons.add(KEY_ICON_WIFI);
		mIcons.add(KEY_ICON_BLUETOOTH);
		mIcons.add(KEY_ICON_RINGER);
		mIcons.add(KEY_ICON_LANGUAGE);
		mIcons.add(KEY_ICON_BATTERY_PERCENT);
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

	// Get all apps with a Launcher icon, or that are a Launcher.
	private static final Intent mAppIntent =
		new Intent(Intent.ACTION_MAIN, null);
	static {
		mAppIntent.addCategory(Intent.CATEGORY_LAUNCHER);
	};

	/**
	 * Information on a given application.
	 */
	public static final class AppInfo
	{
		private final String mName,
							 mPackageName,
							 mVersionName;

		private final int mVersionCode;
		private final Drawable mIcon;

		public AppInfo(String mName, String mPackageName, String mVersionName, Drawable mIcon, int mVersionCode)
		{
			this.mVersionName = mVersionName;
			this.mIcon = mIcon;
			this.mName = mName;
			this.mPackageName = mPackageName;
			this.mVersionCode = mVersionCode;
		}

		/**
		 * @return The icon of this application.
		 */
		public Drawable getIcon()
		{
			return mIcon;
		}

		/**
		 * @return The version code of this application.
		 */
		public int getVersionCode()
		{
			return mVersionCode;
		}

		/**
		 * @return The version name of this application.
		 */
		public String getVersionName()
		{
			return mVersionName;
		}

		/**
		 * @return The package name of this application.
		 */
		public String getPackageName()
		{
			return mPackageName;
		}

		/**
		 * @return The name of this application.
		 */
		public String getName()
		{
			return mName;
		}
	}

	/**
	 * @return An {@link ArrayList} containing the name of every
	 * application installed on the user's device. This only includes
	 * those with a category to display in a launcher, or that are a
	 * launcher themselves.
	 */
	public final ArrayList<String> getAppNames()
	{
		final List<ResolveInfo> mApps = mPackageManager.queryIntentActivities(mAppIntent, 0);

		// Order alphabetically from A - Z.
		Collections.sort(mApps, new ResolveInfo.DisplayNameComparator(mPackageManager));

		// Get the names of applications.
		final ArrayList<String> mNames = new ArrayList<String>();
		for (ResolveInfo mApp : mApps)
			mNames.add(mApp.activityInfo.applicationInfo.loadLabel(mPackageManager).toString());

		return mNames;
	}
	
	/**
	 * @return An {@link ArrayList} containing the package
	 * name of every application installed.
	 */
	public final ArrayList<String> getAppPackages()
	{
		final List<PackageInfo> mPackages =
			mPackageManager.getInstalledPackages(0);
		final ArrayList<String> mPackageNames = new ArrayList<String>();
		
		for (PackageInfo mPackage : mPackages)
			mPackageNames.add(mPackage.packageName);
		
		return mPackageNames;
	}

	/**
	 * {@link Comparator} for {@link AppInfo} objects.
	 */
	public static final class AppComparator implements java.util.Comparator<AppInfo>
	{
		public int compare(AppInfo app1, AppInfo app2)
		{
			return (int) Math.signum(
				(float) app1.getName()
				.compareToIgnoreCase(app2.getName()));
		}
	}

	/**
	 * @param alphabetical True if you want the list of
	 * applications sorted alphabetically.
	 * 
	 * @return An {@link ArrayList} of installed applications
	 * containing all {@link AppInfo}.
	 */
	public final ArrayList<AppInfo> getApps(boolean alphabetical)
	{
		final List<PackageInfo> mPackages =
			mPackageManager.getInstalledPackages(0);

		final ArrayList<AppInfo> mApps = new ArrayList<AppInfo>();
		
		// Get information on all applications.
		for (PackageInfo mPackage : mPackages)
		{
			final ArrayList<ResolveInfo> mActivities = findActivitiesForPackage(mPackage.packageName);

			// Get all Activities for a launcher.
			for (ResolveInfo mActivity : mActivities)
			{
				// Determine the application's icon,
				// or use a default if none exists.
				Drawable mIcon = mActivity.loadIcon(mPackageManager);
				if (mIcon == null) mIcon =
					mPackageManager.getDefaultActivityIcon();

				// Add an item to the list.
				mApps.add(new AppInfo(mActivity.loadLabel(mPackageManager).toString(), mPackage.packageName, mPackage.versionName,  mIcon, mPackage.versionCode));
			}
		}

		if (alphabetical)
			// Order alphabetically from A - Z.
			Collections.sort(mApps, new AppComparator());
		
		return mApps;
	}

	/**
	 * @return An {@link ArrayList} containing all {@link Activity}s that
	 * have an intent to be launched via the launcher.
	 */
	private final ArrayList<ResolveInfo> findActivitiesForPackage(String packageName)
	{
        final Intent mIntent = new Intent(mAppIntent);
        mIntent.setPackage(packageName);

        final ArrayList<ResolveInfo> mApps = new ArrayList<ResolveInfo>(mPackageManager.queryIntentActivities(mIntent, 0));
        return (mApps != null) ? mApps : new ArrayList<ResolveInfo>();
    }

	/**
	 * @return An instance to {@link ActivityManager}.
	 */
	public final ActivityManager getActivityManager()
	{
		return mActivityManager;
	}

	private static Preferences mInstance;
	private final Context mContext;
	private final PackageManager mPackageManager;
	private final ActivityManager mActivityManager;

	public Preferences(Context mContext)
	{
		this.mContext = mContext;
		mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		mPackageManager = mContext.getPackageManager();
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
	private final SharedPreferences getPrefs()
	{
		return PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	/**
	 * Clears ALL preferences.
	 */
	public final void clear()
	{
		final Editor mEditor = getPrefs().edit();
		mEditor.clear();
		mEditor.commit();
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
	 * @return The duration, in seconds, for how long the
	 * icons should drop if they are set to do so.
	 */
	public final int getDropDuration()
	{
		return getPrefs().getInt(KEY_DROP_DURATION, StatusBarView.DEFAULT_DROP_DURATION);
	}

	/**
	 * Sets how long the icons should drop for, assuming
	 * that isDropEnabled is set on. Units are seconds.
	 */
	public final void setDropDuration(int duration)
	{
		final Editor mEditor = getPrefs().edit();
		mEditor.putInt(KEY_DROP_DURATION, duration);
		mEditor.commit();
	}

	/**
	 * @return True if the status bar is allowed to swipe
	 * down to display the system status bar, false otherwise.
	 * The default value is true.
	 */
    public final boolean isSwipeEnabled()
	{
		return getPrefs().getBoolean(KEY_SWIPE, true);
    }

	/**
	 * Set whether or not the status bar should
	 * be allowed to swipe up/ down.
	 */
	public final void setSwipe(boolean swipe)
	{
		final Editor mEditor = getPrefs().edit();
		mEditor.putBoolean(KEY_SWIPE, swipe);
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
