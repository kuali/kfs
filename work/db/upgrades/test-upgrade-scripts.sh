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

# Control Variables for Testing Parts of Scripts
IMPORT_OLD_PROJECT=${IMPORT_OLD_PROJECT:-true}
RUN_UPGRADE_SCRIPTS=${RUN_UPGRADE_SCRIPTS:-true}
EXPORT_UPGRADED_PROJECT=${EXPORT_UPGRADED_PROJECT:-true}
PERFORM_COMPARISON=${PERFORM_COMPARISON:-true}

# Check for WORKSPACE (a Jenkins variable) and set if necessary so this script 
# can run outside of Hudson
if [[ -z "$WORKSPACE" ]]; then
	# Standard Variables from Jenkins
	ABSPATH=$(cd ${0%/*} && echo $PWD/${0##*/})
	WORKSPACE=`dirname $ABSPATH`
	echo Working Directory: $WORKSPACE
	PROJECT_DIR=$WORKSPACE/../../..
	TEMP_DIR=$WORKSPACE/temp
else
	PROJECT_DIR=$WORKSPACE/${PROJECT_NAME:-kfs}
	TEMP_DIR=$PROJECT_DIR/build/temp
fi
ORACLE_TEST_DB_URL=${ORACLE_TEST_DB_URL:-jdbc:oracle:thin:@oraclerds.kfs.kuali.org:1521:KFS}
#MYSQL_TEST_DB_URL=jdbc:mysql://test.db.kfs.kuali.org:3306
MYSQL_TEST_DB_URL=${MYSQL_TEST_DB_URL:-jdbc:mysql://localhost:3306}


# Ensure we are in the right directory
cd $WORKSPACE

# Parameters to job
OLD_BRANCH_PATH=${OLD_BRANCH_PATH:-branches/release-4-1-1}
UPGRADE_SCRIPT_DIR=$PROJECT_DIR/work/db/upgrades/${UPGRADE_SCRIPT_DIR:-4.1.1_5.0}

DB_TYPE=${DB_TYPE:-MYSQL}
DB_USER=${DB_USER:-dbtest}
DB_SCHEMA=${DB_SCHEMA:-$DB_USER}
DB_ADMIN_USER=${DB_ADMIN_USER:-root}
DB_PASSWORD=${DB_PASSWORD:-$DB_USER}
DB_ADMIN_PASSWORD=${DB_ADMIN_PASSWORD:-}

# Prepare a tomcat directory that can be written to
rm -rf $TEMP_DIR/tomcat
mkdir -p $TEMP_DIR/tomcat/common/lib
mkdir -p $TEMP_DIR/tomcat/common/classes

# Lower-case the table names in case we are running against MySQL on Amazon RDS
perl -pi -e 's/dbTable="([^"]*)"/dbTable="\U\1"/g' $TEMP_DIR/old_data/development/graphs/*.xml

# TODO: may need to lower case in the upgrade scripts as well

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

cd $WORKSPACE

if [[ "$IMPORT_OLD_PROJECT" == "true" ]]; then
	(
	cat <<-EOF
		import.torque.database.user=$DB_USER
		import.torque.database.schema=$DB_SCHEMA
		import.torque.database.password=$DB_PASSWORD

		torque.project=kfs
		torque.schema.dir=$TEMP_DIR/old_data/development
		torque.sql.dir=\${torque.schema.dir}/sql
		torque.output.dir=\${torque.schema.dir}/sql

		import.torque.database=$TORQUE_PLATFORM
		import.torque.database.driver=$DRIVER
		import.torque.database.url=$DATASOURCE

		import.admin.user=$DB_ADMIN_USER
		import.admin.password=$DB_ADMIN_PASSWORD
		import.admin.url=$ADMIN_DATASOURCE

		oracle.usermaint.user=kulusermaint

        drivers.directory=$DRIVERS_DIRECTORY
    
	EOF
	) > $TEMP_DIR/impex-build.properties

	if [[ "$DB_TYPE" == "MYSQL" ]]; then
		perl -pi -e 's/dbTable="([^"]*)"/dbTable="\U\1"/g' $TEMP_DIR/old_data/development/graphs/*.xml
		perl -pi -e 's/viewdefinition="([^"]*)"/viewdefinition="\U\1"/g' $TEMP_DIR/old_data/development/schema.xml
		perl -pi -e 's/&#[^;]*;/ /gi' $TEMP_DIR/old_data/development/schema.xml	
	fi
	
	pushd $PROJECT_DIR/work/db/kfs-db/db-impex/impex
	ant "-Dimpex.properties.file=$TEMP_DIR/impex-build.properties" drop-schema create-schema create-ddl apply-ddl import-data apply-constraint-ddl
	popd
fi

set /opt/hudson/drivers/*.jar
DRIVER_CLASSPATH=$(IFS=:; echo "$*")
cd $WORKSPACE

if [[ "$RUN_UPGRADE_SCRIPTS" == "true" ]]; then
	# create the needed liquibase.properties file
	(
	cat <<-EOF
		classpath=$DRIVER_CLASSPATH
		driver=$DRIVER
		url=$DATASOURCE
		username=$DB_USER
		password=$DB_PASSWORD		
EOF
	) > $TEMP_DIR/liquibase.properties
	pushd $UPGRADE_SCRIPT_DIR/db

	java -jar ../liquibase*.jar --defaultsFile=$TEMP_DIR/liquibase.properties --logLevel=info --changeLogFile=rice-client-script.xml updateSQL > $WORKSPACE/upgrade.sql
	java -jar ../liquibase*.jar --defaultsFile=$TEMP_DIR/liquibase.properties --logLevel=info --changeLogFile=master-structure-script.xml updateSQL >> $WORKSPACE/upgrade.sql
	java -jar ../liquibase*.jar --defaultsFile=$TEMP_DIR/liquibase.properties --logLevel=info --changeLogFile=master-data-script.xml updateSQL >> $WORKSPACE/upgrade.sql
	java -jar ../liquibase*.jar --defaultsFile=$TEMP_DIR/liquibase.properties --logLevel=info --changeLogFile=master-constraint-script.xml updateSQL >> $WORKSPACE/upgrade.sql

	java -jar ../liquibase*.jar --defaultsFile=$TEMP_DIR/liquibase.properties --logLevel=debug --changeLogFile=rice-client-script.xml update
	java -jar ../liquibase*.jar --defaultsFile=$TEMP_DIR/liquibase.properties --logLevel=debug --changeLogFile=master-structure-script.xml update
	java -jar ../liquibase*.jar --defaultsFile=$TEMP_DIR/liquibase.properties --logLevel=debug --changeLogFile=master-data-script.xml update
	java -jar ../liquibase*.jar --defaultsFile=$TEMP_DIR/liquibase.properties --logLevel=debug --changeLogFile=master-constraint-script.xml update
	popd
fi



# run the export target
if [[ "$EXPORT_UPGRADED_PROJECT" == "true" ]]; then
	mkdir -p $TEMP_DIR/upgraded_data
	(
	cat <<-EOF
		export.torque.database.user=$DB_USER
		export.torque.database.schema=$DB_SCHEMA
		export.torque.database.password=$DB_PASSWORD

		torque.project=kfs
		torque.schema.dir=$TEMP_DIR/upgraded_data
		torque.sql.dir=\${torque.schema.dir}/sql
		torque.output.dir=\${torque.schema.dir}/sql

		export.torque.database=$TORQUE_PLATFORM
		export.torque.database.driver=$DRIVER
		export.torque.database.url=$DATASOURCE
		
		export.table.name.filter=^(?!.*(_BKUP)).*\$

        drivers.directory=/opt/hudson/drivers
	EOF
	) > $TEMP_DIR/impex-build.properties
	pushd $PROJECT_DIR/work/db/kfs-db/db-impex/impex
	ant "-Dimpex.properties.file=$TEMP_DIR/impex-build.properties" jdbc-to-xml
	popd
fi

# Compare the schema.xml files

if [[ "$PERFORM_COMPARISON" == "true" ]]; then
	cd $WORKSPACE
	pushd $TEMP_DIR
	cp old_data/development/schema.xml $WORKSPACE/old_schema.xml
	cp upgraded_data/schema.xml $WORKSPACE/upgraded_schema.xml
	cp $PROJECT_DIR/work/db/kfs-db/development/schema.xml $WORKSPACE/new_schema.xml
	popd
	# Sanitize the sequence next values
	perl -pi -e 's/nextval="[^"]*"/nextval="0"/g' upgraded_schema.xml
	perl -pi -e 's/nextval="[^"]*"/nextval="0"/g' new_schema.xml

	# Remove some extra control characters from the view
	perl -pi -e 's/&#xa;//g' upgraded_schema.xml
	perl -pi -e 's/&#xa;//g' new_schema.xml

	if [[ "$DB_TYPE" == "MYSQL" ]]; then
		# MySQL exports timestamps as timestamps, and Oracle does them as dates
		perl -pi -e 's/type="TIMESTAMP"/type="DATE"/g' new_schema.xml

		# remove columm defaults, they are handled differently
		perl -pi -e 's/ default=".+?"//g' upgraded_schema.xml
		perl -pi -e 's/ default=".+?"//g' new_schema.xml

		# Views come out *completely* differently - need to exclude them
		perl -pi -e 's/<view .+?\/>//g' upgraded_schema.xml
		perl -pi -e 's/<view .+?\/>//g' new_schema.xml
	fi

	diff -b -i -B -U 3 upgraded_schema.xml new_schema.xml > compare-results.txt
fi
