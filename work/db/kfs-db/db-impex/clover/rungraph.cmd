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

@echo off
if "%3"=="" goto :check_two_args
:three_args
call ant -emacs run "-Ddatabase.config=%1" "-Dgraph.file=%2" "-Dbase.dir=%3"
goto :end

:check_two_args
if "%2"=="" goto :help

:two_args
call ant -emacs run "-Ddatabase.config=%1" "-Dgraph.file=%2" 
goto :end

:help
echo Usage: rungraph db_connection graphfile basedir
echo        db_connection  (Required) The database connection name
echo        graphfile      (Required) The graph file name.
echo        basedir        (Optional) The base directory for the graph file.
:end
