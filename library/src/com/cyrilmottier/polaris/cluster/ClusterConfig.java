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
import android.util.Log;

import com.cyrilmottier.polaris.Annotation;
import com.cyrilmottier.polaris.R;

/**
 * Used to define low, medium and high threshold that is used by the
 * {@link Clusterer} when grouping overlapping {@link Annotation}s.
 * 
 * Also used to customise the general appearance of the ClusterSpot
 * 
 * @author Damian Flannery
 * @author Stefano Dacchille
 */
public class ClusterConfig {

	private static final String TAG = ClusterConfig.class.getSimpleName();

	public static int LOW = 10;
	public static int MEDIUM = 20;

	private final int low;
	private final int medium;

	private ClusterSpot lowClusterSpot;
	private ClusterSpot mediumClusterSpot;
	private ClusterSpot highClusterSpot;

	/**
	 * Create a new {@link Clusterer} with defaults.
	 */
	public ClusterConfig(Context context) {
		this(context, new Builder(context));
	}

	/**
	 * Create a new {@link Clusterer} with custom values Note that values must
	 * conform to low < med < high otherwise the clusterer will just revert to
	 * defaults
	 * 
	 * @param low
	 *            integer to define max num of annotations to be considered as a
	 *            low frequency cluster
	 * @param medium
	 *            integer to define max num of annotations to be considered as a
	 *            medium frequency cluster
	 */
	public ClusterConfig(Context context, int low, int medium) {
		this(context, new Builder(context).setLow(low).setMedium(medium));
	}

	private ClusterConfig(Context context, Builder builder) {
		if (builder.medium <= builder.low) {
			throw new IllegalArgumentException("Invalid params (must be low < medium < high)");
		}
		low = builder.low;
		medium = builder.medium;

		lowClusterSpot = builder.lowClusterSpot;
		mediumClusterSpot = builder.mediumClusterSpot;
		highClusterSpot = builder.highClusterSpot;
	}

	public int getLow() {
		return low;
	}

	public int getMedium() {
		return medium;
	}

	public ClusterSpot getLowClusterSpot() {
		return lowClusterSpot;
	}

	public ClusterSpot getMediumClusterSpot() {
		return mediumClusterSpot;
	}

	public ClusterSpot getHighClusterSpot() {
		return highClusterSpot;
	}

	public static class Builder {
		private int low = LOW;
		private int medium = MEDIUM;

		private ClusterSpot lowClusterSpot;
		private ClusterSpot mediumClusterSpot;
		private ClusterSpot highClusterSpot;

		private Context mContext;

		public Builder(Context context) {
			mContext = context;
			init();
		}

		public Builder(Context context, int low, int medium) {
			mContext = context;
			this.low = low;
			this.medium = medium;
			init();
		}

		private void init() {
			lowClusterSpot = new ClusterSpot(R.drawable.polaris__circle_gradient_low, 
					mContext.getResources().getDimensionPixelSize(R.dimen.polaris__cluster_spot_size_low), 
						mContext.getResources().getDimensionPixelSize(R.dimen.polaris__cluster_spot_size_low));

			mediumClusterSpot = new ClusterSpot(R.drawable.polaris__circle_gradient_medium, mContext.getResources().getDimensionPixelSize(R.dimen.polaris__cluster_spot_size_medium), mContext
					.getResources().getDimensionPixelSize(R.dimen.polaris__cluster_spot_size_medium));

			highClusterSpot = new ClusterSpot(R.drawable.polaris__circle_gradient_high, mContext.getResources().getDimensionPixelSize(R.dimen.polaris__cluster_spot_size_high), mContext.getResources()
					.getDimensionPixelSize(R.dimen.polaris__cluster_spot_size_high));
		}

		/**
		 * 
		 * @param low
		 *            integer to define max num of annotations to be considered
		 *            as a low frequency cluster
		 * @return this builder
		 */
		public Builder setLow(int low) {
			this.low = low;
			return this;
		}

		/**
		 * 
		 * @param medium
		 *            integer to define max num of annotations to be considered
		 *            as a medium frequency cluster
		 * @return this builder
		 */
		public Builder setMedium(int medium) {
			this.medium = medium;
			return this;
		}

		/**
		 * 
		 * @param highClusterSpot
		 *            ClusterSpot to change the appearance of the drawable and
		 *            text used for high frequency clusters
		 * @return this builder
		 */
		public Builder setHighClusterSpot(ClusterSpot highClusterSpot) {
			this.highClusterSpot = highClusterSpot;
			return this;
		}

		/**
		 * 
		 * @param mediumClusterSpot
		 *            ClusterSpot to change the appearance of the drawable and
		 *            text used for medium frequency clusters
		 * @return this builder
		 */
		public Builder setMediumClusterSpot(ClusterSpot mediumClusterSpot) {
			this.mediumClusterSpot = mediumClusterSpot;
			return this;
		}

		/**
		 * 
		 * @param lowClusterSpot
		 *            ClusterSpot to change the appearance of the drawable and
		 *            text used for low frequency clusters
		 * @return this builder
		 */
		public Builder setLowClusterSpot(ClusterSpot lowClusterSpot) {
			this.lowClusterSpot = lowClusterSpot;
			return this;
		}

		/**
		 * 
		 * @return a new @{link ClusterConfig} with the specified configuration.
		 */
		public ClusterConfig build() {
			if (medium <= low) {
				throw new IllegalArgumentException("Invalid params (must be low < medium < high)");
			}
			return new ClusterConfig(mContext, this);
		}
	}
}