package com.tombarrasso.android.wp7bar;

/*
 * BlacklistActivity.java
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
import android.app.ListActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.content.Intent;
import android.view.View;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Checkable;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

// Java Packages
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

// UI Packages
import com.tombarrasso.android.wp7ui.WPTheme;
import com.tombarrasso.android.wp7ui.widget.WPThemeView;
import com.tombarrasso.android.wp7ui.widget.WPJumpView;
import com.tombarrasso.android.wp7ui.widget.WPTextView;

// App Packages
import com.tombarrasso.android.wp7bar.Preferences.AppInfo;

/**
 * This {@link Activity} displays a list of all the applications
 * currently installed on the user's device. This consists of an
 * icon, name, and checkbox. When clicked the checkbox toggles
 * and its value is saved in {@link Preferences}. It will be read
 * later to determine which apps to auto-hide.<br /><br />
 * <ul>
 *	<li>Using an {@link AsyncTask} to retrieve the list of applications; prevents an <abbr title="Android Not Respond">ANR</abbr>.</li>
 *	<li>{@link AppInfo} in a static context to load applications once.</li>
 * </ul>
 *
 * @author		Thomas James Barrasso <contact @ tombarrasso.com>
 * @since		09-23-2011
 * @version		1.01
 * @category	{@link Activity}
 */

public class BlacklistActivity extends ListActivity
{
	public static final String TAG = BlacklistActivity.class.getSimpleName(),
							   PACKAGE = BlacklistActivity.class.getPackage().getName();

	private Preferences mPrefs;
	private static ArrayList<AppInfo> mApps;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		mPrefs = Preferences.getInstance(this);

		super.onCreate(savedInstanceState);
        setContentView(R.layout.icons);

		// Get the list of applications.
		(new GetAppsTask()).execute();
	}

	/**
	 * {@link AsyncTask} used for retreiving the list of applications
	 * and setting the {@link Adapter} for this {@link ListActivity}.
	 */
	private final class GetAppsTask
		extends AsyncTask<Void, Void, Void>
	{
		protected Void doInBackground(Void... nothing)
		{
			if (mApps == null) mApps = mPrefs.getApps(true);
			return null;
		}

		protected void onProgressUpdate(Void... progress) {}

		protected void onPostExecute(Void result)
		{
			final AppAdapter mAdapter =
				new AppAdapter(getBaseContext(), R.id.item_text, mApps);

			// Bind to our new adapter.
		    setListAdapter(mAdapter);
	
			// Hide the divider.
			getListView().setDivider(null); 
			getListView().setDividerHeight(0); 

			// Get rid of the overscroll glow.
			WPThemeView.setOverScrollMode(getListView(), WPThemeView.OVER_SCROLL_NEVER);
		}
	}


	/**
	 * @return A TextView styled to look
	 */
	public final WPTextView getLetterTextView(String letter)
	{
		// Create the click listener if not done already.
		if (mLetterClickListener == null)
			mLetterClickListener = new LetterClickListener(this);

		final WPTextView mTV = new WPTextView(this);
		mTV.setBackgroundColor(WPTheme.getAccentColor());
		mTV.setText(letter);
		mTV.setTag((Character) letter.charAt(0));
		mTV.setOnClickListener(mLetterClickListener);
		mTV.setTextColor(Color.WHITE);
		mTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 28);
		mTV.setGravity(Gravity.BOTTOM | Gravity.LEFT);
		mTV.setSingleLine();
		mTV.setPadding(4, 4, 4, 4);
		return mTV;
	}

	private LetterClickListener mLetterClickListener;

	/**
	 * Listener for when a letter is clicked.
	 */
	public static final class LetterClickListener
		implements View.OnClickListener
	{
		private final Context mContext;

		public LetterClickListener(Context mContext)
		{
			this.mContext = mContext;
		}

		@Override
		public void onClick(View mView)
		{
			final WPJumpView mJump = new WPJumpView(mContext);
			mJump.show();
		}
	}

	// Handle click events here.
	@Override
	public void onListItemClick(ListView parent, View view,
		int position, long id)
	{
		// Get the tag of the item.
		final TextView mTV = (TextView)
			view.findViewById(R.id.item_text);
		final String mPackage = mApps.get(position).getPackageName();
		final Checkable mCheck = (Checkable)
			view.findViewById(R.id.item_check);
		
		// Toggle the check box and update the settings.
		mCheck.toggle();
		mPrefs.setBoolean(mPackage, mCheck.isChecked());
	}

	/**
	 * {@link Adapter} that sets the text and the tag of the view based
	 * on the application's preferences. Includes a check box for each
	 * item that can be toggled by clicking the entire item.
	 */
	private final class AppAdapter extends ArrayAdapter<AppInfo>
	{
        private ArrayList<AppInfo> mItems;
		private final LayoutInflater mLI;

        public AppAdapter(Context context, int textViewResourceId, ArrayList<AppInfo> mItems)
		{
                super(context, textViewResourceId, mItems);
                this.mItems = mItems;
				mLI = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
		{
			// Get the layout.
            RelativeLayout mLayout = null;
            if (convertView == null)
                mLayout = (RelativeLayout) mLI.inflate(R.layout.app_item, null);
            else
				mLayout = (RelativeLayout) convertView;

			// Set the text and tag of the item.
			final String mName = mItems.get(position).getName();
			final String mPackage = mItems.get(position).getPackageName();
			final Drawable mIcon = mItems.get(position).getIcon();
			final TextView mTV = (TextView)
				mLayout.findViewById(R.id.item_text);
			final ImageView mIV = (ImageView)
				mLayout.findViewById(R.id.item_icon);
			mTV.setText(mName);
			mIV.setImageDrawable(mIcon);
			final Checkable mCheck = (Checkable)
				mLayout.findViewById(R.id.item_check);
			mCheck.setChecked(mPrefs.getBoolean(mPackage, false));

            return mLayout;
        }
	}
}
