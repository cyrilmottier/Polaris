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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cyrilmottier.polaris.MapViewUtils;
import com.google.android.maps.MapView;

/**
 * Some utility methods for Clustering.
 * 
 * @author Damian Flannery
 */
public class ClusterUtils {
	
	/**
     * Draws a view into a Bitmap
     * 
     * @param view The View to be converted to a Bitmap
     * @param size The dimensions (width & height) of bitmap to be returned
     * @return The Bitmap representation of the view
     */
	public static Bitmap loadBitmapFromView(View v, int size) {
		Bitmap bm = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		v.draw(new Canvas(bm));
		return bm;
	}

	/**
     * Creates a cluster spot drawable to be used as a map marker with a given size,
     * background and title text.
     * 
     * @param context Activity context
     * @param size The desired dimensions (width & height) of the cluster spot
     * @param backgroundResourceId Drawable resource to be used as background for the cluster spot
     * @param title Title text for this cluster spot     
     * 
     * @return A Map Marker Drawable 
     */
	public static Drawable createClusterSpot(Context context, int size, int backgroundResourceId, String title) {
		
		//Create root container and textview 
		RelativeLayout relativeLayout = new RelativeLayout(context);
		TextView tv = new TextView(context);
		tv.setText(title);
		tv.setTextColor(context.getResources().getColor(android.R.color.white));

		// Defining the layout parameters of the TextView
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(size, size);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);

		// Setting the parameters on the TextView
		tv.setLayoutParams(lp);
		tv.setGravity(Gravity.CENTER);
		
		// Adding the TextView to the RelativeLayout as a child
		relativeLayout.addView(tv);
		relativeLayout.setBackgroundResource(backgroundResourceId);
		relativeLayout.measure(MeasureSpec.makeMeasureSpec(size,
				MeasureSpec.EXACTLY), 
					MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY));
		relativeLayout.layout(0, 0, relativeLayout.getMeasuredWidth(), relativeLayout.getMeasuredHeight());

		//Convert our view to a Drawable and set its bounds
		Drawable d = new BitmapDrawable(context.getResources(), ClusterUtils.loadBitmapFromView(relativeLayout, size));
		return MapViewUtils.boundMarkerCenterBottom(d);

	}
}
