########################################
# The Kuali Financial System, a comprehensive financial management system for higher education.
# 
# Copyright 2005-2014 The Kuali Foundation
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
# 
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
########################################
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
