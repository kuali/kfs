
SELECT UNIV_FISCAL_YR, OBJ_ID, VER_NBR, ACT_FIN_BAL_TYP_CD, BDGT_CHK_BALTYP_CD, BDGT_CHK_OPTN_CD, UNIV_FSCYR_STRT_YR, UNIV_FSCYR_STRT_MO, FOBJTP_INC_CSH_CD, FOBJTP_XPND_EXP_CD, FOBJTP_XPNDNEXP_CD, FOBJTP_EXPNXPND_CD, FOBJ_TYP_ASSET_CD, FOBJ_TYP_LBLTY_CD, FOBJ_TYP_FNDBAL_CD, EXT_ENC_FBALTYP_CD, INT_ENC_FBALTYP_CD, PRE_ENC_FBALTYP_CD, ELIM_FINBAL_TYP_CD, FOBJTP_INC_NCSH_CD, FOBJTP_CSH_NINC_CD, UNIV_FIN_COA_CD, UNIV_FISCAL_YR_NM, FIN_BEGBALLOAD_IND, CSTSHR_ENCUM_FIN_BAL_TYP_CD, BASE_BDGT_FIN_BAL_TYP_CD, MO_BDGT_FIN_BAL_TYP_CD, FIN_OBJECT_TYP_TRNFR_INC_CD, FIN_OBJECT_TYP_TRNFR_EXP_CD, NMNL_FIN_BAL_TYP_CD 
    FROM FS_OPTION_T
    WHERE UNIV_FISCAL_YR = 2011 + 1
/
.saveLastResult 'Format=Insert,
	File="/Users/kellerj/Desktop/temp/01_FS_OPTION_T_2011.sql",
	Overwrite=true,
	Encoding=,
	Platform=Unix (LF) - EOL,
	StatementSeparator=/,
	QuoteIdentifier="\"",
	Schema=KULDBA,
	Table=FS_OPTION_T,
	IncludeCreate=false' 
/
SELECT UNIV_FISCAL_YR, FIN_COA_CD, ORG_CD, OBJ_ID, VER_NBR, BDGT_RVRSN_COA_CD, BDGT_RVRSNACCT_NBR, CF_BY_OBJ_CD_IND, CSH_RVRSNFINCOA_CD, CSH_RVRSN_ACCT_NBR, ROW_ACTV_IND 
    FROM CA_ORG_REVERSION_T
    WHERE UNIV_FISCAL_YR = 2011 - 1
/
.saveLastResult 'Format=Insert,
	File="/Users/kellerj/Desktop/temp/02_CA_ORG_REVERSION_T_2011.sql",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	StatementSeparator=/,
	QuoteIdentifier="\"",
	Schema=KULDBA,
	Table=CA_ORG_REVERSION_T,
	IncludeCreate=false' 
/
SELECT UNIV_FISCAL_YR, UNIV_FISCAL_PRD_CD, OBJ_ID, VER_NBR, UNIV_FISCAL_PRD_NM, ROW_ACTV_IND, BDGT_ROLLOVER_CD, UNIV_FSCPD_END_DT 
    FROM SH_ACCT_PERIOD_T
    WHERE UNIV_FISCAL_YR IN ( 2011, 2011 + 1 )
/
.saveLastResult 'Format=Insert,
	File="/Users/kellerj/Desktop/temp/03_SH_ACCT_PERIOD_T_2011.sql",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	StatementSeparator=/,
	QuoteIdentifier="\"",
	Schema=KULDBA,
	Table=SH_ACCT_PERIOD_T,
	IncludeCreate=false' 
/
SELECT UNIV_FISCAL_YR, FIN_SERIES_ID, OBJ_ID, VER_NBR, ROW_ACTV_IND 
    FROM CA_ICR_RATE_T
    WHERE UNIV_FISCAL_YR = 2011
/
.saveLastResult 'Format=Insert,
	File="/Users/kellerj/Desktop/temp/04_CA_ICR_RATE_T_2011.sql",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	StatementSeparator=/,
	QuoteIdentifier="\"",
	Schema=KULDBA,
	Table=CA_ICR_RATE_T,
	IncludeCreate=false' 
/
SELECT UNIV_FISCAL_YR, FIN_COA_CD, POS_BENEFIT_TYP_CD, OBJ_ID, VER_NBR, POS_FRNG_BENE_PCT, POS_FRNGBEN_OBJ_CD, ACTV_IND 
    FROM LD_BENEFITS_CALC_T
    WHERE UNIV_FISCAL_YR = 2011
/
.saveLastResult 'Format=Insert,
	File="/Users/kellerj/Desktop/temp/05_LD_BENEFITS_CALC_T_2011.sql",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	StatementSeparator=/,
	QuoteIdentifier="\"",
	Schema=KULDBA,
	Table=LD_BENEFITS_CALC_T,
	IncludeCreate=false' 
/
SELECT UNIV_FISCAL_YR, FIN_COA_CD, FIN_OBJECT_CD, OBJ_ID, VER_NBR, FIN_OBJ_CD_NM, FIN_OBJ_CD_SHRT_NM, FIN_OBJ_LEVEL_CD, RPTS_TO_FIN_COA_CD, RPTS_TO_FIN_OBJ_CD, FIN_OBJ_TYP_CD, FIN_OBJ_SUB_TYP_CD, HIST_FIN_OBJECT_CD, FIN_OBJ_ACTIVE_CD, FOBJ_BDGT_AGGR_CD, FOBJ_MNXFR_ELIM_CD, FIN_FED_FUNDED_CD, NXT_YR_FIN_OBJ_CD, RSCH_BDGT_CTGRY_CD, RSCH_OBJ_CD_DESC, RSCH_ON_CMP_IND 
    FROM CA_OBJECT_CODE_T
    WHERE UNIV_FISCAL_YR = 2011
/
.saveLastResult 'Format=Insert,
	File="/Users/kellerj/Desktop/temp/06_CA_OBJECT_CODE_T_2011.sql",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	StatementSeparator=/,
	QuoteIdentifier="\"",
	Schema=KULDBA,
	Table=CA_OBJECT_CODE_T,
	IncludeCreate=false' 
/
SELECT UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, OBJ_ID, VER_NBR, FIN_SUB_OBJ_CD_NM, FIN_SUBOBJ_SHRT_NM, FIN_SUBOBJ_ACTV_CD 
    FROM CA_SUB_OBJECT_CD_T
    WHERE UNIV_FISCAL_YR = 2011
/
.saveLastResult 'Format=Insert,
	File="/Users/kellerj/Desktop/temp/07_CA_SUB_OBJECT_CD_T_2011.sql",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	StatementSeparator=/,
	QuoteIdentifier="\"",
	Schema=KULDBA,
	Table=CA_SUB_OBJECT_CD_T,
	IncludeCreate=false' 
/
SELECT A21_LBR_RPT_NBR, UNIV_FISCAL_YR, OBJ_ID, VER_NBR, A21LBR_RPT_PRD_TTL, LBR_RPT_PRDSTAT_CD, LBR_ET_FSCL_YR, LBR_ET_FSCL_PRD_CD, A21_LBR_RPT_TYP_CD, A21LBR_RPT_RTRN_DT, LBR_RPT_BEG_FSCL_YR, LBR_RPT_BEG_FSCL_PRD_CD, LBR_RPT_END_FSCL_YR, LBR_RPT_END_FSCL_PRD_CD, ROW_ACTV_IND 
    FROM LD_A21_REPORT_T
    WHERE UNIV_FISCAL_YR = 2011
/
.saveLastResult 'Format=Insert,
	File="/Users/kellerj/Desktop/temp/08_LD_A21_REPORT_T_2011.sql",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	StatementSeparator=/,
	QuoteIdentifier="\"",
	Schema=KULDBA,
	Table=LD_A21_REPORT_T,
	IncludeCreate=false' 
/
SELECT UNIV_FISCAL_YR, FIN_COA_CD, ORG_CD, ORG_RVRSN_CTGRY_CD, OBJ_ID, VER_NBR, ORG_RVRSN_CD, ORG_RVRSN_OBJ_CD, ACTV_IND 
    FROM CA_ORG_RVRSN_DTL_T
    WHERE UNIV_FISCAL_YR = 2011 - 1
/
.saveLastResult 'Format=Insert,
	File="/Users/kellerj/Desktop/temp/09_CA_ORG_RVRSN_DTL_T_2011.sql",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	StatementSeparator=/,
	QuoteIdentifier="\"",
	Schema=KULDBA,
	Table=CA_ORG_RVRSN_DTL_T,
	IncludeCreate=false' 
/
SELECT UNIV_FISCAL_YR, FIN_COA_CD, FDOC_TYP_CD, FIN_BALANCE_TYP_CD, OBJ_ID, VER_NBR, FIN_OBJECT_CD 
    FROM GL_OFFSET_DEFN_T
    WHERE UNIV_FISCAL_YR = 2011
/
.saveLastResult 'Format=Insert,
	File="/Users/kellerj/Desktop/temp/10_GL_OFFSET_DEFN_T_2011.sql",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	StatementSeparator=/,
	QuoteIdentifier="\"",
	Schema=KULDBA,
	Table=GL_OFFSET_DEFN_T,
	IncludeCreate=false' 
/
SELECT UNIV_FISCAL_YR, FIN_SERIES_ID, AWRD_ICR_ENTRY_NBR, OBJ_ID, VER_NBR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, TRN_DEBIT_CRDT_CD, AWRD_ICR_RATE_PCT, ACCT_ICR_RATE_ACTV_IND 
    FROM CA_ICR_AUTO_ENTR_T
    WHERE UNIV_FISCAL_YR = 2011
/
.saveLastResult 'Format=Insert,
	File="/Users/kellerj/Desktop/temp/11_CA_ICR_AUTO_ENTR_T_2011.sql",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	StatementSeparator=/,
	QuoteIdentifier="\"",
	Schema=KULDBA,
	Table=CA_ICR_AUTO_ENTR_T,
	IncludeCreate=false' 
/
SELECT UNIV_FISCAL_YR, OBJ_ID, VER_NBR, FIN_COA_CD, ACCOUNT_NBR, INC_FIN_OBJ_CD, EXP_FIN_OBJ_CD, DV_DOMSTC_CHG_AMT, DV_FRGN_CHRG_AMT 
    FROM FP_DV_WIRE_CHRG_T
    WHERE UNIV_FISCAL_YR = 2011
/
.saveLastResult 'Format=Insert,
	File="/Users/kellerj/Desktop/temp/12_FP_DV_WIRE_CHRG_T_2011.sql",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	StatementSeparator=/,
	QuoteIdentifier="\"",
	Schema=KULDBA,
	Table=FP_DV_WIRE_CHRG_T,
	IncludeCreate=false' 
/
SELECT UNIV_FISCAL_YR, DV_DIEM_CNTRY_NM, OBJ_ID, VER_NBR, DV_DIEM_RT, DV_DIEM_CNTRY_TXT 
    FROM FP_DV_DIEM_T
    WHERE UNIV_FISCAL_YR = 2011
/
.saveLastResult 'Format=Insert,
	File="/Users/kellerj/Desktop/temp/13_FP_DV_DIEM_T_2011.sql",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	StatementSeparator=/,
	QuoteIdentifier="\"",
	Schema=KULDBA,
	Table=FP_DV_DIEM_T,
	IncludeCreate=false' 
/
SELECT UNIV_FISCAL_YR, FS_FUNC_CTRL_CD, OBJ_ID, VER_NBR, FS_FUNC_ACTIVE_IND 
    FROM FP_FSCL_YR_CTRL_T
    WHERE UNIV_FISCAL_YR = 2011
/
.saveLastResult 'Format=Insert,
	File="/Users/kellerj/Desktop/temp/14_FP_FSCL_YR_CTRL_T_2011.sql",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	StatementSeparator=/,
	QuoteIdentifier="\"",
	Schema=KULDBA,
	Table=FP_FSCL_YR_CTRL_T,
	IncludeCreate=false' 
/
SELECT UNIV_FISCAL_YR, FIN_COA_CD, FIN_OBJ_SUB_TYPE_CD, OBJ_ID, VER_NBR, CPTLZTN_FOBJ_CD, ACCUM_DEPR_FOBJ_CD, DEPR_EXP_FOBJ_CD, ROW_ACTV_IND 
    FROM CM_CPTLAST_OBJ_T
    WHERE UNIV_FISCAL_YR = 2011
/
.saveLastResult 'Format=Insert,
	File="/Users/kellerj/Desktop/temp/15_CM_CPTLAST_OBJ_T_2011.sql",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	StatementSeparator=/,
	QuoteIdentifier="\"",
	Schema=KULDBA,
	Table=CM_CPTLAST_OBJ_T,
	IncludeCreate=false' 
/
SELECT UNIV_FISCAL_YR, PRCS_FIN_COA_CD, PRCS_ORG_CD, OBJ_ID, VER_NBR, UNIV_FEIN_NBR, REFUND_FIN_OBJ_CD, UNIV_CLR_COA_CD, UNIV_CLR_ACCT_NBR, UNIV_CLR_SUBAC_NBR, UNIV_CLR_OBJECT_CD, UNIV_CLR_SUBOBJ_CD, PMT_CLR_FIN_OBJ_CD, AR_LOCKBOX_NBR, ROW_ACTV_IND, ORG_REMIT_ADDR_NM, ORG_REMIT_LN1_ADDR, ORG_REMIT_LN2_ADDR, ORG_REMIT_CITY_NM, ORG_REMIT_ST_CD, ORG_REMIT_ZIP_CD, ORG_CHCK_PAY_TO_NM, FDOC_INITIATOR_ID 
    FROM AR_SYS_INFO_T
    WHERE UNIV_FISCAL_YR = 2011
/
.saveLastResult 'Format=Insert,
	File="/Users/kellerj/Desktop/temp/16_AR_SYS_INFO_T_2011.sql",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	StatementSeparator=/,
	QuoteIdentifier="\"",
	Schema=KULDBA,
	Table=AR_SYS_INFO_T,
	IncludeCreate=false' 
/
SELECT UNIV_FISCAL_YR, FIN_COA_CD, ORG_CD, OBJ_ID, VER_NBR, ORG_LT_CHRG_OBJ_CD, INV_FIN_COA_CD, INV_ACCT_NBR, INV_SUB_ACCT_NBR, INV_FIN_OBJ_CD, INV_FIN_SUB_OBJ_CD, INV_PROJECT_CD, INV_ORG_REF_ID, PMT_FIN_COA_CD, PMT_ACCT_NBR, PMT_SUB_ACCT_NBR, PMT_FIN_OBJ_CD, PMT_FIN_SUB_OBJ_CD, PMT_PROJECT_CD, PMT_ORG_REF_ID, WRITEOFF_FIN_COA_CD, WRITEOFF_ACCT_NBR, WRITEOFF_SUB_ACCT_NBR, WRITEOFF_FIN_OBJ_CD, WRITEOFF_FIN_SUB_OBJ_CD, WRITEOFF_PROJECT_CD, WRITEOFF_ORG_REF_ID 
    FROM AR_ORG_ACCTGDFLT_T
    WHERE UNIV_FISCAL_YR = 2011
/
.saveLastResult 'Format=Insert,
	File="/Users/kellerj/Desktop/temp/17_AR_ORG_ACCTGDFLT_T_2011.sql",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	StatementSeparator=/,
	QuoteIdentifier="\"",
	Schema=KULDBA,
	Table=AR_ORG_ACCTGDFLT_T,
	IncludeCreate=false' 
/

SELECT UNIV_FISCAL_YR, A21_LBR_RPT_TYP_CD, ERNCD, PAYGROUP, OBJ_ID, VER_NBR, ROW_ACTV_IND 
    FROM LD_A21_RPT_EARN_PAY_T
    WHERE UNIV_FISCAL_YR = 2011
/
.saveLastResult 'Format=Insert,
	File="/Users/kellerj/Desktop/temp/18_LD_A21_RPT_EARN_PAY_T_2011.sql",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	StatementSeparator=/,
	QuoteIdentifier="\"",
	Schema=KULDBA,
	Table=LD_A21_RPT_EARN_PAY_T,
	IncludeCreate=false' 
/
SELECT UNIV_FISCAL_YR, A21_LBR_RPT_NBR, LBR_RPT_POSOBJ_CD, OBJ_ID, VER_NBR, ROW_ACTV_IND 
    FROM LD_A21_RPT_POS_T
    WHERE UNIV_FISCAL_YR = 2011
/
.saveLastResult 'Format=Insert,
	File="/Users/kellerj/Desktop/temp/19_LD_A21_RPT_POS_T_2011.sql",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	StatementSeparator=/,
	QuoteIdentifier="\"",
	Schema=KULDBA,
	Table=LD_A21_RPT_POS_T,
	IncludeCreate=false' 
/

SELECT UNIV_FISCAL_YR, FIN_COA_CD, FIN_OBJECT_CD, OBJ_ID, VER_NBR, DTL_POS_REQ_CD, FIN_OBJ_HRS_REQ_CD, FIN_OBJ_PAY_TYP_CD, FINOBJ_FRNGSLRY_CD, POS_OBJ_GRP_CD, ACTV_IND 
    FROM LD_LABOR_OBJ_T
    WHERE UNIV_FISCAL_YR = 2011
/
.saveLastResult 'Format=Insert,
	File="/Users/kellerj/Desktop/temp/20_LD_LABOR_OBJ_T_2011.sql",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	StatementSeparator=/,
	QuoteIdentifier="\"",
	Schema=KULDBA,
	Table=LD_LABOR_OBJ_T,
	IncludeCreate=false' 
/
SELECT UNIV_FISCAL_YR, FIN_COA_CD, FIN_OBJECT_CD, FINOBJ_BENE_TYP_CD, OBJ_ID, VER_NBR, ACTV_IND 
    FROM LD_LBR_OBJ_BENE_T
    WHERE UNIV_FISCAL_YR = 2011
/
.saveLastResult 'Format=Insert,
	File="/Users/kellerj/Desktop/temp/21_LD_LBR_OBJ_BENE_T_2011.sql",
	Overwrite=true,
	Platform=Unix (LF) - EOL,
	StatementSeparator=/,
	QuoteIdentifier="\"",
	Schema=KULDBA,
	Table=LD_LBR_OBJ_BENE_T,
	IncludeCreate=false' 
/

