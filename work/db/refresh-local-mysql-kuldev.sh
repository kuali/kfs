TARGETS="${1:-create-schema empty-schema import}"
pushd kfs-db/db-impex/impex

set -x
ant $TARGETS -Dimpex.properties.file=../../../impex-local-mysql-kuldev.properties.properties
set +x

popd