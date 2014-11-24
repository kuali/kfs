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

CREATE ROLE KFS_ROLE
/
GRANT CREATE DATABASE LINK TO KFS_ROLE
/
GRANT CREATE MATERIALIZED VIEW TO KFS_ROLE
/
GRANT CREATE PROCEDURE TO KFS_ROLE
/
GRANT CREATE PUBLIC SYNONYM TO KFS_ROLE
/
GRANT CREATE SESSION TO KFS_ROLE
/
GRANT CREATE SYNONYM TO KFS_ROLE
/
GRANT CREATE TABLE TO KFS_ROLE
/
GRANT CREATE TRIGGER TO KFS_ROLE
/
GRANT CREATE VIEW TO KFS_ROLE
/
GRANT RESTRICTED SESSION TO KFS_ROLE
/
GRANT UNLIMITED TABLESPACE TO KFS_ROLE
/

-- create a role for the application user (KFS_USER)

CREATE ROLE kfs_user_role
/
GRANT CREATE SESSION TO KFS_USER_ROLE
/
