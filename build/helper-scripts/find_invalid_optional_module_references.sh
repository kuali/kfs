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

# This script finds references to classes in other optional modules
pushd ../..
MODULES="ar
bc
cg
ec
endow
ld
"

for MODULE in $MODULES
do
	echo "******************************************************"
	echo "Checking for external references to \"$MODULE\" module"
	echo "******************************************************"
	grep -rn --include=*.java --color=auto "import org.kuali.kfs.module.${MODULE}." . | grep -v -e "/${MODULE}/"
done

MODULE=purap
echo "******************************************************"
echo "Checking for external references to \"$MODULE\" module"
echo "******************************************************"

grep -rn --include=*.java --color=auto "import org.kuali.kfs.module.${MODULE}." . | grep -v -e "/${MODULE}/" -e /cab/

MODULE=cam
echo "******************************************************"
echo "Checking for external references to \"$MODULE\" module"
echo "******************************************************"

grep -rn --include=*.java --color=auto "import org.kuali.kfs.module.${MODULE}." . | grep -v -e "/${MODULE}/" -e /cab/

MODULE=cab
echo "******************************************************"
echo "Checking for external references to \"$MODULE\" module"
echo "******************************************************"

grep -rn --include=*.java --color=auto "import org.kuali.kfs.module.${MODULE}." . | grep -v -e "/${MODULE}/" -e /cam/ -e /purap/

popd
