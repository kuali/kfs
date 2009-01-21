-- bo rename not reflected in kim data
UPDATE KRIM_PERM_ATTR_DATA_T SET ATTR_VAL = 'Organization' WHERE ATTR_VAL = 'Org'
/

-- don't want to restrict pdp actions by campus, even though they happen to be assigned to a campus role
delete from krim_perm_rqrd_attr_t where perm_id in (select perm_id from krim_perm_t where nmspc_cd = 'KFS-PDP')
/

-- naming the service more specifically, since we had to add more specific logic
update krim_typ_t set nm = 'Ad Hoc Review', srvc_nm = 'adhocReviewPermissionTypeService' where kim_typ_id = '5'
/

-- ar org role type service back to being an application role type instead of pass through, so no assignments required
DELETE FROM krim_role_mbr_t WHERE role_id IN ('2', '4')
/

-- additions / updates for cams workflow requirements that were missed
update krim_role_rsp_t set role_id = '46' where rsp_id = '18'
/
update krim_rsp_attr_data_t set attr_val = 'PlantFund' where target_primary_key = '18' and kim_attr_defn_id = '16'
/
INSERT INTO KRIM_RSP_T(RSP_ID, OBJ_ID, VER_NBR, RSP_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('102', sys_guid(), 1, '1', null, null, 'Y', 'KFS-CAM')
/
INSERT INTO KRIM_RSP_T(RSP_ID, OBJ_ID, VER_NBR, RSP_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('103', sys_guid(), 1, '1', null, null, 'Y', 'KFS-CAM')
/
INSERT INTO KRIM_RSP_T(RSP_ID, OBJ_ID, VER_NBR, RSP_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('104', sys_guid(), 1, '1', null, null, 'Y', 'KFS-CAM')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('372', sys_guid(), 1, '102', '7', '16', 'Account')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('373', sys_guid(), 1, '102', '7', '13', 'AssetFabricationMaintenanceDocument')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('374', sys_guid(), 1, '102', '7', '41', 'false')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('375', sys_guid(), 1, '102', '7', '40', 'true')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('376', sys_guid(), 1, '103', '7', '16', 'Account')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('377', sys_guid(), 1, '103', '7', '13', 'AssetGlobalMaintenanceDocument')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('378', sys_guid(), 1, '103', '7', '41', 'false')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('379', sys_guid(), 1, '103', '7', '40', 'true')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('380', sys_guid(), 1, '104', '7', '16', 'Account')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('381', sys_guid(), 1, '104', '7', '13', 'AssetTransferDocument')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('382', sys_guid(), 1, '104', '7', '41', 'false')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('383', sys_guid(), 1, '104', '7', '40', 'true')
/
INSERT INTO KRIM_RSP_RQRD_ATTR_T(RSP_RQRD_ATTR_ID, OBJ_ID, VER_NBR, RSP_ID, KIM_ATTR_DEFN_ID, ACTV_IND) 
    VALUES('119', sys_guid(), 1, '102', '22', 'Y')
/
INSERT INTO KRIM_RSP_RQRD_ATTR_T(RSP_RQRD_ATTR_ID, OBJ_ID, VER_NBR, RSP_ID, KIM_ATTR_DEFN_ID, ACTV_IND) 
    VALUES('120', sys_guid(), 1, '102', '23', 'Y')
/
INSERT INTO KRIM_RSP_RQRD_ATTR_T(RSP_RQRD_ATTR_ID, OBJ_ID, VER_NBR, RSP_ID, KIM_ATTR_DEFN_ID, ACTV_IND) 
    VALUES('121', sys_guid(), 1, '103', '22', 'Y')
/
INSERT INTO KRIM_RSP_RQRD_ATTR_T(RSP_RQRD_ATTR_ID, OBJ_ID, VER_NBR, RSP_ID, KIM_ATTR_DEFN_ID, ACTV_IND) 
    VALUES('122', sys_guid(), 1, '103', '23', 'Y')
/
INSERT INTO KRIM_RSP_RQRD_ATTR_T(RSP_RQRD_ATTR_ID, OBJ_ID, VER_NBR, RSP_ID, KIM_ATTR_DEFN_ID, ACTV_IND) 
    VALUES('123', sys_guid(), 1, '104', '22', 'Y')
/
INSERT INTO KRIM_RSP_RQRD_ATTR_T(RSP_RQRD_ATTR_ID, OBJ_ID, VER_NBR, RSP_ID, KIM_ATTR_DEFN_ID, ACTV_IND) 
    VALUES('124', sys_guid(), 1, '104', '23', 'Y')
/
INSERT INTO KRIM_ROLE_RSP_T(ROLE_RSP_ID, OBJ_ID, VER_NBR, ROLE_ID, RSP_ID, ACTV_IND) 
    VALUES('1102', sys_guid(), 1, '41', '102', 'Y')
/
INSERT INTO KRIM_ROLE_RSP_T(ROLE_RSP_ID, OBJ_ID, VER_NBR, ROLE_ID, RSP_ID, ACTV_IND) 
    VALUES('1103', sys_guid(), 1, '41', '103', 'Y')
/
INSERT INTO KRIM_ROLE_RSP_T(ROLE_RSP_ID, OBJ_ID, VER_NBR, ROLE_ID, RSP_ID, ACTV_IND) 
    VALUES('1104', sys_guid(), 1, '41', '104', 'Y')
/
INSERT INTO KRIM_ROLE_RSP_ACTN_T(ROLE_RSP_ACTN_ID, OBJ_ID, VER_NBR, ACTN_TYP_CD, PRIORITY_NBR, ACTN_PLCY_CD, ROLE_MBR_ID, ROLE_RSP_ID) 
    VALUES('127', sys_guid(), 1, 'A', null, 'F', '*', '1102')
/
INSERT INTO KRIM_ROLE_RSP_ACTN_T(ROLE_RSP_ACTN_ID, OBJ_ID, VER_NBR, ACTN_TYP_CD, PRIORITY_NBR, ACTN_PLCY_CD, ROLE_MBR_ID, ROLE_RSP_ID) 
    VALUES('128', sys_guid(), 1, 'A', null, 'F', '*', '1103')
/
INSERT INTO KRIM_ROLE_RSP_ACTN_T(ROLE_RSP_ACTN_ID, OBJ_ID, VER_NBR, ACTN_TYP_CD, PRIORITY_NBR, ACTN_PLCY_CD, ROLE_MBR_ID, ROLE_RSP_ID) 
    VALUES('129', sys_guid(), 1, 'A', null, 'F', '*', '1104')
/
update krim_rsp_attr_data_t set attr_val = 'ExternalTransfer' where target_primary_key = '19' and kim_attr_defn_id = '16'
/
update krim_rsp_attr_data_t set attr_val = 'AssetRetirementGlobalMaintenanceDocument'  where target_primary_key = '20' and kim_attr_defn_id = '13'
/
