DIRNAME=$1_$2

SOURCE_DIR=4.0

svn mkdir $DIRNAME
svn mkdir $DIRNAME/db
svn mkdir $DIRNAME/db/01_structure
svn mkdir $DIRNAME/db/02_data
svn mkdir $DIRNAME/db/03_constraints
svn mkdir $DIRNAME/rice
svn mkdir $DIRNAME/workflow

svn cp $SOURCE_DIR/db/*.xml $DIRNAME/db/
svn cp $SOURCE_DIR/liquibase*.jar $DIRNAME/
svn cp $SOURCE_DIR/liquibase-sample.properties $DIRNAME/
svn cp $SOURCE_DIR/runlog.sh $DIRNAME/
svn cp $SOURCE_DIR/README.txt $DIRNAME/
