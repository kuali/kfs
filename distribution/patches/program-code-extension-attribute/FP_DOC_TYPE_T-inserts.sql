/*
  *Copyright 2007 The Kuali Foundation.
  *
  * Licensed under the Educational Community License, Version 1.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
 
  * http://www.opensource.org/licenses/ecl1.php
 
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
 */
INSERT INTO FP_DOC_TYPE_T(FDOC_TYP_CD, OBJ_ID, VER_NBR, FDOC_NM, FDOC_TYP_ACTIVE_CD)
    VALUES('PROG', sys_guid(), 0, 'Program Code', 'Y')
/
insert into fp_doc_type_attr_t (DOC_TYP_ATTR_VAL,DOC_TYP_ATTR_CD,ID,OBJ_ID,VER_NBR,ACTIVE_IND,FDOC_TYP_CD) 
    values ('Y','TRANSACTION_SCRUBBER_OFFSET_GENERATION',FP_DOC_TYPE_ATTR_ID_SEQ.nextval,sys_guid(),1,'Y','PROG'
/
COMMIT
/

