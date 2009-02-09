INSERT INTO KRIM_ROLE_MBR_T(ROLE_MBR_ID, VER_NBR, OBJ_ID, ROLE_ID, MBR_ID, MBR_TYP_CD, ACTV_FRM_DT, ACTV_TO_DT, LAST_UPDT_DT) 
    VALUES('1640', 1, sys_guid(), '20', '19', 'R', null, null, SYSDATE)
/
delete from krim_role_mbr_t where role_mbr_id = '1353'
/
