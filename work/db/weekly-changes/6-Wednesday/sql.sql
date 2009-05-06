INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('675', sys_guid(), 1, '44', '205', 'Y')
/

delete from krim_role_rsp_actn_t where role_rsp_id in (select role_rsp_id from krim_role_rsp_t where rsp_id = '79')
/
delete from krim_role_rsp_t where rsp_id = '79'
/
delete from krim_rsp_attr_data_t where rsp_id = '79'
/
delete from krim_rsp_t where rsp_id = '79'
/

INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('676', sys_guid(), 1, '2', '360', 'Y')
/
