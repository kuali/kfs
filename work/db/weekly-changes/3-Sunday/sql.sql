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
