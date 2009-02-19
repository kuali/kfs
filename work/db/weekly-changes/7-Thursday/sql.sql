insert into pdp_disb_nbr_rng_t 
(SELECT sys_guid(), 1, 'BL', 100000,
       100095, 999999, to_date('01/01/2009','MM/DD/YYYY'),
       'TEST', 'CHCK', 'Y'
  FROM  dual)
/
insert into pdp_disb_nbr_rng_t
(SELECT sys_guid(), 1, 'BL', 100000,
       100002, 999999, to_date('01/01/2009','MM/DD/YYYY'),
       '1010', 'CHCK', 'Y'
  FROM  dual)
/
update krim_entity_emp_info_t set base_slry_amt = '50000'
/
delete krns_parm_t
where nmspc_cd = 'KFS-FP'
and parm_dtl_typ_cd = 'IndirectCostAdjustment'
and parm_nm = 'OBJECT_SUB_TYPES'
/
delete krns_parm_t
where nmspc_cd = 'KFS-FP'
and parm_dtl_typ_cd = 'IndirectCostAdjustment'
and parm_nm = 'OBJECT_TYPES '
/
delete krns_parm_t
where nmspc_cd = 'KFS-FP'
and parm_dtl_typ_cd = 'IndirectCostAdjustment'
and parm_nm = 'OBJECT_SUB_TYPES'
/
delete krns_parm_t
where nmspc_cd = 'KFS-FP'
and parm_dtl_typ_cd = 'IndirectCostAdjustment'
and parm_nm = 'OBJECT_TYPES '
/