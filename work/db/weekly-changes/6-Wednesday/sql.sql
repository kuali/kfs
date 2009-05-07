update ca_acct_delegate_t
set fdoc_aprv_from_amt = null
where fdoc_aprv_from_amt = 0
;
commit;
update ca_acct_delegate_t
set fdoc_aprv_to_amt = null
where fdoc_aprv_to_amt = 0
;
commit;
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

delete from krim_perm_attr_data_t where perm_id = '7'
/
delete from krim_role_perm_t where perm_id = '7'
/
delete from krim_perm_t where perm_id = '7'
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('369', sys_guid(), 1, '42', 'Create / Maintain Record(s)', null, 'Y', 'KFS-AR')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('562', sys_guid(), 1, '369', '56', '13', 'OADF')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('563', sys_guid(), 1, '369', '56', '7', 'false')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('677', sys_guid(), 1, '4', '369', 'Y')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('370', sys_guid(), 1, '42', 'Create / Maintain Record(s)', null, 'Y', 'KFS-AR')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('564', sys_guid(), 1, '370', '56', '13', 'OADF')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('565', sys_guid(), 1, '370', '56', '7', 'true')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('678', sys_guid(), 1, '2', '370', 'Y')
/
