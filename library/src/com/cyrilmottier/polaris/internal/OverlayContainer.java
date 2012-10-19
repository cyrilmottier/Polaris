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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

/**
 * @author Cyril Mottier
 */
public class OverlayContainer extends Overlay {

    /**
     * @author Cyril Mottier
     */
    public interface MagnetoCallback {
        void onSimpleTap(MotionEvent e);

        void onDoubleTap(MotionEvent e);

        void onLongPress(MotionEvent e);
    }

    @SuppressWarnings("serial")
    private final ArrayList<Overlay> mOverlays = new ArrayList<Overlay>() {
        @Override
        public Overlay get(int index) {
            boolean hasLocationOverlay = mLocationOverlay != null;
            boolean hasAnnotationsOverlay = mAnnotationsOverlay != null;

            switch (index) {
                case 0:
                    if (hasLocationOverlay) {
                        return mLocationOverlay;
                    } else if (hasAnnotationsOverlay) {
                        return mAnnotationsOverlay;
                    }
                    break;

                case 1:
                    if (hasLocationOverlay && hasAnnotationsOverlay) {
                        return mAnnotationsOverlay;
                    }
                    break;
            }

            int reindex = index;
            if (hasLocationOverlay) {
                reindex--;
            }
            if (hasAnnotationsOverlay) {
                reindex--;
            }

            return super.get(reindex);
        }

        @Override
        public int size() {
            int size = super.size();
            if (mLocationOverlay != null) {
                size++;
            }
            if (mAnnotationsOverlay != null) {
                size++;
            }
            return size;
        }
    };

    private final GestureDetector mGestureDetector;
    private final MagnetoCallback mCallback;

    private AnnotationsOverlay mAnnotationsOverlay;
    private MyLocationOverlay mLocationOverlay;
    private boolean mIsTapConsumedPerChildren;

    public OverlayContainer(Context context, MagnetoCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("The given " + MagnetoCallback.class.getSimpleName() + " cannot be null");
        }
        mCallback = callback;
        mGestureDetector = new GestureDetector(context, mOnGestureListener);
        mGestureDetector.setOnDoubleTapListener(mOnGestureListener);
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        final List<Overlay> overlays = mOverlays;
        final int count = overlays.size();
        for (int i = 0; i < count; i++) {
            overlays.get(i).draw(canvas, mapView, shadow);
        }
    }

    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
        boolean result = false;
        final List<Overlay> overlays = mOverlays;
        final int count = overlays.size();
        for (int i = 0; i < count; i++) {
            result |= overlays.get(i).draw(canvas, mapView, shadow, when);
        }
        return result;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event, MapView mapView) {
        boolean result = false;

        final List<Overlay> overlays = mOverlays;
        final int count = overlays.size();
        for (int i = count - 1; i >= 0; i--) {
            result |= overlays.get(i).onKeyDown(keyCode, event, mapView);
        }

        return result;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event, MapView mapView) {
        boolean result = false;
        final List<Overlay> overlays = mOverlays;
        final int count = overlays.size();
        for (int i = count - 1; i >= 0; i--) {
            result |= overlays.get(i).onKeyUp(keyCode, event, mapView);
        }
        return result;
    }

    public boolean onTap(GeoPoint p, MapView mapView) {
        mIsTapConsumedPerChildren = false;

        final List<Overlay> overlays = mOverlays;
        final int count = overlays.size();
        for (int i = count - 1; i >= 0; i--) {
            if (overlays.get(i).onTap(p, mapView)) {
                mIsTapConsumedPerChildren = true;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event, MapView mapView) {
        boolean result = false;
        final List<Overlay> overlays = mOverlays;
        final int count = overlays.size();
        for (int i = count - 1; i >= 0; i--) {
            result |= overlays.get(i).onTouchEvent(event, mapView);
        }

        // Let us the time to listen to some gestures on the MapView
        mGestureDetector.onTouchEvent(event);

        return result;
    }

    public boolean onTrackballEvent(MotionEvent event, MapView mapView) {
        boolean result = false;
        final List<Overlay> overlays = mOverlays;
        final int count = overlays.size();
        for (int i = count - 1; i >= 0; i--) {
            result |= overlays.get(i).onTrackballEvent(event, mapView);
        }
        return result;
    }

    public void setUserLocationOverlay(MyLocationOverlay overlay) {
        mLocationOverlay = overlay;
    }

    public void setAnnotationsOverlay(AnnotationsOverlay overlay) {
        mAnnotationsOverlay = overlay;
    }

    public void addOverlay(Overlay overlay) {
        mOverlays.add(overlay);
    }

    public void addOverlay(int index, Overlay overlay) {
        mOverlays.add(index, overlay);
    }

    public void removeOverlay(Overlay overlay) {
        mOverlays.remove(overlay);
    }

    public void removeOverlay(int index) {
        mOverlays.remove(index);
    }

    public void removeAllOverlays() {
        mOverlays.clear();
    }

    public int indexOfOverlay(Overlay overlay) {
        return mOverlays.indexOf(overlay);
    }

    private final SimpleOnGestureListener mOnGestureListener = new SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (!mIsTapConsumedPerChildren) {
                mCallback.onSimpleTap(e);
            }
            return true;
        }

        public void onLongPress(MotionEvent e) {
            mCallback.onLongPress(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            mCallback.onDoubleTap(e);
            return true;
        }
    };

}
