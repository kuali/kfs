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