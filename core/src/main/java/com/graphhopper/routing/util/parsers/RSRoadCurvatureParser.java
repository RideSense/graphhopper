/*
 *  Licensed to GraphHopper GmbH under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for
 *  additional information regarding copyright ownership.
 *
 *  GraphHopper GmbH licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except in
 *  compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

  // Start block - Added by KJ for RideSense 16062024

 package com.graphhopper.routing.util.parsers;

 import com.graphhopper.reader.ReaderWay;
 import com.graphhopper.routing.ev.EnumEncodedValue;
 import com.graphhopper.routing.ev.RSRoadCurvature;
 import com.graphhopper.routing.ev.EdgeIntAccess;
 import com.graphhopper.storage.IntsRef;

 import static com.graphhopper.routing.ev.RSRoadCurvature.STRAIGHT;

public class RSRoadCurvatureParser implements TagParser {

    private final EnumEncodedValue<RSRoadCurvature> rSRoadCurvatureEnc;

    public RSRoadCurvatureParser(EnumEncodedValue<RSRoadCurvature> rSRoadCurvatureEnc) {
        this.rSRoadCurvatureEnc = rSRoadCurvatureEnc;
    }

    @Override
    public void handleWayTags(int edgeId, EdgeIntAccess edgeIntAccess, ReaderWay readerway, IntsRef relationFlags) {
        String rSRoadCurvatureTag = readerway.getTag("road_curvature_classification");
        RSRoadCurvature rsroadcurvature = RSRoadCurvature.find(rSRoadCurvatureTag);
        rSRoadCurvatureEnc.setEnum(false, edgeId, edgeIntAccess, rsroadcurvature);

    }
}
 // End block - Added by KJ for RideSense 16062024