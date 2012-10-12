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
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * A Drawable representing a callout on MapView. Contrary to a basic 9-patch,
 * {@link MapCalloutDrawable} can change the position of the anchor in a
 * variable way.
 * 
 * @author Cyril Mottier
 */
public class MapCalloutDrawable extends Drawable {
    
    //@formatter:off
    // 
    // Here is a brief draft of what a MapCalloutDrawable looks like and
    // what are the purpose of all of its dimensions.
    //
    //     -----------
    //   /             \
    //  |               |
    //  |               |
    //   \             /
    //     -----  ----
    //          \/       
    //  <> : left margin
    //  right margin : <>
    //
    //  <-------> : anchor offset
    //
    // @formatter:on

    /**
     * Special anchor offset that can be used to center the anchor in the middle
     * of this {@link Drawable}'s bounds.
     */
    public static final int ANCHOR_POSITION_CENTER = -2;

    private final Rect mTempRect = new Rect();

    private boolean mMutated;

    private Drawable mLeftCapDrawable;
    private Drawable mBottomAnchorDrawable;
    private Drawable mRightCapDrawable;

    private int mAnchorOffset = ANCHOR_POSITION_CENTER;

    private int mLeftMargin;
    private int mRightMargin;
    private boolean mNeedBoundsUpdate;

    /**
     * Create a new {@link MapCalloutDrawable} whose anchor is placed in the
     * middle just like a regular 9-patch would do.
     * 
     * @param context The Context in which this {@link MapCalloutDrawable} will
     *            be used.
     */
    public MapCalloutDrawable(Context context) {
        final Resources res = context.getResources();

        mLeftCapDrawable = res.getDrawable(R.drawable.polaris__map_callout_left_cap);
        mBottomAnchorDrawable = res.getDrawable(R.drawable.polaris__map_callout_bottom_anchor);
        mRightCapDrawable = res.getDrawable(R.drawable.polaris__map_callout_right_cap);

        final int halfAnchor = (int) (mBottomAnchorDrawable.getIntrinsicWidth() / 2.0f + 0.5f);
        mLeftMargin = mLeftCapDrawable.getIntrinsicWidth() + halfAnchor;
        mRightMargin = mRightCapDrawable.getIntrinsicWidth() + halfAnchor;
    }

    @Override
    public void draw(Canvas canvas) {
        if (mNeedBoundsUpdate) {
            updateBounds();
            mNeedBoundsUpdate = false;
        }
        mLeftCapDrawable.draw(canvas);
        mBottomAnchorDrawable.draw(canvas);
        mRightCapDrawable.draw(canvas);
    }

    @Override
    public void setAlpha(int alpha) {
        mLeftCapDrawable.setAlpha(alpha);
        mBottomAnchorDrawable.setAlpha(alpha);
        mRightCapDrawable.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mLeftCapDrawable.setColorFilter(cf);
        mBottomAnchorDrawable.setColorFilter(cf);
        mRightCapDrawable.setColorFilter(cf);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return Drawable.resolveOpacity(mLeftCapDrawable.getOpacity(),
                Drawable.resolveOpacity(mBottomAnchorDrawable.getOpacity(), mRightCapDrawable.getOpacity()));
    }

    @Override
    public Drawable mutate() {
        if (!mMutated && super.mutate() == this) {
            mLeftCapDrawable.mutate();
            mBottomAnchorDrawable.mutate();
            mRightCapDrawable.mutate();
            mMutated = true;
        }
        return this;
    }

    @Override
    public boolean getPadding(Rect padding) {
        // The padding for this Drawable is a composition of some of the
        // paddings of the underlying Drawables.
        final Rect rect = mTempRect;

        mLeftCapDrawable.getPadding(rect);
        int pLeft = rect.left;

        mBottomAnchorDrawable.getPadding(rect);
        int pTop = rect.top;
        int pBottom = rect.bottom;

        mRightCapDrawable.getPadding(rect);
        int pRight = rect.right;

        padding.set(pLeft, pTop, pRight, pBottom);

        return pLeft != 0 && pTop != 0 && pRight != 0 && pBottom != 0;
    }

    @Override
    public boolean isStateful() {
        return mLeftCapDrawable.isStateful() || mBottomAnchorDrawable.isStateful() || mRightCapDrawable.isStateful();
    }

    @Override
    protected boolean onStateChange(int[] state) {
        boolean result = false;
        result |= mLeftCapDrawable.setState(state);
        result |= mBottomAnchorDrawable.setState(state);
        result |= mRightCapDrawable.setState(state);
        if (result) {
            // It's weird but we need to invalidate on our own. The framework is
            // not doing it by itself depending on the returned value here.
            invalidateSelf();
        }
        return result;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mNeedBoundsUpdate = true;
    }

    @Override
    public int getIntrinsicHeight() {
        return Math.max(mLeftCapDrawable.getIntrinsicWidth(),
                Math.max(mBottomAnchorDrawable.getIntrinsicHeight(), mRightCapDrawable.getIntrinsicHeight()));
    }

    @Override
    public int getIntrinsicWidth() {
        //@formatter:off
        return mLeftCapDrawable.getIntrinsicWidth() 
                + mBottomAnchorDrawable.getIntrinsicWidth() 
                + mRightCapDrawable.getIntrinsicWidth();
        //@formatter:on
    }

    @Override
    public int getChangingConfigurations() {
        //@formatter:off
        return super.getChangingConfigurations()
                | mLeftCapDrawable.getChangingConfigurations()
                | mBottomAnchorDrawable.getChangingConfigurations()
                | mRightCapDrawable.getChangingConfigurations();
        //@formatter:on
    }

    private void updateBounds() {

        // The anchor offset may be out of range. Let's clamp it to the
        // authorized set of values.
        final int anchorOffset;
        switch (mAnchorOffset) {
            case ANCHOR_POSITION_CENTER:
                anchorOffset = getBounds().width() / 2;
                break;

            default:
                anchorOffset = Math.min(Math.max(mLeftMargin, mAnchorOffset), getBounds().width() - mRightMargin);
                break;
        }

        final Rect selfBounds = getBounds();
        final int leftCapWidth = anchorOffset - mBottomAnchorDrawable.getIntrinsicWidth() / 2;

        //@formatter:off
        mLeftCapDrawable.setBounds(
                selfBounds.left,
                selfBounds.top,
                selfBounds.left + leftCapWidth,
                selfBounds.bottom);
        
        mBottomAnchorDrawable.setBounds(
                selfBounds.left + leftCapWidth,
                selfBounds.top,
                selfBounds.left + leftCapWidth + mBottomAnchorDrawable.getIntrinsicWidth(),
                selfBounds.bottom);
        
        mRightCapDrawable.setBounds(
                selfBounds.left + leftCapWidth + mBottomAnchorDrawable.getIntrinsicWidth(),
                selfBounds.top,
                selfBounds.right,
                selfBounds.bottom);
        //@formatter:on
    }

    /**
     * Return the left margin for this {@link MapCalloutDrawable} in pixels. The
     * left margin defines the minimum value of the anchor offset.
     * 
     * @return The left margin
     */
    public int getLeftMargin() {
        return mLeftMargin;
    }

    /**
     * Return the right margin for this {@link MapCalloutDrawable} in pixels.
     * The right margin defines can be used to compute the maximum value of the
     * anchor offset (getBounds().width() - getRightMargin()).
     * 
     * @return The right margin
     */
    public int getRightMargin() {
        return mRightMargin;
    }

    /**
     * Set the anchor offset in pixels i.e. the horizontal distance between the
     * left of this {@link Drawable} and the middle of the arrow.
     * 
     * @param offset The new anchor offset to set
     */
    public void setAnchorOffset(int offset) {
        mAnchorOffset = offset;
        mNeedBoundsUpdate = true;
        invalidateSelf();
    }

}
