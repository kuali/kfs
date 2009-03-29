INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('349', sys_guid(), 1, '10', 'Initiate Document', null, 'Y', 'KFS-AR')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('509', sys_guid(), 1, '349', '3', '13', 'IICO')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('648', sys_guid(), 1, '2', '349', 'Y')
/

INSERT INTO KRIM_TYP_T(KIM_TYP_ID, OBJ_ID, VER_NBR, NM, SRVC_NM, ACTV_IND, NMSPC_CD) 
    VALUES('65', sys_guid(), 1, 'Derived Role: Cash Receipt Initiator', 'cashReceiptInitiatorDerivedRoleTypeService', 'Y', 'KFS-FP')
/
INSERT INTO KRIM_ROLE_T(ROLE_ID, OBJ_ID, VER_NBR, ROLE_NM, NMSPC_CD, DESC_TXT, KIM_TYP_ID, ACTV_IND, LAST_UPDT_DT) 
    VALUES('92', sys_guid(), 1, 'Cash Receipt Initiator', 'KFS-FP', null, '65', 'Y', SYSDATE)
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('350', sys_guid(), 1, '10', 'Initiate Document', null, 'Y', 'KFS-FP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('510', sys_guid(), 1, '350', '3', '13', 'CR')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('649', sys_guid(), 1, '92', '350', 'Y')
/

INSERT INTO KRIM_ROLE_T(ROLE_ID, OBJ_ID, VER_NBR, ROLE_NM, NMSPC_CD, DESC_TXT, KIM_TYP_ID, ACTV_IND, LAST_UPDT_DT) 
    VALUES('93', sys_guid(), 1, 'Active Employee & Financial System User', 'KFS-SYS', null, '41', 'Y', SYSDATE)
/
INSERT INTO KRIM_ROLE_T(ROLE_ID, OBJ_ID, VER_NBR, ROLE_NM, NMSPC_CD, DESC_TXT, KIM_TYP_ID, ACTV_IND, LAST_UPDT_DT) 
    VALUES('94', sys_guid(), 1, 'Active Professional Employee & Financial System User', 'KFS-SYS', null, '41', 'Y', SYSDATE)
/
update krim_perm_t set nm = 'Serve As Account Manager' where perm_id = '59'
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('351', sys_guid(), 1, '1', 'Serve As Account Supervisor', null, 'Y', 'KFS-COA')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('650', sys_guid(), 1, '33', '351', 'Y')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('352', sys_guid(), 1, '1', 'Serve As Fiscal Officer', null, 'Y', 'KFS-COA')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('651', sys_guid(), 1, '94', '352', 'Y')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('353', sys_guid(), 1, '1', 'Serve As Fiscal Officer Delegate', null, 'Y', 'KFS-COA')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('652', sys_guid(), 1, '93', '353', 'Y')
/

update krim_rsp_attr_data_t set attr_val = 'Account' where attr_data_id = '9'
/
update krim_rsp_attr_data_t set attr_val = 'KFS' where attr_data_id = '10'
/

INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('653', sys_guid(), 1, '62', '102', 'Y')
/
