#!/usr/bin/env bash

# setup custom settings.xml to resolve content from RSO

mkdir "$HOME/.m2"
cat <<EOF > "$HOME/.m2/settings.xml"
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">

  <mirrors>
    <mirror>
      <id>sonatype-public</id>
      <mirrorOf>external:*</mirrorOf>
      <url>https://repository.sonatype.org/content/groups/sonatype-public-grid/</url>
    </mirror>
  </mirrors>

</settings>
EOF
