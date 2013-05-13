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
