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

-- clean out the service definition and message queue tables
DELETE FROM KRSB_SVC_DEF_T
/
DELETE FROM KRSB_MSG_PYLD_T
/
DELETE FROM KRSB_MSG_QUE_T
/
DELETE FROM KRSB_BAM_PARM_T
/
DELETE FROM KRSB_BAM_T
/
DELETE FROM KRSB_SVC_DSCRPTR_T
/
COMMIT
/
-- Clean out the KSB scheduler tables
DELETE FROM KRSB_QRTZ_BLOB_TRIGGERS
/
DELETE FROM KRSB_QRTZ_CALENDARS
/
DELETE FROM KRSB_QRTZ_CRON_TRIGGERS
/
DELETE FROM KRSB_QRTZ_FIRED_TRIGGERS
/
DELETE FROM KRSB_QRTZ_JOB_DETAILS
/
DELETE FROM KRSB_QRTZ_JOB_LISTENERS
/
DELETE FROM KRSB_QRTZ_PAUSED_TRIGGER_GRPS
/
DELETE FROM KRSB_QRTZ_SCHEDULER_STATE
/
DELETE FROM KRSB_QRTZ_SIMPLE_TRIGGERS
/
DELETE FROM KRSB_QRTZ_TRIGGERS
/
DELETE FROM KRSB_QRTZ_TRIGGER_LISTENERS
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
