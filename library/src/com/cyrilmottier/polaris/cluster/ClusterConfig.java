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
import com.cyrilmottier.polaris.R;


/**
 * Used to define low, medium and high threshold that is used by the 
 * {@link Clusterer} when grouping overlapping {@link Annotation}s
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

    private final int lowClusterBackgroundDrawableResource;
    private final int mediumClusterBackgroundDrawableResource;
    private final int highClusterBackgroundDrawableResource;

    /**
     * Create a new {@link Clusterer} with defaults.
     */
    public ClusterConfig() {
        this(new Builder());
    }

    /**
     * Create a new {@link Clusterer} with custom values
     * Note that values must conform to low < med < high otherwise the clusterer
     * will just revert to defaults
     *
     * @param low integer to define max num of annotations to be considered as
     * 			a low frequency cluster
     * @param medium integer to define max num of annotations to be considered as
     * 			a medium frequency cluster
     */
    public ClusterConfig(int low, int medium) {
        this(new Builder().setLow(low).setMedium(medium));
    }

    private ClusterConfig(Builder builder) {
        if (builder.medium <= builder.low) {
            // TODO: Shall we throw an IllegalArgument exception here instead?
            Log.w(TAG, "Invalid params (must be low < medium < high), reverting to defaults");
            builder.setLow(LOW).setMedium(MEDIUM);
        }
        low = builder.low;
        medium = builder.medium;
        lowClusterBackgroundDrawableResource = builder.lowClusterBackgroundDrawableResource;
        mediumClusterBackgroundDrawableResource = builder.mediumClusterBackgroundDrawableResource;
        highClusterBackgroundDrawableResource = builder.highClusterBackgroundDrawableResource;
    }

    public int getLow() {
        return low;
    }

    public int getMedium() {
        return medium;
    }

    public int getLowClusterBackgroundDrawableResource() {
        return lowClusterBackgroundDrawableResource;
    }

    public int getMediumClusterBackgroundDrawableResource() {
        return mediumClusterBackgroundDrawableResource;
    }

    public int getHighClusterBackgroundDrawableResource() {
        return highClusterBackgroundDrawableResource;
    }

    public static class Builder {
        private int low = LOW;
        private int medium = MEDIUM;

        private int lowClusterBackgroundDrawableResource = R.drawable.polaris__circle_gradient_low;
        private int mediumClusterBackgroundDrawableResource = R.drawable.polaris__circle_gradient_medium;
        private int highClusterBackgroundDrawableResource = R.drawable.polaris__circle_gradient_high;

        public Builder() {}

        public Builder(int low, int medium) {
            this.low = low;
            this.medium = medium;
        }

        /**
         *
         * @param low integer to define max num of annotations to be considered as
         * 			a low frequency cluster
         * @return this builder
         */
        public Builder setLow(int low) {
            this.low = low;
            return this;
        }

        /**
        *
        * @param medium integer to define max num of annotations to be considered as
        * 			a medium frequency cluster
        * @return this builder
        */
        public Builder setMedium(int medium) {
            this.medium = medium;
            return this;
        }

        /**
         *
         * @param lowClusterBackgroundDrawableResource background drawable resource for annotations
         *          with low frequency
         * @return this builder
         */
        public Builder setLowClusterBackgroundDrawableResource(int lowClusterBackgroundDrawableResource) {
            this.lowClusterBackgroundDrawableResource = lowClusterBackgroundDrawableResource;
            return this;
        }

        /**
         *
         * @param  mediumClusterBackgroundDrawableResource background drawable resource for annotations
         *          with medium frequency
         * @return this builder
         */
        public Builder setMediumClusterBackgroundDrawableResource(int mediumClusterBackgroundDrawableResource) {
            this.mediumClusterBackgroundDrawableResource = mediumClusterBackgroundDrawableResource;
            return this;
        }

        /**
         *
         * @param  highClusterBackgroundDrawableResource background drawable resource for annotations
         *          with high frequency
         * @return this builder
         */
        public Builder setHighClusterBackgroundDrawableResource(int highClusterBackgroundDrawableResource) {
            this.highClusterBackgroundDrawableResource = highClusterBackgroundDrawableResource;
            return this;
        }

        /**
         *
         * @return a new @{link ClusterConfig} with the specified configuration.
         */
        public ClusterConfig build() {
            if (medium <= low) {
                // TODO: Shall we throw an IllegalArgument exception here instead?
                Log.w(TAG, "Invalid params (must be low < medium < high), reverting to defaults");
                return new ClusterConfig(new Builder());
            }
            return new ClusterConfig(this);
        }
    }
}
