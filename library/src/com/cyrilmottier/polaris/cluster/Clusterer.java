/*
 * Copyright (C) 2012 Damian Flannery (http://www.damianflannery.com)
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
package com.cyrilmottier.polaris.cluster;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.cyrilmottier.polaris.Annotation;
import com.cyrilmottier.polaris.PolarisMapView;
import com.cyrilmottier.polaris.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.Projection;

/**
 * This class is used to group {@link Annotation} objects that overlap each 
 * other on a {@link PolarisMapView} by projecting geolocation to a position
 * on screen and detecting pixel proximity.
 * 
 * A single {@link Annotation} object will be used to represent overlapping 
 * {@link Annotation} objects by replacing its map marker drawable with a 
 * ClusterSpot drawable. This drawable will be a certain size and colour depending 
 * on how many {@link Annotation} objects it contains and the current 
 * {@link ClusterConfig} 
 * 
 * e.g. 
 * 
 * by default a Cluster with less than 10 Annotations will be defined as Low
 * and will display as a yellow circle with a diameter of 24dips.
 * 
 * by default a Cluster with with 10 - 19 Annotations will be defined as Medium
 * and will display as an orange circle with a diameter of 28dips.
 *  
 * by default a Cluster with 20+ Annotations will be defined as High
 * and will display as a red circle with a diameter of 32dips.
 * 
 * These are configurable from within the Polaris colors and dimens project resources.
 * 
 * @author Damian Flannery
 */
public class Clusterer {

	/** Grid size for clustering (in dips). */
	protected int GRIDSIZE = 20;
	
	/** Value to store calculated screen density of device **/
	protected float mScreenDensity;
	
	/** ArrayList of items to be clustered. */
	protected List<Cluster> mClusters = new ArrayList<Cluster>();

	/** MapView **/
	protected PolarisMapView mMapView;
	
	/** Activity Context **/
	protected Context mContext;
	
	
	List<Annotation> mAnnotations = new ArrayList<Annotation>();
	
	/** 
	 * Cluster config used for categorising clusters into low, med 
	 * & high categories. Defaults will apply if not supplied by the
	 * caller
	 **/
	protected ClusterConfig mClusterConfig;
	
	/**
     * Create a new {@link Clusterer}.
     * 
     * @param map A {@link PolarisMapView} object.
     */
	public Clusterer(PolarisMapView map) {
		init(map, null);
	}
	
	/**
     * Create a new {@link Clusterer} with our own custom {@link ClusterConfig}
     * 
     * @param map A {@link PolarisMapView} object.
     * @param map A {@link ClusterConfig} configuration for low, med & high cluster spots.
     */
	public Clusterer(PolarisMapView map, ClusterConfig config) {
		init(map, config);
	}
	
	/**
     * Create a new {@link Clusterer} with an initial set of {@link Annotation}.
     * 
     * @param map A {@link PolarisMapView} object.
     * @param items A list of {@link Annotation} objects.
     */
	public Clusterer(PolarisMapView map, List<Annotation> items) {
		init(map, null);
		add(items);
	}
	
	/**
     * Create a new {@link Clusterer} with an initial set of {@link Annotation}
     * while also initialising our own {@link ClusterConfig}
     * 
     * @param map A {@link PolarisMapView} object.
     * @param items A list of {@link Annotation} objects.
     * @param map A {@link ClusterConfig} configuration for low, med & high cluster spots.
     */
	public Clusterer(PolarisMapView map, List<Annotation> items, ClusterConfig config) {
		init(map, config);
		add(items);
	}
	
	/**
     * Create a new {@link Clusterer} with our own {@link ClusterConfig}
     * 
     * @param map A {@link PolarisMapView} object.
     * @param map A {@link ClusterConfig} configuration for low, med & high cluster spots.
     */
	private void init(PolarisMapView map, ClusterConfig config) {
		mMapView = map;
		mContext = mMapView.getContext();
		mScreenDensity = mContext.getResources().getDisplayMetrics().density;
		
		setClusterConfig(config);
	}

	/**
     * Add a List of {@link Annotation} to our {@link Clusterer} cache.
     * This method will add the supplied {@link Annotation}s to our internal
     * cache of Annotations
     * 
     * @param items A list of {@link Annotation} objects.
     */
	public void add(List<Annotation> items) {
		mAnnotations = items;
	}
	
	/**
     * Add a single {@link Annotation} to our {@link Clusterer} cache.
     * This method will add the supplied {@link Annotation} to our internal
     * cache of Annotations
     * 
     * @param item An {@link Annotation} object.
     */
	public void add(Annotation item) {
		mAnnotations.add(item);
	}
	
	private void addInternal(List<Annotation> items) {
		for (Annotation item : items) {
			addInternal(item);
		}
	}

	/**
     * Add a single {@link Annotation} to our {@link Clusterer}.
     * This method will add the supplied {@link Annotation} to an existing cluster 
     * if it overlaps with any other {@link Cluster} that we have stored. Otherwise
     * it will create a new {@link Cluster} object
     * 
     * @param item An {@link Annotation} object.
     */
	private void addInternal(Annotation item) {
		
		int length = mClusters.size();
		Cluster cluster = null;
		Projection proj = mMapView.getProjection();

		Point pos = proj.toPixels(item.getPoint(), null);
		// check existing cluster
		for (int i = length - 1; i >= 0; i--) {
			cluster = mClusters.get(i);
			GeoPoint gpCenter = cluster.getCenter();
			if (gpCenter == null)
				continue;
			Point ptCenter = proj.toPixels(gpCenter, null);
			// find a cluster which contains the marker.
			final int GridSizePx = (int) (GRIDSIZE * mScreenDensity + 0.5f);
			if (pos.x >= ptCenter.x - GridSizePx && pos.x <= ptCenter.x + GridSizePx && pos.y >= ptCenter.y - GridSizePx && pos.y <= ptCenter.y + GridSizePx) {
				cluster.addItem(item);
				return;
			}
		}
		// No cluster contain the marker, create a new cluster.
		createCluster(item);
	}
	
	private void createCluster(Annotation item) {
		Cluster cluster = new Cluster();
		cluster.addItem(item);
		mClusters.add(cluster);
	}
	
	public List<Annotation> getClusters(List<Annotation> items) {
		mAnnotations = items;
		return getClusters();
	}
	
	 /**
     * This will return a reduced set of {@link Annotation} objects by looping
     * through our list of {@link Cluster} objects and replacing the given marker
     * with a Cluster spot drawable depending on how many Annotations are within the
     * given {@link Cluster}. If an {@link Annotation} is a cluster (i.e.
     * 
     * @return a reduced set of {@link Annotation} objects.
     */
	public List<Annotation> getClusters() {
		mClusters = new ArrayList<Cluster>();
		addInternal(mAnnotations);
		
		List<Annotation> overlays = new ArrayList<Annotation>();
		Annotation overlay;
		
		for (Cluster cluster : mClusters) {
			List<Annotation> items = cluster.getItems();
			String title = "";
			String snippet = "";
			
			int total = items.size();
			if (total > 1) {
				title = mContext.getString(R.string.polaris__cluster_title);
				snippet = mContext.getResources().getQuantityString(R.plurals.polaris__cluster_snippet, total, total);
				
				Drawable marker = null;
				if (total <= mClusterConfig.getLow()) {
					marker = ClusterUtils.createClusterSpot(mContext, 
							mContext.getResources().getDimensionPixelSize(R.dimen.polaris__cluster_spot_size_low), 
							R.drawable.polaris__circle_gradient_low, "" + total);
				} else if (total <= mClusterConfig.getMedium()) {
					marker = ClusterUtils.createClusterSpot(mContext, 
							mContext.getResources().getDimensionPixelSize(R.dimen.polaris__cluster_spot_size_medium), 
							R.drawable.polaris__circle_gradient_medium, "" + total);
				} else {
					marker = ClusterUtils.createClusterSpot(mContext, 
							mContext.getResources().getDimensionPixelSize(R.dimen.polaris__cluster_spot_size_high), 
							R.drawable.polaris__circle_gradient_high, "" + total);
				}
				
				overlay = new Annotation(cluster.getCenter(), title, snippet, marker, items);
			} else {
				title = items.get(0).getTitle();
				overlay = new Annotation(cluster.getCenter(), title, snippet, cluster.getItems().get(0).getMarker(), items);
			}
			
			overlays.add(overlay);
		}
		
		return overlays;
	}

	 /**
     * Returns our current {@link ClusterConfig} config
     * 
     * @return our Clusterer configuration (high, med & low thresholds)
     */
	public ClusterConfig getClusterConfig() {
		return mClusterConfig;
	}

	/**
	 * Sets a custom Clusterer configuration. If null is supplied then we 
	 * create one with default values
	 * 
	 * @param config a config object to define high, med & low thresholds
	 * 			
	 */
	public void setClusterConfig(ClusterConfig config) {
		mClusterConfig = config == null ? new ClusterConfig() : config;
	}
	
	public void clearAnnotations() {
		mClusters = new ArrayList<Cluster>();
		mAnnotations = new ArrayList<Annotation>();
	}
}
