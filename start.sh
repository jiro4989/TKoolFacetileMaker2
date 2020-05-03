#!/bin/bash

set -eu

readonly modules="$(cat modules.txt | tr \\n ,)"

java \
  --module-path /usr/lib/jvm/javafx-sdk-11.0.2/lib \
  --add-modules "$modules" \
  -jar build/libs/TKoolFacetileMaker2.jar
