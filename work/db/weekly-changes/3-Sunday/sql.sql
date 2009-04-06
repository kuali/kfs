update ca_acct_delegate_t set acct_dlgt_unvl_id = '1469805442' where acct_dlgt_unvl_id = '1133303624'
/
update ca_acct_delegate_t set acct_dlgt_unvl_id = '4012108265' where acct_dlgt_unvl_id = '2585603740'
/
update ca_acct_delegate_t set acct_dlgt_unvl_id = '5368508241' where acct_dlgt_unvl_id = '3087001085'
/
update ca_acct_delegate_t set acct_dlgt_unvl_id = '5003409252' where acct_dlgt_unvl_id = '3170902843'
/

INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('354', sys_guid(), 1, '29', 'Use Screen', null, 'Y', 'KFS-FP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('511', sys_guid(), 1, '354', '12', '2', 'org.kuali.kfs.fp.document.web.struts.DisbursementVoucherHelpAction')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('654', sys_guid(), 1, '54', '354', 'Y')
/

INSERT INTO KRIM_RSP_T(RSP_ID, OBJ_ID, VER_NBR, RSP_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('115', sys_guid(), 1, '1', 'Review', null, 'Y', 'KFS-CAM')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('424', sys_guid(), 1, '115', '7', '13', 'BCIE')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('425', sys_guid(), 1, '115', '7', '16', 'Initiator')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('426', sys_guid(), 1, '115', '7', '40', 'false')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('427', sys_guid(), 1, '115', '7', '41', 'false')
/
INSERT INTO KRIM_ROLE_RSP_T(ROLE_RSP_ID, OBJ_ID, VER_NBR, ROLE_ID, RSP_ID, ACTV_IND) 
    VALUES('1116', sys_guid(), 1, '60', '115', '')
/
INSERT INTO KRIM_ROLE_RSP_ACTN_T(ROLE_RSP_ACTN_ID, OBJ_ID, VER_NBR, ACTN_TYP_CD, PRIORITY_NBR, ACTN_PLCY_CD, ROLE_MBR_ID, ROLE_RSP_ID, IGNORE_PREV_IND) 
    VALUES('165', sys_guid(), 1, 'A', null, 'F', '*', '1116', 'Y')
/

INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('655', sys_guid(), 1, '39', '206', 'Y')
/
