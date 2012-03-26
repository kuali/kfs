KFS_BASE_DIR=$PWD
pushd build/tomcat
mkdir -p -v conf/Catalina/localhost
echo "Clearing out web configuration files"
rm -v conf/Catalina/localhost/*.xml
rm -rf webapps/kr-dev2
rm -rf webapps/kfs-dev2
echo "Moving Rice descriptor into path"
cp -v conf/kr-dev2.xml conf/Catalina/localhost
rm -rf logs/*
export JAVA_TOOL_OPTIONS=
export CATALINA_OPTS="-Dxxxxxxx=kfs-dev2 -DTOMCAT_DIR=$PWD -Xmx3g -Xms512m -XX:MaxPermSize=256m -Djava.awt.headless=true -Dadditional.config.locations=${PWD}/conf/rice-config.xml"
echo "Starting up Tomcat"
./bin/startup.sh
sleep 10
echo "Moving KFS descriptor into path"
cp -v conf/kfs-dev2.xml conf/Catalina/localhost
set +x
tail -F logs/catalina.out
set -x
echo "Attempting to Kill Tomcat server"
ps -Af | grep "Dxxxxxxx=kfs-dev2" | grep -v grep | cut -c 7-12 | xargs kill -9
popd