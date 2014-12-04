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

svn diff https://test.kuali.org/svn/kfs/tags/releases/release-3-0-1 https://test.kuali.org/svn/kfs/trunk --summarize > difference_summary.txt
# strip the url prefix
perl -pi -e 's@https://test.kuali.org/svn/kfs/tags/releases/release-3-0-1/@kfs/@g' difference_summary.txt
perl -pi -e 's/^(...... )(.*)$/| $1 | $2 |/' difference_summary.txt
perl -pi -e 's/^\| M /| Modified           /' difference_summary.txt
perl -pi -e 's/^\| A /| Added              /' difference_summary.txt
perl -pi -e 's/^\| D /| Deleted            /' difference_summary.txt
perl -pi -e 's/^\|  M/| Properties Modified/' difference_summary.txt
