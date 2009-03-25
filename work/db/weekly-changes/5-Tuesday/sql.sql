insert into krns_parm_t
(SELECT 'KFS-LD', 'LaborBalancingStep',
'NUMBER_OF_PAST_FISCAL_YEARS_TO_INCLUDE', sys_guid(),1,
'CONFG', '1',
'Number of fiscal years to subtract from current fiscal year to represent what the start range on the balancing batch job is. This is used for laborBalancingStep.',
'A'
FROM dual)
;
delete krns_parm_t
where nmspc_cd = 'KFS-LD'
and parm_dtl_typ_cd = 'LaborBalancingStep'
and parm_nm = 'NUMBER_OF_PAST_FISCAL_YEARS_TO_CONSIDER'
;
update krns_parm_t
set parm_desc_txt = 'Total number of failures to print on the report for each category of balancing failures on the balancing batch job. This is used for laborBalancingStep.'
where parm_nm = 'NUMBER_OF_COMPARISON_FAILURES_TO_PRINT_PER_REPORT'
and nmspc_cd = 'KFS-LD'
and parm_dtl_typ_cd = 'LaborBalancingStep';
insert into krns_parm_t
(SELECT 'KFS-GL', 'PosterBalancingStep',
'NUMBER_OF_PAST_FISCAL_YEARS_TO_INCLUDE', sys_guid(),1,
'CONFG', '1',
'Number of fiscal years to subtract from current fiscal year to represent what the start range on the balancing batch job is. This is used for posterBalancingStep.',
'A'
FROM dual)
;
delete krns_parm_t
where nmspc_cd =  'KFS-GL'
and parm_dtl_typ_cd = 'PosterBalancingStep'
and parm_nm = 'NUMBER_OF_PAST_FISCAL_YEARS_TO_CONSIDER'
;
update krns_parm_t
set parm_desc_txt = 'Total number of failures to print on the report for each category of balancing failures on the balancing batch job. This is used for posterBalancingStep.'
where parm_nm = 'NUMBER_OF_COMPARISON_FAILURES_TO_PRINT_PER_REPORT'
and nmspc_cd = 'KFS-GL'
and parm_dtl_typ_cd = 'PosterBalancingStep';
update krns_parm_t set PARM_NM='NUMBER_OF_PAST_FISCAL_YEARS_TO_INCLUDE'
where PARM_NM='NUMBER_OF_PAST_FISCAL_YEARS_TO_CONSIDER';
commit;
insert into krns_parm_t (SELECT a.nmspc_cd, a.parm_dtl_typ_cd, 'COST_SHARE_PERMISSION', sys_guid(), 1,
       a.parm_typ_cd, a.txt, a.parm_desc_txt, a.cons_cd
FROM krns_parm_t a where nmspc_cd = 'KFS-CG' and parm_dtl_typ_cd = 'Budget' and parm_nm = 'COST_SHARE_PERMISSION_CODE')
;
insert into krns_parm_t (SELECT a.nmspc_cd, a.parm_dtl_typ_cd, 'DEFAULT_FABRICATION_ASSET_TYPE', sys_guid(), 1,
       a.parm_typ_cd, a.txt, a.parm_desc_txt, a.cons_cd
FROM krns_parm_t a where nmspc_cd = 'KFS-CAM' and parm_dtl_typ_cd = 'Asset' and parm_nm = 'DEFAULT_FABRICATION_ASSET_TYPE_CODE')
;
insert into krns_parm_t (SELECT a.nmspc_cd, a.parm_dtl_typ_cd, 'DEFAULT_METHOD_OF_PO_TRANSMISSION' , sys_guid(), 1,
       a.parm_typ_cd, a.txt, a.parm_desc_txt, a.cons_cd
FROM krns_parm_t a where nmspc_cd = 'KFS-PURAP' and parm_dtl_typ_cd = 'Requisition' and parm_nm = 'DEFAULT_TRANSMISSION_CODE')
;
insert into krns_parm_t (SELECT a.nmspc_cd, a.parm_dtl_typ_cd, 'ERROR_TRANSACTION_CHART', sys_guid(), 1,
       a.parm_typ_cd, a.txt, a.parm_desc_txt, a.cons_cd
FROM krns_parm_t a where nmspc_cd = 'KFS-FP' and parm_dtl_typ_cd = 'ProcurementCardCreateDocumentsStep' and parm_nm = 'ERROR_TRANSACTION_CHART_CODE')
;
insert into krns_parm_t (SELECT a.nmspc_cd, a.parm_dtl_typ_cd, 'FEDERAL_AGENCY_TYPE', sys_guid(), 1,
       a.parm_typ_cd, a.txt, a.parm_desc_txt, a.cons_cd
FROM krns_parm_t a where nmspc_cd = 'KFS-EC' and parm_dtl_typ_cd = 'EffortCertificationExtractStep' and parm_nm = 'FEDERAL_AGENCY_TYPE_CODE')
;
insert into krns_parm_t (SELECT a.nmspc_cd, a.parm_dtl_typ_cd, 'GRADUATE_ASSISTANT_NONPERSONNEL_CATEGORY', sys_guid(), 1,
       a.parm_typ_cd, a.txt, a.parm_desc_txt, a.cons_cd
FROM krns_parm_t a where nmspc_cd = 'KFS-CG' and parm_dtl_typ_cd = 'Document' and parm_nm = 'GRADUATE_ASSISTANT_NONPERSONNEL_CATEGORY_CODE')
;
insert into krns_parm_t (SELECT a.nmspc_cd, a.parm_dtl_typ_cd, 'GRADUATE_ASSISTANT_NONPERSONNEL_SUB_CATEGORY', sys_guid(), 1,
       a.parm_typ_cd, a.txt, a.parm_desc_txt, a.cons_cd
FROM krns_parm_t a where nmspc_cd = 'KFS-CG' and parm_dtl_typ_cd = 'Document' and parm_nm = 'GRADUATE_ASSISTANT_NONPERSONNEL_SUB_CATEGORY_CODE')
;
insert into krns_parm_t (SELECT a.nmspc_cd, a.parm_dtl_typ_cd, 'INVALID_ASSET_STATUS_BY_PRIOR_ASSET_STATUS', sys_guid(), 1,
       a.parm_typ_cd, a.txt, a.parm_desc_txt, a.cons_cd
FROM krns_parm_t a where nmspc_cd = 'KFS-CAM' and parm_dtl_typ_cd = 'Asset'
and parm_nm = 'INVALID_INVENTORY_STATUS_CODES_BY_PRIOR_INVENTORY_STATUS_CODE')
;
insert into krns_parm_t (SELECT a.nmspc_cd, a.parm_dtl_typ_cd, 'RESEARCH_RISKS_ANIMALS_ACTIVE', sys_guid(), 1,
       a.parm_typ_cd, a.txt, a.parm_desc_txt, a.cons_cd
FROM krns_parm_t a where nmspc_cd = 'KFS-CG' and parm_dtl_typ_cd = 'Document' and parm_nm = 'RESEARCH_RISKS_ANIMALS_ACTIVE_CODE')
;
insert into krns_parm_t (SELECT a.nmspc_cd, a.parm_dtl_typ_cd, 'RESEARCH_RISKS_HUMAN_SUBJECTS_ACTIVE', sys_guid(), 1,
       a.parm_typ_cd, a.txt, a.parm_desc_txt, a.cons_cd
FROM krns_parm_t a where nmspc_cd = 'KFS-CG' and parm_dtl_typ_cd = 'Document' and parm_nm = 'RESEARCH_RISKS_HUMAN_SUBJECTS_ACTIVE_CODE')
;
insert into krns_parm_t (SELECT a.nmspc_cd, a.parm_dtl_typ_cd, 'TAX_DOCUMENTATION_LOCATION', sys_guid(), 1,
       a.parm_typ_cd, a.txt, a.parm_desc_txt, a.cons_cd
FROM krns_parm_t a where nmspc_cd = 'KFS-FP' and parm_dtl_typ_cd = 'DisbursementVoucher' and parm_nm = 'TAX_DOCUMENTATION_LOCATION_CODE')
;
insert into krns_parm_t (SELECT a.nmspc_cd, a.parm_dtl_typ_cd,  'VALID_ASSET_STATUS_BY_PRIOR_ASSET_STATUS' , sys_guid(), 1,
       a.parm_typ_cd, a.txt, a.parm_desc_txt, a.cons_cd
FROM krns_parm_t a where nmspc_cd = 'KFS-CAM' and parm_dtl_typ_cd = 'Asset' and parm_nm = 'VALID_INVENTORY_STATUS_CODES_BY_PRIOR_INVENTORY_STATUS_CODE')
;
commit;
update krim_attr_defn_t set appl_url = '${application.url}'
/

update krim_typ_attr_t set sort_cd = 'a' where kim_typ_attr_id = '79'
/
update krim_typ_attr_t set sort_cd = 'b' where kim_typ_attr_id = '76'
/
update krim_typ_attr_t set sort_cd = 'c' where kim_typ_attr_id = '77'
/
update krim_typ_attr_t set sort_cd = 'd' where kim_typ_attr_id = '78'
/

update krim_role_t set kim_typ_id = '17', role_nm = 'Disbursement Manager' where ROLE_ID = '12'
/