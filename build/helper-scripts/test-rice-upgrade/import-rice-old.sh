SVNPATH=https://svn.kuali.org/repos/rice/legacy/cfg-dbs/branches/rice-release-1-0-3-clover-br

if [[ -z "$WORKSPACE" ]]; then
	# Standard Variables from Jenkins
	ABSPATH=$(cd ${0%/*} && echo $PWD/${0##*/})
	WORKSPACE=`dirname $ABSPATH`
	echo Working Directory: $WORKSPACE
	KFS_DIR=$WORKSPACE/../../..
#	TEMP_DIR=$WORKSPACE/temp
else
	KFS_DIR=$WORKSPACE/kfs
#	TEMP_DIR=$PROJECT_DIR/build/temp
fi

rm -rf $WORKSPACE/old
svn export $SVNPATH old

pushd $KFS_DIR/work/db/kfs-db/db-impex/impex
ant "-Dimpex.properties.file=$WORKSPACE/impex-local-oracle-rice-old.properties" drop-schema create-schema import
popd
