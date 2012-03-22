pushd build/tomcat
mkdir -p -v conf/Catalina/localhost
rm conf/Catalina/localhost/*.xml
cp conf/kr-dev2.xml conf/Catalina/localhost
rm -rf logs/*
export JAVA_TOOL_OPTIONS=
export CATALINA_OPTS="-Dxxxxxxx=kfs-dev2 -Xmx3g -Xms512m -XX:MaxPermSize=256m -Djava.awt.headless=true -Dadditional.config.locations=${PWD}/conf/rice-config.xml"
./bin/startup.sh
sleep 10
cp conf/kfs-dev2.xml conf/Catalina/localhost
set -x
tail -F logs/catalina.out
set +x
ps -Af | grep "Dxxxxxxx=kfs-dev2" | grep -v grep | cut -c 7-12 | xargs kill -9
popd