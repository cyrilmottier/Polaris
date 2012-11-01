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

import android.util.Log;

import com.cyrilmottier.polaris.Annotation;

/**
 * Used to define low, medium and high threshold that is used by the 
 * {@link Clusterer} when grouping overlapping {@link Annotation}s
 * 
 * @author Damian Flannery
 */
public class ClusterConfig {
	
	private static final String TAG = ClusterConfig.class.getSimpleName();
	
	public static int LOW = 10;
	public static int MEDIUM = 20;
	
	private int low = LOW;
	private int medium = MEDIUM;
	
	/**
     * Create a new {@link Clusterer} with defaults.
     */
	public ClusterConfig() {
	}

	/**
     * Create a new {@link Clusterer} with custom values
     * Note that values must conform to low < med < high otherwise the clusterer
     * will just revert to defaults
     * 
     * @param low integer to define max num of annotations to be considered as 
     * 			a low frequency cluster
     * @param mediun integer to define max num of annotations to be considered as 
     * 			a medium frequency cluster
     * @param high integer to define max num of annotations to be considered as 
     * 			a high frequency cluster
     */
	public ClusterConfig(int low, int medium) {
		
		if (low >= medium) {
			Log.w(TAG, "Invalid params (must be low < medium < high), reverting to defaults");
			return;
		}
		
		this.low = low;
		this.medium = medium;
	}
	
	public int getLow() {
		return low;
	}

	public int getMedium() {
		return medium;
	}
}
