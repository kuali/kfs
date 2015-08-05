#!/bin/bash

export EXTERNAL_CONFIG="-Dadditional.kfs.config.locations=$EXTERNAL_CONFIG_DIR/kfs-config.properties,$EXTERNAL_CONFIG_DIR/kfs-rice-config.properties,$EXTERNAL_CONFIG_DIR/kfs-security-config.properties"

mvn $EXTERNAL_CONFIG -DskipTests=true -P oracle -Dserver.name="http://localhost:8080" tomcat7:run-war
