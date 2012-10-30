


-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-09-26.sql
-- 


--
--     KULRICE-8300	& KULRICE-7799
--

-- NOTE NOTE -  This is the first time that the master database will have KRxxx as the IDs on some of it's tables.
-- This SQL accounts for that and should be error free.

INSERT INTO KRIM_TYP_T(KIM_TYP_ID, OBJ_ID, VER_NBR, NM, SRVC_NM, ACTV_IND, NMSPC_CD)
  VALUES('KR1000', uuid(), 1, 'Document Type, Route Node, and Route Status',
    'documentTypeAndNodeAndRouteStatusPermissionTypeService', 'Y', 'KR-SYS');

INSERT INTO KRIM_TYP_ATTR_T(KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, SORT_CD, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)
  VALUES('KR1000',  uuid(), 1, 'a',
  (select KIM_TYP_ID from KRIM_TYP_T where NM = 'Document Type, Route Node, and Route Status' and NMSPC_CD = 'KR-SYS'),
  (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NM = 'documentTypeName' and NMSPC_CD = 'KR-WKFLW'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T(KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, SORT_CD, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)
  VALUES('KR1001', uuid(), 1, 'b',
  (select KIM_TYP_ID from KRIM_TYP_T where NM = 'Document Type, Route Node, and Route Status' and NMSPC_CD = 'KR-SYS'),
  (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NM = 'routeNodeName' and NMSPC_CD = 'KR-WKFLW'), 'Y');

INSERT INTO KRIM_TYP_ATTR_T(KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, SORT_CD, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)
  VALUES('KR1002', uuid(), 1, 'c',
  (select KIM_TYP_ID from KRIM_TYP_T where NM = 'Document Type, Route Node, and Route Status' and NMSPC_CD = 'KR-SYS'),
  (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NM = 'routeStatusCode' and NMSPC_CD = 'KR-WKFLW'), 'Y');

INSERT INTO KRIM_PERM_TMPL_T (PERM_TMPL_ID,ACTV_IND,KIM_TYP_ID,NM,NMSPC_CD,OBJ_ID,VER_NBR)
  VALUES ('KR1000', 'Y',
  (SELECT KIM_TYP_ID FROM KRIM_TYP_T where NM = 'Document Type, Route Node, and Route Status' and SRVC_NM = 'documentTypeAndNodeAndRouteStatusPermissionTypeService'),
  'Super User Approve Single Action Request', 'KR-WKFLW', uuid(), 1);

INSERT INTO KRIM_PERM_TMPL_T (PERM_TMPL_ID,ACTV_IND,KIM_TYP_ID,NM,NMSPC_CD,OBJ_ID,VER_NBR)
  VALUES ('KR1001', 'Y',
  (SELECT KIM_TYP_ID FROM KRIM_TYP_T where NM = 'Document Type, Route Node, and Route Status' and SRVC_NM = 'documentTypeAndNodeAndRouteStatusPermissionTypeService'),
  'Super User Approve Document', 'KR-WKFLW', uuid(), 1);

INSERT INTO KRIM_PERM_TMPL_T (PERM_TMPL_ID,ACTV_IND,KIM_TYP_ID,NM,NMSPC_CD,OBJ_ID,VER_NBR)
  VALUES ('KR1002', 'Y',
  (SELECT KIM_TYP_ID FROM KRIM_TYP_T where NM = 'Document Type, Route Node, and Route Status' and SRVC_NM = 'documentTypeAndNodeAndRouteStatusPermissionTypeService'),
  'Super User Disapprove Document', 'KR-WKFLW', uuid(), 1);


-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-10-12.sql
-- 


--
--    KULRICE-7792 & KULRICE-7793
--

UPDATE KREW_RTE_NODE_CFG_PARM_T
    SET VAL = REPLACE( VAL, 'org.kuali.rice.kim.workflow.attribute.KimTypeQualifierResolver', 'org.kuali.rice.kim.impl.type.KimTypeQualifierResolver' )
    WHERE val LIKE '%org.kuali.rice.kim.workflow.attribute.KimTypeQualifierResolver%';

UPDATE KREW_RTE_NODE_CFG_PARM_T
    SET VAL = REPLACE( VAL, 'org.kuali.rice.kns.workflow.attribute.DataDictionaryQualifierResolver', 'org.kuali.rice.krad.workflow.attribute.DataDictionaryQualifierResolver' )
    WHERE val LIKE '%org.kuali.rice.kns.workflow.attribute.DataDictionaryQualifierResolver%';



-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-10-17.sql
-- 


--
-- Clean up data and tables that are no longer needed.  Depending on how you ran the 2.0 upgrade scripts, these items
-- may or may not need to be cleaned up.  The SQL will run without error even if the items do not need cleaning.
--
-- The final SQL in this script will add a needed foreign key
--

--
-- KULRICE-7440 - KRMS_CNTXT_TERM_SPEC_PREREQ_S is still in master datasource
--

DROP TABLE IF EXISTS KRMS_CNTXT_TERM_SPEC_PREREQ_S
;

--
-- KULRICE-7412 - KREW_HLP_T and KREW_HLP_S is still in master datasource
--

DROP TABLE IF EXISTS KREW_HLP_T
;

DROP TABLE IF EXISTS KREW_HLP_S
;

--
-- KULRICE-7346 - ACTVN_TYP on KREW_RTE_NODE_T should be a varchar(1)
--

ALTER TABLE KREW_RTE_NODE_T MODIFY ACTVN_TYP VARCHAR(1)
;

--
-- KULRICE-7376 - APPL_ID length is inconsistent; Should always be 255
--

ALTER TABLE KREW_DOC_TYP_T MODIFY APPL_ID VARCHAR(255)
;

ALTER TABLE KREW_RULE_ATTR_T MODIFY APPL_ID VARCHAR(255)
;

ALTER TABLE KRSB_SVC_DEF_T MODIFY APPL_ID VARCHAR(255)
;

ALTER TABLE KRSB_MSG_QUE_T MODIFY APPL_ID VARCHAR(255)
;

ALTER TABLE KRCR_NMSPC_T MODIFY APPL_ID VARCHAR(255)
;

ALTER TABLE KRCR_PARM_T MODIFY APPL_ID VARCHAR(255)
;

--
-- KULRICE-7745 - County (not Country) maintenance document allowing bad state data - add FK constraint
--

ALTER TABLE KRLC_CNTY_T
    ADD CONSTRAINT KRLC_CNTY_TR1 FOREIGN KEY (STATE_CD,POSTAL_CNTRY_CD)
    REFERENCES KRLC_ST_T (POSTAL_STATE_CD, POSTAL_CNTRY_CD)
;


-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-10-19.sql
-- 



--
-- KULRICE-7786: Document Specific Doc Search Application Document Status should be available
-- (and groupable) on the basic version of search
--

-- add category name (which is part of composite fk) to app doc stat
alter table KREW_DOC_TYP_APP_DOC_STAT_T add column CAT_NM varchar(64)
;

-- add index for queries from category to status.  Using non-standard index name to follow table precedent.
CREATE INDEX KREW_DOC_TYP_APP_DOC_STAT_T2 on KREW_DOC_TYP_APP_DOC_STAT_T (DOC_TYP_ID, CAT_NM)
;

-- add sequence number column for ordering to app doc stat
alter table KREW_DOC_TYP_APP_DOC_STAT_T add column SEQ_NO DECIMAL(5,0)
;

-- create category table
CREATE TABLE KREW_DOC_TYP_APP_STAT_CAT_T  (
    DOC_TYP_ID  varchar(40) NOT NULL,
	CAT_NM      varchar(64) NOT NULL,
    VER_NBR     decimal(8,0) DEFAULT '0',
    OBJ_ID      varchar(36) NOT NULL,

	PRIMARY KEY(DOC_TYP_ID, CAT_NM),
	CONSTRAINT KREW_DOC_TYP_APP_STAT_CAT_FK1 foreign key (DOC_TYP_ID) references KREW_DOC_TYP_T (DOC_TYP_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
;

-- object id must be unique
ALTER TABLE KREW_DOC_TYP_APP_STAT_CAT_T
	ADD CONSTRAINT KREW_DOC_TYP_APP_STAT_CAT_TC1
	UNIQUE (OBJ_ID)
;

-- add constraint to tie app doc stat and category together
alter table KREW_DOC_TYP_APP_DOC_STAT_T add constraint KREW_DOC_TYP_APP_DOC_STAT_FK1
foreign key KREW_DOC_TYP_APP_DOC_STAT_TI2 (DOC_TYP_ID, CAT_NM) references KREW_DOC_TYP_APP_STAT_CAT_T (DOC_TYP_ID, CAT_NM)
;



-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-10-24.sql
-- 


--
-- KULRICE-7378 - MySQL Upgrade script for Rice 2.0 is dropping not null constraints
--

--
-- NOTE - This is only an issue for the MySQL scripts, so that is why there is no corresponding
--        2012-010-24.sql script for Oracle.
--

ALTER TABLE KRSB_SVC_DEF_T MODIFY APPL_ID VARCHAR(255) NOT NULL;

ALTER TABLE KRSB_MSG_QUE_T MODIFY APPL_ID VARCHAR(255) NOT NULL;

ALTER TABLE KRCR_PARM_T MODIFY APPL_ID VARCHAR(255) NOT NULL;

ALTER TABLE KRMS_AGENDA_T MODIFY ACTV varchar(1) DEFAULT 'Y' NOT NULL;

ALTER TABLE KREW_DOC_TYP_ATTR_T MODIFY DOC_TYP_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_DOC_TYP_PROC_T MODIFY DOC_TYP_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_ACTN_ITM_T MODIFY ACTN_RQST_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_ACTN_ITM_T MODIFY RSP_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_ACTN_RQST_T MODIFY RSP_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_RULE_TMPL_ATTR_T MODIFY RULE_TMPL_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_RULE_TMPL_ATTR_T MODIFY RULE_ATTR_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_DLGN_RSP_T MODIFY RSP_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_DLGN_RSP_T MODIFY DLGN_RULE_BASE_VAL_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_RULE_RSP_T MODIFY RSP_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_RULE_RSP_T MODIFY RULE_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_RULE_EXT_T MODIFY RULE_TMPL_ATTR_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_RULE_EXT_T MODIFY RULE_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_RTE_NODE_INSTN_T MODIFY RTE_NODE_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_RTE_BRCH_ST_T MODIFY RTE_BRCH_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_RTE_NODE_INSTN_ST_T MODIFY RTE_NODE_INSTN_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_DOC_TYP_ATTR_T MODIFY  DOC_TYP_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_DOC_TYP_ATTR_T MODIFY RULE_ATTR_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_OUT_BOX_ITM_T MODIFY ACTN_RQST_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_OUT_BOX_ITM_T MODIFY RSP_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_RTE_NODE_CFG_PARM_T MODIFY RTE_NODE_ID VARCHAR(40) NOT NULL;

ALTER TABLE KREW_RULE_EXT_VAL_T MODIFY RULE_EXT_ID VARCHAR(40) NOT NULL;


-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
-- mysql-2012-10-25.sql
--


--
-- KULRICE-7509: Rice KIM documents stay editable after submission
--

delete from krim_role_perm_t where role_id = (select role_id from krim_role_t where role_nm = 'Initiator or Reviewer' and nmspc_cd = 'KR-WKFLW') AND
perm_id = (select perm_id from krim_perm_t where nm = 'Edit Kuali ENROUTE Document Route Status Code R' and nmspc_cd = 'KUALI')
;