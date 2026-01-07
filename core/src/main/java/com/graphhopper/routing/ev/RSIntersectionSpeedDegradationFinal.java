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

// Start block - Added by KJ for RideSense - Intersection Speed Degradation v2

package com.graphhopper.routing.ev;

public class RSIntersectionSpeedDegradationFinal {
    public static final String KEY = "intersection_speed_degradation_final";

    public static DecimalEncodedValue create() {
        // Range: 0.0 to 1.0 (parser clamps to 0.5-1.0, default is 1.0)
        // 8 bits precision (256 values), factor = 0.01 for 2 decimal places precision
        // This gives precision of 0.01, covering range 0.00 to 2.55 (sufficient for 0-1.0)
        return new DecimalEncodedValueImpl(KEY, 8, 0.0, 0.01, false, false, false);
    }
}
// End block - Added by KJ for RideSense - Intersection Speed Degradation v2

