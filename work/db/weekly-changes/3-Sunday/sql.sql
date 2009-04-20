delete from krim_role_mbr_t where role_mbr_id in ('1183', '1184', '1185')
/

INSERT INTO KRIM_TYP_T(KIM_TYP_ID, OBJ_ID, VER_NBR, NM, SRVC_NM, ACTV_IND, NMSPC_CD) 
    VALUES('66', sys_guid(), 1, 'Derived Role: Permission (Initiate Document)', 'documentInitiatorRoleTypeService', 'Y', 'KR-SYS')
/
INSERT INTO KRIM_ROLE_T(ROLE_ID, OBJ_ID, VER_NBR, ROLE_NM, NMSPC_CD, DESC_TXT, KIM_TYP_ID, ACTV_IND, LAST_UPDT_DT) 
    VALUES('95', sys_guid(), 1, 'Document Initiator', 'KR-SYS', null, '66', 'Y', SYSDATE)
/
delete from krim_role_perm_t where perm_id = '123'
/
delete from krim_perm_rqrd_attr_t where perm_id = '123'
/
delete from krim_perm_attr_data_t where perm_id = '123'
/
delete from krim_perm_t where perm_id = '123'
/
update krim_perm_t set nmspc_cd = 'KUALI' where perm_id = '156'
/
update krim_perm_attr_data_t set attr_val = 'KualiDocument' where perm_id = '156'
/
update krim_role_perm_t set role_id = '95' where perm_id in ('108', '156')
/

INSERT INTO KRIM_ROLE_T(ROLE_ID, OBJ_ID, VER_NBR, ROLE_NM, NMSPC_CD, DESC_TXT, KIM_TYP_ID, ACTV_IND, LAST_UPDT_DT) 
    VALUES('96', sys_guid(), 1, 'Financial Processing Manager', 'KFS-FP', null, '1', 'Y', SYSDATE)
/
INSERT INTO KRIM_ROLE_MBR_T(ROLE_MBR_ID, VER_NBR, OBJ_ID, ROLE_ID, MBR_ID, MBR_TYP_CD, ACTV_FRM_DT, ACTV_TO_DT, LAST_UPDT_DT) 
    VALUES('1703', 1, sys_guid(), '44', '50', 'R', null, null, SYSDATE)
/
INSERT INTO KRIM_ROLE_MBR_T(ROLE_MBR_ID, VER_NBR, OBJ_ID, ROLE_ID, MBR_ID, MBR_TYP_CD, ACTV_FRM_DT, ACTV_TO_DT, LAST_UPDT_DT) 
    VALUES('1704', 1, sys_guid(), '44', '96', 'R', null, null, SYSDATE)
/

INSERT INTO KRIM_TYP_T(KIM_TYP_ID, OBJ_ID, VER_NBR, NM, SRVC_NM, ACTV_IND, NMSPC_CD) 
    VALUES('67', sys_guid(), 1, 'Namespace', 'namespacePermissionTypeService', 'Y', 'KR-NS')
/
INSERT INTO KRIM_TYP_ATTR_T(KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND, SORT_CD) 
    VALUES('111', sys_guid(), 1, '67', '4', 'Y', 'a')
/
INSERT INTO KRIM_PERM_TMPL_T(PERM_TMPL_ID, OBJ_ID, VER_NBR, NM, DESC_TXT, KIM_TYP_ID, ACTV_IND, NMSPC_CD) 
    VALUES('50', sys_guid(), 1, 'View Batch File(s)', null, '67', 'Y', 'KR-NS')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('362', sys_guid(), 1, '50', 'View Batch File(s)', null, 'Y', 'KFS-SYS')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('523', sys_guid(), 1, '362', '67', '4', 'KFS*')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('665', sys_guid(), 1, '44', '362', 'Y')
/

update krim_typ_t set nm = 'Edit Mode & Document Type', srvc_nm = 'documentTypeAndEditModePermissionTypeService' where kim_typ_id = '14'
/
INSERT INTO KRIM_TYP_ATTR_T(KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND, SORT_CD) 
    VALUES('112', sys_guid(), 1, '14', '13', 'Y', 'b')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('524', sys_guid(), 1, '206', '14', '13', 'ECD')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('525', sys_guid(), 1, '207', '14', '13', 'ECD')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('526', sys_guid(), 1, '208', '14', '13', 'DV')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('527', sys_guid(), 1, '209', '14', '13', 'DV')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('528', sys_guid(), 1, '210', '14', '13', 'DV')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('529', sys_guid(), 1, '211', '14', '13', 'DV')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('530', sys_guid(), 1, '212', '14', '13', 'DV')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('531', sys_guid(), 1, '213', '14', '13', 'DV')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('532', sys_guid(), 1, '214', '14', '13', 'PRAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('533', sys_guid(), 1, '215', '14', '13', 'PRAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('534', sys_guid(), 1, '216', '14', '13', 'PRAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('535', sys_guid(), 1, '217', '14', '13', 'PRAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('536', sys_guid(), 1, '218', '14', '13', 'PRAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('537', sys_guid(), 1, '219', '14', '13', 'PRAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('538', sys_guid(), 1, '272', '14', '13', 'PRAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('539', sys_guid(), 1, '273', '14', '13', 'PRAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('540', sys_guid(), 1, '274', '14', '13', 'PRAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('541', sys_guid(), 1, '275', '14', '13', 'PRAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('542', sys_guid(), 1, '276', '14', '13', 'PRAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('543', sys_guid(), 1, '277', '14', '13', 'PRAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('544', sys_guid(), 1, '278', '14', '13', 'PRAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('545', sys_guid(), 1, '282', '14', '13', 'PRAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('546', sys_guid(), 1, '283', '14', '13', 'PRAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('547', sys_guid(), 1, '286', '14', '13', 'PRAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('548', sys_guid(), 1, '293', '14', '13', 'PRAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('549', sys_guid(), 1, '294', '14', '13', 'PRAP')
/

update krim_perm_t set nmspc_cd = 'KUALI' where perm_id = '146'
/
update krim_perm_attr_data_t set attr_val = 'Kuali Document' where perm_id = '146'
/
update krim_role_perm_t set role_id = '83' where perm_id = '146'
/
delete from krim_role_perm_t where perm_id in ('109', '111', '112', '336', '337', '338', '339', '340', '341')
/
delete from krim_perm_rqrd_attr_t where perm_id in ('109', '111', '112', '336', '337', '338', '339', '340', '341')
/
delete from krim_perm_attr_data_t where perm_id in ('109', '111', '112', '336', '337', '338', '339', '340', '341')
/
delete from krim_perm_t where perm_id in ('109', '111', '112', '336', '337', '338', '339', '340', '341')
/

INSERT INTO KRIM_ROLE_T(ROLE_ID, OBJ_ID, VER_NBR, ROLE_NM, NMSPC_CD, DESC_TXT, KIM_TYP_ID, ACTV_IND, LAST_UPDT_DT) 
    VALUES('97', sys_guid(), 1, 'Non-Ad Hoc Approve Request Recipient', 'KR-WKFLW', null, '42', 'Y', SYSDATE)
/
update krim_role_perm_t set role_id = '97' where perm_id = '181'
/

update krim_perm_t a set a.NM = (select b.nm from krim_perm_tmpl_t b where a.perm_tmpl_id = b.perm_tmpl_id) where a.nm is null
/

INSERT INTO KRIM_TYP_T(KIM_TYP_ID, OBJ_ID, VER_NBR, NM, SRVC_NM, ACTV_IND, NMSPC_CD) 
    VALUES('68', sys_guid(), 1, 'Organization Group', 'organizationGroupTypeService', 'Y', 'KFS-COA')
/
INSERT INTO KRIM_TYP_ATTR_T(KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND, SORT_CD) 
    VALUES('113', sys_guid(), 1, '68', '22', 'Y', 'a')
/
INSERT INTO KRIM_TYP_ATTR_T(KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND, SORT_CD) 
    VALUES('114', sys_guid(), 1, '68', '24', 'Y', 'b')
/
update krew_doc_typ_t set lbl = 'Organization Group Type' where doc_typ_nm = 'ORGG'
/
