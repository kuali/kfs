--
-- The Kuali Financial System, a comprehensive financial management system for higher education.
-- 
-- Copyright 2005-2014 The Kuali Foundation
-- 
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as
-- published by the Free Software Foundation, either version 3 of the
-- License, or (at your option) any later version.
-- 
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU Affero General Public License for more details.
-- 
-- You should have received a copy of the GNU Affero General Public License
-- along with this program.  If not, see <http://www.gnu.org/licenses/>.
--

-- these grants are needed since the functions are being used from within a package
-- and the permissions from a role do not propagate to a package

CREATE USER kulusermaint IDENTIFIED BY kulusermaint
 DEFAULT TABLESPACE USERS
 TEMPORARY TABLESPACE TEMP
 QUOTA UNLIMITED ON USERS
/

GRANT create session TO KULUSERMAINT WITH ADMIN OPTION
/
GRANT select any dictionary to KULUSERMAINT
/
GRANT EXECUTE on dbms_lock to KULUSERMAINT
/
GRANT alter system to KULUSERMAINT
/
GRANT create table to KULUSERMAINT
/
GRANT create procedure to KULUSERMAINT
/
GRANT alter user to KULUSERMAINT
/
GRANT create user to KULUSERMAINT
/
GRANT drop user TO KULUSERMAINT
/
GRANT kfs_role TO KULUSERMAINT WITH ADMIN OPTION
/
GRANT kfs_user_role TO KULUSERMAINT WITH ADMIN OPTION
/
GRANT create sequence to KFS_ROLE
/
