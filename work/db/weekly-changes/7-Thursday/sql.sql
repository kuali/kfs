update krim_perm_attr_data_t set kim_attr_defn_id = '4', attr_val = 'KFS-BC' where target_primary_key = '251'
/
delete from krim_role_perm_t where perm_id in ('252', '253', '254', '255')
/
delete from krim_perm_rqrd_attr_t where perm_id in ('252', '253', '254', '255')
/
delete from krim_perm_attr_data_t where target_primary_key in ('252', '253', '254', '255')
/
delete from krim_perm_t where perm_id in ('252', '253', '254', '255')
/

