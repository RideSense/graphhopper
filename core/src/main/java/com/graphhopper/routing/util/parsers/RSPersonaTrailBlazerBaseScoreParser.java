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

package com.graphhopper.routing.util.parsers;

import com.graphhopper.reader.ReaderWay;
import com.graphhopper.routing.ev.EdgeIntAccess;
import com.graphhopper.routing.ev.IntEncodedValue;
import com.graphhopper.routing.ev.RSPersonaTrailBlazerBaseScore;
import com.graphhopper.storage.IntsRef;

public class RSPersonaTrailBlazerBaseScoreParser implements TagParser {

    private final IntEncodedValue rsPersonaTrailBlazerBaseScoreEnc;

    public RSPersonaTrailBlazerBaseScoreParser(IntEncodedValue rsPersonaTrailBlazerBaseScoreEnc) {
        this.rsPersonaTrailBlazerBaseScoreEnc = rsPersonaTrailBlazerBaseScoreEnc;
    }

    @Override
    public void handleWayTags(int edgeId, EdgeIntAccess edgeIntAccess, ReaderWay readerWay, IntsRef relationFlags) {
        String tagValue = readerWay.getTag(RSPersonaTrailBlazerBaseScore.KEY);
        int value = 0;  // Default to 0 (no score)
        if (tagValue != null) {
            try {
                // Parse as double first (handles decimal values like 47.50), then round to int
                double doubleValue = Double.parseDouble(tagValue);
                value = (int) Math.round(doubleValue);
                // Clamp to 0-100 range
                value = Math.max(0, Math.min(100, value));
            } catch (NumberFormatException e) {
                value = 0;  // Default on parse error
            }
        }
        rsPersonaTrailBlazerBaseScoreEnc.setInt(false, edgeId, edgeIntAccess, value);
    }
}
// End block - Added by KJ for RideSense - Persona Scores Integration

