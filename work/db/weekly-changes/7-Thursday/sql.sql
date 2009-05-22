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

INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD)
    VALUES('375', sys_guid(), 1, '23', 'Look Up Records', null, 'Y', 'KFS-CAB')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('572', sys_guid(), 1, '371', '10', '5', 'Pretag')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
    VALUES('684', sys_guid(), 1, '6', '371', 'Y')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD)
    VALUES('376', sys_guid(), 1, '42', 'Create / Maintain Record(s)', null, 'Y', 'KFS-CAB')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('573', sys_guid(), 1, '372', '56', '13', 'PTAG')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
    VALUES('685', sys_guid(), 1, '6', '372', 'Y')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD)
    VALUES('377', sys_guid(), 1, '1', 'Edit When Tagged Prior Fiscal Year', null, 'Y', 'KFS-CAM')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
    VALUES('686', sys_guid(), 1, '5', '373', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
    VALUES('687', sys_guid(), 1, '35', '373', 'Y')
/
UPDATE KRIM_ROLE_PERM_T SET ROLE_ID=5 WHERE ROLE_PERM_ID=55
/
UPDATE KRIM_ROLE_PERM_T SET ROLE_ID=35 WHERE ROLE_PERM_ID=54
/
UPDATE KRIM_ROLE_PERM_T SET ROLE_ID=5 WHERE ROLE_PERM_ID=57
/
UPDATE KRIM_ROLE_PERM_T SET ROLE_ID=35 WHERE ROLE_PERM_ID=56
/
UPDATE KRIM_ROLE_PERM_T SET ROLE_ID=5 WHERE ROLE_PERM_ID=61
/
UPDATE KRIM_ROLE_PERM_T SET ROLE_ID=35 WHERE ROLE_PERM_ID=60
/
DELETE FROM KRIM_PERM_ATTR_DATA_T WHERE PERM_ID=31
/
DELETE FROM KRIM_ROLE_PERM_T WHERE PERM_ID=31
/
DELETE FROM KRIM_PERM_T WHERE PERM_ID=31
/
DELETE FROM KRIM_PERM_ATTR_DATA_T WHERE PERM_ID=30
/
DELETE FROM KRIM_ROLE_PERM_T WHERE PERM_ID=30
/
DELETE FROM KRIM_PERM_T WHERE PERM_ID=30
/

delete from krim_role_perm_t where perm_id in ('214', '215', '278')
/
delete from krim_perm_attr_data_t where perm_id in ('214', '215', '278')
/
delete from krim_perm_t where perm_id in ('214', '215', '278')
/
update krim_perm_attr_data_t set attr_val = 'amendmentEntry' where attr_data_id = '313'
/

update krim_perm_attr_data_t set attr_val = 'POA' where attr_data_id = '534'
/
update krim_perm_attr_data_t set attr_val = 'AP' where attr_data_id = '535'
/
update krim_perm_attr_data_t set attr_val = 'PREQ' where attr_data_id = '536'
/
update krim_perm_attr_data_t set attr_val = 'POA' where attr_data_id = '537'
/
update krim_perm_attr_data_t set attr_val = 'PO' where attr_data_id = '538'
/
update krim_perm_attr_data_t set attr_val = 'PO' where attr_data_id = '539'
/
update krim_perm_attr_data_t set attr_val = 'PO' where attr_data_id = '540'
/
update krim_perm_attr_data_t set attr_val = 'PO' where attr_data_id = '541'
/
update krim_perm_attr_data_t set attr_val = 'PREQ' where attr_data_id = '542'
/
update krim_perm_attr_data_t set attr_val = 'PREQ' where attr_data_id = '543'
/
update krim_perm_attr_data_t set attr_val = 'PREQ' where attr_data_id = '545'
/
update krim_perm_attr_data_t set attr_val = 'PREQ' where attr_data_id = '546'
/
update krim_perm_attr_data_t set attr_val = 'PREQ' where attr_data_id = '547'
/
update krim_perm_attr_data_t set attr_val = 'CM' where attr_data_id = '548'
/
update krim_perm_attr_data_t set attr_val = 'CM' where attr_data_id = '549'
/
