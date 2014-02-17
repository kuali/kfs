#
# Copyright 2012 The Kuali Foundation
#
# Licensed under the Educational Community License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.opensource.org/licenses/ecl2.php
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# DO NOT add comments before the blank line below, or they will disappear.

set -e
# Control Variables for Testing Parts of Scripts
IMPORT_OLD_PROJECT=${IMPORT_OLD_PROJECT:-true}
IMPORT_NEW_PROJECT=${IMPORT_NEW_PROJECT:-true}
RUN_UPGRADE_SCRIPTS=${RUN_UPGRADE_SCRIPTS:-true}
RUN_UPGRADE_WORKFLOW=${RUN_UPGRADE_WORKFLOW-true}
EXPORT_UPGRADED_PROJECT=${EXPORT_UPGRADED_PROJECT:-true}
PERFORM_COMPARISON=${PERFORM_COMPARISON:-true}
REBUILD_SCHEMA=${REBUILD_SCHEMA:-true}

# Check for WORKSPACE (a Jenkins variable) and set if necessary so this script
# can run outside of Hudson
if [[ -z "$WORKSPACE" ]]; then
# Standard Variables from Jenkins
ABSPATH=$(cd ${0%/*} && echo $PWD/${0##*/})
WORKSPACE=`dirname $ABSPATH`
echo Working Directory: $WORKSPACE
fi

ORACLE_TEST_DB_URL=${ORACLE_TEST_DB_URL:-jdbc:oracle:thin:@oraclerds.kfs.kuali.org:1521:KFS}
#MYSQL_TEST_DB_URL=jdbc:mysql://test.db.kfs.kuali.org:3306
MYSQL_TEST_DB_URL=${MYSQL_TEST_DB_URL:-jdbc:mysql://localhost:3306}

# Ensure we are in the right directory
cd $WORKSPACE

# Parameters to job
UPGRADE_SCRIPT_DIR=$WORKSPACE/kfs/work/db/upgrades/${UPGRADE_SCRIPT_DIR:-4.1.1_5.0}

DB_TYPE=${DB_TYPE:-MYSQL}
DB_USER=${DB_USER:-dbtest}
DB_SCHEMA=${DB_SCHEMA:-$DB_USER}
DB_ADMIN_USER=${DB_ADMIN_USER:-root}
DB_PASSWORD=${DB_PASSWORD:-$DB_USER}
DB_ADMIN_PASSWORD=${DB_ADMIN_PASSWORD:-}
LIQUIBASE_LOG_LEVEL=${LIQUIBASE_LOG_LEVEL:-info}

# Set some properties based on the database type
if [[ "$DB_TYPE" == "MYSQL" ]]; then
DATASOURCE=$MYSQL_TEST_DB_URL/$DB_SCHEMA
ADMIN_DATASOURCE=$MYSQL_TEST_DB_URL
DRIVER=com.mysql.jdbc.Driver
OJB_PLATFORM=MySQL
TORQUE_PLATFORM=mysql
else
DATASOURCE=$ORACLE_TEST_DB_URL
ADMIN_DATASOURCE=$ORACLE_TEST_DB_URL
DRIVER=oracle.jdbc.OracleDriver
OJB_PLATFORM=Oracle9i
TORQUE_PLATFORM=oracle
fi

set /opt/hudson/drivers/*.jar
DRIVER_CLASSPATH=$(IFS=:; echo "$*")
cd $WORKSPACE

# create the needed liquibase.properties file
(
cat <<-EOF
classpath=$DRIVER_CLASSPATH
driver=$DRIVER
url=$DATASOURCE
username=$DB_USER
password=$DB_PASSWORD
EOF
) > $WORKSPACE/liquibase.properties

(
cat <<-EOF

drivers.directory=/opt/hudson/drivers
import.torque.database.user=$DB_USER
import.torque.database.schema=$DB_SCHEMA
import.torque.database.password=$DB_PASSWORD

torque.project=kfs
torque.schema.dir=$WORKSPACE/old_data/rice
torque.sql.dir=\${torque.schema.dir}/sql
torque.output.dir=\${torque.schema.dir}/sql

import.torque.database=$TORQUE_PLATFORM
import.torque.database.driver=$DRIVER
import.torque.database.url=$DATASOURCE

import.admin.user=$DB_ADMIN_USER
import.admin.password=$DB_ADMIN_PASSWORD
import.admin.url=$ADMIN_DATASOURCE

	oracle.usermaint.user=kulusermaint
	
	liquibase.logLevel=$LIQUIBASE_LOG_LEVEL
	
	post.import.liquibase.project=kfs
	post.import.liquibase.xml.directory=$WORKSPACE/old_kfs/work/db/rice-data
	post.import.liquibase.contexts=bootstrap
	
	post.import.workflow.project=kfs
	post.import.workflow.xml.directory=$WORKSPACE/old_kfs/work/workflow
	post.import.workflow.ingester.launcher.ant.script=$WORKSPACE/old_kfs/build.xml
	post.import.workflow.ingester.launcher.ant.target=import-workflow-xml
	post.import.workflow.ingester.launcher.ant.xml.directory.property=workflow.dir
	
	post.import.workflow.ingester.jdbc.url.property=datasource.url
	post.import.workflow.ingester.username.property=datasource.username
	post.import.workflow.ingester.password.property=datasource.password
	post.import.workflow.ingester.additional.command.line=-Ddatasource.ojb.platform=$OJB_PLATFORM -Dbase.directory=$WORKSPACE -Dappserver.home=$WORKSPACE/tomcat -Dexternal.config.directory=$WORKSPACE/opt -Dis.local.build= -Ddev.mode= -Drice.dev.mode=true -Drice.ksb.batch.mode=true -Ddont.filter.project.rice= -Ddont.filter.project.spring.ide= -Ddrivers.directory=/opt/hudson/drivers

liquibase.logLevel=$LIQUIBASE_LOG_LEVEL

post.import.liquibase.project=kfs
post.import.liquibase.xml.directory=$WORKSPACE/old_kfs/work/db/rice-data
post.import.liquibase.contexts=bootstrap

post.import.workflow.project=kfs
post.import.workflow.xml.directory=$WORKSPACE/old_kfs/work/workflow
post.import.workflow.ingester.launcher.ant.script=$WORKSPACE/old_kfs/build.xml
post.import.workflow.ingester.launcher.ant.target=import-workflow-xml
post.import.workflow.ingester.launcher.ant.xml.directory.property=workflow.dir

post.import.workflow.ingester.jdbc.url.property=datasource.url
post.import.workflow.ingester.username.property=datasource.username
post.import.workflow.ingester.password.property=datasource.password
post.import.workflow.ingester.additional.command.line=-Ddatasource.ojb.platform=$OJB_PLATFORM -Dbase.directory=$WORKSPACE -Dappserver.home=$WORKSPACE/tomcat -Dexternal.config.directory=$WORKSPACE/opt -Dis.local.build= -Ddev.mode= -Drice.dev.mode=true -Drice.ksb.batch.mode=true -Ddont.filter.project.rice= -Ddont.filter.project.spring.ide= -Ddrivers.directory=/opt/hudson/drivers

EOF
) > $WORKSPACE/impex-build.properties

if [[ "$IMPORT_OLD_PROJECT" == "true" ]]; then
echo '*********************************************************'
echo OLD DATABASE - IMPORTING
echo '*********************************************************'
# Prepare a tomcat directory that can be written to
rm -rf $WORKSPACE/tomcat
mkdir -p $WORKSPACE/tomcat/common/lib
mkdir -p $WORKSPACE/tomcat/common/classes

perl -pi -e 's/dbTable="([^"]*)"/dbTable="\U\1"/g' $WORKSPACE/old_data/development/graphs/*.xml
perl -pi -e 's/dbTable="([^"]*)"/dbTable="\U\1"/g' $WORKSPACE/old_data/rice/graphs/*.xml
perl -pi -e 's/viewdefinition="([^"]*?)[ ]*"/viewdefinition="\U\1"/g' $WORKSPACE/old_data/rice/schema.xml
perl -pi -e 's/&#[^;]*;/ /gi' $WORKSPACE/old_data/rice/schema.xml

pushd $WORKSPACE/kfs/work/db/kfs-db/db-impex/impex
#ant "-Dimpex.properties.file=$WORKSPACE/impex-build.properties" drop-schema create-schema import
if [[ "$REBUILD_SCHEMA" == "true" ]]; then
ant "-Dimpex.properties.file=$WORKSPACE/impex-build.properties" drop-schema create-schema create-ddl apply-ddl import-data apply-constraint-ddl
fi
echo '*********************************************************'
echo OLD DATABASE - POST-IMPORT LIQUIBASE
echo '*********************************************************'
ant "-Dimpex.properties.file=$WORKSPACE/impex-build.properties" run-liquibase-post-import
echo '*********************************************************'
echo OLD DATABASE - WORKFLOW
echo '*********************************************************'
ant "-Dimpex.properties.file=$WORKSPACE/impex-build.properties" import-workflow
popd
cp $WORKSPACE/old_data/rice/schema.xml $WORKSPACE/old_schema.xml

fi

if [[ "$RUN_UPGRADE_SCRIPTS" == "true" ]]; then
echo '*********************************************************'
echo OLD DATABASE - RUNNING UPGRADE LIQUIBASE
echo '*********************************************************'
pushd $UPGRADE_SCRIPT_DIR/rice_server

rm -f $WORKSPACE/upgrade.sql
touch $WORKSPACE/upgrade.sql
java -jar ../liquibase*.jar --defaultsFile=$WORKSPACE/liquibase.properties --logLevel=debug --changeLogFile=rice-server-script.xml updateSQL >> $WORKSPACE/upgrade.sql
java -jar ../liquibase*.jar --defaultsFile=$WORKSPACE/liquibase.properties --logLevel=debug --changeLogFile=kew_upgrade.xml updateSQL >> $WORKSPACE/upgrade.sql
java -jar ../liquibase*.jar --defaultsFile=$WORKSPACE/liquibase.properties --logLevel=debug --changeLogFile=kim_upgrade.xml updateSQL >> $WORKSPACE/upgrade.sql
java -jar ../liquibase*.jar --defaultsFile=$WORKSPACE/liquibase.properties --logLevel=debug --changeLogFile=parameter_updates.xml updateSQL >> $WORKSPACE/upgrade.sql

java -jar ../liquibase*.jar --defaultsFile=$WORKSPACE/liquibase.properties --logLevel=debug --changeLogFile=rice-server-script.xml update
java -jar ../liquibase*.jar --defaultsFile=$WORKSPACE/liquibase.properties --logLevel=debug --changeLogFile=kew_upgrade.xml update
java -jar ../liquibase*.jar --defaultsFile=$WORKSPACE/liquibase.properties --logLevel=debug --changeLogFile=kim_upgrade.xml update
java -jar ../liquibase*.jar --defaultsFile=$WORKSPACE/liquibase.properties --logLevel=debug --changeLogFile=parameter_updates.xml update
popd
fi

if [[ "$RUN_UPGRADE_WORKFLOW" == "true" ]]; then
echo '*********************************************************'
echo OLD DATABASE - RUNNING UPGRADE WORKFLOW XML
echo '*********************************************************'
pushd $WORKSPACE/kfs
(
cat <<-EOF
datasource.url=$DATASOURCE
datasource.username=$DB_USER
datasource.password=$DB_PASSWORD
datasource.ojb.platform=$OJB_PLATFORM
base.directory=$WORKSPACE
appserver.home=$WORKSPACE/tomcat
external.config.directory=$WORKSPACE/opt
is.local.build=
dev.mode=
rice.dev.mode=true
rice.ksb.batch.mode=true
dont.filter.project.rice=
dont.filter.project.spring.ide=
drivers.directory=/opt/hudson/drivers
EOF
) > $WORKSPACE/kfs-build.properties
#ant import-workflow-xml -Dworkflow.dir=$UPGRADE_SCRIPT_DIR/workflow/rice_provided -Duser.home=$WORKSPACE
ant import-workflow-xml -Dworkflow.dir=$UPGRADE_SCRIPT_DIR/workflow -Duser.home=$WORKSPACE
popd
fi

# run the export target
if [[ "$EXPORT_UPGRADED_PROJECT" == "true" ]]; then
echo '*********************************************************'
echo OLD DATABASE - EXPORTING STRUCTURE
echo '*********************************************************'

pushd $WORKSPACE/kfs/work/db/upgrades
ant drop-tables-before-export "-Ddb.url=$DATASOURCE" "-Ddb.user=$DB_USER" "-Ddb.password=$DB_PASSWORD" "-Ddb.driver=$DRIVER" -Doutput.file=$WORKSPACE/upgraded_kim_data.txt -lib $DRIVER_CLASSPATH
popd

mkdir -p $WORKSPACE/upgraded_data
(
cat <<-EOF
drivers.directory=/opt/hudson/drivers

export.torque.database.user=$DB_USER
export.torque.database.schema=$DB_SCHEMA
export.torque.database.password=$DB_PASSWORD

torque.project=kfs
torque.schema.dir=$WORKSPACE/upgraded_data
torque.sql.dir=\${torque.schema.dir}/sql
torque.output.dir=\${torque.schema.dir}/sql

export.torque.database=$TORQUE_PLATFORM
export.torque.database.driver=$DRIVER
export.torque.database.url=$DATASOURCE

export.table.name.filter=^(?!(.*_BKUP|TRV_.*|BK_.*|OLD_.*|O_.*|TEMP_.*)).*\$
EOF
) > $WORKSPACE/impex-build.properties
pushd $WORKSPACE/kfs/work/db/kfs-db/db-impex/impex
ant "-Dimpex.properties.file=$WORKSPACE/impex-build.properties" jdbc-to-xml
popd

perl -pi -e 's/dbTable="([^"]*)"/dbTable="\U\1"/g' $WORKSPACE/old_data/rice/graphs/*.xml
perl -pi -e 's/viewdefinition="([^"]*?)[ ]*"/viewdefinition="\U\1"/g' $WORKSPACE/old_data/rice/schema.xml
perl -pi -e 's/&#[^;]*;/ /gi' $WORKSPACE/old_data/rice/schema.xml


cp $WORKSPACE/upgraded_data/schema.xml $WORKSPACE/upgraded_schema.xml

echo '*********************************************************'
echo OLD DATABASE - EXPORTING KEW DATA
echo '*********************************************************'

pushd $WORKSPACE/kfs/work/db/upgrades
ant dump-kew-data "-Ddb.url=$DATASOURCE" "-Ddb.user=$DB_USER" "-Ddb.password=$DB_PASSWORD" "-Ddb.driver=$DRIVER" -Doutput.file=$WORKSPACE/upgraded_kew_data.txt -lib $DRIVER_CLASSPATH
ant dump-kim-data "-Ddb.url=$DATASOURCE" "-Ddb.user=$DB_USER" "-Ddb.password=$DB_PASSWORD" "-Ddb.driver=$DRIVER" -Doutput.file=$WORKSPACE/upgraded_kim_data.txt -lib $DRIVER_CLASSPATH
ant dump-parameter-data "-Ddb.url=$DATASOURCE" "-Ddb.user=$DB_USER" "-Ddb.password=$DB_PASSWORD" "-Ddb.driver=$DRIVER" -Doutput.file=$WORKSPACE/upgraded_parameter_data.txt -lib $DRIVER_CLASSPATH
popd
fi

# Compare the schema.xml files

if [[ "$PERFORM_COMPARISON" == "true" ]]; then
echo '*********************************************************'
echo OLD DATABASE - COMPARING RICE SCHEMA STRUCTURE
echo '*********************************************************'

cd $WORKSPACE
cp $WORKSPACE/kfs/work/db/kfs-db/rice/schema.xml $WORKSPACE/new_schema.xml
# Sanitize the sequence next values
perl -pi -e 's/nextval="[^"]*"/nextval="0"/g' upgraded_schema.xml
perl -pi -e 's/nextval="[^"]*"/nextval="0"/g' new_schema.xml

# Remove some extra control characters from the view
perl -pi -e 's/dbTable="([^"]*)"/dbTable="\U\1"/g' upgraded_schema.xml
perl -pi -e 's/viewdefinition="([^"]*?)[ ]*"/viewdefinition="\U\1"/g' upgraded_schema.xml
perl -pi -e 's/&#[^;]*;/ /gi' upgraded_schema.xml

perl -pi -e 's/dbTable="([^"]*)"/dbTable="\U\1"/g' new_schema.xml
perl -pi -e 's/viewdefinition="([^"]*?)[ ]*"/viewdefinition="\U\1"/g' new_schema.xml
perl -pi -e 's/&#[^;]*;/ /gi' new_schema.xml


if [[ "$DB_TYPE" == "MYSQL" ]]; then
# MySQL exports timestamps as timestamps, and Oracle does them as dates
perl -pi -e 's/type="TIMESTAMP"/type="DATE"/g' new_schema.xml

# remove column defaults, they are handled differently
perl -pi -e 's/ default=".+?"//g' upgraded_schema.xml
perl -pi -e 's/ default=".+?"//g' new_schema.xml

# Views come out *completely* differently - need to exclude them
perl -pi -e 's/<view .+?\/>//g' upgraded_schema.xml
perl -pi -e 's/<view .+?\/>//g' new_schema.xml
fi

# Strip trailing spaces from view definitions
perl -pi -e 's/(<view .+?)[ ]*"\/>/\1"\/>/g' upgraded_schema.xml
perl -pi -e 's/(<view .+?)[ ]*"\/>/\1"\/>/g' new_schema.xml

diff -b -i -B -U 3 upgraded_schema.xml new_schema.xml > rice-schema-compare-results.txt || true
fi

if [[ "$IMPORT_NEW_PROJECT" == "true" ]]; then
echo '*********************************************************'
echo NEW DATABASE - IMPORTING
echo '*********************************************************'

# Upper-case the table names in case we are running against MySQL on Amazon RDS
perl -pi -e 's/dbTable="([^"]*)"/dbTable="\U\1"/g' $WORKSPACE/kfs/work/db/kfs-db/rice/graphs/*.xml

(
cat <<-EOF

drivers.directory=/opt/hudson/drivers

import.torque.database.user=$DB_USER
import.torque.database.schema=$DB_SCHEMA
import.torque.database.password=$DB_PASSWORD

torque.project=kfs
torque.schema.dir=$WORKSPACE/kfs/work/db/kfs-db/rice
torque.sql.dir=\${torque.schema.dir}/sql
torque.output.dir=\${torque.schema.dir}/sql

import.torque.database=$TORQUE_PLATFORM
import.torque.database.driver=$DRIVER
import.torque.database.url=$DATASOURCE

import.admin.user=$DB_ADMIN_USER
import.admin.password=$DB_ADMIN_PASSWORD
import.admin.url=$ADMIN_DATASOURCE

		oracle.usermaint.user=kulusermaint
		
		liquibase.logLevel=$LIQUIBASE_LOG_LEVEL
		
		post.import.liquibase.project=kfs
		post.import.liquibase.xml.directory=$WORKSPACE/kfs/work/db/rice-data
		post.import.liquibase.contexts=bootstrap
		
		post.import.workflow.project=kfs
		post.import.workflow.xml.directory=$WORKSPACE/kfs/work/workflow
		post.import.workflow.ingester.launcher.ant.script=$WORKSPACE/kfs/build.xml
		post.import.workflow.ingester.launcher.ant.target=import-workflow-xml
		post.import.workflow.ingester.launcher.ant.xml.directory.property=workflow.dir
		
		post.import.workflow.ingester.jdbc.url.property=datasource.url
		post.import.workflow.ingester.username.property=datasource.username
		post.import.workflow.ingester.password.property=datasource.password
		post.import.workflow.ingester.additional.command.line=-Ddatasource.ojb.platform=$OJB_PLATFORM -Dbase.directory=$WORKSPACE -Dappserver.home=$WORKSPACE/tomcat -Dexternal.config.directory=$WORKSPACE/opt -Dis.local.build= -Ddev.mode= -Drice.dev.mode=true -Drice.ksb.batch.mode=true -Ddont.filter.project.rice= -Ddont.filter.project.spring.ide= -Ddrivers.directory=/opt/hudson/drivers

liquibase.logLevel=$LIQUIBASE_LOG_LEVEL

post.import.liquibase.project=kfs
post.import.liquibase.xml.directory=$WORKSPACE/kfs/work/db/rice-data
post.import.liquibase.contexts=bootstrap

post.import.workflow.project=kfs
post.import.workflow.xml.directory=$WORKSPACE/kfs/work/workflow
post.import.workflow.ingester.launcher.ant.script=$WORKSPACE/kfs/build.xml
post.import.workflow.ingester.launcher.ant.target=import-workflow-xml
post.import.workflow.ingester.launcher.ant.xml.directory.property=workflow.dir

post.import.workflow.ingester.jdbc.url.property=datasource.url
post.import.workflow.ingester.username.property=datasource.username
post.import.workflow.ingester.password.property=datasource.password
post.import.workflow.ingester.additional.command.line=-Ddatasource.ojb.platform=$OJB_PLATFORM -Dbase.directory=$WORKSPACE -Dappserver.home=$WORKSPACE/tomcat -Dexternal.config.directory=$WORKSPACE/opt -Dis.local.build= -Ddev.mode= -Drice.dev.mode=true -Drice.ksb.batch.mode=true -Ddont.filter.project.rice= -Ddont.filter.project.spring.ide= -Ddrivers.directory=/opt/hudson/drivers

EOF
) > $WORKSPACE/impex-build.properties

pushd $WORKSPACE/kfs/work/db/kfs-db/db-impex/impex
#ant "-Dimpex.properties.file=$WORKSPACE/impex-build.properties" drop-schema create-schema import
if [[ "$REBUILD_SCHEMA" == "true" ]]; then
ant "-Dimpex.properties.file=$WORKSPACE/impex-build.properties" drop-schema create-schema create-ddl apply-ddl import-data apply-constraint-ddl
fi
echo '*********************************************************'
echo NEW DATABASE - POST-IMPORT LIQUIBASE
echo '*********************************************************'
ant "-Dimpex.properties.file=$WORKSPACE/impex-build.properties" run-liquibase-post-import
echo '*********************************************************'
echo NEW DATABASE - WORKFLOW
echo '*********************************************************'
ant "-Dimpex.properties.file=$WORKSPACE/impex-build.properties" import-workflow
popd


if [[ "$PERFORM_COMPARISON" == "true" ]]; then
echo '*********************************************************'
echo NEW DATABASE - EXPORTING KEW DATA
echo '*********************************************************'
pushd $WORKSPACE/kfs/work/db/upgrades
ant dump-kew-data "-Ddb.url=$DATASOURCE" "-Ddb.user=$DB_USER" "-Ddb.password=$DB_PASSWORD" "-Ddb.driver=$DRIVER" -Doutput.file=$WORKSPACE/new_kew_data.txt -lib $DRIVER_CLASSPATH
ant dump-kim-data "-Ddb.url=$DATASOURCE" "-Ddb.user=$DB_USER" "-Ddb.password=$DB_PASSWORD" "-Ddb.driver=$DRIVER" -Doutput.file=$WORKSPACE/new_kim_data.txt -lib $DRIVER_CLASSPATH
ant dump-parameter-data "-Ddb.url=$DATASOURCE" "-Ddb.user=$DB_USER" "-Ddb.password=$DB_PASSWORD" "-Ddb.driver=$DRIVER" -Doutput.file=$WORKSPACE/new_parameter_data.txt -lib $DRIVER_CLASSPATH
popd
diff -b -i -B -U 3 upgraded_kew_data.txt new_kew_data.txt > kew-data-compare-results.txt || true
diff -b -i -B -U 3 upgraded_kim_data.txt new_kim_data.txt > kim-data-compare-results.txt || true
diff -b -i -B -U 3 upgraded_parameter_data.txt new_parameter_data.txt > parameter-data-compare-results.txt || true
fi
fi

rm -f mismatch.txt
if [[ -s rice-schema-compare-results.txt || -s kew-data-compare-results.txt || -s kim-data-compare-results.txt || -s parameter-data-compare-results.txt ]]; then
echo 'MISMATCH' > mismatch.txt
else
echo 'SUCCESS' > mismatch.txt
fi

exit 0
