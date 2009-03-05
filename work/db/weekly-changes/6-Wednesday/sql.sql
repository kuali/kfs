create table krim_entity_ent_typ_t_bak as select * from krim_entity_ent_typ_t
/
alter table KRIM_ENTITY_ADDR_T DROP constraint KRIM_ENTITY_ADDR_TR1
/           
alter table KRIM_ENTITY_EMAIL_T DROP constraint KRIM_ENTITY_EMAIL_TR1
/          
alter table KRIM_ENTITY_PHONE_T DROP constraint KRIM_ENTITY_PHONE_TR1 
/
drop table krim_entity_ent_typ_t
/
CREATE TABLE krim_entity_ent_typ_t
    (ent_typ_cd                     VARCHAR2(40) NOT NULL,
    entity_id                      VARCHAR2(40) NOT NULL,
    obj_id                         VARCHAR2(36) NOT NULL,
    ver_nbr                        NUMBER(8,0) DEFAULT 1 NOT NULL,
    actv_ind                       VARCHAR2(1) DEFAULT 'Y',
    last_updt_dt                   DATE DEFAULT SYSDATE)
/
insert into krim_entity_ent_typ_t (select ent_typ_cd, entity_id, obj_id, ver_nbr, actv_ind, last_updt_dt
from krim_entity_ent_typ_t_bak)
/
COMMIT
/
ALTER TABLE krim_entity_ent_typ_t
ADD CONSTRAINT krim_entity_ent_typ_tc0 UNIQUE (obj_id)
/
ALTER TABLE krim_entity_ent_typ_t
ADD CONSTRAINT krim_entity_ent_typ_tp1 PRIMARY KEY (ent_typ_cd, entity_id)
/
ALTER TABLE krim_entity_ent_typ_t
ADD CONSTRAINT krim_entity_ent_typ_tr1 FOREIGN KEY (entity_id)
REFERENCES krim_entity_t (entity_id) ON DELETE CASCADE
/
ALTER TABLE krim_entity_ent_typ_t
ADD CONSTRAINT krim_entity_ent_typ_tr2 FOREIGN KEY (ent_typ_cd)
REFERENCES krim_ent_typ_t (ent_typ_cd)
/
ALTER TABLE krim_entity_addr_t
ADD CONSTRAINT krim_entity_addr_tr1 FOREIGN KEY (ent_typ_cd, entity_id)
REFERENCES krim_entity_ent_typ_t (ent_typ_cd,entity_id) ON DELETE CASCADE
DISABLE NOVALIDATE
/           
ALTER TABLE krim_entity_email_t
ADD CONSTRAINT krim_entity_email_tr1 FOREIGN KEY (ent_typ_cd, entity_id)
REFERENCES krim_entity_ent_typ_t (ent_typ_cd,entity_id) ON DELETE CASCADE
DISABLE NOVALIDATE
/          
ALTER TABLE krim_entity_phone_t
ADD CONSTRAINT krim_entity_phone_tr1 FOREIGN KEY (ent_typ_cd, entity_id)
REFERENCES krim_entity_ent_typ_t (ent_typ_cd,entity_id) ON DELETE CASCADE
DISABLE NOVALIDATE
/
drop table krim_entity_ent_typ_t_bak
/
update krns_parm_t
set txt = 'MF'
where nmspc_cd = 'KFS-LD'
and parm_nm = 'ORIGINATION'
and txt = 'LD'
/
CREATE TABLE gl_balance_hist_t ( 
    UNIV_FISCAL_YR        decimal(4,0) NOT NULL,
    FIN_COA_CD            varchar(2) NOT NULL,
    ACCOUNT_NBR           varchar(7) NOT NULL,
    SUB_ACCT_NBR          varchar(5) NOT NULL,
    FIN_OBJECT_CD         varchar(4) NOT NULL,
    FIN_SUB_OBJ_CD        varchar(3) NOT NULL,
    FIN_BALANCE_TYP_CD    varchar(2) NOT NULL,
    FIN_OBJ_TYP_CD        varchar(2) NOT NULL,
    ACLN_ANNL_BAL_AMT     decimal(19,2),
    FIN_BEG_BAL_LN_AMT    decimal(19,2),
    CONTR_GR_BB_AC_AMT    decimal(19,2),
    MO1_ACCT_LN_AMT       decimal(19,2),
    MO2_ACCT_LN_AMT       decimal(19,2),
    MO3_ACCT_LN_AMT       decimal(19,2),
    MO4_ACCT_LN_AMT       decimal(19,2),
    MO5_ACCT_LN_AMT       decimal(19,2),
    MO6_ACCT_LN_AMT       decimal(19,2),
    MO7_ACCT_LN_AMT       decimal(19,2),
    MO8_ACCT_LN_AMT       decimal(19,2),
    MO9_ACCT_LN_AMT       decimal(19,2),
    MO10_ACCT_LN_AMT      decimal(19,2),
    MO11_ACCT_LN_AMT      decimal(19,2),
    MO12_ACCT_LN_AMT      decimal(19,2),
    MO13_ACCT_LN_AMT      decimal(19,2),
    PRIMARY KEY(UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD)
)
/
ALTER TABLE gl_balance_hist_t
    ADD CONSTRAINT GL_BALANCE_HIST_TR6
    FOREIGN KEY(UNIV_FISCAL_YR)
    REFERENCES fs_option_t(UNIV_FISCAL_YR)
/
ALTER TABLE gl_balance_hist_t
    ADD CONSTRAINT GL_BALANCE_HIST_TR5
    FOREIGN KEY(FIN_BALANCE_TYP_CD)
    REFERENCES ca_balance_type_t(FIN_BALANCE_TYP_CD)
/
ALTER TABLE gl_balance_hist_t
    ADD CONSTRAINT GL_BALANCE_HIST_TR4
    FOREIGN KEY(FIN_OBJ_TYP_CD)
    REFERENCES ca_obj_type_t(FIN_OBJ_TYP_CD)
/
ALTER TABLE gl_balance_hist_t
    ADD CONSTRAINT GL_BALANCE_HIST_TR3
    FOREIGN KEY(FIN_COA_CD)
    REFERENCES ca_chart_t(FIN_COA_CD)
/
ALTER TABLE gl_balance_hist_t
    ADD CONSTRAINT GL_BALANCE_HIST_TR2
    FOREIGN KEY(FIN_COA_CD, ACCOUNT_NBR)
    REFERENCES ca_account_t(FIN_COA_CD, ACCOUNT_NBR)
/
ALTER TABLE gl_balance_hist_t
    ADD CONSTRAINT GL_BALANCE_HIST_TR1
    FOREIGN KEY(UNIV_FISCAL_YR, FIN_COA_CD, FIN_OBJECT_CD)
    REFERENCES ca_object_code_t(UNIV_FISCAL_YR, FIN_COA_CD, FIN_OBJECT_CD)
/
--
CREATE TABLE gl_entry_hist_t ( 
    UNIV_FISCAL_YR            decimal(4,0) NOT NULL,
    FIN_COA_CD                varchar(2) NOT NULL,
    FIN_OBJECT_CD             varchar(4) NOT NULL,
    FIN_BALANCE_TYP_CD        varchar(2) NOT NULL,
    UNIV_FISCAL_PRD_CD        varchar(2) NOT NULL,
    TRN_DEBIT_CRDT_CD         varchar(1) NOT NULL,
    VER_NBR                   decimal(8,0) DEFAULT '1' NOT NULL ,
    TRN_LDGR_ENTR_AMT         decimal(19,2),
    ROW_CNT                       decimal(7,0),
    PRIMARY KEY(UNIV_FISCAL_YR,FIN_COA_CD,FIN_OBJECT_CD,FIN_BALANCE_TYP_CD,UNIV_FISCAL_PRD_CD,TRN_DEBIT_CRDT_CD)
)
/
ALTER TABLE gl_entry_hist_t
    ADD CONSTRAINT GL_ENTRY_HIST_TR1
    FOREIGN KEY(FIN_COA_CD)
    REFERENCES ca_chart_t(FIN_COA_CD)
/
ALTER TABLE gl_entry_hist_t
    ADD CONSTRAINT GL_ENTRY_HIST_TR2
    FOREIGN KEY(UNIV_FISCAL_YR, FIN_COA_CD, FIN_OBJECT_CD)
    REFERENCES ca_object_code_t(UNIV_FISCAL_YR, FIN_COA_CD, FIN_OBJECT_CD)
/
ALTER TABLE gl_entry_hist_t
    ADD CONSTRAINT GL_ENTRY_HIST_TR3
    FOREIGN KEY(UNIV_FISCAL_YR)
    REFERENCES fs_option_t(UNIV_FISCAL_YR)
/
ALTER TABLE gl_entry_hist_t
    ADD CONSTRAINT GL_ENTRY_HIST_TR4
    FOREIGN KEY(UNIV_FISCAL_YR, UNIV_FISCAL_PRD_CD)
    REFERENCES sh_acct_period_t(UNIV_FISCAL_YR, UNIV_FISCAL_PRD_CD)
/
ALTER TABLE gl_entry_hist_t
    ADD CONSTRAINT GL_ENTRY_HIST_TR5
    FOREIGN KEY(FIN_BALANCE_TYP_CD)
    REFERENCES ca_balance_type_t(FIN_BALANCE_TYP_CD)
/
---
CREATE TABLE ld_ldgr_bal_hist_t ( 
    UNIV_FISCAL_YR        decimal(4,0) NOT NULL,
    FIN_COA_CD            varchar(2) NOT NULL,
    ACCOUNT_NBR           varchar(7) NOT NULL,
    SUB_ACCT_NBR          varchar(5) NOT NULL,
    FIN_OBJECT_CD         varchar(4) NOT NULL,
    FIN_SUB_OBJ_CD        varchar(3) NOT NULL,
    FIN_BALANCE_TYP_CD    varchar(2) NOT NULL,
    FIN_OBJ_TYP_CD        varchar(2) NOT NULL,
    POSITION_NBR          varchar(8) NOT NULL,
    EMPLID                varchar(11) NOT NULL,
    OBJ_ID                varchar(36) NOT NULL,
    VER_NBR               decimal(8,0) NOT NULL,
    ACLN_ANNL_BAL_AMT     decimal(19,2),
    FIN_BEG_BAL_LN_AMT    decimal(19,2),
    CONTR_GR_BB_AC_AMT    decimal(19,2),
    MO1_ACCT_LN_AMT       decimal(19,2),
    MO2_ACCT_LN_AMT       decimal(19,2),
    MO3_ACCT_LN_AMT       decimal(19,2),
    MO4_ACCT_LN_AMT       decimal(19,2),
    MO5_ACCT_LN_AMT       decimal(19,2),
    MO6_ACCT_LN_AMT       decimal(19,2),
    MO7_ACCT_LN_AMT       decimal(19,2),
    MO8_ACCT_LN_AMT       decimal(19,2),
    MO9_ACCT_LN_AMT       decimal(19,2),
    MO10_ACCT_LN_AMT      decimal(19,2),
    MO11_ACCT_LN_AMT      decimal(19,2),
    MO12_ACCT_LN_AMT      decimal(19,2),
    MO13_ACCT_LN_AMT      decimal(19,2),
    PRIMARY KEY(UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,POSITION_NBR,EMPLID)
)
/
ALTER TABLE ld_ldgr_bal_hist_t
    ADD CONSTRAINT LD_LDGR_BAL_HIST_TC0
    UNIQUE (OBJ_ID)
/
ALTER TABLE ld_ldgr_bal_hist_t
    ADD CONSTRAINT LD_LDGR_BAL_HIST_TR6
    FOREIGN KEY(UNIV_FISCAL_YR)
    REFERENCES fs_option_t(UNIV_FISCAL_YR)
/
ALTER TABLE ld_ldgr_bal_hist_t
    ADD CONSTRAINT LD_LDGR_BAL_HIST_TR5
    FOREIGN KEY(FIN_BALANCE_TYP_CD)
    REFERENCES ca_balance_type_t(FIN_BALANCE_TYP_CD)
/
ALTER TABLE ld_ldgr_bal_hist_t
    ADD CONSTRAINT LD_LDGR_BAL_HIST_TR4
    FOREIGN KEY(FIN_OBJ_TYP_CD)
    REFERENCES ca_obj_type_t(FIN_OBJ_TYP_CD)
/
ALTER TABLE ld_ldgr_bal_hist_t
    ADD CONSTRAINT LD_LDGR_BAL_HIST_TR3
    FOREIGN KEY(FIN_COA_CD)
    REFERENCES ca_chart_t(FIN_COA_CD)
/
ALTER TABLE ld_ldgr_bal_hist_t
    ADD CONSTRAINT LD_LDGR_BAL_HIST_TR2
    FOREIGN KEY(FIN_COA_CD, ACCOUNT_NBR)
    REFERENCES ca_account_t(FIN_COA_CD, ACCOUNT_NBR)
/
ALTER TABLE ld_ldgr_bal_hist_t
    ADD CONSTRAINT LD_LDGR_BAL_HIST_TR1
    FOREIGN KEY(UNIV_FISCAL_YR, FIN_COA_CD, FIN_OBJECT_CD)
    REFERENCES ca_object_code_t(UNIV_FISCAL_YR, FIN_COA_CD, FIN_OBJECT_CD)
/
----
CREATE TABLE ld_ldgr_entr_hist_t ( 
    UNIV_FISCAL_YR            decimal(4,0) NOT NULL,
    UNIV_FISCAL_PRD_CD        varchar(2) NOT NULL,
    FIN_COA_CD                varchar(2) NOT NULL,
    FIN_OBJECT_CD             varchar(4) NOT NULL,
    FIN_BALANCE_TYP_CD        varchar(2) NOT NULL,
    TRN_DEBIT_CRDT_CD         varchar(1) NOT NULL,
    OBJ_ID                    varchar(36) NOT NULL,
    VER_NBR                   decimal(8,0) DEFAULT '1' NOT NULL ,
    TRN_LDGR_ENTR_AMT         decimal(19,2),
    ROW_CNT                       decimal(7,0),
    PRIMARY KEY(UNIV_FISCAL_YR,FIN_COA_CD,FIN_OBJECT_CD,FIN_BALANCE_TYP_CD,UNIV_FISCAL_PRD_CD,TRN_DEBIT_CRDT_CD)
)
/
ALTER TABLE ld_ldgr_entr_hist_t
    ADD CONSTRAINT LD_LDGR_ENTR_HIST_TC0
    UNIQUE (OBJ_ID)
/
ALTER TABLE ld_ldgr_entr_hist_t
    ADD CONSTRAINT LD_LDGR_ENTR_HIST_TR1
    FOREIGN KEY(FIN_COA_CD)
    REFERENCES ca_chart_t(FIN_COA_CD)
/
ALTER TABLE ld_ldgr_entr_hist_t
    ADD CONSTRAINT LD_LDGR_ENTR_HIST_TR2
    FOREIGN KEY(UNIV_FISCAL_YR, FIN_COA_CD, FIN_OBJECT_CD)
    REFERENCES ca_object_code_t(UNIV_FISCAL_YR, FIN_COA_CD, FIN_OBJECT_CD)
/
ALTER TABLE ld_ldgr_entr_hist_t
    ADD CONSTRAINT LD_LDGR_ENTR_HIST_TR3
    FOREIGN KEY(UNIV_FISCAL_YR)
    REFERENCES fs_option_t(UNIV_FISCAL_YR)
/
ALTER TABLE ld_ldgr_entr_hist_t
    ADD CONSTRAINT LD_LDGR_ENTR_HIST_TR4
    FOREIGN KEY(UNIV_FISCAL_YR, UNIV_FISCAL_PRD_CD)
    REFERENCES sh_acct_period_t(UNIV_FISCAL_YR, UNIV_FISCAL_PRD_CD)
/
ALTER TABLE ld_ldgr_entr_hist_t
    ADD CONSTRAINT LD_LDGR_ENTR_HIST_TR5
    FOREIGN KEY(FIN_BALANCE_TYP_CD)
    REFERENCES ca_balance_type_t(FIN_BALANCE_TYP_CD)
/
Insert into gl_offset_defn_t values (2009, 'BA', 'APP', 'AC', sys_guid(), 1, '8000')
/
Insert into gl_offset_defn_t values (2009, 'EA' , 'APP', 'AC', sys_guid(), 1, '8000')
/
Insert into gl_offset_defn_t values (2009, 'FW' , 'APP', 'AC', sys_guid(), 1, '8000')
/ 
Insert into gl_offset_defn_t values (2009, 'HO' , 'APP', 'AC', sys_guid(), 1, '8000')
/
Insert into gl_offset_defn_t values (2009, 'IA', 'APP', 'AC', sys_guid(), 1, '8000')
/ 
Insert into gl_offset_defn_t values (2009, 'KO', 'APP', 'AC', sys_guid(), 1, '8000')
/ 
Insert into gl_offset_defn_t values (2009, 'NW', 'APP', 'AC', sys_guid(), 1, '8000')
/ 
Insert into gl_offset_defn_t values (2009, 'SB', 'APP', 'AC', sys_guid(), 1, '8000')
/ 
Insert into gl_offset_defn_t values (2009, 'SE', 'APP', 'AC', sys_guid(), 1, '8000')
/ 

update krim_perm_attr_data_t set attr_val = 'LLCP' where attr_data_id = '107'
/

update krim_role_perm_t set role_id = '19' where role_perm_id = '109'
/

-- Update kim group table so kim type is set to the default value instead of null
-- Alter tables so null kim_type_ids are not allowed

UPDATE KRIM_GRP_T SET KIM_TYP_ID = '1' WHERE KIM_TYP_ID IS NULL
/
ALTER TABLE KRIM_DLGN_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_DLGN_MBR_ATTR_DATA_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_GRP_ATTR_DATA_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_GRP_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_PERM_ATTR_DATA_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_PERM_TMPL_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_ROLE_MBR_ATTR_DATA_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_ROLE_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_RSP_ATTR_DATA_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_RSP_TMPL_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_TYP_ATTR_T MODIFY KIM_TYP_ID NOT NULL
/

-- KULRICE-2625 - make KREW_DOC_TYP_T.RTE_VER_NBR nullable

alter table KREW_DOC_TYP_T modify RTE_VER_NBR NULL
/
