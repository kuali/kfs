insert into krim_perm_t values ('363', sys_guid(), 1, '23', 'Look Up Records', '', 'Y', 'KFS-PDP');
insert into krim_perm_attr_data_t values ('550', sys_guid(), 1, '363', '10', '4', 'KFS-PDP');
insert into krim_perm_attr_data_t values ('551', sys_guid(), 1, '363', '10', '5', 'PurchasingPaymentDetail');
insert into krim_role_perm_t values ('666', sys_guid(), 1, '54', '363', 'Y');
insert into krim_role_perm_t values ('667', sys_guid(), 1, '32', '363', 'Y');

delete from krim_role_perm_t where perm_id = '88';

insert into krim_role_perm_t values ('668', sys_guid(), 1, '54', '88', 'Y');
insert into krim_role_perm_t values ('669', sys_guid(), 1, '32', '88', 'Y');
commit;
delete from krns_parm_t
where parm_nm = 'COST_SHARE_ENCUMBRANCE_DOCUMENT_TYPES';
commit;
update ca_object_code_t obj
set FIN_OBJ_ACTIVE_CD='Y'
where exists (select *
from gl_encumbrance_t
where fin_object_cd = obj.fin_object_cd)
and fin_obj_active_cd='N'
;
update ca_object_code_t
set univ_fiscal_yr = 2009
where univ_fiscal_yr = 2006
and fin_object_cd in ('2001','2401')
;
update LD_CSF_TRACKER_T set univ_fiscal_yr = 2009
where univ_fiscal_yr = 2007
;
update LD_JULY1_POS_FND_T set univ_fiscal_yr = 2009
where univ_fiscal_yr = 2007
;
update LD_BCN_CSF_TRCKR_T set univ_fiscal_yr = 2009
where univ_fiscal_yr = 2007
;
update LD_PNDBC_APPTFND_T set univ_fiscal_yr = 2009
where univ_fiscal_yr = 2007
;
update LD_BCN_POS_T  set univ_fiscal_yr = 2009
where univ_fiscal_yr = 2007
;
update gl_entry_t set univ_fiscal_yr = 2009
where univ_fiscal_yr = 2007
;
update gl_balance_t set univ_fiscal_yr = 2009
where univ_fiscal_yr = 2007
;
update gl_encumbrance_t set univ_fiscal_yr = 2009
where univ_fiscal_yr = 2007
;
update gl_acct_balances_t set univ_fiscal_yr = 2009
where univ_fiscal_yr = 2007
;
update gl_sf_balances_t set univ_fiscal_yr = 2009
where univ_fiscal_yr = 2007
;
update ld_ldgr_entr_t set univ_fiscal_yr = 2009
where univ_fiscal_yr = 2007
;
update ld_ldgr_bal_t set univ_fiscal_yr = 2009
where univ_fiscal_yr = 2007
;
update ld_csf_tracker_t set univ_fiscal_yr = 2009
where univ_fiscal_yr = 2007
;
update ld_july1_pos_fnd_t set univ_fiscal_yr = 2009
where univ_fiscal_yr = 2007
;
update gl_reversal_t  set univ_fiscal_yr = 2009
where univ_fiscal_yr = 2007
;
delete LD_BCN_CSF_TRCKR_T
;
delete LD_PNDBC_APPTFND_T
;
commit;
insert into krns_parm_t (
SELECT 'KFS-SYS', 'Document', 'ACCOUNTING_LINE_IMPORT', SYS_GUID(), 1,
       'CONFG', 'default.htm?turl=WordDocuments%2Faccountinglineimporttemplates.htm',
        'Relative URL for the accounting line import help. ', 'A'
  FROM dual);
  commit;
delete from LD_A21_RPT_POS_T where univ_fiscal_yr < 2009;
delete from LD_A21_REPORT_T where univ_fiscal_yr < 2009;
delete from SH_ACCT_PERIOD_T where UNIV_FISCAL_YR < 2009;
delete from SH_UNIV_DATE_T where univ_fiscal_yr < 2009;
delete from FP_DV_DIEM_T where univ_fiscal_yr < 2009;
delete from FP_DV_WIRE_CHRG_T where univ_fiscal_yr < 2009;
delete from AR_ORG_ACCTGDFLT_T where univ_fiscal_yr < 2009;
delete from AR_SYS_INFO_T where univ_fiscal_yr < 2009;
delete from FP_FSCL_YR_CTRL_T where univ_fiscal_yr < 2009;
delete from CA_ORG_RVRSN_DTL_T where univ_fiscal_yr < 2009;
delete from CA_ORG_REVERSION_T where univ_fiscal_yr < 2009;
delete from CA_SUB_OBJECT_CD_T where univ_fiscal_yr < 2009;
delete from LD_CSF_TRACKER_T where univ_fiscal_yr < 2009;
delete from LD_JULY1_POS_FND_T where univ_fiscal_yr < 2009;
delete from GL_ENCUMBRANCE_T where univ_fiscal_yr < 2009;
delete from LD_BCN_CSF_TRCKR_T where univ_fiscal_yr < 2009;
delete from LD_PNDBC_APPTFND_T where univ_fiscal_yr < 2009;
delete from GL_OFFSET_DEFN_T where univ_fiscal_yr < 2009;
delete from CA_OBJECT_CODE_T where univ_fiscal_yr < 2009;
delete from CA_ICR_AUTO_ENTR_T where univ_fiscal_yr < 2009;
delete from CA_ICR_RATE_T where univ_fiscal_yr < 2009;
delete from LD_LBR_OBJ_BENE_T where univ_fiscal_yr < 2009;
delete from LD_BENEFITS_CALC_T where univ_fiscal_yr < 2009;
delete from LD_LABOR_OBJ_T where univ_fiscal_yr < 2009;

delete from CM_CPTLAST_OBJ_T where univ_fiscal_yr < 2009;
delete from LD_A21_RPT_EARN_PAY_T where univ_fiscal_yr < 2009;

delete from LD_BCN_POS_T where univ_fiscal_yr < 2009;

delete from FS_OPTION_T where univ_fiscal_yr < 2009;
update krns_parm_t
set txt = '2009'
where nmspc_cd = 'KFS-BC' and parm_dtl_typ_cd = 'GenesisBatchStep' and parm_nm =
 'SOURCE_FISCAL_YEAR'
;
update krns_parm_t
set txt = '2009'
where nmspc_cd = 'KFS-EC' and parm_dtl_typ_cd = 'EffortCertificationCreateStep'
and parm_nm = 'CREATE_FISCAL_YEAR'
;
update krns_parm_t
set txt = '2009'
where nmspc_cd = 'KFS-EC' and parm_dtl_typ_cd = 'EffortCertificationExtractStep'
 and parm_nm = 'RUN_FISCAL_YEAR'
;
update krns_parm_t
set txt = '2009'
where nmspc_cd = 'KFS-GL' and parm_dtl_typ_cd = 'Batch' and parm_nm = 'ANNUAL_CL
OSING_FISCAL_YEAR'
;
update krns_parm_t
set txt = '2001'
where nmspc_cd = 'KFS-GL' and parm_dtl_typ_cd = 'PurgeAccountBalancesStep' and p
arm_nm = 'PRIOR_TO_YEAR'
;
update krns_parm_t
set txt = '2001'
where nmspc_cd = 'KFS-GL' and parm_dtl_typ_cd = 'PurgeBalanceStep' and parm_nm =
 'PRIOR_TO_YEAR'
;
update krns_parm_t
set txt = '2004'
where nmspc_cd = 'KFS-GL' and parm_dtl_typ_cd = 'PurgeCollectorDetailStep' and p
arm_nm = 'PRIOR_TO_YEAR'
;
update krns_parm_t
set txt = '2004'
where nmspc_cd = 'KFS-GL' and parm_dtl_typ_cd = 'PurgeEncumbranceStep' and parm_
nm = 'PRIOR_TO_YEAR'
;
update krns_parm_t
set txt = '2004'
where nmspc_cd = 'KFS-GL' and parm_dtl_typ_cd = 'PurgeEntryStep' and parm_nm = '
PRIOR_TO_YEAR'
;
update krns_parm_t
set txt = '2001'
where nmspc_cd = 'KFS-GL' and parm_dtl_typ_cd = 'PurgeSufficientFundBalancesStep
' and parm_nm = 'PRIOR_TO_YEAR'
;
update krns_parm_t
set txt = '2009'
where nmspc_cd = 'KFS-GL' and parm_dtl_typ_cd = 'SufficientFundsRebuilderStep' a
nd parm_nm = 'FISCAL_YEAR'
;
update krns_parm_t
set txt = '2001'
where nmspc_cd = 'KFS-LD' and parm_dtl_typ_cd = 'LaborPurgeBalanceStep' and parm
_nm = 'PRIOR_TO_YEAR'
;
update krns_parm_t
set txt = '2004'
where nmspc_cd = 'KFS-LD' and parm_dtl_typ_cd = 'LaborPurgeEntryStep' and parm_n
m = 'PRIOR_TO_YEAR'
;
update krns_parm_t
set txt = '2009'
where nmspc_cd = 'KFS-LD' and parm_dtl_typ_cd = 'LaborYearEndBalanceForwardStep'
 and parm_nm = 'FISCAL_YEAR_SELECTION'
;
update krns_parm_t
set txt = '2010'
where nmspc_cd = 'KFS-SYS' and parm_dtl_typ_cd = 'FiscalYearMakerStep' and parm_
nm = 'SOURCE_FISCAL_YEAR'
;
commit;

