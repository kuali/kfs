TARGETS="${1:-export}"
pushd kfs-db/db-impex/impex

set -x
ant $TARGETS -Dimpex.properties.file=../../../export-rice.properties
set +x

popd