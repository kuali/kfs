#
# Copyright 2011 The Kuali Foundation
# 
# Licensed under the Educational Community License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
# http://www.opensource.org/licenses/ecl2.php
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# DO NOT add comments before the blank line below, or they will disappear.

DIRNAME=$1_$2

SOURCE_DIR=4.0_4.1

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
