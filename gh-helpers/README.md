# GraphHopper API Tester

A Streamlit web application to test all GraphHopper API endpoints running on localhost:8989.

## Features

- Test all GraphHopper endpoints:
  - **Route (GET)**: Calculate routes with query parameters
  - **Route (POST)**: Calculate routes with JSON body
  - **Match**: Map matching with GPX data
  - **Isochrone**: Calculate reachable areas
  - **Nearest**: Find nearest point on road network
  - **Info**: Get server information
  - **SPT**: Get shortest path tree
  - **Health**: Health check endpoint

- Pre-populated sample request bodies for each endpoint
- Real-time JSON response display
- Editable request parameters
- Connection status checking
- **Custom Model Support**: Use custom routing models at runtime with the POST /route endpoint
- **Logging**: All requests and responses are logged to timestamped files in the `logs/` directory

## Prerequisites

- Python virtual environment (`.venv`) in the project root
- GraphHopper server running on `localhost:8989`
- Required packages: `streamlit`, `requests`

## Installation

Install dependencies:

```bash
./run_with_venv.sh pip install -r graphhopper/gh-helpers/requirements.txt
```

## Usage

Run the Streamlit app from the project root:

```bash
./run_with_venv.sh graphhopper/gh-helpers/run_streamlit.sh
```

Or manually:

```bash
cd graphhopper/gh-helpers
./run_with_venv.sh streamlit run streamlit_app.py
```

Then open your browser to the URL shown in the terminal (typically `http://localhost:8501`).

## Configuration

The app connects to `http://localhost:8989` by default. To change this, edit the `BASE_URL` constant in `streamlit_app.py`.

## Endpoints Tested

1. **GET /route** - Calculate route between points
2. **POST /route** - Calculate route with JSON body (supports custom models)
3. **POST /match** - Map matching with GPX XML
4. **GET /isochrone** - Calculate isochrone polygons
5. **GET /nearest** - Find nearest point on road
6. **GET /info** - Get server information and available profiles
7. **GET /spt** - Get shortest path tree (CSV format)
8. **GET /health** - Health check

Each endpoint has pre-populated sample data that you can edit before sending the request.

## Custom Model Support

The **Route (POST)** tab supports using custom routing models at runtime:

1. Check the **"Use Custom Model"** checkbox
2. Enter your custom model JSON in the text area (a default empty model is provided)
3. The app will automatically:
   - Set the profile to `"empty"`
   - Disable Contraction Hierarchies (`ch.disable: true`)
   - Include your custom model in the request body
4. Click **"Send POST Request"** to execute

**Note**: Custom models can only be used with POST /route requests, not GET requests. The custom model JSON should follow GraphHopper's custom model format with properties like `speed`, `priority`, and `distance_influence`.

Example custom model structure:
```json
{
  "distance_influence": 20,
  "priority": [
    { "if": "road_class == MOTORWAY", "multiply_by": "0" }
  ],
  "speed": [
    { "if": "true", "limit_to": "120" }
  ]
}
```

## Logging

All API requests and responses are automatically logged to timestamped files in the `logs/` directory. Log files are named `streamlit_app_YYYYMMDD_HHMMSS.log` and include:
- Request details (endpoint, method, body)
- Response status codes
- Error messages and warnings

This helps with debugging and tracking API usage over time.
