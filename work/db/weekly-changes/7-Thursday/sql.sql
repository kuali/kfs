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

INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('285', sys_guid(), 1, '29', null, null, 'Y', 'KFS-PDP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('415', sys_guid(), 1, '285', '12', '4', 'KFS-PDP')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('545', sys_guid(), 1, '20', '285', 'Y')
/
