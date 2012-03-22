TARGETS="${1:-create-schema empty-schema import}"
pushd work/db/kfs-db/db-impex/impex

set -x
ant $TARGETS -Dimpex.properties.file=../../../../../impex-local-mysql-kfs.properties
set +x

popd