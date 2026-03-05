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

// Start block - Added by KJ for RideSense - Persona Scores Normalized

package com.graphhopper.routing.util.parsers;

import com.graphhopper.reader.ReaderWay;
import com.graphhopper.routing.ev.EdgeIntAccess;
import com.graphhopper.routing.ev.DecimalEncodedValue;
import com.graphhopper.routing.ev.RSPersonaMileMuncherScoreNormalised;
import com.graphhopper.storage.IntsRef;

public class RSPersonaMileMuncherScoreNormalisedParser implements TagParser {

    private final DecimalEncodedValue rsPersonaMileMuncherScoreNormalisedEnc;

    public RSPersonaMileMuncherScoreNormalisedParser(DecimalEncodedValue rsPersonaMileMuncherScoreNormalisedEnc) {
        this.rsPersonaMileMuncherScoreNormalisedEnc = rsPersonaMileMuncherScoreNormalisedEnc;
    }

    @Override
    public void handleWayTags(int edgeId, EdgeIntAccess edgeIntAccess, ReaderWay readerWay, IntsRef relationFlags) {
        String tagValue = readerWay.getTag(RSPersonaMileMuncherScoreNormalised.KEY);
        double value = 0.0;  // Default to 0.0
        if (tagValue != null) {
            try {
                value = Double.parseDouble(tagValue);
                // Clamp to 0.0-1.0 range
                value = Math.max(0.0, Math.min(1.0, value));
            } catch (NumberFormatException e) {
                value = 0.0;  // Default on parse error
            }
        }
        rsPersonaMileMuncherScoreNormalisedEnc.setDecimal(false, edgeId, edgeIntAccess, value);
    }
}
// End block - Added by KJ for RideSense - Persona Scores Normalized
