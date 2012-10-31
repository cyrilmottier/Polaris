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

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Projection;

/**
 * Some utility methods adding capabilities to regular {@link MapView}s.
 * 
 * @author Cyril Mottier
 */
public final class MapViewUtils {

    private static final Point TEMP_POINT = new Point();

    private MapViewUtils() {
    }

    /**
     * Animated equivalent to {@link MapController#scrollBy(int, int)}. Scroll
     * by a given amount using the default animation, in pixels.
     * <p>
     * <strong>Limitations</strong>: This method internally uses
     * {@link MapController#animateTo(com.google.android.maps.GeoPoint)} which
     * doesn't animate anything if the point is really far (in pixels) from the
     * current point. In this case, nothing will be animated at all.
     * 
     * @param mapView The {@link MapView} to scroll
     * @param dx The horizontal scroll amount in pixels.
     * @param dy The vertical scroll amount in pixels.
     */
    public static void smoothScrollBy(MapView mapView, int dx, int dy) {
        final Projection projection = mapView.getProjection();
        final Point tmpPoint = TEMP_POINT;
        projection.toPixels(mapView.getMapCenter(), tmpPoint);
        tmpPoint.offset(dx, dy);
        mapView.getController().animateTo(projection.fromPixels(tmpPoint.x, tmpPoint.y));
    }

    /**
     * Smoothly animates the {@link MapView} so that it centers on the user
     * location. In case, no location is currently available, the error message
     * is displayed to the user via a regular Toast.
     * 
     * @param mapView The {@link MapView} to animate
     * @param myLocationOverlay The {@link MyLocationOverlay} whose location
     *            will be used to determine the user location.
     * @param errorMessageId The resource identifier of the message to display
     *            in case no location is available.
     */
    public static void smoothCenterOnUserLocation(MapView mapView, MyLocationOverlay myLocationOverlay, int errorMessageId) {
        smoothCenterOnUserLocation(mapView, myLocationOverlay, mapView.getContext().getString(errorMessageId));
    }

    /**
     * Smoothly animates the {@link MapView} so that it centers on the user
     * location. In case, no location is currently available, the error message
     * is displayed to the user via a regular Toast.
     * 
     * @param mapView The {@link MapView} to animate
     * @param myLocationOverlay The {@link MyLocationOverlay} whose location
     *            will be used to determine the user location.
     * @param errorMessage The message to display in case no location is
     *            available.
     */
    public static void smoothCenterOnUserLocation(MapView mapView, MyLocationOverlay myLocationOverlay, String errorMessage) {
        if (myLocationOverlay == null) {
            return;
        }

        final GeoPoint myLocation = myLocationOverlay.getMyLocation();
        if (myLocation != null) {
            // TODO Cyril: Find a way to stop all animations
            // prior animating to the given location otherwise the call
            // to animateTo is no-op. None of the methods I've tried can
            // stop the fling animation :s
            // getController().stopPanning();
            mapView.getController().animateTo(myLocation);
        } else {
            Toast.makeText(mapView.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Adjusts a drawable's bounds so that (0,0) is the center center of the
     * drawable. Useful for "waypoint badge"-like graphics. For convenience,
     * returns the same drawable that was passed in.
     * 
     * @param marker The Drawable to adjust
     * @return The Drawable that was passed in with bounds correctly set
     */
    public static Drawable boundMarkerCenter(Drawable marker) {
        return boundMarker(marker, Gravity.CENTER);
    }

    /**
     * Adjusts a drawable's bounds so that (0,0) is a pixel in the center of the
     * bottom row of the drawable. Useful for "pin"-like graphics. For
     * convenience, returns the same drawable that was passed in.
     * 
     * @param marker The Drawable to adjust
     * @return The Drawable that was passed in with bounds correctly set
     */
    public static Drawable boundMarkerCenterBottom(Drawable marker) {
        return boundMarker(marker, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
    }

    /**
     * Adjusts a drawable's bounds so that (0,0) is a pixel set according to the
     * given gravity.
     * 
     * @param marker The Drawable to adjust
     * @return The Drawable that was passed in with bounds correctly set
     */
    public static Drawable boundMarker(Drawable marker, int gravity) {

        if (marker == null) {
            return null;
        }

        final int width = marker.getIntrinsicWidth();
        final int height = marker.getIntrinsicHeight();

        if (width < 0 || height < 0) {
            throw new IllegalStateException("The given Drawable has no intrinsic width or height");
        }

        int left, top;

        switch (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.LEFT:
                left = 0;
                break;
            case Gravity.RIGHT:
                left = -width;
                break;
            case Gravity.CENTER_HORIZONTAL:
            default:
                left = -width / 2;
                break;
        }

        switch (gravity & Gravity.VERTICAL_GRAVITY_MASK) {
            case Gravity.TOP:
                top = 0;
                break;
            case Gravity.CENTER_VERTICAL:
                top = -height / 2;
                break;
            case Gravity.BOTTOM:
            default:
                top = -height;
                break;
        }

        //@formatter:off
        marker.setBounds(
                left,
                top,
                left + width,
                top + height);
        //@formatter:on

        return marker;
    }
    
    /**
	 * Adjust the zoom and center of the map so that all the given
	 * {@link OverlayItem} of the annotations will be displayed on screen
	 * 
	 * @param annotations
	 *            A {@link List} of {@link OverlayItem} used to center and zoom
	 *            the map
	 */
	public static void centerAndZoom(MapView map, List<? extends OverlayItem> list) {
		if (list == null || list.size() < 1) {
			return;
		}

		int minLat = Integer.MAX_VALUE, minLng = Integer.MAX_VALUE, maxLat = Integer.MIN_VALUE, maxLng = Integer.MIN_VALUE;

		final int size = list.size();
		for (int i = 0; i < size; i++) {
			final GeoPoint point = list.get(i).getPoint();
			if (point != null) {
				minLat = Math.min(point.getLatitudeE6(), minLat);
				maxLat = Math.max(point.getLatitudeE6(), maxLat);
				minLng = Math.min(point.getLongitudeE6(), minLng);
				maxLng = Math.max(point.getLongitudeE6(), maxLng);
			}
		}

		final GeoPoint center = new GeoPoint((maxLat + minLat) / 2,
				(maxLng + minLng) / 2);
		final int deltaLat = maxLat - minLat;
		final int deltaLng = maxLng - minLng;

		map.getController().zoomToSpan(deltaLat, deltaLng);
		map.getController().animateTo(center);
		map.invalidate();
	}

}
