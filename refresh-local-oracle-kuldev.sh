TARGETS="${1:-drop-schema create-schema import}"
pushd work/db/kfs-db/db-impex/impex

set -x
ant $TARGETS -Dimpex.properties.file=../../../../../impex-local-oracle-kuldev.properties
set +x

popd