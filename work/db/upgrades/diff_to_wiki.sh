#
# Copyright 2010 The Kuali Foundation
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

svn diff https://test.kuali.org/svn/kfs/tags/releases/release-3-0-1 https://test.kuali.org/svn/kfs/trunk --summarize > difference_summary.txt
# strip the url prefix
perl -pi -e 's@https://test.kuali.org/svn/kfs/tags/releases/release-3-0-1/@kfs/@g' difference_summary.txt
perl -pi -e 's/^(...... )(.*)$/| $1 | $2 |/' difference_summary.txt
perl -pi -e 's/^\| M /| Modified           /' difference_summary.txt
perl -pi -e 's/^\| A /| Added              /' difference_summary.txt
perl -pi -e 's/^\| D /| Deleted            /' difference_summary.txt
perl -pi -e 's/^\|  M/| Properties Modified/' difference_summary.txt
