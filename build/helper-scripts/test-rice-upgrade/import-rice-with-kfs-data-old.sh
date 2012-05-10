SVNPATH=https://svn.kuali.org/repos/rice/legacy/cfg-dbs/branches/rice-release-1-0-3-clover-br

if [[ -z "$WORKSPACE" ]]; then
	# Standard Variables from Jenkins
	ABSPATH=$(cd ${0%/*} && echo $PWD/${0##*/})
	WORKSPACE=`dirname $ABSPATH`
	echo Working Directory: $WORKSPACE
	KFS_DIR=$WORKSPACE/../../..
	KFS411_DIR=$WORKSPACE/../../../../kfs-411
#	TEMP_DIR=$WORKSPACE/temp
else
	KFS_DIR=$WORKSPACE/kfs
	KFS411_DIR=$WORKSPACE/kfs-411
#	TEMP_DIR=$PROJECT_DIR/build/temp
fi

BASE_RICE_DATA_DIR=$WORKSPACE/old

rm -rf $WORKSPACE/old
svn export $SVNPATH old

pushd $KFS_DIR/work/db/kfs-db/db-impex/impex
#ant "-DBASE_RICE_DATA_DIR=$BASE_RICE_DATA_DIR" "-DOLD_KFS=$KFS411_DIR" "-Dimpex.properties.file=$WORKSPACE/import-local-oracle-rice-with-kfs-data-old.properties" drop-schema create-schema create-ddl apply-ddl import-data
#ant "-DBASE_RICE_DATA_DIR=$BASE_RICE_DATA_DIR" "-DOLD_KFS=$KFS411_DIR" "-Dimpex.properties.file=$WORKSPACE/import-local-oracle-rice-with-kfs-data-old.properties" run-liquibase-post-import
#ant "-DBASE_RICE_DATA_DIR=$BASE_RICE_DATA_DIR" "-DOLD_KFS=$KFS411_DIR" "-Dimpex.properties.file=$WORKSPACE/import-local-oracle-rice-with-kfs-data-old.properties" apply-constraint-ddl post-import-sql 
ant "-DBASE_RICE_DATA_DIR=$BASE_RICE_DATA_DIR" "-DOLD_KFS=$KFS411_DIR" "-Dimpex.properties.file=$WORKSPACE/import-local-oracle-rice-with-kfs-data-old.properties" import-workflow

popd
