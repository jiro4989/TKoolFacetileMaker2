#!/bin/bash

set -eux

readonly modules="$(cat modules.txt | tr -d '\r' | tr \\n ,)"
readonly module_path="${1:-/usr/lib/jvm/javafx-jmods-11.0.2/}"

jlink \
  --module-path "$module_path" \
  --add-modules "$modules" \
  --compress=2 \
  --output jre
