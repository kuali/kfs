--
-- Copyright 2005-2012 The Kuali Foundation
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

-- 
-- 2010-04-15.sql
-- 


ALTER TABLE krns_sesn_doc_t ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE krns_sesn_doc_t ADD (VER_NBR NUMBER(8) DEFAULT 0)
/




-- 
-- 2011-06-06.sql
-- 


ALTER TABLE KRSB_MSG_QUE_T RENAME COLUMN SVC_NMSPC TO APPL_ID
/



-- 
-- 2012-01-19c.sql
-- 

--
-- KULRICE-5856: KRNS_DOC_HDR_T.FDOC_DESC column is only 40 char
--

ALTER TABLE KRNS_DOC_HDR_T MODIFY (FDOC_DESC varchar2(255));
