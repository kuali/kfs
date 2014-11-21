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

-- Clean out the KFS Scheduler tables
DELETE FROM QRTZ_BLOB_TRIGGERS
/
DELETE FROM QRTZ_CALENDARS
/
DELETE FROM QRTZ_CRON_TRIGGERS
/
DELETE FROM QRTZ_FIRED_TRIGGERS
/
DELETE FROM QRTZ_JOB_DETAILS
/
DELETE FROM QRTZ_JOB_LISTENERS
/
DELETE FROM QRTZ_PAUSED_TRIGGER_GRPS
/
DELETE FROM QRTZ_SCHEDULER_STATE
/
DELETE FROM QRTZ_SIMPLE_TRIGGERS
/
DELETE FROM QRTZ_TRIGGERS
/
DELETE FROM QRTZ_TRIGGER_LISTENERS
/
COMMIT
/
-- Purge session documents and other temp result tables
DELETE FROM KRNS_SESN_DOC_T
/
DELETE FROM KRNS_LOOKUP_SEL_T
/
DELETE FROM KRNS_LOOKUP_RSLT_T
/
COMMIT
/
