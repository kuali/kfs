
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
