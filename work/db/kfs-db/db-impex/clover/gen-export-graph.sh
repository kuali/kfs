export DB=$1
export SCHEMA=`echo $2 | tr '[:lower:]' '[:upper:]'`
export TABLE=`echo $3 | tr '[:lower:]' '[:upper:]'`
export FILE=`echo $3 | tr '[:upper:]' '[:lower:]'`_export
ant -emacs gen-export-graph -Ddatabase.config=$DB -Dtable.schema=$SCHEMA -Dtable.name=$TABLE -Dconfig.file=$FILE -Dtable.truncate=$TRUNCATE
