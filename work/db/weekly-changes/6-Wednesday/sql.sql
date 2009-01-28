update krim_rsp_attr_data_t set attr_val = 'ImageAttachment' where attr_data_id = '245'
/
update krim_rsp_attr_data_t set attr_val = 'AccountsPayableTransactionalDocument' where attr_data_id = '246'
/

update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'N'
/

INSERT INTO KRIM_RSP_T(RSP_ID, OBJ_ID, VER_NBR, RSP_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD)
    VALUES('107', sys_guid(), 1, '1', null, null, 'Y', 'KFS-PURAP')
/ 
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('392', sys_guid(), 1, '107', '7', '16', 'Account')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('393', sys_guid(), 1, '107', '7', '13', 'PurchaseOrderAmendmentDocument')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('394', sys_guid(), 1, '107', '7', '41', 'false')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('395', sys_guid(), 1, '107', '7', '40', 'true')
/
INSERT INTO KRIM_RSP_RQRD_ATTR_T(RSP_RQRD_ATTR_ID, OBJ_ID, VER_NBR, RSP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)
    VALUES('128', sys_guid(), 1, '107', '22', 'Y')
/
INSERT INTO KRIM_RSP_RQRD_ATTR_T(RSP_RQRD_ATTR_ID, OBJ_ID, VER_NBR, RSP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)
    VALUES('129', sys_guid(), 1, '107', '23', 'Y')
/
INSERT INTO KRIM_ROLE_RSP_T(ROLE_RSP_ID, OBJ_ID, VER_NBR, ROLE_ID, RSP_ID, ACTV_IND)
    VALUES('1107', sys_guid(), 1, '41', '107', 'Y')
/
INSERT INTO KRIM_ROLE_RSP_ACTN_T(ROLE_RSP_ACTN_ID, OBJ_ID, VER_NBR, ACTN_TYP_CD, PRIORITY_NBR, ACTN_PLCY_CD, ROLE_MBR_ID, ROLE_RSP_ID, IGNORE_PREV_IND)
    VALUES('132', sys_guid(), 1, 'A', null, 'F', '*', '1107', 'Y')
/

INSERT INTO KRIM_RSP_T(RSP_ID, OBJ_ID, VER_NBR, RSP_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD)
    VALUES('108', sys_guid(), 1, '1', null, null, 'Y', 'KFS-PURAP')
/ 
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('396', sys_guid(), 1, '108', '7', '16', 'Account')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('397', sys_guid(), 1, '108', '7', '13', 'VendorCreditMemoDocument')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('398', sys_guid(), 1, '108', '7', '41', 'false')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('399', sys_guid(), 1, '108', '7', '40', 'true')
	/
INSERT INTO KRIM_RSP_RQRD_ATTR_T(RSP_RQRD_ATTR_ID, OBJ_ID, VER_NBR, RSP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)
    VALUES('130', sys_guid(), 1, '108', '22', 'Y')
/
INSERT INTO KRIM_RSP_RQRD_ATTR_T(RSP_RQRD_ATTR_ID, OBJ_ID, VER_NBR, RSP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)
    VALUES('131', sys_guid(), 1, '108', '23', 'Y')
/
INSERT INTO KRIM_ROLE_RSP_T(ROLE_RSP_ID, OBJ_ID, VER_NBR, ROLE_ID, RSP_ID, ACTV_IND)
    VALUES('1108', sys_guid(), 1, '41', '108', 'Y')
/
INSERT INTO KRIM_ROLE_RSP_ACTN_T(ROLE_RSP_ACTN_ID, OBJ_ID, VER_NBR, ACTN_TYP_CD, PRIORITY_NBR, ACTN_PLCY_CD, ROLE_MBR_ID, ROLE_RSP_ID, IGNORE_PREV_IND)
    VALUES('133', sys_guid(), 1, 'A', null, 'F', '*', '1108', 'Y')
/

--Recurrence	CustomerInvoiceDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '97')
/
--ProjectManagement	EffortCertificationDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '41')
/
--ImageAttachment	AccountsPayableTransactionalDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '71')
/
--Tax	PaymentRequestDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '74')
/
--Budget	PurchaseOrderDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '76')
/
--Award	PurchaseOrderDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '77')
/
--PrintTransmission	PurchaseOrderDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '79')
/
--Tax	PurchaseOrderDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '80')
/
--AccountsPayable	PurchaseOrderRemoveHoldDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '81')
/
--SeparationOfDuties	RequisitionDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '85')
/
--Organization	RequisitionDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '3')
/
--Initiator	VendorMaintenanceDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '91')
/
--Management	VendorMaintenanceDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '92')
/
