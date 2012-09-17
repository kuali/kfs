export DB=$1
export GRAPH=$2
shift
shift
ant -emacs run "-Ddatabase.config=$DB" "-Dgraph.file=$GRAPH" "$@"
# -Ddo.dumps=true
# -v