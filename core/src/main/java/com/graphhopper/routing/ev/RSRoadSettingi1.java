/*
 * Copyright 2024 KJ
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Start block - Added by KJ for RideSense 22122025

package com.graphhopper.routing.ev;

import com.graphhopper.util.Helper;

public enum RSRoadSettingi1 {
    URBAN, SEMIURBAN, RURAL, UNKNOWN;

    public static final String KEY = "road_setting_i1";

    public static EnumEncodedValue<RSRoadSettingi1> create() {
        return new EnumEncodedValue<>(KEY, RSRoadSettingi1.class);
    }

    @Override
    public String toString() {
        return Helper.toLowerCase(super.toString());
    }

    public static RSRoadSettingi1 find(String name) {
        if (Helper.isEmpty(name))
            return UNKNOWN;
        try {
            return RSRoadSettingi1.valueOf(Helper.toUpperCase(name));
        } catch (IllegalArgumentException ex) {
            return UNKNOWN;
        }
    }
}
// End block - Added by KJ for RideSense 22122025


