--KFSMI-4177
CREATE OR REPLACE VIEW pur_po_itm_tot_enc_v AS
(select a.FDOC_NBR AS fdoc_nbr,
        sum(a.ITM_OSTND_ENC_AMT) AS TOTAL_ENCUMBRANCE
from pur_po_itm_t a
group by a.FDOC_NBR);
--DONE
--KFSMI-3920
Drop table FS_TAX_DISTRICT_T;
--done
--KFSMI-3902
drop table FP_DV_OWNR_TYP_T;
--done
--KFSMI-3801
Alter Table AR_SYS_INFO_T
drop (WIRE_COA_CD, WIRE_ACCT_NBR, WIRE_SUB_ACCT_NBR, WIRE_OBJ_CD, WIRE_SUB_OBJ_CD); 
--done
--KFSMI-3916
 delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'ACADEMIC_YEAR_SUBDIVISION_NAMES';
 delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'BASE_CODE_DEFAULT_VALUE';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'COST_SHARE_PERMISSION';
 delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'COST_SHARE_PERMISSION_CODE';
 delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'DEFAULT_BUDGET_TASK_NAME';
 delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'INDIRECT_COST_MAX_MANUAL_RATE';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'INDIRECT_COST_PROVIDED_MANUALLY';
 delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'INDIRECT_COST_PROVIDED_SYSTEM';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'MANUAL_RATE_INDICATOR_DEFAULT_VALUE';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'MAXIMUM_NUMBER_MODULAR_PERIODS';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'MAXIMUM_NUMBER_OF_PERIODS';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'MAXIMUM_NUMBER_OF_TASKS';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'MAXIMUM_PERIOD_LENGTH';
 delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'MAX_INFLATION_RATE';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'MINIMUM_NUMBER_OF_PERIODS';
 delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'MINIMUM_NUMBER_OF_TASKS';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'NEW_PERIOD_IDENTIFIER';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'NUMBER_OF_ACADEMIC_YEAR_SUBDIVISIONS';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'OUTPUT_GENERIC_BY_PERIOD_XSL_FILENAME';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'OUTPUT_GENERIC_BY_TASK_XSL_FILENAME';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'OUTPUT_NIH2590_XSL_FILENAME';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'OUTPUT_NIH398_XSL_FILENAME';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'OUTPUT_NIH_MODULAR_XSL_FILENAME';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'OUTPUT_NSF_SUMMARY_XSL_FILENAME';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'OUTPUT_PATH_PREFIX';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'OUTPUT_SF424_XSL_FILENAME';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'PERSONNEL_ACADEMIC_YEAR_APPOINTMENT_TYPE';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'PERSONNEL_FULL_YEAR_APPOINTMENT_TYPES';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'PERSONNEL_GRADUATE_RESEARCH_ASSISTANT_APPOINTMENT_TYPES';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'PERSONNEL_HOURLY_APPOINTMENT_TYPES';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'PERSONNEL_SUMMER_GRID_APPOINTMENT_TYPE';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'PERSONNEL_SUMMER_GRID_APPOINTMENT_TYPES';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'PROJECT_DIRECTOR_ORG_PERMISSION';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'PROJECT_DIRECTOR_PERMISSION';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm = 'PURPOSE_CODE_DEFAULT_VALUE';
 delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'APPROVALS_DEFAULT_WORDING';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'APPROVALS_INITIATOR_WORDING';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'APPROVALS_PROJECT_DIRECTOR_WORDING';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'CREATE_PROPOSAL_PROJECT_TYPES';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'OUTPUT_PATH_PREFIX';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'OUTPUT_XSL_FILENAME';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PERSON_ROLE_CODE_CONTACT_PERSON';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PERSON_ROLE_CODE_CO_PROJECT_DIRECTOR';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PERSON_ROLE_CODE_OTHER';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PERSON_ROLE_CODE_PROJECT_DIRECTOR';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PROJECT_TYPES';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PROJECT_TYPE_BUDGET_REVISION_ACTIVE';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PROJECT_TYPE_BUDGET_REVISION_PENDING';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PROJECT_TYPE_NEW';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PROJECT_TYPE_OTHER';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PROJECT_TYPE_TIME_EXTENTION';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PURPOSE_OTHER';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'PURPOSE_RESEARCH';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'ROUTE_TO_COST_SHARE_ORGANIZATIONS_IND';
delete  from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm = 'SUBMISSION_TYPE_CHANGE'; 
delete from krew_doc_hdr_t where doc_typ_id in 
(select doc_typ_id from krew_doc_typ_t
where doc_typ_nm in('BudgetDocument', 'RoutingFormDocument', 'APPT','PRSN','DDT','GAR','IDCL','KEYW','NPC','NPOC','NPSC','PRJT','PRPS','QNT','RTCM')); 
delete from krew_doc_typ_attr_t where doc_typ_id in 
(select doc_typ_id from krew_doc_typ_t
where doc_typ_nm in('BudgetDocument', 'RoutingFormDocument', 'APPT','PRSN','DDT','GAR','IDCL','KEYW','NPC','NPOC','NPSC','PRJT','PRPS','QNT','RTCM'));
delete from krew_doc_typ_plcy_reln_t where doc_typ_id in 
(select doc_typ_id from krew_doc_typ_t
where doc_typ_nm in('BudgetDocument', 'RoutingFormDocument', 'APPT','PRSN','DDT','GAR','IDCL','KEYW','NPC','NPOC','NPSC','PRJT','PRPS','QNT','RTCM')); 
delete from krew_doc_typ_proc_t where doc_typ_id in 
(select doc_typ_id from krew_doc_typ_t
where doc_typ_nm in('BudgetDocument', 'RoutingFormDocument', 'APPT','PRSN','DDT','GAR','IDCL','KEYW','NPC','NPOC','NPSC','PRJT','PRPS','QNT','RTCM')); 
delete from krew_rte_node_t where doc_typ_id in 
(select doc_typ_id from krew_doc_typ_t
where doc_typ_nm in('BudgetDocument', 'RoutingFormDocument', 'APPT','PRSN','DDT','GAR','IDCL','KEYW','NPC','NPOC','NPSC','PRJT','PRPS','QNT','RTCM')); 
delete from krew_doc_typ_t where doc_typ_nm='BudgetDocument';
delete from krew_doc_typ_t where doc_typ_nm='RoutingFormDocument';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='APPT';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='PRSN';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='DDT';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='GAR';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='IDCL';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='KEYW';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='NPC';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='NPOC';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='NPSC';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='PRJT';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='PRPS';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='QNT';
DELETE FROM KREW_DOC_TYP_T WHERE DOC_TYP_NM='RTCM ';
--KFSMI-4251
update krim_perm_attr_data_t
set attr_val = 'collectorXmlInputFileType'
where attr_data_id = '103';
insert into krim_perm_t
(SELECT '385', sys_guid(), 1, '33', 'KFS-GL', 'Upload Batch Input File(s)',
       'Authorizes user to access the Enterprise Feed Upload page.', 'Y'
  FROM dual);
insert into krim_perm_attr_data_t
 ( SELECT '586', sys_guid(), 1, '385', '15',
       1, 'collectorFlatFileInputFileType'
  FROM dual);
--DONE
--KFSMI-4076
  drop table pur_po_itm_use_tax_t
;
CREATE TABLE pur_po_itm_use_tax_t
    (fdoc_nbr                       VARCHAR2(14) NOT NULL,
    po_itm_use_tax_id              NUMBER(10,0) NOT NULL,
    obj_id                         VARCHAR2(36) NOT NULL,
    ver_nbr                        NUMBER(8,0) DEFAULT 1 NOT NULL,
    po_itm_id                      NUMBER(10,0),
    rate_cd                        VARCHAR2(10) NOT NULL,
    tax_amt                        NUMBER(19,4),
    fin_coa_cd                     VARCHAR2(2) NOT NULL,
    account_nbr                    VARCHAR2(7) NOT NULL,
    fin_object_cd                  VARCHAR2(4) NOT NULL)
;
ALTER TABLE pur_po_itm_use_tax_t
ADD CONSTRAINT pur_po_itm_use_tax_tc0 UNIQUE (obj_id)
;

ALTER TABLE pur_po_itm_use_tax_t
ADD CONSTRAINT pur_po_itm_use_tax_tp1 PRIMARY KEY (fdoc_nbr, po_itm_use_tax_id)
;
--done
--KFSMI-4253
drop table GL_ENTRY_MT;
drop table GL_PENDING_ENTRY_MT;
--done
--kfsmi-3916
Drop Table ER_BDGT_NPRS_T;
Drop Table ER_USR_APPT_TSK_PRD_T;
Drop Table ER_USR_APPT_TSK_T;
Drop Table ER_BDGT_USR_T;
Drop Table ER_ADHOC_PRSN_T;
Drop Table ER_ADHOC_ORG_T;
Drop Table ER_AGNCY_EXTNS_T;
Drop Table ER_BDGT_MOD_T;
Drop Table ER_BDGT_PRD_3RD_CST_SHR_T;
Drop Table ER_BDGT_3RD_PRTY_CST_SHR_T;
Drop Table ER_BDGT_PRD_INST_CST_SHR_T;
Drop Table ER_BDGT_INST_CST_SHR_T;
Drop Table ER_INST_CST_SHR_PSNL_T;
Drop Table ER_BDGT_IDC_T;
Drop Table ER_BDGT_IDC_LU_T;
Drop Table ER_IDC_LU_T;
Drop Table ER_BDGT_BASE_CD_T;
Drop Table ER_BDGT_TYP_CD_T;
Drop Table ER_BDGT_TSK_PRD_IDC_T;
Drop Table ER_BDGT_MOD_PRD_T;
Drop Table ER_BDGT_DOC_T;
Drop Table ER_BDGT_TSK_T;
Drop Table ER_BDGT_PRD_T;
Drop Table ER_BDGT_FRNG_RT_T;
Drop Table ER_BDGT_GRAD_ASST_RT_T;
Drop Table ER_RF_BDGT_T;
Drop Table ER_NPRS_OBJ_CD_T;
Drop Table ER_NPRS_CTGRY_CD_T;
Drop Table ER_NPRS_SUB_CTGRY_CD_T;
Drop Table ER_GRAD_ASST_RT_T;
Drop Table ER_APPT_TYP_EFF_DT_T;
Drop Table ER_APPT_TYP_T;
Drop Table ER_RF_SUBCNR_T;
Drop Table ER_RF_ORG_T;
Drop Table ER_RF_KEYWRD_T;
Drop Table ER_RF_PRPS_T;
Drop Table ER_PRPS_T;
Drop Table ER_RF_INST_CST_SHR_T;
Drop Table ER_RF_OTHR_CST_SHR_T;
Drop Table ER_RF_CI_T;
Drop Table ER_RF_RSRCH_RSK_STDY_T;
Drop Table ER_RF_PROJ_TYP_T;
Drop Table ER_PROJ_TYP_T;
Drop Table ER_RF_RSRCH_TYP_CD_T;
Drop Table ER_RSRCH_TYP_CD_T;
Drop Table ER_KEYWRD_T;
Drop Table ER_CG_PRPSL_T;
Drop Table ER_RF_RSRCH_RSK_T;
Drop Table ER_RF_PSNL_T;
Drop Table ER_RF_PRSN_ROLE_T ;
Drop Table ER_PRSN_ROLE_T;
Drop Table ER_RF_ORG_CRDT_PCT_T;
Drop Table ER_RF_QSTN_TYP_T;
Drop Table ER_QSTN_TYP_T;
Drop Table ER_RF_DUE_DT_TYP_T;
Drop Table ER_DUE_DT_TYP_T;
Drop Table ER_RF_AGNCY_T;
alter table ER_RF_DOC_T DROP CONSTRAINT ER_RF_DOC_TR1;
Drop Table ER_BDGT_T;
Drop Table ER_RF_DOC_T;
Drop Table ER_RF_STAT_T;
--DONE
--KFSMI-3919
delete from KRIM_ROLE_MBR_T
    WHERE role_id IN (
        select role_id from krim_role_t
            WHERE kim_typ_id IN ( '55', '49', '63' )
)
;
delete from KRIM_ROLE_PERM_T
    WHERE role_id IN (
        select role_id from krim_role_t
            WHERE kim_typ_id IN ( '55', '49', '63' )
)
;
delete from krim_role_t
    WHERE kim_typ_id IN ( '55', '49', '63' )
;
--done
--KFSMI-4207
delete from KRNS_PARM_T 
where nmspc_cd = 'KFS-PDP' 
and parm_nm = 'DISBURSEMENT_CANCELLATION_TO_EMAIL_ADDRESSES'
;
--done
--KFSMI-4241
create table temp_t as select * from gl_entry_hist_t;
drop table gl_entry_hist_t;
CREATE TABLE gl_entry_hist_t
    (univ_fiscal_yr                 NUMBER(4,0) NOT NULL,
    fin_coa_cd                     VARCHAR2(2) NOT NULL,
    fin_object_cd                  VARCHAR2(4) NOT NULL,
    fin_balance_typ_cd             VARCHAR2(2) NOT NULL,
    univ_fiscal_prd_cd             VARCHAR2(2) NOT NULL,
    trn_debit_crdt_cd              VARCHAR2(1) ,
    ver_nbr                        NUMBER(8,0) DEFAULT 1 NOT NULL,
    trn_ldgr_entr_amt              NUMBER(19,2),
    row_cnt                        NUMBER(7,0))
;
ALTER TABLE gl_entry_hist_t
ADD CONSTRAINT gl_entry_hist_tp1 unique (univ_fiscal_yr, fin_coa_cd, 
  fin_object_cd, fin_balance_typ_cd, univ_fiscal_prd_cd, trn_debit_crdt_cd)
;
ALTER TABLE gl_entry_hist_t
ADD CONSTRAINT gl_entry_hist_tr2 FOREIGN KEY (univ_fiscal_yr, fin_coa_cd, 
  fin_object_cd)
REFERENCES ca_object_code_t (univ_fiscal_yr,fin_coa_cd,fin_object_cd)
;
ALTER TABLE gl_entry_hist_t
ADD CONSTRAINT gl_entry_hist_tr4 FOREIGN KEY (univ_fiscal_yr, univ_fiscal_prd_cd)
REFERENCES sh_acct_period_t (univ_fiscal_yr,univ_fiscal_prd_cd)
;
ALTER TABLE gl_entry_hist_t
ADD CONSTRAINT gl_entry_hist_tr5 FOREIGN KEY (fin_balance_typ_cd)
REFERENCES ca_balance_type_t (fin_balance_typ_cd)
;
ALTER TABLE gl_entry_hist_t
ADD CONSTRAINT gl_entry_hist_tr3 FOREIGN KEY (univ_fiscal_yr)
REFERENCES fs_option_t (univ_fiscal_yr)
;
ALTER TABLE gl_entry_hist_t
ADD CONSTRAINT gl_entry_hist_tr1 FOREIGN KEY (fin_coa_cd)
REFERENCES ca_chart_t (fin_coa_cd)
;
insert into gl_entry_hist_t
select * from temp_t
;
drop table temp_t
;
--done