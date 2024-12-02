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

 // Start block - Added by KJ for RideSense 16062024

package com.graphhopper.routing.ev;

public class RSSceneryBeach {
    public static final String KEY = "road_scenery_beach";

    public static IntEncodedValue create() {
        return new IntEncodedValueImpl(KEY, 2, false);
    }
}
