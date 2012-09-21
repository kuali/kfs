export DB=$1
export DIR=$2
shift
shift
ant -emacs run-dir "-Ddatabase.config=$DB" "-Dbase.dir=$DIR" "$@"
# -Ddo.dumps=true
# -v