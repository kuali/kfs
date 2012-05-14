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
-- 2010-02-15.sql
-- 


ALTER TABLE trv_acct ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE trv_acct ADD (VER_NBR NUMBER(8) DEFAULT 0)
/

ALTER TABLE trv_acct_type ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE trv_acct_type ADD (VER_NBR NUMBER(8) DEFAULT 0)
/

ALTER TABLE trv_acct_fo ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE trv_acct_fo ADD (VER_NBR NUMBER(8) DEFAULT 0)
/

ALTER TABLE trv_acct_ext ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE trv_acct_ext ADD (VER_NBR NUMBER(8) DEFAULT 0)
/





-- 
-- 2010-04-15.sql
-- 


ALTER TABLE krns_sesn_doc_t ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE krns_sesn_doc_t ADD (VER_NBR NUMBER(8) DEFAULT 0)
/





-- 
-- 2010-05-03.sql
-- 


ALTER TABLE KRIM_PND_GRP_ATTR_DATA_T ADD (ACTV_IND VARCHAR2(1) default 'Y'
                                        , EDIT_FLAG VARCHAR2(1) default 'N')
/




-- 
-- 2010-05-12.sql
-- 


alter table kren_chnl_subscrp_t add OBJ_ID varchar2(36)
/
alter table kren_cntnt_typ_t add OBJ_ID varchar2(36) 
/
alter table kren_chnl_t add OBJ_ID varchar2(36) 
/
alter table kren_ntfctn_msg_deliv_t add OBJ_ID varchar2(36) 
/
alter table kren_ntfctn_t add OBJ_ID varchar2(36) 
/
alter table kren_prio_t add OBJ_ID varchar2(36) 
/
alter table kren_prodcr_t add OBJ_ID varchar2(36) 
/
alter table kren_recip_list_t add OBJ_ID varchar2(36)
/
alter table kren_sndr_t add OBJ_ID varchar2(36)
/
alter table kren_recip_t add OBJ_ID varchar2(36) 
/
alter table kren_rvwer_t add OBJ_ID varchar2(36) 
/
alter table kren_chnl_subscrp_t add ver_nbr NUMBER(8)
/
alter table kren_recip_list_t add ver_nbr NUMBER(8)
/
alter table kren_sndr_t add ver_nbr NUMBER(8)
/
alter table kren_recip_t add ver_nbr NUMBER(8)
/




-- 
-- 2011-03-23.sql
-- 


update KREW_DOC_TYP_T set POST_PRCSR='org.kuali.rice.edl.framework.workflow.EDocLitePostProcessor'
where POST_PRCSR='org.kuali.rice.kew.edl.EDocLitePostProcessor'
/

update KREW_DOC_TYP_T set POST_PRCSR='org.kuali.rice.edl.framework.workflow.EDocLiteDatabasePostProcessor'
where POST_PRCSR='org.kuali.rice.kew.edl.EDLDatabasePostProcessor'
/

UPDATE KREW_DOC_TYP_T SET PARNT_ID='2681' WHERE DOC_TYP_NM='TravelAccountMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET PARNT_ID='2681' WHERE DOC_TYP_NM='FiscalOfficerMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET PARNT_ID='2681' WHERE DOC_TYP_NM='TravelRequest'
/





-- 
-- 2011-04-28.sql
-- 


--
-- KULRICE-4794
-- The following statements will change the DOC_HDR_ID from a decimal to a VARCHAR(40) on various tables. 
--
-- NOTE:  If a table is empty, one statement can be used to convert the DOC_HDR_ID as follows:
-- ALTER TABLE TABLE_NAME_HERE MODIFY (DOC_HDR_ID VARCHAR2(40))
--

--
-- KREW_ACTN_ITM_T 
--

ALTER TABLE KREW_ACTN_ITM_T RENAME TO OLD_KREW_ACTN_ITM_T
/
CREATE TABLE KREW_ACTN_ITM_T ( 
    ACTN_ITM_ID   	NUMBER(14,0),
    PRNCPL_ID     	VARCHAR2(40) NOT NULL,
    ASND_DT       	DATE NOT NULL,
    RQST_CD       	CHAR(1) NOT NULL,
    ACTN_RQST_ID  	NUMBER(14,0) NOT NULL,
    DOC_HDR_ID     	VARCHAR2(40) NOT NULL,
    ROLE_NM       	VARCHAR2(2000) NULL,
    DLGN_PRNCPL_ID	VARCHAR2(40) NULL,
    DOC_HDR_TTL   	VARCHAR2(255) NULL,
    DOC_TYP_LBL   	VARCHAR2(128) NOT NULL,
    DOC_HDLR_URL  	VARCHAR2(255) NOT NULL,
    DOC_TYP_NM    	VARCHAR2(64) NOT NULL,
    RSP_ID        	NUMBER(14,0) NOT NULL,
    DLGN_TYP      	VARCHAR2(1) NULL,
    VER_NBR       	NUMBER(8,0) DEFAULT 0 NULL,
    DTYPE         	VARCHAR2(50) NULL,
    GRP_ID        	VARCHAR2(40) NULL,
    DLGN_GRP_ID   	VARCHAR2(40) NULL,
    RQST_LBL      	VARCHAR2(255) NULL
)
/
INSERT INTO KREW_ACTN_ITM_T SELECT * FROM OLD_KREW_ACTN_ITM_T
/
DROP TABLE OLD_KREW_ACTN_ITM_T
/
ALTER TABLE KREW_ACTN_ITM_T ADD CONSTRAINT KREW_ACTN_ITM_TP1 PRIMARY KEY (ACTN_ITM_ID)
/
CREATE INDEX KREW_ACTN_ITM_TI1 ON KREW_ACTN_ITM_T(PRNCPL_ID)
/
CREATE INDEX KREW_ACTN_ITM_TI2 ON KREW_ACTN_ITM_T(DOC_HDR_ID)
/
CREATE INDEX KREW_ACTN_ITM_TI3 ON KREW_ACTN_ITM_T(ACTN_RQST_ID)
/
CREATE INDEX KREW_ACTN_ITM_TI5 ON KREW_ACTN_ITM_T(PRNCPL_ID, DLGN_TYP, DOC_HDR_ID)
/

--
-- KREW_ACTN_RQST_T 
-- Depending on the size of the table, this conversion can take a long time.  Example - takes 90 minutes for 38 million rows. 
--

ALTER TABLE KREW_ACTN_RQST_T RENAME TO OLD_KREW_ACTN_RQST_T
/
CREATE TABLE KREW_ACTN_RQST_T ( 
    ACTN_RQST_ID        	NUMBER(14,0),
    PARNT_ID            	NUMBER(14,0) NULL,
    ACTN_RQST_CD        	CHAR(1) NOT NULL,
    DOC_HDR_ID          	VARCHAR2(40) NOT NULL,
    RULE_ID             	NUMBER(19,0) NULL,
    STAT_CD             	CHAR(1) NOT NULL,
    RSP_ID              	NUMBER(14,0) NOT NULL,
    PRNCPL_ID           	VARCHAR2(40) NULL,
    ROLE_NM             	VARCHAR2(2000) NULL,
    QUAL_ROLE_NM        	VARCHAR2(2000) NULL,
    QUAL_ROLE_NM_LBL_TXT	VARCHAR2(2000) NULL,
    RECIP_TYP_CD        	CHAR(1) NULL,
    PRIO_NBR            	NUMBER(8,0) NOT NULL,
    RTE_TYP_NM          	VARCHAR2(255) NULL,
    RTE_LVL_NBR         	NUMBER(8,0) NOT NULL,
    RTE_NODE_INSTN_ID   	NUMBER(19,0) NULL,
    ACTN_TKN_ID         	NUMBER(14,0) NULL,
    DOC_VER_NBR         	NUMBER(8,0) NOT NULL,
    CRTE_DT             	DATE NOT NULL,
    RSP_DESC_TXT        	VARCHAR2(200) NULL,
    FRC_ACTN            	NUMBER(1,0) DEFAULT 0,
    ACTN_RQST_ANNOTN_TXT	VARCHAR2(2000) NULL,
    DLGN_TYP            	CHAR(1) NULL,
    APPR_PLCY           	CHAR(1) NULL,
    CUR_IND             	NUMBER(1,0) DEFAULT 1,
    VER_NBR             	NUMBER(8,0) DEFAULT 0,
    GRP_ID              	VARCHAR2(40) NULL,
    RQST_LBL            	VARCHAR2(255) NULL
)
/
INSERT INTO KREW_ACTN_RQST_T SELECT * FROM OLD_KREW_ACTN_RQST_T
/
DROP TABLE OLD_KREW_ACTN_RQST_T
/
ALTER TABLE KREW_ACTN_RQST_T ADD CONSTRAINT KREW_ACTN_RQST_TP1 PRIMARY KEY (ACTN_RQST_ID)
/
CREATE INDEX KREW_ACTN_RQST_T11 ON KREW_ACTN_RQST_T(DOC_HDR_ID)
/
CREATE INDEX KREW_ACTN_RQST_T12 ON KREW_ACTN_RQST_T(PRNCPL_ID)
/
CREATE INDEX KREW_ACTN_RQST_T13 ON KREW_ACTN_RQST_T(ACTN_TKN_ID)
/
CREATE INDEX KREW_ACTN_RQST_T14 ON KREW_ACTN_RQST_T(PARNT_ID)
/
CREATE INDEX KREW_ACTN_RQST_T15 ON KREW_ACTN_RQST_T(RSP_ID)
/
CREATE INDEX KREW_ACTN_RQST_T16 ON KREW_ACTN_RQST_T(STAT_CD, RSP_ID)
/
CREATE INDEX KREW_ACTN_RQST_T17 ON KREW_ACTN_RQST_T(RTE_NODE_INSTN_ID)
/
CREATE INDEX KREW_ACTN_RQST_T19 ON KREW_ACTN_RQST_T(STAT_CD, DOC_HDR_ID)
/

--
-- KREW_ACTN_TKN_T 
-- Depending on the size of the table, this conversion can take a long time.  Example - takes 10 minutes for 18 million rows. 
--

ALTER TABLE KREW_ACTN_TKN_T RENAME TO OLD_KREW_ACTN_TKN_T
/
CREATE TABLE KREW_ACTN_TKN_T ( 
    ACTN_TKN_ID    	NUMBER(14,0),
    DOC_HDR_ID      VARCHAR2(40) NOT NULL,
    PRNCPL_ID      	VARCHAR2(40) NOT NULL,
    DLGTR_PRNCPL_ID	VARCHAR2(40),
    ACTN_CD        	CHAR(1) NOT NULL,
    ACTN_DT        	DATE NOT NULL,
    DOC_VER_NBR    	NUMBER(8,0) NOT NULL,
    ANNOTN         	VARCHAR2(2000),
    CUR_IND        	NUMBER(1,0) DEFAULT 1,
    VER_NBR        	NUMBER(8,0) DEFAULT 0,
    DLGTR_GRP_ID   	VARCHAR2(40)
)
/
INSERT INTO KREW_ACTN_TKN_T SELECT * FROM OLD_KREW_ACTN_TKN_T
/
DROP TABLE OLD_KREW_ACTN_TKN_T
/
ALTER TABLE KREW_ACTN_TKN_T ADD CONSTRAINT KREW_ACTN_TKN_TP1 PRIMARY KEY (ACTN_TKN_ID)
/
CREATE INDEX KREW_ACTN_TKN_TI1 ON KREW_ACTN_TKN_T(DOC_HDR_ID, PRNCPL_ID)
/
CREATE INDEX KREW_ACTN_TKN_TI2 ON KREW_ACTN_TKN_T(DOC_HDR_ID, PRNCPL_ID, ACTN_CD)
/
CREATE INDEX KREW_ACTN_TKN_TI3 ON KREW_ACTN_TKN_T(PRNCPL_ID)
/
CREATE INDEX KREW_ACTN_TKN_TI4 ON KREW_ACTN_TKN_T(DLGTR_PRNCPL_ID)
/
CREATE INDEX KREW_ACTN_TKN_TI5 ON KREW_ACTN_TKN_T(DOC_HDR_ID)
/

--
-- KREW_APP_DOC_STAT_TRAN_T 
--

ALTER TABLE KREW_APP_DOC_STAT_TRAN_T RENAME TO OLD_KREW_APP_DOC_STAT_TRAN_T
/
CREATE TABLE KREW_APP_DOC_STAT_TRAN_T
(
      APP_DOC_STAT_TRAN_ID NUMBER(19),
      DOC_HDR_ID VARCHAR2(40),
      APP_DOC_STAT_FROM VARCHAR2(64),
      APP_DOC_STAT_TO VARCHAR2(64),
      STAT_TRANS_DATE DATE,
      VER_NBR NUMBER(8) default 0,
      OBJ_ID VARCHAR2(36) NOT NULL
)
/
INSERT INTO KREW_APP_DOC_STAT_TRAN_T SELECT * FROM OLD_KREW_APP_DOC_STAT_TRAN_T
/
DROP TABLE OLD_KREW_APP_DOC_STAT_TRAN_T
/
ALTER TABLE KREW_APP_DOC_STAT_TRAN_T ADD CONSTRAINT KREW_APP_DOC_STAT_TRAN_TP1 PRIMARY KEY(APP_DOC_STAT_TRAN_ID)
/
ALTER TABLE KREW_APP_DOC_STAT_TRAN_T ADD CONSTRAINT KREW_APP_DOC_STAT_TRAN_TC0 UNIQUE (OBJ_ID)
/
CREATE INDEX KREW_APP_DOC_STAT_TI1 ON KREW_APP_DOC_STAT_TRAN_T (DOC_HDR_ID, STAT_TRANS_DATE)
/
CREATE INDEX KREW_APP_DOC_STAT_TI2 ON KREW_APP_DOC_STAT_TRAN_T (DOC_HDR_ID, APP_DOC_STAT_FROM)
/
CREATE INDEX KREW_APP_DOC_STAT_TI3 ON KREW_APP_DOC_STAT_TRAN_T (DOC_HDR_ID, APP_DOC_STAT_TO)
/

--
-- KREW_DOC_HDR_CNTNT_T 
-- Depending on the size of the table, this conversion can take a long time.  Example - takes 45 minutes for 8 million rows 
-- 

ALTER TABLE KREW_DOC_HDR_CNTNT_T RENAME TO OLD_KREW_DOC_HDR_CNTNT_T
/
CREATE TABLE KREW_DOC_HDR_CNTNT_T ( 
    DOC_HDR_ID   	VARCHAR2(40),
    DOC_CNTNT_TXT	CLOB NULL
)
/
INSERT INTO KREW_DOC_HDR_CNTNT_T SELECT * FROM OLD_KREW_DOC_HDR_CNTNT_T
/
DROP TABLE OLD_KREW_DOC_HDR_CNTNT_T
/
ALTER TABLE KREW_DOC_HDR_CNTNT_T ADD CONSTRAINT KREW_DOC_HDR_CNTNT_TP1 PRIMARY KEY (DOC_HDR_ID)
/

--
-- KREW_DOC_HDR_EXT_DT_T 
-- 

ALTER TABLE KREW_DOC_HDR_EXT_DT_T RENAME TO OLD_KREW_DOC_HDR_EXT_DT_T
/
CREATE TABLE KREW_DOC_HDR_EXT_DT_T ( 
    DOC_HDR_EXT_DT_ID	NUMBER(19,0),
    DOC_HDR_ID   		VARCHAR2(40) NOT NULL,
    KEY_CD           	VARCHAR2(256) NOT NULL,
    VAL              	DATE NULL
)
/
INSERT INTO KREW_DOC_HDR_EXT_DT_T SELECT * FROM OLD_KREW_DOC_HDR_EXT_DT_T
/
DROP TABLE OLD_KREW_DOC_HDR_EXT_DT_T
/
ALTER TABLE KREW_DOC_HDR_EXT_DT_T ADD CONSTRAINT KREW_DOC_HDR_EXT_DT_TP1 PRIMARY KEY (DOC_HDR_EXT_DT_ID)
/
CREATE INDEX KREW_DOC_HDR_EXT_DT_TI1 ON KREW_DOC_HDR_EXT_DT_T(KEY_CD, VAL)
/
CREATE INDEX KREW_DOC_HDR_EXT_DT_TI2 ON KREW_DOC_HDR_EXT_DT_T(DOC_HDR_ID)
/
CREATE INDEX KREW_DOC_HDR_EXT_DT_TI3 ON KREW_DOC_HDR_EXT_DT_T(VAL)
/

--
-- KREW_DOC_HDR_EXT_LONG_T 
-- 

ALTER TABLE KREW_DOC_HDR_EXT_LONG_T RENAME TO OLD_KREW_DOC_HDR_EXT_LONG_T
/
CREATE TABLE KREW_DOC_HDR_EXT_LONG_T ( 
    DOC_HDR_EXT_LONG_ID	NUMBER(19,0),
    DOC_HDR_ID   		VARCHAR2(40) NOT NULL,
    KEY_CD             	VARCHAR2(256) NOT NULL,
    VAL                	NUMBER(22,0) NULL
)
/
INSERT INTO KREW_DOC_HDR_EXT_LONG_T SELECT * FROM OLD_KREW_DOC_HDR_EXT_LONG_T
/
DROP TABLE OLD_KREW_DOC_HDR_EXT_LONG_T
/
ALTER TABLE KREW_DOC_HDR_EXT_LONG_T ADD CONSTRAINT KREW_DOC_HDR_EXT_LONG_TP1 PRIMARY KEY (DOC_HDR_EXT_LONG_ID)
/
CREATE INDEX KREW_DOC_HDR_EXT_LONG_TI1 ON KREW_DOC_HDR_EXT_LONG_T(KEY_CD, VAL)
/
CREATE INDEX KREW_DOC_HDR_EXT_LONG_TI2 ON KREW_DOC_HDR_EXT_LONG_T(DOC_HDR_ID)
/
CREATE INDEX KREW_DOC_HDR_EXT_LONG_TI3 ON KREW_DOC_HDR_EXT_LONG_T(VAL)
/

--
-- KREW_DOC_HDR_EXT_FLT_T 
-- 

ALTER TABLE KREW_DOC_HDR_EXT_FLT_T RENAME TO OLD_KREW_DOC_HDR_EXT_FLT_T
/
CREATE TABLE KREW_DOC_HDR_EXT_FLT_T ( 
    DOC_HDR_EXT_FLT_ID	NUMBER(19,0),
	DOC_HDR_ID    		VARCHAR2(40) NOT NULL,
    KEY_CD            	VARCHAR2(256) NOT NULL,
    VAL               	NUMBER(30,15) NULL
)
/
INSERT INTO KREW_DOC_HDR_EXT_FLT_T SELECT * FROM OLD_KREW_DOC_HDR_EXT_FLT_T
/
DROP TABLE OLD_KREW_DOC_HDR_EXT_FLT_T
/
ALTER TABLE KREW_DOC_HDR_EXT_FLT_T ADD CONSTRAINT KREW_DOC_HDR_EXT_FLT_TP1 PRIMARY KEY (DOC_HDR_EXT_FLT_ID)
/
CREATE INDEX KREW_DOC_HDR_EXT_FLT_TI1 ON KREW_DOC_HDR_EXT_FLT_T(KEY_CD, VAL)
/
CREATE INDEX KREW_DOC_HDR_EXT_FLT_TI2 ON KREW_DOC_HDR_EXT_FLT_T(DOC_HDR_ID)
/
CREATE INDEX KREW_DOC_HDR_EXT_FLT_TI3 ON KREW_DOC_HDR_EXT_FLT_T(VAL)
/

--
-- KREW_DOC_HDR_EXT_T 
-- 

ALTER TABLE KREW_DOC_HDR_EXT_T RENAME TO OLD_KREW_DOC_HDR_EXT_T
/
CREATE TABLE KREW_DOC_HDR_EXT_T ( 
    DOC_HDR_EXT_ID	NUMBER(19,0),
	DOC_HDR_ID    	VARCHAR2(40) NOT NULL,
    KEY_CD        	VARCHAR2(256) NOT NULL,
    VAL           	VARCHAR2(2000)
)
/
INSERT INTO KREW_DOC_HDR_EXT_T SELECT * FROM OLD_KREW_DOC_HDR_EXT_T
/
DROP TABLE OLD_KREW_DOC_HDR_EXT_T
/
ALTER TABLE KREW_DOC_HDR_EXT_T ADD CONSTRAINT KREW_DOC_HDR_EXT_TP1 PRIMARY KEY (DOC_HDR_EXT_ID)
/
CREATE INDEX KREW_DOC_HDR_EXT_TI1 ON KREW_DOC_HDR_EXT_T(KEY_CD, VAL)
/
CREATE INDEX KREW_DOC_HDR_EXT_TI2 ON KREW_DOC_HDR_EXT_T(DOC_HDR_ID)
/
CREATE INDEX KREW_DOC_HDR_EXT_TI3 ON KREW_DOC_HDR_EXT_T(VAL)
/

--
-- KREW_DOC_HDR_T 
-- This SQL works in such a way that the column does not change positions in the table.
-- Done this way to limit disruption to existing indexes.
--

ALTER TABLE KREW_DOC_HDR_T ADD DOC_HDR_ID_TEMP VARCHAR2(40)
/
UPDATE KREW_DOC_HDR_T SET DOC_HDR_ID_TEMP = DOC_HDR_ID
/
ALTER TABLE KREW_DOC_HDR_T DROP CONSTRAINT KREW_DOC_HDR_TP1
/
UPDATE KREW_DOC_HDR_T SET DOC_HDR_ID = NULL
/
ALTER TABLE KREW_DOC_HDR_T MODIFY (DOC_HDR_ID VARCHAR2(40))
/
UPDATE KREW_DOC_HDR_T SET DOC_HDR_ID = DOC_HDR_ID_TEMP
/
ALTER TABLE KREW_DOC_HDR_T ADD CONSTRAINT KREW_DOC_HDR_TP1 PRIMARY KEY (DOC_HDR_ID)
/
ALTER TABLE KREW_DOC_HDR_T DROP COLUMN DOC_HDR_ID_TEMP
/

--
-- KREW_DOC_NTE_T 
--

ALTER TABLE KREW_DOC_NTE_T RENAME TO OLD_KREW_DOC_NTE_T
/
CREATE TABLE KREW_DOC_NTE_T ( 
    DOC_NTE_ID    	NUMBER(19,0),
	DOC_HDR_ID    	VARCHAR2(40) NOT NULL,
    AUTH_PRNCPL_ID	VARCHAR2(40) NOT NULL,
    CRT_DT        	DATE NOT NULL,
    TXT           	VARCHAR2(4000) NULL,
    VER_NBR       	NUMBER(8,0) DEFAULT 0 NULL
)
/
INSERT INTO KREW_DOC_NTE_T SELECT * FROM OLD_KREW_DOC_NTE_T
/
DROP TABLE OLD_KREW_DOC_NTE_T
/
ALTER TABLE KREW_DOC_NTE_T ADD CONSTRAINT KREW_DOC_NTE_TP1 PRIMARY KEY (DOC_NTE_ID)
/
CREATE INDEX KREW_DOC_NTE_TI1 ON KREW_DOC_NTE_T (DOC_HDR_ID)
/

--
-- KREW_DOC_TYP_T 
-- After this conversion, the doc_hdr_id column will be at the end of the table. 
-- Done this way to limit disruption to existing indexes.
--

ALTER TABLE KREW_DOC_TYP_T RENAME COLUMN DOC_HDR_ID TO DOC_HDR_ID_OLD
/
ALTER TABLE KREW_DOC_TYP_T ADD DOC_HDR_ID VARCHAR2(40)
/
UPDATE KREW_DOC_TYP_T SET DOC_HDR_ID = DOC_HDR_ID_OLD
/
ALTER TABLE KREW_DOC_TYP_T DROP COLUMN DOC_HDR_ID_OLD
/

--
-- KREW_EDL_DMP_T 
-- 

ALTER TABLE KREW_EDL_DMP_T RENAME TO OLD_KREW_EDL_DMP_T
/
CREATE TABLE KREW_EDL_DMP_T ( 
    DOC_HDR_ID             	VARCHAR2(40),
    DOC_TYP_NM             	VARCHAR2(64) NOT NULL,
    DOC_HDR_STAT_CD        	CHAR(1) NOT NULL,
    DOC_HDR_MDFN_DT        	DATE NOT NULL,
    DOC_HDR_CRTE_DT        	DATE NOT NULL,
    DOC_HDR_TTL            	VARCHAR2(255) NULL,
    DOC_HDR_INITR_PRNCPL_ID	VARCHAR2(40) NOT NULL,
    CRNT_NODE_NM           	VARCHAR2(30) NOT NULL,
    VER_NBR                	NUMBER(8,0) DEFAULT 0 NULL
)
/
INSERT INTO KREW_EDL_DMP_T SELECT * FROM OLD_KREW_EDL_DMP_T
/
DROP TABLE OLD_KREW_EDL_DMP_T
/
ALTER TABLE KREW_EDL_DMP_T ADD CONSTRAINT KREW_EDL_DMP_TP1 PRIMARY KEY (DOC_HDR_ID)
/
CREATE INDEX KREW_EDL_DMP_TI1 ON KREW_EDL_DMP_T(DOC_TYP_NM, DOC_HDR_ID)
/

--
-- KREW_EDL_FLD_DMP_T 
--

ALTER TABLE KREW_EDL_FLD_DMP_T RENAME TO OLD_KREW_EDL_FLD_DMP_T
/
CREATE TABLE KREW_EDL_FLD_DMP_T ( 
    EDL_FIELD_DMP_ID	NUMBER(14,0),
    DOC_HDR_ID      	VARCHAR2(40) NOT NULL,
    FLD_NM          	VARCHAR2(255) NOT NULL,
    FLD_VAL         	VARCHAR2(4000) NULL,
    VER_NBR         	NUMBER(8,0) DEFAULT 0 NULL
)
/
INSERT INTO KREW_EDL_FLD_DMP_T SELECT * FROM OLD_KREW_EDL_FLD_DMP_T
/
DROP TABLE OLD_KREW_EDL_FLD_DMP_T
/
ALTER TABLE KREW_EDL_FLD_DMP_T ADD CONSTRAINT KREW_EDL_FLD_DMP_TP1 PRIMARY KEY (EDL_FIELD_DMP_ID)
/

--
-- KREW_INIT_RTE_NODE_INSTN_T 
-- 

ALTER TABLE KREW_INIT_RTE_NODE_INSTN_T RENAME TO OLD_INIT_RTE_NODE_INSTN_T
/
CREATE TABLE KREW_INIT_RTE_NODE_INSTN_T ( 
    DOC_HDR_ID       	VARCHAR2(40),
    RTE_NODE_INSTN_ID	NUMBER(19,0)
)
/
INSERT INTO KREW_INIT_RTE_NODE_INSTN_T SELECT * FROM OLD_INIT_RTE_NODE_INSTN_T
/
DROP TABLE OLD_INIT_RTE_NODE_INSTN_T
/
ALTER TABLE KREW_INIT_RTE_NODE_INSTN_T ADD CONSTRAINT KREW_INIT_RTE_NODE_INSTN_TP1 PRIMARY KEY (DOC_HDR_ID, RTE_NODE_INSTN_ID)
/
CREATE INDEX KREW_INIT_RTE_NODE_INSTN_TI1 ON KREW_INIT_RTE_NODE_INSTN_T(DOC_HDR_ID)
/
CREATE INDEX KREW_INIT_RTE_NODE_INSTN_TI2 ON KREW_INIT_RTE_NODE_INSTN_T(RTE_NODE_INSTN_ID)
/

--
-- KREW_OUT_BOX_ITM_T 
-- 

ALTER TABLE KREW_OUT_BOX_ITM_T RENAME TO OLD_KREW_OUT_BOX_ITM_T
/
CREATE TABLE KREW_OUT_BOX_ITM_T ( 
    ACTN_ITM_ID   	NUMBER(14,0),
    PRNCPL_ID     	VARCHAR2(40) NOT NULL,
    ASND_DT       	DATE NOT NULL,
    RQST_CD       	CHAR(1) NOT NULL,
    ACTN_RQST_ID  	NUMBER(14,0) NOT NULL,
    DOC_HDR_ID    	VARCHAR2(40) NOT NULL,
    ROLE_NM       	VARCHAR2(2000) NULL,
    DLGN_PRNCPL_ID	VARCHAR2(40) NULL,
    DOC_HDR_TTL   	VARCHAR2(255) NULL,
    DOC_TYP_LBL   	VARCHAR2(128) NOT NULL,
    DOC_HDLR_URL  	VARCHAR2(255) NOT NULL,
    DOC_TYP_NM    	VARCHAR2(64) NOT NULL,
    RSP_ID        	NUMBER(14,0) NOT NULL,
    DLGN_TYP      	VARCHAR2(1) NULL,
    VER_NBR       	NUMBER(8,0) DEFAULT 0 NULL,
    GRP_ID        	VARCHAR2(40) NULL,
    DLGN_GRP_ID   	VARCHAR2(40) NULL,
    RQST_LBL      	VARCHAR2(255) NULL
)
/
INSERT INTO KREW_OUT_BOX_ITM_T SELECT * FROM OLD_KREW_OUT_BOX_ITM_T
/
DROP TABLE OLD_KREW_OUT_BOX_ITM_T
/
ALTER TABLE KREW_OUT_BOX_ITM_T ADD CONSTRAINT KREW_OUT_BOX_ITM_TP1 PRIMARY KEY (ACTN_ITM_ID)
/
CREATE INDEX KREW_OUT_BOX_ITM_TI1 ON KREW_OUT_BOX_ITM_T(PRNCPL_ID)
/
CREATE INDEX KREW_OUT_BOX_ITM_TI2 ON KREW_OUT_BOX_ITM_T(DOC_HDR_ID)
/
CREATE INDEX KREW_OUT_BOX_ITM_TI3 ON KREW_OUT_BOX_ITM_T(ACTN_RQST_ID)
/

--
-- KREW_RMV_RPLC_DOC_T 
--

ALTER TABLE KREW_RMV_RPLC_DOC_T RENAME TO OLD_KREW_RMV_RPLC_DOC_T
/
CREATE TABLE KREW_RMV_RPLC_DOC_T ( 
    DOC_HDR_ID  	VARCHAR2(40),
    OPRN          	CHAR(1) NOT NULL,
    PRNCPL_ID     	VARCHAR2(40) NOT NULL,
    RPLC_PRNCPL_ID	VARCHAR2(40) NULL,
    VER_NBR       	NUMBER(8,0) DEFAULT 0 NULL
)
/
INSERT INTO KREW_RMV_RPLC_DOC_T SELECT * FROM OLD_KREW_RMV_RPLC_DOC_T
/
DROP TABLE OLD_KREW_RMV_RPLC_DOC_T
/
ALTER TABLE KREW_RMV_RPLC_DOC_T ADD CONSTRAINT KREW_RMV_RPLC_DOC_TP1 PRIMARY KEY (DOC_HDR_ID)
/

--
-- KREW_RMV_RPLC_GRP_T 
--

ALTER TABLE KREW_RMV_RPLC_GRP_T RENAME TO OLD_KREW_RMV_RPLC_GRP_T
/
CREATE TABLE KREW_RMV_RPLC_GRP_T ( 
    DOC_HDR_ID  VARCHAR2(40),
    GRP_ID    	NUMBER(14,0)
)
/
INSERT INTO KREW_RMV_RPLC_GRP_T SELECT * FROM OLD_KREW_RMV_RPLC_GRP_T
/
DROP TABLE OLD_KREW_RMV_RPLC_GRP_T
/
ALTER TABLE KREW_RMV_RPLC_GRP_T ADD CONSTRAINT KREW_RMV_RPLC_GRP_TP1 PRIMARY KEY (DOC_HDR_ID, GRP_ID)
/

--
-- KREW_RMV_RPLC_RULE_T 
--

ALTER TABLE KREW_RMV_RPLC_RULE_T RENAME TO OLD_KREW_RMV_RPLC_RULE_T
/
CREATE TABLE KREW_RMV_RPLC_RULE_T ( 
    DOC_HDR_ID  VARCHAR2(40),
    RULE_ID   	NUMBER(19,0)
)
/
INSERT INTO KREW_RMV_RPLC_RULE_T SELECT * FROM OLD_KREW_RMV_RPLC_RULE_T
/
DROP TABLE OLD_KREW_RMV_RPLC_RULE_T
/
ALTER TABLE KREW_RMV_RPLC_RULE_T ADD CONSTRAINT KREW_RMV_RPLC_RULE_TP1 PRIMARY KEY (DOC_HDR_ID, RULE_ID)
/

--
-- KREW_RTE_NODE_INSTN_T 
-- 

ALTER TABLE KREW_RTE_NODE_INSTN_T RENAME TO OLD_KREW_RTE_NODE_INSTN_T
/
CREATE TABLE KREW_RTE_NODE_INSTN_T ( 
    RTE_NODE_INSTN_ID     	NUMBER(19,0),
    DOC_HDR_ID            	VARCHAR2(40) NOT NULL,
    RTE_NODE_ID           	NUMBER(19,0) NOT NULL,
    BRCH_ID               	NUMBER(19,0) NULL,
    PROC_RTE_NODE_INSTN_ID	NUMBER(19,0) NULL,
    ACTV_IND              	NUMBER(1,0) DEFAULT 0 NOT NULL,
    CMPLT_IND             	NUMBER(1,0) DEFAULT 0 NOT NULL,
    INIT_IND              	NUMBER(1,0) DEFAULT 0 NOT NULL,
    VER_NBR               	NUMBER(8,0) DEFAULT 0 NULL
)
/
INSERT INTO KREW_RTE_NODE_INSTN_T SELECT * FROM OLD_KREW_RTE_NODE_INSTN_T
/
DROP TABLE OLD_KREW_RTE_NODE_INSTN_T
/
ALTER TABLE KREW_RTE_NODE_INSTN_T ADD CONSTRAINT KREW_RTE_NODE_INSTN_TP1 PRIMARY KEY (RTE_NODE_INSTN_ID)
/
CREATE INDEX KREW_RTE_NODE_INSTN_TI1 ON KREW_RTE_NODE_INSTN_T(DOC_HDR_ID, ACTV_IND, CMPLT_IND)
/
CREATE INDEX KREW_RTE_NODE_INSTN_TI2 ON KREW_RTE_NODE_INSTN_T(RTE_NODE_ID)
/
CREATE INDEX KREW_RTE_NODE_INSTN_TI3 ON KREW_RTE_NODE_INSTN_T(BRCH_ID)
/
CREATE INDEX KREW_RTE_NODE_INSTN_TI4 ON KREW_RTE_NODE_INSTN_T(PROC_RTE_NODE_INSTN_ID)
/

--
-- KREW_RULE_T
--

ALTER TABLE KREW_RULE_T RENAME TO OLD_KREW_RULE_T
/
CREATE TABLE KREW_RULE_T ( 
    RULE_ID           	NUMBER(19,0),
    NM                	VARCHAR2(256) NULL,
    RULE_TMPL_ID      	NUMBER(19,0) NULL,
    RULE_EXPR_ID      	NUMBER(19,0) NULL,
    ACTV_IND          	NUMBER(1,0) NOT NULL,
    RULE_BASE_VAL_DESC	VARCHAR2(2000) NULL,
    FRC_ACTN          	NUMBER(1,0) NOT NULL,
    DOC_TYP_NM        	VARCHAR2(64) NOT NULL,
    DOC_HDR_ID          VARCHAR2(40) NULL,
    TMPL_RULE_IND     	NUMBER(1,0) NULL,
    FRM_DT            	DATE NULL,
    TO_DT             	DATE NULL,
    DACTVN_DT         	DATE NULL,
    CUR_IND           	NUMBER(1,0) DEFAULT 0 NULL,
    RULE_VER_NBR      	NUMBER(8,0) DEFAULT 0 NULL,
    DLGN_IND          	NUMBER(1,0) NULL,
    PREV_RULE_VER_NBR 	NUMBER(19,0) NULL,
    ACTVN_DT          	DATE NULL,
    VER_NBR           	NUMBER(8,0) DEFAULT 0 NULL,
    OBJ_ID            	VARCHAR2(36) NOT NULL
)
/
INSERT INTO KREW_RULE_T SELECT * FROM OLD_KREW_RULE_T
/
DROP TABLE OLD_KREW_RULE_T
/
ALTER TABLE KREW_RULE_T ADD CONSTRAINT KREW_RULE_TP1 PRIMARY KEY (RULE_ID)
/
ALTER TABLE KREW_RULE_T ADD CONSTRAINT KREW_RULE_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_RULE_T ADD CONSTRAINT KREW_RULE_TR1 FOREIGN KEY (RULE_EXPR_ID)
    REFERENCES KREW_RULE_EXPR_T (RULE_EXPR_ID)
/

--
-- KREW_DOC_LNK_T 
--

ALTER TABLE KREW_DOC_LNK_T RENAME TO OLD_KREW_DOC_LNK_T
/
create table KREW_DOC_LNK_T(
           DOC_LNK_ID NUMBER(19),
           ORGN_DOC_ID VARCHAR2(40) NOT NULL,
           DEST_DOC_ID VARCHAR2(40) NOT NULL
)
/
INSERT INTO KREW_DOC_LNK_T SELECT * FROM OLD_KREW_DOC_LNK_T
/
DROP TABLE OLD_KREW_DOC_LNK_T
/
ALTER TABLE KREW_DOC_LNK_T ADD CONSTRAINT KREW_DOC_LNK_TP1 PRIMARY KEY (DOC_LNK_ID)
/
create INDEX KREW_DOC_LNK_TI1 on krew_doc_lnk_t(ORGN_DOC_ID)
/





-- 
-- 2011-05-09.sql
-- 



--
--
-- DDL for KRMS repository
--
--

-- -----------------------------------------------------
-- Table krms_typ_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_typ_t'; exception when others then null; end;

CREATE  TABLE  krms_typ_t (
  typ_id VARCHAR2(40)  NOT NULL ,
  nm VARCHAR2(100)  NOT NULL ,
  nmspc_cd VARCHAR2(40)  NOT NULL ,
  srvc_nm VARCHAR2(200) NULL ,
  actv VARCHAR2(1) DEFAULT 'Y'  NOT NULL ,
  ver_nbr NUMBER(8) DEFAULT 0  NOT NULL ,
  PRIMARY KEY (typ_id) )
/



-- -----------------------------------------------------
-- Table krms_attr_defn_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_attr_defn_t'; exception when others then null; end;

CREATE  TABLE  krms_attr_defn_t (
  attr_defn_id VARCHAR2(255)  NOT NULL ,
  nm VARCHAR2(100)  NOT NULL ,
  nmspc_cd VARCHAR2(40)  NOT NULL ,
  lbl VARCHAR2(40) NULL ,
  actv VARCHAR2(1) DEFAULT 'Y'  NOT NULL ,
  cmpnt_nm VARCHAR2(100) NULL ,
  ver_nbr NUMBER(8) DEFAULT 0  NOT NULL ,
  PRIMARY KEY (attr_defn_id) )
/


-- -----------------------------------------------------
-- Table krms_typ_attr_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_typ_attr_t'; exception when others then null; end;

CREATE  TABLE  krms_typ_attr_t (
  typ_attr_id VARCHAR2(40)  NOT NULL ,
  seq_no NUMBER(5)  NOT NULL ,
  typ_id VARCHAR2(40)  NOT NULL ,
  attr_defn_id VARCHAR2(255)  NOT NULL ,
  actv VARCHAR2(1) DEFAULT 'Y'  NOT NULL ,
  ver_nbr NUMBER(8) DEFAULT 0  NOT NULL ,
  PRIMARY KEY (typ_attr_id) ,
  -- CREATE INDEX krms_typ_attr_ti1 (attr_defn_id ASC) ,
  -- CREATE INDEX krms_typ_attr_ti2 (typ_id ASC) ,
  CONSTRAINT krms_typ_attr_tc1 UNIQUE (typ_id, attr_defn_id) ,
  CONSTRAINT krms_typ_attr_fk1
    FOREIGN KEY (attr_defn_id )
    REFERENCES krms_attr_defn_t (attr_defn_id ),
  CONSTRAINT krms_typ_attr_fk2
    FOREIGN KEY (typ_id )
    REFERENCES krms_typ_t (typ_id ))
/

CREATE INDEX krms_typ_attr_ti1 on krms_typ_attr_t (attr_defn_id ASC) 
/
CREATE INDEX krms_typ_attr_ti2 on krms_typ_attr_t (typ_id ASC) 
/


-- -----------------------------------------------------
-- Table krms_rule_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_rule_t'; exception when others then null; end;

CREATE  TABLE  krms_rule_t (
  rule_id VARCHAR2(40)  NOT NULL ,
  nmspc_cd VARCHAR2(40)  NOT NULL ,
  nm VARCHAR2(100)  NOT NULL ,
  typ_id VARCHAR2(40)  NOT NULL ,
  prop_id VARCHAR2(40)  NOT NULL ,
  actv VARCHAR2(1) DEFAULT 'Y'  NOT NULL ,
  ver_nbr NUMBER(8) DEFAULT 0  NOT NULL ,
  descr_txt VARCHAR2(4000) NULL ,
  PRIMARY KEY (rule_id) 
  -- CREATE INDEX krms_rule_ti1 (prop_id ASC) ,
)
/

CREATE INDEX krms_rule_ti1 on krms_rule_t (prop_id ASC)
/

-- -----------------------------------------------------
-- Table krms_prop_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_prop_t'; exception when others then null; end;

CREATE  TABLE  krms_prop_t (
  prop_id VARCHAR2(40)  NOT NULL ,
  desc_txt VARCHAR2(100) NULL ,
  typ_id VARCHAR2(40)  NOT NULL ,
  dscrm_typ_cd VARCHAR2(10)  NOT NULL ,
  cmpnd_op_cd VARCHAR2(40) NULL ,
  rule_id VARCHAR2(40)  NOT NULL ,
  ver_nbr NUMBER(8) DEFAULT 0  NOT NULL ,
  PRIMARY KEY (prop_id) ,
  -- CREATE INDEX krms_prop_ti1 (rule_id ASC) ,
  -- CREATE INDEX krms_prop_fk2 (typ_id ASC) ,
  CONSTRAINT krms_prop_fk1
    FOREIGN KEY (rule_id )
    REFERENCES krms_rule_t (rule_id ) ,
  CONSTRAINT krms_prop_fk2
    FOREIGN KEY (typ_id )
    REFERENCES krms_typ_t (typ_id ) )
/

CREATE INDEX krms_prop_ti1 on krms_prop_t (rule_id ASC)
/
CREATE INDEX krms_prop_fk2 on krms_prop_t (typ_id ASC)
/


ALTER TABLE krms_rule_t ADD CONSTRAINT krms_rule_fk1
    FOREIGN KEY (prop_id )
    REFERENCES krms_prop_t (prop_id )
/


-- -----------------------------------------------------
-- Table krms_rule_attr_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_rule_attr_t'; exception when others then null; end;

CREATE  TABLE  krms_rule_attr_t (
  rule_attr_id VARCHAR2(40)  NOT NULL ,
  rule_id VARCHAR2(40)  NOT NULL ,
  attr_defn_id VARCHAR2(40)  NOT NULL ,
  attr_val VARCHAR2(400) NULL ,
  ver_nbr NUMBER(8) DEFAULT 0  NOT NULL ,
  PRIMARY KEY (rule_attr_id) ,
  -- CREATE INDEX krms_rule_attr_ti1 (rule_id ASC) ,
  -- CREATE INDEX krms_rule_attr_ti2 (attr_defn_id ASC) ,
  CONSTRAINT krms_rule_attr_fk1
    FOREIGN KEY (rule_id )
    REFERENCES krms_rule_t (rule_id ) ,
  CONSTRAINT krms_rule_attr_fk2
    FOREIGN KEY (attr_defn_id )
    REFERENCES krms_attr_defn_t (attr_defn_id ) )
/

CREATE INDEX krms_rule_attr_ti1 on krms_rule_attr_t (rule_id ASC) 
/
CREATE INDEX krms_rule_attr_ti2 on krms_rule_attr_t (attr_defn_id ASC) 
/


-- -----------------------------------------------------
-- Table krms_actn_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_actn_t'; exception when others then null; end;

CREATE  TABLE  krms_actn_t (
  actn_id VARCHAR2(40)  NOT NULL ,
  nm VARCHAR2(40) NULL ,
  desc_txt VARCHAR2(4000) NULL ,
  typ_id VARCHAR2(40)  NOT NULL ,
  rule_id VARCHAR2(40) NULL ,
  seq_no NUMBER(5) NULL ,
  ver_nbr NUMBER(8) DEFAULT 0  NOT NULL ,
  PRIMARY KEY (actn_id) ,
  -- CREATE INDEX KRMS_ACTN_TI2 (rule_id ASC) ,
  -- CREATE INDEX KRMS_ACTN_TI1 (typ_id ASC) ,
  CONSTRAINT KRMS_ACTN_TC2 UNIQUE (actn_id, rule_id, seq_no) ,
  -- CREATE INDEX KRMS_ACTN_TI3 (rule_id ASC, seq_no ASC) ,
  CONSTRAINT KRMS_ACTN_FK1
    FOREIGN KEY (rule_id )
    REFERENCES krms_rule_t (rule_id ) )
/

CREATE INDEX KRMS_ACTN_TI2 on krms_actn_t (rule_id ASC)
/
CREATE INDEX KRMS_ACTN_TI1 on krms_actn_t (typ_id ASC)
/
CREATE INDEX KRMS_ACTN_TI3 on krms_actn_t (rule_id ASC, seq_no ASC)
/


-- -----------------------------------------------------
-- Table krms_actn_attr_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_actn_attr_t'; exception when others then null; end;

CREATE  TABLE  krms_actn_attr_t (
  actn_attr_data_id VARCHAR2(40)  NOT NULL ,
  actn_id VARCHAR2(40)  NOT NULL ,
  attr_defn_id VARCHAR2(40)  NOT NULL ,
  attr_val VARCHAR2(400) NULL ,
  ver_nbr NUMBER(8) DEFAULT 0  NOT NULL ,
  PRIMARY KEY (actn_attr_data_id) ,
  -- CREATE INDEX krms_actn_attr_ti1 (actn_id ASC) ,
  -- CREATE INDEX krms_actn_attr_ti2 (attr_defn_id ASC) ,
  CONSTRAINT krms_actn_attr_fk1
    FOREIGN KEY (actn_id )
    REFERENCES krms_actn_t (actn_id ) ,
  CONSTRAINT krms_actn_attr_fk2
    FOREIGN KEY (attr_defn_id )
    REFERENCES krms_attr_defn_t (attr_defn_id ) )
/

CREATE INDEX krms_actn_attr_ti1 on krms_actn_attr_t (actn_id ASC) 
/
CREATE INDEX krms_actn_attr_ti2 on krms_actn_attr_t (attr_defn_id ASC) 
/


-- -----------------------------------------------------
-- Table krms_cmpnd_prop_props_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_cmpnd_prop_props_t'; exception when others then null; end;

CREATE  TABLE  krms_cmpnd_prop_props_t (
  cmpnd_prop_id VARCHAR2(40)  NOT NULL ,
  prop_id VARCHAR2(40)  NOT NULL ,
  seq_no NUMBER(5)  NOT NULL ,
  PRIMARY KEY (cmpnd_prop_id, prop_id) ,
  -- CREATE INDEX krms_cmpnd_prop_props_ti1 (prop_id ASC) ,
  -- CREATE INDEX krms_cmpnd_prop_props_fk2 (cmpnd_prop_id ASC) ,
  CONSTRAINT krms_cmpnd_prop_props_fk1
    FOREIGN KEY (prop_id )
    REFERENCES krms_prop_t (prop_id ) ,
  CONSTRAINT krms_cmpnd_prop_props_fk2
    FOREIGN KEY (cmpnd_prop_id )
    REFERENCES krms_prop_t (prop_id ) )
/

CREATE INDEX krms_cmpnd_prop_props_ti1 on krms_cmpnd_prop_props_t (prop_id ASC)
/
CREATE INDEX krms_cmpnd_prop_props_fk2 on krms_cmpnd_prop_props_t (cmpnd_prop_id ASC)
/


-- -----------------------------------------------------
-- Table krms_prop_parm_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_prop_parm_t'; exception when others then null; end;

CREATE  TABLE  krms_prop_parm_t (
  prop_parm_id VARCHAR2(40)  NOT NULL ,
  prop_id VARCHAR2(40)  NOT NULL ,
  parm_val VARCHAR2(255) NULL ,
  parm_typ_cd VARCHAR2(1)  NOT NULL ,
  seq_no NUMBER(5)  NOT NULL ,
  ver_nbr NUMBER(8) DEFAULT 0  NOT NULL ,
  PRIMARY KEY (prop_parm_id) ,
  -- CREATE INDEX krms_prop_parm_ti1 (prop_id ASC) ,
  CONSTRAINT krms_prop_parm_fk1
    FOREIGN KEY (prop_id )
    REFERENCES krms_prop_t (prop_id ))
/

CREATE INDEX krms_prop_parm_ti1 ON krms_prop_parm_t (prop_id ASC)
/




-- -----------------------------------------------------
-- Table krms_cntxt_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_cntxt_t'; exception when others then null; end;

CREATE  TABLE  krms_cntxt_t (
  cntxt_id VARCHAR2(40)  NOT NULL ,
  nmspc_cd VARCHAR2(40)  NOT NULL ,
  nm VARCHAR2(100)  NOT NULL ,
  typ_id VARCHAR2(40)  NOT NULL ,
  actv VARCHAR2(1) DEFAULT 'Y'  NOT NULL ,
  ver_nbr NUMBER(8) DEFAULT 0  NOT NULL ,
  PRIMARY KEY (cntxt_id) )
/




-- -----------------------------------------------------
-- Table krms_agenda_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_agenda_t'; exception when others then null; end;

CREATE  TABLE  krms_agenda_t (
  agenda_id VARCHAR2(40)  NOT NULL ,
  nmspc_cd VARCHAR2(40)  NOT NULL ,
  nm VARCHAR2(100)  NOT NULL ,
  cntxt_id VARCHAR2(40)  NOT NULL ,
  init_agenda_itm_id VARCHAR2(40) NULL ,
  typ_id VARCHAR2(40)  NOT NULL ,
  actv VARCHAR2(1) DEFAULT 'Y'  NOT NULL ,
  ver_nbr NUMBER(8) DEFAULT 0  NOT NULL ,
  PRIMARY KEY (agenda_id) ,
  -- CREATE INDEX krms_agenda_ti1 (cntxt_id ASC) ,
  CONSTRAINT krms_agenda_fk1
    FOREIGN KEY (cntxt_id )
    REFERENCES krms_cntxt_t (cntxt_id ) )
/

CREATE INDEX krms_agenda_ti1 on krms_agenda_t (cntxt_id ASC)
/




-- -----------------------------------------------------
-- Table krms_agenda_attr_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_agenda_attr_t'; exception when others then null; end;

CREATE  TABLE  krms_agenda_attr_t (
  agenda_attr_id VARCHAR2(40)  NOT NULL ,
  agenda_id VARCHAR2(40)  NOT NULL ,
  attr_val VARCHAR2(400) NULL ,
  attr_defn_id VARCHAR2(40)  NOT NULL ,
  ver_nbr NUMBER(8) DEFAULT 0  NOT NULL ,
  PRIMARY KEY (agenda_attr_id) ,
  -- CREATE INDEX krms_agenda_attr_ti1 (agenda_id ASC) ,
  -- CREATE INDEX krms_agenda_attr_t12 (attr_defn_id ASC) ,
  CONSTRAINT krms_agenda_attr_fk1
    FOREIGN KEY (agenda_id )
    REFERENCES krms_agenda_t (agenda_id ),
  CONSTRAINT krms_agenda_attr_fk2
    FOREIGN KEY (attr_defn_id )
    REFERENCES krms_attr_defn_t (attr_defn_id ))
/

CREATE INDEX krms_agenda_attr_ti1 on krms_agenda_attr_t (agenda_id ASC)
/
CREATE INDEX krms_agenda_attr_t12 on krms_agenda_attr_t (attr_defn_id ASC)
/


-- -----------------------------------------------------
-- Table krms_agenda_itm_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_agenda_itm_t'; exception when others then null; end;

CREATE  TABLE  krms_agenda_itm_t (
  agenda_itm_id VARCHAR2(40)  NOT NULL ,
  rule_id VARCHAR2(40) NULL ,
  sub_agenda_id VARCHAR2(40) NULL ,
  agenda_id VARCHAR2(40)  NOT NULL ,
  ver_nbr NUMBER(8) DEFAULT 0  NOT NULL ,
  when_true VARCHAR2(40) NULL ,
  when_false VARCHAR2(40) NULL ,
  always VARCHAR2(40) NULL ,
  PRIMARY KEY (agenda_itm_id) ,
  -- CREATE INDEX krms_agenda_itm_ti1 (rule_id ASC) ,
  -- CREATE INDEX krms_agenda_itm_ti2 (agenda_id ASC) ,
  -- CREATE INDEX krms_agenda_itm_ti3 (sub_agenda_id ASC) ,
  -- CREATE INDEX krms_agenda_itm_ti4 (when_true ASC) ,
  -- CREATE INDEX krms_agenda_itm_ti5 (when_false ASC) ,
  -- CREATE INDEX krms_agenda_itm_ti6 (always ASC) ,
  CONSTRAINT krms_agenda_itm_fk1
    FOREIGN KEY (rule_id )
    REFERENCES krms_rule_t (rule_id ) ,
  CONSTRAINT krms_agenda_itm_fk2
    FOREIGN KEY (agenda_id )
    REFERENCES krms_agenda_t (agenda_id ) ,
  CONSTRAINT krms_agenda_itm_fk3
    FOREIGN KEY (sub_agenda_id )
    REFERENCES krms_agenda_t (agenda_id ) ,
  CONSTRAINT krms_agenda_itm_fk4
    FOREIGN KEY (when_true )
    REFERENCES krms_agenda_itm_t (agenda_itm_id ) ,
  CONSTRAINT krms_agenda_itm_fk5
    FOREIGN KEY (when_false )
    REFERENCES krms_agenda_itm_t (agenda_itm_id ) ,
  CONSTRAINT krms_agenda_itm_fk6
    FOREIGN KEY (always )
    REFERENCES krms_agenda_itm_t (agenda_itm_id ) )
/

CREATE INDEX krms_agenda_itm_ti1 on krms_agenda_itm_t (rule_id ASC) 
/
CREATE INDEX krms_agenda_itm_ti2 on krms_agenda_itm_t (agenda_id ASC) 
/
CREATE INDEX krms_agenda_itm_ti3 on krms_agenda_itm_t (sub_agenda_id ASC) 
/
CREATE INDEX krms_agenda_itm_ti4 on krms_agenda_itm_t (when_true ASC) 
/
CREATE INDEX krms_agenda_itm_ti5 on krms_agenda_itm_t (when_false ASC) 
/
CREATE INDEX krms_agenda_itm_ti6 on krms_agenda_itm_t (always ASC) 
/



-- -----------------------------------------------------
-- Table krms_func_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_func_t'; exception when others then null; end;

CREATE  TABLE  krms_func_t (
  func_id VARCHAR2(40)  NOT NULL ,
  nmspc_cd VARCHAR2(40)  NOT NULL ,
  nm VARCHAR2(100)  NOT NULL ,
  desc_txt VARCHAR2(255) NULL ,
  rtrn_typ VARCHAR2(255)  NOT NULL ,
  typ_id VARCHAR2(40)  NOT NULL ,
  actv VARCHAR2(1) DEFAULT 'Y'  NOT NULL ,
  ver_nbr NUMBER(8) DEFAULT 0  NOT NULL ,
  PRIMARY KEY (func_id) ,
  -- CREATE INDEX krms_func_ti1 (typ_id ASC) ,
  CONSTRAINT krms_func_fk1
    FOREIGN KEY (typ_id )
    REFERENCES krms_typ_t (typ_id ))
/

CREATE INDEX krms_func_ti1 on krms_func_t (typ_id ASC)
/



-- -----------------------------------------------------
-- Table krms_func_parm_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_func_parm_t'; exception when others then null; end;

CREATE  TABLE  krms_func_parm_t (
  func_parm_id VARCHAR2(40)  NOT NULL ,
  nm VARCHAR2(100)  NOT NULL ,
  desc_txt VARCHAR2(255) NULL ,
  typ VARCHAR2(255)  NOT NULL ,
  func_id VARCHAR2(40)  NOT NULL ,
  seq_no NUMBER(5)  NOT NULL ,
  PRIMARY KEY (func_parm_id) ,
  -- CREATE INDEX krms_func_parm_ti1 (func_id ASC) ,
  CONSTRAINT krms_func_parm_fk1
    FOREIGN KEY (func_id )
    REFERENCES krms_func_t (func_id ) )
/

CREATE INDEX krms_func_parm_ti1 on krms_func_parm_t (func_id ASC)
/

-- -----------------------------------------------------
-- Table krms_term_spec_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_term_spec_t'; exception when others then null; end;

CREATE  TABLE  krms_term_spec_t (
  term_spec_id VARCHAR2(40)  NOT NULL ,
  cntxt_id VARCHAR2(40) NULL ,
  nm VARCHAR2(255)  NOT NULL ,
  typ VARCHAR2(255)  NOT NULL ,
  actv VARCHAR2(1) DEFAULT 'Y'  NOT NULL ,
  ver_nbr NUMBER(8)  NOT NULL ,
  PRIMARY KEY (term_spec_id) ,
  CONSTRAINT krms_asset_tc1 UNIQUE (nm, cntxt_id) ,
  -- CREATE INDEX krms_asset_ti1 (cntxt_id ASC) ,
  CONSTRAINT krms_asset_fk1
    FOREIGN KEY (cntxt_id )
    REFERENCES krms_cntxt_t (cntxt_id ) )
/

CREATE INDEX krms_asset_ti1 on krms_term_spec_t (cntxt_id ASC)
/




-- -----------------------------------------------------
-- Table krms_term_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_term_t'; exception when others then null; end;

CREATE  TABLE  krms_term_t (
  term_id VARCHAR2(40)  NOT NULL ,
  term_spec_id VARCHAR2(40)  NOT NULL ,
  ver_nbr NUMBER(8)  NOT NULL ,
  PRIMARY KEY (term_id) ,
  -- CREATE INDEX krms_term_ti1 (term_spec_id ASC) ,
  CONSTRAINT krms_term_t__fk1
    FOREIGN KEY (term_spec_id )
    REFERENCES krms_term_spec_t (term_spec_id ) )
/

CREATE INDEX krms_term_ti1 on krms_term_t (term_spec_id ASC)
/


-- -----------------------------------------------------
-- Table krms_term_parm_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_term_parm_t'; exception when others then null; end;

CREATE  TABLE  krms_term_parm_t (
  term_parm_id VARCHAR2(40)  NOT NULL ,
  term_id VARCHAR2(40)  NOT NULL ,
  nm VARCHAR2(255)  NOT NULL ,
  val VARCHAR2(255) NULL ,
  ver_nbr NUMBER(8)  NOT NULL ,
  PRIMARY KEY (term_parm_id) ,
  -- CREATE INDEX krms_term_parm_ti1 (term_id ASC) ,
  CONSTRAINT krms_term_parm_fk1
    FOREIGN KEY (term_id )
    REFERENCES krms_term_t (term_id ))
/

CREATE INDEX krms_term_parm_ti1 on krms_term_parm_t (term_id ASC)
/


-- -----------------------------------------------------
-- Table krms_cntxt_term_spec_prereq_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_cntxt_term_spec_prereq_t'; exception when others then null; end;

CREATE  TABLE  krms_cntxt_term_spec_prereq_t (
  cntxt_term_spec_prereq_id VARCHAR2(40)  NOT NULL ,
  cntxt_id VARCHAR2(40)  NOT NULL ,
  term_spec_id VARCHAR2(40)  NOT NULL ,
  PRIMARY KEY (cntxt_term_spec_prereq_id) ,
  -- CREATE INDEX krms_cntxt_asset_prereq_ti1 (cntxt_id ASC) ,
  -- CREATE INDEX krms_cntxt_asset_prereq_ti2 (term_spec_id ASC) ,
  CONSTRAINT krms_cntxt_asset_prereq_fk1
    FOREIGN KEY (cntxt_id )
    REFERENCES krms_cntxt_t (cntxt_id ) ,
  CONSTRAINT krms_cntxt_asset_prereq_fk2
    FOREIGN KEY (term_spec_id )
    REFERENCES krms_term_spec_t (term_spec_id ) )
/

CREATE INDEX krms_cntxt_asset_prereq_ti1 on krms_cntxt_term_spec_prereq_t (cntxt_id ASC)
/
CREATE INDEX krms_cntxt_asset_prereq_ti2 on krms_cntxt_term_spec_prereq_t (term_spec_id ASC)
/



-- -----------------------------------------------------
-- Table krms_term_rslvr_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_term_rslvr_t'; exception when others then null; end;

CREATE  TABLE  krms_term_rslvr_t (
  term_rslvr_id VARCHAR2(40)  NOT NULL ,
  nmspc_cd VARCHAR2(40)  NOT NULL ,
  nm VARCHAR2(100)  NOT NULL ,
  typ_id VARCHAR2(40)  NOT NULL ,
  output_term_spec_id VARCHAR2(40)  NOT NULL ,
  cntxt_id VARCHAR2(40) NULL ,
  actv VARCHAR2(1) DEFAULT 'Y'  NOT NULL ,
  ver_nbr NUMBER(8) DEFAULT 0  NOT NULL ,
  PRIMARY KEY (term_rslvr_id) ,
  -- CREATE INDEX krms_term_rslvr_ti1 (cntxt_id ASC) ,
  -- CREATE INDEX krms_term_rslvr_ti2 (typ_id ASC) ,
  CONSTRAINT krms_term_rslvr_tc1 UNIQUE (nmspc_cd, nm, cntxt_id) ,
  CONSTRAINT krms_term_rslvr_fk1
    FOREIGN KEY (output_term_spec_id )
    REFERENCES krms_term_spec_t (term_spec_id ) ,
  CONSTRAINT krms_term_rslvr_fk2
    FOREIGN KEY (cntxt_id )
    REFERENCES krms_cntxt_t (cntxt_id ) ,
  CONSTRAINT krms_term_rslvr_fk3
    FOREIGN KEY (typ_id )
    REFERENCES krms_typ_t (typ_id ) )
/

CREATE INDEX krms_term_rslvr_ti1 on krms_term_rslvr_t (cntxt_id ASC) 
/
CREATE INDEX krms_term_rslvr_ti2 on krms_term_rslvr_t (typ_id ASC) 
/

     --
     ----
------------
---------------
------------
     ----
     --


-- -----------------------------------------------------
-- Table krms_cntxt_attr_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_cntxt_attr_t'; exception when others then null; end;

CREATE  TABLE  krms_cntxt_attr_t (
  cntxt_attr_id VARCHAR2(40)  NOT NULL ,
  cntxt_id VARCHAR2(40)  NOT NULL ,
  attr_val VARCHAR2(400) NULL ,
  attr_defn_id VARCHAR2(40) NULL ,
  ver_nbr NUMBER(8) DEFAULT 0  NOT NULL ,
  PRIMARY KEY (cntxt_attr_id) ,
  -- CREATE INDEX krms_cntxt_attr_ti1 (cntxt_id ASC) ,
  -- CREATE INDEX krms_cntxt_attr_ti2 (attr_defn_id ASC) ,
  CONSTRAINT krms_cntxt_attr_fk1
    FOREIGN KEY (cntxt_id )
    REFERENCES krms_cntxt_t (cntxt_id ) ,
  CONSTRAINT krms_cntxt_attr_fk2
    FOREIGN KEY (attr_defn_id )
    REFERENCES krms_attr_defn_t (attr_defn_id ) )
/

CREATE INDEX krms_cntxt_attr_ti1 on krms_cntxt_attr_t (cntxt_id ASC)
/
CREATE INDEX krms_cntxt_attr_ti2 on krms_cntxt_attr_t (attr_defn_id ASC)
/


-- -----------------------------------------------------
-- Table krms_cntxt_vld_actn_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_cntxt_vld_actn_t'; exception when others then null; end;

CREATE  TABLE  krms_cntxt_vld_actn_t (
  cntxt_vld_actn_id VARCHAR2(40)  NOT NULL ,
  cntxt_id VARCHAR2(40)  NOT NULL ,
  actn_typ_id VARCHAR2(40)  NOT NULL ,
  ver_nbr NUMBER(8) DEFAULT 0  NOT NULL ,
  PRIMARY KEY (cntxt_vld_actn_id) ,
  -- CREATE INDEX krms_cntxt_vld_actn_ti1 (cntxt_id ASC) ,
  CONSTRAINT krms_cntxt_vld_actn_fk1
    FOREIGN KEY (cntxt_id )
    REFERENCES krms_cntxt_t (cntxt_id ) )
/

CREATE INDEX krms_cntxt_vld_actn_ti1 on krms_cntxt_vld_actn_t (cntxt_id ASC)
/


-- -----------------------------------------------------
-- Table krms_term_rslvr_attr_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_term_rslvr_attr_t'; exception when others then null; end;

CREATE  TABLE  krms_term_rslvr_attr_t (
  term_rslvr_attr_id VARCHAR2(40)  NOT NULL ,
  term_rslvr_id VARCHAR2(40)  NOT NULL ,
  attr_defn_id VARCHAR2(40)  NOT NULL ,
  attr_val VARCHAR2(400) NULL ,
  ver_nbr NUMBER(8) DEFAULT 0  NOT NULL ,
  PRIMARY KEY (term_rslvr_attr_id) ,
  -- CREATE INDEX krms_asset_rslvr_attr_ti1 (term_rslvr_id ASC) ,
  -- CREATE INDEX krms_asset_rslvr_attr_ti2 (attr_defn_id ASC) ,
  CONSTRAINT krms_asset_rslvr_attr_fk1
    FOREIGN KEY (term_rslvr_id )
    REFERENCES krms_term_rslvr_t (term_rslvr_id ) ,
  CONSTRAINT krms_asset_rslvr_attr_fk2
    FOREIGN KEY (attr_defn_id )
    REFERENCES krms_attr_defn_t (attr_defn_id ) )
/

CREATE INDEX krms_asset_rslvr_attr_ti1 on krms_term_rslvr_attr_t (term_rslvr_id ASC)
/
CREATE INDEX krms_asset_rslvr_attr_ti2 on krms_term_rslvr_attr_t (attr_defn_id ASC)
/


-- -----------------------------------------------------
-- Table krms_term_rslvr_input_spec_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_term_rslvr_input_spec_t'; exception when others then null; end;

CREATE  TABLE  krms_term_rslvr_input_spec_t (
  term_spec_id VARCHAR2(40)  NOT NULL ,
  term_rslvr_id VARCHAR2(40)  NOT NULL ,
  -- CREATE INDEX krms_input_asset_ti1 (term_spec_id ASC) ,
  -- CREATE INDEX krms_input_asset_ti2 (term_rslvr_id ASC) ,
  PRIMARY KEY (term_spec_id, term_rslvr_id) ,
  CONSTRAINT krms_input_asset_fk2
    FOREIGN KEY (term_spec_id )
    REFERENCES krms_term_spec_t (term_spec_id ) ,
  CONSTRAINT krms_input_asset_fk1
    FOREIGN KEY (term_rslvr_id )
    REFERENCES krms_term_rslvr_t (term_rslvr_id ) )
/

CREATE INDEX krms_input_asset_ti1 on krms_term_rslvr_input_spec_t (term_spec_id ASC)
/
CREATE INDEX krms_input_asset_ti2 on krms_term_rslvr_input_spec_t (term_rslvr_id ASC)
/



-- -----------------------------------------------------
-- Table krms_term_rslvr_parm_spec_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_term_rslvr_parm_spec_t'; exception when others then null; end;

CREATE  TABLE  krms_term_rslvr_parm_spec_t (
  term_rslvr_parm_spec_id VARCHAR2(40)  NOT NULL ,
  term_rslvr_id VARCHAR2(40)  NOT NULL ,
  nm VARCHAR2(45)  NOT NULL ,
  ver_nbr NUMBER(8)  NOT NULL ,
  PRIMARY KEY (term_rslvr_parm_spec_id) ,
  -- CREATE INDEX krms_term_reslv_parm_fk1 (term_rslvr_id ASC) ,
  CONSTRAINT krms_term_reslv_parm_fk1
    FOREIGN KEY (term_rslvr_id )
    REFERENCES krms_term_rslvr_t (term_rslvr_id ) )
/

CREATE INDEX krms_term_reslv_parm_fk1 on krms_term_rslvr_parm_spec_t (term_rslvr_id ASC)
/

-- -----------------------------------------------------
-- Table krms_cntxt_vld_func_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_cntxt_vld_func_t'; exception when others then null; end;

CREATE  TABLE  krms_cntxt_vld_func_t (
  cntxt_vld_func_id VARCHAR2(40)  NOT NULL ,
  cntxt_id VARCHAR2(40)  NOT NULL ,
  func_id VARCHAR2(40)  NOT NULL ,
  ver_nbr NUMBER(8) DEFAULT 0  NOT NULL ,
  PRIMARY KEY (cntxt_vld_func_id) ,
  -- CREATE INDEX krms_cntxt_vld_func_ti1 (cntxt_id ASC) ,
  CONSTRAINT krms_cntxt_vld_func_fk1
    FOREIGN KEY (cntxt_id )
    REFERENCES krms_cntxt_t (cntxt_id ) )
/

CREATE INDEX krms_cntxt_vld_func_ti1 on krms_cntxt_vld_func_t (cntxt_id ASC)
/


-- -----------------------------------------------------
-- Table krms_cntxt_vld_rule_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_cntxt_vld_rule_t'; exception when others then null; end;

CREATE  TABLE  krms_cntxt_vld_rule_t (
  cntxt_vld_rule_id VARCHAR2(40)  NOT NULL ,
  cntxt_id VARCHAR2(40)  NOT NULL ,
  rule_id VARCHAR2(40)  NOT NULL ,
  ver_nbr NUMBER(8) DEFAULT 0  NOT NULL ,
  PRIMARY KEY (cntxt_vld_rule_id) ,
  -- CREATE INDEX krms_cntxt_vld_rule_ti1 (cntxt_id ASC) ,
  CONSTRAINT krms_cntxt_vld_rule_fk1
    FOREIGN KEY (cntxt_id )
    REFERENCES krms_cntxt_t (cntxt_id ) )
/

CREATE INDEX krms_cntxt_vld_rule_ti1 on krms_cntxt_vld_rule_t (cntxt_id ASC)
/

-- -----------------------------------------------------
-- Table krms_cntxt_vld_event_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_cntxt_vld_event_t'; exception when others then null; end;

CREATE  TABLE  krms_cntxt_vld_event_t (
  cntxt_vld_event_id VARCHAR2(40)  NOT NULL ,
  cntxt_id VARCHAR2(40)  NOT NULL ,
  event_nm VARCHAR2(255)  NOT NULL ,
  ver_nbr NUMBER(8) DEFAULT 0  NOT NULL ,
  PRIMARY KEY (cntxt_vld_event_id) ,
  -- CREATE INDEX krms_cntxt_vld_event_ti1 (cntxt_id ASC) ,
  CONSTRAINT krms_cntxt_vld_event_fk1
    FOREIGN KEY (cntxt_id )
    REFERENCES krms_cntxt_t (cntxt_id ) )
/

CREATE INDEX krms_cntxt_vld_event_ti1 on krms_cntxt_vld_event_t (cntxt_id ASC)
/


--
--
-- sequences
--
--


CREATE SEQUENCE krms_typ_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_prop_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_rule_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_cntxt_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_agenda_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_attr_defn_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_typ_attr_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_actn_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_actn_attr_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_agenda_itm_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_rule_attr_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_cntxt_attr_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_cntxt_vld_actn_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_agenda_attr_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_cmpnd_prop_props_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_prop_parm_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_term_spec_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_term_rslvr_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_term_rslvr_attr_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_term_rslvr_input_spec_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_cntxt_term_spec_prereq_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_term_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_term_rslvr_parm_spec_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_term_parm_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_func_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_func_parm_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_cntxt_vld_func_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_cntxt_vld_rule_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE SEQUENCE krms_cntxt_vld_event_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/





-- 
-- 2011-05-13.sql
-- 


drop table KRSB_SVC_DEF_T
/
drop table KRSB_FLT_SVC_DEF_T
/
drop sequence KRSB_SVC_DEF_S
/
drop sequence KRSB_FLT_SVC_DEF_S
/
CREATE TABLE KRSB_SVC_DSCRPTR_T (
  SVC_DSCRPTR_ID varchar2(40) NOT NULL,
  DSCRPTR clob NOT NULL,
  PRIMARY KEY (SVC_DSCRPTR_ID)
)
/
CREATE SEQUENCE KRSB_SVC_DSCRPTR_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/
CREATE TABLE KRSB_SVC_DEF_T (
  SVC_DEF_ID varchar2(40) NOT NULL,
  SVC_NM varchar2(255) NOT NULL,
  SVC_URL varchar2(500) NOT NULL,
  INSTN_ID varchar2(255) NOT NULL,
  APPL_NMSPC varchar2(255) NOT NULL,
  SRVR_IP varchar2(40) NOT NULL,
  TYP_CD varchar2(40) NOT NULL,
  SVC_VER varchar2(40) NOT NULL,
  STAT_CD varchar2(1) NOT NULL,
  SVC_DSCRPTR_ID varchar2(40) NOT NULL,
  CHKSM varchar2(30) NOT NULL,
  VER_NBR number(8) DEFAULT 0 NOT NULL,
  PRIMARY KEY (SVC_DEF_ID),
  CONSTRAINT KRSB_SVC_DEF_FK1
    FOREIGN KEY (SVC_DSCRPTR_ID)
    REFERENCES KRSB_SVC_DSCRPTR_T(SVC_DSCRPTR_ID) ON DELETE CASCADE
)
/
CREATE SEQUENCE KRSB_SVC_DEF_S INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/
CREATE INDEX KRSB_SVC_DEF_TI1 on KRSB_SVC_DEF_T (INSTN_ID)
/
CREATE INDEX KRSB_SVC_DEF_TI2 on KRSB_SVC_DEF_T (SVC_NM, STAT_CD)
/
CREATE INDEX KRSB_SVC_DEF_TI3 on KRSB_SVC_DEF_T (STAT_CD)
/





-- 
-- 2011-06-06.sql
-- 


ALTER TABLE KREW_DOC_TYP_T RENAME COLUMN SVC_NMSPC TO APPL_ID
/
ALTER TABLE KREW_RULE_ATTR_T RENAME COLUMN SVC_NMSPC TO APPL_ID
/
ALTER TABLE KRSB_SVC_DEF_T RENAME COLUMN APPL_NMSPC TO APPL_ID
/
ALTER TABLE KRSB_MSG_QUE_T RENAME COLUMN SVC_NMSPC TO APPL_ID
/
ALTER TABLE KRNS_NMSPC_T RENAME COLUMN APPL_NMSPC_CD TO APPL_ID
/
ALTER TABLE KRNS_PARM_T RENAME COLUMN APPL_NMSPC_CD TO APPL_ID
/

ALTER TABLE KRNS_NMSPC_T RENAME TO KRCR_NMSPC_T
/
ALTER TABLE KRNS_PARM_TYP_T RENAME TO KRCR_PARM_TYP_T
/
ALTER TABLE KRNS_PARM_DTL_TYP_T RENAME TO KRCR_PARM_DTL_TYP_T
/
ALTER TABLE KRNS_PARM_T RENAME TO KRCR_PARM_T
/

ALTER TABLE KRNS_CAMPUS_T RENAME TO KRLC_CMP_T
/
ALTER TABLE KRNS_CMP_TYP_T RENAME TO KRLC_CMP_TYP_T
/
ALTER TABLE KR_COUNTRY_T RENAME TO KRLC_CNTRY_T
/
ALTER TABLE KR_STATE_T RENAME TO KRLC_ST_T
/
ALTER TABLE KR_POSTAL_CODE_T RENAME TO KRLC_PSTL_CD_T
/
ALTER TABLE KR_COUNTY_T RENAME TO KRLC_CNTY_T
/





-- 
-- 2011-06-08.sql
-- 


-- make krms_rule_t.prop_id nullable
alter table krms_rule_t modify (prop_id NULL)
/

-- add krms_actn_t.nmspc_cd
alter table krms_actn_t add nmspc_cd varchar2(40) not null
/

-- make krms_agenda_t default to 'Y'
alter table krms_agenda_t modify actv varchar2(1) DEFAULT 'Y'
/

-- make krms_prop_t.typ_id nullable 
alter table krms_prop_t modify (typ_id NULL)
/

-- change krms_rule_t.descr_txt to desc_t for consistency
alter table krms_rule_t rename column descr_txt to desc_txt
/





-- 
-- 2011-06-13-m6.sql
-- 


update krew_doc_typ_t set post_prcsr = 'org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor' where post_prcsr = 'org.kuali.rice.kns.workflow.postprocessor.KualiPostProcessor'
/
update krew_rule_attr_t set cls_nm = 'org.kuali.rice.krad.workflow.attribute.KualiXmlSearchableAttributeImpl' where cls_nm = 'org.kuali.rice.kns.workflow.attribute.KualiXmlSearchableAttributeImpl'
/
update krew_rule_attr_t set cls_nm = 'org.kuali.rice.kns.workflow.attribute.KualiXMLBooleanTranslatorSearchableAttributeImpl' where cls_nm = 'org.kuali.rice.kns.workflow.attribute.KualiXMLBooleanTranslatorSearchableAttributeImpl'
/
update krew_rule_attr_t set cls_nm = 'org.kuali.rice.kns.workflow.attribute.KualiXmlRuleAttributeImpl' where cls_nm = 'org.kuali.rice.kns.workflow.attribute.KualiXmlRuleAttributeImpl'
/





-- 
-- 2011-06-17-m6.sql
-- 


--
-- NOTE: when assembling this script for release, please merge any table rebuilds with those from 2011-04-28.sql
--

-----------------------------------------------------------------------------
-- KREW_DOC_TYP_T
-----------------------------------------------------------------------------

DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_DOC_TYP_TC0' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_DOC_TYP_T DROP CONSTRAINT KREW_DOC_TYP_TC0';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_TYP_TC0 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_DOC_TYP_TC0' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_DOC_TYP_TC0';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_TYP_TC0 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_DOC_TYP_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_DOC_TYP_T DROP CONSTRAINT KREW_DOC_TYP_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_TYP_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_DOC_TYP_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_DOC_TYP_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_TYP_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_DOC_TYP_T RENAME TO OLD_KREW_DOC_TYP_T
/

CREATE TABLE KREW_DOC_TYP_T
(
      DOC_TYP_ID VARCHAR2(40)
        , PARNT_ID VARCHAR2(40)
        , DOC_TYP_NM VARCHAR2(64)
        , DOC_TYP_VER_NBR NUMBER(10) default 0
        , ACTV_IND NUMBER(1)
        , CUR_IND NUMBER(1)
        , LBL VARCHAR2(128)
        , PREV_DOC_TYP_VER_NBR VARCHAR2(40)
        , DOC_TYP_DESC VARCHAR2(4000)
        , DOC_HDLR_URL VARCHAR2(255)
        , POST_PRCSR VARCHAR2(255)
        , JNDI_URL VARCHAR2(255)
        , BLNKT_APPR_PLCY VARCHAR2(10)
        , ADV_DOC_SRCH_URL VARCHAR2(255)
        , CSTM_ACTN_LIST_ATTRIB_CLS_NM VARCHAR2(255)
        , CSTM_ACTN_EMAIL_ATTRIB_CLS_NM VARCHAR2(255)
        , CSTM_DOC_NTE_ATTRIB_CLS_NM VARCHAR2(255)
        , RTE_VER_NBR VARCHAR2(2) default '1'
        , NOTIFY_ADDR VARCHAR2(255)
        , APPL_ID VARCHAR2(255)
        , EMAIL_XSL VARCHAR2(255)
        , SEC_XML CLOB
        , VER_NBR NUMBER(8) default 0
        , BLNKT_APPR_GRP_ID VARCHAR2(40)
        , RPT_GRP_ID VARCHAR2(40)
        , GRP_ID VARCHAR2(40)
        , HELP_DEF_URL VARCHAR2(4000)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , DOC_SEARCH_HELP_URL VARCHAR2(4000)
        , DOC_HDR_ID VARCHAR2(40)
    , CONSTRAINT KREW_DOC_TYP_TC0 UNIQUE (OBJ_ID)
    , CONSTRAINT KREW_DOC_TYP_TI1 UNIQUE (DOC_TYP_NM, DOC_TYP_VER_NBR)
)
/

INSERT INTO KREW_DOC_TYP_T(
		DOC_TYP_ID
        , PARNT_ID
        , DOC_TYP_NM
        , DOC_TYP_VER_NBR
        , ACTV_IND
        , CUR_IND
        , LBL
        , PREV_DOC_TYP_VER_NBR
        , DOC_TYP_DESC
        , DOC_HDLR_URL
        , POST_PRCSR
        , JNDI_URL
        , BLNKT_APPR_PLCY
        , ADV_DOC_SRCH_URL
        , CSTM_ACTN_LIST_ATTRIB_CLS_NM
        , CSTM_ACTN_EMAIL_ATTRIB_CLS_NM
        , CSTM_DOC_NTE_ATTRIB_CLS_NM
        , RTE_VER_NBR
        , NOTIFY_ADDR
        , APPL_ID
        , EMAIL_XSL
        , SEC_XML
        , VER_NBR
        , BLNKT_APPR_GRP_ID
        , RPT_GRP_ID
        , GRP_ID
        , HELP_DEF_URL
        , OBJ_ID
        , DOC_SEARCH_HELP_URL
        , DOC_HDR_ID
)
SELECT DOC_TYP_ID
        , PARNT_ID
        , DOC_TYP_NM
        , DOC_TYP_VER_NBR
        , ACTV_IND
        , CUR_IND
        , LBL
        , PREV_DOC_TYP_VER_NBR
        , DOC_TYP_DESC
        , DOC_HDLR_URL
        , POST_PRCSR
        , JNDI_URL
        , BLNKT_APPR_PLCY
        , ADV_DOC_SRCH_URL
        , CSTM_ACTN_LIST_ATTRIB_CLS_NM
        , CSTM_ACTN_EMAIL_ATTRIB_CLS_NM
        , CSTM_DOC_NTE_ATTRIB_CLS_NM
        , RTE_VER_NBR
        , NOTIFY_ADDR
        , APPL_ID
        , EMAIL_XSL
        , SEC_XML
        , VER_NBR
        , BLNKT_APPR_GRP_ID
        , RPT_GRP_ID
        , GRP_ID
        , HELP_DEF_URL
        , OBJ_ID
        , DOC_SEARCH_HELP_URL
        , DOC_HDR_ID
FROM OLD_KREW_DOC_TYP_T
/

DROP TABLE OLD_KREW_DOC_TYP_T CASCADE CONSTRAINTS PURGE
/


ALTER TABLE KREW_DOC_TYP_T
    ADD CONSTRAINT KREW_DOC_TYP_TP1
PRIMARY KEY (DOC_TYP_ID)
/

CREATE INDEX KREW_DOC_TYP_TI2 
  ON KREW_DOC_TYP_T 
  (PARNT_ID)
/
CREATE INDEX KREW_DOC_TYP_TI3 
  ON KREW_DOC_TYP_T 
  (DOC_TYP_ID, PARNT_ID)
/
CREATE INDEX KREW_DOC_TYP_TI4 
  ON KREW_DOC_TYP_T 
  (PREV_DOC_TYP_VER_NBR)
/
CREATE INDEX KREW_DOC_TYP_TI5 
  ON KREW_DOC_TYP_T 
  (CUR_IND)
/
CREATE INDEX KREW_DOC_TYP_TI6 
  ON KREW_DOC_TYP_T 
  (DOC_TYP_NM)
/

-----------------------------------------------------------------------------
-- KREW_DOC_HDR_T
-----------------------------------------------------------------------------

DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_DOC_HDR_TC0' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_DOC_HDR_T DROP CONSTRAINT KREW_DOC_HDR_TC0';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_HDR_TC0 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_DOC_HDR_TC0' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_DOC_HDR_TC0';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_HDR_TC0 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_DOC_HDR_T RENAME TO OLD_KREW_DOC_HDR_T
/

CREATE TABLE KREW_DOC_HDR_T
(
      DOC_HDR_ID VARCHAR2(40)
        , DOC_TYP_ID VARCHAR2(40)
        , DOC_HDR_STAT_CD CHAR(1) NOT NULL
        , RTE_LVL NUMBER(8) NOT NULL
        , STAT_MDFN_DT DATE NOT NULL
        , CRTE_DT DATE NOT NULL
        , APRV_DT DATE
        , FNL_DT DATE
        , RTE_STAT_MDFN_DT DATE
        , RTE_LVL_MDFN_DT DATE
        , TTL VARCHAR2(255)
        , APP_DOC_ID VARCHAR2(255)
        , DOC_VER_NBR NUMBER(8) NOT NULL
        , INITR_PRNCPL_ID VARCHAR2(40) NOT NULL
        , VER_NBR NUMBER(8) default 0
        , RTE_PRNCPL_ID VARCHAR2(40)
        , DTYPE VARCHAR2(50)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , APP_DOC_STAT VARCHAR2(64)
        , APP_DOC_STAT_MDFN_DT DATE
    , CONSTRAINT KREW_DOC_HDR_TC0 UNIQUE (OBJ_ID)
)
/

INSERT INTO KREW_DOC_HDR_T(DOC_HDR_ID, DOC_TYP_ID, DOC_HDR_STAT_CD, RTE_LVL, STAT_MDFN_DT, CRTE_DT, APRV_DT, FNL_DT, RTE_STAT_MDFN_DT, RTE_LVL_MDFN_DT, TTL, APP_DOC_ID, DOC_VER_NBR, INITR_PRNCPL_ID, VER_NBR, RTE_PRNCPL_ID, DTYPE, OBJ_ID, APP_DOC_STAT, APP_DOC_STAT_MDFN_DT)
SELECT DOC_HDR_ID, DOC_TYP_ID, DOC_HDR_STAT_CD, RTE_LVL, STAT_MDFN_DT, CRTE_DT, APRV_DT, FNL_DT, RTE_STAT_MDFN_DT, RTE_LVL_MDFN_DT, TTL, APP_DOC_ID, DOC_VER_NBR, INITR_PRNCPL_ID, VER_NBR, RTE_PRNCPL_ID, DTYPE, OBJ_ID, APP_DOC_STAT, APP_DOC_STAT_MDFN_DT
FROM OLD_KREW_DOC_HDR_T
/

DROP TABLE OLD_KREW_DOC_HDR_T CASCADE CONSTRAINTS PURGE
/

ALTER TABLE KREW_DOC_HDR_T
    ADD CONSTRAINT KREW_DOC_HDR_TP1
PRIMARY KEY (DOC_HDR_ID)
/

CREATE INDEX KREW_DOC_HDR_T10 
  ON KREW_DOC_HDR_T 
  (APP_DOC_STAT)
/
  
CREATE INDEX KREW_DOC_HDR_T12 
  ON KREW_DOC_HDR_T 
  (APP_DOC_STAT_MDFN_DT)
/
  
CREATE INDEX KREW_DOC_HDR_TI1 
  ON KREW_DOC_HDR_T 
  (DOC_TYP_ID)
/
  
CREATE INDEX KREW_DOC_HDR_TI2 
  ON KREW_DOC_HDR_T 
  (INITR_PRNCPL_ID)
/
  
CREATE INDEX KREW_DOC_HDR_TI3 
  ON KREW_DOC_HDR_T 
  (DOC_HDR_STAT_CD)
/
  
CREATE INDEX KREW_DOC_HDR_TI4 
  ON KREW_DOC_HDR_T 
  (TTL)
/
  
CREATE INDEX KREW_DOC_HDR_TI5 
  ON KREW_DOC_HDR_T 
  (CRTE_DT)
/
  
CREATE INDEX KREW_DOC_HDR_TI6 
  ON KREW_DOC_HDR_T 
  (RTE_STAT_MDFN_DT)
/
  
CREATE INDEX KREW_DOC_HDR_TI7 
  ON KREW_DOC_HDR_T 
  (APRV_DT)
/
  
CREATE INDEX KREW_DOC_HDR_TI8 
  ON KREW_DOC_HDR_T 
  (FNL_DT)
/
  
CREATE INDEX KREW_DOC_HDR_TI9 
  ON KREW_DOC_HDR_T 
  (APP_DOC_ID)
/

-----------------------------------------------------------------------------
-- KREW_DOC_TYP_PLCY_RELN_T
-----------------------------------------------------------------------------

DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_DOC_TYP_PLCY_RELN_TC0' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_DOC_TYP_PLCY_RELN_T DROP CONSTRAINT KREW_DOC_TYP_PLCY_RELN_TC0';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_TYP_PLCY_RELN_TC0 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_DOC_TYP_PLCY_RELN_TC0' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_DOC_TYP_PLCY_RELN_TC0';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_TYP_PLCY_RELN_TC0 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_DOC_TYP_PLCY_RELN_T RENAME TO OLD_KREW_DOC_TYP_PLCY_RELN_T
/

CREATE TABLE KREW_DOC_TYP_PLCY_RELN_T
(
      DOC_TYP_ID VARCHAR2(40)
        , DOC_PLCY_NM VARCHAR2(255)
        , PLCY_NM NUMBER(1) NOT NULL
        , VER_NBR NUMBER(8) default 0
        , OBJ_ID VARCHAR2(36) NOT NULL
        , PLCY_VAL VARCHAR2(64)

    , CONSTRAINT KREW_DOC_TYP_PLCY_RELN_TC0 UNIQUE (OBJ_ID)
)
/

INSERT INTO KREW_DOC_TYP_PLCY_RELN_T(
DOC_TYP_ID
        , DOC_PLCY_NM
        , PLCY_NM
        , VER_NBR
        , OBJ_ID
        , PLCY_VAL
)
SELECT DOC_TYP_ID
        , DOC_PLCY_NM
        , PLCY_NM
        , VER_NBR
        , OBJ_ID
        , PLCY_VAL
FROM OLD_KREW_DOC_TYP_PLCY_RELN_T
/

DROP TABLE OLD_KREW_DOC_TYP_PLCY_RELN_T CASCADE CONSTRAINTS PURGE
/

ALTER TABLE KREW_DOC_TYP_PLCY_RELN_T
    ADD CONSTRAINT KREW_DOC_TYP_PLCY_RELN_TP1
PRIMARY KEY (DOC_TYP_ID,DOC_PLCY_NM)
/

-----------------------------------------------------------------------------
-- KREW_DOC_TYP_APP_DOC_STAT_T
-----------------------------------------------------------------------------

DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_DOC_TYP_APP_DOC_STAT_TC0' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_DOC_TYP_APP_DOC_STAT_T DROP CONSTRAINT KREW_DOC_TYP_APP_DOC_STAT_TC0';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_TYP_APP_DOC_STAT_TC0 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_DOC_TYP_APP_DOC_STAT_TC0' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_DOC_TYP_APP_DOC_STAT_TC0';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_TYP_APP_DOC_STAT_TC0 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_DOC_TYP_APP_DOC_STAT_T RENAME TO O_KREW_DOC_TYP_APP_DOC_STAT_T
/

CREATE TABLE KREW_DOC_TYP_APP_DOC_STAT_T
(
      DOC_TYP_ID VARCHAR2(40)
        , DOC_STAT_NM VARCHAR2(64)
        , VER_NBR NUMBER(8) default 0
        , OBJ_ID VARCHAR2(36) NOT NULL
    , CONSTRAINT KREW_DOC_TYP_APP_DOC_STAT_TC0 UNIQUE (OBJ_ID)
)
/

INSERT INTO KREW_DOC_TYP_APP_DOC_STAT_T(
DOC_TYP_ID
        , DOC_STAT_NM
        , VER_NBR
        , OBJ_ID
)
SELECT DOC_TYP_ID
        , DOC_STAT_NM
        , VER_NBR
        , OBJ_ID
FROM O_KREW_DOC_TYP_APP_DOC_STAT_T
/

DROP TABLE O_KREW_DOC_TYP_APP_DOC_STAT_T CASCADE CONSTRAINTS PURGE
/

ALTER TABLE KREW_DOC_TYP_APP_DOC_STAT_T
    ADD CONSTRAINT KREW_DOC_TYP_APP_DOC_STAT_TP1
PRIMARY KEY (DOC_TYP_ID,DOC_STAT_NM)
/

CREATE INDEX KREW_DOC_TYP_APP_DOC_STAT_T1 
  ON KREW_DOC_TYP_APP_DOC_STAT_T 
  (DOC_TYP_ID)
/

-----------------------------------------------------------------------------
-- KREW_DOC_TYP_ATTR_T
-----------------------------------------------------------------------------

ALTER TABLE KREW_DOC_TYP_ATTR_T RENAME TO OLD_KREW_DOC_TYP_ATTR_T
/
CREATE TABLE KREW_DOC_TYP_ATTR_T
(
      DOC_TYP_ATTRIB_ID NUMBER(19)
        , DOC_TYP_ID VARCHAR2(40) NOT NULL
        , RULE_ATTR_ID NUMBER(19) NOT NULL
        , ORD_INDX NUMBER(4) default 0
)
/

INSERT INTO KREW_DOC_TYP_ATTR_T(
DOC_TYP_ATTRIB_ID
        , DOC_TYP_ID
        , RULE_ATTR_ID
        , ORD_INDX
)
SELECT DOC_TYP_ATTRIB_ID
        , DOC_TYP_ID
        , RULE_ATTR_ID
        , ORD_INDX
FROM OLD_KREW_DOC_TYP_ATTR_T
/

DROP TABLE OLD_KREW_DOC_TYP_ATTR_T CASCADE CONSTRAINTS PURGE
/

ALTER TABLE KREW_DOC_TYP_ATTR_T
    ADD CONSTRAINT KREW_DOC_TYP_ATTR_TP1
PRIMARY KEY (DOC_TYP_ATTRIB_ID)
/

CREATE INDEX KREW_DOC_TYP_ATTR_TI1 
  ON KREW_DOC_TYP_ATTR_T 
  (DOC_TYP_ID)
/

-----------------------------------------------------------------------------
-- KREW_RTE_NODE_T
-----------------------------------------------------------------------------

ALTER TABLE KREW_RTE_NODE_T RENAME TO OLD_KREW_RTE_NODE_T
/

CREATE TABLE KREW_RTE_NODE_T
(
      RTE_NODE_ID NUMBER(19)
        , DOC_TYP_ID VARCHAR2(40)
        , NM VARCHAR2(255) NOT NULL
        , TYP VARCHAR2(255) NOT NULL
        , RTE_MTHD_NM VARCHAR2(255)
        , RTE_MTHD_CD VARCHAR2(2)
        , FNL_APRVR_IND NUMBER(1)
        , MNDTRY_RTE_IND NUMBER(1)
        , ACTVN_TYP VARCHAR2(250)
        , BRCH_PROTO_ID NUMBER(19)
        , VER_NBR NUMBER(8) default 0
        , CONTENT_FRAGMENT VARCHAR2(4000)
        , GRP_ID VARCHAR2(40)
        , NEXT_DOC_STAT VARCHAR2(64)
)
/

INSERT INTO KREW_RTE_NODE_T(
RTE_NODE_ID
        , DOC_TYP_ID
        , NM
        , TYP
        , RTE_MTHD_NM
        , RTE_MTHD_CD
        , FNL_APRVR_IND
        , MNDTRY_RTE_IND
        , ACTVN_TYP
        , BRCH_PROTO_ID
        , VER_NBR
        , CONTENT_FRAGMENT
        , GRP_ID
        , NEXT_DOC_STAT
)
SELECT RTE_NODE_ID
        , DOC_TYP_ID
        , NM
        , TYP
        , RTE_MTHD_NM
        , RTE_MTHD_CD
        , FNL_APRVR_IND
        , MNDTRY_RTE_IND
        , ACTVN_TYP
        , BRCH_PROTO_ID
        , VER_NBR
        , CONTENT_FRAGMENT
        , GRP_ID
        , NEXT_DOC_STAT
FROM OLD_KREW_RTE_NODE_T
/

DROP TABLE OLD_KREW_RTE_NODE_T CASCADE CONSTRAINTS PURGE
/

ALTER TABLE KREW_RTE_NODE_T
    ADD CONSTRAINT KREW_RTE_NODE_TP1
PRIMARY KEY (RTE_NODE_ID)
/

CREATE INDEX KREW_RTE_NODE_TI1 
  ON KREW_RTE_NODE_T 
  (NM, DOC_TYP_ID)
/
CREATE INDEX KREW_RTE_NODE_TI2 
  ON KREW_RTE_NODE_T 
  (DOC_TYP_ID, FNL_APRVR_IND)
/
CREATE INDEX KREW_RTE_NODE_TI3 
  ON KREW_RTE_NODE_T 
  (BRCH_PROTO_ID)
/
CREATE INDEX KREW_RTE_NODE_TI4 
  ON KREW_RTE_NODE_T 
  (DOC_TYP_ID)
/
  
-----------------------------------------------------------------------------
-- KREW_DOC_TYP_PROC_T
-----------------------------------------------------------------------------

ALTER TABLE KREW_DOC_TYP_PROC_T RENAME TO OLD_KREW_DOC_TYP_PROC_T
/

CREATE TABLE KREW_DOC_TYP_PROC_T
(
      DOC_TYP_PROC_ID NUMBER(19)
        , DOC_TYP_ID VARCHAR2(40) NOT NULL
        , INIT_RTE_NODE_ID NUMBER(22)
        , NM VARCHAR2(255) NOT NULL
        , INIT_IND NUMBER(1) default 0 NOT NULL
        , VER_NBR NUMBER(8) default 0
)
/

INSERT INTO KREW_DOC_TYP_PROC_T(
DOC_TYP_PROC_ID
        , DOC_TYP_ID
        , INIT_RTE_NODE_ID
        , NM
        , INIT_IND
        , VER_NBR
)
SELECT DOC_TYP_PROC_ID
        , DOC_TYP_ID
        , INIT_RTE_NODE_ID
        , NM
        , INIT_IND
        , VER_NBR
FROM OLD_KREW_DOC_TYP_PROC_T
/

DROP TABLE OLD_KREW_DOC_TYP_PROC_T CASCADE CONSTRAINTS PURGE
/

ALTER TABLE KREW_DOC_TYP_PROC_T
    ADD CONSTRAINT KREW_DOC_TYP_PROC_TP1
PRIMARY KEY (DOC_TYP_PROC_ID)
/

CREATE INDEX KREW_DOC_TYP_PROC_TI1 
  ON KREW_DOC_TYP_PROC_T 
  (DOC_TYP_ID)
/
CREATE INDEX KREW_DOC_TYP_PROC_TI2 
  ON KREW_DOC_TYP_PROC_T 
  (INIT_RTE_NODE_ID)
/
CREATE INDEX KREW_DOC_TYP_PROC_TI3 
  ON KREW_DOC_TYP_PROC_T 
  (NM)
/





-- 
-- 2011-06-21.sql
-- 


alter table KRIM_PERM_TMPL_T modify NMSPC_CD varchar2(40) not null
/
alter table KRIM_PERM_TMPL_T modify NM varchar2(100) not null
/
alter table KRIM_PERM_TMPL_T add constraint KRIM_PERM_TMPL_TC1 unique (NM, NMSPC_CD)
/

alter table KRIM_RSP_TMPL_T modify NMSPC_CD varchar2(40) not null
/
alter table KRIM_RSP_TMPL_T modify NM varchar2(100) not null
/
alter table KRIM_RSP_TMPL_T add constraint KRIM_RSP_TMPL_TC1 unique (NM, NMSPC_CD)
/





-- 
-- 2011-06-23.sql
-- 


CREATE SEQUENCE KRMS_CTGRY_S INCREMENT BY 1 START WITH 1 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

CREATE TABLE KRMS_CTGRY_T
(
    CTGRY_ID VARCHAR2(40) NOT NULL
      , NM VARCHAR2(255) NOT NULL
      , NMSPC_CD VARCHAR2(40) NOT NULL
      , VER_NBR NUMBER(8) DEFAULT 0
    , PRIMARY KEY (CTGRY_ID)
    , CONSTRAINT KRMS_CTGRY_TC0 UNIQUE (NM, NMSPC_CD)
)
/

CREATE TABLE KRMS_TERM_SPEC_CTGRY_T
(
  TERM_SPEC_ID VARCHAR2(40) NOT NULL
      , CTGRY_ID VARCHAR2(40) NOT NULL
  , PRIMARY KEY (TERM_SPEC_ID, CTGRY_ID)
  , CONSTRAINT KRMS_TERM_SPEC_CTGRY_FK1 FOREIGN KEY (TERM_SPEC_ID) REFERENCES KRMS_TERM_SPEC_T (TERM_SPEC_ID)
  , CONSTRAINT KRMS_TERM_SPEC_CTGRY_FK2 FOREIGN KEY (CTGRY_ID) REFERENCES KRMS_CTGRY_T (CTGRY_ID)
)
/

CREATE TABLE KRMS_FUNC_CTGRY_T
(
  FUNC_ID VARCHAR2(40) NOT NULL
  , CTGRY_ID VARCHAR2(40) NOT NULL
  , PRIMARY KEY (FUNC_ID, CTGRY_ID)
  , CONSTRAINT KRMS_FUNC_CTGRY_FK1 FOREIGN KEY (FUNC_ID) REFERENCES KRMS_FUNC_T (FUNC_ID)
  , CONSTRAINT KRMS_FUNC_CTGRY_FK2 FOREIGN KEY (CTGRY_ID) REFERENCES KRMS_CTGRY_T (CTGRY_ID)
)
/





-- 
-- 2011-06-29.sql
-- 


update KRIM_PERM_T t set NM='Use Document Operation Screen' where PERM_ID = '140'
/
update KRIM_PERM_T t set NM='Use Java Security Management Screen' where PERM_ID = '141'
/
update KRIM_PERM_T t set NM='Use Message Queue Screen' where PERM_ID = '142'
/
update KRIM_PERM_T t set NM='Use Service Registry Screen' where PERM_ID = '143'
/
update KRIM_PERM_T t set NM='Use Thread Pool Screen' where PERM_ID = '144'
/
update KRIM_PERM_T t set NM='Use Quartz Queue Screen' where PERM_ID = '145'
/
update KRIM_PERM_T t set NM='Ad Hoc Review RICE Document' where PERM_ID = '146'
/
update KRIM_PERM_T t set NM='Administer Routing RICE Document' where PERM_ID = '147'
/
update KRIM_PERM_T t set NM='Blanket Approve RICE Document'	where PERM_ID = '148'
/
update KRIM_PERM_T t set NM='Initiate RICE Document' where PERM_ID = '149'
/
update KRIM_PERM_T t set NM='Assign Role' where PERM_ID = '150'
/
update KRIM_PERM_T t set NM='Grant Permission' where PERM_ID = '151'
/
update KRIM_PERM_T t set NM='Grant Responsibility' where PERM_ID = '152'
/
update KRIM_PERM_T t set NM='Populate Group' where PERM_ID = '155'
/
update KRIM_PERM_T t set NM='Copy RICE Document' where PERM_ID = '156'
/
update KRIM_PERM_T t set NM='Inquire Into RICE Records' where PERM_ID = '161'
/
update KRIM_PERM_T t set NM='Look Up RICE Records' where PERM_ID = '162'
/
update KRIM_PERM_T t set NM='Maintain System Parameter' where PERM_ID = '163'
/
update KRIM_PERM_T t set NM='Modify Batch Job' where PERM_ID = '164'
/
update KRIM_PERM_T t set NM='Open RICE Document' where PERM_ID = '165'
/
update KRIM_PERM_T t set NM='Use all RICE Screen' where PERM_ID = '166'
/
update KRIM_PERM_T t set NM='Cancel Document' where PERM_ID = '167'
/
update KRIM_PERM_T t set NM='Route Document' where PERM_ID = '168'
/
update KRIM_PERM_T t set NM='Take Requested Apprive Action' where PERM_ID = '170'
/
update KRIM_PERM_T t set NM='Take Requested FYI Action' where PERM_ID = '172'
/
update KRIM_PERM_T t set NM='Take Requested Acknowledge Action' where PERM_ID = '173'
/
update KRIM_PERM_T t set NM='Log In Kuali Portal' where PERM_ID = '174'
/
update KRIM_PERM_T t set NM='Edit Kuali ENROUTE Document Node Name PreRoute' where PERM_ID = '180'
/
update KRIM_PERM_T t set NM='Edit Kuali ENROUTE Document Route Status Code R' where PERM_ID = '181'
/
update KRIM_PERM_T t set NM='Full Unmask Tax Identification Number Payee ACH Document'	where PERM_ID = '183'
/
update KRIM_PERM_T t set NM='Add Note  Attachment Kuali Document' where PERM_ID = '259'
/
update KRIM_PERM_T t set NM='View Note  Attachment Kuali Document' where PERM_ID = '261'
/
update KRIM_PERM_T t set NM='Delete Note  Attachment Kuali Document' where PERM_ID = '264'
/
update KRIM_PERM_T t set NM='Use Screen XML Ingester Screen' where PERM_ID = '265'
/
update KRIM_PERM_T t set NM='Administer Pessimistic Locking' where PERM_ID = '289'
/
update KRIM_PERM_T t set NM='Save RICE Document' where PERM_ID = '290'
/
update KRIM_PERM_T t set NM='View Other Action List' where PERM_ID = '298'
/
update KRIM_PERM_T t set NM='Unrestricted Document Search' where PERM_ID = '299'
/
update KRIM_PERM_T t set NM='Full Unmask Tax Identification Number Person Document'	where PERM_ID = '306'
/
update KRIM_PERM_T t set NM='Modify Entity' where PERM_ID = '307'
/
update KRIM_PERM_T t set NM='Send FYI Request Kuali Document' where PERM_ID = '332'
/
update KRIM_PERM_T t set NM='Send Acknowledge Request Kuali Document' where PERM_ID = '333'
/
update KRIM_PERM_T t set NM='Send Approve Request Kuali Document' where PERM_ID = '334'
/
update KRIM_PERM_T t set NM='Override Entity Privacy Preferences' where PERM_ID = '378'
/
update KRIM_PERM_T t set NM='Look Up Rule Template'	where PERM_ID = '701'
/
update KRIM_PERM_T t set NM='Look Up Stylesheet' where PERM_ID = '702'
/
update KRIM_PERM_T t set NM='Look Up eDocLite' where PERM_ID = '703'
/
update KRIM_PERM_T t set NM='Look Up Rule Attribute' where PERM_ID = '707'
/
update KRIM_PERM_T t set NM='Look Up Parameter Component' where PERM_ID = '719'
/
update KRIM_PERM_T t set NM='Look Up Namespace'	where PERM_ID = '720'
/
update KRIM_PERM_T t set NM='Look Up Parameter Type' where PERM_ID = '721'
/
update KRIM_PERM_T t set NM='Inquire Into Rule Template' where PERM_ID = '801'
/
update KRIM_PERM_T t set NM='Inquire Into Stylesheet' where PERM_ID = '802'
/
update KRIM_PERM_T t set NM='Inquire Into eDocLite' where PERM_ID = '803'
/
update KRIM_PERM_T t set NM='Inquire Into Rule Attribute' where PERM_ID = '807'
/
update KRIM_PERM_T t set NM='Inquire Into Pessimistic' where PERM_ID = '814'
/
update KRIM_PERM_T t set NM='Inquire Into Parameter Component' where PERM_ID = '819'
/
update KRIM_PERM_T t set NM='Inquire Into Namespace' where PERM_ID = '820'
/
update KRIM_PERM_T t set NM='Inquire Into Parameter Type' where PERM_ID = '821'
/
update KRIM_PERM_T t set NM='Populate Group KUALI Namespace' where PERM_ID = '833'
/
update KRIM_PERM_T t set NM='Assign Role KUALI Namespace' where PERM_ID = '834'
/
update KRIM_PERM_T t set NM='Grant Permission KUALI Namespace' where PERM_ID = '835'
/
update KRIM_PERM_T t set NM='Grant Responsibility KUALI Namespace' where PERM_ID = '836'
/
update KRIM_PERM_T t set NM='Use Configuration Viewer Screen' where PERM_ID = '840'
/
update KRIM_PERM_T t set NM='Add Message to Route Log'	where PERM_ID = '841'
/
alter table KRIM_PERM_T modify NMSPC_CD varchar2(40) not null
/
alter table KRIM_PERM_T modify NM varchar2(100) not null
/
alter table KRIM_PERM_T add constraint KRIM_PERM_T_TC1 unique (NM, NMSPC_CD)
/
alter table KRIM_RSP_T modify NMSPC_CD varchar2(40) not null
/
alter table KRIM_RSP_T modify NM varchar2(100) not null
/
alter table KRIM_RSP_T add constraint KRIM_RSP_T_TC1 unique (NM, NMSPC_CD)
/





-- 
-- 2011-07-07-m6.sql
-- 


--
-- NOTE: when assembling this script for release, please merge any table rebuilds with those from 2011-04-28.sql
--

-----------------------------------------------------------------------------
-- KREW_DOC_NTE_T
-----------------------------------------------------------------------------
ALTER TABLE KREW_DOC_NTE_T RENAME TO TEMP_KREW_DOC_NTE_T
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_DOC_NTE_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE TEMP_KREW_DOC_NTE_T DROP CONSTRAINT KREW_DOC_NTE_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_NTE_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_DOC_NTE_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_DOC_NTE_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_NTE_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
CREATE TABLE KREW_DOC_NTE_T ( 
    DOC_NTE_ID    	VARCHAR2(40),
	DOC_HDR_ID    	VARCHAR2(40) NOT NULL,
    AUTH_PRNCPL_ID	VARCHAR2(40) NOT NULL,
    CRT_DT        	DATE NOT NULL,
    TXT           	VARCHAR2(4000) NULL,
    VER_NBR       	NUMBER(8,0) DEFAULT 0 NULL
)
/
ALTER TABLE KREW_DOC_NTE_T ADD CONSTRAINT KREW_DOC_NTE_TP1 PRIMARY KEY (DOC_NTE_ID)
/
CREATE INDEX KREW_DOC_NTE_TI1 ON KREW_DOC_NTE_T (DOC_HDR_ID)
/
INSERT INTO KREW_DOC_NTE_T
(DOC_NTE_ID, DOC_HDR_ID, AUTH_PRNCPL_ID, CRT_DT, TXT, VER_NBR)
SELECT CAST(DOC_NTE_ID as VARCHAR2(40)), DOC_HDR_ID, AUTH_PRNCPL_ID, CRT_DT, TXT, VER_NBR
FROM TEMP_KREW_DOC_NTE_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_DOC_NTE_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_DOC_NTE_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_ATT_T
-----------------------------------------------------------------------------
ALTER TABLE KREW_ATT_T RENAME TO TEMP_KREW_ATT_T
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_ATT_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE TEMP_KREW_ATT_T DROP CONSTRAINT KREW_ATT_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_ATT_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_ATT_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_ATT_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_ATT_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
CREATE TABLE KREW_ATT_T (
	ATTACHMENT_ID	VARCHAR2(40),
	NTE_ID			VARCHAR2(40) NULL,
	FILE_NM			VARCHAR2(255) NOT NULL,
	FILE_LOC		VARCHAR2(255) NOT NULL,
	MIME_TYP		VARCHAR2(255) NOT NULL,
	VER_NBR			NUMBER(8,0) DEFAULT 0 NULL
)
/
ALTER TABLE KREW_ATT_T ADD CONSTRAINT KREW_ATT_TP1 PRIMARY KEY (ATTACHMENT_ID)
/
CREATE INDEX KREW_ATT_TI1 ON KREW_ATT_T(NTE_ID)
/
INSERT INTO KREW_ATT_T
(ATTACHMENT_ID, NTE_ID, FILE_NM, FILE_LOC, MIME_TYP, VER_NBR)
SELECT CAST(ATTACHMENT_ID as VARCHAR2(40)), CAST(NTE_ID as VARCHAR2(40)), FILE_NM, FILE_LOC, MIME_TYP, VER_NBR
FROM TEMP_KREW_ATT_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_ATT_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_ATT_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_ACTN_ITM_T
-----------------------------------------------------------------------------
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_ACTN_ITM_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_ACTN_ITM_T DROP CONSTRAINT KREW_ACTN_ITM_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_ACTN_ITM_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_ACTN_ITM_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_ACTN_ITM_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_ACTN_ITM_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_ACTN_ITM_TI2' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_ACTN_ITM_TI2';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_ACTN_ITM_TI2 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_ACTN_ITM_TI3' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_ACTN_ITM_TI3';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_ACTN_ITM_TI3 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_ACTN_ITM_TI5' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_ACTN_ITM_TI5';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_ACTN_ITM_TI5 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_ACTN_ITM_T RENAME TO TEMP_KREW_ACTN_ITM_T
/
CREATE TABLE KREW_ACTN_ITM_T
(
      ACTN_ITM_ID VARCHAR2(40)
        , PRNCPL_ID VARCHAR2(40) NOT NULL
        , ASND_DT DATE NOT NULL
        , RQST_CD CHAR(1) NOT NULL
        , ACTN_RQST_ID VARCHAR2(40) NOT NULL
        , DOC_HDR_ID VARCHAR2(40) NOT NULL
        , ROLE_NM VARCHAR2(2000)
        , DLGN_PRNCPL_ID VARCHAR2(40)
        , DOC_HDR_TTL VARCHAR2(255)
        , DOC_TYP_LBL VARCHAR2(128) NOT NULL
        , DOC_HDLR_URL VARCHAR2(255) NOT NULL
        , DOC_TYP_NM VARCHAR2(64) NOT NULL
        , RSP_ID VARCHAR2(40) NOT NULL
        , DLGN_TYP VARCHAR2(1)
        , VER_NBR NUMBER(8) default 0
        , DTYPE VARCHAR2(50)
        , GRP_ID VARCHAR2(40)
        , DLGN_GRP_ID VARCHAR2(40)
        , RQST_LBL VARCHAR2(255)
)
/
ALTER TABLE KREW_ACTN_ITM_T
    ADD CONSTRAINT KREW_ACTN_ITM_TP1
PRIMARY KEY (ACTN_ITM_ID)
/
CREATE INDEX KREW_ACTN_ITM_TI1
  ON KREW_ACTN_ITM_T 
  (PRNCPL_ID)
/
CREATE INDEX KREW_ACTN_ITM_TI2
  ON KREW_ACTN_ITM_T
  (DOC_HDR_ID)
/
CREATE INDEX KREW_ACTN_ITM_TI3
  ON KREW_ACTN_ITM_T
  (ACTN_RQST_ID)
/
CREATE INDEX KREW_ACTN_ITM_TI5
  ON KREW_ACTN_ITM_T
  (PRNCPL_ID, DLGN_TYP, DOC_HDR_ID)
/
INSERT INTO KREW_ACTN_ITM_T
(ACTN_ITM_ID, PRNCPL_ID, ASND_DT, RQST_CD, ACTN_RQST_ID, DOC_HDR_ID, ROLE_NM, DLGN_PRNCPL_ID, 
 DOC_HDR_TTL, DOC_TYP_LBL, DOC_HDLR_URL, DOC_TYP_NM, RSP_ID, DLGN_TYP, VER_NBR, DTYPE, 
 GRP_ID, DLGN_GRP_ID, RQST_LBL)
SELECT CAST(ACTN_ITM_ID as VARCHAR2(40)), PRNCPL_ID, ASND_DT, RQST_CD, CAST(ACTN_RQST_ID as VARCHAR2(40)), DOC_HDR_ID, 
 ROLE_NM, DLGN_PRNCPL_ID, 
 DOC_HDR_TTL, DOC_TYP_LBL, DOC_HDLR_URL, DOC_TYP_NM, CAST(RSP_ID as VARCHAR2(40)), DLGN_TYP, VER_NBR, DTYPE, 
 GRP_ID, DLGN_GRP_ID, RQST_LBL
FROM TEMP_KREW_ACTN_ITM_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_ACTN_ITM_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_ACTN_ITM_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_ACTN_TKN_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_ACTN_TKN_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_ACTN_TKN_T DROP CONSTRAINT KREW_ACTN_TKN_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_ACTN_TKN_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_ACTN_TKN_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_ACTN_TKN_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_ACTN_TKN_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_ACTN_TKN_TI2' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_ACTN_TKN_TI2';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_ACTN_TKN_TI2 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_ACTN_TKN_TI3' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_ACTN_TKN_TI3';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_ACTN_TKN_TI3 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_ACTN_TKN_TI4' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_ACTN_TKN_TI4';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_ACTN_TKN_TI4 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_ACTN_TKN_TI5' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_ACTN_TKN_TI5';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_ACTN_TKN_TI5 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_ACTN_TKN_T RENAME TO TEMP_KREW_ACTN_TKN_T
/
CREATE TABLE KREW_ACTN_TKN_T
(
      ACTN_TKN_ID VARCHAR2(40)
        , DOC_HDR_ID VARCHAR2(40) NOT NULL
        , PRNCPL_ID VARCHAR2(40) NOT NULL
        , DLGTR_PRNCPL_ID VARCHAR2(40)
        , ACTN_CD CHAR(1) NOT NULL
        , ACTN_DT DATE NOT NULL
        , DOC_VER_NBR NUMBER(8) NOT NULL
        , ANNOTN VARCHAR2(2000)
        , CUR_IND NUMBER(1) default 1
        , VER_NBR NUMBER(8) default 0
        , DLGTR_GRP_ID VARCHAR2(40)
)
/
ALTER TABLE KREW_ACTN_TKN_T
    ADD CONSTRAINT KREW_ACTN_TKN_TP1
PRIMARY KEY (ACTN_TKN_ID)
/
CREATE INDEX KREW_ACTN_TKN_TI1 
  ON KREW_ACTN_TKN_T 
  (DOC_HDR_ID, PRNCPL_ID)
/
CREATE INDEX KREW_ACTN_TKN_TI2 
  ON KREW_ACTN_TKN_T 
  (DOC_HDR_ID, PRNCPL_ID, ACTN_CD)
/
CREATE INDEX KREW_ACTN_TKN_TI3 
  ON KREW_ACTN_TKN_T 
  (PRNCPL_ID)
/
CREATE INDEX KREW_ACTN_TKN_TI4 
  ON KREW_ACTN_TKN_T 
  (DLGTR_PRNCPL_ID)
/
CREATE INDEX KREW_ACTN_TKN_TI5 
  ON KREW_ACTN_TKN_T 
  (DOC_HDR_ID)
/
INSERT INTO KREW_ACTN_TKN_T
(ACTN_TKN_ID, DOC_HDR_ID, PRNCPL_ID, DLGTR_PRNCPL_ID, ACTN_CD, ACTN_DT, DOC_VER_NBR, ANNOTN, 
CUR_IND, VER_NBR, DLGTR_GRP_ID)
SELECT CAST(ACTN_TKN_ID as VARCHAR2(40)), DOC_HDR_ID, PRNCPL_ID, DLGTR_PRNCPL_ID, ACTN_CD, ACTN_DT, DOC_VER_NBR, ANNOTN, 
CUR_IND, VER_NBR, DLGTR_GRP_ID
FROM TEMP_KREW_ACTN_TKN_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_ACTN_TKN_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_ACTN_TKN_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_ACTN_RQST_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_ACTN_RQST_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_ACTN_RQST_T DROP CONSTRAINT KREW_ACTN_RQST_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_ACTN_RQST_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_ACTN_RQST_T11' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_ACTN_RQST_T11';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_ACTN_RQST_T11 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_ACTN_RQST_T12' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_ACTN_RQST_T12';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_ACTN_RQST_T12 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_ACTN_RQST_T13' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_ACTN_RQST_T13';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_ACTN_RQST_T13 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_ACTN_RQST_T14' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_ACTN_RQST_T14';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_ACTN_RQST_T14 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_ACTN_RQST_T15' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_ACTN_RQST_T15';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_ACTN_RQST_T15 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_ACTN_RQST_T16' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_ACTN_RQST_T16';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_ACTN_RQST_T16 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_ACTN_RQST_T17' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_ACTN_RQST_T17';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_ACTN_RQST_T17 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_ACTN_RQST_T19' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_ACTN_RQST_T19';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_ACTN_RQST_T19 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_ACTN_RQST_T RENAME TO TEMP_KREW_ACTN_RQST_T
/
CREATE TABLE KREW_ACTN_RQST_T
(
      ACTN_RQST_ID VARCHAR2(40)
        , PARNT_ID VARCHAR2(40)
        , ACTN_RQST_CD CHAR(1) NOT NULL
        , DOC_HDR_ID VARCHAR2(40) NOT NULL
        , RULE_ID VARCHAR2(40)
        , STAT_CD CHAR(1) NOT NULL
        , RSP_ID VARCHAR2(40) NOT NULL
        , PRNCPL_ID VARCHAR2(40)
        , ROLE_NM VARCHAR2(2000)
        , QUAL_ROLE_NM VARCHAR2(2000)
        , QUAL_ROLE_NM_LBL_TXT VARCHAR2(2000)
        , RECIP_TYP_CD CHAR(1)
        , PRIO_NBR NUMBER(8) NOT NULL
        , RTE_TYP_NM VARCHAR2(255)
        , RTE_LVL_NBR NUMBER(8) NOT NULL
        , RTE_NODE_INSTN_ID VARCHAR2(40)
        , ACTN_TKN_ID VARCHAR2(40)
        , DOC_VER_NBR NUMBER(8) NOT NULL
        , CRTE_DT DATE NOT NULL
        , RSP_DESC_TXT VARCHAR2(200)
        , FRC_ACTN NUMBER(1) default 0
        , ACTN_RQST_ANNOTN_TXT VARCHAR2(2000)
        , DLGN_TYP CHAR(1)
        , APPR_PLCY CHAR(1)
        , CUR_IND NUMBER(1) default 1
        , VER_NBR NUMBER(8) default 0
        , GRP_ID VARCHAR2(40)
        , RQST_LBL VARCHAR2(255)
)
/
ALTER TABLE KREW_ACTN_RQST_T
    ADD CONSTRAINT KREW_ACTN_RQST_TP1
PRIMARY KEY (ACTN_RQST_ID)
/
CREATE INDEX KREW_ACTN_RQST_T11 
  ON KREW_ACTN_RQST_T 
  (DOC_HDR_ID)
/
CREATE INDEX KREW_ACTN_RQST_T12 
  ON KREW_ACTN_RQST_T 
  (PRNCPL_ID)
/
CREATE INDEX KREW_ACTN_RQST_T13 
  ON KREW_ACTN_RQST_T 
  (ACTN_TKN_ID)
/
CREATE INDEX KREW_ACTN_RQST_T14 
  ON KREW_ACTN_RQST_T 
  (PARNT_ID)
/
CREATE INDEX KREW_ACTN_RQST_T15 
  ON KREW_ACTN_RQST_T 
  (RSP_ID)
/
CREATE INDEX KREW_ACTN_RQST_T16 
  ON KREW_ACTN_RQST_T 
  (STAT_CD, RSP_ID)
/
CREATE INDEX KREW_ACTN_RQST_T17 
  ON KREW_ACTN_RQST_T 
  (RTE_NODE_INSTN_ID)
/
CREATE INDEX KREW_ACTN_RQST_T19 
  ON KREW_ACTN_RQST_T 
  (STAT_CD, DOC_HDR_ID)
/
INSERT INTO KREW_ACTN_RQST_T
(ACTN_RQST_ID, PARNT_ID, ACTN_RQST_CD, DOC_HDR_ID, RULE_ID, STAT_CD, RSP_ID, PRNCPL_ID, 
ROLE_NM, QUAL_ROLE_NM, QUAL_ROLE_NM_LBL_TXT, RECIP_TYP_CD, PRIO_NBR, RTE_TYP_NM, 
RTE_LVL_NBR, RTE_NODE_INSTN_ID, ACTN_TKN_ID, DOC_VER_NBR, CRTE_DT, RSP_DESC_TXT, 
FRC_ACTN, ACTN_RQST_ANNOTN_TXT, DLGN_TYP, APPR_PLCY, CUR_IND, VER_NBR, GRP_ID, RQST_LBL)
SELECT CAST(ACTN_RQST_ID as VARCHAR2(40)), CAST(PARNT_ID as VARCHAR2(40)), 
ACTN_RQST_CD, DOC_HDR_ID, CAST(RULE_ID as VARCHAR2(40)), STAT_CD, CAST(RSP_ID as VARCHAR2(40)), PRNCPL_ID, 
ROLE_NM, QUAL_ROLE_NM, QUAL_ROLE_NM_LBL_TXT, RECIP_TYP_CD, PRIO_NBR, RTE_TYP_NM, 
RTE_LVL_NBR, CAST(RTE_NODE_INSTN_ID as VARCHAR2(40)), CAST(ACTN_TKN_ID as VARCHAR2(40)), DOC_VER_NBR, 
CRTE_DT, RSP_DESC_TXT, 
FRC_ACTN, ACTN_RQST_ANNOTN_TXT, DLGN_TYP, APPR_PLCY, CUR_IND, VER_NBR, GRP_ID, RQST_LBL
FROM TEMP_KREW_ACTN_RQST_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_ACTN_RQST_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_ACTN_RQST_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_RULE_TMPL_T
-----------------------------------------------------------------------------
ALTER TABLE KREW_RULE_TMPL_T DROP CONSTRAINT KREW_RULE_TMPL_TC0
/
ALTER TABLE KREW_RULE_TMPL_T DROP CONSTRAINT KREW_RULE_TMPL_TI1
/
ALTER TABLE KREW_RULE_TMPL_T RENAME TO TEMP_KREW_RULE_TMPL_T
/
ALTER TABLE TEMP_KREW_RULE_TMPL_T DROP CONSTRAINT KREW_RULE_TMPL_TP1
/
CREATE TABLE KREW_RULE_TMPL_T
(
      RULE_TMPL_ID VARCHAR2(40)
        , NM VARCHAR2(250) NOT NULL
        , RULE_TMPL_DESC VARCHAR2(2000)
        , DLGN_RULE_TMPL_ID VARCHAR2(40)
        , VER_NBR NUMBER(8) default 0
        , OBJ_ID VARCHAR2(36) NOT NULL
    
    , CONSTRAINT KREW_RULE_TMPL_TC0 UNIQUE (OBJ_ID)
    , CONSTRAINT KREW_RULE_TMPL_TI1 UNIQUE (NM)
)
/
ALTER TABLE KREW_RULE_TMPL_T
    ADD CONSTRAINT KREW_RULE_TMPL_TP1
PRIMARY KEY (RULE_TMPL_ID)
/
INSERT INTO KREW_RULE_TMPL_T
(RULE_TMPL_ID, NM, RULE_TMPL_DESC, DLGN_RULE_TMPL_ID, VER_NBR, OBJ_ID)
SELECT CAST(RULE_TMPL_ID as VARCHAR2(40)), NM, RULE_TMPL_DESC, CAST(DLGN_RULE_TMPL_ID as VARCHAR2(40)), 
VER_NBR, OBJ_ID
FROM TEMP_KREW_RULE_TMPL_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_RULE_TMPL_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_RULE_TMPL_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_RULE_TMPL_ATTR_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_RULE_TMPL_ATTR_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_RULE_TMPL_ATTR_T DROP CONSTRAINT KREW_RULE_TMPL_ATTR_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RULE_TMPL_ATTR_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RULE_TMPL_ATTR_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RULE_TMPL_ATTR_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RULE_TMPL_ATTR_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RULE_TMPL_ATTR_TI2' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RULE_TMPL_ATTR_TI2';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RULE_TMPL_ATTR_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_RULE_TMPL_ATTR_T RENAME TO TEMP_KREW_RULE_TMPL_ATTR_T
/
ALTER TABLE TEMP_KREW_RULE_TMPL_ATTR_T DROP CONSTRAINT KREW_RULE_TMPL_ATTR_TC0
/
CREATE TABLE KREW_RULE_TMPL_ATTR_T
(
      RULE_TMPL_ATTR_ID VARCHAR2(40)
        , RULE_TMPL_ID VARCHAR2(40) NOT NULL
        , RULE_ATTR_ID VARCHAR2(40) NOT NULL
        , REQ_IND NUMBER(1) NOT NULL
        , ACTV_IND NUMBER(1) NOT NULL
        , DSPL_ORD NUMBER(5) NOT NULL
        , DFLT_VAL VARCHAR2(2000)
        , VER_NBR NUMBER(8) default 0
        , OBJ_ID VARCHAR2(36) NOT NULL
    , CONSTRAINT KREW_RULE_TMPL_ATTR_TC0 UNIQUE (OBJ_ID)
)
/
ALTER TABLE KREW_RULE_TMPL_ATTR_T
    ADD CONSTRAINT KREW_RULE_TMPL_ATTR_TP1
PRIMARY KEY (RULE_TMPL_ATTR_ID)
/
CREATE INDEX KREW_RULE_TMPL_ATTR_TI1 
  ON KREW_RULE_TMPL_ATTR_T 
  (RULE_TMPL_ID)
/
CREATE INDEX KREW_RULE_TMPL_ATTR_TI2 
  ON KREW_RULE_TMPL_ATTR_T 
  (RULE_ATTR_ID)
/
INSERT INTO KREW_RULE_TMPL_ATTR_T
(RULE_TMPL_ATTR_ID, RULE_TMPL_ID, RULE_ATTR_ID, REQ_IND, ACTV_IND, DSPL_ORD, DFLT_VAL, VER_NBR, OBJ_ID)
SELECT 
CAST(RULE_TMPL_ATTR_ID as VARCHAR2(40)), CAST(RULE_TMPL_ID as VARCHAR2(40)), 
CAST(RULE_ATTR_ID as VARCHAR2(40)), REQ_IND, ACTV_IND, DSPL_ORD, DFLT_VAL, VER_NBR, OBJ_ID
FROM TEMP_KREW_RULE_TMPL_ATTR_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_RULE_TMPL_ATTR_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_RULE_TMPL_ATTR_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_RULE_T
-----------------------------------------------------------------------------
ALTER TABLE KREW_RULE_T DROP CONSTRAINT KREW_RULE_TC0
/
ALTER TABLE KREW_RULE_T DROP CONSTRAINT KREW_RULE_TP1
/
ALTER TABLE KREW_RULE_T DROP CONSTRAINT KREW_RULE_TR1
/
ALTER TABLE KREW_RULE_T RENAME TO TEMP_KREW_RULE_T
/
CREATE TABLE KREW_RULE_T
(
      RULE_ID VARCHAR2(40)
        , NM VARCHAR2(256)
        , RULE_TMPL_ID VARCHAR2(40)
        , RULE_EXPR_ID VARCHAR2(40)
        , ACTV_IND NUMBER(1) NOT NULL
        , RULE_BASE_VAL_DESC VARCHAR2(2000)
        , FRC_ACTN NUMBER(1) NOT NULL
        , DOC_TYP_NM VARCHAR2(64) NOT NULL
        , DOC_HDR_ID VARCHAR2(40)
        , TMPL_RULE_IND NUMBER(1)
        , FRM_DT DATE
        , TO_DT DATE
        , DACTVN_DT DATE
        , CUR_IND NUMBER(1) default 0
        , RULE_VER_NBR NUMBER(8) default 0
        , DLGN_IND NUMBER(1)
        , PREV_RULE_VER_NBR VARCHAR2(40)
        , ACTVN_DT DATE
        , VER_NBR NUMBER(8) default 0
        , OBJ_ID VARCHAR2(36) NOT NULL
    , CONSTRAINT KREW_RULE_TC0 UNIQUE (OBJ_ID)
)
/
ALTER TABLE KREW_RULE_T
    ADD CONSTRAINT KREW_RULE_TP1
PRIMARY KEY (RULE_ID)
/
INSERT INTO KREW_RULE_T
(RULE_ID, NM, RULE_TMPL_ID, RULE_EXPR_ID, ACTV_IND, RULE_BASE_VAL_DESC, FRC_ACTN, 
DOC_TYP_NM, DOC_HDR_ID, TMPL_RULE_IND, FRM_DT, TO_DT, DACTVN_DT, CUR_IND, RULE_VER_NBR, 
DLGN_IND, PREV_RULE_VER_NBR, ACTVN_DT, VER_NBR, OBJ_ID)
SELECT 
CAST(RULE_ID as VARCHAR2(40)), NM, CAST(RULE_TMPL_ID as VARCHAR2(40)), CAST(RULE_EXPR_ID as VARCHAR2(40)), 
ACTV_IND, RULE_BASE_VAL_DESC, FRC_ACTN, 
DOC_TYP_NM, DOC_HDR_ID, TMPL_RULE_IND, FRM_DT, TO_DT, DACTVN_DT, CUR_IND, RULE_VER_NBR, 
DLGN_IND, CAST(PREV_RULE_VER_NBR as VARCHAR2(40)), ACTVN_DT, VER_NBR, OBJ_ID
FROM TEMP_KREW_RULE_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_RULE_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_RULE_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_DLGN_RSP_T
-----------------------------------------------------------------------------
ALTER TABLE KREW_DLGN_RSP_T DROP CONSTRAINT KREW_DLGN_RSP_TC0
/
ALTER TABLE KREW_DLGN_RSP_T DROP CONSTRAINT KREW_DLGN_RSP_TP1
/
ALTER TABLE KREW_DLGN_RSP_T RENAME TO TEMP_KREW_DLGN_RSP_T
/
CREATE TABLE KREW_DLGN_RSP_T
(
      DLGN_RULE_ID VARCHAR2(40)
        , RSP_ID VARCHAR2(40) NOT NULL
        , DLGN_RULE_BASE_VAL_ID VARCHAR2(40) NOT NULL
        , DLGN_TYP VARCHAR2(20) NOT NULL
        , VER_NBR NUMBER(8) default 0
        , OBJ_ID VARCHAR2(36) NOT NULL
    , CONSTRAINT KREW_DLGN_RSP_TC0 UNIQUE (OBJ_ID)
)
/
ALTER TABLE KREW_DLGN_RSP_T
    ADD CONSTRAINT KREW_DLGN_RSP_TP1
PRIMARY KEY (DLGN_RULE_ID)
/
INSERT INTO KREW_DLGN_RSP_T
(DLGN_RULE_ID, RSP_ID, DLGN_RULE_BASE_VAL_ID, DLGN_TYP, VER_NBR, OBJ_ID)
SELECT 
CAST(DLGN_RULE_ID as VARCHAR2(40)), CAST(RSP_ID as VARCHAR2(40)), CAST(DLGN_RULE_BASE_VAL_ID as VARCHAR2(40)), 
DLGN_TYP, VER_NBR, OBJ_ID
FROM TEMP_KREW_DLGN_RSP_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_DLGN_RSP_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_DLGN_RSP_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_RULE_RSP_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_RULE_RSP_TC0' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_RULE_RSP_T DROP CONSTRAINT KREW_RULE_RSP_TC0';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RULE_RSP_TC0 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_RULE_RSP_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_RULE_RSP_T DROP CONSTRAINT KREW_RULE_RSP_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RULE_RSP_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RULE_RSP_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RULE_RSP_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RULE_RSP_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_RULE_RSP_T RENAME TO TEMP_KREW_RULE_RSP_T
/
CREATE TABLE KREW_RULE_RSP_T
(
      RULE_RSP_ID VARCHAR2(40)
        , RSP_ID VARCHAR2(40) NOT NULL
        , RULE_ID VARCHAR2(40) NOT NULL
        , PRIO NUMBER(5)
        , ACTN_RQST_CD VARCHAR2(2000)
        , NM VARCHAR2(200)
        , TYP VARCHAR2(1)
        , APPR_PLCY CHAR(1)
        , VER_NBR NUMBER(8) default 0
        , OBJ_ID VARCHAR2(36) NOT NULL
    , CONSTRAINT KREW_RULE_RSP_TC0 UNIQUE (OBJ_ID)
)
/
ALTER TABLE KREW_RULE_RSP_T
    ADD CONSTRAINT KREW_RULE_RSP_TP1
PRIMARY KEY (RULE_RSP_ID)
/
CREATE INDEX KREW_RULE_RSP_TI1 
  ON KREW_RULE_RSP_T 
  (RULE_ID)
/
INSERT INTO KREW_RULE_RSP_T
(RULE_RSP_ID, RSP_ID, RULE_ID, PRIO, ACTN_RQST_CD, NM, TYP, APPR_PLCY, VER_NBR, OBJ_ID)
SELECT CAST(RULE_RSP_ID as VARCHAR2(40)), CAST(RSP_ID as VARCHAR2(40)), CAST(RULE_ID as VARCHAR2(40)), 
PRIO, ACTN_RQST_CD, NM, TYP, APPR_PLCY, VER_NBR, OBJ_ID
FROM TEMP_KREW_RULE_RSP_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_RULE_RSP_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_RULE_RSP_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_RULE_EXT_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_RULE_EXT_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_RULE_EXT_T DROP CONSTRAINT KREW_RULE_EXT_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RULE_EXT_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RULE_EXT_T1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RULE_EXT_T1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RULE_EXT_T1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_RULE_EXT_T RENAME TO TEMP_KREW_RULE_EXT_T
/
CREATE TABLE KREW_RULE_EXT_T
(
      RULE_EXT_ID VARCHAR2(40)
        , RULE_TMPL_ATTR_ID VARCHAR2(40) NOT NULL
        , RULE_ID VARCHAR2(40) NOT NULL
        , VER_NBR NUMBER(8) default 0
)
/
ALTER TABLE KREW_RULE_EXT_T
    ADD CONSTRAINT KREW_RULE_EXT_TP1
PRIMARY KEY (RULE_EXT_ID)
/
CREATE INDEX KREW_RULE_EXT_T1 
  ON KREW_RULE_EXT_T 
  (RULE_ID)
/
INSERT INTO KREW_RULE_EXT_T
(RULE_EXT_ID, RULE_TMPL_ATTR_ID, RULE_ID, VER_NBR)
SELECT CAST(RULE_EXT_ID as VARCHAR2(40)), CAST(RULE_TMPL_ATTR_ID as VARCHAR2(40)), 
CAST(RULE_ID as VARCHAR2(40)), VER_NBR 
FROM TEMP_KREW_RULE_EXT_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_RULE_EXT_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_RULE_EXT_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_RTE_NODE_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_RTE_NODE_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_RTE_NODE_T DROP CONSTRAINT KREW_RTE_NODE_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_NODE_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RTE_NODE_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RTE_NODE_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_NODE_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RTE_NODE_TI2' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RTE_NODE_TI2';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_NODE_TI2 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RTE_NODE_TI3' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RTE_NODE_TI3';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_NODE_TI3 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RTE_NODE_TI4' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RTE_NODE_TI4';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_NODE_TI4 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_RTE_NODE_T RENAME TO TEMP_KREW_RTE_NODE_T
/
CREATE TABLE KREW_RTE_NODE_T
(
      RTE_NODE_ID VARCHAR2(40)
        , DOC_TYP_ID NUMBER(19)
        , NM VARCHAR2(255) NOT NULL
        , TYP VARCHAR2(255) NOT NULL
        , RTE_MTHD_NM VARCHAR2(255)
        , RTE_MTHD_CD VARCHAR2(2)
        , FNL_APRVR_IND NUMBER(1)
        , MNDTRY_RTE_IND NUMBER(1)
        , ACTVN_TYP VARCHAR2(250)
        , BRCH_PROTO_ID VARCHAR2(40)
        , VER_NBR NUMBER(8) default 0
        , CONTENT_FRAGMENT VARCHAR2(4000)
        , GRP_ID VARCHAR2(40)
        , NEXT_DOC_STAT VARCHAR2(64)
)
/
ALTER TABLE KREW_RTE_NODE_T
    ADD CONSTRAINT KREW_RTE_NODE_TP1
PRIMARY KEY (RTE_NODE_ID)
/
CREATE INDEX KREW_RTE_NODE_TI1 
  ON KREW_RTE_NODE_T 
  (NM, DOC_TYP_ID)
/
CREATE INDEX KREW_RTE_NODE_TI2 
  ON KREW_RTE_NODE_T 
  (DOC_TYP_ID, FNL_APRVR_IND)
/
CREATE INDEX KREW_RTE_NODE_TI3 
  ON KREW_RTE_NODE_T 
  (BRCH_PROTO_ID)
/
CREATE INDEX KREW_RTE_NODE_TI4 
  ON KREW_RTE_NODE_T 
  (DOC_TYP_ID)
/
INSERT INTO KREW_RTE_NODE_T
(RTE_NODE_ID, DOC_TYP_ID, NM, TYP, RTE_MTHD_NM, RTE_MTHD_CD, FNL_APRVR_IND,
MNDTRY_RTE_IND, ACTVN_TYP, BRCH_PROTO_ID, VER_NBR, CONTENT_FRAGMENT, GRP_ID, NEXT_DOC_STAT)
SELECT CAST(RTE_NODE_ID as VARCHAR2(40)), DOC_TYP_ID, NM, TYP, RTE_MTHD_NM, RTE_MTHD_CD, FNL_APRVR_IND,
MNDTRY_RTE_IND, ACTVN_TYP, CAST(BRCH_PROTO_ID as VARCHAR2(40)), VER_NBR, CONTENT_FRAGMENT, GRP_ID, NEXT_DOC_STAT
FROM TEMP_KREW_RTE_NODE_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_RTE_NODE_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_RTE_NODE_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_RTE_NODE_INSTN_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_RTE_NODE_INSTN_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_RTE_NODE_INSTN_T DROP CONSTRAINT KREW_RTE_NODE_INSTN_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_NODE_INSTN_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RTE_NODE_INSTN_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RTE_NODE_INSTN_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_NODE_INSTN_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RTE_NODE_INSTN_TI2' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RTE_NODE_INSTN_TI2';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_NODE_INSTN_TI2 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RTE_NODE_INSTN_TI3' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RTE_NODE_INSTN_TI3';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_NODE_INSTN_TI3 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RTE_NODE_INSTN_TI4' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RTE_NODE_INSTN_TI4';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_NODE_INSTN_TI4 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_RTE_NODE_INSTN_T RENAME TO TEMP_KREW_RTE_NODE_INSTN_T
/
CREATE TABLE KREW_RTE_NODE_INSTN_T
(
      RTE_NODE_INSTN_ID VARCHAR2(40)
        , DOC_HDR_ID VARCHAR2(40) NOT NULL
        , RTE_NODE_ID VARCHAR2(40) NOT NULL
        , BRCH_ID VARCHAR2(40)
        , PROC_RTE_NODE_INSTN_ID VARCHAR2(40)
        , ACTV_IND NUMBER(1) default 0 NOT NULL
        , CMPLT_IND NUMBER(1) default 0 NOT NULL
        , INIT_IND NUMBER(1) default 0 NOT NULL
        , VER_NBR NUMBER(8) default 0
)
/
ALTER TABLE KREW_RTE_NODE_INSTN_T
    ADD CONSTRAINT KREW_RTE_NODE_INSTN_TP1
PRIMARY KEY (RTE_NODE_INSTN_ID)
/
CREATE INDEX KREW_RTE_NODE_INSTN_TI1 
  ON KREW_RTE_NODE_INSTN_T 
  (DOC_HDR_ID, ACTV_IND, CMPLT_IND)
/
CREATE INDEX KREW_RTE_NODE_INSTN_TI2 
  ON KREW_RTE_NODE_INSTN_T 
  (RTE_NODE_ID)
/
CREATE INDEX KREW_RTE_NODE_INSTN_TI3 
  ON KREW_RTE_NODE_INSTN_T 
  (BRCH_ID)
/
CREATE INDEX KREW_RTE_NODE_INSTN_TI4 
  ON KREW_RTE_NODE_INSTN_T 
  (PROC_RTE_NODE_INSTN_ID)
/
INSERT INTO KREW_RTE_NODE_INSTN_T
(RTE_NODE_INSTN_ID, DOC_HDR_ID, RTE_NODE_ID, BRCH_ID, PROC_RTE_NODE_INSTN_ID, 
ACTV_IND, CMPLT_IND, INIT_IND, VER_NBR)
SELECT CAST(RTE_NODE_INSTN_ID as VARCHAR2(40)), DOC_HDR_ID, 
CAST(RTE_NODE_ID as VARCHAR2(40)), CAST(BRCH_ID as VARCHAR2(40)), 
CAST(PROC_RTE_NODE_INSTN_ID as VARCHAR2(40)), 
ACTV_IND, CMPLT_IND, INIT_IND, VER_NBR
FROM TEMP_KREW_RTE_NODE_INSTN_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_RTE_NODE_INSTN_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_RTE_NODE_INSTN_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_RTE_NODE_INSTN_LNK_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_RTE_NODE_INSTN_LNK_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_RTE_NODE_INSTN_LNK_T DROP CONSTRAINT KREW_RTE_NODE_INSTN_LNK_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_NODE_INSTN_LNK_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RTE_NODE_INSTN_LNK_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RTE_NODE_INSTN_LNK_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_NODE_INSTN_LNK_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RTE_NODE_INSTN_LNK_TI2' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RTE_NODE_INSTN_LNK_TI2';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_NODE_INSTN_LNK_TI2 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_RTE_NODE_INSTN_LNK_T RENAME TO TEMP_KREW_RTE_NODE_INSTN_LNK_T
/
CREATE TABLE KREW_RTE_NODE_INSTN_LNK_T
(
      FROM_RTE_NODE_INSTN_ID VARCHAR2(40)
        , TO_RTE_NODE_INSTN_ID VARCHAR2(40)
)
/
ALTER TABLE KREW_RTE_NODE_INSTN_LNK_T
    ADD CONSTRAINT KREW_RTE_NODE_INSTN_LNK_TP1
PRIMARY KEY (FROM_RTE_NODE_INSTN_ID,TO_RTE_NODE_INSTN_ID)
/
CREATE INDEX KREW_RTE_NODE_INSTN_LNK_TI1 
  ON KREW_RTE_NODE_INSTN_LNK_T 
  (FROM_RTE_NODE_INSTN_ID)
/
CREATE INDEX KREW_RTE_NODE_INSTN_LNK_TI2 
  ON KREW_RTE_NODE_INSTN_LNK_T 
  (TO_RTE_NODE_INSTN_ID)
/
INSERT INTO KREW_RTE_NODE_INSTN_LNK_T
(FROM_RTE_NODE_INSTN_ID,TO_RTE_NODE_INSTN_ID)
SELECT CAST(FROM_RTE_NODE_INSTN_ID as VARCHAR2(40)),CAST(TO_RTE_NODE_INSTN_ID as VARCHAR2(40))
FROM TEMP_KREW_RTE_NODE_INSTN_LNK_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_RTE_NODE_INSTN_LNK_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_RTE_NODE_INSTN_LNK_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_RTE_BRCH_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_RTE_BRCH_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_RTE_BRCH_T DROP CONSTRAINT KREW_RTE_BRCH_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_BRCH_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RTE_BRCH_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RTE_BRCH_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_BRCH_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RTE_BRCH_TI2' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RTE_BRCH_TI2';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_BRCH_TI2 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RTE_BRCH_TI3' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RTE_BRCH_TI3';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_BRCH_TI3 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RTE_BRCH_TI4' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RTE_BRCH_TI4';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_BRCH_TI4 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RTE_BRCH_TI5' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RTE_BRCH_TI5';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_BRCH_TI5 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_RTE_BRCH_T RENAME TO TEMP_KREW_RTE_BRCH_T
/
CREATE TABLE KREW_RTE_BRCH_T
(
      RTE_BRCH_ID VARCHAR2(40)
        , NM VARCHAR2(255) NOT NULL
        , PARNT_ID VARCHAR2(40)
        , INIT_RTE_NODE_INSTN_ID VARCHAR2(40)
        , SPLT_RTE_NODE_INSTN_ID VARCHAR2(40)
        , JOIN_RTE_NODE_INSTN_ID VARCHAR2(40)
        , VER_NBR NUMBER(8) default 0
)
/
ALTER TABLE KREW_RTE_BRCH_T
    ADD CONSTRAINT KREW_RTE_BRCH_TP1
PRIMARY KEY (RTE_BRCH_ID)
/
CREATE INDEX KREW_RTE_BRCH_TI1 
  ON KREW_RTE_BRCH_T 
  (NM)
/
CREATE INDEX KREW_RTE_BRCH_TI2 
  ON KREW_RTE_BRCH_T 
  (PARNT_ID)
/
CREATE INDEX KREW_RTE_BRCH_TI3 
  ON KREW_RTE_BRCH_T 
  (INIT_RTE_NODE_INSTN_ID)
/
CREATE INDEX KREW_RTE_BRCH_TI4 
  ON KREW_RTE_BRCH_T 
  (SPLT_RTE_NODE_INSTN_ID)
/
CREATE INDEX KREW_RTE_BRCH_TI5 
  ON KREW_RTE_BRCH_T 
  (JOIN_RTE_NODE_INSTN_ID)
/
INSERT INTO KREW_RTE_BRCH_T
(RTE_BRCH_ID, NM, PARNT_ID, INIT_RTE_NODE_INSTN_ID, SPLT_RTE_NODE_INSTN_ID, JOIN_RTE_NODE_INSTN_ID, VER_NBR)
SELECT 
CAST(RTE_BRCH_ID as VARCHAR2(40)), NM, CAST(PARNT_ID as VARCHAR2(40)), 
CAST(INIT_RTE_NODE_INSTN_ID as VARCHAR2(40)), CAST(SPLT_RTE_NODE_INSTN_ID as VARCHAR2(40)), 
CAST(JOIN_RTE_NODE_INSTN_ID as VARCHAR2(40)), VER_NBR
FROM TEMP_KREW_RTE_BRCH_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_RTE_BRCH_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_RTE_BRCH_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_RTE_BRCH_ST_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_RTE_BRCH_ST_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_RTE_BRCH_ST_T DROP CONSTRAINT KREW_RTE_BRCH_ST_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_BRCH_ST_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RTE_BRCH_ST_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RTE_BRCH_ST_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_BRCH_ST_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RTE_BRCH_ST_TI2' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RTE_BRCH_ST_TI2';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_BRCH_ST_TI2 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RTE_BRCH_ST_TI3' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RTE_BRCH_ST_TI3';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_BRCH_ST_TI3 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_RTE_BRCH_ST_T RENAME TO TEMP_KREW_RTE_BRCH_ST_T
/
CREATE TABLE KREW_RTE_BRCH_ST_T
(
      RTE_BRCH_ST_ID VARCHAR2(40)
        , RTE_BRCH_ID VARCHAR2(40) NOT NULL
        , KEY_CD VARCHAR2(255) NOT NULL
        , VAL VARCHAR2(2000)
        , VER_NBR NUMBER(8) default 0
)
/
ALTER TABLE KREW_RTE_BRCH_ST_T
    ADD CONSTRAINT KREW_RTE_BRCH_ST_TP1
PRIMARY KEY (RTE_BRCH_ST_ID)
/
CREATE INDEX KREW_RTE_BRCH_ST_TI1 
  ON KREW_RTE_BRCH_ST_T 
  (RTE_BRCH_ID, KEY_CD)
/
CREATE INDEX KREW_RTE_BRCH_ST_TI2 
  ON KREW_RTE_BRCH_ST_T 
  (RTE_BRCH_ID)
/
CREATE INDEX KREW_RTE_BRCH_ST_TI3 
  ON KREW_RTE_BRCH_ST_T 
  (KEY_CD, VAL)
/
INSERT INTO KREW_RTE_BRCH_ST_T
(RTE_BRCH_ST_ID, RTE_BRCH_ID, KEY_CD, VAL, VER_NBR)
SELECT 
CAST(RTE_BRCH_ST_ID as VARCHAR2(40)), CAST(RTE_BRCH_ID as VARCHAR2(40)), KEY_CD, VAL, VER_NBR
FROM TEMP_KREW_RTE_BRCH_ST_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_RTE_BRCH_ST_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_RTE_BRCH_ST_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_RTE_NODE_INSTN_ST_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_RTE_NODE_INSTN_ST_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_RTE_NODE_INSTN_ST_T DROP CONSTRAINT KREW_RTE_NODE_INSTN_ST_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_NODE_INSTN_ST_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RTE_NODE_INSTN_ST_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RTE_NODE_INSTN_ST_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_NODE_INSTN_ST_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RTE_NODE_INSTN_ST_TI2' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RTE_NODE_INSTN_ST_TI2';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_NODE_INSTN_ST_TI2 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RTE_NODE_INSTN_ST_TI3' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RTE_NODE_INSTN_ST_TI3';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_NODE_INSTN_ST_TI3 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_RTE_NODE_INSTN_ST_T RENAME TO TEMP_KREW_RTE_NODE_INSTN_ST_T
/
CREATE TABLE KREW_RTE_NODE_INSTN_ST_T
(
      RTE_NODE_INSTN_ST_ID VARCHAR2(40)
        , RTE_NODE_INSTN_ID VARCHAR2(40) NOT NULL
        , KEY_CD VARCHAR2(255) NOT NULL
        , VAL VARCHAR2(2000)
        , VER_NBR NUMBER(8) default 0
)
/
ALTER TABLE KREW_RTE_NODE_INSTN_ST_T
    ADD CONSTRAINT KREW_RTE_NODE_INSTN_ST_TP1
PRIMARY KEY (RTE_NODE_INSTN_ST_ID)
/
CREATE INDEX KREW_RTE_NODE_INSTN_ST_TI1 
  ON KREW_RTE_NODE_INSTN_ST_T 
  (RTE_NODE_INSTN_ID, KEY_CD)
/
CREATE INDEX KREW_RTE_NODE_INSTN_ST_TI2 
  ON KREW_RTE_NODE_INSTN_ST_T 
  (RTE_NODE_INSTN_ID)
/
CREATE INDEX KREW_RTE_NODE_INSTN_ST_TI3 
  ON KREW_RTE_NODE_INSTN_ST_T 
  (KEY_CD, VAL)
/
INSERT INTO KREW_RTE_NODE_INSTN_ST_T
(RTE_NODE_INSTN_ST_ID, RTE_NODE_INSTN_ID, KEY_CD, VAL, VER_NBR)
SELECT 
CAST(RTE_NODE_INSTN_ST_ID as VARCHAR2(40)), CAST(RTE_NODE_INSTN_ID as VARCHAR2(40)), KEY_CD, VAL, VER_NBR
FROM TEMP_KREW_RTE_NODE_INSTN_ST_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_RTE_NODE_INSTN_ST_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_RTE_NODE_INSTN_ST_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_DOC_TYP_ATTR_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_DOC_TYP_ATTR_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_DOC_TYP_ATTR_T DROP CONSTRAINT KREW_DOC_TYP_ATTR_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_TYP_ATTR_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_DOC_TYP_ATTR_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_DOC_TYP_ATTR_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_TYP_ATTR_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_DOC_TYP_ATTR_T RENAME TO TEMP_KREW_DOC_TYP_ATTR_T
/
CREATE TABLE KREW_DOC_TYP_ATTR_T
(
      DOC_TYP_ATTRIB_ID VARCHAR2(40)
        , DOC_TYP_ID VARCHAR2(40) NOT NULL
        , RULE_ATTR_ID VARCHAR2(40) NOT NULL
        , ORD_INDX NUMBER(4) default 0
)
/
ALTER TABLE KREW_DOC_TYP_ATTR_T
    ADD CONSTRAINT KREW_DOC_TYP_ATTR_TP1
PRIMARY KEY (DOC_TYP_ATTRIB_ID)
/
CREATE INDEX KREW_DOC_TYP_ATTR_TI1 
  ON KREW_DOC_TYP_ATTR_T 
  (DOC_TYP_ID)
/
INSERT INTO KREW_DOC_TYP_ATTR_T
(DOC_TYP_ATTRIB_ID, DOC_TYP_ID, RULE_ATTR_ID, ORD_INDX)
SELECT 
CAST(DOC_TYP_ATTRIB_ID as VARCHAR2(40)), CAST(DOC_TYP_ID as VARCHAR2(40)), 
CAST(RULE_ATTR_ID as VARCHAR2(40)), ORD_INDX
FROM TEMP_KREW_DOC_TYP_ATTR_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_DOC_TYP_ATTR_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_DOC_TYP_ATTR_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_RULE_ATTR_T
-----------------------------------------------------------------------------
ALTER TABLE KREW_RULE_ATTR_T DROP CONSTRAINT KREW_RULE_ATTR_TC0
/
ALTER TABLE KREW_RULE_ATTR_T DROP CONSTRAINT KREW_RULE_ATTR_TP1
/
ALTER TABLE KREW_RULE_ATTR_T RENAME TO TEMP_KREW_RULE_ATTR_T
/
CREATE TABLE KREW_RULE_ATTR_T
(
      RULE_ATTR_ID VARCHAR2(40)
        , NM VARCHAR2(255) NOT NULL
        , LBL VARCHAR2(2000) NOT NULL
        , RULE_ATTR_TYP_CD VARCHAR2(2000) NOT NULL
        , DESC_TXT VARCHAR2(2000)
        , CLS_NM VARCHAR2(2000)
        , XML CLOB
        , VER_NBR NUMBER(8) default 0
        , APPL_ID VARCHAR2(255)
        , OBJ_ID VARCHAR2(36) NOT NULL
    , CONSTRAINT KREW_RULE_ATTR_TC0 UNIQUE (OBJ_ID)
)
/
ALTER TABLE KREW_RULE_ATTR_T
    ADD CONSTRAINT KREW_RULE_ATTR_TP1
PRIMARY KEY (RULE_ATTR_ID)
/
INSERT INTO KREW_RULE_ATTR_T
(RULE_ATTR_ID, NM, LBL, RULE_ATTR_TYP_CD, DESC_TXT, CLS_NM, XML, VER_NBR, APPL_ID, OBJ_ID)
SELECT 
CAST(RULE_ATTR_ID as VARCHAR2(40)), NM, LBL, RULE_ATTR_TYP_CD, DESC_TXT, CLS_NM, XML, VER_NBR, APPL_ID, OBJ_ID
FROM TEMP_KREW_RULE_ATTR_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_RULE_ATTR_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_RULE_ATTR_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_RULE_TMPL_OPTN_T
-----------------------------------------------------------------------------
ALTER TABLE KREW_RULE_TMPL_OPTN_T DROP CONSTRAINT KREW_RULE_TMPL_OPTN_TP1
/
ALTER TABLE KREW_RULE_TMPL_OPTN_T RENAME TO TEMP_KREW_RULE_TMPL_OPTN_T
/
CREATE TABLE KREW_RULE_TMPL_OPTN_T
(
      RULE_TMPL_OPTN_ID VARCHAR2(40)
        , RULE_TMPL_ID VARCHAR2(40)
        , KEY_CD VARCHAR2(250)
        , VAL VARCHAR2(2000)
        , VER_NBR NUMBER(8) default 0
)
/
ALTER TABLE KREW_RULE_TMPL_OPTN_T
    ADD CONSTRAINT KREW_RULE_TMPL_OPTN_TP1
PRIMARY KEY (RULE_TMPL_OPTN_ID)
/
INSERT INTO KREW_RULE_TMPL_OPTN_T
(RULE_TMPL_OPTN_ID, RULE_TMPL_ID, KEY_CD, VAL, VER_NBR)
SELECT 
CAST(RULE_TMPL_OPTN_ID as VARCHAR2(40)), CAST(RULE_TMPL_ID as VARCHAR2(40)), KEY_CD, VAL, VER_NBR
FROM TEMP_KREW_RULE_TMPL_OPTN_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_RULE_TMPL_OPTN_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_RULE_TMPL_OPTN_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_OUT_BOX_ITM_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_OUT_BOX_ITM_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_OUT_BOX_ITM_T DROP CONSTRAINT KREW_OUT_BOX_ITM_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_OUT_BOX_ITM_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_OUT_BOX_ITM_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_OUT_BOX_ITM_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_OUT_BOX_ITM_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_OUT_BOX_ITM_TI2' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_OUT_BOX_ITM_TI2';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_OUT_BOX_ITM_TI2 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_OUT_BOX_ITM_TI3' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_OUT_BOX_ITM_TI3';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_OUT_BOX_ITM_TI3 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_OUT_BOX_ITM_T RENAME TO TEMP_KREW_OUT_BOX_ITM_T
/
CREATE TABLE KREW_OUT_BOX_ITM_T
(
      ACTN_ITM_ID VARCHAR2(40)
        , PRNCPL_ID VARCHAR2(40) NOT NULL
        , ASND_DT DATE NOT NULL
        , RQST_CD CHAR(1) NOT NULL
        , ACTN_RQST_ID VARCHAR2(40) NOT NULL
        , DOC_HDR_ID VARCHAR2(40) NOT NULL
        , ROLE_NM VARCHAR2(2000)
        , DLGN_PRNCPL_ID VARCHAR2(40)
        , DOC_HDR_TTL VARCHAR2(255)
        , DOC_TYP_LBL VARCHAR2(128) NOT NULL
        , DOC_HDLR_URL VARCHAR2(255) NOT NULL
        , DOC_TYP_NM VARCHAR2(64) NOT NULL
        , RSP_ID VARCHAR2(40) NOT NULL
        , DLGN_TYP VARCHAR2(1)
        , VER_NBR NUMBER(8) default 0
        , GRP_ID VARCHAR2(40)
        , DLGN_GRP_ID VARCHAR2(40)
        , RQST_LBL VARCHAR2(255)
)
/
ALTER TABLE KREW_OUT_BOX_ITM_T
    ADD CONSTRAINT KREW_OUT_BOX_ITM_TP1
PRIMARY KEY (ACTN_ITM_ID)
/
CREATE INDEX KREW_OUT_BOX_ITM_TI1 
  ON KREW_OUT_BOX_ITM_T 
  (PRNCPL_ID)
/
CREATE INDEX KREW_OUT_BOX_ITM_TI2 
  ON KREW_OUT_BOX_ITM_T 
  (DOC_HDR_ID)
/
CREATE INDEX KREW_OUT_BOX_ITM_TI3 
  ON KREW_OUT_BOX_ITM_T 
  (ACTN_RQST_ID)
/
INSERT INTO KREW_OUT_BOX_ITM_T
(ACTN_ITM_ID, PRNCPL_ID, ASND_DT, RQST_CD, ACTN_RQST_ID, DOC_HDR_ID, ROLE_NM, 
DLGN_PRNCPL_ID, DOC_HDR_TTL, DOC_TYP_LBL, DOC_HDLR_URL, DOC_TYP_NM, RSP_ID, 
DLGN_TYP, VER_NBR, GRP_ID, DLGN_GRP_ID, RQST_LBL)
SELECT 
CAST(ACTN_ITM_ID as VARCHAR2(40)), PRNCPL_ID, ASND_DT, RQST_CD, CAST(ACTN_RQST_ID as VARCHAR2(40)), 
DOC_HDR_ID, ROLE_NM, 
DLGN_PRNCPL_ID, DOC_HDR_TTL, DOC_TYP_LBL, DOC_HDLR_URL, DOC_TYP_NM, 
CAST(RSP_ID as VARCHAR2(40)), 
DLGN_TYP, VER_NBR, GRP_ID, DLGN_GRP_ID, RQST_LBL
FROM TEMP_KREW_OUT_BOX_ITM_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_OUT_BOX_ITM_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_OUT_BOX_ITM_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_RTE_NODE_CFG_PARM_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_RTE_NODE_CFG_PARM_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_RTE_NODE_CFG_PARM_T DROP CONSTRAINT KREW_RTE_NODE_CFG_PARM_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_NODE_CFG_PARM_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RTE_NODE_CFG_PARM_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RTE_NODE_CFG_PARM_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_NODE_CFG_PARM_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_RTE_NODE_CFG_PARM_T RENAME TO TEMP_KREW_RTE_NODE_CFG_PARM_T
/
CREATE TABLE KREW_RTE_NODE_CFG_PARM_T
(
      RTE_NODE_CFG_PARM_ID VARCHAR2(40)
        , RTE_NODE_ID VARCHAR2(40) NOT NULL
        , KEY_CD VARCHAR2(255) NOT NULL
        , VAL VARCHAR2(4000)
)
/
ALTER TABLE KREW_RTE_NODE_CFG_PARM_T
    ADD CONSTRAINT KREW_RTE_NODE_CFG_PARM_TP1
PRIMARY KEY (RTE_NODE_CFG_PARM_ID)
/
CREATE INDEX KREW_RTE_NODE_CFG_PARM_TI1 
  ON KREW_RTE_NODE_CFG_PARM_T 
  (RTE_NODE_ID)
/
INSERT INTO KREW_RTE_NODE_CFG_PARM_T
(RTE_NODE_CFG_PARM_ID, RTE_NODE_ID, KEY_CD, VAL)
SELECT 
CAST(RTE_NODE_CFG_PARM_ID as VARCHAR2(40)), CAST(RTE_NODE_ID as VARCHAR2(40)), KEY_CD, VAL
FROM TEMP_KREW_RTE_NODE_CFG_PARM_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_RTE_NODE_CFG_PARM_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_RTE_NODE_CFG_PARM_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_DOC_TYP_PROC_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_DOC_TYP_PROC_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_DOC_TYP_PROC_T DROP CONSTRAINT KREW_DOC_TYP_PROC_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_TYP_PROC_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_DOC_TYP_PROC_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_DOC_TYP_PROC_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_TYP_PROC_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_DOC_TYP_PROC_TI2' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_DOC_TYP_PROC_TI2';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_TYP_PROC_TI2 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_DOC_TYP_PROC_TI3' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_DOC_TYP_PROC_TI3';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_TYP_PROC_TI3 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_DOC_TYP_PROC_T RENAME TO TEMP_KREW_DOC_TYP_PROC_T
/
CREATE TABLE KREW_DOC_TYP_PROC_T
(
      DOC_TYP_PROC_ID VARCHAR2(40)
        , DOC_TYP_ID VARCHAR2(40) NOT NULL
        , INIT_RTE_NODE_ID NUMBER(22)
        , NM VARCHAR2(255) NOT NULL
        , INIT_IND NUMBER(1) default 0 NOT NULL
        , VER_NBR NUMBER(8) default 0
)
/
ALTER TABLE KREW_DOC_TYP_PROC_T
    ADD CONSTRAINT KREW_DOC_TYP_PROC_TP1
PRIMARY KEY (DOC_TYP_PROC_ID)
/
CREATE INDEX KREW_DOC_TYP_PROC_TI1 
  ON KREW_DOC_TYP_PROC_T 
  (DOC_TYP_ID)
/
CREATE INDEX KREW_DOC_TYP_PROC_TI2 
  ON KREW_DOC_TYP_PROC_T 
  (INIT_RTE_NODE_ID)
/
CREATE INDEX KREW_DOC_TYP_PROC_TI3 
  ON KREW_DOC_TYP_PROC_T 
  (NM)
/
INSERT INTO KREW_DOC_TYP_PROC_T
(DOC_TYP_PROC_ID, DOC_TYP_ID, INIT_RTE_NODE_ID, NM, INIT_IND, VER_NBR)
SELECT 
CAST(DOC_TYP_PROC_ID as VARCHAR2(40)), CAST(DOC_TYP_ID as VARCHAR2(40)), INIT_RTE_NODE_ID, 
NM, INIT_IND, VER_NBR
FROM TEMP_KREW_DOC_TYP_PROC_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_DOC_TYP_PROC_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_DOC_TYP_PROC_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_DOC_LNK_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_DOC_LNK_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_DOC_LNK_T DROP CONSTRAINT KREW_DOC_LNK_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_LNK_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_DOC_LNK_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_DOC_LNK_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_LNK_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_DOC_LNK_T RENAME TO TEMP_KREW_DOC_LNK_T
/
CREATE TABLE KREW_DOC_LNK_T
(
      DOC_LNK_ID VARCHAR2(40)
        , ORGN_DOC_ID VARCHAR2(40) NOT NULL
        , DEST_DOC_ID VARCHAR2(40) NOT NULL
)
/
ALTER TABLE KREW_DOC_LNK_T
    ADD CONSTRAINT KREW_DOC_LNK_TP1
PRIMARY KEY (DOC_LNK_ID)
/
CREATE INDEX KREW_DOC_LNK_TI1 
  ON KREW_DOC_LNK_T 
  (ORGN_DOC_ID)
/
INSERT INTO KREW_DOC_LNK_T
(DOC_LNK_ID, ORGN_DOC_ID, DEST_DOC_ID)
SELECT 
CAST(DOC_LNK_ID as VARCHAR2(40)), ORGN_DOC_ID, DEST_DOC_ID
FROM TEMP_KREW_DOC_LNK_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_DOC_LNK_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_DOC_LNK_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_RTE_BRCH_PROTO_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_RTE_BRCH_PROTO_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_RTE_BRCH_PROTO_T DROP CONSTRAINT KREW_RTE_BRCH_PROTO_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_BRCH_PROTO_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RTE_BRCH_PROTO_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RTE_BRCH_PROTO_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RTE_BRCH_PROTO_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_RTE_BRCH_PROTO_T RENAME TO TEMP_KREW_RTE_BRCH_PROTO_T
/
CREATE TABLE KREW_RTE_BRCH_PROTO_T
(
      RTE_BRCH_PROTO_ID VARCHAR2(40)
        , BRCH_NM VARCHAR2(255) NOT NULL
        , VER_NBR NUMBER(8) default 0
)
/
ALTER TABLE KREW_RTE_BRCH_PROTO_T
    ADD CONSTRAINT KREW_RTE_BRCH_PROTO_TP1
PRIMARY KEY (RTE_BRCH_PROTO_ID)
/
CREATE INDEX KREW_RTE_BRCH_PROTO_TI1 
  ON KREW_RTE_BRCH_PROTO_T 
  (BRCH_NM)
/
INSERT INTO KREW_RTE_BRCH_PROTO_T
(RTE_BRCH_PROTO_ID, BRCH_NM, VER_NBR)
SELECT 
CAST(RTE_BRCH_PROTO_ID as VARCHAR2(40)), BRCH_NM, VER_NBR
FROM TEMP_KREW_RTE_BRCH_PROTO_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_RTE_BRCH_PROTO_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_RTE_BRCH_PROTO_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_HLP_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_HLP_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_HLP_T DROP CONSTRAINT KREW_HLP_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_HLP_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_HLP_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_HLP_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_HLP_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_HLP_T RENAME TO TEMP_KREW_HLP_T
/
CREATE TABLE KREW_HLP_T
(
      HLP_ID VARCHAR2(40)
        , NM VARCHAR2(500) NOT NULL
        , KEY_CD VARCHAR2(500) NOT NULL
        , HLP_TXT VARCHAR2(4000) NOT NULL
        , VER_NBR NUMBER(8) default 0
)
/
ALTER TABLE KREW_HLP_T
    ADD CONSTRAINT KREW_HLP_TP1
PRIMARY KEY (HLP_ID)
/
CREATE INDEX KREW_HLP_TI1 
  ON KREW_HLP_T 
  (KEY_CD)
/
INSERT INTO KREW_HLP_T
(HLP_ID, NM, KEY_CD, HLP_TXT, VER_NBR)
SELECT 
CAST(HLP_ID as VARCHAR2(40)), NM, KEY_CD, HLP_TXT, VER_NBR
FROM TEMP_KREW_HLP_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_HLP_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_HLP_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_RULE_EXT_VAL_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_RULE_EXT_VAL_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_RULE_EXT_VAL_T DROP CONSTRAINT KREW_RULE_EXT_VAL_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RULE_EXT_VAL_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RULE_EXT_VAL_T1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RULE_EXT_VAL_T1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RULE_EXT_VAL_T1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_RULE_EXT_VAL_T2' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_RULE_EXT_VAL_T2';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_RULE_EXT_VAL_T2 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_RULE_EXT_VAL_T RENAME TO TEMP_KREW_RULE_EXT_VAL_T
/
CREATE TABLE KREW_RULE_EXT_VAL_T
(
      RULE_EXT_VAL_ID VARCHAR2(40)
        , RULE_EXT_ID VARCHAR2(40) NOT NULL
        , VAL VARCHAR2(2000) NOT NULL
        , KEY_CD VARCHAR2(2000) NOT NULL
        , VER_NBR NUMBER(8) default 0
)
/
ALTER TABLE KREW_RULE_EXT_VAL_T
    ADD CONSTRAINT KREW_RULE_EXT_VAL_TP1
PRIMARY KEY (RULE_EXT_VAL_ID)
/
CREATE INDEX KREW_RULE_EXT_VAL_T1 
  ON KREW_RULE_EXT_VAL_T 
  (RULE_EXT_ID)
/
CREATE INDEX KREW_RULE_EXT_VAL_T2 
  ON KREW_RULE_EXT_VAL_T 
  (RULE_EXT_VAL_ID, KEY_CD)
/
INSERT INTO KREW_RULE_EXT_VAL_T
(RULE_EXT_VAL_ID, RULE_EXT_ID, VAL, KEY_CD, VER_NBR)
SELECT 
CAST(RULE_EXT_VAL_ID as VARCHAR2(40)), CAST(RULE_EXT_ID as VARCHAR2(40)), VAL, KEY_CD, VER_NBR
FROM TEMP_KREW_RULE_EXT_VAL_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_RULE_EXT_VAL_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_RULE_EXT_VAL_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_RULE_EXPR_T
-----------------------------------------------------------------------------
ALTER TABLE KREW_RULE_EXPR_T DROP CONSTRAINT KREW_RULE_EXPR_TP1
/
ALTER TABLE KREW_RULE_EXPR_T RENAME TO TEMP_KREW_RULE_EXPR_T
/
ALTER TABLE TEMP_KREW_RULE_EXPR_T DROP CONSTRAINT KREW_RULE_EXPR_TC0
/
CREATE TABLE KREW_RULE_EXPR_T
(
      RULE_EXPR_ID VARCHAR2(40)
        , TYP VARCHAR2(256) NOT NULL
        , RULE_EXPR VARCHAR2(4000)
        , OBJ_ID VARCHAR2(36) NOT NULL
        , VER_NBR NUMBER(8) default 0
    , CONSTRAINT KREW_RULE_EXPR_TC0 UNIQUE (OBJ_ID)
)
/
ALTER TABLE KREW_RULE_EXPR_T
    ADD CONSTRAINT KREW_RULE_EXPR_TP1
PRIMARY KEY (RULE_EXPR_ID)
/
INSERT INTO KREW_RULE_EXPR_T
(RULE_EXPR_ID, TYP, RULE_EXPR, OBJ_ID, VER_NBR)
SELECT 
CAST(RULE_EXPR_ID as VARCHAR2(40)), TYP, RULE_EXPR, OBJ_ID, VER_NBR
FROM TEMP_KREW_RULE_EXPR_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_RULE_EXPR_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_RULE_EXPR_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_APP_DOC_STAT_TRAN_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_APP_DOC_STAT_TRAN_TC0' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_APP_DOC_STAT_TRAN_T DROP CONSTRAINT KREW_APP_DOC_STAT_TRAN_TC0';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_APP_DOC_STAT_TRAN_TC0 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_APP_DOC_STAT_TRAN_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_APP_DOC_STAT_TRAN_T DROP CONSTRAINT KREW_APP_DOC_STAT_TRAN_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_APP_DOC_STAT_TRAN_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_APP_DOC_STAT_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_APP_DOC_STAT_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_APP_DOC_STAT_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_APP_DOC_STAT_TI2' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_APP_DOC_STAT_TI2';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_APP_DOC_STAT_TI2 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_APP_DOC_STAT_TI3' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_APP_DOC_STAT_TI3';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_APP_DOC_STAT_TI3 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_APP_DOC_STAT_TRAN_T RENAME TO TEMP_KREW_APP_DOC_STAT_TRAN_T
/
CREATE TABLE KREW_APP_DOC_STAT_TRAN_T
(
      APP_DOC_STAT_TRAN_ID VARCHAR2(40)
        , DOC_HDR_ID VARCHAR2(40)
        , APP_DOC_STAT_FROM VARCHAR2(64)
        , APP_DOC_STAT_TO VARCHAR2(64)
        , STAT_TRANS_DATE DATE
        , VER_NBR NUMBER(8) default 0
        , OBJ_ID VARCHAR2(36) NOT NULL
    , CONSTRAINT KREW_APP_DOC_STAT_TRAN_TC0 UNIQUE (OBJ_ID)
)
/
ALTER TABLE KREW_APP_DOC_STAT_TRAN_T
    ADD CONSTRAINT KREW_APP_DOC_STAT_TRAN_TP1
PRIMARY KEY (APP_DOC_STAT_TRAN_ID)
/
CREATE INDEX KREW_APP_DOC_STAT_TI1 
  ON KREW_APP_DOC_STAT_TRAN_T 
  (DOC_HDR_ID, STAT_TRANS_DATE)
/
CREATE INDEX KREW_APP_DOC_STAT_TI2 
  ON KREW_APP_DOC_STAT_TRAN_T 
  (DOC_HDR_ID, APP_DOC_STAT_FROM)
/
CREATE INDEX KREW_APP_DOC_STAT_TI3 
  ON KREW_APP_DOC_STAT_TRAN_T 
  (DOC_HDR_ID, APP_DOC_STAT_TO)
/
INSERT INTO KREW_APP_DOC_STAT_TRAN_T
(APP_DOC_STAT_TRAN_ID, DOC_HDR_ID, APP_DOC_STAT_FROM, APP_DOC_STAT_TO, STAT_TRANS_DATE, VER_NBR, OBJ_ID)
SELECT 
CAST(APP_DOC_STAT_TRAN_ID as VARCHAR2(40)), DOC_HDR_ID, APP_DOC_STAT_FROM, APP_DOC_STAT_TO, STAT_TRANS_DATE, VER_NBR, OBJ_ID
FROM TEMP_KREW_APP_DOC_STAT_TRAN_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_APP_DOC_STAT_TRAN_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_APP_DOC_STAT_TRAN_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_DOC_HDR_EXT_DT_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_DOC_HDR_EXT_DT_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_DOC_HDR_EXT_DT_T DROP CONSTRAINT KREW_DOC_HDR_EXT_DT_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_HDR_EXT_DT_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_DOC_HDR_EXT_DT_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_DOC_HDR_EXT_DT_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_HDR_EXT_DT_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_DOC_HDR_EXT_DT_TI2' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_DOC_HDR_EXT_DT_TI2';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_HDR_EXT_DT_TI2 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_DOC_HDR_EXT_DT_TI3' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_DOC_HDR_EXT_DT_TI3';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_HDR_EXT_DT_TI3 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_DOC_HDR_EXT_DT_T RENAME TO TEMP_KREW_DOC_HDR_EXT_DT_T
/
CREATE TABLE KREW_DOC_HDR_EXT_DT_T
(
      DOC_HDR_EXT_DT_ID VARCHAR2(40)
        , DOC_HDR_ID VARCHAR2(40) NOT NULL
        , KEY_CD VARCHAR2(256) NOT NULL
        , VAL DATE
)
/
ALTER TABLE KREW_DOC_HDR_EXT_DT_T
    ADD CONSTRAINT KREW_DOC_HDR_EXT_DT_TP1
PRIMARY KEY (DOC_HDR_EXT_DT_ID)
/
CREATE INDEX KREW_DOC_HDR_EXT_DT_TI1 
  ON KREW_DOC_HDR_EXT_DT_T 
  (KEY_CD, VAL)
/
CREATE INDEX KREW_DOC_HDR_EXT_DT_TI2 
  ON KREW_DOC_HDR_EXT_DT_T 
  (DOC_HDR_ID)
/
CREATE INDEX KREW_DOC_HDR_EXT_DT_TI3 
  ON KREW_DOC_HDR_EXT_DT_T 
  (VAL)
/
INSERT INTO KREW_DOC_HDR_EXT_DT_T
(DOC_HDR_EXT_DT_ID, DOC_HDR_ID, KEY_CD, VAL)
SELECT 
CAST(DOC_HDR_EXT_DT_ID as VARCHAR2(40)), DOC_HDR_ID, KEY_CD, VAL
FROM TEMP_KREW_DOC_HDR_EXT_DT_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_DOC_HDR_EXT_DT_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_DOC_HDR_EXT_DT_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_DOC_HDR_EXT_LONG_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_DOC_HDR_EXT_LONG_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_DOC_HDR_EXT_LONG_T DROP CONSTRAINT KREW_DOC_HDR_EXT_LONG_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_HDR_EXT_LONG_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_DOC_HDR_EXT_LONG_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_DOC_HDR_EXT_LONG_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_HDR_EXT_LONG_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_DOC_HDR_EXT_LONG_TI2' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_DOC_HDR_EXT_LONG_TI2';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_HDR_EXT_LONG_TI2 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_DOC_HDR_EXT_LONG_TI3' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_DOC_HDR_EXT_LONG_TI3';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_HDR_EXT_LONG_TI3 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_DOC_HDR_EXT_LONG_T RENAME TO TEMP_KREW_DOC_HDR_EXT_LONG_T
/
CREATE TABLE KREW_DOC_HDR_EXT_LONG_T
(
      DOC_HDR_EXT_LONG_ID VARCHAR2(40)
        , DOC_HDR_ID VARCHAR2(40) NOT NULL
        , KEY_CD VARCHAR2(256) NOT NULL
        , VAL NUMBER(22)
)
/
ALTER TABLE KREW_DOC_HDR_EXT_LONG_T
    ADD CONSTRAINT KREW_DOC_HDR_EXT_LONG_TP1
PRIMARY KEY (DOC_HDR_EXT_LONG_ID)
/
CREATE INDEX KREW_DOC_HDR_EXT_LONG_TI1 
  ON KREW_DOC_HDR_EXT_LONG_T 
  (KEY_CD, VAL)
/
CREATE INDEX KREW_DOC_HDR_EXT_LONG_TI2 
  ON KREW_DOC_HDR_EXT_LONG_T 
  (DOC_HDR_ID)
/
CREATE INDEX KREW_DOC_HDR_EXT_LONG_TI3 
  ON KREW_DOC_HDR_EXT_LONG_T 
  (VAL)
/
INSERT INTO KREW_DOC_HDR_EXT_LONG_T
(DOC_HDR_EXT_LONG_ID, DOC_HDR_ID, KEY_CD, VAL)
SELECT 
CAST(DOC_HDR_EXT_LONG_ID as VARCHAR2(40)), DOC_HDR_ID, KEY_CD, VAL
FROM TEMP_KREW_DOC_HDR_EXT_LONG_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_DOC_HDR_EXT_LONG_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_DOC_HDR_EXT_LONG_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_DOC_HDR_EXT_FLT_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_DOC_HDR_EXT_FLT_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_DOC_HDR_EXT_FLT_T DROP CONSTRAINT KREW_DOC_HDR_EXT_FLT_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_HDR_EXT_FLT_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_DOC_HDR_EXT_FLT_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_DOC_HDR_EXT_FLT_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_HDR_EXT_FLT_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_DOC_HDR_EXT_FLT_TI2' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_DOC_HDR_EXT_FLT_TI2';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_HDR_EXT_FLT_TI2 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_DOC_HDR_EXT_FLT_TI3' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_DOC_HDR_EXT_FLT_TI3';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_HDR_EXT_FLT_TI3 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_DOC_HDR_EXT_FLT_T RENAME TO TEMP_KREW_DOC_HDR_EXT_FLT_T
/
CREATE TABLE KREW_DOC_HDR_EXT_FLT_T
(
      DOC_HDR_EXT_FLT_ID VARCHAR2(40)
        , DOC_HDR_ID VARCHAR2(40) NOT NULL
        , KEY_CD VARCHAR2(256) NOT NULL
        , VAL NUMBER(30,15)
)
/
ALTER TABLE KREW_DOC_HDR_EXT_FLT_T
    ADD CONSTRAINT KREW_DOC_HDR_EXT_FLT_TP1
PRIMARY KEY (DOC_HDR_EXT_FLT_ID)
/
CREATE INDEX KREW_DOC_HDR_EXT_FLT_TI1 
  ON KREW_DOC_HDR_EXT_FLT_T 
  (KEY_CD, VAL)
/
CREATE INDEX KREW_DOC_HDR_EXT_FLT_TI2 
  ON KREW_DOC_HDR_EXT_FLT_T 
  (DOC_HDR_ID)
/
CREATE INDEX KREW_DOC_HDR_EXT_FLT_TI3 
  ON KREW_DOC_HDR_EXT_FLT_T 
  (VAL)
/
INSERT INTO KREW_DOC_HDR_EXT_FLT_T
(DOC_HDR_EXT_FLT_ID, DOC_HDR_ID, KEY_CD, VAL)
SELECT 
CAST(DOC_HDR_EXT_FLT_ID as VARCHAR2(40)), DOC_HDR_ID, KEY_CD, VAL
FROM TEMP_KREW_DOC_HDR_EXT_FLT_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_DOC_HDR_EXT_FLT_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_DOC_HDR_EXT_FLT_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
-----------------------------------------------------------------------------
-- KREW_DOC_HDR_EXT_T
-----------------------------------------------------------------------------
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_DOC_HDR_EXT_TP1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_DOC_HDR_EXT_T DROP CONSTRAINT KREW_DOC_HDR_EXT_TP1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_HDR_EXT_TP1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_DOC_HDR_EXT_TI1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_DOC_HDR_EXT_TI1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_HDR_EXT_TI1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_DOC_HDR_EXT_TI2' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_DOC_HDR_EXT_TI2';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_HDR_EXT_TI2 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_indexes where INDEX_NAME = 'KREW_DOC_HDR_EXT_TI3' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'DROP INDEX KREW_DOC_HDR_EXT_TI3';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_DOC_HDR_EXT_TI3 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
ALTER TABLE KREW_DOC_HDR_EXT_T RENAME TO TEMP_KREW_DOC_HDR_EXT_T
/
CREATE TABLE KREW_DOC_HDR_EXT_T
(
      DOC_HDR_EXT_ID VARCHAR2(40)
        , DOC_HDR_ID VARCHAR2(40) NOT NULL
        , KEY_CD VARCHAR2(256) NOT NULL
        , VAL VARCHAR2(2000)
)
/
ALTER TABLE KREW_DOC_HDR_EXT_T
    ADD CONSTRAINT KREW_DOC_HDR_EXT_TP1
PRIMARY KEY (DOC_HDR_EXT_ID)
/
CREATE INDEX KREW_DOC_HDR_EXT_TI1 
  ON KREW_DOC_HDR_EXT_T 
  (KEY_CD, VAL)
/
CREATE INDEX KREW_DOC_HDR_EXT_TI2 
  ON KREW_DOC_HDR_EXT_T 
  (DOC_HDR_ID)
/
CREATE INDEX KREW_DOC_HDR_EXT_TI3 
  ON KREW_DOC_HDR_EXT_T 
  (VAL)
/
INSERT INTO KREW_DOC_HDR_EXT_T
(DOC_HDR_EXT_ID, DOC_HDR_ID, KEY_CD, VAL)
SELECT 
CAST(DOC_HDR_EXT_ID as VARCHAR2(40)), DOC_HDR_ID, KEY_CD, VAL
FROM TEMP_KREW_DOC_HDR_EXT_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_DOC_HDR_EXT_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_DOC_HDR_EXT_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

-----------------------------------------------------------------------------
-- FOREIGN KEY CONSTRAINTS IMPACTED BY CHANGES
-----------------------------------------------------------------------------
ALTER TABLE KREW_RULE_T
    ADD CONSTRAINT KREW_RULE_TR1 FOREIGN KEY (RULE_EXPR_ID)
    REFERENCES KREW_RULE_EXPR_T (RULE_EXPR_ID)
/
ALTER TABLE KREW_RTE_NODE_CFG_PARM_T
    ADD CONSTRAINT EN_RTE_NODE_CFG_PARM_TR1 FOREIGN KEY (RTE_NODE_ID)
    REFERENCES KREW_RTE_NODE_T (RTE_NODE_ID)
/





-- 
-- 2011-07-11-m6.sql
-- 


--
-- NOTE: when assembling this script for release, please merge any table rebuilds with those from 2011-04-28.sql
--

-----------------------------------------------------------------------------
-- KREW_RTE_NODE_LNK_T
-----------------------------------------------------------------------------
ALTER TABLE KREW_RTE_NODE_LNK_T RENAME TO TEMP_KREW_RTE_NODE_LNK_T
/
ALTER TABLE TEMP_KREW_RTE_NODE_LNK_T DROP CONSTRAINT KREW_RTE_NODE_LNK_TP1
/
DROP INDEX KREW_RTE_NODE_LNK_TI1
/
DROP INDEX KREW_RTE_NODE_LNK_TI2
/
CREATE TABLE KREW_RTE_NODE_LNK_T
(
	FROM_RTE_NODE_ID VARCHAR2(40)
    , TO_RTE_NODE_ID VARCHAR2(40)
)
/
ALTER TABLE KREW_RTE_NODE_LNK_T
    ADD CONSTRAINT KREW_RTE_NODE_LNK_TP1
PRIMARY KEY (FROM_RTE_NODE_ID, TO_RTE_NODE_ID)
/
CREATE INDEX KREW_RTE_NODE_LNK_TI1 
  ON KREW_RTE_NODE_LNK_T 
  (FROM_RTE_NODE_ID)
/
CREATE INDEX KREW_RTE_NODE_LNK_TI2 
  ON KREW_RTE_NODE_LNK_T 
  (TO_RTE_NODE_ID)
/
INSERT INTO KREW_RTE_NODE_LNK_T
(FROM_RTE_NODE_ID, TO_RTE_NODE_ID)
SELECT CAST(FROM_RTE_NODE_ID as VARCHAR2(40)), CAST(TO_RTE_NODE_ID as VARCHAR2(40))
FROM TEMP_KREW_RTE_NODE_LNK_T
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_RTE_NODE_LNK_T';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_RTE_NODE_LNK_T CASCADE CONSTRAINTS PURGE'; END IF;
END;
/





-- 
-- 2011-07-13.sql
-- 


--KRCR_PARM_T
ALTER TABLE KRCR_PARM_T RENAME column PARM_DTL_TYP_CD to CMPNT_CD
/
ALTER TABLE KRCR_PARM_T RENAME column TXT to VAL
/
ALTER TABLE KRCR_PARM_T RENAME column CONS_CD to EVAL_OPRTR_CD
/

--KRCR_PARM_DTL_TYP_T to KRCR_CMPNT_T
ALTER TABLE KRCR_PARM_DTL_TYP_T RENAME TO KRCR_CMPNT_T
/
ALTER TABLE KRCR_CMPNT_T RENAME COLUMN PARM_DTL_TYP_CD TO CMPNT_CD
/

--KRLC_CMP_TYP_T
ALTER TABLE KRLC_CMP_TYP_T DROP COLUMN DOBJ_MAINT_CD_ACTV_IND
/





-- 
-- 2011-07-22.sql
-- 


-- Oracle sql for KULRICE-5419:
alter table krms_cntxt_t add desc_txt varchar2(255) default null
/
alter table krms_term_spec_t add desc_txt varchar2(255) default null
/
alter table krms_term_t add desc_txt varchar2(255) default null
/
alter table krms_attr_defn_t add desc_txt varchar2(255) default null
/





-- 
-- 2011-07-24-m7.sql
-- 


drop sequence KREW_HLP_S 
/
drop table KREW_HLP_T
/





-- 
-- 2011-07-25-m7.sql
-- 


drop table KREW_RIA_DOC_T
/
drop sequence KREW_RIA_DOCTYPE_MAP_ID_S
/
drop table KREW_RIA_DOCTYPE_MAP_T
/
drop table KREW_RMV_RPLC_DOC_T 
/
drop table KREW_RMV_RPLC_GRP_T
/
drop table KREW_RMV_RPLC_RULE_T
/





-- 
-- 2011-07-28-m7.sql
-- 


-----------------------------------------------------------------------------
-- KREW_INIT_RTE_NODE_INSTN_T
-----------------------------------------------------------------------------
ALTER TABLE KREW_INIT_RTE_NODE_INSTN_T DROP CONSTRAINT KREW_INIT_RTE_NODE_INSTN_TP1
/
DROP INDEX KREW_INIT_RTE_NODE_INSTN_TI1
/
DROP INDEX KREW_INIT_RTE_NODE_INSTN_TI2
/
ALTER TABLE KREW_INIT_RTE_NODE_INSTN_T RENAME TO TEMP_KREW_INIT_RTE_NODE
/
CREATE TABLE KREW_INIT_RTE_NODE_INSTN_T
(
         DOC_HDR_ID VARCHAR2(40) NOT NULL
        , RTE_NODE_INSTN_ID VARCHAR2(40) NOT NULL
)
/
ALTER TABLE KREW_INIT_RTE_NODE_INSTN_T
    ADD CONSTRAINT KREW_INIT_RTE_NODE_INSTN_TP1
PRIMARY KEY (DOC_HDR_ID, RTE_NODE_INSTN_ID)
/
CREATE INDEX KREW_INIT_RTE_NODE_INSTN_TI1
  ON KREW_INIT_RTE_NODE_INSTN_T
  (DOC_HDR_ID)
/
CREATE INDEX KREW_INIT_RTE_NODE_INSTN_TI2
  ON KREW_INIT_RTE_NODE_INSTN_T
  (RTE_NODE_INSTN_ID)
/
INSERT INTO KREW_INIT_RTE_NODE_INSTN_T
(DOC_HDR_ID, RTE_NODE_INSTN_ID)
SELECT DOC_HDR_ID, CAST(RTE_NODE_INSTN_ID as VARCHAR2(40))
FROM TEMP_KREW_INIT_RTE_NODE
/
DECLARE temp NUMBER;
BEGIN
	SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'TEMP_KREW_INIT_RTE_NODE';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE TEMP_KREW_INIT_RTE_NODE CASCADE CONSTRAINTS PURGE'; END IF;
END;
/





-- 
-- 2011-07-29-m7.sql
-- 


update KRIM_PERM_T t set NM='Take Requested Approve Action' where PERM_ID = '170'
/





-- 
-- 2011-07-29.sql
-- 


--
-- make combo of nm & nmspc_cd unique in all applicable KRMS tables
-- and drop nmspc_cd where it doesn't make sense
--

--
-- break direct fk to krms_cntxt_t from krms_term_rslvr_t & krms_term_spec_t
--

-- remove cntxt_id from krms_term_rslvr_t, fix unique constraint
alter table krms_term_rslvr_t drop constraint krms_term_rslvr_tc1
/
alter table krms_term_rslvr_t add constraint krms_term_rslvr_tc1 unique (nm, nmspc_cd)
/
alter table krms_term_rslvr_t drop constraint KRMS_TERM_RSLVR_FK2
/
alter table krms_term_rslvr_t drop column cntxt_id
/
-- remove fk from krms_term_spec_t to krms_cntxt_t
alter table krms_term_spec_t add nmspc_cd varchar2(40) not null
/
alter table krms_term_spec_t drop constraint KRMS_ASSET_FK1
/
drop index KRMS_ASSET_TI1
/
alter table krms_term_spec_t drop constraint krms_asset_tc1
/
alter table krms_term_spec_t add constraint krms_term_spec_tc1 unique (nm, nmspc_cd)
/
alter table krms_term_spec_t drop column cntxt_id
/
--
-- refactor krms_cntxt_term_spec_prereq_t to be a valid term specs table instead
--
-- rename krms_cntxt_term_spec_prereq_t to krms_cntxt_vld_term_spec_t
-- and add prereq column
alter table krms_cntxt_term_spec_prereq_t drop constraint KRMS_CNTXT_ASSET_PREREQ_FK1
/
drop index krms_cntxt_asset_prereq_ti1
/
alter table krms_cntxt_term_spec_prereq_t drop constraint KRMS_CNTXT_ASSET_PREREQ_FK2
/
drop index krms_cntxt_asset_prereq_ti2
/
alter table krms_cntxt_term_spec_prereq_t rename to krms_cntxt_vld_term_spec_t
/
alter table krms_cntxt_vld_term_spec_t add prereq varchar2(1) default 'n'
/
alter table krms_cntxt_vld_term_spec_t add constraint krms_cntxt_vld_term_spec_ti1 foreign key (cntxt_id) references krms_cntxt_t(cntxt_id)
/
alter table krms_cntxt_vld_term_spec_t add constraint krms_cntxt_vld_term_spec_ti2 foreign key (term_spec_id) references krms_term_spec_t(term_spec_id)
/
--
-- set up some missing unique constraints
--
-- wow, Oracle and MySQL support the same syntax here
alter table krms_cntxt_t add constraint krms_cntxt_tc1 unique (nm, nmspc_cd)
/
alter table krms_func_t add constraint krms_func_tc1 unique (nm, nmspc_cd)
/
-- drop namespace code from krms_agenda_t
alter table krms_agenda_t drop column nmspc_cd
/
alter table krms_agenda_t add constraint krms_agenda_tc1 unique (nm, cntxt_id)
/
alter table krms_typ_t add constraint krms_typ_tc1 unique (nm, nmspc_cd)
/
alter table krms_attr_defn_t add constraint krms_attr_defn_tc1 unique (nm, nmspc_cd)
/
alter table krms_rule_t add constraint krms_rule_tc1 unique (nm, nmspc_cd)
/
--
-- clean up some crufty index and constraint names
--

alter table krms_term_rslvr_attr_t drop constraint KRMS_ASSET_RSLVR_ATTR_FK1
/
alter table krms_term_rslvr_attr_t drop constraint KRMS_ASSET_RSLVR_ATTR_FK2
/
drop index krms_asset_rslvr_attr_ti1
/
create index krms_term_rslvr_attr_ti1 on krms_term_rslvr_attr_t (term_rslvr_id)
/
drop index krms_asset_rslvr_attr_ti2
/
create index krms_term_rslvr_attr_ti2 on krms_term_rslvr_attr_t (attr_defn_id)
/
alter table krms_term_rslvr_attr_t add constraint krms_term_rslvr_attr_fk1 foreign key (term_rslvr_id) references krms_term_rslvr_t (term_rslvr_id)
/
alter table krms_term_rslvr_attr_t add constraint krms_term_rslvr_attr_fk2 foreign key (attr_defn_id) references krms_attr_defn_t (attr_defn_id)
/





-- 
-- 2011-08-11.sql
-- 




-- -----------------------------------------------------
-- Table krew_typ_t
-- -----------------------------------------------------

CREATE  TABLE krew_typ_t (
  typ_id VARCHAR2(40) NOT NULL ,
  nm VARCHAR2(100) NOT NULL ,
  nmspc_cd VARCHAR2(40) NOT NULL ,
  srvc_nm VARCHAR2(200) NULL ,
  actv VARCHAR2(1) DEFAULT 'Y' NOT NULL ,
  ver_nbr NUMBER(8) DEFAULT 0 NOT NULL ,
  PRIMARY KEY (typ_id) )
/

CREATE UNIQUE INDEX krew_typ_tc1 ON krew_typ_t (nm, nmspc_cd)
/


-- -----------------------------------------------------
-- Table krew_ppl_flw_t
-- -----------------------------------------------------

CREATE  TABLE krew_ppl_flw_t (
  ppl_flw_id VARCHAR2(40) NOT NULL ,
  nm VARCHAR2(100) NOT NULL ,
  nmspc_cd VARCHAR2(40) NOT NULL ,
  typ_id VARCHAR2(40) NOT NULL ,
  actv VARCHAR2(1) DEFAULT 'Y' NOT NULL ,
  ver_nbr NUMBER(8) DEFAULT 0 NOT NULL ,
  desc_txt VARCHAR2(4000) NULL ,
  PRIMARY KEY (ppl_flw_id) ,
  CONSTRAINT krew_ppl_flw_fk1
    FOREIGN KEY (typ_id )
    REFERENCES krew_typ_t (typ_id ))
/

CREATE UNIQUE INDEX krew_ppl_flw_tc1 ON krew_ppl_flw_t (nm, nmspc_cd)
/

CREATE INDEX krew_ppl_flw_fk1 ON krew_ppl_flw_t (typ_id)
/


-- -----------------------------------------------------
-- Table krew_attr_defn_t
-- -----------------------------------------------------

CREATE  TABLE krew_attr_defn_t (
  attr_defn_id VARCHAR2(40) NOT NULL ,
  nm VARCHAR2(100) NOT NULL ,
  nmspc_cd VARCHAR2(40) NOT NULL ,
  lbl VARCHAR2(40) NULL ,
  actv VARCHAR2(1) DEFAULT 'Y' NOT NULL ,
  cmpnt_nm VARCHAR2(100) NULL ,
  ver_nbr NUMBER(8) DEFAULT 0 NOT NULL ,
  desc_txt VARCHAR2(40) NULL ,
  PRIMARY KEY (attr_defn_id) )
/

CREATE UNIQUE INDEX krew_attr_defn_tc1 ON krew_attr_defn_t (nm, nmspc_cd)
/


-- -----------------------------------------------------
-- Table krew_typ_attr_t
-- -----------------------------------------------------

CREATE  TABLE krew_typ_attr_t (
  typ_attr_id VARCHAR2(40) NOT NULL ,
  seq_no NUMBER(5) NOT NULL ,
  typ_id VARCHAR2(40) NOT NULL ,
  attr_defn_id VARCHAR2(255) NOT NULL ,
  actv VARCHAR2(1) DEFAULT 'Y' NOT NULL ,
  ver_nbr NUMBER(8) DEFAULT 0 NOT NULL ,
  PRIMARY KEY (typ_attr_id) ,
  CONSTRAINT krew_typ_attr_fk1
    FOREIGN KEY (attr_defn_id )
    REFERENCES krew_attr_defn_t (attr_defn_id ),
  CONSTRAINT krew_typ_attr_fk2
    FOREIGN KEY (typ_id )
    REFERENCES krew_typ_t (typ_id ))
/

CREATE INDEX krew_typ_attr_ti1 ON krew_typ_attr_t (attr_defn_id)
/

CREATE INDEX krew_typ_attr_ti2 ON krew_typ_attr_t (typ_id)
/

CREATE UNIQUE INDEX krew_typ_attr_tc1 ON krew_typ_attr_t (typ_id, attr_defn_id)
/


-- -----------------------------------------------------
-- Table krew_ppl_flw_mbr_t
-- -----------------------------------------------------

CREATE  TABLE krew_ppl_flw_mbr_t (
  ppl_flw_mbr_id VARCHAR2(40) NOT NULL ,
  ppl_flw_id VARCHAR2(40) NOT NULL ,
  mbr_typ_cd VARCHAR2(1) NOT NULL ,
  mbr_id VARCHAR2(40) NOT NULL ,
  prio NUMBER(8) NULL ,
  dlgt_frm_id VARCHAR2(40) NULL ,
  ver_nbr NUMBER(8) DEFAULT 0 NOT NULL ,
  PRIMARY KEY (ppl_flw_mbr_id) ,
  CONSTRAINT krew_ppl_flw_mbr_fk1
    FOREIGN KEY (ppl_flw_id )
    REFERENCES krew_ppl_flw_t (ppl_flw_id ),
  CONSTRAINT krew_ppl_flw_mbr_fk2
    FOREIGN KEY (dlgt_frm_id )
    REFERENCES krew_ppl_flw_mbr_t (ppl_flw_mbr_id ))
/

CREATE INDEX krew_ppl_flw_mbr_ti1 ON krew_ppl_flw_mbr_t (ppl_flw_id)
/

CREATE INDEX krew_ppl_flw_mbr_ti2 ON krew_ppl_flw_mbr_t (ppl_flw_id, prio)
/

CREATE UNIQUE INDEX krew_ppl_flw_mbr_tc1 ON krew_ppl_flw_mbr_t (ppl_flw_id, mbr_typ_cd, mbr_id, dlgt_frm_id)
/

CREATE INDEX krew_ppl_flw_mbr_fk2 ON krew_ppl_flw_mbr_t (dlgt_frm_id)
/


-- -----------------------------------------------------
-- Table krew_ppl_flw_attr_t
-- -----------------------------------------------------

CREATE  TABLE krew_ppl_flw_attr_t (
  ppl_flw_attr_id VARCHAR2(40) NOT NULL ,
  ppl_flw_id VARCHAR2(40) NOT NULL ,
  attr_defn_id VARCHAR2(40) NOT NULL ,
  attr_val VARCHAR2(400) NULL ,
  ver_nbr NUMBER(8) DEFAULT 0 NOT NULL ,
  PRIMARY KEY (ppl_flw_attr_id) ,
  CONSTRAINT krew_ppl_flw_attr_fk1
    FOREIGN KEY (ppl_flw_id )
    REFERENCES krew_ppl_flw_t (ppl_flw_id ),
  CONSTRAINT krew_ppl_flw_attr_fk2
    FOREIGN KEY (attr_defn_id )
    REFERENCES krew_attr_defn_t (attr_defn_id ))
/

CREATE INDEX krew_ppl_flw_attr_ti1 ON krew_ppl_flw_attr_t (ppl_flw_id)
/

CREATE INDEX krew_ppl_flw_attr_ti2 ON krew_ppl_flw_attr_t (attr_defn_id)
/


-- -----------------------------------------------------
-- Table krew_typ_s
-- -----------------------------------------------------
CREATE SEQUENCE krew_typ_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

-- -----------------------------------------------------
-- Table krew_typ_attr_s
-- -----------------------------------------------------
CREATE SEQUENCE krew_typ_attr_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

-- -----------------------------------------------------
-- Table krew_attr_defn_s
-- -----------------------------------------------------
CREATE SEQUENCE krew_attr_defn_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

-- -----------------------------------------------------
-- Table krew_ppl_flw_s
-- -----------------------------------------------------
CREATE SEQUENCE krew_ppl_flw_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

-- -----------------------------------------------------
-- Table krew_ppl_flw_attr_s
-- -----------------------------------------------------
CREATE SEQUENCE krew_ppl_flw_attr_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

-- -----------------------------------------------------
-- Table krew_ppl_flw_mbr_s
-- -----------------------------------------------------
CREATE SEQUENCE krew_ppl_flw_mbr_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/





-- 
-- 2011-08-17.sql
-- 


delete from krcr_parm_t
where nmspc_cd = 'KR-NS'
and cmpnt_cd = 'All'
and parm_nm in ('STRING_TO_DATE_FORMATS', 'STRING_TO_TIMESTAMP_FORMATS', 'TIMESTAMP_TO_STRING_FORMAT_FOR_USER_INTERFACE', 'DATE_TO_STRING_FORMAT_FOR_FILE_NAME', 'TIMESTAMP_TO_STRING_FORMAT_FOR_FILE_NAME')
/





-- 
-- 2011-08-29.sql
-- 


delete from krcr_parm_t where PARM_NM = 'CACHING_IND'
/





-- 
-- 2011-09-07.sql
-- 


-- KULRICE-5360 rename KIM entity fields
alter table KRIM_ENTITY_NM_T rename column TITLE_NM to PREFIX_NM
/

alter table KRIM_ENTITY_BIO_T rename column BIRTH_STATE_CD to BIRTH_STATE_PVC_CD
/

alter table KRIM_ENTITY_ADDR_T rename column POSTAL_STATE_CD to STATE_PVC_CD
/
alter table KRIM_ENTITY_ADDR_T rename column CITY_NM to CITY
/

alter table KRIM_PND_NM_MT rename column TITLE_NM to PREFIX_NM
/
alter table KRIM_PND_ADDR_MT rename column POSTAL_STATE_CD to STATE_PVC_CD
/
alter table KRIM_PND_ADDR_MT rename column CITY_NM to CITY
/





-- 
-- 2011-09-15.sql
-- 


delete from KREW_USR_OPTN_T where PRSN_OPTN_ID like 'DocSearch%'
/





-- 
-- 2011-09-16.sql
-- 


-- KULRICE-5360 add KIM entity fields
alter table KRIM_ENTITY_NM_T add TITLE_NM VARCHAR(20)
/
alter table KRIM_ENTITY_NM_T add NOTE_MSG VARCHAR(1024)
/
alter table KRIM_ENTITY_NM_T add NM_CHNG_DT DATE
/

alter table KRIM_ENTITY_ADDR_T add ATTN_LINE VARCHAR(45)
/
alter table KRIM_ENTITY_ADDR_T add ADDR_FMT VARCHAR(256)
/
alter table KRIM_ENTITY_ADDR_T add MOD_DT DATE DEFAULT SYSDATE
/
alter table KRIM_ENTITY_ADDR_T add VALID_DT DATE
/
alter table KRIM_ENTITY_ADDR_T add VALID_IND VARCHAR(1)
/
alter table KRIM_ENTITY_ADDR_T add NOTE_MSG VARCHAR(1024)
/


alter table KRIM_ENTITY_BIO_T add NOTE_MSG VARCHAR(1024)
/
alter table KRIM_ENTITY_BIO_T add GNDR_CHG_CD VARCHAR(20)
/

alter table KRIM_PND_NM_MT add TITLE_NM VARCHAR(20)
/
alter table KRIM_PND_NM_MT add NOTE_MSG VARCHAR(1024)
/
alter table KRIM_PND_NM_MT add NM_CHNG_DT DATE
/

alter table KRIM_PND_ADDR_MT add ATTN_LINE VARCHAR(45)
/
alter table KRIM_PND_ADDR_MT add ADDR_FMT VARCHAR(256)
/
alter table KRIM_PND_ADDR_MT add MOD_DT DATE DEFAULT SYSDATE
/
alter table KRIM_PND_ADDR_MT add VALID_DT DATE
/
alter table KRIM_PND_ADDR_MT add VALID_IND VARCHAR(1)
/
alter table KRIM_PND_ADDR_MT add NOTE_MSG VARCHAR(1024)
/





-- 
-- 2011-09-18.sql
-- 


alter table KREW_PPL_FLW_T modify(TYP_ID NULL)
/
DECLARE
c NUMBER;
BEGIN
select count(*) into c from all_constraints where CONSTRAINT_NAME = 'KREW_PPL_FLW_MBR_TC1' ;
IF c>0 THEN
EXECUTE IMMEDIATE 'ALTER TABLE KREW_PPL_FLW_MBR_T DROP CONSTRAINT KREW_PPL_FLW_MBR_TC1';
ELSE
DBMS_OUTPUT.PUT_LINE('KREW_PPL_FLW_MBR_TC1 does not exist, so not running statement to change/drop it.');
END IF;
END;
/
alter table KREW_PPL_FLW_MBR_T DROP COLUMN dlgt_frm_id
/

-- -----------------------------------------------------
-- Table krew_ppl_flw_dlgt_t
-- -----------------------------------------------------

CREATE  TABLE krew_ppl_flw_dlgt_t (
  ppl_flw_dlgt_id VARCHAR2(40) NOT NULL ,
  ppl_flw_mbr_id VARCHAR2(40) NOT NULL ,
  mbr_id VARCHAR2(40) NOT NULL ,
  mbr_typ_cd VARCHAR2(1) NOT NULL ,
  dlgn_typ_cd VARCHAR2(1) NOT NULL ,
  ver_nbr NUMBER(8) DEFAULT 0 NOT NULL ,
  PRIMARY KEY (ppl_flw_dlgt_id) ,
  CONSTRAINT krew_ppl_flw_dlgt_fk1
    FOREIGN KEY (ppl_flw_mbr_id )
    REFERENCES krew_ppl_flw_mbr_t (ppl_flw_mbr_id ))
/

CREATE INDEX krew_ppl_flw_dlgt_ti1 ON krew_ppl_flw_dlgt_t (ppl_flw_mbr_id)
/

CREATE SEQUENCE krew_ppl_flw_dlgt_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/





-- 
-- 2011-09-26b.sql
-- 


-- KIM permissions
insert into krcr_nmspc_t
(nmspc_cd, nm, actv_ind, appl_id, ver_nbr, obj_id)
values ('KR-RULE','Kuali Rules','Y','RICE',1,sys_guid())
/

insert into krim_perm_tmpl_t
(perm_tmpl_id, nm, nmspc_cd, desc_txt, kim_typ_id, actv_ind, ver_nbr, obj_id)
values ((select (max(to_number(perm_tmpl_id)) + 1) from krim_perm_tmpl_t where perm_tmpl_id is not NULL and to_number(perm_tmpl_id) < 10000),
        'KRMS Agenda Permission','KR-RULE','View/Maintain Agenda',
        (select kim_typ_id from krim_typ_t where nm = 'Namespace' and nmspc_cd = 'KR-NS'),
        'Y',1,sys_guid())
/

insert into krim_perm_t
(perm_id, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind, ver_nbr, obj_id)
values ((select (max(to_number(perm_id)) + 1) from krim_perm_t where perm_id is not NULL and to_number(perm_id) < 10000),
        (select perm_tmpl_id from krim_perm_tmpl_t where nm = 'KRMS Agenda Permission' and nmspc_cd = 'KR-RULE'),
        'KR-RULE','Maintain KRMS Agenda','Allows creation and modification of agendas via the agenda editor','Y',1,sys_guid())
/

insert into krim_perm_attr_data_t
(attr_data_id, perm_id, kim_typ_id, kim_attr_defn_id, attr_val, ver_nbr, obj_id)
values ((select (max(to_number(attr_data_id)) + 1) from krim_perm_attr_data_t where attr_data_id is not NULL and to_number(attr_data_id) < 10000),
        (select perm_id from krim_perm_t where nm = 'Maintain KRMS Agenda' and nmspc_cd = 'KR-RULE'),
        (select kim_typ_id from krim_typ_t where nm = 'Namespace' and nmspc_cd = 'KR-NS'),
        (select kim_attr_defn_id from krim_attr_defn_t where nm = 'namespaceCode'),
        'KRMS_TEST',1,sys_guid())
/

-- KIM roles
insert into krim_role_t
(role_id, role_nm, nmspc_cd, desc_txt, kim_typ_id, actv_ind, last_updt_dt, obj_id)
values ((select (max(to_number(role_id)) + 1) from krim_role_t where role_id is not NULL and to_number(role_id) < 10000),
        'Kuali Rules Management System Administrator',
        'KR-RULE',
        'This role maintains KRMS agendas and rules.',
        (select kim_typ_id from krim_typ_t where nm = 'Default' and nmspc_cd = 'KUALI'),
        'Y', current_date, sys_guid())
/

insert into krim_role_mbr_t
(role_mbr_id, role_id, mbr_id, mbr_typ_cd, last_updt_dt, ver_nbr, obj_id)
values ((select (max(to_number(role_mbr_id)) + 1) from krim_role_mbr_t where role_mbr_id is not NULL and to_number(role_mbr_id) < 10000),
        (select role_id from krim_role_t where role_nm = 'Kuali Rules Management System Administrator' and nmspc_cd = 'KR-RULE'),
        (select prncpl_id from krim_prncpl_t where prncpl_nm = 'admin'),
        'P', current_date, 1, sys_guid())
/

insert into krim_role_perm_t
(role_perm_id, role_id, perm_id, actv_ind, ver_nbr, obj_id)
values ((select (max(to_number(role_perm_id)) + 1) from krim_role_perm_t where role_perm_id is not NULL and to_number(role_perm_id) < 10000),
        (select role_id from krim_role_t where role_nm = 'Kuali Rules Management System Administrator' and nmspc_cd = 'KR-RULE'),
        (select perm_id from krim_perm_t where nm = 'Maintain KRMS Agenda' and nmspc_cd = 'KR-RULE'),
        'Y', 1, sys_guid())
/




-- 
-- 2011-09-26.sql
-- 


ALTER TABLE KREW_DOC_TYP_T DROP COLUMN CSTM_ACTN_LIST_ATTRIB_CLS_NM
/
ALTER TABLE KREW_DOC_TYP_T DROP COLUMN CSTM_ACTN_EMAIL_ATTRIB_CLS_NM
/
ALTER TABLE KREW_DOC_TYP_T DROP COLUMN CSTM_DOC_NTE_ATTRIB_CLS_NM
/





-- 
-- 2011-09-27.sql
-- 


-- Notification PeopleFlowActionType

insert into krms_typ_t
(typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr)
values ('1000', 'notificationPeopleFlowActionType', 'KRMS', 'notificationPeopleFlowActionTypeService', 'Y', 1)
/

-- Approval PeopleFlowActionType

insert into krms_typ_t
(typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr)
values ('1001', 'approvalPeopleFlowActionType', 'KRMS_TEST', 'approvalPeopleFlowActionTypeService', 'Y', 1)
/





-- 
-- 2011-09-30.sql
-- 




-- -----------------------------------------------------
-- Table krms_cntxt_vld_agenda_t
-- -----------------------------------------------------
-- begin execute immediate 'drop table krms_cntxt_vld_agenda_t'; exception when others then null; end;

CREATE  TABLE  krms_cntxt_vld_agenda_t (
  cntxt_vld_agenda_id VARCHAR2(40)  NOT NULL ,
  cntxt_id VARCHAR2(40)  NOT NULL ,
  agenda_typ_id VARCHAR2(40)  NOT NULL ,
  ver_nbr NUMBER(8) DEFAULT 0  NOT NULL ,
  PRIMARY KEY (cntxt_vld_agenda_id) ,
  -- CREATE INDEX krms_cntxt_vld_agenda_ti1 (cntxt_id ASC) ,
  CONSTRAINT krms_cntxt_vld_agenda_fk1
    FOREIGN KEY (cntxt_id )
    REFERENCES krms_cntxt_t (cntxt_id ) )
/

CREATE INDEX krms_cntxt_vld_agenda_ti1 on krms_cntxt_vld_agenda_t (cntxt_id ASC)
/

CREATE SEQUENCE krms_cntxt_vld_agenda_s INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER
/





-- 
-- 2011-10-03.sql
-- 


alter table KREW_PPL_FLW_MBR_T add ACTN_RQST_PLCY_CD VARCHAR2(1)
/
alter table KREW_PPL_FLW_MBR_T add RSP_ID VARCHAR2(40) NOT NULL
/
alter table KREW_PPL_FLW_DLGT_T add ACTN_RQST_PLCY_CD VARCHAR2(1)
/
alter table KREW_PPL_FLW_DLGT_T add RSP_ID VARCHAR2(40) NOT NULL
/





-- 
-- 2011-10-21.sql
-- 


-- KULRICE-5348
ALTER TABLE KREW_RULE_T RENAME COLUMN PREV_RULE_VER_NBR TO PREV_VER_RULE_ID
/
-- KULRICE-4589
UPDATE KRCR_PARM_T
SET PARM_NM='NOTIFY_GROUPS',
    PARM_DESC_TXT='Defines a group name (in the format "namespace:name") which contains members who should never receive "notifications" action requests from KEW.',
    EVAL_OPRTR_CD='D'
WHERE NMSPC_CD = 'KR-WKFLW'
  AND CMPNT_CD = 'Notification'
  AND PARM_NM = 'NOTIFY_EXCLUDED_USERS_IND'
/





-- 
-- 2011-10-23.sql
-- 


alter table krcr_cmpnt_t add cmpnt_set_id varchar2(40)
/
create table krcr_cmpnt_set_t (
  cmpnt_set_id varchar2(40) not null,
  last_updt_ts date not null,
  chksm varchar2(40) not null,
  ver_nbr number(8) default 0 not null,
  primary key (cmpnt_set_id) )
/





-- 
-- 2011-10-24.sql
-- 


alter table krcr_cmpnt_t drop column cmpnt_set_id
/
create table krcr_drvd_cmpnt_t (
  nmspc_cd varchar2(20) not null,
  cmpnt_cd varchar2(100) not null,
  nm varchar2(255),
  cmpnt_set_id varchar2(40) not null,
  primary key (nmspc_cd, cmpnt_cd))
/





-- 
-- 2011-10-25.sql
-- 


update KREW_RULE_ATTR_T set RULE_ATTR_TYP_CD='DocumentSecurityAttribute' where RULE_ATTR_TYP_CD='DocumentSearchSecurityFilterAttribute'
/

update KRCR_PARM_T set CMPNT_CD='DocumentSearch' where CMPNT_CD='DocSearchCriteriaDTO'
/
insert into KRCR_CMPNT_T (NMSPC_CD, CMPNT_CD, NM, ACTV_IND, OBJ_ID, VER_NBR)
values ('KR-WKFLW', 'DocumentSearch', 'Document Search', 'Y', sys_guid(), 1)
/





-- 
-- 2011-10-26.sql
-- 


insert into KRCR_CMPNT_T (NMSPC_CD, CMPNT_CD, NM, ACTV_IND, OBJ_ID, VER_NBR)
VALUES ('KR-WKFLW', 'Rule', 'Rule', 'Y', sys_guid(), 1)
/
update KRCR_CMPNT_T set cmpnt_cd='EDocLite' where cmpnt_cd like 'EDocLite%'
/





-- 
-- 2011-10-27.sql
-- 


-- create a KIM permission for the Cache Administrator screen/controller

insert into krim_perm_t
(perm_id, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind, ver_nbr, obj_id)
values ((select (max(to_number(perm_id)) + 1) from krim_perm_t where perm_id is not NULL and to_number(perm_id) < 10000),
        (select perm_tmpl_id from krim_perm_tmpl_t where nm = 'Use Screen' and nmspc_cd = 'KR-NS'),
        'KR-SYS','Use Cache Adminstration Screen','Allows use of the cache administration screen','Y',1,sys_guid())
/



insert into krim_perm_attr_data_t
(attr_data_id, perm_id, kim_typ_id, kim_attr_defn_id, attr_val, ver_nbr, obj_id)
values ((select (max(to_number(attr_data_id)) + 1) from krim_perm_attr_data_t where attr_data_id is not NULL and to_number(attr_data_id) < 10000),
        (select perm_id from krim_perm_t where nm = 'Use Cache Adminstration Screen' and nmspc_cd = 'KR-SYS'),
        (select kim_typ_id from krim_typ_t where nm = 'Namespace or Action' and nmspc_cd = 'KR-NS'),
        (select kim_attr_defn_id from krim_attr_defn_t where nm = 'actionClass'),
        'org.kuali.rice.core.web.cache.CacheAdminController',1,sys_guid())
/


insert into krim_role_perm_t
(role_perm_id, role_id, perm_id, actv_ind, ver_nbr, obj_id)
values ((select (max(to_number(role_perm_id)) + 1) from krim_role_perm_t where role_perm_id is not NULL and to_number(role_perm_id) < 10000),
        (select role_id from krim_role_t where role_nm = 'Technical Administrator' and nmspc_cd = 'KR-SYS'),
        (select perm_id from krim_perm_t where nm = 'Use Cache Adminstration Screen' and nmspc_cd = 'KR-SYS'),
        'Y', 1, sys_guid())
/





-- 
-- 2011-11-03.sql
-- 


-- Make typ_id column optional where appropriate

ALTER TABLE krms_rule_t MODIFY typ_id varchar2(40) null
/
ALTER TABLE krms_agenda_t MODIFY typ_id varchar2(40) null
/
ALTER TABLE krms_cntxt_t MODIFY typ_id varchar2(40) null
/





-- 
-- 2011-11-14.sql
-- 


-- Drop foreign key constraint on krms_prop_t table
alter table KRMS_PROP_T drop constraint KRMS_PROP_FK1
/





-- 
-- 2011-11-23.sql
-- 


-- give PeopleFlows friendlier names

update krms_typ_t set nm='Notify PeopleFlow' where typ_id = '1000'
/
update krms_typ_t set nm='Route to PeopleFlow' where typ_id = '1001'
/

-- remove constraint that is preventing compound props from persisting

alter table krms_cmpnd_prop_props_t modify seq_no NUMBER(5) null
/





-- 
-- 2011-11-28.sql
-- 


update krim_perm_t
   set nmspc_cd = 'KRMS_TEST'
 where nm = 'Maintain KRMS Agenda'
   and nmspc_cd = 'KR-RULE'
/

delete from krim_perm_attr_data_t
 where perm_id = (select perm_id from krim_perm_t where nm = 'Maintain KRMS Agenda' and nmspc_cd = 'KRMS_TEST')
/





-- 
-- 2011-11-29.sql
-- 


alter table KREW_RTE_NODE_T modify ACTVN_TYP varchar(1)
/





-- 
-- 2011-12-07.sql
-- 



-- correct fields in krms test data
update krms_prop_t set cmpnd_op_cd = '&' where cmpnd_op_cd = 'a'
/
update krms_cmpnd_prop_props_t set seq_no = '2' where prop_id = 'P421C'
/
update krms_cmpnd_prop_props_t set seq_no = '3' where prop_id = 'P421D'
/
update krms_cmpnd_prop_props_t set seq_no = '3' where prop_id = 'P502C'
/

-- move seq_no column from krms_cmpnd_prop_props_t pivot table to krms_prop_t table.
alter table krms_prop_t add (cmpnd_seq_no decimal(5,0) default null)
/

update krms_prop_t set krms_prop_t.cmpnd_seq_no = (select seq_no from krms_cmpnd_prop_props_t where prop_id = krms_prop_t.prop_id)
/

alter table krms_cmpnd_prop_props_t drop (seq_no)
/





-- 
-- 2011-12-21.sql
-- 


UPDATE KRCR_NMSPC_T SET APPL_ID = 'RICE' WHERE NMSPC_CD = 'KUALI'
/




-- 
-- 2012-01-03.sql
-- 


INSERT INTO KRCR_NMSPC_T VALUES ('KR-KRAD', sys_guid(), 1, 'Kuali Rapid Application Development', 'Y', 'RICE')
/
INSERT INTO KRIM_ATTR_DEFN_T VALUES ((select (max(to_number(KIM_ATTR_DEFN_ID)) + 1) from KRIM_ATTR_DEFN_T where KIM_ATTR_DEFN_ID is not NULL and to_number(KIM_ATTR_DEFN_ID) < 10000), sys_guid(), 1, 'viewId', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes')
/
INSERT INTO KRIM_ATTR_DEFN_T VALUES ((select (max(to_number(KIM_ATTR_DEFN_ID)) + 1) from KRIM_ATTR_DEFN_T where KIM_ATTR_DEFN_ID is not NULL and to_number(KIM_ATTR_DEFN_ID) < 10000), sys_guid(), 1, 'actionEvent', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes')
/
INSERT INTO KRIM_ATTR_DEFN_T VALUES ((select (max(to_number(KIM_ATTR_DEFN_ID)) + 1) from KRIM_ATTR_DEFN_T where KIM_ATTR_DEFN_ID is not NULL and to_number(KIM_ATTR_DEFN_ID) < 10000), sys_guid(), 1, 'collectionPropertyName', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes')
/
INSERT INTO KRIM_ATTR_DEFN_T VALUES ((select (max(to_number(KIM_ATTR_DEFN_ID)) + 1) from KRIM_ATTR_DEFN_T where KIM_ATTR_DEFN_ID is not NULL and to_number(KIM_ATTR_DEFN_ID) < 10000), sys_guid(), 1, 'fieldId', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes')
/
INSERT INTO KRIM_ATTR_DEFN_T VALUES ((select (max(to_number(KIM_ATTR_DEFN_ID)) + 1) from KRIM_ATTR_DEFN_T where KIM_ATTR_DEFN_ID is not NULL and to_number(KIM_ATTR_DEFN_ID) < 10000), sys_guid(), 1, 'groupId', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes')
/
INSERT INTO KRIM_ATTR_DEFN_T VALUES ((select (max(to_number(KIM_ATTR_DEFN_ID)) + 1) from KRIM_ATTR_DEFN_T where KIM_ATTR_DEFN_ID is not NULL and to_number(KIM_ATTR_DEFN_ID) < 10000), sys_guid(), 1, 'widgetId', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes')
/
INSERT INTO KRIM_ATTR_DEFN_T VALUES ((select (max(to_number(KIM_ATTR_DEFN_ID)) + 1) from KRIM_ATTR_DEFN_T where KIM_ATTR_DEFN_ID is not NULL and to_number(KIM_ATTR_DEFN_ID) < 10000), sys_guid(), 1, 'actionId', null, 'Y', 'KR-KRAD', 'org.kuali.rice.kim.bo.impl.KimAttributes')
/
INSERT INTO KRIM_TYP_T VALUES ((select (max(to_number(kim_typ_id)) + 1) from KRIM_TYP_T where kim_typ_id is not NULL and to_number(kim_typ_id) < 10000), sys_guid(), 1, 'View', 'viewPermissionTypeService', 'Y', 'KR-KRAD')
/
INSERT INTO KRIM_TYP_T VALUES ((select (max(to_number(kim_typ_id)) + 1) from KRIM_TYP_T where kim_typ_id is not NULL and to_number(kim_typ_id) < 10000), sys_guid(), 1, 'View Edit Mode', 'viewEditModePermissionTypeService', 'Y', 'KR-KRAD')
/
INSERT INTO KRIM_TYP_T VALUES ((select (max(to_number(kim_typ_id)) + 1) from KRIM_TYP_T where kim_typ_id is not NULL and to_number(kim_typ_id) < 10000), sys_guid(), 1, 'View Field', 'viewFieldPermissionTypeService', 'Y', 'KR-KRAD')
/
INSERT INTO KRIM_TYP_T VALUES ((select (max(to_number(kim_typ_id)) + 1) from KRIM_TYP_T where kim_typ_id is not NULL and to_number(kim_typ_id) < 10000), sys_guid(), 1, 'View Group', 'viewGroupPermissionTypeService', 'Y', 'KR-KRAD')
/
INSERT INTO KRIM_TYP_T VALUES ((select (max(to_number(kim_typ_id)) + 1) from KRIM_TYP_T where kim_typ_id is not NULL and to_number(kim_typ_id) < 10000), sys_guid(), 1, 'View Widget', 'viewWidgetPermissionTypeService', 'Y', 'KR-KRAD')
/
INSERT INTO KRIM_TYP_T VALUES ((select (max(to_number(kim_typ_id)) + 1) from KRIM_TYP_T where kim_typ_id is not NULL and to_number(kim_typ_id) < 10000), sys_guid(), 1, 'View Action', 'viewActionPermissionTypeService', 'Y', 'KR-KRAD')
/
INSERT INTO KRIM_TYP_T VALUES ((select (max(to_number(kim_typ_id)) + 1) from KRIM_TYP_T where kim_typ_id is not NULL and to_number(kim_typ_id) < 10000), sys_guid(), 1, 'View Line Field', 'viewLineFieldPermissionTypeService', 'Y', 'KR-KRAD')
/
INSERT INTO KRIM_TYP_T VALUES ((select (max(to_number(kim_typ_id)) + 1) from KRIM_TYP_T where kim_typ_id is not NULL and to_number(kim_typ_id) < 10000), sys_guid(), 1, 'View Line Action', 'viewLineActionPermissionTypeService', 'Y', 'KR-KRAD')
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'a', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'a', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Edit Mode'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'b', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Edit Mode'), '10', 'Y');
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'a', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Field'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'b', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Field'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='fieldId'), 'Y');
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'c', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Field'), '6', 'Y');
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'a', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Group'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'b', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Group'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='groupId'), 'Y');
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'c', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Group'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='fieldId'), 'Y');
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'a', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Widget'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'b', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Widget'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='widgetId'), 'Y');
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'a', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'b', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='actionId'), 'Y');
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'c', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='actionEvent'), 'Y');
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'a', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Field'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'b', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Field'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='groupId'), 'Y');
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'c', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Field'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='collectionPropertyName'), 'Y');
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'd', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Field'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='fieldId'), 'Y');
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'e', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Field'), '6', 'Y');
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'a', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='viewId'), 'Y');
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'b', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='groupId'), 'Y');
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'c', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='collectionPropertyName'), 'Y');
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'd', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='actionId'), 'Y');
/
INSERT INTO KRIM_TYP_ATTR_T VALUES ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from KRIM_TYP_ATTR_T where KIM_TYP_ATTR_ID is not NULL and to_number(KIM_TYP_ATTR_ID) < 10000), sys_guid(), 1, 'e', (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Action'), (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T where NMSPC_CD='KR-KRAD' and NM='actionEvent'), 'Y');
/
INSERT INTO KRIM_PERM_TMPL_T VALUES ((select (max(to_number(perm_tmpl_id)) + 1) from krim_perm_tmpl_t where perm_tmpl_id is not NULL and to_number(perm_tmpl_id) < 10000), sys_guid(), 1, 'KR-KRAD', 'Open View', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View'), 'Y')
/
INSERT INTO KRIM_PERM_TMPL_T VALUES ((select (max(to_number(perm_tmpl_id)) + 1) from krim_perm_tmpl_t where perm_tmpl_id is not NULL and to_number(perm_tmpl_id) < 10000), sys_guid(), 1, 'KR-KRAD', 'Edit View', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View'), 'Y')
/
INSERT INTO KRIM_PERM_TMPL_T VALUES ((select (max(to_number(perm_tmpl_id)) + 1) from krim_perm_tmpl_t where perm_tmpl_id is not NULL and to_number(perm_tmpl_id) < 10000), sys_guid(), 1, 'KR-KRAD', 'Use View', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Edit Mode'), 'Y')
/
INSERT INTO KRIM_PERM_TMPL_T VALUES ((select (max(to_number(perm_tmpl_id)) + 1) from krim_perm_tmpl_t where perm_tmpl_id is not NULL and to_number(perm_tmpl_id) < 10000), sys_guid(), 1, 'KR-KRAD', 'View Field', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Field'), 'Y')
/
INSERT INTO KRIM_PERM_TMPL_T VALUES ((select (max(to_number(perm_tmpl_id)) + 1) from krim_perm_tmpl_t where perm_tmpl_id is not NULL and to_number(perm_tmpl_id) < 10000), sys_guid(), 1, 'KR-KRAD', 'Edit Field', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Field'), 'Y')
/
INSERT INTO KRIM_PERM_TMPL_T VALUES ((select (max(to_number(perm_tmpl_id)) + 1) from krim_perm_tmpl_t where perm_tmpl_id is not NULL and to_number(perm_tmpl_id) < 10000), sys_guid(), 1, 'KR-KRAD', 'View Group', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Group'), 'Y')
/
INSERT INTO KRIM_PERM_TMPL_T VALUES ((select (max(to_number(perm_tmpl_id)) + 1) from krim_perm_tmpl_t where perm_tmpl_id is not NULL and to_number(perm_tmpl_id) < 10000), sys_guid(), 1, 'KR-KRAD', 'Edit Group', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Group'), 'Y')
/
INSERT INTO KRIM_PERM_TMPL_T VALUES ((select (max(to_number(perm_tmpl_id)) + 1) from krim_perm_tmpl_t where perm_tmpl_id is not NULL and to_number(perm_tmpl_id) < 10000), sys_guid(), 1, 'KR-KRAD', 'View Widget', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Widget'), 'Y')
/
INSERT INTO KRIM_PERM_TMPL_T VALUES ((select (max(to_number(perm_tmpl_id)) + 1) from krim_perm_tmpl_t where perm_tmpl_id is not NULL and to_number(perm_tmpl_id) < 10000), sys_guid(), 1, 'KR-KRAD', 'Edit Widget', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Widget'), 'Y')
/
INSERT INTO KRIM_PERM_TMPL_T VALUES ((select (max(to_number(perm_tmpl_id)) + 1) from krim_perm_tmpl_t where perm_tmpl_id is not NULL and to_number(perm_tmpl_id) < 10000), sys_guid(), 1, 'KR-KRAD', 'Perform Action', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Action'), 'Y')
/
INSERT INTO KRIM_PERM_TMPL_T VALUES ((select (max(to_number(perm_tmpl_id)) + 1) from krim_perm_tmpl_t where perm_tmpl_id is not NULL and to_number(perm_tmpl_id) < 10000), sys_guid(), 1, 'KR-KRAD', 'View Line', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Group'), 'Y')
/
INSERT INTO KRIM_PERM_TMPL_T VALUES ((select (max(to_number(perm_tmpl_id)) + 1) from krim_perm_tmpl_t where perm_tmpl_id is not NULL and to_number(perm_tmpl_id) < 10000), sys_guid(), 1, 'KR-KRAD', 'Edit Line', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Group'), 'Y')
/
INSERT INTO KRIM_PERM_TMPL_T VALUES ((select (max(to_number(perm_tmpl_id)) + 1) from krim_perm_tmpl_t where perm_tmpl_id is not NULL and to_number(perm_tmpl_id) < 10000), sys_guid(), 1, 'KR-KRAD', 'View Line Field', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Field'), 'Y')
/
INSERT INTO KRIM_PERM_TMPL_T VALUES ((select (max(to_number(perm_tmpl_id)) + 1) from krim_perm_tmpl_t where perm_tmpl_id is not NULL and to_number(perm_tmpl_id) < 10000), sys_guid(), 1, 'KR-KRAD', 'Edit Line Field', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Field'), 'Y')
/
INSERT INTO KRIM_PERM_TMPL_T VALUES ((select (max(to_number(perm_tmpl_id)) + 1) from krim_perm_tmpl_t where perm_tmpl_id is not NULL and to_number(perm_tmpl_id) < 10000), sys_guid(), 1, 'KR-KRAD', 'Perform Line Action', null, (select kim_typ_id from krim_typ_t where NMSPC_CD='KR-KRAD' and NM='View Line Action'), 'Y')
/





-- 
-- 2012-01-04.sql
-- 


update KRMS_TERM_SPEC_T set TYP = 'java.lang.String' where TYP = 'T2'
/
	
update KRMS_TERM_SPEC_T set TYP = 'java.lang.String' where TYP = 'T1'
/

update KRMS_TERM_SPEC_T set TYP = 'java.lang.Integer' where TYP = 'T6'
/





-- 
-- 2012-01-05.sql
-- 


ALTER TABLE KRMS_CNTXT_VLD_ACTN_T RENAME CONSTRAINT KRMS_CNTXT_VLD_ACTN_FK1 TO KRMS_CNTXT_VLD_ACTN_TYP_FK1
/
ALTER INDEX KRMS_CNTXT_VLD_ACTN_TI1 RENAME TO KRMS_CNTXT_VLD_ACTN_TYP_TI1
/
RENAME KRMS_CNTXT_VLD_ACTN_S TO KRMS_CNTXT_VLD_ACTN_TYP_S
/
ALTER TABLE KRMS_CNTXT_VLD_ACTN_T RENAME TO KRMS_CNTXT_VLD_ACTN_TYP_T
/

ALTER TABLE KRMS_CNTXT_VLD_AGENDA_T RENAME CONSTRAINT KRMS_CNTXT_VLD_AGENDA_FK1 TO KRMS_CNTXT_VLD_AGENDA_TYP_FK1
/
ALTER INDEX KRMS_CNTXT_VLD_AGENDA_TI1 RENAME TO KRMS_CNTXT_VLD_AGENDA_TYP_TI1
/
RENAME KRMS_CNTXT_VLD_AGENDA_S TO KRMS_CNTXT_VLD_AGENDA_TYP_S
/
ALTER TABLE KRMS_CNTXT_VLD_AGENDA_T RENAME TO KRMS_CNTXT_VLD_AGENDA_TYP_T
/

ALTER TABLE KRMS_CNTXT_VLD_RULE_T RENAME CONSTRAINT KRMS_CNTXT_VLD_RULE_FK1 TO KRMS_CNTXT_VLD_RULE_TYP_FK1
/
ALTER INDEX KRMS_CNTXT_VLD_RULE_TI1 RENAME TO KRMS_CNTXT_VLD_RULE_TYP_TI1
/
RENAME KRMS_CNTXT_VLD_RULE_S TO KRMS_CNTXT_VLD_RULE_TYP_S
/
ALTER TABLE KRMS_CNTXT_VLD_RULE_T RENAME TO KRMS_CNTXT_VLD_RULE_TYP_T
/

ALTER TABLE KRMS_CNTXT_VLD_RULE_TYP_T RENAME COLUMN RULE_ID TO RULE_TYP_ID
/
UPDATE KRMS_AGENDA_T SET TYP_ID = NULL WHERE TYP_ID = 'T5'
/
UPDATE KRMS_RULE_T SET TYP_ID = NULL
/
DELETE FROM KRMS_CNTXT_VLD_RULE_TYP_T
/





-- 
-- 2012-01-06.sql
-- 


-- KULRICE-6299: New DB index to improve action list performance
create index KREW_ACTN_ITM_TI6 on KREW_ACTN_ITM_T (DLGN_TYP, DLGN_PRNCPL_ID, DLGN_GRP_ID)
/





-- 
-- 2012-01-11.sql
-- 


-- KULRICE-6452

drop table krms_cntxt_vld_event_t
/
drop sequence krms_cntxt_vld_event_s
/
rename krms_cntxt_term_spec_prereq_s to krms_cntxt_vld_term_spec_s
/





-- 
-- 2012-01-18.sql
-- 


alter table KREW_DOC_HDR_T drop column RTE_LVL_MDFN_DT
/





-- 
-- 2012-01-19b.sql
-- 



--
-- KULRICE-5856: KRNS_DOC_HDR_T.FDOC_DESC column is only 40 char
--

ALTER TABLE KRNS_DOC_HDR_T MODIFY (FDOC_DESC varchar2(255))
/

--
-- KULRICE-6463: New DB Indexes for KIM Permission checks
--

CREATE INDEX KRIM_ROLE_PERM_TI2 ON KRIM_ROLE_PERM_T (PERM_ID, ACTV_IND)
/
CREATE INDEX KRIM_PERM_TI1 ON KRIM_PERM_T (PERM_TMPL_ID)
/
CREATE INDEX KRIM_PERM_TI2 ON KRIM_PERM_T (PERM_TMPL_ID, ACTV_IND)
/
CREATE INDEX KRIM_PERM_TMPL_TI1 ON KRIM_PERM_TMPL_T (NMSPC_CD, NM)
/
CREATE INDEX KRIM_ROLE_MBR_TI2 ON KRIM_ROLE_MBR_T (role_id, mbr_id, mbr_typ_cd)
/
CREATE INDEX KRIM_ROLE_MBR_TI3 ON KRIM_ROLE_MBR_T (mbr_id, mbr_typ_cd)
/

--
-- KRMS Sample (and production) Data
--

---- If you should want to clean out your KRMS tables:
delete from  krms_cntxt_vld_rule_typ_t
/
delete from  krms_cntxt_vld_func_t
/
delete from  krms_term_spec_ctgry_t
/
delete from  krms_func_ctgry_t
/
delete from  krms_ctgry_t
/
delete from  krms_func_parm_t
/
delete from  krms_func_t
/
delete from  krms_term_parm_t
/
delete from  krms_term_rslvr_parm_spec_t
/
delete from  krms_term_t
/
delete from  krms_cntxt_vld_term_spec_t
/
delete from  krms_term_rslvr_input_spec_t
/
delete from  krms_term_rslvr_attr_t
/
delete from  krms_term_rslvr_t
/
delete from  krms_term_spec_t
/
delete from  krms_prop_parm_t
/
delete from  krms_cmpnd_prop_props_t
/
delete from  krms_agenda_attr_t
/
delete from  krms_cntxt_vld_actn_typ_t
/
delete from  krms_cntxt_vld_agenda_typ_t
/
delete from  krms_cntxt_attr_t
/
delete from  krms_rule_attr_t
/
update krms_agenda_itm_t set when_true=null
/
update krms_agenda_itm_t set when_false=null
/
update krms_agenda_itm_t set always=null
/
delete from  krms_agenda_itm_t
/
delete from  krms_actn_attr_t
/
delete from  krms_actn_t
/
delete from  krms_typ_attr_t
/
delete from  krms_attr_defn_t
/
delete from  krms_agenda_t
/
update krms_rule_t set prop_id=null
/
delete from  krms_prop_t
/
delete from  krms_rule_t
/
delete from  krms_typ_t
/
delete from  krms_cntxt_t
/
delete from krcr_nmspc_t where obj_id = '5a83c912-94b9-4b4d-ac3f-88c53380a4a3'
/
---- KRMS test namespace

insert into krcr_nmspc_t (nmspc_cd, obj_id, nm, appl_id) 
values ('KR-RULE-TEST', '5a83c912-94b9-4b4d-ac3f-88c53380a4a3', 'Kuali Rules Test', 'RICE')
/

-- misc category
insert into krms_ctgry_t (ctgry_id, nm, nmspc_cd) values ('T1000', 'misc', 'KR-RULE-TEST')
/

--
-- TermResolver taking 1 campus code parameter
--

insert into krms_term_spec_t
(term_spec_id, nmspc_cd, nm, typ, desc_txt, actv, ver_nbr)
values ('T1000', 'KR-RULE-TEST', 'campusSize', 'java.lang.Integer', 'Size in # of students of the campus', 'Y', 1)
/

insert into krms_typ_t
(typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr)
values ('T1000', 'TermResolver', 'KR-RULE-TEST', null, 'Y', 1)
/

insert into krms_term_rslvr_t
(term_rslvr_id, nmspc_cd, nm, typ_id, output_term_spec_id, actv, ver_nbr)
values ('T1000', 'KR-RULE-TEST', 'campusSizeResolver', 'T1000','T1000', 'Y', 1)
/

insert into krms_term_rslvr_parm_spec_t
(term_rslvr_parm_spec_id, term_rslvr_id, nm, ver_nbr)
values ('T1000', 'T1000', 'Campus Code', 1)
/





insert into krms_term_spec_t
(term_spec_id, nmspc_cd, nm, typ, desc_txt, actv, ver_nbr)
values ('T1001', 'KR-RULE-TEST', 'orgMember', 'java.lang.Boolean', 'is the principal in the organization', 'Y', 1)
/

insert into krms_term_rslvr_t
(term_rslvr_id, nmspc_cd, nm, typ_id, output_term_spec_id, actv, ver_nbr)
values ('T1001', 'KR-RULE-TEST', 'orgMemberResolver', 'T1000','T1001', 'Y', 1)
/

insert into krms_term_rslvr_parm_spec_t
(term_rslvr_parm_spec_id, term_rslvr_id, nm, ver_nbr)
values ('T1001', 'T1001', 'Org Code', 1)
/

insert into krms_term_rslvr_parm_spec_t
(term_rslvr_parm_spec_id, term_rslvr_id, nm, ver_nbr)
values ('T1002', 'T1001', 'Principal ID', 1)
/




insert into krms_term_t
(term_id, term_spec_id, desc_txt, ver_nbr)
values ('T1000', 'T1000', 'Bloomington Campus Size', 1)
/
insert into krms_term_parm_t
(term_parm_id, term_id, nm, val, ver_nbr)
values ('T1000', 'T1000', 'Campus Code', 'BL', 1)
/







insert into krms_attr_defn_t
(attr_defn_id, nm, nmspc_cd, lbl, actv, ver_nbr)
values('T1000', 'Context1Qualifier', 'KR-RULE-TEST', 'Context 1 Qualifier', 'Y', 1)
/

insert into krms_attr_defn_t
(attr_defn_id, nm, nmspc_cd, lbl, actv, ver_nbr)
values('T1001', 'Event', 'KR-RULE-TEST', 'Event Name', 'Y', 1)
/

insert into krms_typ_t
(typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr)
values ('T1001', 'CAMPUS', 'KR-RULE-TEST', 'myCampusService', 'Y', 1)
/

insert into krms_typ_t
(typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr)
values ('T1002', 'KrmsActionResolverType', 'KR-RULE-TEST', 'testActionTypeService', 'Y', 1)
/

insert into krms_typ_t
(typ_id, nm, nmspc_cd, actv, ver_nbr)
values ('T1003', 'CONTEXT', 'KR-RULE-TEST',  'Y', 1)
/

insert into krms_typ_attr_t
(typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr)
values ('T1000', 1, 'T1003', 'T1000', 'Y', 1)
/

insert into krms_typ_t
(typ_id, nm, nmspc_cd, actv, ver_nbr)
values ('T1004', 'AGENDA', 'KR-RULE-TEST',  'Y', 1)
/


insert into krms_typ_t
(typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr)
values ('T1005', 'Campus Agenda', 'KR-RULE-TEST', 'campusAgendaTypeService', 'Y', 1)
/


insert into krms_cntxt_t
(cntxt_id, nmspc_cd, nm, typ_id, actv, ver_nbr)
values ('CONTEXT1','KR-RULE-TEST', 'Context1', 'T1003', 'Y', 1)
/

insert into krms_cntxt_t
(cntxt_id, nmspc_cd, nm, typ_id, actv, ver_nbr)
values ('CONTEXT_NO_PERMISSION','KRMS_TEST_VOID', 'Context with no premissions', 'T1003', 'Y', 1)
/

insert into krms_cntxt_attr_t
(cntxt_attr_id, cntxt_id, attr_val, attr_defn_id, ver_nbr)
values('T1000', 'CONTEXT1', 'BLAH', 'T1000', 1)
/

insert into krms_cntxt_vld_actn_typ_t
(cntxt_vld_actn_id, cntxt_id, actn_typ_id, ver_nbr)
values ('T1000', 'CONTEXT1', 'T1002', 1)
/

insert into krms_cntxt_vld_actn_typ_t
(cntxt_vld_actn_id, cntxt_id, actn_typ_id, ver_nbr)
values ('T1001', 'CONTEXT1', '1000', 1)
/

insert into krms_cntxt_vld_actn_typ_t
(cntxt_vld_actn_id, cntxt_id, actn_typ_id, ver_nbr)
values ('T1002', 'CONTEXT1', '1001', 1)
/

insert into krms_rule_t
(rule_id, nmspc_cd, nm, typ_id, prop_id, actv, ver_nbr, desc_txt)
values ('T1000', 'KR-RULE-TEST', 'Rule1', null, null, 'Y', 1, 'stub rule lorem ipsum')
/

insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1000', 'is campus bloomington', null, 'S','T1000',1)
/

update krms_rule_t
set prop_id = 'T1000' where rule_id = 'T1000'
/

insert into krms_term_spec_t
(term_spec_id, nm, nmspc_cd,  typ, actv, ver_nbr)
values ('T1002', 'Campus Code', 'KR-RULE-TEST', 'java.lang.String', 'Y', 1)
/

insert into krms_term_t
(term_id, term_spec_id, desc_txt, ver_nbr)
values ('T1002', 'T1002', null, 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1000', 'T1000', 'T1002', 'T', 1, 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1001', 'T1000', 'BL', 'C', 2, 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1002', 'T1000', '=', 'O', 3, 1)
/

insert into krms_actn_t
(actn_id, nm, nmspc_cd, desc_txt, typ_id, rule_id, seq_no, ver_nbr)
values ( 'T1000', 'testAction', 'KR-RULE-TEST', 'Action Stub for Testing', 'T1002', 'T1000', 1, 1)
/

insert into krms_agenda_t
(agenda_id, nm, cntxt_id, init_agenda_itm_id, typ_id, actv, ver_nbr)
values ( 'T1000', 'My Fabulous Agenda', 'CONTEXT1', null, 'T1005', 'Y', 1)
/

insert into krms_agenda_itm_t
(agenda_itm_id, rule_id, agenda_id, ver_nbr)
VALUES('T1000', 'T1000', 'T1000', 1)
/

update krms_agenda_t set INIT_AGENDA_ITM_ID = 'T1000' where agenda_id = 'T1000'
/


insert into krms_term_spec_t
(term_spec_id, nmspc_cd, nm, typ, actv, ver_nbr)
values ('T1008', 'KR-RULE-TEST', 'campusCode', 'java.lang.String', 'Y', 1)
/

-- next item

insert into krms_rule_t
(rule_id, nmspc_cd, nm, typ_id, prop_id, actv, ver_nbr, desc_txt)
values ('T1001', 'KR-RULE-TEST', 'Rule2', null, null, 'Y', 1, 'Frog specimens bogus rule foo')
/

insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1001', 'is campus bloomington', null, 'S','T1001',1)
/

update krms_rule_t
set prop_id = 'T1001' where rule_id = 'T1001'
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1003', 'T1001', 'T1002', 'T', 1, 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1004', 'T1001', 'BL', 'C', 2, 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1005', 'T1001', '=', 'O', 3, 1)
/

insert into krms_agenda_itm_t
(agenda_itm_id, rule_id, agenda_id, ver_nbr)
VALUES('T1001', 'T1001', 'T1000', 1)
/

update krms_agenda_itm_t
SET when_true = 'T1001' WHERE agenda_itm_id = 'T1000'
/

-- next item

insert into krms_rule_t
(rule_id, nmspc_cd, nm, typ_id, prop_id, actv, ver_nbr, desc_txt)
values ('T1002', 'KR-RULE-TEST', 'Rule3', null, null, 'Y', 1, 'Bloomington campus code rule')
/

insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1002', 'is campus bloomington', null, 'S','T1002',1)
/

update krms_rule_t
set prop_id = 'T1002' where rule_id = 'T1002'
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1006', 'T1002', 'T1002', 'T', 1, 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1007', 'T1002', 'BL', 'C', 2, 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1008', 'T1002', '=', 'O', 3, 1)
/

insert into krms_agenda_itm_t
(agenda_itm_id, rule_id, agenda_id, ver_nbr)
VALUES('T1002', 'T1002', 'T1000', 1)
/
--
update krms_agenda_itm_t
SET always = 'T1002' WHERE agenda_itm_id = 'T1001'
/

-- next item

insert into krms_rule_t
(rule_id, nmspc_cd, nm, typ_id, prop_id, actv, ver_nbr, desc_txt)
values ('T1003', 'KR-RULE-TEST', 'Rule4', null, null, 'Y', 1, 'check for possible BBQ ingiter hazard')
/

insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1003', 'is campus bloomington', null, 'S','T1003',1)
/

update krms_rule_t
set prop_id = 'T1003' where rule_id = 'T1003'
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1009', 'T1003', 'T1002', 'T', 1, 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1010', 'T1003', 'BL', 'C', 2, 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1011', 'T1003', '=', 'O', 3, 1)
/

insert into krms_agenda_itm_t
(agenda_itm_id, rule_id, agenda_id, ver_nbr)
VALUES('T1003', 'T1003', 'T1000', 1)
/
--
update krms_agenda_itm_t
SET always = 'T1003' WHERE agenda_itm_id = 'T1002'
/

-- next item

insert into krms_rule_t
(rule_id, nmspc_cd, nm, typ_id, prop_id, actv, ver_nbr, desc_txt)
values ('T1004', 'KR-RULE-TEST', 'Rule5', null, null, 'Y', 1, 'remembered to wear socks')
/

insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1004', 'is campus bloomington', null, 'S','T1004',1)
/

update krms_rule_t
set prop_id = 'T1004' where rule_id = 'T1004'
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1012', 'T1004', 'T1002', 'T', 1, 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1013', 'T1004', 'BL', 'C', 2, 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1014', 'T1004', '=', 'O', 3, 1)
/

insert into krms_agenda_itm_t
(agenda_itm_id, rule_id, agenda_id, ver_nbr)
VALUES('T1004', 'T1004', 'T1000', 1)
/

update krms_agenda_itm_t
SET when_false = 'T1004' WHERE agenda_itm_id = 'T1000'
/

-- next item

insert into krms_rule_t
(rule_id, nmspc_cd, nm, typ_id, prop_id, actv, ver_nbr, desc_txt)
values ('T1005', 'KR-RULE-TEST', 'Rule6', null, null, 'Y', 1, 'good behavior at carnival')
/

insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1005', 'is campus bloomington', null, 'S','T1005',1)
/

update krms_rule_t
set prop_id = 'T1005' where rule_id = 'T1005'
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1015', 'T1005', 'T1002', 'T', 1, 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1016', 'T1005', 'BL', 'C', 2, 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1017', 'T1005', '=', 'O', 3, 1)
/

insert into krms_agenda_itm_t
(agenda_itm_id, rule_id, agenda_id, ver_nbr)
VALUES('T1005', 'T1005', 'T1000', 1)
/
--
update krms_agenda_itm_t
SET always = 'T1005' WHERE agenda_itm_id = 'T1000'
/



--
-- next item
--

insert into krms_rule_t
(rule_id, nmspc_cd, nm, typ_id, prop_id, actv, ver_nbr, desc_txt)
values ('T1006', 'KR-RULE-TEST', 'Rule7', null, null, 'Y', 1, 'is KRMS in da haus')
/

insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1006', 'is campus bloomington', null, 'S','T1006',1)
/

update krms_rule_t
set prop_id = 'T1006' where rule_id = 'T1006'
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1018', 'T1006', 'T1002', 'T', 1, 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1019', 'T1006', 'BL', 'C', 2, 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1020', 'T1006', '=', 'O', 3, 1)
/

insert into krms_agenda_itm_t
(agenda_itm_id, rule_id, agenda_id, ver_nbr)
VALUES('T1006', 'T1006', 'T1000', 1)
/
--
update krms_agenda_itm_t
SET when_false = 'T1006' WHERE agenda_itm_id = 'T1002'
/

--
-- rule with a compound proposition
--
insert into krms_rule_t
(rule_id, nmspc_cd, nm, typ_id, prop_id, actv, ver_nbr, desc_txt)
values ('T1007', 'KR-RULE-TEST', 'CmpdTestRule', null, null, 'Y', 1, 'For testing compound props')
/

insert into krms_prop_t
(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, cmpnd_op_cd, ver_nbr)
values ('T1007', 'a compound prop', null, 'C','T1007', '&', 1)
/

update krms_rule_t
set prop_id = 'T1007' where rule_id = 'T1007'
/

insert into krms_term_spec_t
(term_spec_id, nmspc_cd, nm, typ, actv, ver_nbr)
values ('T1003', 'KR-RULE-TEST', 'bogusFundTermSpec', 'java.lang.String', 'Y', 1)
/

insert into krms_term_t
(term_id, term_spec_id, desc_txt, ver_nbr)
values ('T1003', 'T1003', 'Fund Name', 1)
/

-- 2nd level prop
insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1008', 'a simple child to a compound prop', null, 'S','T1007', 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1021', 'T1008', 'T1002', 'T', 1, 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1022', 'T1008', 'Muir', 'C', 2, 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1023', 'T1008', '=', 'O', 3, 1)
/

insert into krms_cmpnd_prop_props_t
(cmpnd_prop_id, prop_id)
values ('T1007', 'T1008')
/

insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, cmpnd_seq_no, ver_nbr)
values ('T1009', '2nd simple child to a compound prop ', null, 'S','T1007', 2, 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1024', 'T1009', 'T1002', 'T', 1, 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1025', 'T1009', 'Revelle', 'C', 2, 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1026', 'T1009', '=', 'O', 3, 1)
/

insert into krms_cmpnd_prop_props_t
(cmpnd_prop_id, prop_id)
values ('T1007', 'T1009')
/


insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, cmpnd_seq_no, ver_nbr)
values ('T1010', '3nd simple child to a compound prop ', null, 'S','T1007', 3, 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1027', 'T1010', 'T1002', 'T', 1, 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1028', 'T1010', 'Warren', 'C', 2, 1)
/

insert into krms_prop_parm_t
(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1029', 'T1010', '=', 'O', 3, 1)
/

insert into krms_cmpnd_prop_props_t
(cmpnd_prop_id, prop_id)
values ('T1007', 'T1010')
/


--
-- start of new agendas (AGENDA002, AGENDA003) and their associated items
--



--  AGENDA 002
insert into krms_agenda_t (agenda_id, nm, cntxt_id, init_agenda_itm_id, typ_id, actv, ver_nbr)
values ('T1001', 'SimpleAgendaCompoundProp', 'CONTEXT1', null, 'T1004', 'Y', 1)
/

insert into krms_agenda_itm_t (AGENDA_ITM_ID, RULE_ID, AGENDA_ID, VER_NBR)
values ('T1007', 'T1007', 'T1001', 1)
/
update krms_agenda_t set INIT_AGENDA_ITM_ID = 'T1007' where AGENDA_ID = 'T1001'
/
--  AGENDA 003 stuff

insert into krms_term_spec_t (TERM_SPEC_ID, NM, TYP, ACTV, VER_NBR, DESC_TXT, nmspc_cd)
values ('T1004', 'PO Value', 'java.lang.Integer', 'Y', 1, 'Purchase Order Value', 'KR-RULE-TEST')
/
insert into krms_term_spec_t (TERM_SPEC_ID, NM, TYP, ACTV, VER_NBR, DESC_TXT, nmspc_cd)
values ('T1005', 'PO Item Type', 'java.lang.String', 'Y', 1, 'Purchased Item Type', 'KR-RULE-TEST')
/
insert into krms_term_spec_t (TERM_SPEC_ID, NM, TYP, ACTV, VER_NBR, DESC_TXT, nmspc_cd)
values ('T1006', 'Account', 'java.lang.String', 'Y', 1, 'Charged To Account', 'KR-RULE-TEST')
/
insert into krms_term_spec_t (TERM_SPEC_ID, NM, TYP, ACTV, VER_NBR, DESC_TXT, nmspc_cd)
values ('T1007', 'Occasion', 'java.lang.String', 'Y', 1, 'Special Event', 'KR-RULE-TEST')
/
insert into krms_cntxt_vld_term_spec_t(cntxt_term_spec_prereq_id, cntxt_id, term_spec_id, prereq)
values ('T1000', 'CONTEXT1', 'T1002', 'N')
/
insert into krms_cntxt_vld_term_spec_t(cntxt_term_spec_prereq_id, cntxt_id, term_spec_id, prereq)
values ('T1001', 'CONTEXT1', 'T1003', 'N')
/
insert into krms_cntxt_vld_term_spec_t(cntxt_term_spec_prereq_id, cntxt_id, term_spec_id, prereq)
values ('T1002', 'CONTEXT1', 'T1004', 'N')
/
insert into krms_cntxt_vld_term_spec_t(cntxt_term_spec_prereq_id, cntxt_id, term_spec_id, prereq)
values ('T1003', 'CONTEXT1', 'T1005', 'N')
/
insert into krms_cntxt_vld_term_spec_t(cntxt_term_spec_prereq_id, cntxt_id, term_spec_id, prereq)
values ('T1004', 'CONTEXT1', 'T1006', 'N')
/
insert into krms_cntxt_vld_term_spec_t(cntxt_term_spec_prereq_id, cntxt_id, term_spec_id, prereq)
values ('T1005', 'CONTEXT1', 'T1007', 'N')
/
insert into krms_cntxt_vld_term_spec_t(cntxt_term_spec_prereq_id, cntxt_id, term_spec_id, prereq)
values ('T1006', 'CONTEXT1', 'T1000', 'N')
/
insert into krms_cntxt_vld_term_spec_t(cntxt_term_spec_prereq_id, cntxt_id, term_spec_id, prereq)
values ('T1007', 'CONTEXT1', 'T1001', 'N')
/

insert into krms_term_t(term_id,TERM_SPEC_ID, DESC_TXT, VER_NBR)values ('T1004', 'T1004', 'PO Value', 1)
/
insert into krms_term_t(term_id,TERM_SPEC_ID, DESC_TXT, VER_NBR)values ('T1005', 'T1005', 'PO Item Type', 1)
/
insert into krms_term_t(term_id,TERM_SPEC_ID, DESC_TXT, VER_NBR)values ('T1006', 'T1006', 'Account', 1)
/
insert into krms_term_t(term_id,TERM_SPEC_ID, DESC_TXT, VER_NBR)values ('T1007', 'T1007', 'Occasion', 1)
/
--
-- big fin rule
--
insert into krms_rule_t(rule_id, nmspc_cd, nm, typ_id, prop_id, actv, ver_nbr, desc_txt)
values ('T1008', 'KR-RULE-TEST', 'Going Away Party for Travis', null, null, 'Y', 1, 'Does PO require my approval')
/
insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, cmpnd_op_cd, ver_nbr)
values ('T1011', 'is purchase special', null, 'C','T1008', '&', 1)
/
update krms_rule_t set prop_id = 'T1011' where rule_id = 'T1008'
/

-- 2nd level prop

-- is it expensive
insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1012', 'is purchase order value large', null, 'S','T1008', 1)
/
insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1030', 'T1012', 'T1004', 'T', 1, 1)
/
insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1031', 'T1012', '5500.00', 'C', 2, 1)
/
insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1032', 'T1012', '>', 'O', 3, 1)
/
insert into krms_cmpnd_prop_props_t(cmpnd_prop_id, prop_id)
values ('T1011', 'T1012')
/
-- is it controlled
insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, cmpnd_op_cd, ver_nbr)
values ('T1013', 'is purchased item controlled', null, 'C','T1008', '|', 1)
/
insert into krms_cmpnd_prop_props_t(cmpnd_prop_id, prop_id)
values ('T1011', 'T1013')
/
-- is it special
insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, cmpnd_op_cd, ver_nbr)
values ('T1014', 'is it for a special event', null, 'C','T1008', '&', 1)
/
insert into krms_cmpnd_prop_props_t(cmpnd_prop_id, prop_id)
values ('T1011', 'T1014')
/
---- controlled 3rd level props -----

insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1015', 'is item purchased animal', null, 'S','T1008', 1)
/
insert into krms_cmpnd_prop_props_t(cmpnd_prop_id, prop_id)
values ('T1013', 'T1015')
/
insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1033', 'T1015', 'T1005', 'T', 1, 1)
/
insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1034', 'T1015', 'ANIMAL', 'C', 2, 1)
/
insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1035', 'T1015', '=', 'O', 3, 1)
/


insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1016', 'is purchased item radioactive', null, 'S','T1008', 1)
/
insert into krms_cmpnd_prop_props_t(cmpnd_prop_id, prop_id)
values ('T1013', 'T1016')
/
insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1036', 'T1016', 'T1005', 'T', 1, 1)
/
insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1037', 'T1016', 'RADIOACTIVE', 'C', 2, 1)
/
insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1038', 'T1016', '=', 'O', 3, 1)
/

insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, cmpnd_seq_no, ver_nbr)
values ('T1017', 'is it medicinal', null, 'S','T1008', 3, 1)
/
insert into krms_cmpnd_prop_props_t(cmpnd_prop_id, prop_id)
values ('T1013', 'T1017')
/
insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1039', 'T1017', 'T1005', 'T', 1, 1)
/
insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1040', 'T1017', 'ALCOHOL BEVERAGE', 'C', 2, 1)
/
insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1041', 'T1017', '=', 'O', 3, 1)
/

-- is it special 3rd level props

insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1018', 'charged to Kuali', null, 'S','T1008', 1)
/
insert into krms_cmpnd_prop_props_t(cmpnd_prop_id, prop_id)
values ('T1014', 'T1018')
/
insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1042', 'T1018', 'T1006', 'T', 1, 1)
/
insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1043', 'T1018', 'KUALI SLUSH FUND', 'C', 2, 1)
/
insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1044', 'T1018', '=', 'O', 3, 1)
/


insert into krms_prop_t(prop_id, desc_txt, typ_id, dscrm_typ_cd, rule_id, ver_nbr)
values ('T1019', 'Party at Travis House', null, 'S','T1008', 1)
/
insert into krms_cmpnd_prop_props_t(cmpnd_prop_id, prop_id)
values ('T1014', 'T1019')
/
insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1045', 'T1019', 'T1007', 'T', 1, 1)
/
insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1046', 'T1019', 'Christmas Party', 'C', 2, 1)
/
insert into krms_prop_parm_t(prop_parm_id, prop_id, parm_val, parm_typ_cd, seq_no, ver_nbr)
values ('T1047', 'T1019', '=', 'O', 3, 1)
/

--  AGENDA 003
insert into krms_agenda_t (agenda_id, nm, cntxt_id, init_agenda_itm_id, typ_id, actv, ver_nbr)
values ('T1002', 'One Big Rule', 'CONTEXT1', null, 'T1004', 'Y', 1)
/
insert into krms_agenda_itm_t (AGENDA_ITM_ID, RULE_ID, AGENDA_ID, VER_NBR)
values ('T1008', 'T1008', 'T1002', 1)
/
update krms_agenda_t set INIT_AGENDA_ITM_ID = 'T1008' where AGENDA_ID = 'T1002'
/


-- SQL for test CampusAgendaType:

insert into krms_cntxt_vld_agenda_typ_t
(cntxt_vld_agenda_id, cntxt_id, agenda_typ_id, ver_nbr)
values ('T1000', 'CONTEXT1', 'T1005', 1)
/

-- add a db-only attribute to CampusAgendaType
insert into krms_attr_defn_t (ATTR_DEFN_ID, NM, NMSPC_CD, LBL, CMPNT_NM, DESC_TXT)
values ('T1002', 'Optional Test Attribute', 'KR-RULE-TEST', 'label', null,
'this is an optional attribute for testing')
/
insert into krms_typ_attr_t (TYP_ATTR_ID, SEQ_NO, TYP_ID, ATTR_DEFN_ID) values ('T1001', 2, 'T1005', 'T1002')
/
-- add our campus attribute to CampusAgendaType
insert into krms_attr_defn_t (ATTR_DEFN_ID, NM, NMSPC_CD, LBL, CMPNT_NM, DESC_TXT)
values ('T1003', 'Campus', 'KR-RULE-TEST', 'campus label', null, 'the campus which this agenda is valid for')
/
insert into krms_typ_attr_t (TYP_ATTR_ID, SEQ_NO, TYP_ID, ATTR_DEFN_ID) values ('T1002', 1, 'T1005', 'T1003')
/

--
-- Copyright 2005-2012 The Kuali Foundation
--
--

-- Notification PeopleFlowActionType

insert into krms_typ_t
(typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr)
values ('1000', 'Notify PeopleFlow', 'KR-RULE', 'notificationPeopleFlowActionTypeService', 'Y', 1)
/

-- Approval PeopleFlowActionType

insert into krms_typ_t
(typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr)
values ('1001', 'Route to PeopleFlow', 'KR-RULE', 'approvalPeopleFlowActionTypeService', 'Y', 1)
/


-- add a PeopleFlow attribute to the PeopleFlow types
insert into krms_attr_defn_t (ATTR_DEFN_ID, NM, NMSPC_CD, LBL, CMPNT_NM, DESC_TXT)
values ('1000', 'peopleFlowId', 'KR-RULE', 'PeopleFlow', null,
'An identifier for a PeopleFlow')
/
insert into krms_typ_attr_t (TYP_ATTR_ID, SEQ_NO, TYP_ID, ATTR_DEFN_ID) values ('1000', 1, '1000', '1000')
/
insert into krms_typ_attr_t (TYP_ATTR_ID, SEQ_NO, TYP_ID, ATTR_DEFN_ID) values ('1001', 1, '1001', '1000')
/


-- General validation rule type
insert into krms_typ_t (typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr) values('1002', 'Validation Rule', 'KR-RULE', 'validationRuleTypeService', 'Y', 1)
/
-- General validation action type
insert into krms_typ_t (typ_id, nm, nmspc_cd, srvc_nm, actv, ver_nbr) values('1003', 'Validation Action', 'KR-RULE', 'validationActionTypeService', 'Y', 1)
/
-- Invalid rule
insert into krms_attr_defn_t (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt) values ('1001', 'Invalid Rule', 'KR-RULE', 'Invalid Rule', 'Y', null, 1, 'If true, execute the action')
/
-- Valid rule
insert into krms_attr_defn_t (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt) values ('1002', 'Valid Rule', 'KR-RULE', 'Valid Rule', 'Y', null, 1, 'If false, execute the action')
/
-- General context rule type mapping
insert into krms_cntxt_vld_rule_typ_t (cntxt_vld_rule_id, cntxt_id, rule_typ_id, ver_nbr) values ('T1000', 'CONTEXT1', '1002', 1)
/
-- The General rule subtypes attributes
-- use same sequence number to prove that falling back to natural order when sequences are the same works.
insert into krms_typ_attr_t (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr) values ('1002', 1, '1002', '1001', 'Y', 1)
/
insert into krms_typ_attr_t (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr) values ('1003', 1, '1002', '1002', 'Y', 1)
/
-- warning action
insert into krms_attr_defn_t (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt) values ('1003', 'Warning Action', 'KR-RULE', 'Warning Action', 'Y', null, 1, 'Warning')
/
-- error action
insert into krms_attr_defn_t (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt) values ('1004', 'Error Action', 'KR-RULE', 'Error Action', 'Y', null, 1, 'Error')
/
-- action message
insert into krms_attr_defn_t (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt) values ('1005', 'Action Message', 'KR-RULE', 'Action Message', 'Y', null, 1, 'Message validation action returns')
/
-- Context general validation acton type mapping
insert into krms_cntxt_vld_actn_typ_t (cntxt_vld_actn_id, cntxt_id, actn_typ_id, ver_nbr) values ('T1003', 'CONTEXT1', '1003', 1)
/
-- The General action type attribute
insert into krms_typ_attr_t (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr) values ('1004', 1, '1003', '1003', 'Y', 1)
/
insert into krms_typ_attr_t (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr) values ('1005', 2, '1003', '1004', 'Y', 1)
/
insert into krms_typ_attr_t (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr) values ('1006', 3, '1003', '1005', 'Y', 1)
/


-- change permisison to use new krms test namespace
update krim_perm_attr_data_t set attr_val = 'KR-RULE-TEST' where attr_val = 'KRMS_TEST'
/
update krim_perm_t
   set nmspc_cd = 'KR-RULE-TEST'
 where nm = 'Maintain KRMS Agenda'
   and nmspc_cd = 'KR-RULE'
/

update krim_perm_t
   set nmspc_cd = 'KR-RULE-TEST'
 where nm = 'Maintain KRMS Agenda'
   and nmspc_cd = 'KRMS_TEST'
/

delete from krim_perm_attr_data_t
 where perm_id = (select perm_id from krim_perm_t where nm = 'Maintain KRMS Agenda' and nmspc_cd = 'KRMS_TEST')
/





-- 
-- 2012-01-19c.sql
-- 


-- KULRICE-6537 - increase size of MBR_NM field
ALTER TABLE KRIM_PND_GRP_MBR_T MODIFY (MBR_NM varchar2(100))
/





-- 
-- 2012-01-19.sql
-- 


CREATE TABLE KRCR_STYLE_T
(
      STYLE_ID VARCHAR2(40)
        , NM VARCHAR2(200) NOT NULL
        , XML CLOB NOT NULL
        , ACTV_IND NUMBER(1) NOT NULL
        , VER_NBR NUMBER(8) default 0
        , OBJ_ID VARCHAR2(36) NOT NULL
    , CONSTRAINT KRCR_STYLE_TC0 UNIQUE (OBJ_ID)
)
/
INSERT INTO KRCR_STYLE_T SELECT STYLE_ID, NM, XML, ACTV_IND, VER_NBR, OBJ_ID FROM KREW_STYLE_T
/
DROP TABLE KREW_STYLE_T
/
ALTER TABLE KRCR_STYLE_T
    ADD CONSTRAINT KRCR_STYLE_TP1
PRIMARY KEY (STYLE_ID)
/


--
-- 2012-01-24.sql
--


-- unset type on My Fabulous Agenda since the required attribute isn't specified
UPDATE krms_agenda_t SET TYP_ID=null WHERE AGENDA_ID='T1000'
/

-- PeopleFlow Name
insert into krms_attr_defn_t (attr_defn_id, nm, nmspc_cd, lbl, actv, cmpnt_nm, ver_nbr, desc_txt)
values ('1006', 'peopleFlowName', 'KR_RULE', 'PeopleFlow Name', 'Y', null, 1, 'PeopleFlow Name')
/
insert into krms_typ_attr_t (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr)
values ('1007', 3, '1000', '1006', 'Y', 1)
/
insert into krms_typ_attr_t (typ_attr_id, seq_no, typ_id, attr_defn_id, actv, ver_nbr)
values ('1008', 3, '1001', '1006', 'Y', 1)
/


--
-- 2010-01-31.sql
--


-- KULRICE-6481
ALTER TABLE KRIM_ROLE_PERM_T MODIFY (ROLE_ID NOT NULL)
/
ALTER TABLE KRIM_ROLE_PERM_T MODIFY (PERM_ID NOT NULL)
/

--
-- 2012-02-03.sql
--

-- KULRICE-6630: Add SQL scripts for missing data in master database

insert into krew_typ_t values ('1', 'Sample Type', 'KR-SAP', 'sampleAppPeopleFlowTypeService', 'Y', 1)
/
insert into krew_attr_defn_t values ('1', 'number', 'KR-SAP', 'Travel Number', 'Y', 'edu.sampleu.travel.bo.TravelAccount', 1, null)
/
insert into krew_attr_defn_t values ('2', 'id', 'KR-SAP', null, 'Y', 'edu.sampleu.travel.bo.FiscalOfficer', 1, null)
/
insert into krew_typ_attr_t values ('1', 1, '1', '1', 'Y', 1)
/
insert into krew_typ_attr_t values ('2', 2, '1', '2', 'Y', 1)
/


--
-- 2012-02-08.sql
--

-- create a KIM permission for the Creating a new Term, Context and TermSpecification

insert into krim_perm_t
(perm_id, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind, ver_nbr, obj_id)
values ((select (max(to_number(perm_id)) + 1) from krim_perm_t where perm_id is not NULL and to_number(perm_id) < 10000),
        (select perm_tmpl_id from krim_perm_tmpl_t where nm = 'Create / Maintain Record(s)' and nmspc_cd = 'KR-NS'),
        'KR-NS','Create Term Maintenance Document','Allows user to create a new Term maintainence document','Y',1,
        '0dbce939-4f22-4e9b-a4bb-1615c0f411a2')
/
insert into krim_perm_attr_data_t
(attr_data_id, perm_id, kim_typ_id, kim_attr_defn_id, attr_val, ver_nbr, obj_id)
values ((select (max(to_number(attr_data_id)) + 1) from krim_perm_attr_data_t where attr_data_id is not NULL and to_number(attr_data_id) < 10000),
        (select perm_id from krim_perm_t where nm = 'Create Term Maintenance Document' and nmspc_cd = 'KR-NS'),
        (select kim_typ_id from krim_typ_t where nm = 'Document Type & Existing Records Only' and nmspc_cd = 'KR-NS'),
        (select kim_attr_defn_id from krim_attr_defn_t where nm = 'documentTypeName'),
        'TermMaintenanceDocument',1,'aa1d1400-6155-4819-8bad-e5dd81f9871b')
/
insert into krim_role_perm_t
(role_perm_id, role_id, perm_id, actv_ind, ver_nbr, obj_id)
values ((select (max(to_number(role_perm_id)) + 1) from krim_role_perm_t where role_perm_id is not NULL and to_number(role_perm_id) < 10000),
        (select role_id from krim_role_t where role_nm = 'Kuali Rules Management System Administrator' and nmspc_cd = 'KR-RULE'),
        (select perm_id from krim_perm_t where nm = 'Create Term Maintenance Document' and nmspc_cd = 'KR-NS'),
        'Y', 1, '45f8f55e-23d9-4278-ade8-ddfc870852e6')
/
insert into krim_perm_t
(perm_id, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind, ver_nbr, obj_id)
values ((select (max(to_number(perm_id)) + 1) from krim_perm_t where perm_id is not NULL and to_number(perm_id) < 10000),
        (select perm_tmpl_id from krim_perm_tmpl_t where nm = 'Create / Maintain Record(s)' and nmspc_cd = 'KR-NS'),
        'KR-NS','Create Context Maintenance Document','Allows user to create a new Context maintainence document','Y',1,
        'cefeed6d-b5e2-40aa-9034-137db317b532')
/
insert into krim_perm_attr_data_t
(attr_data_id, perm_id, kim_typ_id, kim_attr_defn_id, attr_val, ver_nbr, obj_id)
values ((select (max(to_number(attr_data_id)) + 1) from krim_perm_attr_data_t where attr_data_id is not NULL and to_number(attr_data_id) < 10000),
        (select perm_id from krim_perm_t where nm = 'Create Context Maintenance Document' and nmspc_cd = 'KR-NS'),
        (select kim_typ_id from krim_typ_t where nm = 'Document Type & Existing Records Only' and nmspc_cd = 'KR-NS'),
        (select kim_attr_defn_id from krim_attr_defn_t where nm = 'documentTypeName'),
        'ContextMaintenanceDocument',1,'c43bc7f5-949e-4a85-b173-6a53d81f2762')
/
insert into krim_role_perm_t
(role_perm_id, role_id, perm_id, actv_ind, ver_nbr, obj_id)
values ((select (max(to_number(role_perm_id)) + 1) from krim_role_perm_t where role_perm_id is not NULL and to_number(role_perm_id) < 10000),
        (select role_id from krim_role_t where role_nm = 'Kuali Rules Management System Administrator' and nmspc_cd = 'KR-RULE'),
        (select perm_id from krim_perm_t where nm = 'Create Context Maintenance Document' and nmspc_cd = 'KR-NS'),
        'Y', 1, 'cd7cbc67-c0b2-4785-afa8-8c8d073b78df')
/
insert into krim_perm_t
(perm_id, perm_tmpl_id, nmspc_cd, nm, desc_txt, actv_ind, ver_nbr, obj_id)
values ((select (max(to_number(perm_id)) + 1) from krim_perm_t where perm_id is not NULL and to_number(perm_id) < 10000),
        (select perm_tmpl_id from krim_perm_tmpl_t where nm = 'Create / Maintain Record(s)' and nmspc_cd = 'KR-NS'),
        'KR-NS','Create TermSpecification Maintenance Document','Allows user to create a new TermSpecification maintainence document','Y',1,
        '02bd9acd-48d9-4fec-acbd-6a441c5ea8c2')
/
insert into krim_perm_attr_data_t
(attr_data_id, perm_id, kim_typ_id, kim_attr_defn_id, attr_val, ver_nbr, obj_id)
values ((select (max(to_number(attr_data_id)) + 1) from krim_perm_attr_data_t where attr_data_id is not NULL and to_number(attr_data_id) < 10000),
        (select perm_id from krim_perm_t where nm = 'Create TermSpecification Maintenance Document' and nmspc_cd = 'KR-NS'),
        (select kim_typ_id from krim_typ_t where nm = 'Document Type & Existing Records Only' and nmspc_cd = 'KR-NS'),
        (select kim_attr_defn_id from krim_attr_defn_t where nm = 'documentTypeName'),
        'TermSpecificationMaintenanceDocument',1,'d3e373ca-296b-4834-bd66-ba159ebe733a')
/
insert into krim_role_perm_t
(role_perm_id, role_id, perm_id, actv_ind, ver_nbr, obj_id)
values ((select (max(to_number(role_perm_id)) + 1) from krim_role_perm_t where role_perm_id is not NULL and to_number(role_perm_id) < 10000),
        (select role_id from krim_role_t where role_nm = 'Kuali Rules Management System Administrator' and nmspc_cd = 'KR-RULE'),
        (select perm_id from krim_perm_t where nm = 'Create TermSpecification Maintenance Document' and nmspc_cd = 'KR-NS'),
        'Y', 1, '83a270a0-1cdb-4440-ab8b-41cd8afc41d9')
/


--
-- 2012-02-14.sql
--

--
-- KULRICE-6710: Drop krms_cntxt_vld_rule_t, krms_cntxt_vld_actn_t and krms_cntxt_vld_agenda_t tables
--

-- NOTE that these tables should have been renamed in the master db, but mysteriously still were present.
-- deleting here.  If you get errors that these tables and sequences don't exist, you can omit these statements
-- without concern.

drop table krms_cntxt_vld_actn_t
/
drop sequence krms_cntxt_vld_actn_s
/
drop table krms_cntxt_vld_agenda_t
/
drop sequence krms_cntxt_vld_agenda_s
/
drop table krms_cntxt_vld_rule_t
/
drop sequence krms_cntxt_vld_rule_s
/

--
-- KULRICE-6799: At one point (2.0-RC4 and before), the DOC_STAT_NM column was incorrectly set to 20 characters
--               the original script (2011-06-17.sql, and the "final" script) has been corrected, but this script has
--               been created in case that script had already been run.  Mysql scripts are unaffected by this, so there is no
--               equivalent mysql script.
--

alter table KREW_DOC_TYP_APP_DOC_STAT_T modify (DOC_STAT_NM varchar2(64))
/

--
-- 2012-02-23.sql
--

--
-- KULRICE-6811: Conversion of WorkflowFunctions to EDLFunctions in eDocLite stylesheets
--

UPDATE KRCR_STYLE_T set XML=replace(XML,'org.kuali.rice.kew.edl.WorkflowFunctions','org.kuali.rice.edl.framework.util.EDLFunctions')
/
