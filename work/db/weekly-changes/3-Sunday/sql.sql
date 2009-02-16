-- create one namespace-based use perm for kfs-ar biller and delete individual action perms
update krim_perm_attr_data_t set kim_attr_defn_id = '4', attr_val = 'KFS-AR' where attr_data_id = '18'
/
delete from krim_role_perm_t where perm_id in ('13', '14', '288')
/
delete from krim_perm_rqrd_attr_t where perm_id in ('13', '14', '288')
/
delete from krim_perm_attr_data_t where target_primary_key in ('13', '14', '288')
/
delete from krim_perm_t where perm_id in ('13', '14', '288')
/

-- req needs an initiator responsibility for when no accounting lines and content review not set up
INSERT INTO KRIM_RSP_T(RSP_ID, OBJ_ID, VER_NBR, RSP_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('113', sys_guid(), 1, '1', null, null, 'Y', 'KFS-PURAP')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('416', sys_guid(), 1, '113', '7', '13', 'REQS')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('417', sys_guid(), 1, '113', '7', '16', 'Initiator')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('418', sys_guid(), 1, '113', '7', '40', 'true')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('419', sys_guid(), 1, '113', '7', '41', 'false')
/
INSERT INTO KRIM_ROLE_RSP_T(ROLE_RSP_ID, OBJ_ID, VER_NBR, ROLE_ID, RSP_ID, ACTV_IND) 
    VALUES('1113', sys_guid(), 1, '60', '113', 'Y')
/
INSERT INTO KRIM_ROLE_RSP_ACTN_T(ROLE_RSP_ACTN_ID, OBJ_ID, VER_NBR, ACTN_TYP_CD, PRIORITY_NBR, ACTN_PLCY_CD, ROLE_MBR_ID, ROLE_RSP_ID, IGNORE_PREV_IND) 
    VALUES('138', sys_guid(), 1, 'A', null, 'F', '*', '1113', 'Y')
/

-- cams plant fund routing needs to go to asset processor
update krim_role_rsp_t set role_id = '35' where role_rsp_id = '1040'
/

-- give cam merge and separate perms to kfs-sys asset processor too
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('573', sys_guid(), 1, '35', '21', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('574', sys_guid(), 1, '35', '26', 'Y')
/

-- create cab lookup and inquiry perms and give to asset processor
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('303', sys_guid(), 1, '23', null, null, 'Y', 'KFS-CAB')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('304', sys_guid(), 1, '24', null, null, 'Y', 'KFS-CAB')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('427', sys_guid(), 1, '303', '10', '4', 'KFS-CAB')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('428', sys_guid(), 1, '304', '10', '4', 'KFS-CAB')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('575', sys_guid(), 1, '35', '303', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('576', sys_guid(), 1, '35', '304', 'Y')
/

-- make account supv request for account maint doc an fyi
update krim_role_rsp_actn_t set actn_typ_cd = 'F' where role_rsp_actn_id = '52'
/
