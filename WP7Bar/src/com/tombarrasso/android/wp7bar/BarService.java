package com.tombarrasso.android.wp7bar;

/*
 * BarService.java
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
import android.content.Context;
import android.content.res.Resources;
import android.app.Activity;
import android.app.Service;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Binder;
import android.view.View;
import android.view.WindowManager;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.util.Log;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.content.ComponentName;
import android.view.View.OnLongClickListener;
import android.content.BroadcastReceiver;

// UI Packages
import com.tombarrasso.android.wp7ui.statusbar.*;
import com.tombarrasso.android.wp7ui.widget.WPDigitalClock;

// Java Packages
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This is a {@link Service} designed to open a {@link Window}
 * of type {@link TYPE_SYSTEM_ALERT}. Such a window with the
 * {@link FLAG_LAYOUT_IN_SCREEN} flag displays above all other
 * windows. This provides a window with which we can add views
 * to, ie. a {@link StatusBarView}. This service displays a
 * notification in default system status bar, and when expanded
 * you can click an ongoining {@link Notification} you are taken
 * to {@link HomeActivity} which allows you to stop the service,
 * remove the status bar, or edit settings. Includes built-in
 * support for screen on/ off and unlock intents to disallow
 * expansion in a lockscreen (this may vary based on the lockscreen
 * currently used).<br /><br />
 * <u>Change Log:</u>
 * <b>Version 1.01</b>
 * <ul>
 *	<li>Now using {@link setForeground}/ {@link startForeground} to ensure that the {@link Service} remains running even in low-memory conditions.</li>
 *	<li>System status bar height determination is now moved to {@link StatucBarView} and using a {@link Map} for the fallback for all pixel densities.</li>
 *	<li>{@link WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY} used when "click to drop" is disabled.</li>
 * </ul>
 *
 * @author		Thomas James Barrasso <contact @ tombarrasso.com>
 * @since		09-16-2011
 * @version		1.01
 * @category	{@link Service}
 */

public final class BarService extends Service
{
	public static final String TAG = BarService.class.getSimpleName(),
							   PACKAGE = BarService.class.getPackage().getName();

	// Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private static final int NOTIFICATION = R.string.service_started;
	public static final int FLAG_ALLOW_LOCK_WHILE_SCREEN_ON = 0x00000001;

	// We'll need these things later.
	private WindowManager mWM;
	private NotificationManager mNM;
	private LayoutInflater mLI;
	private StatusBarView mBarView;
	private Preferences mPrefs;

	// Initialize the intent filter statically.
	private static final IntentFilter mFilter =
		new IntentFilter(Intent.ACTION_SCREEN_ON);
	private static final IntentFilter mLockFilter =
		new IntentFilter(Intent.ACTION_USER_PRESENT);
	static {
        mFilter.addAction(Intent.ACTION_SCREEN_OFF);
	};

	private final ScreenReceiver mScreenReceiver = new ScreenReceiver();
	private final PresenceReceiver mPresenceReceiver = new PresenceReceiver();

	public static boolean wasScreenOn = true;

	/**
     * @see IStatusBarService
	 *
	 * Implementation of the public API for accessing and
	 * controlling the custom status bar. Bind to the remote
	 * service and attempt to call any of these methods.
     */
    public final IStatusBarService.Stub mBinder =
		new IStatusBarService.Stub()
	{
		/**
		 * Makes the status bar invisible.
	 	 */
		public void hide()
		{
			mBarView.setVisibility(View.GONE);
		}

		/**
		 * Makes the status bar visible.
		 */
		public void show()
		{
			mBarView.setVisibility(View.VISIBLE);
		}

		/**
		 * Toggles between {@link show} and {@link hide}.
		 */
		public void toggle()
		{
			if (mBarView.getVisibility() == View.VISIBLE)
				hide();
			else
				show();
		}

		/**
		 * Drops the status bar icons.
		 */
		public void drop()
		{
			mBarView.drop();
		}

		/**
		 * @return True if the icons are dropped.
		 */
		public boolean isDropped()
		{
			return mBarView.isDropped();
		}

		/**
		 * Completly removes the status bar from
		 * the SYSTEM ALERT WINDOW.
		 */
		public void destroy()
		{
			destroyStatusBar();
		}

		/**
		 * Creates a status bar and adds it to a
		 * SYSTEM ALERT WINDOW above the default
		 * status bar.
	 	 */
		public void create()
		{
			createStatusBar();
		}

		/**
		 * Disables the status bar's expansion.
		 */
		public void disableExpand()
		{
			mBarView.setExpand(false);
		}

		/**
		 * Enables the status bar's expansion.
		 */
		public void enableExpand()
		{
			mBarView.setExpand(true);
		}

		/**
		 * @return The color of the icons.
		 */
		public int getIconColor()
		{
			return mPrefs.getIconColor();
		}

		/**
		 * @return The background color of the {@limk StatusBarView}.
		 */
		public int getBackgroundColor()
		{
			return mBarView.getBackgroundColor();
		}
    };

	private static final Class[] mStartForegroundSignature = new Class[] {
        int.class, Notification.class};
    private static final Class[] mStopForegroundSignature = new Class[] {
        boolean.class};
	private static final Class mClass = Service.class;
    
    private static Method mStartForeground;
    private static Method mStopForeground;
	private static Method mSetForeground;

	// Obtain methods in a static context for effeciancy.
	static {
		try
		{
            mStartForeground = mClass.getMethod("startForeground",
                    mStartForegroundSignature);
        }
		catch (NoSuchMethodException e)
		{
            // Running on an older platform.
            mStartForeground = null;
        }

		try
		{
            mStopForeground = mClass.getMethod("stopForeground",
                    mStopForegroundSignature);
        }
		catch (NoSuchMethodException e)
		{
            // Running on an older platform.
            mStopForeground = null;
        }

		try
		{
            mSetForeground = mClass.getMethod("setForeground", mStopForegroundSignature);
        }
		catch (NoSuchMethodException e)
		{
            // Running on an older platform.
            mSetForeground = null;
        }
	};

    /**
     * This is a wrapper around the new startForeground method, using the older
     * APIs if it is not available.
     */
    private final void startForegroundCompat(
		int id, Notification notification)
	{
        // If we have the new startForeground API, then use it.
        if (mStartForeground != null)
		{
            try
			{
                mStartForeground.invoke(this, new Object[] { Integer.valueOf(id), notification });
            }
			catch (InvocationTargetException e)
			{
                // Should not happen.
                Log.w(TAG, "Unable to invoke startForeground", e);
            }
			catch (IllegalAccessException e)
			{
                // Should not happen.
                Log.w(TAG, "Unable to invoke startForeground", e);
            }
            return;
        }
        
        // Fall back on the old API.
        callSetForeground(true);
        mNM.notify(id, notification);
    }
    
    /**
     * This is a wrapper around the new stopForeground method, using the older
     * APIs if it is not available.
     */
    private final void stopForegroundCompat(int id)
	{
        // If we have the new stopForeground API, then use it.
        if (mStopForeground != null)
		{
            try
			{
                mStopForeground.invoke(this, new Object[] { Boolean.TRUE });
            }
			catch (InvocationTargetException e)
			{
                // Should not happen.
                Log.w(TAG, "Unable to invoke stopForeground", e);
            }
			catch (IllegalAccessException e)
			{
                // Should not happen.
                Log.w(TAG, "Unable to invoke stopForeground", e);
            }
            return;
        }
        
        // Fall back on the old API.  Note to cancel BEFORE changing the
        // foreground state, since we could be killed at that point.
        mNM.cancel(id);
        callSetForeground(false);
    }

   /**
	* Will call the "setForeground" method if available using reflection,
	* since the method has been removed completely in Android 3.0 and
	* above.
	* @param foreground should service run as a foreground service?
	*/
	public final void callSetForeground(boolean foreground)
	{
		try
		{
			mSetForeground.invoke(this, new Object[] { (Boolean) foreground });
		}
		catch (IllegalAccessException e)
		{
			// Should not happen.
			Log.w(TAG, "Unable to invoke setForeground", e);
		}
		catch (InvocationTargetException e)
		{
			// Should not happen.
			Log.w(TAG, "Unable to invoke setForeground", e);
		}
	}

	private void destroyStatusBar()
	{
		// Remove the view from the window.
		if(mBarView != null)
	    {
	        mWM.removeView(mBarView);
	        mBarView = null;
	    }

		removeListeners();
	}

	/**
	 * Removes all {@link BroadcastReceivers} and such
	 * to prevent them from being leaked.
	 */
	private void removeListeners()
	{
		// Make an array list with all listeners.
		final ArrayList<StateListener> mListeners =
			new ArrayList<StateListener>();

		final Context mContext = getApplicationContext();		

		// Only add listeners that have been used before.
		if (BatteryListener.hasInitialised())
			 mListeners.add((StateListener) BatteryListener.getInstance(mContext));
		if (BluetoothListener.hasInitialised())
			 mListeners.add((StateListener) BluetoothListener.getInstance(mContext));
		if (PhoneListener.hasInitialised())
			 mListeners.add((StateListener) PhoneListener.getInstance(mContext));
		if (RingerListener.hasInitialised())
			 mListeners.add((StateListener) RingerListener.getInstance(mContext));
		if (TimeListener.hasInitialised())
			 mListeners.add((StateListener) TimeListener.getInstance(mContext));
		if (WifiListener.hasInitialised())
			 mListeners.add((StateListener) WifiListener.getInstance(mContext));
		if (LanguageListener.hasInitialised())
			 mListeners.add((StateListener) LanguageListener.getInstance(mContext));
		
		// Close all listeners.
		// Check against null and use parameter-less
		// method to avoid unsafe/ unchecked warning.
		for (StateListener mListener : mListeners)
			if (mListener != null)
				mListener.close();
	}

	private void createStatusBar()
	{
		// Attach this View using WindowManager.
		if (mWM == null)
			mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
		if (mLI == null)
			mLI = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (mBarView == null)
		{
			// Use a TYPE_SYSTEM_OVERLAY when click to drop is
			// disabled. This allows it to hover above even the
			// system lockscreen, but it cannot consume touch events.
			final WindowManager.LayoutParams mParams =
				new WindowManager.LayoutParams(
					WindowManager.LayoutParams.FILL_PARENT,
					StatusBarView.getSystemStatusBarHeight(this),
					((mPrefs.isDropEnabled()) ? 
					WindowManager.LayoutParams.TYPE_SYSTEM_ALERT :
					WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY),
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
					WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
					WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
					WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING |
					WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
					PixelFormat.TRANSLUCENT);

			// Be sure that we are starting at (0, 0).
			mParams.gravity = Gravity.TOP | Gravity.FILL_HORIZONTAL;

			// Add the window title noticable in HierarchyViewer.
			mParams.setTitle(getString(R.string.window_title));
			mParams.packageName = PACKAGE;

			// Give the window an animation when opening.
			final int mAnimId = Resources.getSystem().getIdentifier("Animation_statusBar", "style", "android");
			if (mAnimId != 0)
				mParams.windowAnimations = mAnimId;

			// Inflate the status bar layout.
			mBarView = (StatusBarView) mLI.inflate(R.layout.statusbar, null);

			// Set the colors based on the user's preferences.
			// These NEED to happen before we add them to the
			// {@link Window}. If not we will get {@link RemoteException}
			// because once the views are added we cannot manipulate
			// them, like changing their color/ background color.
			mBarView.setBackgroundColor(mPrefs.getBackgroundColor());
			mBarView.setAllColors(mPrefs.getIconColor());

			// Set whether the icons should drop or not.
			if (!mPrefs.isDropEnabled())
				mBarView.setDropAllowed(false);

			final ArrayList<String> mIconKeys = Preferences.getIconKeys();
			for (int i = 0, e = mBarView.getChildCount(); i < e; ++i)
			{
				final View mChild = mBarView.getChildAt(i);

				for (String mKey : mIconKeys)
				{
					final boolean mKeyEnabled = mPrefs.getBoolean(mKey, true);
					if (!mKeyEnabled)
					{
						// Hide all icons that are set to do so.
						if ((Preferences.KEY_ICON_SIGNAL.equals(mKey) &&
							mChild instanceof SignalView))
							mChild.setVisibility(View.GONE);
						else if ((Preferences.KEY_ICON_DATA.equals(mKey) &&
							mChild instanceof DataView))
							mChild.setVisibility(View.GONE);
						else if ((Preferences.KEY_ICON_ROAMING.equals(mKey) &&
							mChild instanceof RoamingView))
							mChild.setVisibility(View.GONE);
						else if ((Preferences.KEY_ICON_WIFI.equals(mKey) &&
							mChild instanceof WifiView))
							mChild.setVisibility(View.GONE);
						else if ((Preferences.KEY_ICON_BLUETOOTH.equals(mKey) &&
							mChild instanceof BluetoothView))
							mChild.setVisibility(View.GONE);
						else if ((Preferences.KEY_ICON_LANGUAGE.equals(mKey) &&
							mChild instanceof LanguageView))
							mChild.setVisibility(View.GONE);
						else if ((Preferences.KEY_ICON_BATTERY.equals(mKey) &&
							mChild instanceof BatteryView))
							mChild.setVisibility(View.GONE);
						else if ((Preferences.KEY_ICON_TIME.equals(mKey) &&
							mChild instanceof WPDigitalClock))
							mChild.setVisibility(View.GONE);
					}
				}
			}

			mWM.addView(mBarView, mParams);
		}
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
	
		// Get an instance of the preferences.
		mPrefs = Preferences.getInstance(this);

		// Don't bother listening to screen/ unlock
		// events unless the setting is enabled.
		if (mPrefs.isExpandDisabled())
		{
			// Listen for screen on/ off.
			registerReceiver(mScreenReceiver, mFilter);

			// Listen for unlock.
			registerReceiver(mPresenceReceiver, mLockFilter);
		}

        // Display a notification about us starting.
		// We put an icon in the status bar.
        showNotification();
		createStatusBar();
	}
	
    @Override
    public void onDestroy()
	{
		destroyStatusBar();

		// Cancel the status bar notification.
		if (mNM != null)
			mNM.cancel(NOTIFICATION);

		super.onDestroy();
	}

	public class ScreenReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// Get be safe.
			if (intent == null) return;
			final String mAction = intent.getAction();
			if (mAction == null) return;

			// Disable status bar expansion when the screen is off.
		    if (mAction.equals(Intent.ACTION_SCREEN_OFF))
			{
				if (mPrefs.isExpandDisabled())
					mBarView.setExpand(false);
		        wasScreenOn = false;
		    }
			else if (mAction.equals(Intent.ACTION_SCREEN_ON))
			{
		        wasScreenOn = true;
		    }
		}
	}

	public class PresenceReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// Get be safe.
			if (intent == null) return;
			final String mAction = intent.getAction();
			if (mAction == null) return;

			// Make the status bar expandable when the device was unlocked.
			if (mAction.equals(Intent.ACTION_USER_PRESENT))
			{
				if (mPrefs.isExpandDisabled())
					mBarView.setExpand(true);
			}
		}
	}


	/**
	 * Bind to an instance of {@link IStatusBarService} remotely.
	 */
	@Override
    public IBinder onBind(Intent intent)
	{
        return mBinder;
    }

	private Thread mThread;

	/**
	 * Starts the {@link Thread} that monitors
	 * when an {@link Activity} is opened/ launched.
	 */
	private final void startMonitorThread()
	{
		// Only monitor {@link Activity}s if
		// the setting is enabled to do so.
		if (!mPrefs.isUsingBlacklist()) return;

		if (mThread != null)
                mThread.interrupt();
        
		mThread = new MonitorActivityThread(new MonitorActivityHandler(this));
		mThread.start();
    }


	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// Start monitoring when apps are opened.
		startMonitorThread();

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

	/**
     * Show a notification while this service is running.
     */
    private void showNotification()
	{
		// Get Notification and Activity Managers.
		if (mNM == null)
			mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        final CharSequence mTitle = getText(R.string.notification_marquee);

        // Set the icon, scrolling text and timestamp.
        final Notification mNotif = new Notification(
			R.drawable.notification, mTitle, System.currentTimeMillis());
		
		// It is an ongoing serviec.
		mNotif.flags = Notification.FLAG_ONGOING_EVENT;

        // The PendingIntent to launch our activity if
		// the user selects this notification.
        final PendingIntent mIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, HomeActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        mNotif.setLatestEventInfo(this,
			getText(R.string.bar_service), mTitle, mIntent);

		// Notify the user and enter foreground.
		startForegroundCompat(NOTIFICATION, mNotif);
    }
}
