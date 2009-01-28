update krim_perm_attr_data_t set attr_val = 'org.kuali.kfs.sys.web.struts.ElectronicFundTransferAction' where attr_data_id = '395'
/

update krim_role_mbr_attr_data_t set target_primary_key = '1290', attr_val = 'UA' where attr_data_id = '2912'
/
update krim_role_mbr_attr_data_t set target_primary_key = '1290', attr_val = 'VPIT' where attr_data_id = '2913'
/

INSERT INTO KRIM_ROLE_T(ROLE_ID, OBJ_ID, VER_NBR, ROLE_NM, NMSPC_CD, DESC_TXT, KIM_TYP_ID, ACTV_IND, LAST_UPDT_DT) 
    VALUES('90', sys_guid(), 1, 'Rice', 'KR-SYS', null, '1', 'Y', SYSDATE)
/
update krim_role_t set role_nm = 'Financial System', nmspc_cd = 'KFS-SYS', kim_typ_id = '1' where role_id = '62'
/
update krim_role_mbr_t set role_id = '90' where role_id = '62' and mbr_id = '1'
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('290', sys_guid(), 1, '15', null, null, 'Y', 'KR-SYS')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('419', sys_guid(), 1, '290', '8', '13', 'RiceDocument')
/
update krim_perm_t set nmspc_cd = 'KFS-SYS' where perm_id = '169'
/
update krim_perm_attr_data_t set attr_val = 'FinancialSystemDocument' where attr_data_id = '222'
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('552', sys_guid(), 1, '90', '290', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('553', sys_guid(), 1, '66', '290', 'Y')
/

