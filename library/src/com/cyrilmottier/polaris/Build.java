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

/**
 * Information about the current build.
 * 
 * @author Cyril Mottier
 */
public final class Build {

    private Build() {
    }

    /**
     * Enumeration of the currently known library version codes. These are the
     * values that can be found in library. Version numbers increment
     * monotonically with each official library release.
     * 
     * @author Cyril Mottier
     */
    public static class VERSION_CODES {

        /**
         * The initial version of the library ... Yep, it starts with the 'b'
         * letter so I guess you'll easily understand the logic behind the
         * version naming.
         */
        public static final int BREAKING_BAD = 1;
    }
}
