KFS_BASE_DIR=$PWD
pushd build/tomcat
mkdir -p -v conf/Catalina/localhost
echo "Clearing out web configuration files"
rm -v conf/Catalina/localhost/*.xml
rm -rf work/*
rm -rf temp/*
rm -rf logs/*
rm -rf webapps/kr-*
rm -rf webapps/kfs-*
echo "Moving Rice descriptor into path"
cp -v conf/kr-dev2.xml conf/Catalina/localhost
export JAVA_TOOL_OPTIONS=
export CATALINA_OPTS="-Dxxxxxxx=kfs-dev2 -DTOMCAT_DIR=$PWD -Xmx3g -Xms512m -XX:MaxPermSize=256m -Djava.awt.headless=true -Dadditional.config.locations=${PWD}/conf/rice-config.xml"
if [[ "$1" == "-debug" ]]; then
	export CATALINA_OPTS="$CATALINA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,suspend=n,server=y"
fi

trap stop_tomcat 1 2 3 15
function stop_tomcat() {
	echo "Attempting to Kill Tomcat server"
	ps -Af | grep "Dxxxxxxx=kfs-dev2" | grep -v grep | cut -c 7-12 | xargs kill -9
} # catch_sig()


echo "Starting up Tomcat"
./bin/startup.sh
sleep 10
echo "Moving KFS descriptor into path"
cp -v conf/kfs-dev2.xml conf/Catalina/localhost
tail -F logs/catalina.out
stop_tomcat
popd