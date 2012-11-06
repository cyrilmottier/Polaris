/*
 * Copyright (C) 2012 Damian Flannery (http://www.damianflannery.com)
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
package com.cyrilmottier.polaris.cluster;

import android.graphics.drawable.Drawable;

import com.cyrilmottier.polaris.R;

/**
 * Used to represent the style properties of a ClusterSpot e.g. text size &
 * color, background drawable, width & height (taken from supplied drawable
 * bounds if not specified) etc
 * 
 * @author Damian Flannery
 */
public class ClusterSpot {

	/** Defaults **/
	private static final int DEFAULT_TEXT_SIZE = 14;
	private static final int DEFAULT_COLOR = android.R.color.white;
	private static final int DEFAULT_WIDTH = 28;
	private static final int DEFAULT_HEIGHT = 28;
	private static final int DEFAULT_IMAGE_RESOURCE = R.drawable.polaris__circle_gradient_medium;

	private String mTitle;
	private int mTextSize = DEFAULT_TEXT_SIZE;
	private int mTextColorResourceId = DEFAULT_COLOR;
	private Drawable mDrawable;
	private int mDrawableResourceId = DEFAULT_IMAGE_RESOURCE;
	private int mDrawableWidth = DEFAULT_WIDTH;
	private int mDrawableHeight = DEFAULT_HEIGHT;

	/**
	 * Create a new {@link ClusterSpot}.
	 * 
	 */
	public ClusterSpot() {
	}

	/**
	 * Create a new {@link ClusterSpot}.
	 * 
	 * @param drawable
	 *            A custom {@link Drawable} to be used as background.
	 */
	public ClusterSpot(Drawable drawable) {
		setDrawable(drawable);
	}

	/**
	 * Create a new {@link ClusterSpot}.
	 * 
	 * @param drawableResourceId
	 *            A resource id for custom {@link Drawable} to be used as
	 *            background.
	 */
	public ClusterSpot(int drawableResourceId) {
		setDrawableResourceId(drawableResourceId);
	}

	/**
	 * Create a new {@link ClusterSpot}.
	 * 
	 * @param drawableResourceId
	 *            A resource id for custom {@link Drawable} to be used as
	 *            background.
	 * @param width
	 *            custom width for drawable resource
	 * @param height
	 *            custom height for drawable resource
	 */
	public ClusterSpot(int drawableResourceId, int width, int height) {
		setDrawableResourceId(drawableResourceId);
		mDrawableWidth = width;
		mDrawableHeight = height;
	}

	/**
	 * Returns text that will be displayed on ClusterSpot drawable. If null,
	 * then number of Annotations contained within Cluster will be displayed on
	 * ClusterSpot
	 * 
	 * @return custom text to be displayed on ClusterSpot (if supplied by
	 *         caller)
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * @param title
	 *            text to be displayed on ClusterSpot drawable
	 */
	public void setTitle(String title) {
		this.mTitle = title;
	}

	/**
	 * @return the size in pixels of the text displayed on ClusterSpot drawable
	 */
	public int getTextSize() {
		return mTextSize;
	}

	/**
	 * 
	 * @param textSize
	 *            the size in pixels of the text displayed on ClusterSpot
	 *            drawable
	 */
	public void setTextSize(int textSize) {
		this.mTextSize = textSize;
	}

	/**
	 * 
	 * @return the background drawable for the ClusterSpot supplied by the
	 *         caller or null if not supplied
	 */
	public Drawable getDrawable() {
		return mDrawable;
	}

	/**
	 * Sets the background for the ClusterSpot. Please note that this will reset
	 * the bounds of this drawable will be used when drawing the ClusterSpot
	 * unless you specifically call setDrawableWidth & setDrawableHeight after
	 * this call.
	 * 
	 * @param drawable
	 *            custom Drawable for background
	 */
	public void setDrawable(Drawable drawable) {
		if (drawable == null) {
			throw new IllegalArgumentException("The supplied " + ClusterSpot.class.getSimpleName() + " drawable cannot be null");
		}

		mDrawable = drawable;

		// need to reset width & height so that Clusterer will use bounds of
		// drawable by default
		mDrawableWidth = 0;
		mDrawableHeight = 0;
	}

	/**
	 * 
	 * @return resourceId for drawable
	 */
	public int getDrawableResourceId() {
		return mDrawableResourceId;
	}

	/**
	 * Sets the background for the ClusterSpot. Please note that the bounds of
	 * this drawable will be used (once inflated) when drawing the ClusterSpot
	 * unless you specifically call setDrawableWidth & setDrawableHeight after
	 * this call.
	 * 
	 * @param drawableResourceId
	 *            resource id for drawable
	 */
	public void setDrawableResourceId(int drawableResourceId) {
		mDrawableResourceId = drawableResourceId;

		// need to reset width & height so that Clusterer will use bounds of
		// inflated drawable by default
		mDrawableWidth = 0;
		mDrawableHeight = 0;
	}

	/**
	 * This will return 0 unless it is set by the caller. It is only used when
	 * you want to override the width of the ClusterSpot which is determined
	 * from the bounds of the drawable by default
	 * 
	 * @return width of drawable in pixels
	 */
	public int getDrawableWidth() {
		return mDrawableWidth;
	}

	/**
	 * Use this if you want to override the width of the ClusterSpot
	 * 
	 * @param mDrawableWidth
	 *            custom width of ClusterSpot
	 */
	public void setDrawableWidth(int mDrawableWidth) {
		this.mDrawableWidth = mDrawableWidth;
	}

	/**
	 * This will return 0 unless it is set by the caller. It is only used when
	 * you want to override the height of the ClusterSpot which is determined
	 * from the bounds of the drawable by default
	 * 
	 * @return height of drawable in pixels
	 */
	public int getDrawableHeight() {
		return mDrawableHeight;
	}

	/**
	 * Use this if you want to override the height of the ClusterSpot
	 * 
	 * @param mDrawableHeight
	 *            custom height of ClusterSpot
	 */
	public void setDrawableHeight(int mDrawableHeight) {
		this.mDrawableHeight = mDrawableHeight;
	}

	/**
	 * 
	 * @return resourceId for text color used in ClusterSpot
	 */
	public int getTextColorResourceId() {
		return mTextColorResourceId;
	}

	/**
	 * Set the color of the text used in the ClusterSpot
	 * 
	 * @param mTextColorResourceId
	 *            resourceId for text color used in ClusterSpot
	 */
	public void setTextColorResourceId(int mTextColorResourceId) {
		this.mTextColorResourceId = mTextColorResourceId;
	}
}
