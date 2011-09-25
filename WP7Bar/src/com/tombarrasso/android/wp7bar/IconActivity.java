package com.tombarrasso.android.wp7bar;

/*
 * IconActivity.java
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
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.util.Log;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ListView;
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

// UI Packages
import com.tombarrasso.android.wp7ui.widget.WPThemeView;

/**
 * This {@link Activity} manages which icons are displayed
 * and which are not by filling a {@link ListView} with every
 * given icon name with a checkbox that allows it to be toggled
 * on and off.
 *
 * @author		Thomas James Barrasso <contact @ tombarrasso.com>
 * @since		09-17-2011
 * @version		1.0
 * @category	{@link Activity}
 */

public class IconActivity extends ListActivity
{
	public static final String TAG = IconActivity.class.getSimpleName(),
							   PACKAGE = IconActivity.class.getPackage().getName();

	private Preferences mPrefs;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		mPrefs = Preferences.getInstance(this);

		super.onCreate(savedInstanceState);
        setContentView(R.layout.icons);

		final ArrayList<String> mList = new ArrayList<String>(
			Arrays.asList(getResources().getStringArray(R.array.icons)));
		final IconAdapter mAdapter = new IconAdapter(getBaseContext(), R.id.item_text,  mList);

		// Bind to our new adapter.
        setListAdapter(mAdapter);

		// Hide the divider.
		getListView().setDivider(null); 
		getListView().setDividerHeight(0); 

		// Get rid of the overscroll glow.
		WPThemeView.setOverScrollMode(getListView(), WPThemeView.OVER_SCROLL_NEVER);
	}

	// Handle click events here.
	@Override
	public void onListItemClick(ListView parent, View view,
		int position, long id)
	{
		// Get the tag of the item.
		final TextView mTV = (TextView)
			view.findViewById(R.id.item_text);
		final String mKey = mTextTag.get(position);
		final Checkable mCheck = (Checkable)
			view.findViewById(R.id.item_check);
		
		// Toggle the check box and update the settings.
		mCheck.toggle();
		mPrefs.setBoolean(mKey, mCheck.isChecked());
	}

	private static final ArrayList<String> mTextTag =
			Preferences.getIconKeys();

	/**
	 * {@link Adapter} that sets the text and the tag of the view based
	 * on the application's preferences. Includes a check box for each
	 * item that can be toggled by clicking the entire item.
	 */
	private final class IconAdapter extends ArrayAdapter<String>
	{
        private ArrayList<String> mItems;
		private final LayoutInflater mLI;

        public IconAdapter(Context context, int textViewResourceId, ArrayList<String> mItems)
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
                mLayout = (RelativeLayout) mLI.inflate(R.layout.check_item, null);
            else
				mLayout = (RelativeLayout) convertView;

			// Set the text and tag of the item.
			final String mText = mItems.get(position);
			final String mKey = mTextTag.get(position);
			final TextView mTV = (TextView)
				mLayout.findViewById(R.id.item_text);
			mTV.setText(mText);
			final Checkable mCheck = (Checkable)
				mLayout.findViewById(R.id.item_check);
			mCheck.setChecked(mPrefs.getBoolean(mKey, true));

            return mLayout;
        }
	}
}
