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

INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('286', sys_guid(), 1, '31', null, null, 'Y', 'KFS-PURAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('416', sys_guid(), 1, '286', '14', '10', 'requestPaymentRequestHold')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('546', sys_guid(), 1, '22', '286', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('547', sys_guid(), 1, '59', '286', 'Y')
/

INSERT INTO KRIM_RSP_T(RSP_ID, OBJ_ID, VER_NBR, RSP_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('105', sys_guid(), 1, '1', null, null, 'Y', 'KFS-AR')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('384', sys_guid(), 1, '105', '7', '16', 'Account')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('385', sys_guid(), 1, '105', '7', '13', 'CustomerInvoiceDocument')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('386', sys_guid(), 1, '105', '7', '41', 'false')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('387', sys_guid(), 1, '105', '7', '40', 'true')
/
INSERT INTO KRIM_RSP_RQRD_ATTR_T(RSP_RQRD_ATTR_ID, OBJ_ID, VER_NBR, RSP_ID, KIM_ATTR_DEFN_ID, ACTV_IND) 
    VALUES('126', sys_guid(), 1, '105', '22', 'Y')
/
INSERT INTO KRIM_RSP_RQRD_ATTR_T(RSP_RQRD_ATTR_ID, OBJ_ID, VER_NBR, RSP_ID, KIM_ATTR_DEFN_ID, ACTV_IND) 
    VALUES('127', sys_guid(), 1, '105', '23', 'Y')
/
INSERT INTO KRIM_ROLE_RSP_T(ROLE_RSP_ID, OBJ_ID, VER_NBR, ROLE_ID, RSP_ID, ACTV_IND) 
    VALUES('1105', sys_guid(), 1, '41', '105', 'Y')
/
INSERT INTO KRIM_ROLE_RSP_ACTN_T(ROLE_RSP_ACTN_ID, OBJ_ID, VER_NBR, ACTN_TYP_CD, PRIORITY_NBR, ACTN_PLCY_CD, ROLE_MBR_ID, ROLE_RSP_ID) 
    VALUES('130', sys_guid(), 1, 'A', null, 'F', '*', '1105')
/

INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('287', sys_guid(), 1, '29', null, null, 'Y', 'KFS-GL')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('417', sys_guid(), 1, '287', '12', '2', 'org.kuali.kfs.gl.web.struts.BalanceInquiryAction')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('549', sys_guid(), 1, '54', '287', 'Y')
/

INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('288', sys_guid(), 1, '29', null, null, 'Y', 'KFS-AR')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('418', sys_guid(), 1, '288', '12', '2', 'org.kuali.kfs.module.ar.web.struts.CustomerAgingReportAction')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('548', sys_guid(), 1, '2', '288', 'Y')
/

update krim_perm_t set nm = null where perm_tmpl_id <> '1'
/