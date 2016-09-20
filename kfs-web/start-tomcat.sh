#!/bin/bash

export EXTERNAL_CONFIG="-Dadditional.kfs.config.locations=$UA_KFS_EXTERNAL_CONFIG_DIR/kfs-config.properties,$UA_KFS_EXTERNAL_CONFIG_DIR/kfs-rice-config.properties,$UA_KFS_EXTERNAL_CONFIG_DIR/kfs-security-config.properties,$UA_KFS_INSTITUTIONAL_CONFIG_DIR/institutional-config.properties"

mvn $EXTERNAL_CONFIG -DskipTests=true -P oracle -Dserver.name="http://localhost:8080" tomcat7:run-war
