update krew_doc_typ_t set lbl='Role' where doc_typ_nm='IdentityManagementRoleDocument'
/

CREATE TABLE KRIM_PND_DLGN_T ( 
    FDOC_NBR        VARCHAR2(14)  NOT NULL,
    DLGN_ID     	VARCHAR2(40) NOT NULL,
    ROLE_ID     	VARCHAR2(40) NOT NULL,
    OBJ_ID      	VARCHAR2(36) NOT NULL,
    VER_NBR     	NUMBER(8,0) DEFAULT 1 NOT NULL,
    KIM_TYP_ID  	VARCHAR2(40) NULL,
    DLGN_TYP_CD    	VARCHAR2(100) NOT NULL,
    ACTV_IND    	VARCHAR2(1) DEFAULT 'Y' NULL,
    PRIMARY KEY(DLGN_ID,FDOC_NBR)
)
/
CREATE TABLE KRIM_PND_DLGN_MBR_T
(
    FDOC_NBR        VARCHAR2(14) NOT NULL,
    DLGN_MBR_ID  	VARCHAR2(40) NOT NULL,
    OBJ_ID      	VARCHAR2(36) NOT NULL,
    VER_NBR     	NUMBER(8,0)  DEFAULT 1 NOT NULL,
    DLGN_ID      	VARCHAR2(40) NOT NULL,
    MBR_ID   		VARCHAR2(40),
    MBR_NM			VARCHAR2(40),
    MBR_TYP_CD		VARCHAR2(40) NOT NULL,
    ACTV_IND    	VARCHAR2(1)  DEFAULT 'Y' NULL,
    ACTV_FRM_DT  	DATE NULL,
    ACTV_TO_DT  	DATE NULL,
    PRIMARY KEY(DLGN_MBR_ID,FDOC_NBR)
)
/
CREATE TABLE KRIM_PND_DLGN_MBR_ATTR_DATA_T ( 
    FDOC_NBR          VARCHAR2(14)  NOT NULL,
    ATTR_DATA_ID      	VARCHAR2(40) NOT NULL,
    OBJ_ID            	VARCHAR2(36) NOT NULL,
    VER_NBR           	NUMBER(8,0) DEFAULT 1 NOT NULL,
    TARGET_PRIMARY_KEY	VARCHAR2(40) NULL,
    KIM_TYP_ID        	VARCHAR2(40) NULL,
    KIM_ATTR_DEFN_ID  	VARCHAR2(40) NULL,
    ATTR_VAL          	VARCHAR2(400) NULL,
    ACTV_IND    	VARCHAR2(1) DEFAULT 'Y' NULL,
    EDIT_FLAG    	VARCHAR2(1) DEFAULT 'N' NULL,
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
