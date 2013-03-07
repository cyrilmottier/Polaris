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
package com.cyrilmottier.android.polarissample;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.cyrilmottier.android.polarissample.util.Config;
import com.cyrilmottier.polaris.Annotation;
import com.cyrilmottier.polaris.MapCalloutView;
import com.cyrilmottier.polaris.MapViewUtils;
import com.cyrilmottier.polaris.PolarisMapView;
import com.cyrilmottier.polaris.PolarisMapView.OnAnnotationSelectionChangedListener;
import com.cyrilmottier.polaris.PolarisMapView.OnRegionChangedListener;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;

public class MainActivity extends MapActivity implements OnRegionChangedListener, OnAnnotationSelectionChangedListener {

    private static final String LOG_TAG = "MainActivity";

    //@formatter:off
    private static final Annotation[] sFrance = {
        new Annotation(new GeoPoint(48635600, -1510600), "Mont Saint Michel", "Mont Saint-Michel is a rocky tidal island and a commune in Normandy, France. It is located approximately one kilometre (just over half a mile) off the country's north-western coast, at the mouth of the Couesnon River near Avranches."),
        new Annotation(new GeoPoint(48856600, 2351000), "Paris", "The city of love"),
        new Annotation(new GeoPoint(44837400, -576100), "Bordeaux", "A port city in southwestern France"),
        new Annotation(new GeoPoint(48593100, -647500), "Domfront", "A commune in the Orne department in north-western France"),
    };
    
    private static final Annotation[] sEurope = {
        new Annotation(new GeoPoint(55755800, 37617600), "Moscow"),
        new Annotation(new GeoPoint(59332800, 18064500), "Stockholm"),
        new Annotation(new GeoPoint(59939000, 30315800), "Saint Petersburg"),
        new Annotation(new GeoPoint(60169800, 24938200), "Helsinki"),
        new Annotation(new GeoPoint(60451400, 22268700), "Turku"),
        new Annotation(new GeoPoint(65584200, 22154700), "Lule\u00E5"),
        new Annotation(new GeoPoint(59438900, 24754500), "Talinn"),
        new Annotation(new GeoPoint(66498700, 25721100), "Rovaniemi"),
    };
    
    private static final Annotation[] sUsaWestCoast = {
        new Annotation(new GeoPoint(40714400, -74006000), "New York City"),
        new Annotation(new GeoPoint(39952300, -75163800), "Philadelphia"),
        new Annotation(new GeoPoint(38895100, -77036400), "Washington"),
        new Annotation(new GeoPoint(41374800, -83651300), "Bowling Green"),
        new Annotation(new GeoPoint(42331400, -83045800), "Detroit"),
    };
    
    private static final Annotation[] sUsaEastCoast = {
        new Annotation(new GeoPoint(37774900, -122419400), "San Francisco"),
        new Annotation(new GeoPoint(37770600, -119510800), "Yosemite National Park"),
        new Annotation(new GeoPoint(36878200, -121947300), "Monteray Bay"),
        new Annotation(new GeoPoint(35365800, -120849900), "Morro Bay"),
        new Annotation(new GeoPoint(34420800, -119698200), "Santa Barbara"),
        new Annotation(new GeoPoint(34052200, -118243700), "Los Angeles"),
        new Annotation(new GeoPoint(32715300, -117157300), "San Diego"),
        new Annotation(new GeoPoint(36114600, -115172800), "Las Vegas"),
        new Annotation(new GeoPoint(36220100, -116881700), "Death Valley"),
        new Annotation(new GeoPoint(36355200, -112661200), "Grand Canyon"),
        new Annotation(new GeoPoint(37289900, -113048900), "Zion National Park"),
        new Annotation(new GeoPoint(37628300, -112167700), "Bryce Canyon"),
        new Annotation(new GeoPoint(36936900, -111483800), "Lake Powell"),
    };
    
    private static final Annotation[][] sRegions = {sFrance, sEurope, sUsaEastCoast, sUsaWestCoast};
    //@formatter:on

    private PolarisMapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        mMapView = new PolarisMapView(this, Config.GOOGLE_MAPS_API_KEY);
        mMapView.setUserTrackingButtonEnabled(true);
        mMapView.setOnRegionChangedListenerListener(this);
        mMapView.setOnAnnotationSelectionChangedListener(this);

        // Prepare an alternate pin Drawable
        final Drawable altMarker = MapViewUtils.boundMarkerCenterBottom(getResources().getDrawable(R.drawable.map_pin_holed_purple));

        // Prepare the list of Annotation using the alternate Drawable for all
        // Annotation located in France
        final ArrayList<Annotation> annotations = new ArrayList<Annotation>();
        for (Annotation[] region : sRegions) {
            for (Annotation annotation : region) {
                if (region == sFrance) {
                    annotation.setMarker(altMarker);
                }
                annotations.add(annotation);
            }
        }
        mMapView.setAnnotations(annotations, R.drawable.map_pin_holed_blue);

        final FrameLayout mapViewContainer = (FrameLayout) findViewById(R.id.map_view_container);
        mapViewContainer.addView(mMapView, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    @Override
    public void onRegionChanged(PolarisMapView mapView) {
        if (Config.INFO_LOGS_ENABLED) {
            Log.i(LOG_TAG, "onRegionChanged");
        }
    }

    @Override
    public void onRegionChangeConfirmed(PolarisMapView mapView) {
        if (Config.INFO_LOGS_ENABLED) {
            Log.i(LOG_TAG, "onRegionChangeConfirmed");
        }
    }

    @Override
    public void onAnnotationSelected(PolarisMapView mapView, MapCalloutView calloutView, int position, Annotation annotation) {
        if (Config.INFO_LOGS_ENABLED) {
            Log.i(LOG_TAG, "onAnnotationSelected");
        }
        calloutView.setDisclosureEnabled(true);
        calloutView.setClickable(true);
        if (!TextUtils.isEmpty(annotation.getSnippet())) {
            calloutView.setLeftAccessoryView(getLayoutInflater().inflate(R.layout.accessory, calloutView, false));
        } else {
            calloutView.setLeftAccessoryView(null);
        }
    }

    @Override
    public void onAnnotationDeselected(PolarisMapView mapView, MapCalloutView calloutView, int position, Annotation annotation) {
        if (Config.INFO_LOGS_ENABLED) {
            Log.i(LOG_TAG, "onAnnotationDeselected");
        }
    }

    @Override
    public void onAnnotationClicked(PolarisMapView mapView, MapCalloutView calloutView, int position, Annotation annotation) {
        if (Config.INFO_LOGS_ENABLED) {
            Log.i(LOG_TAG, "onAnnotationClicked");
        }
        Toast.makeText(this, getString(R.string.annotation_clicked, annotation.getTitle()), Toast.LENGTH_SHORT).show();
    }

}
