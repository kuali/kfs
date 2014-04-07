--
-- Copyright 2009-2013 The Kuali Foundation
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

/*  Clear out tables we don't need rows in  */
/*  Leave all rice tables - if they're all in the same database, then we don't want to wipe out Rice data */

DECLARE
  CURSOR tables_to_empty IS
    SELECT table_name
      FROM user_tables
      WHERE table_name NOT IN ( 
	  'AP_CRDT_MEMO_STAT_T'
	, 'AP_ELCTRNC_INV_RJT_REAS_TYP_T'
	, 'AP_PMT_RQST_STAT_T'
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
	, 'CM_AST_PMT_DST_CD_T'
	, 'CM_AST_PMT_DOC_TYP_T'
	, 'CM_AST_STATUS_T'
	, 'CM_ACQ_TYPE_T'
	, 'CM_RETIRE_REAS_T'
	, 'ER_CTRL_ATTRIB_TYP_T'
	, 'ER_RSRCH_RSK_TYP_T'
	, 'FP_CR_CARD_TYP_T'
	, 'FP_DV_DIEM_T'
	, 'FP_DV_EXP_TYP_T'
	, 'FP_DV_MLG_T'
	, 'FP_DV_PMT_REAS_T'
	, 'FP_DV_TRVL_CO_NM_T'
	, 'FP_FSCL_YR_CTRL_T'
	, 'FP_FUNC_CTRL_CD_T'
	, 'FP_INC_CLS_T'
	, 'FP_NRA_TAX_PCT_T'
	, 'FS_HOME_ORIGIN_T'
	, 'FS_OPTION_T'
	, 'FS_ORIGIN_CODE_T'
	, 'FS_TAX_REGION_TYPE_T'
	, 'LD_A21_PRD_STAT_T'
	, 'LD_BCN_AF_RSN_CD_T'
	, 'LD_BCN_DURATION_T'
	, 'LD_BENEFITS_TYPE_T'
	, 'LD_LBR_BFT_RT_CAT_T'
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
	, 'PUR_PO_QT_STAT_T'
	, 'PUR_SHP_PMT_TERM_T'
	, 'PUR_SHP_SPCL_COND_T'
	, 'PUR_SHP_TTL_T'
	, 'PUR_SUPP_DVRST_T'
	, 'PUR_VNDR_INACTV_REAS_T'
	, 'PUR_VNDR_STPLTN_T'
	, 'PUR_VNDR_TYP_T'
	, 'QRTZ_LOCKS'
	, 'SEC_SCRTY_ATTR_T'
	, 'SH_ACCT_PERIOD_T'
	, 'SH_UNIV_DATE_T'
	, 'SH_UOM_T'
	, 'END_ACRL_MTHD_T'
	, 'END_CAE_CD_T'
	, 'END_CLS_CD_TYP_T'
	, 'END_DONR_LBL_SEL_T'
	, 'END_ETRAN_TYP_CD_T'
	, 'END_FEE_BAL_TYP_CD_T'
	, 'END_FEE_BASE_CD_T'
	, 'END_FEE_RT_DEF_CD_T'
	, 'END_FEE_TYP_CD_T'
	, 'END_FREQ_CD_T'
	, 'END_IP_IND_T'
	, 'END_PMT_TYP_CD_T'
	, 'END_SEC_VLTN_MTHD_T'
	, 'END_TRAN_SUB_TYP_T'
	, 'END_TRAN_SRC_TYP_T'
	, 'END_SEC_RPT_GRP_T'
	, 'END_AGRMNT_SPCL_INSTRC_CD_T'
	, 'END_AGRMNT_STAT_CD_T'
	, 'END_TRAN_RESTR_CD_T'
	, 'END_TYP_RESTR_CD_T'
	, 'TEM_ACCOMM_TYP_T'
	, 'TEM_CLASS_SVC_T'
	, 'TEM_CONT_REL_TYP_T'
	, 'TEM_PER_DIEM_MIE_BREAK_DOWN_T'
	, 'TEM_TRAVELER_TYP_T'
	, 'TEM_TRIP_TYP_T'
	, 'TEM_TRVL_CARD_TYP_T'
	
	
);
BEGIN
  FOR rec IN tables_to_empty LOOP
    dbms_output.put_line( 'Truncated Table: '||rec.table_name );
    EXECUTE IMMEDIATE 'TRUNCATE TABLE '||rec.table_name;
  END LOOP;
END;
/

/* Inserting some really base data to keep the system happy. */
--INSERT INTO CA_CHART_T
--("FIN_COA_CD",OBJ_ID,"FIN_COA_DESC","FIN_COA_ACTIVE_CD","RPTS_TO_FIN_COA_CD")
--VALUES
--('01',SYS_GUID(),'Default Chart','Y','01')
--/
--INSERT INTO CA_ORG_T
--("FIN_COA_CD","ORG_CD",OBJ_ID,"ORG_NM")
--VALUES
--('01','0001',SYS_GUID(),'Default Org')
--/

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

/* clean out all non "01" origin codes */
DELETE FROM fs_origin_code_t WHERE fs_origin_cd <> '01'
/

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

/* Endowment */

-- Only the CSHEQ record is required to be kept
DELETE FROM END_SEC_RPT_GRP_T
	WHERE SEC_RPT_GRP != 'CSHEQ' 
/
--    Only the record 0, None
DELETE FROM END_AGRMNT_SPCL_INSTRC_CD_T   
	WHERE AGRMNT_SPCL_INSTRC_CD != '0'
/

--   3 records : COMP, NONE, PEND
DELETE FROM END_AGRMNT_STAT_CD_T
	WHERE AGRMNT_STAT_CD NOT IN ( 'COMP', 'NONE', 'PEND' )
/
--  3 records: NDISB, NONE, NTRAN
DELETE FROM END_TRAN_RESTR_CD_T
	WHERE TRAN_RESTR_CD NOT IN ( 'NDISB', 'NONE', 'NTRAN' )
/
--  4 records: U, NA, P, T
DELETE FROM END_TYP_RESTR_CD_T
	WHERE TYP_RESTR_CD NOT IN ( 'U', 'NA', 'P', 'T' )
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