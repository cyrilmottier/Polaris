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

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * @author Cyril Mottier
 */
public class MapCalloutView extends ViewGroup {

    /**
     * @author Cyril Mottier
     */
    public interface OnDoubleTapListener {
        /**
         * Called when a view has been double tapped.
         * 
         * @param v The view that was double tapped.
         */
        void onDoubleTap(View v);
    }

    public static final int ANCHOR_MODE_FIXED = 1;
    public static final int ANCHOR_MODE_VARIABLE = 2;

    private final Point mTempPoint = new Point();
    private final Rect mTempRect1 = new Rect();
    private final Rect mTempRect2 = new Rect();

    private final GestureListener mGestureListener = new GestureListener();

    private LinearLayout mCallout;

    private TextView mTitle;
    private TextView mSubtitle;
    private View mDisclosure;
    private FrameLayout mContentContainer;
    private View mContent;

    private View mLeftAccessory;
    private View mRightAccessory;
    private View mCustomView;

    private boolean mIsDisclosureEnabled;
    private GestureDetector mGestureDetector;
    private OnClickListener mOnClickListener;
    private OnDoubleTapListener mOnDoubleTapListener;

    private int mInset;
    private int mSpacing;
    private int mMarkerHeight;
    private int mAnchorMode = ANCHOR_MODE_VARIABLE;

    private boolean mNeedRelayout;
    private MapCalloutDrawable mMapCalloutDrawable;

    public MapCalloutView(Context context) {
        super(context);
        init(context);
    }

    public MapCalloutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MapCalloutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @SuppressWarnings("deprecation")
    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.polaris__map_callout_view_merge, this);

        mInset = getResources().getDimensionPixelSize(R.dimen.polaris__spacing_large);
        mSpacing = getResources().getDimensionPixelSize(R.dimen.polaris__spacing_normal);

        mGestureDetector = new GestureDetector(getContext(), mGestureListener);
        mGestureDetector.setOnDoubleTapListener(mGestureListener);

        mMapCalloutDrawable = new MapCalloutDrawable(context);

        mCallout = (LinearLayout) findViewById(R.id.polaris__callout);
        mCallout.setOnTouchListener(mOnTouchListener);
        mCallout.setBackgroundDrawable(mMapCalloutDrawable);

        mTitle = (TextView) findViewById(R.id.polaris__title);
        mSubtitle = (TextView) findViewById(R.id.polaris__subtitle);
        mDisclosure = (ImageView) findViewById(R.id.polaris__disclosure);
        mContentContainer = (FrameLayout) findViewById(R.id.polaris__content_container);
        mContent = findViewById(R.id.polaris__content);
    }

    @Override
    public void setBackground(Drawable background) {
        oopsBackgroundModified();
    }

    @Override
    public void setBackgroundColor(int color) {
        oopsBackgroundModified();
    }

    @Override
    public void setBackgroundResource(int resid) {
        oopsBackgroundModified();
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        oopsBackgroundModified();
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        if (!isClickable()) {
            setClickable(true);
        }
        mOnClickListener = l;
    }

    @Override
    public boolean isClickable() {
        return mCallout.isClickable();
    }

    @Override
    public void setClickable(boolean clickable) {
        mCallout.setClickable(clickable);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int extraPadding = mInset;
        final int widthCoeff = mAnchorMode == ANCHOR_MODE_VARIABLE ? 2 : 1;
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec) - 2 * extraPadding;
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec) - 2 * extraPadding;

        mCallout.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.getMode(widthMeasureSpec)),
                MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.getMode(heightMeasureSpec)));

        setMeasuredDimension(mCallout.getMeasuredWidth() * widthCoeff, mCallout.getMeasuredHeight() + mMarkerHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        switch (mAnchorMode) {
            case ANCHOR_MODE_VARIABLE:
                layoutVariable(changed, l, t, r, b);
                break;

            case ANCHOR_MODE_FIXED:
            default:
                layoutFixed(changed, l, t, r, b);
                break;
        }
    }

    private void layoutFixed(boolean changed, int l, int t, int r, int b) {
        mCallout.layout(0, 0, mCallout.getMeasuredWidth(), mCallout.getMeasuredHeight());
    }

    private void layoutVariable(boolean changed, int l, int t, int r, int b) {
        if (mNeedRelayout) {
            if (!(getParent() instanceof MapView)) {
                throw new IllegalStateException(MapCalloutView.class.getSimpleName() + " can only be used in MapView");
            }

            final MapView mapView = (MapView) getParent();
            final MapCalloutDrawable drawable = mMapCalloutDrawable;

            final Rect mapViewDrawingRect = mTempRect1;
            mapView.getDrawingRect(mapViewDrawingRect);

            final Rect selfDrawingRect = mTempRect2;
            getDrawingRect(selfDrawingRect);
            mapView.offsetDescendantRectToMyCoords(this, selfDrawingRect);

            int anchorX = selfDrawingRect.centerX();

            int calloutX = (int) ((mapViewDrawingRect.right - mapViewDrawingRect.left - mCallout.getMeasuredWidth()) / 2.0f + 0.5f);

            // What's the farthest to the left and right that we could point to,
            // given our background image constraints?
            int minX = calloutX + drawable.getLeftMargin();
            int maxX = calloutX + mCallout.getMeasuredWidth() - drawable.getRightMargin();

            // we may need to scoot over to the left or right to point at the
            // correct spot
            int adjustX = 0;
            if (anchorX < minX) {
                adjustX = anchorX - minX;
            }
            if (anchorX > maxX) {
                adjustX = anchorX - maxX;
            }

            calloutX = calloutX + adjustX;

            //@formatter:off
            selfDrawingRect.set(
                    calloutX,
                    t,
                    calloutX + mCallout.getMeasuredWidth(),
                    t + mCallout.getMeasuredHeight());
            //@formatter:on

            mapViewDrawingRect.inset(mInset, mInset);
            offsetToContainRect(selfDrawingRect, mapViewDrawingRect, mTempPoint);

            selfDrawingRect.offset(-l, -t);

            drawable.setAnchorOffset(anchorX - calloutX);

            //@formatter:off
            mCallout.layout(
                    selfDrawingRect.left, 
                    selfDrawingRect.top,
                    selfDrawingRect.right,
                    selfDrawingRect.bottom);
            //@formatter:on           

            if (mTempPoint.x != 0 || mTempPoint.y != 0) {
                MapViewUtils.smoothScrollBy(mapView, mTempPoint.x, mTempPoint.y);
            }

            mNeedRelayout = false;
        } else {
            mCallout.layout(mTempRect2.left, mTempRect2.top, mTempRect2.right, mTempRect2.bottom);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mNeedRelayout = true;
    }

    public void show(MapView mapView, GeoPoint point, boolean animated) {
        final int index = mapView.indexOfChild(this);
        if (index == -1) {
            mapView.addView(this, new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, point,
                    MapView.LayoutParams.BOTTOM_CENTER));
        }

        bringToFront();

        final MapView.LayoutParams params = (MapView.LayoutParams) getLayoutParams();
        params.width = LayoutParams.WRAP_CONTENT;
        params.height = LayoutParams.WRAP_CONTENT;
        params.mode = MapView.LayoutParams.MODE_MAP;
        params.point = point;

        mNeedRelayout = true;
        if (animated) {
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.polaris__grow_fade_in_from_bottom);
            startAnimation(animation);
        }

        setVisibility(View.VISIBLE);
    }

    public void dismiss(boolean animated) {
        setVisibility(View.GONE);
        if (animated) {
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.polaris__shrink_fade_out_to_bottom);
            startAnimation(animation);
        }
    }

    /**
     * Set a new listener to listen to double tap events.
     * 
     * @param l The listener to set
     */
    public void setOnDoubleTapListener(OnDoubleTapListener l) {
        mOnDoubleTapListener = l;
    }

    /**
     * Set the title of the {@link MapViewCallout}. The {@link MapViewCallout}
     * automatically manages empty (null or zero-length) title.
     * 
     * @param title The title to apply to this {@link MapViewCallout}
     */
    public void setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
            mTitle.setVisibility(View.VISIBLE);
        } else {
            mTitle.setVisibility(View.GONE);
        }
    }

    /**
     * Set the subtitle of the {@link MapViewCallout}. The
     * {@link MapViewCallout} automatically manages empty (null or zero-length)
     * subtitle.
     * 
     * @param subtitle The subtitle to apply to this {@link MapViewCallout}
     */
    public void setSubtitle(CharSequence subtitle) {
        if (!TextUtils.isEmpty(subtitle)) {
            mSubtitle.setText(subtitle);
            mSubtitle.setVisibility(View.VISIBLE);
        } else {
            mSubtitle.setVisibility(View.GONE);
        }
    }

    /**
     * Sets the view's data from a given overlay item. Overlay's title and
     * snippet are respectively used as the new title and subtitle.
     * 
     * @param item - The overlay item containing the relevant view's data (title
     *            and snippet).
     */
    public void setData(OverlayItem item) {
        setTitle(item == null ? null : item.getTitle());
        setSubtitle(item == null ? null : item.getSnippet());
    }

    /**
     * Indicates whether the disclosure indicator is enabled or not. Please note
     * an enabled disclosure indicator doesn't mean it is visible. Indeed, it
     * may be enabled but invisible if a non-null right accessory view has been
     * set.
     * 
     * @return true is the disclosure indicator is enabled. false otherwise
     * @see #setDisclosureEnabled(boolean)
     */
    public boolean isDisclosureEnabled() {
        return mIsDisclosureEnabled;
    }

    /**
     * Enable or disable the disclosure indicator. Put simple, the disclosure
     * indicator should always be visible when the {@link MapViewCallout} is
     * clickable i.e. when an action such as "opening" a details screen is done
     * on click.
     * 
     * @param enabled Whether the disclosure indicator is enabled or not
     */
    public void setDisclosureEnabled(boolean enabled) {
        if (mIsDisclosureEnabled != enabled) {
            mIsDisclosureEnabled = enabled;
            if (enabled && mRightAccessory == null) {
                mDisclosure.setVisibility(View.VISIBLE);
            } else {
                mDisclosure.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Return the right accessory view.
     * 
     * @return The right accessory view.
     */
    public View getRightAccessoryView() {
        return mRightAccessory;
    }

    /**
     * Set a new right accessory view to this {@link MapViewCallout}. The newly
     * added view will remove the previous one. Setting
     * {@link android.view.ViewGroup.LayoutParams} to the given right accessory
     * is not necessary as they will be automatically set to (
     * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT},
     * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}). Please note it
     * is not possible to use the right accessory in addition to the disclosure
     * indicator. A non-null right accessory automatically replaces the
     * disclosure indicator.
     * 
     * @param rightAccessoryView The new right accessory view.
     */
    public void setRightAccessoryView(View rightAccessoryView) {
        if (rightAccessoryView != null && rightAccessoryView.getParent() != null) {
            throw new IllegalArgumentException("The given view is already attached to a parent");
        }

        if (mRightAccessory != rightAccessoryView) {
            // Remove the previous custom view
            if (mRightAccessory != null) {
                mCallout.removeView(mRightAccessory);
            }

            mRightAccessory = rightAccessoryView;

            // Add the new custom view
            if (rightAccessoryView != null) {
                mDisclosure.setVisibility(View.GONE);

                final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER_VERTICAL;
                params.weight = 0;
                params.leftMargin = mSpacing;

                mCallout.addView(rightAccessoryView, params);
            } else {
                if (mIsDisclosureEnabled) {
                    mDisclosure.setVisibility(View.VISIBLE);
                } else {
                    mDisclosure.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * Return the left accessory view.
     * 
     * @return The left accessory view.
     */
    public View getLeftAccessoryView() {
        return mLeftAccessory;
    }

    /**
     * Set a new left accessory view to this {@link MapViewCallout}. The newly
     * added view will remove the previous one. Setting
     * {@link android.view.ViewGroup.LayoutParams} to the given left accessory
     * is not necessary as they will be automatically set to (
     * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT},
     * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}).
     * 
     * @param rightAccessoryView The new left accessory view.
     */
    public void setLeftAccessoryView(View leftAccessoryView) {
        if (leftAccessoryView != null && leftAccessoryView.getParent() != null) {
            throw new IllegalArgumentException("The given view is already attached to a parent");
        }

        if (mLeftAccessory != leftAccessoryView) {
            // Remove the previous custom view
            if (mLeftAccessory != null) {
                mCallout.removeView(mLeftAccessory);
            }

            mLeftAccessory = leftAccessoryView;

            // Add the new custom view
            if (leftAccessoryView != null) {

                final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER_VERTICAL;
                params.weight = 0;
                params.rightMargin = mSpacing;

                mCallout.addView(leftAccessoryView, 0, params);
            }
        }
    }

    /**
     * Return the custom view.
     * 
     * @return The custom view or null if no custom view has been set
     */
    public View getCustomView() {
        return mCustomView;
    }

    /**
     * Set a new custom view to this {@link MapViewCallout}. The newly added
     * view will remove the previous one. Custom views are usually used to
     * completely manage the content of the {@link MapViewCallout}. Setting
     * {@link android.view.ViewGroup.LayoutParams} to the given left accessory
     * is not necessary as they will be automatically set to (
     * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT},
     * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}).
     * 
     * @param rightAccessoryView The new left accessory view.
     */
    public void setCustomView(View customView) {
        if (customView != null && customView.getParent() != null) {
            throw new IllegalArgumentException("The given view is already attached to a parent");
        }

        if (mCustomView != customView) {
            // Remove the previous custom view
            if (mCustomView != null) {
                mContentContainer.removeView(mCustomView);
            }

            mCustomView = customView;

            // Add the new custom view
            if (customView != null) {
                mContent.setVisibility(View.GONE);
                mContentContainer.addView(customView, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            } else {
                mContent.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Indicates whether this {@link MapViewCallout} has some displayable
     * content. The result of this method is used as a hint to know whether or
     * not the callout should be displayed once an {@link Annotation} has been
     * applied to it.
     * 
     * @return true if this callout has some displayable content, false
     *         otherwise.
     */
    public boolean hasDisplayableContent() {
        if (mCustomView != null) {
            return true;
        }
        if (mLeftAccessory != null || mRightAccessory != null) {
            return true;
        }
        if (mTitle != null && mTitle.getVisibility() == View.VISIBLE) {
            return true;
        }
        if (mSubtitle != null && mSubtitle.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }

    /**
     * Defines the height of the marker i.e. the dimension in pixels the callout
     * will be offset from the bottom.
     * 
     * @return
     */
    public int getMarkerHeight() {
        return mMarkerHeight;
    }

    /**
     * Defines the height of the marker i.e. the dimension in pixels the callout
     * will be offset from the bottom.
     * 
     * @return The height (in pixels) of the pin.
     */
    public void setMarkerHeight(int markerHeight) {
        if (markerHeight < 0) {
            markerHeight = 0;
        }
        if (mMarkerHeight != markerHeight) {
            mMarkerHeight = markerHeight;
            requestLayout();
            invalidate();
        }
    }

    public int getAnchorMode() {
        return mAnchorMode;
    }

    public void setAnchorMode(int anchorMode) {
        switch (anchorMode) {
            case ANCHOR_MODE_FIXED:
            case ANCHOR_MODE_VARIABLE:
                break;
            default:
                anchorMode = ANCHOR_MODE_FIXED;
                break;
        }
        if (mAnchorMode != anchorMode) {
            mAnchorMode = anchorMode;
            switch (anchorMode) {
                case ANCHOR_MODE_VARIABLE:
                    mNeedRelayout = true;
                    break;
                case ANCHOR_MODE_FIXED:
                default:
                    mMapCalloutDrawable.setAnchorOffset(MapCalloutDrawable.ANCHOR_POSITION_CENTER);
                    break;
            }
            requestLayout();
            invalidate();
        }
    }

    private void offsetToContainRect(Rect innerRect, Rect outerRect, Point outPoint) {

        final int offsetLeft = Math.min(0, innerRect.left - outerRect.left);
        final int offsetTop = Math.min(0, innerRect.top - outerRect.top);
        final int offsetRight = Math.max(0, innerRect.right - outerRect.right);
        final int offsetBottom = Math.max(0, innerRect.bottom - outerRect.bottom);

        //@formatter:off
        outPoint.set(
                offsetLeft != 0 ? offsetLeft : offsetRight,
                offsetTop != 0 ? offsetTop : offsetBottom);
        //@formatter:on
    }

    private void oopsBackgroundModified() {
        throw new UnsupportedOperationException("The background of a " + MapCalloutView.class.getSimpleName() + " cannot be changed");
    }

    private final OnTouchListener mOnTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // We want to consume MotionEvent even if the view is not clickable
            // to prevent the user clicking on hidden widgets.
            if (!isClickable()) {
                return true;
            }

            if (mGestureDetector.onTouchEvent(event)) {
                return true;
            }

            // HACK Cyril: GestureDetector never callbacks onSingleTapConfirmed
            // when the click is done in done after a long press (which we don't
            // want to handle).
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mGestureListener.onUp(event);
            }

            return false;
        }
    };

    private class GestureListener extends SimpleOnGestureListener {

        private final Rect mRect = new Rect();
        private boolean mHasLongPressed;

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(MapCalloutView.this);
            }
            return false;
        }

        public void onUp(MotionEvent e) {
            getDrawingRect(mRect);
            if (mRect.contains((int) e.getX(), (int) e.getY()) && mHasLongPressed) {
                onSingleTapConfirmed(e);
            }
            mHasLongPressed = false;
        }

        public void onLongPress(MotionEvent e) {
            mHasLongPressed = true;
        };

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mOnDoubleTapListener != null) {
                mOnDoubleTapListener.onDoubleTap(MapCalloutView.this);
            }
            return true;
        }
    };
}
