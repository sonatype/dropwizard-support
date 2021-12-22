#!/usr/bin/env bash

echo "HOME: $HOME"
echo "----8<----"
(cd $HOME; ls -l)
echo "---->8----"

echo "CWD: $(pwd)"
echo "----8<----"
ls -l
echo "---->8----"

# install custom maven settings.xml
mkdir -v $HOME/.m2
cp -v .lift/maven-settings.xml $HOME/.m2/settings.xml
