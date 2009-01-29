delete from krns_parm_t
where nmspc_cd = 'KFS-COA'
and parm_nm = 'ACTIVE_EMPLOYEE_STATUSES'
/
delete from krns_parm_t
where nmspc_cd = 'KFS-COA'
and parm_nm = 'CG_GROUP'
/
delete from krns_parm_t
where nmspc_cd = 'KFS-COA'
and parm_nm = 'PLANT_GROUP'
/
delete from krns_parm_t
where nmspc_cd = 'KFS-COA'
and parm_nm = 'MAINTENANCE_ADMIN_GROUP'
/
delete from krns_parm_t
where nmspc_cd = 'KFS-COA'
and parm_nm = 'ROLE_EMPLOYEE_STATUSES'
/
delete from krns_parm_t
where nmspc_cd = 'KFS-COA'
and parm_nm = 'ROLE_EMPLOYEE_TYPES'
/
delete from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_nm = 'POST_AWARD_GROUP'
/
delete from krns_parm_t
where nmspc_cd = 'KFS-CG'
and parm_nm = 'PRE_AWARD_GROUP'
/
update krim_role_t set kim_typ_id = '41' where role_id = '33'
/