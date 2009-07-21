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
