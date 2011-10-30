package com.tombarrasso.android.wp7bar;

/*
 * TypeClearingPreferences.java
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
import java.util.Map;

// Android Packages
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * Simple wrapper for {@link SharedPreferences} that, upon receiving
 * a {@link ClassCastException} clears the preferences to avoid a
 * force close of the application. This occurs both for getting and
 * setting preferences. This is especially useful when upgrading
 * settings from a previous model and such an exception is thrown
 * often.
 *
 * @author		Thomas James Barrasso <contact @ tombarrasso.com>
 * @since		10-26-2011
 * @version		1.00
 * @category	Wrapper
 */

public class TypeClearingPreferences implements SharedPreferences
{
    public static final String TAG = TypeClearingPreferences.class.getSimpleName();

    protected final SharedPreferences delegate;
    protected final Context context;

    public TypeClearingPreferences(Context context, SharedPreferences delegate)
	{
        this.delegate = delegate;
        this.context = context;
    }

	/**
	 * If {@link restart} is true then the current
	 * application will be restarted.
	 */
	public void clear(boolean restart)
	{
		final Editor mEditor = edit();
		mEditor.clear();
		mEditor.commit();

		if (!restart) return;

		// Clear the current Activity stack and restart
		// this application by opening the main Activity.
		try
		{
			final Intent mIntent = context.getPackageManager()
				.getLaunchIntentForPackage( context.getPackageName() );
			mIntent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
			context.startActivity( mIntent );
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

    public class Editor implements SharedPreferences.Editor
	{
        protected SharedPreferences.Editor delegate;

        public Editor()
		{
            this.delegate = TypeClearingPreferences.this.delegate.edit();                    
        }

		public Context getContext()
		{
			return TypeClearingPreferences.this.context;
		}

        @Override
        public Editor putBoolean(String key, boolean value)
		{
			try
			{
	            delegate.putBoolean(key, value);
			}
			catch (ClassCastException e)
			{
				TypeClearingPreferences.this.clear(true);
			}
            return this;
        }

        @Override
        public Editor putFloat(String key, float value)
		{
            try
			{
	            delegate.putFloat(key, value);
			}
			catch (ClassCastException e)
			{
				TypeClearingPreferences.this.clear(true);
			}
            return this;
        }

        @Override
        public Editor putInt(String key, int value)
		{
            try
			{
	            delegate.putInt(key, value);
			}
			catch (ClassCastException e)
			{
				TypeClearingPreferences.this.clear(true);
			}
            return this;
        }

        @Override
        public Editor putLong(String key, long value)
		{
            try
			{
	            delegate.putLong(key, value);
			}
			catch (ClassCastException e)
			{
				TypeClearingPreferences.this.clear(true);
			}
            return this;
        }

        @Override
        public Editor putString(String key, String value)
		{
            try
			{
	            delegate.putString(key, value);
			}
			catch (ClassCastException e)
			{
				TypeClearingPreferences.this.clear(true);
			}
            return this;
        }

        @Override
        public Editor clear()
		{
            delegate.clear();
            return this;
        }

        @Override
        public boolean commit()
		{
            return delegate.commit();
        }

        @Override
        public Editor remove(String s)
		{
            delegate.remove(s);
            return this;
        }
    }

    public Editor edit()
	{
        return new Editor();
    }

    @Override
    public boolean getBoolean(String key, boolean defValue)
	{
        boolean mBool = false;
		try
		{
	        mBool = delegate.getBoolean(key, defValue);
		}
		catch (ClassCastException e)
		{
			clear(true);
		}
        return mBool;
    }

    @Override
    public float getFloat(String key, float defValue)
	{
        float mFloat = 0l;
		try
		{
	        mFloat = delegate.getFloat(key, defValue);
		}
		catch (ClassCastException e)
		{
			clear(true);
		}
        return mFloat;
    }

    @Override
    public int getInt(String key, int defValue)
	{
        int mInt = 0;
		try
		{
	        mInt = delegate.getInt(key, defValue);
		}
		catch (ClassCastException e)
		{
			clear(true);
		}
        return mInt;
    }

    @Override
    public long getLong(String key, long defValue)
	{
        long mLong = 0l;
		try
		{
	        mLong = delegate.getLong(key, defValue);
		}
		catch (ClassCastException e)
		{
			clear(true);
		}
        return mLong;
    }

    @Override
    public String getString(String key, String defValue)
	{
		String mStr = null;
		try
		{
	        mStr = delegate.getString(key, defValue);
		}
		catch (ClassCastException e)
		{
			clear(true);
		}
        return mStr;
    }

	@Override
    public Map<String, ?> getAll()
	{
        return delegate.getAll();
    }

    @Override
    public boolean contains(String s)
	{
        return delegate.contains(s);
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(
		OnSharedPreferenceChangeListener onSharedPreferenceChangeListener)
	{
        delegate.registerOnSharedPreferenceChangeListener(
			onSharedPreferenceChangeListener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(
		OnSharedPreferenceChangeListener onSharedPreferenceChangeListener)
	{
        delegate.unregisterOnSharedPreferenceChangeListener(
			onSharedPreferenceChangeListener);
    }
}
