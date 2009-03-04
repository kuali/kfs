INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('611', sys_guid(), 1, '62', '95', 'Y')
/

INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('329', sys_guid(), 1, '29', null, null, 'Y', 'KFS-FP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('475', sys_guid(), 1, '329', '12', '2', 'org.kuali.kfs.fp.document.web.struts.CashManagementStatusAction')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('612', sys_guid(), 1, '11', '329', 'Y')
/
insert into krns_parm_t values
('KFS-FP','GeneralErrorCorrection','INVALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE',
sys_guid(), 2, 'VALID','ES=CF,CM,WO',
'Defines an invalid relationship between an Object Type and the Object Sub-Type(s) on the General Error Correction document. Format of list is object type 1=object sub type 1, object sub type 2;object type 2=object sub type 3,object sub type 4,object sub type 5.',
'D')
/
insert into krns_parm_t values
('KFS-FP','GeneralErrorCorrection','VALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE',
sys_guid(), 2, 'VALID','',
'Defines a valid relationship between an Object Type and the Object Sub-Type(s) on the General Error Correction document. Format of list is object type 1=object sub type 1, object sub type 2;object type 2=object sub type 3,object sub type 4,object sub type 5',
'D')
/
alter table KRIM_DLGN_MBR_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table KRIM_DLGN_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table KRIM_GRP_MBR_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table KRIM_ROLE_MBR_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table AP_CRDT_MEMO_ACCT_CHG_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table AP_ELCTRNC_INV_MAP_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table AP_ELCTRNC_INV_RJT_ITM_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table AR_INV_RCURRNC_DTL_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table AR_WRITEOFF_DOC_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table CA_ICR_AUTO_ENTR_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table CB_AST_TRN_TYP_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table FP_CASHIER_ITM_IN_PROC_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table FP_CSH_DRWR_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table FP_JV_ACCT_LINES_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table FS_DOC_TYP_CD_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table LD_LDGR_BAL_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table PUR_BLK_RCVNG_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table PUR_CPTL_AST_SYS_ST_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table PUR_SNSTV_DTA_ASGN_DTL_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table PUR_SNSTV_DTA_ASGN_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table PUR_SNSTV_DTA_T  modify (ver_nbr NUMBER(8,0) default 1)
/
alter table KRNS_NMSPC_T add (APPL_NMSPC_CD VARCHAR2(20)); 