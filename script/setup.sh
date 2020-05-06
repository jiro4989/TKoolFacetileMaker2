#!/bin/bash

set -eux

curl -O https://download.java.net/java/GA/jdk14/076bab302c7b4508975440c56f6cc26a/36/GPL/openjdk-14_linux-x64_bin.tar.gz
tar xvf openjdk-14_linux-x64_bin.tar.gz

mkdir -p /opt/java
mv jdk-14 /opt/java/
ln -sfn /opt/java/jdk-14 /opt/java/current
