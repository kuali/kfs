#
# Copyright 2012 The Kuali Foundation
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
