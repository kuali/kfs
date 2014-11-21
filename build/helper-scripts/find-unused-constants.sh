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

pushd ../..
CONSTANT_NAMES=`grep -o "public static final String [^ =]*" work/src/org/kuali/kfs/sys/KFSConstants.java | awk '{ print $5 ; }'`
#echo $CONSTANT_NAMES
echo "Unused Constant Names"
for NM in $CONSTANT_NAMES; do
	grep -r -q --include=*.java --include=*.jsp --include=*.tag --exclude=KFSConstants.java $NM work
	if [[ $? != 0 ]]; then
		echo "$NM"
	fi
done
popd
