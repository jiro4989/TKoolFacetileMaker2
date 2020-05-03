#!/bin/bash

set -eu

readonly modules="$(cat modules.txt | tr \\n ,)"
readonly module_path="${1:-/usr/lib/jvm/javafx-jmods-11.0.2/}"

jlink \
  --module-path "$module_path" \
  --add-modules "$modules" \
  --compress=2 \
  --output jre
