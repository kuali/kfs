alter table pdp_payee_ach_acct_t
drop constraint PDP_PAYEE_ACH_ACCT_TR1
/

delete from krns_parm_t where PARM_NM = 'B2B_PURCHASE_ORDER_PASSWORD'
/
delete from krns_parm_t where PARM_NM = 'B2B_PURCHASE_ORDER_URL'
/
delete from krns_parm_t where PARM_NM = 'B2B_ENVIRONMENT'
/
delete from krns_parm_t where PARM_NM = 'B2B_PUNCHBACK_URL'
/
delete from krns_parm_t where PARM_NM = 'B2B_PUNCHOUT_URL'
/
delete from krns_parm_t where PARM_NM = 'B2B_SHOPPING_PASSWORD'
/
delete from krns_parm_t where PARM_NM = 'B2B_USER_AGENT'
/

INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('372', sys_guid(), 1, '48', 'Edit Bank Code', null, 'Y', 'KFS-FP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('569', sys_guid(), 1, '372', '3', '13', 'CMD')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('680', sys_guid(), 1, '11', '372', 'Y')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('373', sys_guid(), 1, '48', 'Edit Bank Code', null, 'Y', 'KFS-PURAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('570', sys_guid(), 1, '373', '3', '13', 'PREQ')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('681', sys_guid(), 1, '22', '373', 'Y')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('374', sys_guid(), 1, '48', 'Edit Bank Code', null, 'Y', 'KFS-FP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('571', sys_guid(), 1, '374', '3', '13', 'DV')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('682', sys_guid(), 1, '22', '374', 'Y')
/
update krim_role_perm_t set role_id = '16' where perm_id = '220'
/

INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('683', sys_guid(), 1, '59', '334', 'Y')
/
