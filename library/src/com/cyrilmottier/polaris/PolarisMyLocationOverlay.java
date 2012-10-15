package com.cyrilmottier.polaris;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class PolarisMyLocationOverlay extends MyLocationOverlay {

    private OnCurrentLocationClickListener mOnCurrentLocationClickListener;
    private OnCurrentLocationChangedListener mOnCurrentLocationChangedListener;

    public PolarisMyLocationOverlay(Context context, MapView mapView) {
        super(context, mapView);
    }

    @Override
    protected boolean dispatchTap() {
        GeoPoint p = this.getMyLocation();

        if(mOnCurrentLocationClickListener != null)
            mOnCurrentLocationClickListener.onCurrentLocationClick(p);
        return true;
    }

    @Override
    public synchronized void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        if(mOnCurrentLocationChangedListener != null)
            mOnCurrentLocationChangedListener.onCurrentLocationChanged(location);
    }

    public void setOnCurrentLocationClickListener(OnCurrentLocationClickListener mOnCurrentLocationClickListener) {
        this.mOnCurrentLocationClickListener = mOnCurrentLocationClickListener;
    }

    public OnCurrentLocationClickListener getOnCurrentLocationClickListener() {
        return mOnCurrentLocationClickListener;
    }

    public OnCurrentLocationChangedListener getOnCurrentLocationChangedListener() {
        return mOnCurrentLocationChangedListener;
    }

    public void setOnCurrentLocationChangedListener(OnCurrentLocationChangedListener listener) {
        mOnCurrentLocationChangedListener = listener;
    }

    public static interface OnCurrentLocationClickListener {
        public void onCurrentLocationClick(GeoPoint point);
    }

    public static interface OnCurrentLocationChangedListener {
        public void onCurrentLocationChanged(Location location);
    }
}