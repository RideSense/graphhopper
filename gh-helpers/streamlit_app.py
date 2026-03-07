#!/usr/bin/env python3
"""
Streamlit app to test GraphHopper API endpoints
Connects to GraphHopper server at localhost:8989
"""

import streamlit as st
import requests
import json
from typing import Dict, Any, Optional, Tuple
from datetime import datetime
import os
import logging

# Configuration
BASE_URL = "http://localhost:8989"
TIMEOUT = 30  # seconds

# Setup logging
LOGS_DIR = os.path.join(os.path.dirname(__file__), "logs")
os.makedirs(LOGS_DIR, exist_ok=True)
log_filename = f"streamlit_app_{datetime.now().strftime('%Y%m%d_%H%M%S')}.log"
log_filepath = os.path.join(LOGS_DIR, log_filename)

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler(log_filepath),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

# Sample request bodies for each endpoint
SAMPLE_REQUESTS = {
    "route": {
        "GET": {
            "description": "GET /route - Calculate route between points",
            "params": {
                "point": ["12.9716,77.5946", "28.6139,77.2090"],  # Bangalore to Delhi
                "profile": "motorcycle",
                "instructions": "true",
                "calc_points": "true",
                "points_encoded": "true",
                "locale": "en"
            }
        },
        "POST": {
            "description": "POST /route - Calculate route with JSON body",
            "body": {
                "points": [[77.5946, 12.9716], [77.2090, 28.6139]],  # Bangalore to Delhi [lon, lat]
                "profile": "motorcycle",
                "instructions": True,
                "calc_points": True,
                "points_encoded": True,
                "locale": "en"
            }
        }
    },
    "match": {
        "POST": {
            "description": "POST /match - Map matching with GPX data",
            "body": "",  # No default GPX data - user should provide their own
            "params": {
                "profile": "motorcycle",
                "type": "json",
                "instructions": "true",
                "gps_accuracy": "10"
            }
        }
    },
    "isochrone": {
        "GET": {
            "description": "GET /isochrone - Calculate isochrone from a point",
            "params": {
                "point": "12.9716,77.5946",  # Bangalore
                "profile": "motorcycle",
                "time_limit": "3600",  # 1 hour
                "buckets": "1",
                "type": "json"
            }
        }
    },
    "nearest": {
        "GET": {
            "description": "GET /nearest - Find nearest point on road",
            "params": {
                "point": "23.5,77.5",  # Center of India
                "elevation": "false"
            }
        }
    },
    "info": {
        "GET": {
            "description": "GET /info - Get server information",
            "params": {}
        }
    },
    "spt": {
        "GET": {
            "description": "GET /spt - Get shortest path tree",
            "params": {
                "point": "12.9716,77.5946",  # Bangalore
                "profile": "motorcycle",
                "time_limit": "3600",  # 1 hour
                "columns": "node_id,time,distance"
            }
        }
    },
    "health": {
        "GET": {
            "description": "GET /health - Health check",
            "params": {}
        }
    }
}

def make_request(method: str, endpoint: str, params: Optional[Dict] = None, 
                 json_body: Optional[Dict] = None, xml_body: Optional[str] = None,
                 headers: Optional[Dict] = None) -> Tuple[Optional[Dict], Optional[str], int]:
    """
    Make HTTP request to GraphHopper API
    
    Returns:
        (response_json, response_text, status_code)
    """
    url = f"{BASE_URL}/{endpoint}"
    
    try:
        if method.upper() == "GET":
            response = requests.get(url, params=params, headers=headers, timeout=TIMEOUT)
        elif method.upper() == "POST":
            if xml_body:
                headers = headers or {}
                headers["Content-Type"] = "application/gpx+xml"
                response = requests.post(url, data=xml_body, params=params, headers=headers, timeout=TIMEOUT)
            else:
                response = requests.post(url, json=json_body, params=params, headers=headers, timeout=TIMEOUT)
        else:
            return None, f"Unsupported method: {method}", 400
        
        # Try to parse as JSON
        try:
            return response.json(), None, response.status_code
        except json.JSONDecodeError:
            return None, response.text, response.status_code
            
    except requests.exceptions.ConnectionError:
        return None, f"Connection error: Could not connect to {BASE_URL}. Is the server running?", 0
    except requests.exceptions.Timeout:
        return None, f"Request timeout after {TIMEOUT} seconds", 0
    except Exception as e:
        return None, f"Error: {str(e)}", 0

def format_json(obj: Any) -> str:
    """Format JSON object as pretty string"""
    return json.dumps(obj, indent=2, ensure_ascii=False)

def main():
    st.set_page_config(
        page_title="GraphHopper API Tester",
        page_icon="🗺️",
        layout="wide"
    )
    
    st.title("🗺️ GraphHopper API Tester")
    st.markdown(f"**Server:** `{BASE_URL}`")
    
    # Check server connection
    with st.spinner("Checking server connection..."):
        info_response, info_error, info_status = make_request("GET", "info")
        if info_status == 200 and info_response:
            st.success("✅ Server is connected and responding")
            if "profiles" in info_response:
                profiles = [p.get("name", "unknown") for p in info_response.get("profiles", [])]
                st.info(f"Available profiles: {', '.join(profiles)}")
        else:
            st.error(f"❌ Cannot connect to server: {info_error or 'Unknown error'}")
            st.warning("Make sure the GraphHopper server is running on localhost:8989")
    
    st.divider()
    
    # Create tabs for each endpoint
    tabs = st.tabs(["Route (GET)", "Route (POST)", "Match", "Isochrone", "Nearest", "Info", "SPT", "Health"])
    
    # Route GET
    with tabs[0]:
        st.header("Route - GET")
        st.markdown("Calculate route between points using GET request")
        
        sample = SAMPLE_REQUESTS["route"]["GET"]
        st.markdown(f"*{sample['description']}*")
        
        col1, col2 = st.columns([2, 1])
        
        with col1:
            # Editable parameters
            points_input = st.text_area(
                "Points (one per line, format: lat,lon)",
                value="\n".join(sample["params"]["point"]),
                height=100,
                key="route_get_points"
            )
            
            profile = st.selectbox(
                "Profile",
                options=["motorcycle", "milemuncher", "cornercraver", "trailblazer", "tranquiltraveller"],
                index=0,
                key="route_get_profile"
            )
            
            instructions = st.checkbox("Instructions", value=True, key="route_get_instructions")
            calc_points = st.checkbox("Calculate Points", value=True, key="route_get_calc_points")
            points_encoded = st.checkbox("Points Encoded", value=True, key="route_get_points_encoded")
            locale = st.text_input("Locale", value="en", key="route_get_locale")
        
        with col2:
            st.markdown("### Additional Options")
            algorithm = st.text_input("Algorithm", value="", key="route_get_algorithm")
            details = st.text_input("Details (comma-separated)", value="", key="route_get_details")
            snap_prevention = st.text_input("Snap Prevention", value="", key="route_get_snap_prevention")
        
        if st.button("Send GET Request", type="primary", key="route_get_button"):
            # Parse points
            points = [p.strip() for p in points_input.split("\n") if p.strip()]
            
            params = {
                "profile": profile,
                "instructions": str(instructions).lower(),
                "calc_points": str(calc_points).lower(),
                "points_encoded": str(points_encoded).lower(),
                "locale": locale
            }
            
            # Add point parameters
            for point in points:
                params.setdefault("point", []).append(point)
            
            # Add optional parameters
            if algorithm:
                params["algorithm"] = algorithm
            if details:
                params["details"] = details.split(",")
            if snap_prevention:
                params["snap_prevention"] = snap_prevention.split(",")
            
            with st.spinner("Sending request..."):
                response_json, response_text, status_code = make_request("GET", "route", params=params)
            
            st.markdown("### Response")
            st.code(f"Status Code: {status_code}")
            
            if response_json:
                st.json(response_json)
            elif response_text:
                st.text_area("Response", response_text, height=400, key="route_get_response")
            else:
                st.error("No response received")
    
    # Route POST
    with tabs[1]:
        st.header("Route - POST")
        st.markdown("Calculate route between points using POST request with JSON body")
        
        sample = SAMPLE_REQUESTS["route"]["POST"]
        st.markdown(f"*{sample['description']}*")
        
        # Custom model option
        use_custom_model = st.checkbox(
            "Use Custom Model",
            value=False,
            key="route_post_use_custom_model",
            help="Enable to use a custom model. This will automatically set profile to 'empty' and disable CH."
        )
        
        if use_custom_model:
            st.info("ℹ️ Custom model mode: Profile will be set to 'empty' and CH will be disabled automatically.")
            
            # Default empty custom model
            default_custom_model = {
                "distance_influence": 20,
                "priority": [],
                "speed": [
                    {"if": "true", "limit_to": "120"}
                ]
            }
            
            custom_model_text = st.text_area(
                "Custom Model (JSON)",
                value=format_json(default_custom_model),
                height=200,
                key="route_post_custom_model",
                help="Enter your custom model JSON. This will be included in the request body."
            )
            
            # Try to parse and validate custom model
            custom_model_json = None
            try:
                custom_model_json = json.loads(custom_model_text)
                st.success("✅ Custom model JSON is valid")
            except json.JSONDecodeError as e:
                st.error(f"❌ Invalid custom model JSON: {str(e)}")
                custom_model_json = None
        
        # Editable JSON body
        default_body = format_json(sample["body"])
        body_text = st.text_area(
            "Request Body (JSON)",
            value=default_body,
            height=300,
            key="route_post_body",
            disabled=use_custom_model,
            help="Request body (disabled when using custom model - will be auto-generated)"
        )
        
        if st.button("Send POST Request", type="primary", key="route_post_button"):
            try:
                if use_custom_model:
                    if custom_model_json is None:
                        st.error("Please fix the custom model JSON errors before sending the request.")
                    else:
                        # Parse the base body to get points and other settings
                        try:
                            base_body = json.loads(body_text)
                        except json.JSONDecodeError:
                            # If base body is invalid, use defaults from sample
                            base_body = sample["body"].copy()
                        
                        # Build request with custom model
                        request_body = {
                            "points": base_body.get("points", sample["body"]["points"]),
                            "profile": "empty",  # Force empty profile for custom models
                            "ch.disable": True,  # Disable CH for custom models
                            "custom_model": custom_model_json,
                            "instructions": base_body.get("instructions", True),
                            "calc_points": base_body.get("calc_points", True),
                            "points_encoded": base_body.get("points_encoded", True),
                            "locale": base_body.get("locale", "en")
                        }
                        
                        # Add optional fields if present
                        if "algorithm" in base_body:
                            request_body["algorithm"] = base_body["algorithm"]
                        if "details" in base_body:
                            request_body["details"] = base_body["details"]
                        if "snap_preventions" in base_body:
                            request_body["snap_preventions"] = base_body["snap_preventions"]
                        
                        logger.info(f"POST /route with custom model - Request: {format_json(request_body)}")
                        
                        with st.spinner("Sending request with custom model..."):
                            response_json, response_text, status_code = make_request("POST", "route", json_body=request_body)
                        
                        logger.info(f"POST /route response - Status: {status_code}")
                        if status_code != 200:
                            logger.warning(f"POST /route error response: {response_text}")
                        
                        st.markdown("### Response")
                        st.code(f"Status Code: {status_code}")
                        
                        if response_json:
                            st.json(response_json)
                        elif response_text:
                            st.text_area("Response", response_text, height=400, key="route_post_response")
                        else:
                            st.error("No response received")
                else:
                    # Standard POST request without custom model
                    body_json = json.loads(body_text)
                    
                    logger.info(f"POST /route - Request: {format_json(body_json)}")
                    
                    with st.spinner("Sending request..."):
                        response_json, response_text, status_code = make_request("POST", "route", json_body=body_json)
                    
                    logger.info(f"POST /route response - Status: {status_code}")
                    if status_code != 200:
                        logger.warning(f"POST /route error response: {response_text}")
                    
                    st.markdown("### Response")
                    st.code(f"Status Code: {status_code}")
                    
                    if response_json:
                        st.json(response_json)
                    elif response_text:
                        st.text_area("Response", response_text, height=400, key="route_post_response")
                    else:
                        st.error("No response received")
                    
            except json.JSONDecodeError as e:
                error_msg = f"Invalid JSON: {str(e)}"
                logger.error(f"JSON decode error: {error_msg}")
                st.error(error_msg)
            except Exception as e:
                error_msg = f"Error: {str(e)}"
                logger.error(f"Request error: {error_msg}")
                st.error(error_msg)
    
    # Match
    with tabs[2]:
        st.header("Map Matching")
        st.markdown("Match GPS track to road network using GPX data")
        
        sample = SAMPLE_REQUESTS["match"]["POST"]
        st.markdown(f"*{sample['description']}*")
        
        col1, col2 = st.columns([2, 1])
        
        with col1:
            gpx_body = st.text_area(
                "GPX Data (XML)",
                value=sample["body"],
                height=300,
                key="match_gpx"
            )
        
        with col2:
            profile = st.selectbox(
                "Profile",
                options=["motorcycle", "milemuncher", "cornercraver", "trailblazer", "tranquiltraveller"],
                index=0,
                key="match_profile"
            )
            
            output_type = st.selectbox("Output Type", options=["json", "gpx", "extended_json"], index=0, key="match_output_type")
            instructions = st.checkbox("Instructions", value=True, key="match_instructions")
            gps_accuracy = st.number_input("GPS Accuracy (meters)", value=10.0, min_value=1.0, max_value=100.0, key="match_gps_accuracy")
        
        if st.button("Send Match Request", type="primary", key="match_button"):
            if not gpx_body or not gpx_body.strip():
                st.error("Please provide GPX data to match")
            else:
                params = {
                    "profile": profile,
                    "type": output_type,
                    "instructions": str(instructions).lower(),
                    "gps_accuracy": str(gps_accuracy)
                }
                
                with st.spinner("Sending request..."):
                    response_json, response_text, status_code = make_request("POST", "match", params=params, xml_body=gpx_body)
                
                st.markdown("### Response")
                st.code(f"Status Code: {status_code}")
                
                if response_json:
                    st.json(response_json)
                elif response_text:
                    st.text_area("Response", response_text, height=400, key="match_response")
                else:
                    st.error("No response received")
    
    # Isochrone
    with tabs[3]:
        st.header("Isochrone")
        st.markdown("Calculate isochrone (reachable area) from a point")
        
        sample = SAMPLE_REQUESTS["isochrone"]["GET"]
        st.markdown(f"*{sample['description']}*")
        
        col1, col2 = st.columns(2)
        
        with col1:
            point = st.text_input("Point (lat,lon)", value=sample["params"]["point"], key="isochrone_point")
            profile = st.selectbox(
                "Profile",
                options=["motorcycle", "milemuncher", "cornercraver", "trailblazer", "tranquiltraveller"],
                index=0,
                key="isochrone_profile"
            )
            time_limit = st.number_input("Time Limit (seconds)", value=3600, min_value=1, max_value=36000, key="isochrone_time_limit")
        
        with col2:
            buckets = st.number_input("Buckets", value=1, min_value=1, max_value=20, key="isochrone_buckets")
            output_type = st.selectbox("Output Type", options=["json", "geojson"], index=0, key="isochrone_output_type")
            distance_limit = st.number_input("Distance Limit (meters, -1 to disable)", value=100000, min_value=-1, key="isochrone_distance_limit")
        
        if st.button("Send Isochrone Request", type="primary", key="isochrone_button"):
            params = {
                "point": point,
                "profile": profile,
                "time_limit": str(time_limit),
                "buckets": str(buckets),
                "type": output_type
            }
            
            if distance_limit > 0:
                params["distance_limit"] = str(distance_limit)
            
            with st.spinner("Sending request..."):
                response_json, response_text, status_code = make_request("GET", "isochrone", params=params)
            
            st.markdown("### Response")
            st.code(f"Status Code: {status_code}")
            
            if response_json:
                st.json(response_json)
            elif response_text:
                st.text_area("Response", response_text, height=400, key="isochrone_response")
            else:
                st.error("No response received")
    
    # Nearest
    with tabs[4]:
        st.header("Nearest")
        st.markdown("Find nearest point on road network")
        
        sample = SAMPLE_REQUESTS["nearest"]["GET"]
        st.markdown(f"*{sample['description']}*")
        
        point = st.text_input("Point (lat,lon)", value=sample["params"]["point"], key="nearest_point")
        elevation = st.checkbox("Include Elevation", value=False, key="nearest_elevation")
        
        if st.button("Send Nearest Request", type="primary", key="nearest_button"):
            params = {
                "point": point,
                "elevation": str(elevation).lower()
            }
            
            with st.spinner("Sending request..."):
                response_json, response_text, status_code = make_request("GET", "nearest", params=params)
            
            st.markdown("### Response")
            st.code(f"Status Code: {status_code}")
            
            if response_json:
                st.json(response_json)
            elif response_text:
                st.text_area("Response", response_text, height=400, key="nearest_response")
            else:
                st.error("No response received")
    
    # Info
    with tabs[5]:
        st.header("Server Info")
        st.markdown("Get information about the GraphHopper server")
        
        sample = SAMPLE_REQUESTS["info"]["GET"]
        st.markdown(f"*{sample['description']}*")
        
        if st.button("Get Server Info", type="primary", key="info_button"):
            with st.spinner("Sending request..."):
                response_json, response_text, status_code = make_request("GET", "info")
            
            st.markdown("### Response")
            st.code(f"Status Code: {status_code}")
            
            if response_json:
                st.json(response_json)
            elif response_text:
                st.text_area("Response", response_text, height=400, key="info_response")
            else:
                st.error("No response received")
    
    # SPT
    with tabs[6]:
        st.header("Shortest Path Tree")
        st.markdown("Get shortest path tree from a point")
        
        sample = SAMPLE_REQUESTS["spt"]["GET"]
        st.markdown(f"*{sample['description']}*")
        
        col1, col2 = st.columns(2)
        
        with col1:
            point = st.text_input("Point (lat,lon)", value=sample["params"]["point"], key="spt_point")
            profile = st.selectbox(
                "Profile",
                options=["motorcycle", "milemuncher", "cornercraver", "trailblazer", "tranquiltraveller"],
                index=0,
                key="spt_profile"
            )
            time_limit = st.number_input("Time Limit (seconds)", value=3600, min_value=1, max_value=36000, key="spt_time_limit")
        
        with col2:
            columns = st.text_input("Columns (comma-separated)", value=sample["params"]["columns"], key="spt_columns")
            reverse_flow = st.checkbox("Reverse Flow", value=False, key="spt_reverse_flow")
            distance_limit = st.number_input("Distance Limit (meters, -1 to disable)", value=-1, min_value=-1, key="spt_distance_limit")
        
        if st.button("Send SPT Request", type="primary", key="spt_button"):
            params = {
                "point": point,
                "profile": profile,
                "time_limit": str(time_limit),
                "columns": columns,
                "reverse_flow": str(reverse_flow).lower()
            }
            
            if distance_limit > 0:
                params["distance_limit"] = str(distance_limit)
            
            with st.spinner("Sending request..."):
                response_json, response_text, status_code = make_request("GET", "spt", params=params)
            
            st.markdown("### Response")
            st.code(f"Status Code: {status_code}")
            
            if response_json:
                st.json(response_json)
            elif response_text:
                st.text_area("Response", response_text, height=400, key="spt_response")
            else:
                st.error("No response received")
    
    # Health
    with tabs[7]:
        st.header("Health Check")
        st.markdown("Check server health status")
        
        sample = SAMPLE_REQUESTS["health"]["GET"]
        st.markdown(f"*{sample['description']}*")
        
        if st.button("Check Health", type="primary", key="health_button"):
            with st.spinner("Sending request..."):
                response_json, response_text, status_code = make_request("GET", "health")
            
            st.markdown("### Response")
            st.code(f"Status Code: {status_code}")
            
            if response_json:
                st.json(response_json)
            elif response_text:
                st.text_area("Response", response_text, height=400, key="health_response")
            else:
                st.error("No response received")

if __name__ == "__main__":
    main()
