#!/bin/bash

set -eu

readonly modules="$(cat modules.txt | tr \\n ,)"

jlink \
  --module-path "/usr/lib/jvm/javafx-jmods-11.0.2/" \
  --add-modules "$modules" \
  --compress=2 \
  --output jre
