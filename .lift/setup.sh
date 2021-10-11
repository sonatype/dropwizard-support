#!/usr/bin/env bash

echo "HOME: $HOME"
echo "CWD: $(pwd)"
echo "LS: ----8<----"
ls -l
echo "---->8----"

# setup custom settings.xml to resolve content from RSO

cat <<EOF > lift-settings.xml
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

  <profiles>
    <profile>
      <id>lift-analysis</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>http://central</url>
          <releases>
            <enabled>true</enabled>
          </releases>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <id>central</id>
          <url>http://central</url>
          <releases>
            <enabled>true</enabled>
          </releases>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </pluginRepository>
      </pluginRepositories>
    </profile>
  </profiles>

  <activeProfiles>
    <activeProfile>lift-analysis</activeProfile>
  </activeProfiles>

</settings>
EOF
