update krim_role_mbr_attr_data_t set attr_val = 'ARSC' where attr_val = 'ARSD' and target_primary_key in (select role_mbr_id from krim_role_mbr_t where role_id in (select role_id from krim_role_t where nmspc_cd = 'KFS-CAM' and role_nm = 'Processor'))
/
