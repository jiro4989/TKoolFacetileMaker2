#!/bin/bash

set -eux

curl -O https://download.java.net/java/GA/jdk16.0.1/7147401fd7354114ac51ef3e1328291f/9/GPL/openjdk-16.0.1_linux-x64_bin.tar.gz

java_version=16
tar xvf openjdk-${java_version}_linux-x64_bin.tar.gz

mkdir -p /opt/java
mv jdk-${java_version} /opt/java/
ln -sfn /opt/java/jdk-${java_version} /opt/java/current

echo '[INFO] set "export JAVA_HOME=/opt/java/current" to your .bashrc or .zshrc or config.fish'
