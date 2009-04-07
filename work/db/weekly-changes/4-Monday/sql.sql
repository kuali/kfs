update krns_parm_t
set txt='DENIED'
where nmspc_cd = 'KFS-PURAP'
AND PARM_NM IN ('TAXABLE_SUB_FUND_GROUPS_FOR_TAXABLE_STATES',
'TAXABLE_OBJECT_LEVELS_FOR_TAXABLE_STATES',
'TAXABLE_OBJECT_CONSOLIDATIONS_FOR_TAXABLE_STATES',
'TAXABLE_FUND_GROUPS_FOR_TAXABLE_STATES',
'TAXABLE_DELIVERY_STATES' );
commit;
update ar_sys_info_t
set fdoc_initiator_id = '2'
where fdoc_initiator_id = 'KULUSER'
; 
commit;
update krns_parm_t 
set txt ='/module/cg/routingform/'
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'RoutingForm'
and parm_nm ='OUTPUT_PATH_PREFIX'
;
update krns_parm_t 
set txt ='/module/cg/budget/' 
where nmspc_cd = 'KFS-CG'
and parm_dtl_typ_cd = 'Budget'
and parm_nm ='OUTPUT_PATH_PREFIX'
;
commit;