/*
 * Copyright (C) 2012 Cyril Mottier (http://www.cyrilmottier.com)
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
package com.cyrilmottier.polaris;

import com.google.android.maps.MapView;

/**
 * Representation of a {@link MapView} region in the coordinate space. A
 * {@link CoordinateRegion} is defined by a point - the center of the region -
 * and two spans for the latitude and longitude - the extend of the region
 * around the center.
 * 
 * @author Cyril Mottier
 */
public class CoordinateRegion {

    /**
     * The latitude of this region's center.
     */
    public int latitude;

    /**
     * The longitude of this region's center.
     */
    public int longitude;

    /**
     * The latitude span of this region.
     */
    public int latitudeSpan;

    /**
     * The longitude span of this region.
     */
    public int longitudeSpan;

    /**
     * Create a new empty region. All coordinates are initialized to 0.
     */
    public CoordinateRegion() {
    }

    /**
     * Create a new region with the specified values. Note: no range checking is
     * performed, so it is up to the caller to ensure that latitude, longitude,
     * latitudeSpan and longitudeSpan are valid.
     * 
     * @param latitude The latitude of the region
     * @param longitude The longitude of the region
     * @param latitudeSpan The span on the latitude axis of the region
     * @param longitudeSpan The span on the longitude axis of the region
     */
    public CoordinateRegion(int latitude, int longitude, int latitudeSpan, int longitudeSpan) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.latitudeSpan = latitudeSpan;
        this.longitudeSpan = longitudeSpan;
    }

    /**
     * Create a new region, initialized with the values in the specified region
     * (which is left unmodified).
     * 
     * @param region The region whose coordinates are copied into this region.
     */
    public CoordinateRegion(CoordinateRegion region) {
        this.latitude = region.latitude;
        this.longitude = region.longitude;
        this.latitudeSpan = region.latitudeSpan;
        this.longitudeSpan = region.longitudeSpan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CoordinateRegion region = (CoordinateRegion) o;
        return latitude == region.latitude && longitude == region.longitude && latitudeSpan == region.latitudeSpan
                && longitudeSpan == region.longitudeSpan;
    }

    @Override
    public int hashCode() {
        int result = latitude;
        result = 31 * result + longitude;
        result = 31 * result + latitudeSpan;
        result = 31 * result + longitudeSpan;
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(32);
        sb.append("CoordinateRegion(");
        sb.append(latitude);
        sb.append(", ");
        sb.append(longitude);
        sb.append(" - ");
        sb.append(latitudeSpan);
        sb.append(", ");
        sb.append(longitudeSpan);
        sb.append(")");
        return sb.toString();
    }

    /**
     * Set the {@link CoordinateRegion} to the specified values. Note: no range
     * checking is performed, so it is up to the caller to ensure that latitude,
     * longitude, latitudeSpan and longitudeSpan are valid.
     * 
     * @param latitude The latitude of the region
     * @param longitude The longitude of the region
     * @param latitudeSpan The span on the latitude axis of the region
     * @param longitudeSpan The span on the longitude axis of the region
     */
    public void set(int latitude, int longitude, int latitudeSpan, int longitudeSpan) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.latitudeSpan = latitudeSpan;
        this.longitudeSpan = longitudeSpan;
    }

    /**
     * Copy the coordinates into this {@link CoordinateRegion}.
     * 
     * @param region The region whose coordinates are copied into this region
     */
    public void set(CoordinateRegion region) {
        latitude = region.latitude;
        longitude = region.longitude;
        latitudeSpan = region.latitudeSpan;
        longitudeSpan = region.longitudeSpan;
    }

    /**
     * Returns true if the region defined by this {@link CoordinateRegion} is
     * empty i.e. if at least one of the span values is <= 0.
     * 
     * @return true if this region is empty
     */
    public boolean isEmpty() {
        return latitudeSpan <= 0 || longitudeSpan <= 0;
    }

    /**
     * Set the {@link CoordinateRegion} to (0,0,0,0).
     */
    public void setEmpty() {
        latitude = longitude = latitudeSpan = longitudeSpan = 0;
    }

}
