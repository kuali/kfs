--
-- Copyright 2012 The Kuali Foundation
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
