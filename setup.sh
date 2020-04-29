#!/bin/bash

set -eux

python3 -m pip install --upgrade --user pip setuptools virtualenv
python3 -m virtualenv ~/kivy_venv
source ~/kivy_venv/bin/activate
python3 -m pip install kivy
python3 -m pip install kivy_examples
