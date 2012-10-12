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
package com.cyrilmottier.android.polarissample.util;

import java.lang.reflect.Field;

import android.util.Log;

import com.cyrilmottier.android.polarissample.BuildConfig;

/**
 * @author Cyril Mottier
 */
@SuppressWarnings("all")
public class Config {

    private static final String LOG_TAG = "Config";

    private Config() {
    }

    // /////////////////////////////////////////////////////////////
    //
    // Local config
    //
    // /////////////////////////////////////////////////////////////

    private static final String LOCAL_CONFIG_CLASS_NAME = "com.cyrilmottier.android.polarissample.util.LocalConfig";
    private static final String LOCAL_GOOGLE_MAPS_API_KEY_RELEASE_FIELD_NAME = "GOOGLE_MAPS_API_KEY_RELEASE";
    private static final String LOCAL_GOOGLE_MAPS_API_KEY_DEBUG_FIELD_NAME = "GOOGLE_MAPS_API_KEY_DEBUG";

    private static final Class<?> LOCAL_CONFIG_CLASS = getLocalConfig();

    private static Class<?> getLocalConfig() {
        try {
            return Class.forName(LOCAL_CONFIG_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG, "No local configuration file with class name (" + LOCAL_CONFIG_CLASS_NAME + ") found");
            return null;
        }
    }

    /**
     * Uses reflection in order to extract a local configuration.
     * 
     * @return The String associated to the given name (should be stored as a
     *         static final variable) or the defaultValue if an error occurred.
     */
    private static String getLocalConfigString(String name) {
        try {
            return (String) LOCAL_CONFIG_CLASS.getField(name).get(null);
        } catch (Exception e) {
            Log.e(LOG_TAG, "No local configuration found with key " + name);
            return null;
        }
    }

    // /////////////////////////////////////////////////////////////
    //
    // Compilation target
    //
    // /////////////////////////////////////////////////////////////

    private static final int COMPILATION_TARGET_RELEASE = 0;
    private static final int COMPILATION_TARGET_DEBUG = 1;

    /**
     * The current compilation target
     */
    private static final int COMPILATION_TARGET = BuildConfig.DEBUG ? COMPILATION_TARGET_DEBUG : COMPILATION_TARGET_RELEASE;

    // /////////////////////////////////////////////////////////////
    //
    // Google Maps API key
    //
    // /////////////////////////////////////////////////////////////

    /**
     * The current Google Maps API key to use when using MapView instances
     */
    public static final String GOOGLE_MAPS_API_KEY;

    static {
        GOOGLE_MAPS_API_KEY = (COMPILATION_TARGET == COMPILATION_TARGET_RELEASE) ? getLocalConfigString(LOCAL_GOOGLE_MAPS_API_KEY_RELEASE_FIELD_NAME)
                : getLocalConfigString(LOCAL_GOOGLE_MAPS_API_KEY_DEBUG_FIELD_NAME);
    }

    // /////////////////////////////////////////////////////////////
    //
    // Logs
    //
    // /////////////////////////////////////////////////////////////

    private static final int LOG_LEVEL_INFO = 3;
    private static final int LOG_LEVEL_WARNING = 2;
    private static final int LOG_LEVEL_ERROR = 1;
    private static final int LOG_LEVEL_NONE = 0;

    /**
     * Set this flag to LOG_LEVEL_NONE when releasing your application in order
     * to remove all logs.
     */
    private static final int LOG_LEVEL = COMPILATION_TARGET == COMPILATION_TARGET_DEBUG ? LOG_LEVEL_INFO : LOG_LEVEL_ERROR;

    /**
     * Indicates whether info logs are enabled. This should be true only when
     * developing/debugging an application/the library
     */
    public static final boolean INFO_LOGS_ENABLED = (LOG_LEVEL == LOG_LEVEL_INFO);

    /**
     * Indicates whether warning logs are enabled
     */
    public static final boolean WARNING_LOGS_ENABLED = INFO_LOGS_ENABLED || (LOG_LEVEL == LOG_LEVEL_WARNING);

    /**
     * Indicates whether error logs are enabled. Error logs are usually always
     * enabled, even in production releases.
     */
    public static final boolean ERROR_LOGS_ENABLED = WARNING_LOGS_ENABLED || (LOG_LEVEL == LOG_LEVEL_ERROR);

}
