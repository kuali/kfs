# Control Variables for Testing Parts of Scripts
CHECKOUT_CODE=${CHECKOUT_CODE:-true}
IMPORT_OLD_PROJECT=${IMPORT_OLD_PROJECT:-true}
RUN_UPGRADE_SCRIPTS=${RUN_UPGRADE_SCRIPTS:-true}
EXPORT_UPGRADED_PROJECT=${EXPORT_UPGRADED_PROJECT:-true}

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

PROJECT_DIR=$WORKSPACE/${PROJECT_NAME:-kfs}


# Ensure we are in the right directory
cd $WORKSPACE

# Parameters to job
OLD_BRANCH_PATH=${OLD_BRANCH_PATH:-branches/release-4-1-1}
#NEW_BRANCH_PATH=${NEW_BRANCH_PATH:-branches/rice-2-0-b1-merge2}
UPGRADE_SCRIPT_DIR=$PROJECT_DIR/work/db/upgrades/${UPGRADE_SCRIPT_DIR:-4.1.1_5.0}
RICE_UPGRADE_SCRIPT_DIR=""

# Other parameters
SVNREPO=${SVNREPO:-https://svn.kuali.org/repos}
BASE_SVN_DATA_PATH=$SVNREPO/kfs/legacy/cfg-dbs
BASE_SVN_RICE_PROJECT_PATH=$SVNREPO/rice

PRIOR_SVN_DATA_PATH=$BASE_SVN_DATA_PATH/$OLD_BRANCH_PATH

DB_TYPE=${DB_TYPE:-MYSQL}
DB_USER=${DB_USER:-dbtest}
DB_SCHEMA=${DB_SCHEMA:-$DB_USER}
DB_ADMIN_USER=${DB_ADMIN_USER:-root}
DB_PASSWORD=${DB_PASSWORD:-$DB_USER}
DB_ADMIN_PASSWORD=${DB_ADMIN_PASSWORD:-}

if [[ "$CHECKOUT_CODE" == "true" ]]; then

	echo Obtaining OLD Data Project from $PRIOR_SVN_DATA_PATH
	# Check out the old data project
	if [[ -d $PROJECT_DIR/build/temp/old_data ]]; then
		svn -q revert -R $PROJECT_DIR/build/temp/old_data
		svn -q switch $PRIOR_SVN_DATA_PATH $PROJECT_DIR/build/temp/old_data
		svn -q update --non-interactive $PROJECT_DIR/build/temp/old_data
	else
		svn -q co $PRIOR_SVN_DATA_PATH $PROJECT_DIR/build/temp/old_data
	fi

fi

# Prepare a tomcat directory that can be written to
rm -rf $PROJECT_DIR/build/temp/tomcat
mkdir -p $PROJECT_DIR/build/temp/tomcat/common/lib
mkdir -p $PROJECT_DIR/build/temp/tomcat/common/classes

# Lower-case the table names in case we are running against MySQL on Amazon RDS
perl -pi -e 's/dbTable="([^"]*)"/dbTable="\U\1"/g' $PROJECT_DIR/build/temp/old_data/development/graphs/*.xml

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
		torque.schema.dir=$PROJECT_DIR/build/temp/old_data/development
		torque.sql.dir=\${torque.schema.dir}/sql
		torque.output.dir=\${torque.schema.dir}/sql

		import.torque.database=$TORQUE_PLATFORM
		import.torque.database.driver=$DRIVER
		import.torque.database.url=$DATASOURCE

		import.admin.user=$DB_ADMIN_USER
		import.admin.password=$DB_ADMIN_PASSWORD
		import.admin.url=$ADMIN_DATASOURCE

		oracle.usermaint.user=kulusermaint
	EOF
	) > $PROJECT_DIR/build/temp/impex-build.properties

	if [[ "$DB_TYPE" == "MYSQL" ]]; then
		perl -pi -e 's/dbTable="([^"]*)"/dbTable="\U\1"/g' $PROJECT_DIR/build/temp/old_data/development/graphs/*.xml
		perl -pi -e 's/viewdefinition="([^"]*)"/viewdefinition="\U\1"/g' $PROJECT_DIR/build/temp/old_data/development/schema.xml
		perl -pi -e 's/&#[^;]*;/ /gi' $PROJECT_DIR/build/temp/old_data/development/schema.xml	
	fi
	
	pushd $PROJECT_DIR/work/db/kfs-db/db-impex/impex
	ant "-Dimpex.properties.file=$PROJECT_DIR/build/temp/impex-build.properties" drop-schema create-schema create-ddl apply-ddl import-data apply-constraint-ddl
	popd
fi

set $PROJECT_DIR/build/drivers/*.jar
DRIVER_CLASSPATH=$(IFS=:; echo "$*")

if [[ "$RUN_UPGRADE_SCRIPTS" == "true" ]]; then
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
	) > $PROJECT_DIR/build/temp/liquibase.properties
	pushd $UPGRADE_SCRIPT_DIR
	java -jar ../liquibase*.jar --defaultsFile=$PROJECT_DIR/build/temp/liquibase.properties --logLevel=finest --changeLogFile=master-structure-script.xml update
	java -jar ../liquibase*.jar --defaultsFile=$PROJECT_DIR/build/temp/liquibase.properties --logLevel=finest --changeLogFile=master-data-script.xml update
	java -jar ../liquibase*.jar --defaultsFile=$PROJECT_DIR/build/temp/liquibase.properties --logLevel=finest --changeLogFile=master-constraint-script.xml update
	popd
fi

# run the export target
if [[ "$EXPORT_UPGRADED_PROJECT" == "true" ]]; then
	mkdir -p $PROJECT_DIR/build/temp/upgraded_data
	(
	cat <<-EOF
		export.torque.database.user=$DB_USER
		export.torque.database.schema=$DB_SCHEMA
		export.torque.database.password=$DB_PASSWORD

		torque.project=kfs
		torque.schema.dir=$PROJECT_DIR/build/temp/upgraded_data
		torque.sql.dir=\${torque.schema.dir}/sql
		torque.output.dir=\${torque.schema.dir}/sql

		export.torque.database=$TORQUE_PLATFORM
		export.torque.database.driver=$DRIVER
		export.torque.database.url=$DATASOURCE
	EOF
	) > $PROJECT_DIR/build/temp/impex-build.properties
	pushd $PROJECT_DIR/work/db/kfs-db/db-impex/impex
	ant "-Dimpex.properties.file=$PROJECT_DIR/build/temp/impex-build.properties" jdbc-to-xml
	popd
fi

# Compare the schema.xml files

pushd $PROJECT_DIR/build/temp
# Sanitize the sequence next values
perl -pi -e 's/nextval="[^"]*"/nextval="0"/g' upgraded_data/schema.xml
perl -pi -e 's/nextval="[^"]*"/nextval="0"/g' new_data/schema.xml

# Remove some extra control characters from the view
perl -pi -e 's/&#xa;//g' upgraded_data/schema.xml
perl -pi -e 's/&#xa;//g' new_data/schema.xml

if [[ "$DB_TYPE" == "MYSQL" ]]; then
	# MySQL exports timestamps as timestamps, and Oracle does them as dates
	perl -pi -e 's/type="TIMESTAMP"/type="DATE"/g' new_data/schema.xml

	# remove columm defaults, they are handled differently
	perl -pi -e 's/ default=".+?"//g' upgraded_data/schema.xml
	perl -pi -e 's/ default=".+?"//g' new_data/schema.xml

	# Views come out *completely* differently - need to exclude them
	perl -pi -e 's/<view .+?\/>//g' upgraded_data/schema.xml
	perl -pi -e 's/<view .+?\/>//g' new_data/schema.xml
fi

diff -b -U 3 upgraded_data/schema.xml new_data/schema.xml > compare-results.txt
popd
