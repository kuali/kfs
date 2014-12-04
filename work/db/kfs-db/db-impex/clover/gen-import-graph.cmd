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
if "%1"=="" goto :help
if "%2"=="" goto :help
if "%3"=="" goto :help
if "%4"=="" goto :help
if "%5"=="" goto :default
   call ant -emacs gen-import-graph -Ddatabase.config=%1 -Dtable.schema=%2 -Dtable.name=%3 -Dconfig.file=%4 -Dtable.truncate=%5
goto :end

:default
   call ant -emacs gen-import-graph -Ddatabase.config=%1 -Dtable.schema=%2 -Dtable.name=%3 -Dconfig.file=%4 -Dtable.truncate=false
goto :end


:help
echo Usage: gen-import-graph db_connection schema table graphfile truncate
echo        db_connection  (Required) The database connection name
echo        schema         (Required) The database schema
echo        table_name     (Required) The table name.
echo        graphfile      (Required) The output graph file name.
echo        truncate       (Optional) true/false indicate if we need to first truncate the table.

:end
