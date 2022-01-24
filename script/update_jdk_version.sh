#!/bin/bash

set -eu

java_version=$1

sed -i -E \
  -e "/(source|target)Compatibility/s/[0-9]+/${java_version}/g" \
  -e "/jvmTarget/s/[0-9]+/${java_version}/g" \
  build.gradle

sed -i -E \
  -e "s/(JAVAFX_VERSION=)[0-9]+/\1${java_version}/g" \
  script/build_for_linux.sh

find src \
  -name '*.fxml' \
  -exec \
  sed -i -E \
  -e "s^(http://javafx.com/javafx/)[0-9]+^\1${java_version}^" {} \;

find .github/workflows \
  -name '*.yml' \
  -exec \
  sed -i -E \
  -e "/JAVA(FX)?_VERSION:/s/[0-9]+/${java_version}/g" {} \;
