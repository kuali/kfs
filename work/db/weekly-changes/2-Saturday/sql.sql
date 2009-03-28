INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('335', sys_guid(), 1, '26', 'Modify Maintenance Document Field', null, 'Y', 'KFS-VND')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('484', sys_guid(), 1, '335', '11', '5', 'VendorDetail')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('485', sys_guid(), 1, '335', '11', '6', 'vendorHeader.vendorTaxTypeCode')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('619', sys_guid(), 1, '49', '335', 'Y')
/

update krim_rsp_attr_data_t set attr_val = 'FinancialSystemTransactionalDocument' where attr_data_id = '294'
/

update krim_role_rsp_t set role_id = '38' where rsp_id = '95'
/
update krim_role_perm_t set role_id = '38' where role_perm_id = '267'
/

update krim_rsp_t set nmspc_cd = 'KFS-SYS' where rsp_id in ('12', '29')
/
update krim_rsp_attr_data_t set attr_val = 'KFS' where attr_data_id = '9'
/
delete from krim_role_rsp_actn_t where role_rsp_id in (select role_rsp_id from krim_role_rsp_t where rsp_id in ('102', '103', '104', '105', '13', '14', '20', '21', '27', '36', '37', '39', '40', '42', '45', '52', '55', '57', '59', '60', '62', '63', '64', '66', '69', '72', '84', '98'))
/
delete from krim_role_rsp_t where rsp_id in ('102', '103', '104', '105', '13', '14', '20', '21', '27', '36', '37', '39', '40', '42', '45', '52', '55', '57', '59', '60', '62', '63', '64', '66', '69', '72', '84', '98')
/
delete from krim_rsp_rqrd_attr_t where rsp_id in ('102', '103', '104', '105', '13', '14', '20', '21', '27', '36', '37', '39', '40', '42', '45', '52', '55', '57', '59', '60', '62', '63', '64', '66', '69', '72', '84', '98')
/
delete from krim_rsp_attr_data_t where target_primary_key in  ('102', '103', '104', '105', '13', '14', '20', '21', '27', '36', '37', '39', '40', '42', '45', '52', '55', '57', '59', '60', '62', '63', '64', '66', '69', '72', '84', '98')
/
delete from krim_rsp_t where RSP_ID in ('102', '103', '104', '105', '13', '14', '20', '21', '27', '36', '37', '39', '40', '42', '45', '52', '55', '57', '59', '60', '62', '63', '64', '66', '69', '72', '84', '98')
/

update krim_rsp_t set nmspc_cd = 'KFS-SYS' where rsp_id = '31'
/
update krim_rsp_attr_data_t set attr_val = 'KFS' where target_primary_key = '31' and kim_attr_defn_id = '13'
/
delete from krim_role_rsp_actn_t where role_rsp_id in (select role_rsp_id from krim_role_rsp_t where rsp_id in ('43', '47', '53', '54', '56', '58', '61', '65', '67', '68', '70', '77', '110'))
/
delete from krim_role_rsp_t where rsp_id in ('43', '47', '53', '54', '56', '58', '61', '65', '67', '68', '70', '77', '110')
/
delete from krim_rsp_rqrd_attr_t where rsp_id in ('43', '47', '53', '54', '56', '58', '61', '65', '67', '68', '70', '77', '110')
/
delete from krim_rsp_attr_data_t where target_primary_key in  ('43', '47', '53', '54', '56', '58', '61', '65', '67', '68', '70', '77', '110')
/
delete from krim_rsp_t where RSP_ID in ('43', '47', '53', '54', '56', '58', '61', '65', '67', '68', '70', '77', '110')
/

update krim_rsp_t set nmspc_cd = 'KFS-SYS' where rsp_id = '26'
/
update krim_rsp_attr_data_t set attr_val = 'KFS' where target_primary_key = '26' and kim_attr_defn_id = '13'
/
delete from krim_role_rsp_actn_t where role_rsp_id in (select role_rsp_id from krim_role_rsp_t where rsp_id in ('28', '30', '32', '33', '34', '35', '38'))
/
delete from krim_role_rsp_t where rsp_id in ('28', '30', '32', '33', '34', '35', '38')
/
delete from krim_rsp_rqrd_attr_t where rsp_id in ('28', '30', '32', '33', '34', '35', '38')
/
delete from krim_rsp_attr_data_t where target_primary_key in  ('28', '30', '32', '33', '34', '35', '38')
/
delete from krim_rsp_t where RSP_ID in ('28', '30', '32', '33', '34', '35', '38')
/

update krim_rsp_attr_data_t set attr_val = 'AccountsReceivableComplexMaintenanceDocument' where target_primary_key = '100' and kim_attr_defn_id = '13'
/
update krim_rsp_attr_data_t set attr_val = 'AccountsReceivableTransactionalDocument' where target_primary_key = '101' and kim_attr_defn_id = '13'
/
delete from krim_role_rsp_actn_t where role_rsp_id in (select role_rsp_id from krim_role_rsp_t where rsp_id in ('15', '106'))
/
delete from krim_role_rsp_t where rsp_id in ('15', '106')
/
delete from krim_rsp_rqrd_attr_t where rsp_id in ('15', '106')
/
delete from krim_rsp_attr_data_t where target_primary_key in  ('15', '106')
/
delete from krim_rsp_t where RSP_ID in ('15', '106')
/

INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('336', sys_guid(), 1, '9', 'Ad Hoc Review Document', null, 'Y', 'KFS-CG')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('486', sys_guid(), 1, '336', '5', '13', 'KRFD')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('487', sys_guid(), 1, '336', '5', '14', 'A')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('620', sys_guid(), 1, '54', '336', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('621', sys_guid(), 1, '32', '336', 'Y')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('337', sys_guid(), 1, '9', 'Ad Hoc Review Document', null, 'Y', 'KFS-VND')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('488', sys_guid(), 1, '337', '5', '13', 'PVEN')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('489', sys_guid(), 1, '337', '5', '14', 'A')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('622', sys_guid(), 1, '54', '337', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('623', sys_guid(), 1, '32', '337', 'Y')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('338', sys_guid(), 1, '9', 'Ad Hoc Review Document', null, 'Y', 'KFS-PURAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('490', sys_guid(), 1, '338', '5', '13', 'ReceivingTransactionalDocument')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('491', sys_guid(), 1, '338', '5', '14', 'A')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('624', sys_guid(), 1, '54', '338', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('625', sys_guid(), 1, '32', '338', 'Y')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('339', sys_guid(), 1, '9', 'Ad Hoc Review Document', null, 'Y', 'KFS-PURAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('492', sys_guid(), 1, '339', '5', '13', 'PORT')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('493', sys_guid(), 1, '339', '5', '14', 'A')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('626', sys_guid(), 1, '54', '339', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('627', sys_guid(), 1, '32', '339', 'Y')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('340', sys_guid(), 1, '9', 'Ad Hoc Review Document', null, 'Y', 'KFS-PURAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('494', sys_guid(), 1, '340', '5', '13', 'POC')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('495', sys_guid(), 1, '340', '5', '14', 'A')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('628', sys_guid(), 1, '54', '340', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('629', sys_guid(), 1, '32', '340', 'Y')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('341', sys_guid(), 1, '9', 'Ad Hoc Review Document', null, 'Y', 'KFS-PURAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('496', sys_guid(), 1, '341', '5', '13', 'REQS')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('497', sys_guid(), 1, '341', '5', '14', 'A')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('630', sys_guid(), 1, '54', '341', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('631', sys_guid(), 1, '32', '341', 'Y')
/

-- remove KFS-CAM Manager and add KFS-SYS Plant Fund Accountant, KFS-SYS Asset Manager for capitalAssetInServiceDate, organizationOwnerAccountNumber, organizationOwnerChartOfAccountsCode
update krim_role_perm_t set role_id = '34' where perm_id in ('32', '33', '34')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('632', sys_guid(), 1, '46', '32', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('633', sys_guid(), 1, '46', '33', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('634', sys_guid(), 1, '46', '34', 'Y')
/
-- add modify maint doc field perms for capitalAssetTypeCode, capitalAssetDescription, campusTagNumber and grant to KFS-CAM Manager and KFS-SYS Asset Processor
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('342', sys_guid(), 1, '26', 'Modify Maintenance Document Field', null, 'Y', 'KFS-CAM')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('498', sys_guid(), 1, '342', '11', '5', 'Asset')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('499', sys_guid(), 1, '342', '11', '6', 'capitalAssetTypeCode')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('635', sys_guid(), 1, '35', '342', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('636', sys_guid(), 1, '5', '342', 'Y')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('343', sys_guid(), 1, '26', 'Modify Maintenance Document Field', null, 'Y', 'KFS-CAM')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('500', sys_guid(), 1, '343', '11', '5', 'Asset')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('501', sys_guid(), 1, '343', '11', '6', 'capitalAssetDescription')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('637', sys_guid(), 1, '35', '343', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('638', sys_guid(), 1, '5', '343', 'Y')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('344', sys_guid(), 1, '26', 'Modify Maintenance Document Field', null, 'Y', 'KFS-CAM')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('502', sys_guid(), 1, '344', '11', '5', 'Asset')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('503', sys_guid(), 1, '344', '11', '6', 'campusTagNumber')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('639', sys_guid(), 1, '35', '344', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('640', sys_guid(), 1, '5', '344', 'Y')
/
-- add Perform Custom Maintenance Document Function perms for calculateEqualSourceAmountsButton, calculateSeparateSourceRemainingAmountButton buttons and grant to KFS-SYS Asset Processor and KFS-CAM Manager
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('346', sys_guid(), 1, '30', 'Perform Custom Maintenance Document Function', null, 'Y', 'KFS-CAM')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('505', sys_guid(), 1, '346', '13', '3', 'calculateEqualSourceAmountsButton')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('642', sys_guid(), 1, '35', '346', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('643', sys_guid(), 1, '5', '346', 'Y')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('347', sys_guid(), 1, '30', 'Perform Custom Maintenance Document Function', null, 'Y', 'KFS-CAM')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('506', sys_guid(), 1, '347', '13', '3', 'calculateSeparateSourceRemainingAmountButton')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('644', sys_guid(), 1, '35', '347', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('645', sys_guid(), 1, '5', '347', 'Y')
/
-- add View Inquiry or Maintenance Document Field permission for Asset oldTagNumber - KFS-SYS Asset Processor and KFS-CAM Manager
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('348', sys_guid(), 1, '25', 'View Inquiry or Maintenance Document Field', null, 'Y', 'KFS-CAM')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('507', sys_guid(), 1, '348', '11', '5', 'Asset')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('508', sys_guid(), 1, '348', '11', '6', 'oldTagNumber')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('646', sys_guid(), 1, '35', '348', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('647', sys_guid(), 1, '5', '348', 'Y')
/
