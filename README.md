Polaris
=======

Polaris is a framework greatly enhancing the Google Maps external framework. It aims to:

  * Make the user's life easier
  * Make the developer's life easier
  * Make the resulting map look polished and natural
  
[![Polaris Sample screenshot][1]][6]

Here are some interesting links you can use to discover Polaris:

  * **Polaris Sample**: A demo application demonstrating some features of the Polaris. It is available on the [Google Play][2]).
  * **Meet Polaris, a map library for Android**: A blog post I wrote to introduce the first version of the library. It contains a great description of the main features included in Polaris. You can read it on my [personal blog][3]).

Features
--------

  * Gesture support
  * Effortless map annotating
  * Map callout variable anchor positioning
  * Built-in “user tracking” mode
  * Automatic built-in zoom controls
  * Natural map callouts transitions
  * Additional listeners
  * And many more!

Limitations
-----------
Polaris is an extension of the Google Maps External Library and hence suffers from a lot of its limitations and bugs (`PolarisMapView` must be used in a `MapActivity`, only one instance of `PolarisMapView` is allowed per `MapActivity`, etc.).

While reducing the complexity of the library, `Polaris` made the use of `Overlay` almost useless by managing main `Overlay`s such as `MyLocationOverlay` and `ItemizedOverlay` internally. As a consequence, you must never use the `getOverlays()` method. `PolarisMapView` provides replacement methods such as `addOverlay(Overlay)`, `removeOverlay(Overlay)`, etc.

Usage
-----

  1. Include the PolarisMapView widget in your layout.

        <com.cyrilmottier.polaris.PolarisMapView
            android:id="@+id/polaris_map_view"
            android:layout_height="match_parent"
            android:layout_width="match_parent"
            android:apiKey="[YOUR_API_KEY]" />

  2. In your `onCreate` method, add/find the PolarisMapView to your layout.

         mPolarisMapView = (PolarisMapView) findViewById(R.id.polaris_map_view)
         
  3. Bind your `MapActivity`'s `onStart` and `onStop` method to the `PolarisMapView`
     equivalent.

        @Override
        protected void onStart() {
            super.onStart();
            mPolarisMapView.onStart();
        }
    
        @Override
        protected void onStop() {
            super.onStop();
            mPolarisMapView.onStop();
        } 

###Including In Your Project

The Polaris library is presented as an [Android library project][4]. You can include this project by [referencing it as a library project][5] in Eclipse or ant.

###Building the sample application

Using the Google Maps external library requires registering your signing key to Google. When doing so, Google generates an API key you must pass to `MapView` when instanciating. For obvious reasons, you must keep these keys private.

Logically, the sample application doesn't include API keys. In order to run the project correctly you must add a `LocalConfig.java` file in the `com.cyrilmottier.android.polarissample.util` package containing your own API keys:

    package com.cyrilmottier.android.polarissample.util;
    
    public final class LocalConfig {
    
        private LocalConfig() {
        }
    
        public static final String GOOGLE_MAPS_API_KEY_RELEASE = "<YOUR_RELEASE_KEY>";
        public static final String GOOGLE_MAPS_API_KEY_DEBUG = "<YOUR_DEBUG_KEY>";
    
    }

Clustering
----------

You can simply use the `Clusterer` to filter and give you a reduced set of `Annotation`s before adding to your `PolarisMapView`.

The `Clusterer` groups `Annotation` objects that overlap each by projecting geolocation to a position on screen and detecting pixel proximity. A single `Annotation` object will be used to represent overlapping `Annotation`s by replacing its map marker drawable with a cluster spot. This drawable will be a certain size and colour depending on how many `Annotation` objects it contains and the current `ClusterConfig` configuration.

e.g. in the sample below, a `Cluster` with
  * 4 or less `Annotation`s will be defined as Low and will display as a yellow circle
  * between 5 - 8 `Annotation`s will be defined as Medium and will display as an orange circle
  * 9 or more `Annotation`s will be defined as High and will display as a red circle
    
    Clusterer clusterer = new Clusterer(mMapView, annotations, new ClusterConfig(4, 8));
    mMapView.setAnnotations(clusterer.getClusters(), R.drawable.map_pin_holed_blue);

[![Normal Polaris Sample screenshot][7]
[![Clustered Polaris Sample screenshot][8]

Developed By
------------

Cyril Mottier - <cyril@cyrilmottier.com>

###Credits

- [Marie Schweiz](http://marie-schweiz.de): Designed the Polaris icon
- [Pierre Valade](https://twitter.com/pierrevalade): Helped me finding a nice name to this library

License
-------

	Copyright (C) 2012 Cyril Mottier (http://www.cyrilmottier.com)
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	     http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

[1]: http://cyrilmottier.github.com/Polaris/img/polaris_sample.png
[2]: https://play.google.com/store/apps/details?id=com.cyrilmottier.android.polarissample
[3]: http://android.cyrilmottier.com/?p=824
[4]: http://developer.android.com/guide/developing/projects/projects-eclipse.html
[5]: http://developer.android.com/guide/developing/projects/projects-eclipse.html#ReferencingLibraryProject
[6]: http://cyrilmottier.github.com/Polaris/img/polaris_sample_large.png
[7]: https://dl.dropbox.com/u/27007357/Polaris/polaris_normal.png
[8]: https://dl.dropbox.com/u/27007357/Polaris/polaris_clustered.png