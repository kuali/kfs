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

SELECT DISTINCT POSTAL_CNTRY_CD AS NON_CONVERTABLE_CODES FROM FS_TAX_STATE_T WHERE POSTAL_CNTRY_CD NOT IN
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
 
