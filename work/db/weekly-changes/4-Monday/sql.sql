delete from krns_parm_t a where nmspc_cd = 'KFS-CAM' and parm_dtl_typ_cd = 'Asset' and parm_nm = 'VALID_INVENTORY_STATUS_CODES_BY_PRIOR_INVENTORY_STATUS_CODE'
;
delete from krns_parm_t a where nmspc_cd = 'KFS-FP' and parm_dtl_typ_cd = 'DisbursementVoucher' and parm_nm = 'TAX_DOCUMENTATION_LOCATION_CODE'
;
delete from krns_parm_t a where nmspc_cd = 'KFS-CG' and parm_dtl_typ_cd = 'Document' and parm_nm = 'RESEARCH_RISKS_ANIMALS_ACTIVE_CODE'
;
delete from krns_parm_t a where nmspc_cd = 'KFS-CG' and parm_dtl_typ_cd = 'Document' and parm_nm = 'RESEARCH_RISKS_HUMAN_SUBJECTS_ACTIVE_CODE'
;
delete FROM krns_parm_t a where nmspc_cd = 'KFS-CAM' and parm_dtl_typ_cd = 'Asset'  and parm_nm = 'INVALID_INVENTORY_STATUS_CODES_BY_PRIOR_INVENTORY_STATUS_CODE'
;            
delete from krns_parm_t a where nmspc_cd = 'KFS-EC' and parm_dtl_typ_cd = 'EffortCertificationExtractStep' and parm_nm = 'FEDERAL_AGENCY_TYPE_CODE'
;
delete from krns_parm_t a where nmspc_cd = 'KFS-CG' and parm_dtl_typ_cd = 'Document' and parm_nm = 'GRADUATE_ASSISTANT_NONPERSONNEL_CATEGORY_CODE'
;
delete from krns_parm_t a where nmspc_cd = 'KFS-CAM' and parm_dtl_typ_cd = 'Asset' and parm_nm = 'DEFAULT_FABRICATION_ASSET_TYPE_CODE'
;
delete from krns_parm_t a where nmspc_cd = 'KFS-CG' and parm_dtl_typ_cd = 'Document' and parm_nm = 'GRADUATE_ASSISTANT_NONPERSONNEL_SUB_CATEGORY_CODE'
;
delete from krns_parm_t a where nmspc_cd = 'KFS-FP' and parm_dtl_typ_cd = 'ProcurementCardCreateDocumentsStep' and parm_nm = 'ERROR_TRANSACTION_CHART_CODE'
;
delete from krns_parm_t a where nmspc_cd = 'KFS-PURAP' and parm_dtl_typ_cd = 'Requisition' and parm_nm = 'DEFAULT_TRANSMISSION_CODE'
;
delete from krns_parm_t a where nmspc_cd = 'KFS-CG' and parm_dtl_typ_cd = 'ResearchBudget' and parm_nm = 'COST_SHARE_PERMISSION_CODE'
;
commit;
update krim_perm_attr_data_t set attr_val = 'AccountFullEdit' where attr_val = 'AccountFullEntry'
commit;
delete  from krns_parm_t
where nmspc_cd = 'KFS-PURAP'
and parm_nm = 'BULK_RECEIVING';
commit;
update ca_org_rvrsn_dtl_t set actv_ind = 'Y';
commit;
ALTER TABLE KREW_DOC_HDR_EXT_FLT_T MODIFY VAL NUMBER(30, 15);
