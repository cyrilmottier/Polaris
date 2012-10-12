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

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * The basic component to use in order to easily annotate a
 * {@link PolarisMapView}.
 * 
 * @author Cyril Mottier
 */
public class Annotation extends OverlayItem {

	private Object mExtra;

    /**
     * Construct an {@link Annotation}.
     * 
     * @param point Position of the annotation.
     * @param title Title text for this annotation
     */
    public Annotation(GeoPoint point, String title) {
        this(point, title, null, null, null);
    }

    /**
     * Construct an {@link Annotation}.
     * 
     * @param point Position of the annotation.
     * @param title Title text for this annotation
     * @param snippet Snippet text for this annotation
     */
    public Annotation(GeoPoint point, String title, String snippet) {
        this(point, title, snippet, null, null);
    }

    /**
     * Construct an {@link Annotation}.
     * 
     * @param point Position of the annotation.
     * @param title Title text for this annotation
     * @param snippet Snippet text for this annotation
     * @param marker Drawable used as this {@link Annotation}'s marker (please
     *            note this marker must have its bounds already set. You canuse
     *            the {@link MapViewUtils#boundMarker(Drawable, int)} utility
     *            method to prepare this {@link Drawable}'s bounds)
     */
    public Annotation(GeoPoint point, String title, String snippet, Drawable marker) {
        this(point, title, snippet, marker, null);
    }

    /**
     * Construct an {@link Annotation}.
     * 
     * @param point Position of the annotation.
     * @param title Title text for this annotation
     * @param snippet Snippet text for this annotation
     * @param marker Drawable used as this {@link Annotation}'s marker (please
     *            note this marker must have its bounds already set. You canuse
     *            the {@link MapViewUtils#boundMarker(Drawable, int)} utility
     *            method to prepare this {@link Drawable}'s bounds)
     * @param extra Extra information of the annotation
     */
    public Annotation(GeoPoint point, String title, String snippet, Drawable marker, Object extra) {
        super(point, title, snippet);
        mMarker = marker;
        mExtra = extra;
    }

    /**
     * Return the Drawable use as this {@link Annotation}'s marker or null if
     * none have been set.
     * 
     * @return The marker {@link Drawable}
     */
    public Drawable getMarker() {
        return mMarker;
    }

    /**
     * Return the Extra information of this {@link Annotation}
     * 
     * @return The extra
     */
    public Object getExtra() {
        return mExtra;
    }

    /**
     * Sets the {@link Annotation} extra
     * 
     * @param extra Extra information for the annotation
     */
    public void setExtra(Object extra) {
        mExtra = extra;
    }
}
