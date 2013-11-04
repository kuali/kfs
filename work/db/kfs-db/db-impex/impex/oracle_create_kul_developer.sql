--
-- Copyright 2007-2012 The Kuali Foundation
--
-- Licensed under the Educational Community License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
-- http://www.opensource.org/licenses/ecl2.php
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
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
