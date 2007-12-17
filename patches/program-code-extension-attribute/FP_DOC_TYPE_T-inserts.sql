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
INSERT INTO FP_DOC_TYPE_T(FDOC_TYP_CD, OBJ_ID, VER_NBR, FDOC_GRP_CD, FDOC_NM, FIN_ELIM_ELGBL_CD, FDOC_TYP_ACTIVE_CD, FDOC_RTNG_RULE_CD, FDOC_AUTOAPRV_DAYS, FDOC_BALANCED_CD, TRN_SCRBBR_OFST_GEN_IND)
    VALUES('PROG', sys_guid(), 0, 'MR', 'Program Code', 'N', 'Y', 'N', 0, 'N', 'N')
/
COMMIT
/

