
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
