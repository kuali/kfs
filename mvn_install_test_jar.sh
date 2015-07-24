#!/bin/bash

cd kfs-core && \

mvn jar:test-jar && \

mvn install:install-file -Dfile=target/kfs-core-tests.jar \
                         -DpomFile=pom.xml \
                         -Dclassifier=tests \
                         -DcreateChecksum=true && \

cd ../kfs-cg && \

mvn jar:test-jar && \

mvn install:install-file -Dfile=target/kfs-cg-tests.jar \
                         -DpomFile=pom.xml \
                         -Dclassifier=tests \
                         -DcreateChecksum=true && \

cd ../kfs-purap && \

mvn jar:test-jar && \

mvn install:install-file -Dfile=target/kfs-purap-tests.jar \
                         -DpomFile=pom.xml \
                         -Dclassifier=tests \
                         -DcreateChecksum=true && \      

cd ../kfs-bc && \

mvn jar:test-jar && \

mvn install:install-file -Dfile=target/kfs-bc-tests.jar \
                         -DpomFile=pom.xml \
                         -Dclassifier=tests \
                         -DcreateChecksum=true && \   

mvn -Dmaven.test.skip=true install && \

cd ../kfs-ld && \

mvn jar:test-jar && \

mvn install:install-file -Dfile=target/kfs-ld-tests.jar \
                         -DpomFile=pom.xml \
                         -Dclassifier=tests \
                         -DcreateChecksum=true && \

cd ../kfs-ar && \

mvn jar:test-jar && \

mvn install:install-file -Dfile=target/kfs-ar-tests.jar \
                         -DpomFile=pom.xml \
                         -Dclassifier=tests \
                         -DcreateChecksum=true                     