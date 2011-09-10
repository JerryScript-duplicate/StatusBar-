package com.tombarrasso.android.wp7bar;

/*
 * IStatusBarService.java
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

/**
 * Interface for communicating with the custom status bar.
 * It sits on a SYSTEM_ALERT window. These methods allow you
 * to access the view that displays various system indicators
 * including time, WiFi, Bluetooth, and more. Meant for a remote
 * service so that other applications can access this API. The
 * expand method is intentionally removed because Android requires
 * the {@link android.Manifest.permission.EXPAND_STATUS_BAR} to do
 * so and I am not looking to allow other applications access to my
 * permissions... that just isn't right.
 *
 * @author		Thomas James Barrasso <contact @ tombarrasso.com>
 * @since		09-09-2011
 * @version		1.0
 * @category	Interface
 */

interface IStatusBarService
{
	/**
	 * Hides the status bar by setting its visibility to {@link View.GONE}.
	 * This has no affect if it is already invisible.
	 */
	void hide();

	/**
	 * Sets the visibility of status bar to {@link View.VISIBLE}.
	 * This has no affect if it is already visible.
	 */
	void show();

	/**
	 * Toggles between {@link hide} and {@link show}.
	 */
	void toggle();
	
	/**
	 * Destroys the status bar completly by
	 * removing the {@link View} from its window.
	 */
	void destroy();

	/**
	 * If the status bar is currently not attached
	 * to a window, create it and add it. Otherwise
	 * calling this does nothing.
	 */
	void create();

	/**
	 * This method is specific to the Seven+ {@link StatusBarView},
	 * where the icons do not display until tapped. Calling this
	 * will force the icons to drop if they are not already.
	 */
	void drop();

	/**
	 * Disable the ability for the status bar to expand when swiped
	 * vertically downward. This is called when the screen turns off
	 * such that the status bar cannot be expanded in the lockscreen.
	 * NOT to be confused with "dropped" where all icons are visible.
	 */
	void disableExpand();

	/**
	 * @see disableExpand
	 *
	 * Does the opposite, enables system status bar expansion.
	 */
	void enableExpand();

	/**
	 * @return True if the custom status bar is dropped, false if
	 * else. This only pertains the the Seven+ {@link StatusBarView}.
	 */
	boolean isDropped();

	/**
	 * @return The color of the icons in the {@link StatusbarView}.
	 * This only pertains to the Seven+ implementation.
	 */
	int getIconColor();

	/**
	 * @return The background color of the {@link StatusbarView}.
	 * This only pertains to the Seven+ implementation.
	 */
	int getBackgroundColor();
}
