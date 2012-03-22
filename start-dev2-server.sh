pushd build/tomcat
mkdir -p -v conf/Catalina/localhost
rm conf/Catalina/localhost/*.xml
cp conf/kr-dev2.xml conf/Catalina/localhost
export CATALINA_OPTS="-Xmx3g -Xms512m -XX:MaxPermSize=256m -Djava.awt.headless=true -Dadditional.config.locations=${PWD}/conf/rice-config.xml"
./bin/startup.sh
sleep 10
cp conf/kfs-dev2.xml conf/Catalina/localhost
tail -F logs/catalina.out
popd