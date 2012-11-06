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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.cyrilmottier.polaris.cluster.ClusterConfig;
import com.cyrilmottier.polaris.cluster.ClusterSpot;
import com.cyrilmottier.polaris.cluster.Clusterer;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;

public class MainActivity extends MapActivity implements OnRegionChangedListener, OnAnnotationSelectionChangedListener {

	private static final String LOG_TAG = "MainActivity";

	// @formatter:off
	private static final Annotation[] sFrance = {
			new Annotation(
					new GeoPoint(48635600, -1510600),
					"Mont Saint Michel",
					"Mont Saint-Michel is a rocky tidal island and a commune in Normandy, France. It is located approximately one kilometre (just over half a mile) off the country's north-western coast, at the mouth of the Couesnon River near Avranches."),
			new Annotation(new GeoPoint(48856600, 2351000), "Paris", "The city of love"), new Annotation(new GeoPoint(44837400, -576100), "Bordeaux", "A port city in southwestern France"),
			new Annotation(new GeoPoint(48593100, -647500), "Domfront", "A commune in the Orne department in north-western France"), };

	private static final Annotation[] sEurope = { new Annotation(new GeoPoint(55755800, 37617600), "Moscow"), new Annotation(new GeoPoint(59332800, 18064500), "Stockholm"),
			new Annotation(new GeoPoint(59939000, 30315800), "Saint Petersburg"), new Annotation(new GeoPoint(60169800, 24938200), "Helsinki"),
			new Annotation(new GeoPoint(60451400, 22268700), "Turku"), new Annotation(new GeoPoint(65584200, 22154700), "Lule\u00E5"), new Annotation(new GeoPoint(59438900, 24754500), "Talinn"),
			new Annotation(new GeoPoint(66498700, 25721100), "Rovaniemi"), };

	private static final Annotation[] sUsaWestCoast = { new Annotation(new GeoPoint(40714400, -74006000), "New York City"), new Annotation(new GeoPoint(39952300, -75163800), "Philadelphia"),
			new Annotation(new GeoPoint(38895100, -77036400), "Washington"), new Annotation(new GeoPoint(41374800, -83651300), "Bowling Green"),
			new Annotation(new GeoPoint(42331400, -83045800), "Detroit"), };

	private static final Annotation[] sUsaEastCoast = { new Annotation(new GeoPoint(37774900, -122419400), "San Francisco"),
			new Annotation(new GeoPoint(37770600, -119510800), "Yosemite National Park"), new Annotation(new GeoPoint(36878200, -121947300), "Monteray Bay"),
			new Annotation(new GeoPoint(35365800, -120849900), "Morro Bay"), new Annotation(new GeoPoint(34420800, -119698200), "Santa Barbara"),
			new Annotation(new GeoPoint(34052200, -118243700), "Los Angeles"), new Annotation(new GeoPoint(32715300, -117157300), "San Diego"),
			new Annotation(new GeoPoint(36114600, -115172800), "Las Vegas"), new Annotation(new GeoPoint(36220100, -116881700), "Death Valley"),
			new Annotation(new GeoPoint(36355200, -112661200), "Grand Canyon"), new Annotation(new GeoPoint(37289900, -113048900), "Zion National Park"),
			new Annotation(new GeoPoint(37628300, -112167700), "Bryce Canyon"), new Annotation(new GeoPoint(36936900, -111483800), "Lake Powell"), };

	private static final Annotation[] sUK = { new Annotation(new GeoPoint((int) (51.75222 * 1E6), (int) (-1.25596 * 1E6)), "Oxford"),
			new Annotation(new GeoPoint((int) (51.50249 * 1E6), (int) (-0.11579 * 1E6)), "London"), new Annotation(new GeoPoint((int) (52.48048 * 1E6), (int) (-1.89823 * 1E6)), "Birmingham"),
			new Annotation(new GeoPoint((int) (53.39763 * 1E6), (int) (-2.99205 * 1E6)), "Liverpool"), new Annotation(new GeoPoint((int) (55.94973 * 1E6), (int) (-3.19333 * 1E6)), "Edinburgh"),
			new Annotation(new GeoPoint((int) (55.86667 * 1E6), (int) (-4.25 * 1E6)), "Glasgow"), new Annotation(new GeoPoint((int) (50.46384 * 1E6), (int) (-3.51434 * 1E6)), "Torquay"),
			new Annotation(new GeoPoint((int) (54.58333 * 1E6), (int) (-5.93333 * 1E6)), "Belfast"), new Annotation(new GeoPoint((int) (53.48095 * 1E6), (int) (-2.23743 * 1E6)), "Manchester"),
			new Annotation(new GeoPoint((int) (53.81667 * 1E6), (int) (-3.05 * 1E6)), "Blackpool"), new Annotation(new GeoPoint((int) (50.82838 * 1E6), (int) (-0.13947 * 1E6)), "Brighton"),
			new Annotation(new GeoPoint((int) (52.2 * 1E6), (int) (0.11667 * 1E6)), "Cambridge"), new Annotation(new GeoPoint((int) (50.72048 * 1E6), (int) (-1.8795 * 1E6)), "Bournemouth"),
			new Annotation(new GeoPoint((int) (50.90395 * 1E6), (int) (-1.40428 * 1E6)), "Southampton"), new Annotation(new GeoPoint((int) (50.79899 * 1E6), (int) (-1.09125 * 1E6)), "Portsmouth"), };

	private static final Annotation[][] sRegions = { sFrance, sEurope, sUsaEastCoast, sUsaWestCoast, sUK };

	// @formatter:on

	PolarisMapView mMapView;
	Clusterer mClusterer;
	ArrayList<Annotation> annotations = new ArrayList<Annotation>();
	boolean mUseClustering = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_activity);

		mMapView = new PolarisMapView(this, Config.GOOGLE_MAPS_API_KEY);
		mMapView.setUserTrackingButtonEnabled(true);
		mMapView.setOnRegionChangedListenerListener(this);
		mMapView.setOnAnnotationSelectionChangedListener(this);

		// Prepare an alternate pin Drawable
		final Drawable altMarker = MapViewUtils.boundMarkerCenterBottom(getResources().getDrawable(R.drawable.map_pin_holed_violet));

		// Prepare the list of Annotation using the alternate Drawable for all
		// Annotation located in France
		for (Annotation[] region : sRegions) {
			for (Annotation annotation : region) {
				if (region == sFrance) {
					annotation.setMarker(altMarker);
				}
				annotations.add(annotation);
			}
		}

		// Create a Clusterer, optionally configuring our own defaults for low,
		// med & high marker thresholds
		mClusterer = new Clusterer(mMapView, new ClusterConfig(this, 4, 8));
		
		//TODO: uncomment and play if you want something more custom
		//mClusterer = new Clusterer(mMapView, getAdvancedClusterConfig());
		
		mClusterer.add(annotations);
		
		// moved loading of annotations to separate method so that it can be
		// reused
		loadMapData();

		final FrameLayout mapViewContainer = (FrameLayout) findViewById(R.id.map_view_container);
		mapViewContainer.addView(mMapView, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	private ClusterConfig getAdvancedClusterConfig() {
		
		// Create cluster spot with our own custom drawable and
		// change the text size and color as we like
		ClusterSpot spot = new ClusterSpot(R.drawable.black_square);
		spot.setTextColorResourceId(android.R.color.white);
		spot.setTextSize(18);
		
		// Give our cluster marker our own title e.g. H for High 
		// (this will be shown instead of the number of markers within
		// the cluster spot)
		spot.setTitle("H");
		
		// Build and return our ClusterConfig with custom ClusterSpot and
		// thresholds for clustering
		return new ClusterConfig.Builder(this)
		.setHighClusterSpot(spot)
		//TODO: define your own ClusterSpots for Med & High
		//.setMediumClusterSpot(anotherSpot)
		//.setLowClusterSpot(moarSpot)
		.setLow(4).setMedium(8)
			.build();
	}

	void loadMapData() {
		// Don't forget to clear any existing annotations and add new ones if
		// your dataset changes!
		
		// mClusterer.clearAnnotations();
		// mClusterer.addItems(annotations);

		// Add markers on map view using a reduced set of annotations filtered
		// Clusterer has a handle on the map view so it can see that the
		// viewport has changed and produce a fresh filtered set of Annotations 
		// accordingly
		if (mUseClustering)
			mMapView.setAnnotations(mClusterer.getClusters(), R.drawable.map_pin_holed_blue);
		else
			mMapView.setAnnotations(annotations, R.drawable.map_pin_holed_blue);
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

			// Re-cluster and load map markers now that the region has changed
			// TODO: This should probably be in an onZoomChangeConfirmed
			// (which does not exist yet) method for efficiency
			if (mUseClustering)
				loadMapData();
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

		if (annotation.isCluster()) {
			String cities = "[";
			for (Annotation an : annotation.getClusteredAnnotations()) {
				cities += an.getTitle() + ", ";
			}
			cities = cities.substring(0, cities.length() - 2) + "]";
			Toast.makeText(this, "Cluster clicked " + cities, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, getString(R.string.annotation_clicked, annotation.getTitle()), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		if (mUseClustering) {
			menu.findItem(R.id.use_clustering).setTitle("Disable Clustering");
		} else {
			menu.findItem(R.id.use_clustering).setTitle("Enable Clustering");
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.use_clustering:
			mUseClustering = !mUseClustering;
			loadMapData();
		}

		return true;
	}
}