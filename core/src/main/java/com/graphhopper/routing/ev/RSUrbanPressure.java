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

// Start block - Added by KJ for RideSense

package com.graphhopper.routing.ev;

public class RSUrbanPressure {
    public static final String KEY = "urban_pressure";

    public static DecimalEncodedValue create() {
        // Range: 0.0 to 1.0 (urban pressure score)
        // 7 bits precision (128 values), factor = 0.01 for 2 decimal places precision
        // This gives precision of 0.01, covering range 0.00 to 1.27 (sufficient for 0-1.0)
        return new DecimalEncodedValueImpl(KEY, 7, 0.0, 0.01, false, false, false);
    }
}
// End block - Added by KJ for RideSense
