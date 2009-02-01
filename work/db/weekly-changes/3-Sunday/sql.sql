update krim_role_mbr_attr_data_t set attr_val = 'ARSC' where attr_val = 'ARSD' and target_primary_key in (select role_mbr_id from krim_role_mbr_t where role_id in (select role_id from krim_role_t where nmspc_cd = 'KFS-CAM' and role_nm = 'Processor'))
/

INSERT INTO KRIM_ROLE_MBR_T(ROLE_MBR_ID, VER_NBR, OBJ_ID, ROLE_ID, MBR_ID, MBR_TYP_CD, ACTV_FRM_DT, ACTV_TO_DT, LAST_UPDT_DT) 
    VALUES('1633', 1, sys_guid(), '2', '4', 'R', null, null, SYSDATE)
/
