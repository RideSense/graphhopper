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

package com.graphhopper.routing.ev;

import com.graphhopper.routing.util.CurvatureCalculator;
import com.graphhopper.routing.util.FerrySpeedCalculator;
import com.graphhopper.routing.util.PriorityCode;
import com.graphhopper.routing.util.SlopeCalculator;
import com.graphhopper.routing.util.TransportationMode;
import com.graphhopper.routing.util.parsers.BikeAccessParser;
import com.graphhopper.routing.util.parsers.BikeAverageSpeedParser;
import com.graphhopper.routing.util.parsers.BikePriorityParser;
import com.graphhopper.routing.util.parsers.CarAccessParser;
import com.graphhopper.routing.util.parsers.CarAverageSpeedParser;
import com.graphhopper.routing.util.parsers.CountryParser;
import com.graphhopper.routing.util.parsers.FootAccessParser;
import com.graphhopper.routing.util.parsers.FootAverageSpeedParser;
import com.graphhopper.routing.util.parsers.FootPriorityParser;
import com.graphhopper.routing.util.parsers.MaxWeightExceptParser;
import com.graphhopper.routing.util.parsers.ModeAccessParser;
import com.graphhopper.routing.util.parsers.MountainBikeAccessParser;
import com.graphhopper.routing.util.parsers.MountainBikeAverageSpeedParser;
import com.graphhopper.routing.util.parsers.MountainBikePriorityParser;
import com.graphhopper.routing.util.parsers.OSMCrossingParser;
import com.graphhopper.routing.util.parsers.OSMFootwayParser;
import com.graphhopper.routing.util.parsers.OSMGetOffBikeParser;
import com.graphhopper.routing.util.parsers.OSMHazmatParser;
import com.graphhopper.routing.util.parsers.OSMHazmatTunnelParser;
import com.graphhopper.routing.util.parsers.OSMHazmatWaterParser;
import com.graphhopper.routing.util.parsers.OSMHgvParser;
import com.graphhopper.routing.util.parsers.OSMHikeRatingParser;
import com.graphhopper.routing.util.parsers.OSMHorseRatingParser;
import com.graphhopper.routing.util.parsers.OSMLanesParser;
import com.graphhopper.routing.util.parsers.OSMMaxAxleLoadParser;
import com.graphhopper.routing.util.parsers.OSMMaxHeightParser;
import com.graphhopper.routing.util.parsers.OSMMaxLengthParser;
import com.graphhopper.routing.util.parsers.OSMMaxSpeedParser;
import com.graphhopper.routing.util.parsers.OSMMaxWeightParser;
import com.graphhopper.routing.util.parsers.OSMMaxWidthParser;
import com.graphhopper.routing.util.parsers.OSMMtbRatingParser;
import com.graphhopper.routing.util.parsers.OSMRoadAccessParser;
import com.graphhopper.routing.util.parsers.OSMRoadClassLinkParser;
import com.graphhopper.routing.util.parsers.OSMRoadClassParser;
import com.graphhopper.routing.util.parsers.OSMRoadEnvironmentParser;
import com.graphhopper.routing.util.parsers.OSMRoundaboutParser;
import com.graphhopper.routing.util.parsers.OSMSmoothnessParser;
import com.graphhopper.routing.util.parsers.OSMSurfaceParser;
import com.graphhopper.routing.util.parsers.OSMTemporalAccessParser;
import com.graphhopper.routing.util.parsers.OSMTollParser;
import com.graphhopper.routing.util.parsers.OSMTrackTypeParser;
import com.graphhopper.routing.util.parsers.OSMWayIDParser;
import com.graphhopper.routing.util.parsers.OrientationCalculator;
import com.graphhopper.routing.util.parsers.RSBikeAccessParser;
import com.graphhopper.routing.util.parsers.RSBuildPercParser;
import com.graphhopper.routing.util.parsers.RSPopulationDensityParser;
import com.graphhopper.routing.util.parsers.RSRoadClassificationParser;
import com.graphhopper.routing.util.parsers.RSRoadClassificationv2Parser;
import com.graphhopper.routing.util.parsers.RSRoadCurvatureParser;
import com.graphhopper.routing.util.parsers.RSSceneryBackwaterParser;
import com.graphhopper.routing.util.parsers.RSSceneryBeachParser;
import com.graphhopper.routing.util.parsers.RSSceneryDesertParser;
import com.graphhopper.routing.util.parsers.RSSceneryFieldParser;
import com.graphhopper.routing.util.parsers.RSSceneryForestParser;
import com.graphhopper.routing.util.parsers.RSSceneryHillParser;
import com.graphhopper.routing.util.parsers.RSSceneryLakeParser;
import com.graphhopper.routing.util.parsers.RSSceneryMountainPassParser;
import com.graphhopper.routing.util.parsers.RSSceneryPlantationParser;
import com.graphhopper.routing.util.parsers.RSSceneryRiverParser;
import com.graphhopper.routing.util.parsers.RSScenerySaltFlatParser;
import com.graphhopper.routing.util.parsers.RSScenerySemiUrbanParser;
import com.graphhopper.routing.util.parsers.RSScenerySnowCappedMountainParser;
import com.graphhopper.routing.util.parsers.RSSceneryUrbanParser;
import com.graphhopper.routing.util.parsers.RacingBikeAccessParser;
import com.graphhopper.routing.util.parsers.RacingBikeAverageSpeedParser;
import com.graphhopper.routing.util.parsers.RacingBikePriorityParser;
import com.graphhopper.routing.util.parsers.StateParser;
import com.graphhopper.util.PMap;

public class DefaultImportRegistry implements ImportRegistry {
    @Override
    public ImportUnit createImportUnit(String name) {
        if (Roundabout.KEY.equals(name))
            return ImportUnit.create(name, props -> Roundabout.create(),
                    (lookup, props) -> new OSMRoundaboutParser(
                            lookup.getBooleanEncodedValue(Roundabout.KEY))
            );
        else if (GetOffBike.KEY.equals(name))
            return ImportUnit.create(name, props -> GetOffBike.create(),
                    (lookup, pros) -> new OSMGetOffBikeParser(
                            lookup.getBooleanEncodedValue(GetOffBike.KEY),
                            lookup.getBooleanEncodedValue("bike_access")
                    ), "bike_access");
        else if (RoadClass.KEY.equals(name))
            return ImportUnit.create(name, props -> RoadClass.create(),
                    (lookup, props) -> new OSMRoadClassParser(
                            lookup.getEnumEncodedValue(RoadClass.KEY, RoadClass.class))
            );
        else if (RoadClassLink.KEY.equals(name))
            return ImportUnit.create(name, props -> RoadClassLink.create(),
                    (lookup, props) -> new OSMRoadClassLinkParser(
                            lookup.getBooleanEncodedValue(RoadClassLink.KEY))
            );
        else if (RoadEnvironment.KEY.equals(name))
            return ImportUnit.create(name, props -> RoadEnvironment.create(),
                    (lookup, props) -> new OSMRoadEnvironmentParser(
                            lookup.getEnumEncodedValue(RoadEnvironment.KEY, RoadEnvironment.class))
            );
        else if (FootRoadAccess.KEY.equals(name))
            return ImportUnit.create(name, props -> FootRoadAccess.create(),
                    (lookup, props) -> new OSMRoadAccessParser<>(
                            lookup.getEnumEncodedValue(FootRoadAccess.KEY, FootRoadAccess.class),
                            OSMRoadAccessParser.toOSMRestrictions(TransportationMode.FOOT),
                            (readerWay, accessValue) -> accessValue,
                            FootRoadAccess::find)
            );
        else if (BikeRoadAccess.KEY.equals(name))
            return ImportUnit.create(name, props -> BikeRoadAccess.create(),
                    (lookup, props) -> new OSMRoadAccessParser<>(
                            lookup.getEnumEncodedValue(BikeRoadAccess.KEY, BikeRoadAccess.class),
                            OSMRoadAccessParser.toOSMRestrictions(TransportationMode.BIKE),
                            (readerWay, accessValue) -> accessValue,
                            BikeRoadAccess::find)
            );
        else if (RoadAccess.KEY.equals(name))
            return ImportUnit.create(name, props -> RoadAccess.create(),
                    (lookup, props) -> new OSMRoadAccessParser<>(
                            lookup.getEnumEncodedValue(RoadAccess.KEY, RoadAccess.class),
                            OSMRoadAccessParser.toOSMRestrictions(TransportationMode.CAR),
                            RoadAccess::countryHook, RoadAccess::find)
            );
        else if (MaxSpeed.KEY.equals(name))
            return ImportUnit.create(name, props -> MaxSpeed.create(),
                    (lookup, props) -> new OSMMaxSpeedParser(
                            lookup.getDecimalEncodedValue(MaxSpeed.KEY))
            );
        else if (MaxSpeedEstimated.KEY.equals(name))
            return ImportUnit.create(name, props -> MaxSpeedEstimated.create(),
                    null, Country.KEY, UrbanDensity.KEY);
        else if (UrbanDensity.KEY.equals(name))
            return ImportUnit.create(name, props -> UrbanDensity.create(),
                    null);
        else if (MaxWeight.KEY.equals(name))
            return ImportUnit.create(name, props -> MaxWeight.create(),
                    (lookup, props) -> new OSMMaxWeightParser(
                            lookup.getDecimalEncodedValue(MaxWeight.KEY))
            );
        else if (MaxWeightExcept.KEY.equals(name))
            return ImportUnit.create(name, props -> MaxWeightExcept.create(),
                    (lookup, props) -> new MaxWeightExceptParser(
                            lookup.getEnumEncodedValue(MaxWeightExcept.KEY, MaxWeightExcept.class))
            );
        else if (MaxHeight.KEY.equals(name))
            return ImportUnit.create(name, props -> MaxHeight.create(),
                    (lookup, props) -> new OSMMaxHeightParser(
                            lookup.getDecimalEncodedValue(MaxHeight.KEY))
            );
        else if (MaxWidth.KEY.equals(name))
            return ImportUnit.create(name, props -> MaxWidth.create(),
                    (lookup, props) -> new OSMMaxWidthParser(
                            lookup.getDecimalEncodedValue(MaxWidth.KEY))
            );
        else if (MaxAxleLoad.KEY.equals(name))
            return ImportUnit.create(name, props -> MaxAxleLoad.create(),
                    (lookup, props) -> new OSMMaxAxleLoadParser(
                            lookup.getDecimalEncodedValue(MaxAxleLoad.KEY))
            );
        else if (MaxLength.KEY.equals(name))
            return ImportUnit.create(name, props -> MaxLength.create(),
                    (lookup, props) -> new OSMMaxLengthParser(
                            lookup.getDecimalEncodedValue(MaxLength.KEY))
            );
        else if (Orientation.KEY.equals(name))
            return ImportUnit.create(name, props -> Orientation.create(),
                    (lookup, props) -> new OrientationCalculator(
                            lookup.getDecimalEncodedValue(Orientation.KEY))
            );
        else if (Surface.KEY.equals(name))
            return ImportUnit.create(name, props -> Surface.create(),
                    (lookup, props) -> new OSMSurfaceParser(
                            lookup.getEnumEncodedValue(Surface.KEY, Surface.class))
            );
        else if (Smoothness.KEY.equals(name))
            return ImportUnit.create(name, props -> Smoothness.create(),
                    (lookup, props) -> new OSMSmoothnessParser(
                            lookup.getEnumEncodedValue(Smoothness.KEY, Smoothness.class))
            );
        else if (Hgv.KEY.equals(name))
            return ImportUnit.create(name, props -> Hgv.create(),
                    (lookup, props) -> new OSMHgvParser(
                            lookup.getEnumEncodedValue(Hgv.KEY, Hgv.class)
                    ));
        else if (Toll.KEY.equals(name))
            return ImportUnit.create(name, props -> Toll.create(),
                    (lookup, props) -> new OSMTollParser(
                            lookup.getEnumEncodedValue(Toll.KEY, Toll.class))
            );
        else if (TrackType.KEY.equals(name))
            return ImportUnit.create(name, props -> TrackType.create(),
                    (lookup, props) -> new OSMTrackTypeParser(
                            lookup.getEnumEncodedValue(TrackType.KEY, TrackType.class))
            );
        else if (Hazmat.KEY.equals(name))
            return ImportUnit.create(name, props -> Hazmat.create(),
                    (lookup, props) -> new OSMHazmatParser(
                            lookup.getEnumEncodedValue(Hazmat.KEY, Hazmat.class))
            );
        else if (HazmatTunnel.KEY.equals(name))
            return ImportUnit.create(name, props -> HazmatTunnel.create(),
                    (lookup, props) -> new OSMHazmatTunnelParser(
                            lookup.getEnumEncodedValue(HazmatTunnel.KEY, HazmatTunnel.class))
            );
        else if (HazmatWater.KEY.equals(name))
            return ImportUnit.create(name, props -> HazmatWater.create(),
                    (lookup, props) -> new OSMHazmatWaterParser(
                            lookup.getEnumEncodedValue(HazmatWater.KEY, HazmatWater.class))
            );
        else if (Lanes.KEY.equals(name))
            return ImportUnit.create(name, props -> Lanes.create(),
                    (lookup, props) -> new OSMLanesParser(
                            lookup.getIntEncodedValue(Lanes.KEY))
            );
        else if (Footway.KEY.equals(name))
            return ImportUnit.create(name, props -> Footway.create(),
                    (lookup, props) -> new OSMFootwayParser(
                            lookup.getEnumEncodedValue(Footway.KEY, Footway.class))
            );
        // Start block - Added by KJ for RideSense 22062024
        else if (RSRoadCurvature.KEY.equals(name))
            return ImportUnit.create(name, props -> RSRoadCurvature.create(),
                    (lookup, props) -> new RSRoadCurvatureParser(
                            lookup.getEnumEncodedValue(RSRoadCurvature.KEY, RSRoadCurvature.class))
            );
            else if (RSBuildPerc.KEY.equals(name))
            return ImportUnit.create(name, props -> RSBuildPerc.create(),
                    (lookup, props) -> new RSBuildPercParser(
                            lookup.getDecimalEncodedValue(RSBuildPerc.KEY))
            );
            else if (RSPopulationDensity.KEY.equals(name))
            return ImportUnit.create(name, props -> RSPopulationDensity.create(),
                    (lookup, props) -> new RSPopulationDensityParser(
                            lookup.getDecimalEncodedValue(RSPopulationDensity.KEY))
            );
            else if (RSBikeAccess.KEY.equals(name))
            return ImportUnit.create(name, props -> RSBikeAccess.create(),
                    (lookup, props) -> new RSBikeAccessParser(
                            lookup.getEnumEncodedValue(RSBikeAccess.KEY, RSBikeAccess.class))
            );            
            else if (RSRoadClassification.KEY.equals(name))
            return ImportUnit.create(name, props -> RSRoadClassification.create(),
                    (lookup, props) -> new RSRoadClassificationParser(
                            lookup.getEnumEncodedValue(RSRoadClassification.KEY, RSRoadClassification.class))
            );
            else if (RSRoadClassificationv2.KEY.equals(name))
            return ImportUnit.create(name, props -> RSRoadClassificationv2.create(),
                    (lookup, props) -> new RSRoadClassificationv2Parser(
                           lookup.getEnumEncodedValue(RSRoadClassificationv2.KEY, RSRoadClassificationv2.class))
            );            
            else if (RSSceneryUrban.KEY.equals(name))
            return ImportUnit.create(name, props -> RSSceneryUrban.create(),
                    (lookup, props) -> new RSSceneryUrbanParser(
                            lookup.getIntEncodedValue(RSSceneryUrban.KEY))
            );
            else if (RSSceneryForest.KEY.equals(name))
            return ImportUnit.create(name, props -> RSSceneryForest.create(),
                    (lookup, props) -> new RSSceneryForestParser(
                            lookup.getIntEncodedValue(RSSceneryForest.KEY))
            );
            else if (RSSceneryHill.KEY.equals(name))
            return ImportUnit.create(name, props -> RSSceneryHill.create(),
                    (lookup, props) -> new RSSceneryHillParser(
                            lookup.getIntEncodedValue(RSSceneryHill.KEY))
            );
            else if (RSSceneryLake.KEY.equals(name))
            return ImportUnit.create(name, props -> RSSceneryLake.create(),
                    (lookup, props) -> new RSSceneryLakeParser(
                            lookup.getIntEncodedValue(RSSceneryLake.KEY))
            );
            else if (RSSceneryBeach.KEY.equals(name))
            return ImportUnit.create(name, props -> RSSceneryBeach.create(),
                    (lookup, props) -> new RSSceneryBeachParser(
                            lookup.getIntEncodedValue(RSSceneryBeach.KEY))
            );
            else if (RSSceneryRiver.KEY.equals(name))
            return ImportUnit.create(name, props -> RSSceneryRiver.create(),
                    (lookup, props) -> new RSSceneryRiverParser(
                            lookup.getIntEncodedValue(RSSceneryRiver.KEY))
            );
            else if (RSSceneryDesert.KEY.equals(name))
            return ImportUnit.create(name, props -> RSSceneryDesert.create(),
                    (lookup, props) -> new RSSceneryDesertParser(
                            lookup.getIntEncodedValue(RSSceneryDesert.KEY))
            );
            else if (RSScenerySnowCappedMountain.KEY.equals(name))
            return ImportUnit.create(name, props -> RSScenerySnowCappedMountain.create(),
                    (lookup, props) -> new RSScenerySnowCappedMountainParser(
                            lookup.getIntEncodedValue(RSScenerySnowCappedMountain.KEY))
            );
            else if (RSSceneryField.KEY.equals(name))
            return ImportUnit.create(name, props -> RSSceneryField.create(),
                    (lookup, props) -> new RSSceneryFieldParser(
                            lookup.getIntEncodedValue(RSSceneryField.KEY))
            );
            else if (RSSceneryPlantation.KEY.equals(name))
            return ImportUnit.create(name, props -> RSSceneryPlantation.create(),
                    (lookup, props) -> new RSSceneryPlantationParser(
                            lookup.getIntEncodedValue(RSSceneryPlantation.KEY))
            );
            else if (RSScenerySaltFlat.KEY.equals(name))
            return ImportUnit.create(name, props -> RSScenerySaltFlat.create(),
                    (lookup, props) -> new RSScenerySaltFlatParser(
                            lookup.getIntEncodedValue(RSScenerySaltFlat.KEY))
            );
            else if (RSScenerySemiUrban.KEY.equals(name))
            return ImportUnit.create(name, props -> RSScenerySemiUrban.create(),
                    (lookup, props) -> new RSScenerySemiUrbanParser(
                            lookup.getIntEncodedValue(RSScenerySemiUrban.KEY))
            );
            else if (RSSceneryBackwater.KEY.equals(name))
            return ImportUnit.create(name, props -> RSSceneryBackwater.create(),
                    (lookup, props) -> new RSSceneryBackwaterParser(
                            lookup.getIntEncodedValue(RSSceneryBackwater.KEY))
            );
            else if (RSSceneryMountainPass.KEY.equals(name))
            return ImportUnit.create(name, props -> RSSceneryMountainPass.create(),
                    (lookup, props) -> new RSSceneryMountainPassParser(
                            lookup.getIntEncodedValue(RSSceneryMountainPass.KEY))
            );
            // End block - Added by KJ for RideSense 22062024            
        else if (OSMWayID.KEY.equals(name))
            return ImportUnit.create(name, props -> OSMWayID.create(),
                    (lookup, props) -> new OSMWayIDParser(
                            lookup.getIntEncodedValue(OSMWayID.KEY))
            );
        else if (MtbRating.KEY.equals(name))
            return ImportUnit.create(name, props -> MtbRating.create(),
                    (lookup, props) -> new OSMMtbRatingParser(
                            lookup.getIntEncodedValue(MtbRating.KEY))
            );
        else if (HikeRating.KEY.equals(name))
            return ImportUnit.create(name, props -> HikeRating.create(),
                    (lookup, props) -> new OSMHikeRatingParser(
                            lookup.getIntEncodedValue(HikeRating.KEY))
            );
        else if (HorseRating.KEY.equals(name))
            return ImportUnit.create(name, props -> HorseRating.create(),
                    (lookup, props) -> new OSMHorseRatingParser(
                            lookup.getIntEncodedValue(HorseRating.KEY))
            );
        else if (Country.KEY.equals(name))
            return ImportUnit.create(name, props -> Country.create(),
                    (lookup, props) -> new CountryParser(
                            lookup.getEnumEncodedValue(Country.KEY, Country.class))
            );
        else if (State.KEY.equals(name))
            return ImportUnit.create(name, props -> State.create(),
                    (lookup, props) -> new StateParser(
                            lookup.getEnumEncodedValue(State.KEY, State.class))
            );
        else if (Crossing.KEY.equals(name))
            return ImportUnit.create(name, props -> Crossing.create(),
                    (lookup, props) -> new OSMCrossingParser(
                            lookup.getEnumEncodedValue(Crossing.KEY, Crossing.class))
            );
        else if (FerrySpeed.KEY.equals(name))
            return ImportUnit.create(name, props -> FerrySpeed.create(),
                    (lookup, props) -> new FerrySpeedCalculator(
                            lookup.getDecimalEncodedValue(FerrySpeed.KEY)));
        else if (Curvature.KEY.equals(name))
            return ImportUnit.create(name, props -> Curvature.create(),
                    (lookup, props) -> new CurvatureCalculator(
                            lookup.getDecimalEncodedValue(Curvature.KEY))
            );
        else if (AverageSlope.KEY.equals(name))
            return ImportUnit.create(name, props -> AverageSlope.create(), null, "slope_calculator");
        else if (MaxSlope.KEY.equals(name))
            return ImportUnit.create(name, props -> MaxSlope.create(), null, "slope_calculator");
        else if ("slope_calculator".equals(name))
            return ImportUnit.create(name, null,
                    (lookup, props) -> new SlopeCalculator(
                            lookup.hasEncodedValue(MaxSlope.KEY) ? lookup.getDecimalEncodedValue(MaxSlope.KEY) : null,
                            lookup.hasEncodedValue(AverageSlope.KEY) ? lookup.getDecimalEncodedValue(AverageSlope.KEY) : null
                    ));
        else if (BikeNetwork.KEY.equals(name) || MtbNetwork.KEY.equals(name) || FootNetwork.KEY.equals(name))
            return ImportUnit.create(name, props -> RouteNetwork.create(name), null);

        else if (BusAccess.KEY.equals(name))
            return ImportUnit.create(name, props -> BusAccess.create(),
                    (lookup, props) -> new ModeAccessParser(OSMRoadAccessParser.toOSMRestrictions(TransportationMode.BUS),
                            lookup.getBooleanEncodedValue(name), true, lookup.getBooleanEncodedValue(Roundabout.KEY),
                            PMap.toSet(props.getString("restrictions", "")), PMap.toSet(props.getString("barriers", ""))),
                    "roundabout"
            );

        else if (HovAccess.KEY.equals(name))
            return ImportUnit.create(name, props -> HovAccess.create(),
                    (lookup, props) -> new ModeAccessParser(OSMRoadAccessParser.toOSMRestrictions(TransportationMode.HOV),
                            lookup.getBooleanEncodedValue(name), true, lookup.getBooleanEncodedValue(Roundabout.KEY),
                            PMap.toSet(props.getString("restrictions", "")), PMap.toSet(props.getString("barriers", ""))),
                    "roundabout"
            );
        else if (FootTemporalAccess.KEY.equals(name))
            return ImportUnit.create(name, props -> FootTemporalAccess.create(),
                    (lookup, props) -> {
                        EnumEncodedValue<FootTemporalAccess> enc = lookup.getEnumEncodedValue(FootTemporalAccess.KEY, FootTemporalAccess.class);
                        OSMTemporalAccessParser.Setter fct = (edgeId, edgeIntAccess, b) -> enc.setEnum(false, edgeId, edgeIntAccess, b ? FootTemporalAccess.YES : FootTemporalAccess.NO);
                        return new OSMTemporalAccessParser(FootTemporalAccess.CONDITIONALS, fct, props.getString("date_range_parser_day", ""));
                    }
            );

        else if (BikeTemporalAccess.KEY.equals(name))
            return ImportUnit.create(name, props -> BikeTemporalAccess.create(),
                    (lookup, props) -> {
                        EnumEncodedValue<BikeTemporalAccess> enc = lookup.getEnumEncodedValue(BikeTemporalAccess.KEY, BikeTemporalAccess.class);
                        OSMTemporalAccessParser.Setter fct = (edgeId, edgeIntAccess, b) -> enc.setEnum(false, edgeId, edgeIntAccess, b ? BikeTemporalAccess.YES : BikeTemporalAccess.NO);
                        return new OSMTemporalAccessParser(BikeTemporalAccess.CONDITIONALS, fct, props.getString("date_range_parser_day", ""));
                    }
            );

        else if (CarTemporalAccess.KEY.equals(name))
            return ImportUnit.create(name, props -> CarTemporalAccess.create(),
                    (lookup, props) -> {
                        EnumEncodedValue<CarTemporalAccess> enc = lookup.getEnumEncodedValue(CarTemporalAccess.KEY, CarTemporalAccess.class);
                        OSMTemporalAccessParser.Setter fct = (edgeId, edgeIntAccess, b) -> enc.setEnum(false, edgeId, edgeIntAccess, b ? CarTemporalAccess.YES : CarTemporalAccess.NO);
                        return new OSMTemporalAccessParser(CarTemporalAccess.CONDITIONALS, fct, props.getString("date_range_parser_day", ""));
                    }
            );

        else if (VehicleAccess.key("car").equals(name))
            return ImportUnit.create(name, props -> VehicleAccess.create("car"),
                    CarAccessParser::new,
                    "roundabout"
            );
        else if (VehicleAccess.key("roads").equals(name))
            throw new IllegalArgumentException("roads_access parser no longer necessary, see docs/migration/config-migration-08-09.md");
        else if (VehicleAccess.key("bike").equals(name))
            return ImportUnit.create(name, props -> VehicleAccess.create("bike"),
                    BikeAccessParser::new,
                    "roundabout"
            );
        else if (VehicleAccess.key("racingbike").equals(name))
            return ImportUnit.create(name, props -> VehicleAccess.create("racingbike"),
                    RacingBikeAccessParser::new,
                    "roundabout"
            );
        else if (VehicleAccess.key("mtb").equals(name))
            return ImportUnit.create(name, props -> VehicleAccess.create("mtb"),
                    MountainBikeAccessParser::new,
                    "roundabout"
            );
        else if (VehicleAccess.key("foot").equals(name))
            return ImportUnit.create(name, props -> VehicleAccess.create("foot"),
                    FootAccessParser::new);

        else if (VehicleSpeed.key("car").equals(name))
            return ImportUnit.create(name, props -> new DecimalEncodedValueImpl(
                            name, props.getInt("speed_bits", 7), props.getDouble("speed_factor", 2), true),
                    (lookup, props) -> new CarAverageSpeedParser(lookup),
                    "ferry_speed"
            );
        else if (VehicleSpeed.key("roads").equals(name))
            throw new IllegalArgumentException("roads_average_speed parser no longer necessary, see docs/migration/config-migration-08-09.md");
        else if (VehicleSpeed.key("bike").equals(name))
            return ImportUnit.create(name, props -> new DecimalEncodedValueImpl(
                            name, props.getInt("speed_bits", 4), props.getDouble("speed_factor", 2), false),
                    (lookup, props) -> new BikeAverageSpeedParser(lookup),
                    "ferry_speed", "smoothness"
            );
        else if (VehicleSpeed.key("racingbike").equals(name))
            return ImportUnit.create(name, props -> new DecimalEncodedValueImpl(
                            name, props.getInt("speed_bits", 4), props.getDouble("speed_factor", 2), false),
                    (lookup, props) -> new RacingBikeAverageSpeedParser(lookup),
                    "ferry_speed", "smoothness"
            );
        else if (VehicleSpeed.key("mtb").equals(name))
            return ImportUnit.create(name, props -> new DecimalEncodedValueImpl(
                            name, props.getInt("speed_bits", 4), props.getDouble("speed_factor", 2), false),
                    (lookup, props) -> new MountainBikeAverageSpeedParser(lookup),
                    "ferry_speed", "smoothness"
            );
        else if (VehicleSpeed.key("foot").equals(name))
            return ImportUnit.create(name, props -> new DecimalEncodedValueImpl(
                            name, props.getInt("speed_bits", 4), props.getDouble("speed_factor", 1), false),
                    (lookup, props) -> new FootAverageSpeedParser(lookup),
                    "ferry_speed"
            );
        else if (VehiclePriority.key("foot").equals(name))
            return ImportUnit.create(name, props -> VehiclePriority.create("foot", 4, PriorityCode.getFactor(1), false),
                    (lookup, props) -> new FootPriorityParser(lookup),
                    RouteNetwork.key("foot")
            );
        else if (VehiclePriority.key("bike").equals(name))
            return ImportUnit.create(name, props -> VehiclePriority.create("bike", 4, PriorityCode.getFactor(1), false),
                    (lookup, props) -> new BikePriorityParser(lookup),
                    VehicleSpeed.key("bike"), BikeNetwork.KEY
            );
        else if (VehiclePriority.key("racingbike").equals(name))
            return ImportUnit.create(name, props -> VehiclePriority.create("racingbike", 4, PriorityCode.getFactor(1), false),
                    (lookup, props) -> new RacingBikePriorityParser(lookup),
                    VehicleSpeed.key("racingbike"), BikeNetwork.KEY
            );
        else if (VehiclePriority.key("mtb").equals(name))
            return ImportUnit.create(name, props -> VehiclePriority.create("mtb", 4, PriorityCode.getFactor(1), false),
                    (lookup, props) -> new MountainBikePriorityParser(lookup),
                    VehicleSpeed.key("mtb"), BikeNetwork.KEY
            );
        return null;
    }
}
