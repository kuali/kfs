--
-- Copyright 2010 The Kuali Foundation
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

SELECT DISTINCT CUST_CNTRY_CD AS NON_CONVERTABLE_CODES FROM AR_CUST_ADDR_T WHERE CUST_CNTRY_CD NOT IN
('AA','AC','AF','AG','AJ','AL','AM','AN','AO','AQ','AR','AS','AT','AU','AV',
 'AY','BA','BB','BC','BD','BE','BF','BG','BH','BK','BL','BM','BN','BO','BP',
 'BQ','BR','BS','BT','BU','BV','BX','BY','CA','CB','CD','CE','CF','CG','CH',
 'CI','CJ','CK','CM','CN','CO','CQ','CR','CS','CT','CU','CV','CW','CY','DA',
 'DJ','DO','DR','EC','EG','EI','EK','EN','ER','ES','ET','EU','EZ','FA','FG',
 'FI','FJ','FM','FO','FP','FQ','FR','FS','GA','GB','GE','GG','GH','GI','GJ',
 'GK','GL','GM','GO','GP','GQ','GR','GT','GV','GY','GZ','HA','HK','HM','HO',
 'HQ','HR','HU','IC','ID','IM','IN','IO','IP','IR','IS','IT','IV','IZ','JA',
 'JE','JM','JN','JO','JQ','JU','KE','KG','KN','KQ','KR','KS','KT','KU','KZ',
 'LA','LE','LG','LH','LI','LO','LQ','LS','LT','LU','LY','MA','MB','MC','MD',
 'MF','MG','MH','MI','MK','ML','MN','MO','MP','MQ','MR','MT','MU','MV','MW',
 'MX','MY','MZ','NA','NC','NE','NF','NG','NH','NI','NL','NO','NP','NR','NS',
 'NU','NZ','OC','PA','PC','PE','PK','PL','PM','PO','PP','PS','PU','QA','RE',
 'RM','RO','RP','RQ','RS','RW','SA','SB','SC','SE','SF','SG','SH','SI','SL',
 'SM','SN','SO','SP','SR','ST','SU','SV','SW','SY','SZ','TC','TD','TE','TH',
 'TI','TK','TL','TN','TO','TP','TS','TU','TV','TW','TX','TZ','UG','UK','UP',
 'US','UY','UZ','VC','VE','VI','VM','VQ','VT','WA','WE','WF','WI','WQ','WS',
 'WZ','YM','ZA','ZI');
 