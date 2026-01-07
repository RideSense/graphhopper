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

package com.graphhopper.routing.util.parsers;

import com.graphhopper.reader.ReaderWay;
import com.graphhopper.routing.ev.EdgeIntAccess;
import com.graphhopper.routing.ev.DecimalEncodedValue;
import com.graphhopper.routing.ev.RSIntersectionSpeedDegradationFinal;
import com.graphhopper.storage.IntsRef;

public class RSIntersectionSpeedDegradationFinalParser implements TagParser {

    private final DecimalEncodedValue rsIntersectionSpeedDegradationFinalEnc;

    public RSIntersectionSpeedDegradationFinalParser(DecimalEncodedValue rsIntersectionSpeedDegradationFinalEnc) {
        this.rsIntersectionSpeedDegradationFinalEnc = rsIntersectionSpeedDegradationFinalEnc;
    }

    @Override
    public void handleWayTags(int edgeId, EdgeIntAccess edgeIntAccess, ReaderWay readerWay, IntsRef relationFlags) {
        String tagValue = readerWay.getTag(RSIntersectionSpeedDegradationFinal.KEY);
        double value = 1.0;  // Default to 1.0 (no degradation = full speed multiplier)
        if (tagValue != null) {
            try {
                value = Double.parseDouble(tagValue);
                // Clamp to 0.5-1.0 range (speed multiplier: 50% to 100% of base speed)
                value = Math.max(0.5, Math.min(1.0, value));
            } catch (NumberFormatException e) {
                value = 1.0;  // Default on parse error (full speed)
            }
        }
        rsIntersectionSpeedDegradationFinalEnc.setDecimal(false, edgeId, edgeIntAccess, value);
    }
}
// End block - Added by KJ for RideSense - Intersection Speed Degradation v2

