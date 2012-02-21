pushd work/db/kfs-db/db-impex/impex

ant create-schema empty-schema import -Dimpex.properties.file=../../../../../impex-build-local-mysql.properties

popd