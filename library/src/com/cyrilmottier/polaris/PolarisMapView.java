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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.cyrilmottier.polaris.MapCalloutView.OnDoubleTapListener;
import com.cyrilmottier.polaris.internal.AnnotationsOverlay;
import com.cyrilmottier.polaris.internal.OverlayContainer;
import com.cyrilmottier.polaris.internal.AnnotationsOverlay.MystiqueCallback;
import com.cyrilmottier.polaris.internal.OverlayContainer.MagnetoCallback;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

//@formatter:off
/**
 * An extension of the regular {@link MapView} that provides several useful
 * features. Here is a incomplete list of the features supported by
 * {@link PolarisMapView}:
 * 
 * <h1>Support of gestures</h1>
 * <ul>
 *   <li>Single tap: Open the callout associated to the tapped {@link Annotation}</li>
 *   <li>Double tap: Zoom in focusing on the tapped location</li>
 *   <li>Long press: Do nothing by default. Clients may use {@link OnMapViewLongClickListener} 
 *       to be notified of long presses on the {@link PolarisMapView}</li>
 *   <li>Double tap on callout: Zoom in/pan to the maximum level of detail available</li>
 * </ul>
 * 
 * <h1>Effortless map annotating</h1>
 * <p>{@link PolarisMapView} drastically simplifies addition of markers on a {@link MapView}
 * by getting rid of the {@link ItemizedOverlay} and introducing {@link Annotation}s.
 * {@link Annotation} is an extension of {@link OverlayItem}. It contains map-related
 * information such as coordinates of a point, a title, a snippet and an optional marker
 * Drawable. Annotating a {@link PolarisMapView} consists on building a list of
 * {@link Annotation}s and adding it to the map with the {@link #setAnnotations(List, int)} or
 * the {@link #setAnnotations(List, Drawable)} method.</p>
 * 
 * <h1>Automatic management of map callout</h1>
 * <p>The Google Maps External Library includes an {@link OverlayItem} containing a two Strings:
 * a title and a snippet. Unfortunately, no matter how hard you search into the documentation,
 * you will notice these Strings are NEVER used, ever. {@link PolarisMapView} automatically 
 * displays a map callout when the underlying annotation is tapped. This makes addition of 
 * markers to a map insanely easy.</p>
 * 
 * <h1>Built-in "user tracking" button.</h1>
 * <p>When user tracking is enabled, {@link PolarisMapView} automatically tracks user location in
 * the background (internally uses a {@link MyLocationOverlay}) and displays it on the map. The 
 * map can still be zoomed in and out and panned. In addition to automatic tracking, a button letting
 * the user re-center the map on its current location is also overlaid in the top right-hand corner.</p>
 * 
 * <h1>Automatic built-in zoom controls</h1>
 * <p>Android is used on a large set of devices. The vast majority of these devices now supports 
 * multi-touch gestures. However, some other don't. {@link PolarisMapView} seamlessly displays 
 * zoom controls when required (i.e. the device doesn't support pinch-to-zoom). It prevents modern
 * devices from reducing the visible portion of the map with useless controls.</p>
 * 
 * <h1>Map callout variable anchor positioning</h1>
 * <p>Most (or should I say all) map-based applications uses 9-patches as map callout background.
 * While 9-patches are great in most cases, it doesn't allow variable stretching of stretchable areas.
 * In general, the Polaris library contains default resources and more specifically 
 * {@link MapCalloutView}. {@link MapCalloutView} allows variable positioning of the anchor. This
 * improvement is largely used by the Polaris library to get a more polished map. While most 
 * applications center the map on the tapped {@link OverlayItem}, {@link PolarisMapView} shows a
 * map callout trying to reduce scrolling as much as possible. The map is actually scrolled only there
 * is not enough space to entirely show the map callout on screen.</p>
 * 
 * <h1>Smooth showing/dismissing of map callouts</h1>
 * <p>{@link PolarisMapView} animates showing and dismissing of map callouts. This results in a 
 * more natural and smoother annotation rendering.</p>
 * 
 * <h1>Additional listener</h1>
 * <p>{@link PolarisMapView} lets you listen to region changes (i.e. when the map has been zoomed 
 * and/or panned. Using {@link OnRegionChangedListener} can be particularly useful in order to lazy
 * load annotations depending on the currently visible region displayed by the {@link PolarisMapView}
 * </p>
 * 
 * <p><strong>Note: </strong>{@link PolarisMapView} relies on {@link MapView} and tries to be as 
 * transparent as possible. However there is a limitation when adding {@link Overlay}s to the 
 * {@link PolarisMapView}. You MUST NOT use the {@link MapView#getOverlays()} method and use the 
 * {@link #addOverlay(Overlay)}, {@link #removeOverlay(Overlay)} and their equivalents instead.</p>
 * 
 * <p><strong>Note: </strong>Do not forget to call onStart() and onStop() in your {@link MapActivity}
 * equivalent. These methods binds the {@link PolarisMapView} to the {@link MapActivity} lifecycle.</p>
 * 
 * @author Cyril Mottier
 */
//@formatter:on
public class PolarisMapView extends MapView {

    /**
     * Represents an invalid position. All valid positions are in the range 0 to
     * 1 less than the number of annotations.
     */
    public static final int INVALID_POSITION = -1;

    /**
     * Clients may use this interface to listen to a region change. For
     * instance, listening to confirmed region changes can be a good way to
     * lazy-load pins only for the visible map region.
     * 
     * @author Cyril Mottier
     */
    public interface OnRegionChangedListener {
        /**
         * Tells client that the region displayed by the {@link PolarisMapView}
         * just changed.
         * <p>
         * <strong>Note</strong>: This method is called whenever the currently
         * displayed map region changes. During scrolling, this method may be
         * called many times to report updates to the map position. Therefore,
         * your implementation of this method should be as lightweight as
         * possible to avoid affecting scrolling performance. If you are just
         * looking to be notified once a region change is confirmed, prefer
         * listen to {@link #onRegionChangeConfirmed(PolarisMapView)}.
         * </p>
         * 
         * @param mapView The {@link PolarisMapView} whose visible region
         *            changed
         * @see #onRegionChangeConfirmed(PolarisMapView)
         */
        void onRegionChanged(PolarisMapView mapView);

        /**
         * Equivalent to {@link #onRegionChanged(PolarisMapView)}. This method
         * has the advantage to be called only when a change has been confirmed
         * (i.e. once animations and gestures are over).
         * 
         * @param mapView The {@link PolarisMapView} whose visible region
         *            changed
         * @see #onRegionChanged(PolarisMapView)
         */
        void onRegionChangeConfirmed(PolarisMapView mapView);
    }

    /**
     * This adapter class provides empty implementations of the methods from
     * {@link OnRegionChangedListener}. Any custom listener that cares only
     * about a subset of the methods of this listener can simply subclass this
     * adapter class instead of implementing the interface directly.
     * 
     * @author Cyril Mottier
     */
    public static class OnMapViewRegionChangedListenerAdapter implements OnRegionChangedListener {
        @Override
        public void onRegionChanged(PolarisMapView mapView) {
        }

        @Override
        public void onRegionChangeConfirmed(PolarisMapView mapView) {
        }
    }

    /**
     * Clients may use this interface to listen to annotation selection changes.
     * 
     * @author Cyril Mottier
     */
    public interface OnAnnotationSelectionChangedListener {
        /**
         * Tells client that an annotation has been selected. An annotation can
         * be selected either because a marker has been tapped or because it has
         * been selected programmatically.
         * 
         * @param mapView The {@link MapView} whose annotation has been selected
         * @param calloutView The {@link View} that is used as a callout for
         *            this annotation
         * @param position The position of the annotation in the annotations
         *            list
         * @param annotation The annotation associated to the tapped marker
         */
        void onAnnotationSelected(PolarisMapView mapView, MapCalloutView calloutView, int position, Annotation annotation);

        /**
         * Tells client that an annotation has been de-selected. An annotation
         * can be de-selected either because the map has been tapped or because
         * it has been de-selected programmatically.
         * 
         * @param mapView The {@link MapView} whose annotation has been
         *            de-selected
         * @param calloutView The {@link View} that is used as a callout for
         *            this annotation
         * @param position The position of the annotation in the annotations
         *            list
         * @param annotation The annotation
         */
        void onAnnotationDeselected(PolarisMapView mapView, MapCalloutView calloutView, int position, Annotation annotation);

        /**
         * Indicates the selected annotation has been clicked. This is usually
         * the callback one may use to show additional information about the
         * clicked annotation.
         * 
         * @param mapView The {@link MapView} whose annotation has been clicked
         * @param calloutView The {@link View} that is used as a callout for
         *            this annotation
         * @param position The position of the annotation in the annotations
         *            list
         * @param annotation The annotation
         */
        public void onAnnotationClicked(PolarisMapView mapView, MapCalloutView calloutView, int position, Annotation annotation);
    }

    /**
     * This adapter class provides empty implementations of the methods from
     * {@link OnAnnotationSelectionChangedListener}. Any custom listener that
     * cares only about a subset of the methods of this listener can simply
     * subclass this adapter class instead of implementing the interface
     * directly.
     * 
     * @author Cyril Mottier
     */
    public static class OnAnnotationSelectionChangedListenerAdapter implements OnAnnotationSelectionChangedListener {
        @Override
        public void onAnnotationSelected(PolarisMapView mapView, MapCalloutView calloutView, int position, Annotation annotation) {
        }

        @Override
        public void onAnnotationDeselected(PolarisMapView mapView, MapCalloutView calloutView, int position, Annotation annotation) {
        }

        @Override
        public void onAnnotationClicked(PolarisMapView mapView, MapCalloutView calloutView, int position, Annotation annotation) {
        }
    }

    /**
     * Clients may use this interface to listen to long presses.
     * 
     * @author Cyril Mottier
     */
    public interface OnMapViewLongClickListener {
        void onLongClick(PolarisMapView mapView, GeoPoint geoPoint);
    }

    /**
     * Amount of time to display about 10 frames at 60Hz
     */
    private static final long REGION_CHANGE_CONFIRMED_DELAY = 1000L / 60L * 10L;

    private static final int INDEX_FIRST = 0;
    private static final int INDEX_SECOND = 1;

    private final CoordinateRegion mTempRegion = new CoordinateRegion();
    private final CoordinateRegion mPreviousRegion = new CoordinateRegion();
    private final CoordinateRegion mPreviousRegionConfirmed = new CoordinateRegion();

    private OnAnnotationSelectionChangedListener mOnAnnotationSelectionChangedListener;
    private OnRegionChangedListener mOnRegionChangedListener;
    private OnMapViewLongClickListener mOnMapViewLongClickListener;

    private OverlayContainer mOverlayContainer;
    private PolarisMyLocationOverlay mMyLocationOverlay;
    private AnnotationsOverlay mAnnotationsOverlay;

    private boolean mIsInGesture;

    private boolean mIsUserTrackingButtonEnabled;
    private ImageButton mUserTrackingButton;

    private MapCalloutView mMapCallouts[] = new MapCalloutView[2];
    private int mMapCalloutIndex;

    private Annotation mCurrentLocationAnnotation;
    private String mCurrentLocationTitle;
    private String mCurrentLocationSubtitle;

    /**
     * Create a new {@link PolarisMapView}.
     * 
     * @param context A {@link MapActivity} Context.
     * @param attrs An attribute set.
     */
    public PolarisMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Create a new {@link PolarisMapView}.
     * 
     * @param context A {@link MapActivity} Context.
     * @param attrs An attribute set.
     * @param defStyle The default style to apply to this view
     */
    public PolarisMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * @param context A {@link MapActivity} Context.
     * @param apiKey A Google Maps API Key.
     */
    public PolarisMapView(Context context, String apiKey) {
        super(context, apiKey);
        init();
    }

    @TargetApi(3)
    private void init() {
        setBuiltInZoomControls(!supportsMultiTouchZoom());
        setActionnable(true);

        mOverlayContainer = new OverlayContainer(getContext(), mMagnetoCallback);
        getOverlays().add(mOverlayContainer);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mOnRegionChangedListener != null && testRegionChange()) {
            mOnRegionChangedListener.onRegionChanged(this);
            scheduleRegionChangeConfirmed();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mIsUserTrackingButtonEnabled) {
            final LayoutParams params = (LayoutParams) mUserTrackingButton.getLayoutParams();
            final int spacing = getResources().getDimensionPixelOffset(R.dimen.polaris__spacing_normal);
            params.x = w - spacing;
            params.y = spacing;
            // This will obviously start a new layout pass. However there is no
            // risk to fall in an infinite loop here as this code is only called
            // when the size has changed.
            mUserTrackingButton.requestLayout();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsInGesture = true;
                removeCallbacks(mRegionChangeConfirmedRunnable);
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsInGesture = false;
                scheduleRegionChangeConfirmed();
        }

        return super.onTouchEvent(ev);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        // We need to ensure mUserTrackingButton is always on top. As a result,
        // we simply enforce it is brought to front whenever a new child is
        // added. Note that this method is actually called by all other
        // "addView" variants.
        if (mIsUserTrackingButtonEnabled) {
            super.bringChildToFront(mUserTrackingButton);
        }
    }

    @Override
    public void bringChildToFront(View child) {
        // The code below is part of the trick described in the addView method.
        if (child != mUserTrackingButton) {
            super.bringChildToFront(child);
        }
        if (mIsUserTrackingButtonEnabled) {
            super.bringChildToFront(mUserTrackingButton);
        }
    }

    /**
     * Method that MUST be called in the hosting {@link MapActivity} onStart()
     * method.
     */
    public void onStart() {
        if (mMyLocationOverlay != null) {
            mMyLocationOverlay.enableMyLocation();
        }
    }

    /**
     * Method that MUST be called in the hosting {@link MapActivity} onStop()
     * method.
     */
    public void onStop() {
        if (mMyLocationOverlay != null) {
            mMyLocationOverlay.disableMyLocation();
        }
    }

    /**
     * Add a new {@link Overlay} to the {@link Overlay}'s list. The
     * {@link Overlay} will be added at the end of the list. Hence, it will be
     * drawn on top of all other {@link Overlay}s.
     * 
     * @param overlay The {@link Overlay} to add
     */
    public void addOverlay(Overlay overlay) {
        mOverlayContainer.addOverlay(overlay);
    }

    /**
     * Equivalent to {@link #addOverlay(Overlay)}. It inserts the
     * {@link Overlay} at the given index.
     * 
     * @param index The index at which to insert the {@link Overlay}
     * @param overlay The {@link Overlay} to add
     */
    public void addOverlay(int index, Overlay overlay) {
        mOverlayContainer.addOverlay(index, overlay);
    }

    /**
     * Remove an {@link Overlay} from the {@link Overlay}'s list.
     * 
     * @param overlay The {@link Overlay} to remove
     */
    public void removeOverlay(Overlay overlay) {
        mOverlayContainer.removeOverlay(overlay);
    }

    /**
     * Remove the {@link Overlay} at the given index.
     * 
     * @param index The index of the {@link Overlay} to remove.
     */
    public void removeOverlay(int index) {
        mOverlayContainer.removeOverlay(index);
    }

    /**
     * Remove all overlays from the {@link Overlay}'s list.
     */
    public void removeAllOverlays() {
        mOverlayContainer.removeAllOverlays();
    }

    /**
     * Searches the {@link Overlay}'s list for the specified {@link Overlay} and
     * returns the index of the first occurrence.
     * 
     * @param overlay The {@link Overlay} to search
     * @return The index of the first occurence of the given {@link Overlay} or
     *         -1 if not found
     */
    public int indexOfOverlay(Overlay overlay) {
        return mOverlayContainer.indexOfOverlay(overlay);
    }

    /**
     * Set a new {@link OnRegionChangedListener}.
     * 
     * @param listener The new {@link OnRegionChangedListener}
     */
    public void setOnRegionChangedListenerListener(OnRegionChangedListener listener) {
        mOnRegionChangedListener = listener;
    }

    /**
     * Set a new {@link OnMapViewLongClickListener}.
     * 
     * @param l The new {@link OnMapViewLongClickListener}
     */
    public void setOnMapViewLongClickListener(OnMapViewLongClickListener l) {
        mOnMapViewLongClickListener = l;
    }

    /**
     * Indicate whether user tracking is enabled or not. Having user tracking
     * enabled will automatically add a {@link MyLocationOverlay} and a button
     * on the upper-right corner to center the map back to the user's location.
     * 
     * @return true if the user tracking is enabled else it returns false
     */
    public boolean isUserTrackingButtonEnabled() {
        return mIsUserTrackingButtonEnabled;
    }

    public void setCurrentLocationMarker(String title, String subtitle) {
        mCurrentLocationTitle = title;
        mCurrentLocationSubtitle = subtitle;
    }

    /**
     * Enable/disable user tracking on this {@link PolarisMapView}.
     * 
     * @param enabled True if user tracking is enabled, false otherwise.
     */
    public void setUserTrackingButtonEnabled(boolean enabled) {
        if (mIsUserTrackingButtonEnabled != enabled) {
            mIsUserTrackingButtonEnabled = enabled;
            if (enabled) {
                if (mUserTrackingButton == null) {
                    mUserTrackingButton = (ImageButton) LayoutInflater.from(getContext()).inflate(R.layout.polaris__user_tracking_button,
                            null);
                    mUserTrackingButton.setOnClickListener(mOnUserTrackingButtonClickListener);
                    //@formatter:off
                    mUserTrackingButton.setLayoutParams(new MapView.LayoutParams(
                            LayoutParams.WRAP_CONTENT,                       // width
                            LayoutParams.WRAP_CONTENT,                       // height
                            0,                                               // x
                            0,                                               // y
                            LayoutParams.TOP | MapView.LayoutParams.RIGHT)   // alignment
                    );
                    //@formatter:on
                }
                mMyLocationOverlay = new PolarisMyLocationOverlay(getContext(), this);
                mMyLocationOverlay.enableMyLocation();
                mOverlayContainer.setUserLocationOverlay(mMyLocationOverlay);
                addView(mUserTrackingButton);

                mMyLocationOverlay.setOnCurrentLocationChangedListener(new PolarisMyLocationOverlay.OnCurrentLocationChangedListener() {
                    @Override
                    public void onCurrentLocationChanged(Location location) {
                        final GeoPoint p = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
                        if(mCurrentLocationAnnotation != null)
                            mAnnotationsOverlay.removeAnnotation(mCurrentLocationAnnotation);

                        ColorDrawable blankMarker = new ColorDrawable(Color.TRANSPARENT);
                        mCurrentLocationAnnotation = new Annotation(p, mCurrentLocationTitle, mCurrentLocationSubtitle, blankMarker);
                        if(mAnnotationsOverlay == null) {
                            setAnnotations(Collections.singletonList(mCurrentLocationAnnotation), blankMarker);
                        } else {
                            mAnnotationsOverlay.addAnnotation(mCurrentLocationAnnotation);
                        }
                    }
                });

            } else {
                if (mUserTrackingButton != null) {
                    removeView(mUserTrackingButton);
                }
                mOverlayContainer.setUserLocationOverlay(null);
                mMyLocationOverlay.disableMyLocation();
                mMyLocationOverlay = null;
            }
        }
    }

    /**
     * Indicates whether this {@link PolarisMapView} is actionnable (i.e. if it
     * can be zoomed and panned by the user). More specifically, this method is
     * a replacement for the {@link MapView#isClickable()}.
     * 
     * @return true if this {@link PolarisMapView} is actionnable, false
     *         otherwise
     */
    public boolean isActionnable() {
        return isClickable();
    }

    /**
     * Enables or disables action events for this view. When a
     * {@link PolarisMapView} is actionnable it will be zoomable and pannable.
     * 
     * @param actionnable true to make the view actionnable, false otherwise
     */
    public void setActionnable(boolean actionnable) {
        setClickable(actionnable);
    }

    // /**
    // * @return
    // * @hide
    // */
    // public MyLocationOverlay getMyLocationOverlay() {
    // return mMyLocationOverlay;
    // }
    //
    // /**
    // * @return
    // * @hide
    // */
    // public ItemizedOverlay<?> getAnnotationOverlay() {
    // return mAnnotationsOverlay;
    // }

    /**
     * Set a new {@link OnAnnotationSelectionChangedListener}.
     * 
     * @param listener The new {@link OnAnnotationSelectionChangedListener}
     */
    public void setOnAnnotationSelectionChangedListener(OnAnnotationSelectionChangedListener listener) {
        mOnAnnotationSelectionChangedListener = listener;
    }

    /**
     * Set the annotations to display in this {@link PolarisMapView}.
     * 
     * @param annotations The annotations
     * @param annotationMarkerId The resource identifier to the default marker.
     */
    public void setAnnotations(List<Annotation> annotations, int annotationMarkerId) {
        setAnnotations(annotations, getResources().getDrawable(annotationMarkerId));
    }

    /**
     * Set the annotations to display in this {@link PolarisMapView}.
     * 
     * @param annotations The annotations
     * @param annotationMarker The default marker
     */
    public void setAnnotations(List<Annotation> annotations, Drawable annotationMarker) {
        if (annotations == null) {
            mOverlayContainer.setAnnotationsOverlay(null);
        } else {
            mAnnotationsOverlay = new AnnotationsOverlay(mMystiqueCallback, new ArrayList<Annotation>(annotations), annotationMarker);
            if(mCurrentLocationAnnotation != null)
                mAnnotationsOverlay.addAnnotation(mCurrentLocationAnnotation);

            mOverlayContainer.setAnnotationsOverlay(mAnnotationsOverlay);
        }
        // Reflect the changes in the MapView
        invalidate();
    }

    /**
     * Returns the position of the selected annotation or
     * {@value #INVALID_POSITION} if no annotation is currently selected.
     * 
     * @return the position of the selected annotation or
     *         {@value #INVALID_POSITION}.
     */
    public int getSelectedAnnotationPosition() {
        if (mAnnotationsOverlay != null) {
            return mAnnotationsOverlay.getSelectedAnnotation();
        }
        return INVALID_POSITION;
    }

    /**
     * Returns the selected annotation.
     * 
     * @return The selected annotation
     */
    public Annotation getSelectedAnnotation() {
        if (mAnnotationsOverlay != null) {
            return mAnnotationsOverlay.getAnnotation(mAnnotationsOverlay.getSelectedAnnotation());
        }
        return null;
    }

    /**
     * Set the selected annotation.
     * 
     * @param position The position of the annotation to select
     */
    public void setSelectedAnnotation(int position) {
        if (mAnnotationsOverlay != null) {
            mAnnotationsOverlay.setSelectedAnnotation(position);
        }
    }

    /**
     * Return the visible region of your map. Fills in the output region with
     * the values from the center coordinates and the spans.
     * 
     * @param region The visible region of the map
     */
    public void getCoordinateRegion(CoordinateRegion region) {
        //@formatter:off
        region.set(
                getMapCenter().getLatitudeE6(),
                getMapCenter().getLongitudeE6(),
                getLatitudeSpan(),
                getLongitudeSpan());
        //@formatter:on
    }

    /**
     * Determine whether multi-touch is fully enabled on the current device. The
     * hint returned by this method can be used to decide whether to enable the
     * built-in zoom controls or not.
     * 
     * @return True is the device supports distinct multi-touch gestures.
     *         Otherwise it return false.
     */
    private boolean supportsMultiTouchZoom() {
        final PackageManager pm = getContext().getPackageManager();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            // Prior Froyo we don't even have the FEATURE_TOUCHSCREEN feature!
            // As a result let's suppose multitouch is not enabled.
            return false;
        } else {
            if (pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH_DISTINCT)
                    || pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH_JAZZHAND)) {
                // Distinct and/or jazzhand multitouch is enabled. It means
                // MapView can be (un)zoomed via multitouch gestures and no zoom
                // controls are required
                return true;
            }
        }
        return false;
    }

    private void scheduleRegionChangeConfirmed() {
        if (!mIsInGesture) {
            removeCallbacks(mRegionChangeConfirmedRunnable);
            postDelayed(mRegionChangeConfirmedRunnable, REGION_CHANGE_CONFIRMED_DELAY);
        }
    }

    private boolean testRegionChange() {
        return testRegionChange(mPreviousRegion);
    }

    private boolean testRegionChangeConfirmed() {
        return testRegionChange(mPreviousRegionConfirmed);
    }

    private boolean testRegionChange(CoordinateRegion region) {
        getCoordinateRegion(mTempRegion);
        boolean result = !mTempRegion.equals(region);
        region.set(mTempRegion);
        return result;
    }

    private MapCalloutView getMapCallout(int index) {
        if (mMapCallouts[index] == null) {
            mMapCallouts[index] = new MapCalloutView(getContext());
            mMapCallouts[index].setVisibility(View.GONE);
            mMapCallouts[index].setOnClickListener(mOnClickListener);
            mMapCallouts[index].setOnDoubleTapListener(mOnDoubleTapListener);
        }
        return mMapCallouts[index];
    }

    private MapCalloutView getCurrentMapCallout() {
        return getMapCallout(mMapCalloutIndex);
    }

    private MapCalloutView getNextMapCallout() {
        if (mMapCalloutIndex == INDEX_FIRST) {
            mMapCalloutIndex = INDEX_SECOND;
        } else {
            mMapCalloutIndex = INDEX_FIRST;
        }
        return getMapCallout(mMapCalloutIndex);
    }

    private final Runnable mRegionChangeConfirmedRunnable = new Runnable() {
        public void run() {
            if (mOnRegionChangedListener != null && testRegionChangeConfirmed()) {
                mOnRegionChangedListener.onRegionChangeConfirmed(PolarisMapView.this);
            }
        }
    };

    private final MystiqueCallback mMystiqueCallback = new MystiqueCallback() {
        @Override
        public void dismissCallout(int position) {
            final Annotation annotation = mAnnotationsOverlay.getAnnotation(position);
            if (annotation == null) {
                return;
            }
            final MapCalloutView mapCalloutView = getCurrentMapCallout();
            if (mapCalloutView != null && mapCalloutView.getVisibility() == View.VISIBLE) {
                mapCalloutView.dismiss(true);
                if (mOnAnnotationSelectionChangedListener != null) {
                    //@formatter:off
                    mOnAnnotationSelectionChangedListener.onAnnotationDeselected(
                            PolarisMapView.this,
                            mapCalloutView,
                            position,
                            annotation);
                    //@formatter:on
                }
            }
        }

        @Override
        public void showCallout(int position) {
            final Annotation annotation = mAnnotationsOverlay.getAnnotation(position);
            if (annotation == null) {
                return;
            }

            dismissCallout(position);

            final MapCalloutView mapCalloutView = getNextMapCallout();
            mapCalloutView.setData(annotation);

            int markerHeight = mAnnotationsOverlay.getDefaultMarker().getBounds().height();
            final Drawable marker = annotation.getMarker();
            if (marker != null) {
                markerHeight = marker.getBounds().height();
            }
            mapCalloutView.setMarkerHeight(markerHeight);

            if (mOnAnnotationSelectionChangedListener != null) {
                //@formatter:off
                mOnAnnotationSelectionChangedListener.onAnnotationSelected(
                        PolarisMapView.this,
                        mapCalloutView,
                        position,
                        annotation);
                //@formatter:on
            }

            if (mapCalloutView.hasDisplayableContent()) {
                mapCalloutView.show(PolarisMapView.this, annotation.getPoint(), true);
            }

        }
    };

    private final MagnetoCallback mMagnetoCallback = new MagnetoCallback() {
        @Override
        public void onSinpleTap(MotionEvent e) {
            setSelectedAnnotation(INVALID_POSITION);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (mOnMapViewLongClickListener != null) {
                mOnMapViewLongClickListener.onLongClick(PolarisMapView.this, getProjection().fromPixels((int) e.getX(), (int) e.getY()));
            }
        }

        @Override
        public void onDoubleTap(MotionEvent e) {
            getController().zoomInFixing((int) e.getX(), (int) e.getY());
        }
    };

    private final OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnAnnotationSelectionChangedListener != null) {
                final int annotationPosition = getSelectedAnnotationPosition();
                final Annotation annotation = getSelectedAnnotation();
                if (annotation != null && annotationPosition != INVALID_POSITION) {
                    //@formatter:off
                    mOnAnnotationSelectionChangedListener.onAnnotationClicked(
                            PolarisMapView.this,
                            (MapCalloutView) v,
                            annotationPosition,
                            annotation);
                    //@formatter:on
                }
            }
        }
    };

    private final OnDoubleTapListener mOnDoubleTapListener = new OnDoubleTapListener() {
        @Override
        public void onDoubleTap(View v) {
            final Annotation annotation = getSelectedAnnotation();
            if (annotation != null) {
                getController().zoomToSpan(1, 1);
                getController().setCenter(annotation.getPoint());
            }
        }
    };

    private final OnClickListener mOnUserTrackingButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            MapViewUtils.smoothCenterOnUserLocation(PolarisMapView.this, mMyLocationOverlay, R.string.polaris__unable_to_locate_you);
        }
    };

}
