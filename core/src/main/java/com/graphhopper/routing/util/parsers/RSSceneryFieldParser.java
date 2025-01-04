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

package com.graphhopper.routing.util.parsers;

import com.graphhopper.reader.ReaderWay;
import com.graphhopper.routing.ev.EdgeIntAccess;
import com.graphhopper.routing.ev.IntEncodedValue;
import com.graphhopper.storage.IntsRef;


public class RSSceneryFieldParser implements TagParser {

    private final IntEncodedValue rSSceneryFieldEnc;

    public RSSceneryFieldParser(IntEncodedValue rSSceneryFieldEnc) {
        this.rSSceneryFieldEnc = rSSceneryFieldEnc;
    }

    @Override
    public void handleWayTags(int edgeId, EdgeIntAccess edgeIntAccess, ReaderWay readerWay, IntsRef relationFlags) {
        
        String rssceneryField = readerWay.getTag("road_scenery_field"); 
        int value = 0;
        if (rssceneryField != null) {
            value = Integer.parseInt(rssceneryField);
        }        
        rSSceneryFieldEnc.setInt(false, edgeId, edgeIntAccess, value);

    }
}