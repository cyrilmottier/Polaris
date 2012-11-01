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

import android.os.Message;

import com.cyrilmottier.polaris.Annotation;
import com.google.android.maps.GeoPoint;

/**
 * Used to store a list of overlapping {@link Annotation}s and a 
 * given geolocation as a center for the cluster
 * 
 * @author Damian Flannery
 */
public class Cluster {
	private List<Annotation> list = new ArrayList<Annotation>();
	private GeoPoint center;
	
	public void addItem(Annotation annotation) {
		list.add(annotation);
		if (center == null) {
			center = annotation.getPoint();
		}
	}
	
	public List<Annotation> getItems() {
		return list;
	}
	
	public GeoPoint getCenter() {
		return center;
	}

}
