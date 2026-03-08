#!/bin/bash
# Wrapper script to run Streamlit app with the project venv
# Usage: ./run_with_venv.sh graphhopper/gh-helpers/run_streamlit.sh

cd "$(dirname "$0")" || exit 1
exec streamlit run streamlit_app.py
