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




-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-02-27.sql
-- 



--
-- KULRICE-6842: Don't allow requests for null principals or null groups or null principal types
-- or null roles.
--

ALTER TABLE `KRIM_GRP_MBR_T`
MODIFY COLUMN `GRP_ID` VARCHAR(40) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
MODIFY COLUMN `MBR_ID` VARCHAR(40) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
MODIFY COLUMN `MBR_TYP_CD` CHAR(1) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL;

ALTER TABLE `KRIM_ROLE_MBR_T`
MODIFY COLUMN `ROLE_ID` VARCHAR(40) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
MODIFY COLUMN `MBR_ID` VARCHAR(40) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
MODIFY COLUMN `MBR_TYP_CD` CHAR(1) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL;



-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-03-05.sql
-- 


UPDATE KREW_DOC_TYP_T SET LBL = 'Undefined' WHERE LBL is null
;
ALTER TABLE KREW_DOC_TYP_T MODIFY COLUMN LBL  VARCHAR(128) NOT NULL
;



-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-03-13.sql
-- 


--
-- KULRICE-6916 KRIM_ENTITY_CACHE_T.PRSN_NM is too small
--

ALTER TABLE `krim_entity_cache_t`
CHANGE COLUMN `PRSN_NM` `PRSN_NM` VARCHAR(255)
CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL;



-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-03-20.sql
-- 


--
-- KULRICE-5931 increase PLCY_VAL in order to store extended Recall notification configuration
--

ALTER TABLE krew_doc_typ_plcy_reln_t MODIFY PLCY_VAL VARCHAR(1024) CHARACTER SET utf8 COLLATE utf8_bin
;



-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-03-28.sql
-- 


-- KULRICE-5931

-- add 'appDocStatus' attr definition
INSERT INTO KRIM_ATTR_DEFN_T VALUES ((select KIM_ATTR_DEFN_ID from (select (max(cast(KIM_ATTR_DEFN_ID as decimal)) + 1) as KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where KIM_ATTR_DEFN_ID is not NULL and KIM_ATTR_DEFN_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_ATTR_DEFN_ID as decimal) < 10000) as tmptable), uuid(), 1, 'appDocStatus', null, 'Y', 'KR-WKFLW', 'org.kuali.rice.kim.bo.impl.KimAttributes')
;
-- assign it to 'Document Type & Routing Node or State' type
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1) as KIM_TYP_ATTR_ID from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable), uuid(), 1, 'a', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-SYS' and NM='Document Type & Routing Node or State'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-WKFLW' and NM='appDocStatus'), 'Y')
;

-- create Recall permission template
INSERT INTO KRIM_PERM_TMPL_T VALUES ((select perm_tmpl_id from (select (max(cast(perm_tmpl_id as decimal)) + 1) as perm_tmpl_id from krim_perm_tmpl_t where perm_tmpl_id is not NULL and perm_tmpl_id REGEXP '^[1-9][0-9]*$' and cast(perm_tmpl_id as decimal) < 10000) as tmptable), uuid(), 1, 'KR-WKFLW', 'Recall Document', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-SYS' and NM='Document Type & Routing Node or State'), 'Y')
;



-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-04-04.sql
-- 


-- KULRICE-6784 Add index and constraint on KREW_RULE_ATTR_T.NM
alter table KREW_RULE_ATTR_T add constraint KREW_RULE_ATTR_TC1 unique(NM)
;



-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-04-11.sql
-- 


-- insert Recall permission for initiators
INSERT INTO KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM) values ((select PERM_ID from (select (max(cast(PERM_ID as decimal)) + 1) as PERM_ID from KRIM_PERM_T where PERM_ID is not NULL and PERM_ID REGEXP '^[1-9][0-9]*$' and cast(PERM_ID as decimal) < 10000) as tmptable), uuid(), 1, (select PERM_TMPL_ID from KRIM_PERM_TMPL_T where NMSPC_CD = 'KR-WKFLW' and NM = 'Recall Document'), 'KR-WKFLW', 'Recall Document')
;
-- define document type wildcard for permission
INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) values ((select ATTR_DATA_ID from (select (max(cast(ATTR_DATA_ID as decimal)) + 1) as ATTR_DATA_ID from KRIM_PERM_ATTR_DATA_T where ATTR_DATA_ID is not NULL and ATTR_DATA_ID REGEXP '^[1-9][0-9]*$' and cast(ATTR_DATA_ID as decimal) < 10000) as tmptable), uuid(), 1, (select PERM_ID from KRIM_PERM_T where NMSPC_CD = 'KR-WKFLW' and NM='Recall Document'), (select KIM_TYP_ID from KRIM_PERM_TMPL_T where NMSPC_CD = 'KR-WKFLW' and NM = 'Recall Document'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD = 'KR-WKFLW' and NM = 'documentTypeName'), '*')
;
-- associate Recall permission with initiator derived role
INSERT INTO KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values ((select ROLE_PERM_ID from (select (max(cast(ROLE_PERM_ID as decimal)) + 1) as ROLE_PERM_ID from KRIM_ROLE_PERM_T where ROLE_PERM_ID is not NULL and ROLE_PERM_ID REGEXP '^[1-9][0-9]*$' and cast(ROLE_PERM_ID as decimal) < 10000) as tmptable), uuid(), 1, (select ROLE_ID from KRIM_ROLE_T where ROLE_NM = 'Initiator' and NMSPC_CD = 'KR-WKFLW'), (select PERM_ID from KRIM_PERM_T where NMSPC_CD = 'KR-WKFLW' and NM='Recall Document'), 'Y')
;



-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-04-12.sql
-- 


-- create a Kim Type wired to the documentRouterRoleTypeService permission-derived role service
INSERT INTO KRIM_TYP_T (KIM_TYP_ID, OBJ_ID, VER_NBR, NM, SRVC_NM, ACTV_IND, NMSPC_CD) values ((select KIM_TYP_ID from (select (max(cast(KIM_TYP_ID as decimal)) + 1) as KIM_TYP_ID from KRIM_TYP_T where KIM_TYP_ID is not NULL and KIM_TYP_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ID as decimal) < 10000) as tmptable), uuid(), 1, 'Derived Role: Permission (Route Document)', 'documentRouterRoleTypeService', 'Y', 'KR-WKFLW')
;
-- define the Route Document derived role
INSERT INTO KRIM_ROLE_T (ROLE_ID, OBJ_ID, VER_NBR, ROLE_NM, NMSPC_CD, DESC_TXT, KIM_TYP_ID, ACTV_IND) values ((select ROLE_ID from (select (max(cast(ROLE_ID as decimal)) + 1) as ROLE_ID from KRIM_ROLE_T where ROLE_ID is not NULL and ROLE_ID REGEXP '^[1-9][0-9]*$' and cast(ROLE_ID as decimal) < 10000) as tmptable), uuid(), 1, 'Document Router', 'KR-WKFLW', 'This role derives its members from users with the Route Document permission for a given document type.', (select KIM_TYP_ID from KRIM_TYP_T where NM = 'Derived Role: Permission (Route Document)' and NMSPC_CD = 'KR-WKFLW'), 'Y')
;



-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-04-16.sql
-- 


-- KULRICE-7128 wire kim type attribute 'qualifierResolverProvidedIdentifier' to Responsibility type
Insert into `krim_typ_attr_t`
(`KIM_TYP_ATTR_ID`,
`OBJ_ID`,
`VER_NBR`,
`SORT_CD`,
`KIM_TYP_ID`,
`KIM_ATTR_DEFN_ID`,
`ACTV_IND`)
VALUES
((select KIM_TYP_ATTR_ID from (select (max(cast(KIM_TYP_ATTR_ID as decimal)) + 1)
  as KIM_TYP_ATTR_ID from krim_typ_attr_t
  where KIM_TYP_ATTR_ID is not NULL and KIM_TYP_ATTR_ID REGEXP '^[1-9][0-9]*$' and cast(KIM_TYP_ATTR_ID as decimal) < 10000) as tmptable),
  '69FA55ACC2EE2598E0404F8189D86880',
  1,
  'e',
  7,
  46,
  'Y')
;



-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-04-19.sql
-- 


CREATE TABLE KRNS_MAINT_DOC_ATT_LST_T  (
    ATT_ID      varchar(40) NOT NULL,
	DOC_HDR_ID	varchar(14) NOT NULL,
	ATT_CNTNT 	longblob NOT NULL,
	FILE_NM   	varchar(150) NULL,
	CNTNT_TYP 	varchar(255) NULL,
	OBJ_ID    	varchar(36) NOT NULL,
	VER_NBR   	decimal(8,0) NOT NULL DEFAULT 0,
	PRIMARY KEY(ATT_ID),
	CONSTRAINT KRNS_MAINT_DOC_ATT_LST_FK1 foreign key (DOC_HDR_ID) references KRNS_MAINT_DOC_T (DOC_HDR_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
;

ALTER TABLE KRNS_MAINT_DOC_ATT_LST_T
	ADD CONSTRAINT KRNS_MAINT_DOC_ATT_LST_TC0
	UNIQUE (OBJ_ID);

create index KRNS_MAINT_DOC_ATT_LST_TI1 on KRNS_MAINT_DOC_ATT_LST_T (DOC_HDR_ID);

create table KRNS_MAINT_DOC_ATT_S (
  id bigint(19) not null auto_increment,
  primary key (id)
) ENGINE MyISAM;
alter table KRNS_MAINT_DOC_ATT_S auto_increment = 10000;


-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-04-25.sql
-- 


update krms_attr_defn_t set nm='actionTypeCode' where attr_defn_id='1004';
update krms_attr_defn_t set nm='actionMessage' where attr_defn_id='1005';
update krms_attr_defn_t set nm='ruleTypeCode' where attr_defn_id='1001';

delete from krms_typ_attr_t where ATTR_DEFN_ID = '1002';
delete from krms_typ_attr_t where ATTR_DEFN_ID = '1003';
delete from krms_attr_defn_t where ATTR_DEFN_ID = '1002';
delete from krms_attr_defn_t where ATTR_DEFN_ID = '1003';


-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-05-11.sql
-- 


insert into krim_perm_t (perm_id, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind, ver_nbr, obj_id)
values ((select perm_id from (select (max(cast(perm_id as decimal)) + 1) as perm_id from krim_perm_t where perm_id is not NULL and perm_id REGEXP '^[1-9][0-9]*$' and cast(perm_id as decimal) < 10000)
         as tmptable),
        (select perm_tmpl_id from krim_perm_tmpl_t where nm = 'Send Ad Hoc Request' and nmspc_cd = 'KR-NS'),
        'KR-SYS','Send Complete Request Kuali Document','Authorizes users to send Complete ad hoc requests for Kuali Documents','Y',1,uuid())
;

insert into krim_perm_attr_data_t
(attr_data_id, perm_id, kim_typ_id, kim_attr_defn_id, attr_val, ver_nbr, obj_id)
values ((select attr_data_id from
          (select (max(cast(attr_data_id as decimal)) + 1) as attr_data_id from krim_perm_attr_data_t where attr_data_id is not NULL and attr_data_id REGEXP '^[1-9][0-9]*$' and cast(attr_data_id as decimal) < 10000)
         as tmptable),
        (select perm_id from krim_perm_t where nm = 'Send Complete Request Kuali Document' and nmspc_cd = 'KR-SYS'),
        (select kim_typ_id from krim_typ_t where nm = 'Ad Hoc Review' and nmspc_cd = 'KR-WKFLW'),
        (select kim_attr_defn_id from krim_attr_defn_t where nm = 'documentTypeName'), 'KualiDocument',1,uuid())
;


insert into krim_perm_attr_data_t
(attr_data_id, perm_id, kim_typ_id, kim_attr_defn_id, attr_val, ver_nbr, obj_id)
values ((select attr_data_id from
          (select (max(cast(attr_data_id as decimal)) + 1) as attr_data_id from krim_perm_attr_data_t where attr_data_id is not NULL and attr_data_id REGEXP '^[1-9][0-9]*$' and cast(attr_data_id as decimal) < 10000)
         as tmptable),
        (select perm_id from krim_perm_t where nm = 'Send Complete Request Kuali Document' and nmspc_cd = 'KR-SYS'),
        (select kim_typ_id from krim_typ_t where nm = 'Ad Hoc Review' and nmspc_cd = 'KR-WKFLW'),
        (select kim_attr_defn_id from krim_attr_defn_t where nm = 'actionRequestCd'), 'C',1,uuid())
;

insert into krim_role_perm_t
(role_perm_id, role_id, perm_id, actv_ind, ver_nbr, obj_id)
values ((select role_perm_id from
          (select (max(cast(role_perm_id as decimal)) + 1) as role_perm_id from krim_role_perm_t where role_perm_id is not NULL and role_perm_id REGEXP '^[1-9][0-9]*$' and cast(role_perm_id as decimal) < 10000)
         as tmptable),
        (select role_id from krim_role_t where role_nm = 'Document Opener' and nmspc_cd = 'KR-NS'),
        (select perm_id from krim_perm_t where nm = 'Send Complete Request Kuali Document' and nmspc_cd = 'KR-SYS'),
        'Y', 1, uuid())
;

insert into krim_role_perm_t
(role_perm_id, role_id, perm_id, actv_ind, ver_nbr, obj_id)
values ((select role_perm_id from
          (select (max(cast(role_perm_id as decimal)) + 1) as role_perm_id from krim_role_perm_t where role_perm_id is not NULL and role_perm_id REGEXP '^[1-9][0-9]*$' and cast(role_perm_id as decimal) < 10000)
         as tmptable),
        (select role_id from krim_role_t where role_nm = 'Initiator or Reviewer' and nmspc_cd = 'KR-WKFLW'),
        (select perm_id from krim_perm_t where nm = 'Cancel Document' and nmspc_cd = 'KUALI'),
        'Y', 1, uuid())
;


-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-05-17.sql
-- 



-- KULRICE-7237: KRNS_NTE_T is selected by a field with no indexes - full table scan every time
create index KRNS_NTE_TI1 on KRNS_NTE_T (RMT_OBJ_ID);


-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-05-21.sql
-- 


update krim_perm_attr_data_t set ATTR_VAL='org.kuali.rice.core.web.impex.IngesterAction' where ATTR_VAL='org.kuali.rice.kew.batch.web.IngesterAction';




-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-05-24.sql
-- 



--
--  KULRICE-7377: KREW_RTE_NODE_T still defines DOC_TYP_ID as NUMBER(19)
--

alter table KREW_RTE_NODE_T modify column DOC_TYP_ID varchar(40);


-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-05-25.sql
-- 



--
--  KULRICE-7375: Rice master data source has KREW_DOC_TYP_PROC_T.INIT_RTE_NODE_ID still defined as a NUMBER
--

alter table KREW_DOC_TYP_PROC_T modify column INIT_RTE_NODE_ID varchar(40);