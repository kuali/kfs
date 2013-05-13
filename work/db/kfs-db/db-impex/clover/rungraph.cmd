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
