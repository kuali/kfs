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
--KFSMI-3888 fixing typo
update krim_rsp_attr_data_t
set attr_val = 'LDYE'
where attr_data_id = 459; 
--done
--KFSMI-3932
update krim_role_t
set desc_txt = 'Users who can initiate CAM transactional and maintenance documents, create new records using the asset document, maintain asset locations and modify accounting lines on invoice documents.'
where nmspc_cd = 'KFS-CAM'
and role_nm = 'Processor';
update krim_role_t
set desc_txt = 'Users identified as part of an AR billing organization. They have access to the basic functions of the KFS AR module such as creating invoices or customers.'
where nmspc_cd = 'KFS-AR'
and role_nm = 'Biller';
update krim_role_t
set desc_txt = 'Users that manage the KFS AR module. They receive Accounts Receivable transactional and maintenance documents for approval.'
where nmspc_cd = 'KFS-AR'
and role_nm = 'Manager';
update krim_role_t
set desc_txt = 'Users associated with KFS AR processing organizations. They can initiate Application and Cash control documents and add new records to the Organization Options and Organization Accounting Defaults tables.'
where nmspc_cd = 'KFS-AR'
and role_nm = 'Processor';
update krim_role_t
set desc_txt = 'Users who receive Invoice and Invoice Recurrence documents for approval.'
where nmspc_cd = 'KFS-AR'
and role_nm = 'Invoice Recurrence Instance Reviewer';
update krim_role_t
set desc_txt = 'Users authorized to create Invoice Recurrence documents.'
where nmspc_cd = 'KFS-AR'
and role_nm = 'Invoice Recurrence Creator';
update krim_role_t
set desc_txt = 'Users authorized to edit Budget Construction Documents.'
where nmspc_cd = 'KFS-BC'
and role_nm = 'Document Editor';
update krim_role_t
set desc_txt = 'Users who have view-only access to Budget Construction Documents.'
where nmspc_cd = 'KFS-BC'
and role_nm = 'Document Viewer';
update krim_role_t
set desc_txt = 'Users who can use the Organization Salary Setting, import/export payrate and unlock options on the Budget Construction Document.'
where nmspc_cd = 'KFS-BC'
and role_nm = 'Processor';
update krim_role_t
set desc_txt = 'Users that manage the KFS-CAM module. They can initiate Barcode Inventory Error documents and have access to take restricted actions on assets and modify fields on the asset document that other users do not.'
where nmspc_cd = 'KFS-CAM'
and role_nm = 'Manager';
update krim_role_t
set desc_txt = 'Users who can initiate CAM transactional and maintenance documents, create new records using the asset document, maintain asset locations and modify accounting lines on invoice documents.'
where nmspc_cd = 'KFS-CAM'
and role_nm = 'Processor';
update krim_role_t
set desc_txt = 'Users borrowing assets on the Equipment Loan/Return document.'
where nmspc_cd = 'KFS-CAM'
and role_nm = 'Asset Borrower';
update krim_role_t
set desc_txt = 'Users who receive  workflow action requests when Proposal or Award documents involve research risk.'
where nmspc_cd = 'KFS-CG'
and role_nm = 'Research Risk Reviewer';
update krim_role_t
set desc_txt = 'Users authorized to work the Cash Management Document and verify Cash Receipt documents for a given campus.'
where nmspc_cd = 'KFS-FP'
and role_nm = 'Cash Manager';
update krim_role_t
set desc_txt = 'Users who receive workflow action requests for Disbursement Vouchers based on the campus code associated with the initiator of the document.'
where nmspc_cd = 'KFS-FP'
and role_nm = 'Disbursement Manager';
update krim_role_t
set desc_txt = 'Users authorized to use the Service Billing document and enter specified accounts on the "Income" side of the document.'
where nmspc_cd = 'KFS-FP'
and role_nm = 'Service Bill Processor';
update krim_role_t
set desc_txt = 'Users who receive workflow action requests for Disbursement Vouchers for travel payment reasons and can edit the accounting line and Non-Employee Travel Expense or Pre-Paid Travel Expenses tabs.'
where nmspc_cd = 'KFS-FP'
and role_nm = 'Travel Manager';
update krim_role_t
set desc_txt = 'Users who receive workflow action requests for Disbursement Vouchers with specified payment methods and can edit the accounting lines and Wire Transfer and Foreign Draft tabs.'
where nmspc_cd = 'KFS-FP'
and role_nm = 'Disbursement Method Reviewer';
update krim_role_t
set desc_txt = 'Users authorized to initiate Cash Receipt documents. This role exists to exclude Cash Managers from being able to initiate Cash Receipt documents. You do not need to add explicit members to this role to accomplish this exclusion.'
where nmspc_cd = 'KFS-FP'
and role_nm = 'Cash Receipt Initiator';
update krim_role_t
set desc_txt = 'Users that manage the KFS-FP module. This role has no inherent permissions or responsibilities.'
where nmspc_cd = 'KFS-FP'
and role_nm = 'Financial Processing Manager';
update krim_role_t
set desc_txt = 'Users authorized to use the Collector Upload screen.'
where nmspc_cd = 'KFS-GL'
and role_nm = 'Interdepartmental Billing Processor';
update krim_role_t
set desc_txt = 'Users associated with PDP customers that can use the  Payment File Batch Upload screen and have basic PDP inquiry access. '
where nmspc_cd = 'KFS-PDP'
and role_nm = 'Customer Contact';
update krim_role_t
set desc_txt = 'Users who can cancel or hold payments reset locked format processes and view unmasked bank routing and account numbers in PDP.'
where nmspc_cd = 'KFS-PDP'
and role_nm = 'Manager';
update krim_role_t
set desc_txt = 'Users who can set payments for immediate pay and use the Format Checks/ACH screen in PDP.'
where nmspc_cd = 'KFS-PDP'
and role_nm = 'Processor';
update krim_role_t
set desc_txt = 'Accounts Payable users who can initiate Payment Requests and Credit Memo documents.  They also have several permissions related to processing these document types and receive workflow action requests for them. '
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Accounts Payable Processor';
update krim_role_t
set desc_txt = 'Users who receive workflow action requests for Purchasing transactional documents that contain a specific commodity code and campus combination.'
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Commodity Reviewer';
update krim_role_t
set desc_txt = 'Users who receive incomplete Requisition documents for completion for a given Chart and Organization.'
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Content Reviewer';
update krim_role_t
set desc_txt = 'Contract Managers review and approve Purchase Order documents. A Purchase Order is assigned to a given Contract Manager for their review and approval.'
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Contract Manager';
update krim_role_t
set desc_txt = 'This role represents central or campus Purchasing staff. They have additional permissions for and receive action requests for most Purchasing document types as well as receiving action requests for Disbursement Vouchers paying PO Type Vendors.'
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Purchasing Processor';
update krim_role_t
set desc_txt = 'Users authorized to view KFS-PURAP documents identified with a specific Sensitive Data Code. '
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Sensitive Data Viewer';
update krim_role_t
set desc_txt = 'Users who wish to receive workflow action requests for KFS-PURAP documents that involve a specific account number and sub-account number.'
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Sub-Account Reviewer';
update krim_role_t
set desc_txt = 'Identifies the user who routed the source document (Requisition) for a KFS-PURAP document.'
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Source Document Router';
update krim_role_t
set desc_txt = 'Central administration users charged with reviewing Purchase Order documents that exceed an account''s sufficient funds balance.'
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Budget Reviewer';
update krim_role_t
set desc_txt = 'This role houses other roles and indicates which of those can view KFS-PURAP documents that have been identified as potentially sensitive. '
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Potentially Sensitive Document User';
update krim_role_t
set desc_txt = 'A role that derives the users who initiated or received a workflow action request for a sensitive KFS-PURAP document.'
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Sensitive Related Document Initiator Or Reviewer';
update krim_role_t
set desc_txt = 'This role derives users who placed a Payment Request or Credit Memo on hold or canceled it in order to determine who can remove those actions. '
where nmspc_cd = 'KFS-PURAP'
and role_nm = 'Payment Request Hold / Cancel Initiator';
update krim_role_t
set desc_txt = 'Users who can use the Electronic Fund Transfer screen and use DI or YEDI documents to claim those funds.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Treasury Manager';
update krim_role_t
set desc_txt = 'An optional role that allows users to receive workflow action requests for documents of a specified type that contain accounts belonging to a specified chart and organization (including the organization hierarchy) and within a certain dollar amount  or involving a specified override code.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Accounting Reviewer';
update krim_role_t
set desc_txt = 'Users with manager-level access to Accounts Payable documents. This includes the ability to hold or cancel (or remove those states) from Payment Request and Credit Memo documents. '
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Accounts Payable Manager';
update krim_role_t
set desc_txt = 'Central Accounts Receivable staff that receive workflow action requests for Cash Control and Lockbox documents. They can also use the Electronic Fund Transfer screen and claim those funds using a Cash Control document.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Accounts Receivable Lockbox Manager';
update krim_role_t
set desc_txt = 'Users that manage the KFS-AR module.  This role has no inherent permissions or responsibilities.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Accounts Receivable Manager';
update krim_role_t
set desc_txt = 'A role that uses the Affiliation Type and Employee Status on a Principal record to determine if a user is an active faculty or staff employee. These users can initiate some KFS-PURAP documents and inquire into certain KFS screens.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Active Faculty or Staff';
update krim_role_t
set desc_txt = 'A role that uses the Employee Status (A,L or P) and Employee Type (P) to determine that a given Principal represents a professional staff employee. These users are allowed to be Account Supervisors or Account Managers on Accounts.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Active Professional Employee';
update krim_role_t
set desc_txt = 'Central Capital Assets staff capable of taking restricted actions on Assets, including retiring or transferring non-moveable assets.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Asset Manager';
update krim_role_t
set desc_txt = 'Central Capital Assets staff capable of applying asset payments, using KFS-CAB and adding negative payments.  This role contains permissions to modify restricted asset fields and to override the defined capitalization threshold.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Asset Processor';
update krim_role_t
set desc_txt = 'Defines users responsible for managing the chart data for a given Chart of Accounts code. They may initiate Global Object Code and Organization Reversion maintenance documents and modify the Campus and Organization Plant Chart Code and Account on Organization documents.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Chart Manager';
update krim_role_t
set desc_txt = 'Central contract and grant staff that have special permissions related to Effort Certification. They can override the edit that prevents transferring salary for an open effort reporting period and receive workflow action requests for Effort Certification Recreates.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Contracts & Grants Manager';
update krim_role_t
set desc_txt = 'Central contract and grant staff that receive workflow action requests for transactions involving grant accounts.  They can view Research Risk information on Proposal documents, establish cost share sub-accounts and modify the object codes on Salary Expense Transfer documents.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Contracts & Grants Processor';
update krim_role_t
set desc_txt = 'This role defines the list of users that may be selected as Project Directors on the Proposal or Award document.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Contracts & Grants Project Director';
update krim_role_t
set desc_txt = 'This role derives its members from the Fiscal Officer field on the Account. Fiscal Officers receive workflow action requests for most transactional documents and have edit permissions that allow them to change accounting lines involving their accounts.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Fiscal Officer';
update krim_role_t
set desc_txt = 'This role derives its members from the Primary delegates defined in the Account Delegate table in KFS.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Fiscal Officer Primary Delegate';
update krim_role_t
set desc_txt = 'This role derives its members from the Secondary delegates defined in the Account Delegate table in KFS.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Fiscal Officer Secondary Delegate';
update krim_role_t
set desc_txt = 'This role represents a collection of all  the KFS module manager roles and has permission to initiate simple maintenance documents and restricted documents such as the JV and LLJV. These users also have the ability to blanket approve most document types and assign roles and permissions for all KFS namespaces.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Manager';
update krim_role_t
set desc_txt = 'This role represents a very select central processing function allowed to run KFS batch jobs, initiate GLCP and LLCP documents and upload Enterprise Feed and Procurement Card files.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Operations';
update krim_role_t
set desc_txt = 'This role manages the plant fund functions associated with KFS-CAM and has special permissions related to assets in support of these functions. It can also edit the Organization and Campus Plant Chart and Account fields on the Organization document.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Plant Fund Accountant';
update krim_role_t
set desc_txt = 'Users that manage the KFS-PURAP module.  This role can take the resend  action on Purchase Order documents.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Purchasing Manager';
update krim_role_t
set desc_txt = 'Users who receive workflow action requests for documents that include accounts belonging to particular sub-funds groups.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Sub-Fund Reviewer';
update krim_role_t
set desc_txt = 'Users with a need to view unmasked Tax ID numbers. They can also modify the tax number associated with AR customer records and PURAP vendor records.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Tax Identification Number User';
update krim_role_t
set desc_txt = 'Represents a central tax area that receives workflow action requests for DVs, Payment Requests, and POs involving payments to non-resident aliens or employees. They can also edit the Tax tabs on the DV and Payment Request documents.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Tax Manager';
update krim_role_t
set desc_txt = 'A technical administrator that is specific to the KFS system. This role has no inherent permissions or responsibilities.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Technical Administrator';
update krim_role_t
set desc_txt = 'This role derives its members from the KFS Chart table. It is used to determine the Chart Manager of the top level Chart in the organization hierarchy. This role receives workflow action requests for Chart documents and has the ability to edit the organization and campus Plant Chart and Account fields on the Organization document.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'University Chart Manager';
update krim_role_t
set desc_txt = 'The basic role that grants users access to KFS. It gives users the ability to initiate most documents and use inquiries and search screens.  Users are qualified by namespace, chart and organization. If these fields are not defined the chart and organization are inherited from the Department ID on the users'' Principal record.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'User';
update krim_role_t
set desc_txt = 'Users capable of taking superuser action on KFS documents and blanket approving some document types not available to the KFS-SYS Manager role.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Workflow Administrator';
update krim_role_t
set desc_txt = 'This role represents the KFS System User, that is the user ID the system uses when it takes programmed actions (such as auto-initiating or approving documents such as the PCDO and PO).'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'System User';
update krim_role_t
set desc_txt = 'An optional role that allows users to receive workflow action requests for documents of a specified type that include a specified chart and organization (including the organization hierarchy).,'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Organization Reviewer';
update krim_role_t
set desc_txt = 'This role is derived from the accounts appearing on an Effort Certification document. KFS finds the most recent award associated with each account and routes workflow action requests to the Project Director''s associated with the accounts on the Effort Certification document.,'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Award Project Director';
update krim_role_t
set desc_txt = 'This role is derived from users with the Modify Batch Job permission. They are able to use the Schedule lookup.,'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Batch Job Modifier';
update krim_role_t
set desc_txt = 'This role derives its members from the Account Supervisor field on the Account. Account Supervisors receive workflow action requests for Asset and Asset Retirement Global documents.,'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Account Supervisor';
update krim_role_t
set desc_txt = 'This role derives its members from the Account Supervisor field on the Account. Account Supervisors receive workflow action requests for Asset and Asset Retirement Global documents.,'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Active Employee & Financial System User';
update krim_role_t
set desc_txt = 'A role that uses the Employee Status (A,L or P) and Employee Type (P), along with the presence of the KFS-SYS User role to determine that a given Principal represents a professional staff employee with KFS access. These users are allowed to be fiscal Officers on Accounts.'
where nmspc_cd = 'KFS-SYS'
and role_nm = 'Active Professional Employee & Financial System User';
update krim_role_t
set desc_txt = 'This role receives workflow action requests for the Vendor document.'
where nmspc_cd = 'KFS-VND'
and role_nm = 'Reviewer';
update krim_role_t
set desc_txt = 'This role derives its members from users with the Edit Document permission for a given document type.,'
where nmspc_cd = 'KR-NS'
and role_nm = 'Document Editor';
update krim_role_t
set desc_txt = 'This role derives its members from users with the Open Document permission for a given document type.,'
where nmspc_cd = 'KR-NS'
and role_nm = 'Document Opener';
update krim_role_t
set desc_txt = 'This  role can take superuser actions and blanket approve RICE documents as well as being able to modify and assign permissions, responsibilities and roles belonging to the KR namespaces.'
where nmspc_cd = 'KR-SYS'
and role_nm = 'Technical Administrator';
update krim_role_t
set desc_txt = 'This role represents the KR System User, that is the user ID RICE uses when it takes programmed actions.'
where nmspc_cd = 'KR-SYS'
and role_nm = 'System User';
update krim_role_t
set desc_txt = 'This role derives its members from users with the Initiate Document permission for a given document type.'
where nmspc_cd = 'KR-SYS'
and role_nm = 'Document Initiator';
update krim_role_t
set desc_txt = 'This role derives its members from users with that have received an action request for a given document.'
where nmspc_cd = 'KR-WKFLW'
and role_nm = 'Approve Request Recipient';
update krim_role_t
set desc_txt = 'This role derives its members from the initiator listed within the route log of a given document.'
where nmspc_cd = 'KR-WKFLW'
and role_nm = 'Initiator';
update krim_role_t
set desc_txt = 'This role derives its members from the initiator and action request recipients listed within the route log of a given document.'
where nmspc_cd = 'KR-WKFLW'
and role_nm = 'Initiator or Reviewer';
update krim_role_t
set desc_txt = 'This role derives its members from the user who took the Complete action on a given document.'
where nmspc_cd = 'KR-WKFLW'
and role_nm = 'Router';
update krim_role_t
set desc_txt = 'This role derives its members from users with an acknowledge action request in the route log of a given document.'
where nmspc_cd = 'KR-WKFLW'
and role_nm = 'Acknowledge Request Recipient';
update krim_role_t
set desc_txt = 'This role derives its members from users with an FYI action request in the route log of a given document.'
where nmspc_cd = 'KR-WKFLW'
and role_nm = 'FYI Request Recipient';
update krim_role_t
set desc_txt = 'This role derives its members from users with an Approval action request (that was not generated via the ad-hoc recipients tab) in the route log of a given document.'
where nmspc_cd = 'KR-WKFLW'
and role_nm = 'Non-Ad Hoc Approve Request Recipient';
update krim_role_t
set desc_txt = 'This role derives its members from the users in the Principal table. This role gives users high-level permissions to interact with RICE documents and to login to KUALI.'
where nmspc_cd = 'KUALI'
and role_nm = 'User';
--done