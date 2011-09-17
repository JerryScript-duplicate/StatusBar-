/*
 * Copyright (C) 2010 Daniel Nilsson
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

package afzkl.development.mColorPicker;

// Color Picker Packages
import afzkl.development.mColorPicker.views.ColorPanelView;
import afzkl.development.mColorPicker.views.ColorPickerView;
import afzkl.development.mColorPicker.views.ColorPickerView.OnColorChangedListener;

// Android Packages
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

// App Packages
import com.tombarrasso.android.wp7bar.R;

public class ColorPickerDialog extends AlertDialog implements
		ColorPickerView.OnColorChangedListener {

	private ColorPickerView mColorPicker;

	private ColorPanelView mOldColor;
	private ColorPanelView mNewColor;

	private OnColorChangedListener mListener;

	public ColorPickerDialog(Context context, int initialColor) {
		super(context);

		init(initialColor);
	}

	private void init(int color) {
		// To fight color branding.
		getWindow().setFormat(PixelFormat.RGBA_8888);

		setUp(color);

	}

	private void setUp(int color) {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_color_picker, null);

		setView(layout);

		setTitle("Pick a Color");
		// setIcon(android.R.drawable.ic_dialog_info);

		try
		{
			mColorPicker = (ColorPickerView) layout
					.findViewById(R.id.color_picker_view);
		}
		catch(ClassCastException e) { }
		
		try
		{
			mOldColor = (ColorPanelView) layout.findViewById(R.id.old_color_panel);
		}
		catch(ClassCastException e) { }

		try
		{
			mNewColor = (ColorPanelView) layout.findViewById(R.id.new_color_panel);
		}
		catch(ClassCastException e) { }

		if (mOldColor != null && mColorPicker != null)
			((LinearLayout) mOldColor.getParent()).setPadding(Math
				.round(mColorPicker.getDrawingOffset()), 0, Math
				.round(mColorPicker.getDrawingOffset()), 0);

		if (mColorPicker != null)
			mColorPicker.setOnColorChangedListener(this);

		if (mOldColor != null)
			mOldColor.setColor(color);

		if (mColorPicker != null)
			mColorPicker.setColor(color, true);

		// Default enable alpha slider.
		setAlphaSliderVisible(true);
	}

	/**
	 * Set the listener... ah, you know what it is for.
	 */
	public void setOnColorChangedListener(OnColorChangedListener mListener)
	{
		this.mListener = mListener;
	}

	@Override
	public void onColorChanged(int color) {

		if (mNewColor != null)
			mNewColor.setColor(color);

		if (mListener != null) {
			mListener.onColorChanged(color);
		}

	}

	public void setAlphaSliderVisible(boolean visible) {
		if (mColorPicker != null)
			mColorPicker.setAlphaSliderVisible(visible);
	}

	public int getColor() {
		if (mColorPicker != null)
			return mColorPicker.getColor();

		return Color.TRANSPARENT;
	}

}
