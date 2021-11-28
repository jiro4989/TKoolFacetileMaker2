#!/bin/bash

set -eux

APP_NAME="tkfm" JAVAFX_VERSION=16 OS_NAME=linux ARCHIVE_CMD="tar czf" ARTIFACT_EXT=.tar.gz ENTRYPOINT_SCRIPT_EXT="" VERSION="dev" ./script/create_artifact.sh
