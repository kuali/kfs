#!/bin/bash

export EXTERNAL_CONFIG="-Dadditional.kfs.config.locations=/Users/shaloo/java/env/kfs6/external_config/kfs-config.properties,/Users/shaloo/java/env/kfs6/external_config/kfs-rice-config.properties,/Users/shaloo/java/env/kfs6/external_config/kfs-security-config.properties"

mvn $EXTERNAL_CONFIG -DskipTests=true -P oracle -Dserver.name="http://localhost:8080" tomcat7:run-war
