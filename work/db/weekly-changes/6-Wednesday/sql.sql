INSERT INTO KRIM_ROLE_PERM_T VALUES ('570', sys_guid(), 1, '54', '247', 'Y')
/

delete from krim_perm_attr_data_t where TARGET_PRIMARY_KEY = '110'
/
delete from krim_perm_rqrd_attr_t where perm_id = '110'
/
delete from krim_role_perm_t where perm_id = '110'
/
delete from krim_perm_t where perm_id = '110'
/
