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
package com.cyrilmottier.polaris.internal;

import java.util.List;

import android.graphics.drawable.Drawable;

import com.cyrilmottier.polaris.Annotation;
import com.cyrilmottier.polaris.PolarisMapView;
import com.google.android.maps.ItemizedOverlay;

/**
 * @author Cyril Mottier
 */
public class AnnotationsOverlay extends ItemizedOverlay<Annotation> {

    private static final int INVALID_POSITION = PolarisMapView.INVALID_POSITION;

    /**
     * @author Cyril Mottier
     */
    public interface MystiqueCallback {
        void dismissCallout(int position);

        void showCallout(int position);
    }

    private final List<Annotation> mAnnotations;
    private final Drawable mDefaultMarker;
    private final MystiqueCallback mCallback;
    private int mSelectedAnnotation = INVALID_POSITION;

    public AnnotationsOverlay(MystiqueCallback callback, List<Annotation> annotations, Drawable defaultMarker) {
        super(boundCenterBottom(defaultMarker));
        if (callback == null) {
            throw new IllegalArgumentException("The given " + MystiqueCallback.class.getSimpleName() + " cannot be null");
        }
        mCallback = callback;
        mDefaultMarker = defaultMarker;
        mAnnotations = annotations;
        populate();
    }

    public Drawable getDefaultMarker() {
        return mDefaultMarker;
    }

    @Override
    protected Annotation createItem(int index) {
        return mAnnotations.get(index);
    }

    @Override
    public int size() {
        return mAnnotations.size();
    }

    @Override
    protected boolean onTap(int index) {
        if (index >= 0 && index < size()) {
            setSelectedAnnotation(index);
            return true;
        }
        return super.onTap(index);
    }

    public Annotation getAnnotation(int index) {
        if (index < 0 || index >= size()) {
            return null;
        }
        return mAnnotations.get(index);
    }

    public int getSelectedAnnotation() {
        return mSelectedAnnotation;
    }

    public void setSelectedAnnotation(int position) {
        if (position != INVALID_POSITION) {
            if (position < 0 || position >= size()) {
                position = INVALID_POSITION;
            }
        }

        if (position != mSelectedAnnotation) {
            setFocus(getAnnotation(position));
            if (mSelectedAnnotation != INVALID_POSITION) {
                mCallback.dismissCallout(mSelectedAnnotation);
            }
            mSelectedAnnotation = position;
            if (position != INVALID_POSITION) {
                mCallback.showCallout(position);
            }
        }
    }

}
