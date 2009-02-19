update krew_doc_typ_t set lbl='Role' where doc_typ_nm='IdentityManagementRoleDocument'
/

CREATE TABLE KRIM_PND_DLGN_T ( 
    FDOC_NBR        VARCHAR2(14)  NOT NULL,
    DLGN_ID         VARCHAR2(40) NOT NULL,
    ROLE_ID         VARCHAR2(40) NOT NULL,
    OBJ_ID          VARCHAR2(36) NOT NULL,
    VER_NBR         NUMBER(8,0) DEFAULT 1 NOT NULL,
    KIM_TYP_ID      VARCHAR2(40) NULL,
    DLGN_TYP_CD        VARCHAR2(100) NOT NULL,
    ACTV_IND        VARCHAR2(1) DEFAULT 'Y' NULL,
    PRIMARY KEY(DLGN_ID,FDOC_NBR)
)
/
CREATE TABLE KRIM_PND_DLGN_MBR_T
(
    FDOC_NBR        VARCHAR2(14) NOT NULL,
    DLGN_MBR_ID      VARCHAR2(40) NOT NULL,
    OBJ_ID          VARCHAR2(36) NOT NULL,
    VER_NBR         NUMBER(8,0)  DEFAULT 1 NOT NULL,
    DLGN_ID          VARCHAR2(40) NOT NULL,
    MBR_ID           VARCHAR2(40),
    MBR_NM            VARCHAR2(40),
    MBR_TYP_CD        VARCHAR2(40) NOT NULL,
    ACTV_IND        VARCHAR2(1)  DEFAULT 'Y' NULL,
    ACTV_FRM_DT      DATE NULL,
    ACTV_TO_DT      DATE NULL,
    PRIMARY KEY(DLGN_MBR_ID,FDOC_NBR)
)
/
CREATE TABLE KRIM_PND_DLGN_MBR_ATTR_DATA_T ( 
    FDOC_NBR          VARCHAR2(14)  NOT NULL,
    ATTR_DATA_ID          VARCHAR2(40) NOT NULL,
    OBJ_ID                VARCHAR2(36) NOT NULL,
    VER_NBR               NUMBER(8,0) DEFAULT 1 NOT NULL,
    TARGET_PRIMARY_KEY    VARCHAR2(40) NULL,
    KIM_TYP_ID            VARCHAR2(40) NULL,
    KIM_ATTR_DEFN_ID      VARCHAR2(40) NULL,
    ATTR_VAL              VARCHAR2(400) NULL,
    ACTV_IND        VARCHAR2(1) DEFAULT 'Y' NULL,
    EDIT_FLAG        VARCHAR2(1) DEFAULT 'N' NULL,
    PRIMARY KEY(ATTR_DATA_ID,FDOC_NBR)
)
/

update krns_parm_t set PARM_DESC_TXT = 'Controls whether the bank code functionality is enabled in the system. If set to Y additional bank entries will be created on supported documents. Also when set to Y document types that appear in the BANK_CODE_DOCUMENT_TYPES parameter list will display the bank code for viewing and editing by users who have permission.' where PARM_NM = 'ENABLE_BANK_SPECIFICATION_IND'
/

delete from krim_role_perm_t where perm_id in  ('153', '154')
/
delete from krim_perm_attr_data_t where target_primary_key in  ('153', '154')
/
delete from krim_perm_t where perm_tmpl_id = '39'
/
delete from krim_perm_tmpl_t where perm_tmpl_id = '39'
/
delete from krim_typ_attr_t where kim_typ_id = '22'
/
delete from krim_typ_t where kim_typ_id = '22'
/
delete from krim_attr_defn_t where kim_attr_defn_id = '17'
/
delete from ca_acct_delegate_t dlgt
where not exists (select *
from krew_doc_typ_t
where doc_typ_nm = dlgt.fdoc_typ_cd)
and fdoc_typ_cd != 'ALL'
/
insert into ca_acct_delegate_t ( select 'BL', '1020087',    'REQS', a.prncpl_id, sys_guid(), 1, 0, 'N', 'Y', null, 0   from krim_prncpl_t a  where prncpl_nm = 'dstanfil' )
/
insert into ca_acct_delegate_t ( select 'BL', '1031420',    'PREQ', a.prncpl_id, sys_guid(), 1, 0, 'Y', 'Y', null, 5000   from krim_prncpl_t a  where prncpl_nm = 'fhogg' )
/
insert into ca_acct_delegate_t ( select 'BL', '1031420',    'PREQ', a.prncpl_id, sys_guid(), 1, 0, 'N', 'Y', null, 1000   from krim_prncpl_t a  where prncpl_nm = 'jtbrockw' )
/
insert into ca_acct_delegate_t ( select 'BL', '1031420',    'PREQ', a.prncpl_id, sys_guid(), 1, 0, 'N', 'Y', null, 500   from krim_prncpl_t a  where prncpl_nm = 'jfaust' )
/
insert into ca_acct_delegate_t ( select 'IN', '1292016',    'ALL', a.prncpl_id, sys_guid(), 1, 0, 'N', 'Y', null, 0   from krim_prncpl_t a  where prncpl_nm = 'bramer' )
/
insert into ca_acct_delegate_t ( select 'IN', '1292016',    'PREQ', a.prncpl_id, sys_guid(), 1, 0, 'N', 'Y', null, 0   from krim_prncpl_t a  where prncpl_nm = 'dbaer' )
/
insert into ca_acct_delegate_t ( select 'IN', '1292016',    'PREQ', a.prncpl_id, sys_guid(), 1, 0, 'Y', 'Y', null, 0   from krim_prncpl_t a  where prncpl_nm = 'dlcarl' )
/
insert into ca_acct_delegate_t ( select 'IN', '1292087',    'ALL', a.prncpl_id, sys_guid(), 1, 0, 'N', 'Y', null, 0   from krim_prncpl_t a  where prncpl_nm = 'bramer' )
/
insert into ca_acct_delegate_t ( select 'IN', '1292087',    'PREQ', a.prncpl_id, sys_guid(), 1, 0, 'N', 'Y', null, 0   from krim_prncpl_t a  where prncpl_nm = 'dbaer' )
/
insert into ca_acct_delegate_t ( select 'IN', '1293716',    'ALL', a.prncpl_id, sys_guid(), 1, 0, 'N', 'Y', null, 0   from krim_prncpl_t a  where prncpl_nm = 'bramer' )
/
insert into ca_acct_delegate_t ( select 'IN', '1293716',    'PREQ', a.prncpl_id, sys_guid(), 1, 0, 'Y', 'Y', null, 0   from krim_prncpl_t a  where prncpl_nm = 'dlcarl' )
/
insert into ca_acct_delegate_t ( select 'BL', '2231401',    'PREQ', a.prncpl_id, sys_guid(), 1, 0, 'N', 'Y', null, 0   from krim_prncpl_t a  where prncpl_nm = 'jtbrockw' )
/
insert into ca_acct_delegate_t ( select 'BL', '2231405',    'PREQ', a.prncpl_id, sys_guid(), 1, 1000, 'Y', 'Y', null, 5000   from krim_prncpl_t a  where prncpl_nm = 'fhogg' )
/
insert into ca_acct_delegate_t ( select 'BL', '2231405',    'PREQ', a.prncpl_id, sys_guid(), 1, 500, 'N', 'Y', null, 1000   from krim_prncpl_t a  where prncpl_nm = 'jtbrockw' )
/
insert into ca_acct_delegate_t ( select 'BL', '2231405',    'PREQ', a.prncpl_id, sys_guid(), 1, 50, 'N', 'Y', null, 500   from krim_prncpl_t a  where prncpl_nm = 'jfaust' )
/
insert into ca_acct_delegate_t ( select 'BL', '2231453',    'PREQ', a.prncpl_id, sys_guid(), 1, 0, 'Y', 'Y', null, 0   from krim_prncpl_t a  where prncpl_nm = 'fhogg' )
/
insert into ca_acct_delegate_t ( select 'BL', '2231453',    'PREQ', a.prncpl_id, sys_guid(), 1, 0, 'N', 'Y', null, 0   from krim_prncpl_t a  where prncpl_nm = 'jtbrockw' )
/
insert into ca_acct_delegate_t ( select 'BL', '2231453',    'PREQ', a.prncpl_id, sys_guid(), 1, 0, 'N', 'Y', null, 0   from krim_prncpl_t a  where prncpl_nm = 'jfaust' )
/
insert into ca_acct_delegate_t ( select 'BL', '2231454',    'PREQ', a.prncpl_id, sys_guid(), 1, 0, 'N', 'Y', null, 0   from krim_prncpl_t a  where prncpl_nm = 'jtbrockw' )
/
insert into ca_acct_delegate_t ( select 'BL', '2231454',    'PREQ', a.prncpl_id, sys_guid(), 1, 0, 'N', 'Y', null, 0   from krim_prncpl_t a  where prncpl_nm = 'jfaust' )
/
insert into ca_acct_delegate_t ( select 'IN', '2283072',    'ALL', a.prncpl_id, sys_guid(), 1, 0, 'Y', 'Y', null, 0   from krim_prncpl_t a  where prncpl_nm = 'bhoneycu' )
/
insert into ca_acct_delegate_t ( select 'IN', '2283072',    'PREQ', a.prncpl_id, sys_guid(), 1, 0, 'N', 'Y', null, 0   from krim_prncpl_t a  where prncpl_nm = 'dbaer' )
/
insert into ca_acct_delegate_t ( select 'UA', '2512891',    'PREQ', a.prncpl_id, sys_guid(), 1, 0, 'Y', 'Y', null, 0   from krim_prncpl_t a  where prncpl_nm = 'fhogg' )
/
insert into ca_acct_delegate_t ( select 'UA', '2512891',    'PREQ', a.prncpl_id, sys_guid(), 1, 0, 'N', 'Y', null, 0   from krim_prncpl_t a  where prncpl_nm = 'jtbrockw' )
/
insert into ca_acct_delegate_t ( select 'UA', '2512891',    'PREQ', a.prncpl_id, sys_guid(), 1, 0, 'N', 'Y', null, 0   from krim_prncpl_t a  where prncpl_nm = 'jfaust' )
/
insert into ca_acct_delegate_t ( select 'UA', '2512892',    'PREQ', a.prncpl_id, sys_guid(), 1, 0, 'N', 'Y', null, 0   from krim_prncpl_t a  where prncpl_nm = 'jtbrockw' )
/
insert into ca_acct_delegate_t ( select 'IN', '4684206',    'ALL', a.prncpl_id, sys_guid(), 1, 0, 'Y', 'Y', null, 0   from krim_prncpl_t a  where prncpl_nm = 'bhoneycu' )
/
insert into ca_acct_delegate_t ( select 'IN', '4894706',    'ALL', a.prncpl_id, sys_guid(), 1, 0, 'N', 'Y', null, 0   from krim_prncpl_t a  where prncpl_nm = 'bramer' )
/
insert into krew_doc_typ_t (DOC_TYP_ID, PARNT_ID, DOC_TYP_NM, DOC_TYP_VER_NBR, ACTV_IND, CUR_IND, LBL, VER_NBR, RTE_VER_NBR, OBJ_ID)
    values (KREW_DOC_HDR_S.NEXTVAL, 320880, 'ID01', 0, 1, 1, 'Physical Plant-Bloomington', 1, 2, sys_guid())
/
insert into krew_doc_typ_t (DOC_TYP_ID, PARNT_ID, DOC_TYP_NM, DOC_TYP_VER_NBR, ACTV_IND, CUR_IND, LBL, VER_NBR, RTE_VER_NBR, OBJ_ID)
    values (KREW_DOC_HDR_S.NEXTVAL, 320880, 'ID02', 0, 1, 1, 'Physical Plant-Indianapolis', 1, 2, sys_guid())
/
insert into krew_doc_typ_t (DOC_TYP_ID, PARNT_ID, DOC_TYP_NM, DOC_TYP_VER_NBR, ACTV_IND, CUR_IND, LBL, VER_NBR, RTE_VER_NBR, OBJ_ID)
    values (KREW_DOC_HDR_S.NEXTVAL, 320880, 'ID03', 0, 1, 1, 'Motor Vehicles', 1, 2, sys_guid())
/
insert into krew_doc_typ_t (DOC_TYP_ID, PARNT_ID, DOC_TYP_NM, DOC_TYP_VER_NBR, ACTV_IND, CUR_IND, LBL, VER_NBR, RTE_VER_NBR, OBJ_ID)
    values (KREW_DOC_HDR_S.NEXTVAL, 320880, 'ID04', 0, 1, 1, 'Food Stores', 1, 2, sys_guid())
/
insert into krew_doc_typ_t (DOC_TYP_ID, PARNT_ID, DOC_TYP_NM, DOC_TYP_VER_NBR, ACTV_IND, CUR_IND, LBL, VER_NBR, RTE_VER_NBR, OBJ_ID)
    values (KREW_DOC_HDR_S.NEXTVAL, 320880, 'EX01', 0, 1, 1, 'HP Products', 1, 2, sys_guid())
/
insert into krew_doc_typ_t (DOC_TYP_ID, PARNT_ID, DOC_TYP_NM, DOC_TYP_VER_NBR, ACTV_IND, CUR_IND, LBL, VER_NBR, RTE_VER_NBR, OBJ_ID)
    values (KREW_DOC_HDR_S.NEXTVAL, 320880, 'EX02', 0, 1, 1, 'IKON', 1, 2, sys_guid())
/
insert into krew_doc_typ_t (DOC_TYP_ID, PARNT_ID, DOC_TYP_NM, DOC_TYP_VER_NBR, ACTV_IND, CUR_IND, LBL, VER_NBR, RTE_VER_NBR, OBJ_ID)
    values (KREW_DOC_HDR_S.NEXTVAL, 320880, 'EX03', 0, 1, 1, 'Shell', 1, 2, sys_guid())
/
insert into krew_doc_typ_t (DOC_TYP_ID, PARNT_ID, DOC_TYP_NM, DOC_TYP_VER_NBR, ACTV_IND, CUR_IND, LBL, VER_NBR, RTE_VER_NBR, OBJ_ID)
    values (KREW_DOC_HDR_S.NEXTVAL, 320880, 'EX04', 0, 1, 1, 'Ameritech Paging', 1, 2, sys_guid())
/
delete ca_account_t
where fin_coa_cd = 'UA'
and account_nbr = '1912610'
/ 