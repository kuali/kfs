INSERT INTO KRIM_ROLE_PERM_T VALUES ('570', sys_guid(), 1, '54', '247', 'Y')
/

delete from krim_perm_attr_data_t where TARGET_PRIMARY_KEY = '110'
/
delete from krim_perm_rqrd_attr_t where perm_id = '110'
/
delete from krim_role_perm_t where perm_id = '110'
/
delete from krim_perm_t where perm_id = '110'
/

update krim_role_perm_t set role_id = '66' where perm_id = '259'
/

INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('301', sys_guid(), 1, '29', null, null, 'Y', 'KFS-GL')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('425', sys_guid(), 1, '301', '12', '2', 'org.kuali.kfs.sys.web.struts.KualiBalanceInquiryReportMenuAction')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('571', sys_guid(), 1, '54', '301', 'Y')
/
