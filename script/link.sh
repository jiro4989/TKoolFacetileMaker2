#!/bin/bash

set -eu

jlink \
  --compress=2 \
  --module-path /usr/lib/jvm/java-11-openjdk-amd64/jmods:/usr/lib/jvm/javafx-13-openjfx/javafx-jmods-13 \
  --add-modules java.base,javafx.base,javafx.controls,javafx.fxml,javafx.swing \
  --output jre
