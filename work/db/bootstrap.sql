set serverout on

/* KULDBA to Demo */
/* Disable constraints for the duration of this script */
DECLARE 
   CURSOR constraint_cursor IS 
      SELECT table_name, constraint_name 
         FROM user_constraints 
         WHERE constraint_type = 'R'
           AND status = 'ENABLED';
BEGIN 
   FOR r IN constraint_cursor LOOP
      execute immediate 'ALTER TABLE '||r.table_name||' DISABLE CONSTRAINT '||r.constraint_name; 
   END LOOP; 
END;
/

-- clear all locks

TRUNCATE TABLE krns_maint_lock_t
/

-- clean out non release 3.0 documents and associated entries

DELETE FROM ca_acct_delegate_t
    WHERE fdoc_typ_cd NOT IN ( SELECT DOC_TYP_NM FROM KREW_DOC_TYP_T )
/

DELETE FROM gl_offset_defn_t
    WHERE fdoc_typ_cd NOT IN ( SELECT DOC_TYP_NM FROM KREW_DOC_TYP_T )
/    

-- NOTE: this will leave the balance table out of sync with the GL entries
DELETE FROM gl_entry_t
    WHERE fdoc_typ_cd NOT IN ( SELECT DOC_TYP_NM FROM KREW_DOC_TYP_T )
/    

DELETE FROM gl_encumbrance_t
    WHERE fdoc_typ_cd NOT IN ( SELECT DOC_TYP_NM FROM KREW_DOC_TYP_T )
/    

DELETE FROM ld_ldgr_entr_t
    WHERE fdoc_typ_cd NOT IN ( SELECT DOC_TYP_NM FROM KREW_DOC_TYP_T )
/    

DELETE FROM ca_org_rtng_mdl_t
    WHERE fdoc_typ_cd NOT IN ( SELECT DOC_TYP_NM FROM KREW_DOC_TYP_T )
/    


-- delete old version document types
DELETE FROM KREW_DOC_TYP_T
    WHERE cur_ind = 0
/


DELETE
  FROM KREW_RULE_TMPL_OPTN_T
 WHERE rule_tmpl_id IN (SELECT a.rule_tmpl_id
                          FROM KREW_RULE_TMPL_T a
                         WHERE nm LIKE 'Test%' OR nm LIKE 'KualiReporting%')
/

COMMIT
/


/*  Clear out tables we don't need data in  */

DECLARE
  CURSOR tables_to_empty IS
    SELECT table_name
      FROM user_tables
      WHERE table_name NOT IN ( 
	  'AP_CRDT_MEMO_STAT_T'
	, 'AP_ELCTRNC_INV_RJT_REAS_TYP_T'
	, 'AP_PMT_RQST_STAT_T'
	, 'AR_CLCTN_STATUS_T'
	, 'AR_CUST_ADDR_TYP_T'
	, 'AR_CUST_PRCS_TYP_T'
	, 'AR_CUST_TYP_T'
	, 'AR_PMT_MEDIUM_T'
	, 'CA_ACCOUNT_TYPE_T'
	, 'CA_ACCTG_CTGRY_T'
	, 'CA_ACCT_SF_T'
	, 'CA_AICPA_FUNC_T'
	, 'CA_BALANCE_TYPE_T'
	, 'CA_BDGT_AGGR_T'
	, 'CA_BDGT_REC_LVL_T'
	, 'CA_FED_FND_T'
	, 'CA_FEDERAL_FUNC_T'
	, 'CA_FUND_GRP_T'
	, 'CA_HIGHR_ED_FUNC_T'
	, 'CA_ICR_TYPE_T'
	, 'CA_MNXFR_ELIM_T'
	, 'CA_OBJ_SUB_TYPE_T'
	, 'CA_OBJ_TYPE_T'
	, 'CA_ORG_RVRSN_CTGRY_T'
	, 'CA_ORG_TYPE_T'
	, 'CA_RC_T'
	, 'CA_RESTRICT_STAT_T'
	, 'CA_SUB_FND_GRP_TYP_T'
	, 'CA_SUB_FUND_GRP_T'
	, 'CA_UBO_FUNC_T'
	, 'CB_AST_TRN_TYP_T'
	, 'CG_AGENCY_TYP_T'
	, 'CG_AWD_STAT_T'
	, 'CG_CFDA_REF_T'
	, 'CG_GRANT_DESC_T'
	, 'CG_LTRCR_FNDGRP_T'
	, 'CG_PRPSL_AWD_TYP_T'
	, 'CG_PRPSL_PURPOSE_T'
	, 'CG_PRPSL_STAT_T'
	, 'CM_ASSET_TYPE_T'
	, 'CM_AST_CONDITION_T'
	, 'CM_AST_DEPR_MTHD_T'
	, 'CM_AST_LOC_TYP_T'
	, 'CM_AST_STATUS_T'
	, 'CM_ACQ_TYPE_T'
	, 'CM_RETIRE_REAS_T'
	, 'ER_APPT_TYP_EFF_DT_T'
	, 'ER_APPT_TYP_T'
	, 'ER_BDGT_BASE_CD_T'
	, 'ER_BDGT_TYP_CD_T'
	, 'ER_CTRL_ATTRIB_TYP_T'
	, 'ER_DUE_DT_TYP_T'
	, 'ER_IDC_LU_T'
	, 'ER_NPRS_CTGRY_CD_T'
	, 'ER_NPRS_SUB_CTGRY_CD_T'
	, 'ER_PROJ_TYP_T'
	, 'ER_PRPS_T'
	, 'ER_PRSN_ROLE_T'
	, 'ER_RSRCH_RSK_TYP_T'
	, 'ER_RSRCH_TYP_CD_T'
	, 'ER_QSTN_TYP_T'
	, 'FP_CR_CARD_TYP_T'
	, 'FP_DV_DIEM_T'
	, 'FP_DV_EXP_TYP_T'
	, 'FP_DV_MLG_T'
	, 'FP_DV_OWNR_TYP_T'
	, 'FP_DV_PMT_REAS_T'
	, 'FP_DV_TAX_CTRL_T'
	, 'FP_DV_TRVL_CO_NM_T'
	, 'FP_FSCL_YR_CTRL_T'
	, 'FP_FUNC_CTRL_CD_T'
	, 'FP_INC_CLS_T'
	, 'FP_NRA_TAX_PCT_T'
	, 'FP_DV_PMT_REAS_T'
	, 'FS_HOME_ORIGIN_T'
	, 'FS_OPTION_T'
	, 'FS_ORIGIN_CODE_T'
	, 'FS_TAX_REGION_TYPE_T'
	, 'GL_ORIGIN_ENTRY_SRC_T'
	, 'KR_COUNTY_T'
	, 'KR_COUNTRY_T'
	, 'KR_POSTAL_CODE_T'
	, 'KR_STATE_T'
	, 'KREW_DLGN_RSP_T'
	, 'KREW_DOC_TYP_ATTR_T'
	, 'KREW_DOC_TYP_PLCY_RELN_T'
	, 'KREW_DOC_TYP_PROC_T'
	, 'KREW_DOC_TYP_T'
	, 'KREW_RTE_BRCH_PROTO_T'
	, 'KREW_RTE_BRCH_ST_T'
	, 'KREW_RTE_BRCH_T'
	, 'KREW_RTE_NODE_CFG_PARM_T'
	, 'KREW_RTE_NODE_LNK_T'
	, 'KREW_RTE_NODE_T'
	, 'KREW_RULE_ATTR_T'
	, 'KREW_RULE_EXT_T'
	, 'KREW_RULE_EXT_VAL_T'
	, 'KREW_RULE_RSP_T'
	, 'KREW_RULE_T'
	, 'KREW_RULE_TMPL_ATTR_T'
	, 'KREW_RULE_TMPL_OPTN_T'
	, 'KREW_RULE_TMPL_T'
	, 'KREW_USR_OPTN_T'
	, 'KRIM_ADDR_TYP_T'
	, 'KRIM_AFLTN_TYP_T'
	, 'KRIM_ATTR_DEFN_T'
	, 'KRIM_CTZNSHP_STAT_T'
	, 'KRIM_DLGN_MBR_T'
	, 'KRIM_DLGN_T'
	, 'KRIM_EMAIL_TYP_T'
	, 'KRIM_EMP_STAT_T'
	, 'KRIM_EMP_TYP_T'
	, 'KRIM_ENT_NM_TYP_T'
	, 'KRIM_ENT_TYP_T'
	, 'KRIM_EXT_ID_TYP_T'
	, 'KRIM_PERM_ATTR_DATA_T'
	, 'KRIM_PERM_RQRD_ATTR_T'
	, 'KRIM_PERM_T'
	, 'KRIM_PERM_TMPL_T'
	, 'KRIM_PHONE_TYP_T'
	, 'KRIM_ROLE_MBR_ATTR_DATA_T'
	, 'KRIM_ROLE_MBR_T'
	, 'KRIM_ROLE_PERM_T'
	, 'KRIM_ROLE_RSP_ACTN_T'
	, 'KRIM_ROLE_RSP_T'
	, 'KRIM_ROLE_T'
	, 'KRIM_RSP_ATTR_DATA_T'
	, 'KRIM_RSP_RQRD_ATTR_T'
	, 'KRIM_RSP_T'
	, 'KRIM_RSP_TMPL_T'
	, 'KRIM_TYP_ATTR_T'
	, 'KRIM_TYP_T'
	, 'KRNS_CMP_TYP_T'
	, 'KRNS_DOC_TYP_ATTR_T'
	, 'KRNS_NMSPC_T'
	, 'KRNS_NTE_TYP_T'
	, 'KRNS_PARM_T'
	, 'KRNS_PARM_TYP_T'
	, 'KRNS_PARM_DTL_TYP_T'
	, 'LD_A21_PRD_STAT_T'
	, 'LD_BCN_AF_RSN_CD_T'
	, 'LD_BCN_DURATION_T'
	, 'LD_BENEFITS_TYPE_T'
	, 'LD_POS_OBJ_GRP_T'
	, 'PDP_ACCTG_CHG_CD_T'
	, 'PDP_ACH_TRANS_CD_T'
	, 'PDP_DISB_TYP_CD_T'
	, 'PDP_PAYEE_TYP_T'
	, 'PDP_PMT_CHG_CD_T'
	, 'PDP_PMT_STAT_CD_T'
	, 'PDP_PMT_TYP_T'
	, 'PUR_ADDR_TYP_T'
	, 'PUR_AP_ITM_TYP_T'
	, 'PUR_AP_RECUR_PMT_FREQ_T'
	, 'PUR_AP_RECUR_PMT_TYP_T'
	, 'PUR_CARI_T'
	, 'PUR_CNTCT_TYP_T'
	, 'PUR_CONTR_MGR_T' 
	, 'PUR_CPTL_AST_SYS_ST_T'
	, 'PUR_CPTL_AST_SYS_TYP_T'
	, 'PUR_DLVY_REQ_DT_REAS_T'
	, 'PUR_FND_SRC_T'
	, 'PUR_ITM_REAS_ADD_T'
	, 'PUR_OWNR_CTGRY_T'
	, 'PUR_OWNR_TYP_T'
	, 'PUR_PHN_TYP_T'
	, 'PUR_PMT_TERM_TYP_T'
	, 'PUR_PO_CST_SRC_T'
	, 'PUR_PO_STAT_T'
	, 'PUR_PO_TRNS_MTHD_T'
	, 'PUR_PO_VNDR_CHC_T'
	, 'PUR_RCVNG_LN_STAT_T'
	, 'PUR_REQS_SRC_T'
	, 'PUR_REQS_STAT_T'
	, 'PUR_SHP_PMT_TERM_T'
	, 'PUR_SHP_SPCL_COND_T'
	, 'PUR_SHP_TTL_T'
	, 'PUR_SUPP_DVRST_T'
	, 'PUR_VNDR_INACTV_REAS_T'
	, 'PUR_VNDR_STPLTN_T'
	, 'PUR_VNDR_TYP_T'
	, 'QRTZ_LOCKS'
	, 'SH_ACCT_PERIOD_T'
	, 'SH_UNIV_DATE_T'
	, 'SH_UOM_T'
	);
BEGIN
  FOR rec IN tables_to_empty LOOP
    dbms_output.put_line( 'Truncated Table: '||rec.table_name );
    EXECUTE IMMEDIATE 'TRUNCATE TABLE '||rec.table_name;
  END LOOP;
END;
/

/*  Users & Groups  */

DELETE FROM KRIM_ROLE_MBR_T WHERE MBR_TYP_CD='P'; 
/
DELETE FROM KRIM_DLGN_MBR_T WHERE MBR_TYP_CD='P'; 
/

/* these next two delete most of the info in these tables,
 * the little that is left probably needs to be reviewed by
 * implementing schools to verify that data. without the
 * entries though, routing fails on most transactional docs */

DELETE FROM KRIM_ROLE_RSP_ACTN_T WHERE ROLE_MBR_ID <> '*';
/
DELETE FROM KRIM_ROLE_MBR_ATTR_DATA_T WHERE ROLE_MBR_ID NOT IN (SELECT ROLE_MBR_ID from KRIM_ROLE_MBR_T where MBR_TYP_CD='R')
/
/* RE-INSERT to user and workgroup member table (admin) */
INSERT INTO krim_prncpl_t
(prncpl_id,"PRNCPL_NM",entity_id,obj_id)
VALUES
('1','kr','1',SYS_GUID())
/
INSERT INTO krim_prncpl_t
(prncpl_id,"PRNCPL_NM",entity_id,obj_id)
VALUES
('2','kfs','2',SYS_GUID())
/
INSERT INTO krim_prncpl_t
(prncpl_id,"PRNCPL_NM",entity_id,obj_id)
VALUES
('3','admin','3',SYS_GUID())
/
INSERT INTO krim_entity_t
(entity_id,obj_id)
VALUES
('1',SYS_GUID())
/
INSERT INTO krim_entity_t
(entity_id,obj_id)
VALUES
('2',SYS_GUID())
/
INSERT INTO krim_entity_t
(entity_id,obj_id)
VALUES
('3',SYS_GUID())
/

INSERT INTO KRIM_ENTITY_ENT_TYP_T
("ENT_TYP_CD",entity_id,obj_id)
VALUES
('SYSTEM','1',SYS_GUID())
/
INSERT INTO KRIM_ENTITY_ENT_TYP_T
("ENT_TYP_CD",entity_id,obj_id)
VALUES
('SYSTEM','2',SYS_GUID())
/
INSERT INTO KRIM_ENTITY_ENT_TYP_T
("ENT_TYP_CD",entity_id,obj_id)
VALUES
('PERSON','3',SYS_GUID())
/
INSERT INTO KRIM_ENTITY_NM_T
("ENTITY_NM_ID",obj_id,entity_id,"NM_TYP_CD","FIRST_NM","MIDDLE_NM","LAST_NM","DFLT_IND")
VALUES
('1',SYS_GUID(),'3','PRFR','KFS','','Admin', 'Y')
/
INSERT INTO KRIM_ENTITY_AFLTN_T
("ENTITY_AFLTN_ID",OBJ_ID,ENTITY_ID,"AFLTN_TYP_CD","CAMPUS_CD","DFLT_IND")
VALUES
('1',SYS_GUID(),'3','STAFF','01','Y')
/
INSERT INTO KRIM_ENTITY_EMP_INFO_T
("ENTITY_EMP_ID",OBJ_ID,ENTITY_ID,"ENTITY_AFLTN_ID","EMP_STAT_CD","EMP_TYP_CD","BASE_SLRY_AMT","PRMRY_IND")
VALUES
('1',SYS_GUID(),'3','1','A','P',100000,'Y')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('1',SYS_GUID(),'90','1')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('2',SYS_GUID(),'62','2')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('3',SYS_GUID(),'45','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('4',SYS_GUID(),'11','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('5',SYS_GUID(),'12','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('6',SYS_GUID(),'13','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('7',SYS_GUID(),'15','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('8',SYS_GUID(),'16','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('11',SYS_GUID(),'22','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('12',SYS_GUID(),'25','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('14',SYS_GUID(),'29','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('15',SYS_GUID(),'30','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('16',SYS_GUID(),'31','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('17',SYS_GUID(),'34','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('18',SYS_GUID(),'36','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('19',SYS_GUID(),'37','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('20',SYS_GUID(),'38','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('22',SYS_GUID(),'40','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('23',SYS_GUID(),'46','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('24',SYS_GUID(),'47','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('26',SYS_GUID(),'49','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('27',SYS_GUID(),'50','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('28',SYS_GUID(),'51','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('29',SYS_GUID(),'54','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('30',SYS_GUID(),'55','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('32',SYS_GUID(),'65','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('34',SYS_GUID(),'7','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('35',SYS_GUID(),'76','3')
/
INSERT INTO KRIM_ROLE_MBR_T
("ROLE_MBR_ID",OBJ_ID,"ROLE_ID","MBR_ID")
VALUES
('36',SYS_GUID(),'79','3')
/

INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T
("ATTR_DATA_ID",OBJ_ID,"ROLE_MBR_ID","KIM_TYP_ID","KIM_ATTR_DEFN_ID","ATTR_VAL")
VALUES
('1',SYS_GUID(),'6','26','22','01')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T
("ATTR_DATA_ID",OBJ_ID,"ROLE_MBR_ID","KIM_TYP_ID","KIM_ATTR_DEFN_ID","ATTR_VAL")
VALUES
('2',SYS_GUID(),'6','26','23','0001')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T
("ATTR_DATA_ID",OBJ_ID,"ROLE_MBR_ID","KIM_TYP_ID","KIM_ATTR_DEFN_ID","ATTR_VAL")
VALUES
('3',SYS_GUID(),'4','17','12','01')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T
("ATTR_DATA_ID",OBJ_ID,"ROLE_MBR_ID","KIM_TYP_ID","KIM_ATTR_DEFN_ID","ATTR_VAL")
VALUES
('4',SYS_GUID(),'5','17','12','01')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T
("ATTR_DATA_ID",OBJ_ID,"ROLE_MBR_ID","KIM_TYP_ID","KIM_ATTR_DEFN_ID","ATTR_VAL")
VALUES
('5',SYS_GUID(),'12','34','32','99')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T
("ATTR_DATA_ID",OBJ_ID,"ROLE_MBR_ID","KIM_TYP_ID","KIM_ATTR_DEFN_ID","ATTR_VAL")
VALUES
('6',SYS_GUID(),'15','27','22','01')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T
("ATTR_DATA_ID",OBJ_ID,"ROLE_MBR_ID","KIM_TYP_ID","KIM_ATTR_DEFN_ID","ATTR_VAL")
VALUES
('7',SYS_GUID(),'15','27','24','0001')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T
("ATTR_DATA_ID",OBJ_ID,"ROLE_MBR_ID","KIM_TYP_ID","KIM_ATTR_DEFN_ID","ATTR_VAL")
VALUES
('8',SYS_GUID(),'19','25','22','01')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T
("ATTR_DATA_ID",OBJ_ID,"ROLE_MBR_ID","KIM_TYP_ID","KIM_ATTR_DEFN_ID","ATTR_VAL")
VALUES
('9',SYS_GUID(),'29','36','4','KFS-SYS')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T
("ATTR_DATA_ID",OBJ_ID,"ROLE_MBR_ID","KIM_TYP_ID","KIM_ATTR_DEFN_ID","ATTR_VAL")
VALUES
('10',SYS_GUID(),'29','36','22','01')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T
("ATTR_DATA_ID",OBJ_ID,"ROLE_MBR_ID","KIM_TYP_ID","KIM_ATTR_DEFN_ID","ATTR_VAL")
VALUES
('11',SYS_GUID(),'29','36','24','0001')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T
("ATTR_DATA_ID",OBJ_ID,"ROLE_MBR_ID","KIM_TYP_ID","KIM_ATTR_DEFN_ID","ATTR_VAL")
VALUES
('12',SYS_GUID(),'34','29','22','01')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T
("ATTR_DATA_ID",OBJ_ID,"ROLE_MBR_ID","KIM_TYP_ID","KIM_ATTR_DEFN_ID","ATTR_VAL")
VALUES
('13',SYS_GUID(),'34','29','24','0001')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T
("ATTR_DATA_ID",OBJ_ID,"ROLE_MBR_ID","KIM_TYP_ID","KIM_ATTR_DEFN_ID","ATTR_VAL")
VALUES
('14',SYS_GUID(),'34','29','13','ACCT')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T
("ATTR_DATA_ID",OBJ_ID,"ROLE_MBR_ID","KIM_TYP_ID","KIM_ATTR_DEFN_ID","ATTR_VAL")
VALUES
('15',SYS_GUID(),'35','27','22','01')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T
("ATTR_DATA_ID",OBJ_ID,"ROLE_MBR_ID","KIM_TYP_ID","KIM_ATTR_DEFN_ID","ATTR_VAL")
VALUES
('16',SYS_GUID(),'35','27','24','0001')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T
("ATTR_DATA_ID",OBJ_ID,"ROLE_MBR_ID","KIM_TYP_ID","KIM_ATTR_DEFN_ID","ATTR_VAL")
VALUES
('17',SYS_GUID(),'36','25','22','0001')
/

INSERT INTO CA_CHART_T
("FIN_COA_CD",OBJ_ID,"FIN_COA_DESC","FIN_COA_ACTIVE_CD","RPTS_TO_FIN_COA_CD")
VALUES
('01',SYS_GUID(),'Default Chart','Y','01')
/
INSERT INTO CA_ORG_T
("FIN_COA_CD","ORG_CD",OBJ_ID,"ORG_NM")
VALUES
('01','0001',SYS_GUID(),'Default Org')
/

COMMIT
/


/* Workflow Constants, Document Types & Rules */


-- Clean up person responsibility references
UPDATE KREW_RULE_RSP_T
    SET NM = '1'
    WHERE TYP = 'F'
/


/*  System Parameters & Rules  */
-- blank out rules which contain values from other emptied tables

--SELECT a.sh_parm_nmspc_cd, a.sh_parm_dtl_typ_cd, a.sh_parm_nm, a.sh_parm_typ_cd, a.sh_parm_txt
--  FROM sh_parm_t a
  UPDATE KRNS_PARM_T 
	SET TXT = NULL
  WHERE TXT IS NOT NULL
    AND ( 
		parm_nm LIKE '%CHARTS'
	 OR parm_nm LIKE '%CHART'
	 OR parm_nm LIKE '%CHART_CODE'
	 OR parm_nm LIKE '%OBJECT_CODE%'
	 OR parm_nm LIKE '%OBJECT_LEVEL%'
	 OR parm_nm LIKE '%OBJECT_CONS%'
	 OR parm_nm LIKE '%CAMPUS%'
	 OR parm_nm LIKE '%ORIGINATIONS%'
	 OR parm_nm LIKE '%ACCOUNT%'
	 OR parm_nm LIKE '%BANK_ACCOUNT%'
	 OR parm_nm LIKE '%ORGANIZATION'
	 OR parm_nm LIKE '%USER'
   )
   AND parm_nm NOT LIKE '%GROUP'
   AND parm_nm <> 'SUB_ACCOUNT_TYPES'
   AND parm_nm NOT LIKE '%OBJECT_SUB_TYPES'
   AND parm_nm NOT LIKE 'MAX_FILE_SIZE%'
   AND parm_nm <> 'MAXIMUM_ACCOUNT_RESPONSIBILITY_ID'
/
COMMIT
/

/*  Reference Data  */

/* One origin code */
DELETE FROM FS_ORIGIN_CODE_T
   WHERE FS_ORIGIN_CD NOT IN ( '01', 'EU', 'MF', 'PD' )
/
/
/* Fix home origin table */
update fs_home_origin_t set fs_home_origin_cd = '01'
/
/* Fix the sh_campus_t table */
insert into KRNS_CAMPUS_T values ('01','Default Campus','Campus','F',sys_guid(),1,'Y')
/
/*  Charts & Organizations  */

DELETE FROM ca_org_type_t
   WHERE org_typ_cd NOT IN ( 'C', 'N', 'R', 'U' )
/

DELETE FROM ca_rc_t WHERE rc_cd <> 'NO'
/
COMMIT
/

-- clean up the options table

UPDATE fs_option_t
	SET univ_fin_coa_cd = NULL
/

/*  Chart of Accounts  */

DELETE FROM ca_obj_sub_type_t
WHERE fin_obj_sub_typ_cd NOT IN (
  'AR'
, 'BU'
, 'CA'
, 'CM'
, 'FB'
, 'FR'
, 'GI'
, 'HW'
, 'IN'
, 'IV'
, 'MT'
, 'NA'
, 'SA'
, 'SF'
, 'SS'
, 'TN'
, 'TR'
)
/

DELETE FROM ca_sub_fund_grp_t
WHERE sub_fund_grp_cd NOT IN (
  'GENFND'
, 'DCEDU'
, 'DPSA'
, 'DOFDS'
, 'DSCFE'
, 'RESSCH'
, 'RESFEL'
, 'ROFDS'
, 'HIEDUA'
, 'FEDERA'
, 'COMMEA'
, 'STATEA'
, 'FOUNDA'
, 'NONPRA'
, 'OTGOVA'
, 'AUXENT'
, 'CLEAR'
, 'LOANFD'
, 'ENDOW'
, 'PFCMR'
, 'PFRI'
, 'PFRR'
, 'PFIP'
, 'EXTAGY'
, 'OTHFDS'
)
/

DELETE FROM ca_account_type_t
    WHERE acct_typ_cd NOT IN ( 'AI', 'BS', 'EQ', 'NA', 'RA', 'WS' )
/

/*  Disbursement Voucher Data  */

-- create new DV document location    
INSERT INTO fp_dv_doc_loc_t
(DV_DOC_LOC_CD,OBJ_ID,VER_NBR,DV_DOC_LOC_NM,DV_DOC_LOC_ADDR,ROW_ACTV_IND)
VALUES
('01',sys_guid(),1,'Kuali Default DV Doc Loc',NULL,'Y')
/

-- clear out references to exact object codes
UPDATE fp_dv_pmt_reas_t 
    SET dv_pmt_reas_desc = SUBSTR( dv_pmt_reas_desc, 1, INSTR( dv_pmt_reas_desc, '.  Some common' ) )
    WHERE dv_pmt_reas_desc LIKE '%.  Some common%'
/    
UPDATE fp_dv_pmt_reas_t 
    SET dv_pmt_reas_desc = SUBSTR( dv_pmt_reas_desc, 1, INSTR( dv_pmt_reas_desc, '. Some common' ) )
    WHERE dv_pmt_reas_desc LIKE '%. Some common%'
/    
UPDATE fp_dv_pmt_reas_t 
    SET dv_pmt_reas_desc = SUBSTR( dv_pmt_reas_desc, 1, INSTR( dv_pmt_reas_desc, '.  Object codes' ) )
    WHERE dv_pmt_reas_desc LIKE '%.  Object codes%'
/    
UPDATE fp_dv_pmt_reas_t 
    SET dv_pmt_reas_desc = SUBSTR( dv_pmt_reas_desc, 1, INSTR( dv_pmt_reas_desc, '.  The object code' ) )
    WHERE dv_pmt_reas_desc LIKE '%.  The object code%'
/    
UPDATE fp_dv_pmt_reas_t 
    SET dv_pmt_reas_desc = SUBSTR( dv_pmt_reas_desc, 1, INSTR( dv_pmt_reas_desc, '.  Common object' ) )
    WHERE dv_pmt_reas_desc LIKE '%.  Common object%'
/


DELETE FROM fp_dv_trvl_co_nm_t
WHERE ( dv_exp_cd, dv_exp_co_nm ) NOT IN (
 ( 'A', 'CONTINENTAL/CONTINENTAL EXPRESS' )
, ( 'A', 'DELTA' )
, ( 'A', 'NORTHWEST' )
, ( 'A', 'OTHER' )
, ( 'A', 'SOUTHWEST' )
, ( 'A', 'TRANS WORLD AIRLINES' )
, ( 'A', 'U.S. AIRWAYS' )
, ( 'A', 'UNITED AIRLINES' )
, ( 'L', 'BEST WESTERN' )
, ( 'L', 'CENTURY SUITES' )
, ( 'L', 'COMFORT AND CLARION INNS' )
, ( 'L', 'COURTYARD' )
, ( 'L', 'DAYS INN' )
, ( 'L', 'FAIRFIELD INN' )
, ( 'L', 'HAMPTON INN' )
, ( 'L', 'HOLIDAY INNS' )
, ( 'L', 'OTHER' )
, ( 'L', 'RAMADA INNS' )
, ( 'L', 'TRAVELODGE' )
, ( 'M', 'OTHER' )
, ( 'M', 'RYDER' )
, ( 'M', 'U-HAUL' )
, ( 'O', 'OTHER' )
, ( 'PA', 'CONTINENTAL/CONTINENTAL EXPRESS' )
, ( 'PA', 'DELTA' )
, ( 'PA', 'NORTHWEST' )
, ( 'PA', 'OTHER' )
, ( 'PA', 'SOUTHWEST' )
, ( 'PA', 'TRANS WORLD AIRLINES' )
, ( 'PA', 'U.S. AIRWAYS' )
, ( 'PA', 'UNITED AIRLINES' )
, ( 'PC', 'OTHER' )
, ( 'PL', 'BEST WESTERN' )
, ( 'PL', 'CENTURY SUITES' )
, ( 'PL', 'COMFORT AND CLARION INNS' )
, ( 'PL', 'COURTYARD' )
, ( 'PL', 'DAYS INN' )
, ( 'PL', 'FAIRFIELD INN' )
, ( 'PL', 'HAMPTON INN' )
, ( 'PL', 'HOLIDAY INNS' )
, ( 'PL', 'OTHER' )
, ( 'PL', 'RAMADA INNS' )
, ( 'PL', 'TRAVELODGE' )
, ( 'PM', 'OTHER' )
, ( 'PM', 'RYDER' )
, ( 'PM', 'U-HAUL' )
, ( 'PO', 'PREPAID OTHER' )
, ( 'PR', 'ALAMO' )
, ( 'PR', 'AVIS' )
, ( 'PR', 'BUDGET' )
, ( 'PR', 'HERTZ' )
, ( 'PR', 'NATIONAL' )
, ( 'PR', 'OTHER' )
, ( 'PT', 'OTHER' )
, ( 'PT', 'YELLOW CAB CO.' )
, ( 'R', 'ALAMO' )
, ( 'R', 'AVIS' )
, ( 'R', 'BUDGET' )
, ( 'R', 'HERTZ' )
, ( 'R', 'NATIONAL' )
, ( 'R', 'OTHER' )
, ( 'T', 'OTHER' )
, ( 'T', 'YELLOW CAB CO.' )
)
/

/*  Financial Document Data  */
/* No demo to bootstrap changes */


/*  Labor Distribution  */
/* No demo to bootstrap changes */


/*  Pre-Disbursement Processor  */
/* No demo to bootstrap changes */


/*  Vendor  */
/* No demo to bootstrap changes */

/*  Purchasing/Accounts Payable  */
DELETE FROM pur_contr_mgr_t 
	WHERE contr_mgr_cd NOT IN ( 99 )
/
UPDATE pur_contr_mgr_t 
	SET contr_mgr_nm = 'User,Kuali'
      , contr_mgr_phn_nbr = '000-555-1212'
      , contr_mgr_fax_nbr = '000-555-1212'
/
COMMIT
/


/*  Contracts & Grants  */
/* No demo to bootstrap changes */


/*  Research Administration  */
/* No demo to bootstrap changes */


/* Re-enable constraints */
DECLARE 
   CURSOR constraint_cursor IS 
      SELECT table_name, constraint_name 
         FROM user_constraints 
         WHERE constraint_type = 'R'
           AND status <> 'ENABLED';
BEGIN 
   FOR r IN constraint_cursor LOOP
      execute immediate 'ALTER TABLE '||r.table_name||' ENABLE CONSTRAINT '||r.constraint_name; 
   END LOOP; 
END;
/
