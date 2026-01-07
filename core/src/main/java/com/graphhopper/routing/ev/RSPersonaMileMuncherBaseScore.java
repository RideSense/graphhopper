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

// Start block - Added by KJ for RideSense - Persona Scores Integration

package com.graphhopper.routing.ev;

public class RSPersonaMileMuncherBaseScore {
    public static final String KEY = "persona_milemuncher_base_score";

    public static IntEncodedValue create() {
        // Range: 0 to 100 (persona base score)
        // 7 bits precision (2^7 = 128, covers 0-100)
        return new IntEncodedValueImpl(KEY, 7, 0, false, false);
    }
}
// End block - Added by KJ for RideSense - Persona Scores Integration
