pushd build/kfs-lib
svn rm avalon-framework-cvs-20020806.jar
svn rm backport-util-concurrent-2.1.jar
svn rm commons-dbcp-1.4.jar
svn rm commons-digester-1.7.jar
svn rm commons-pool-1.5.4.jar
# Maybe - if patch7 contains the needed fix for JavaMelody
#svn rm db-ojb-1.0.4-patch8.jar
svn rm dwr-1.1.4.jar
svn rm geronimo-annotation_1.0_spec-1.1.1.jar
svn rm xalan-serializer-2.7.0.jar
popd
pushd build/external/appserver
svn rm rice-web-1.0.3.3.war
svn rm howl.jar
cp ../../rice-lib/howl*.jar .
svn rm connector-1_5.jar
cp ../../rice-lib/connector-api*.jar .
svn rm jotm*.jar
cp ../../rice-lib/jotm*.jar .
svn rm jta-spec1_0_1.jar
cp ../../rice-lib/jta*.jar .
svn rm objectweb-datasource.jar ow_carol.jar
cp ../../rice-lib/carol*.jar .
# remove local CAS support, will not be updated for Rice 2.0
svn rm kuali-cas-1.0.0.war context-cas.xml standalone-cas-readme.txt
svn add *.jar *.war
popd
svn remove build/rice-sources/*1.0.3*
svn add build/rice-sources/*
svn revert -R work
svn remove --force `svn status | grep ? | awk '{ print $2; }'` work