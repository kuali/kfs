drop table GL_ORIGIN_ENTRY_GRP_T
/
drop table GL_ORIGIN_ENTRY_SRC_T
/
drop table GL_ORIGIN_ENTRY_T
/
drop table LD_LBR_ORIGIN_ENTRY_T
/
alter table
   GL_COR_DOC_T
drop
   (GL_COR_INP_GRP_ID, GL_COR_OUT_GRP_ID)
/

-- Create permission template for Send Ad Hoc Request - action request type is detail
-- Grant Send Ad Hoc Request for FYI and ACK to Document Opener role
-- Grant Add Note / Attachment base perm to Document Opener role (double check purap special attachment type perms to make sure they still make sense - they should)
-- Grant Send Ad Hoc Request for Approve to Document Editor role 
INSERT INTO KRIM_PERM_TMPL_T(PERM_TMPL_ID, OBJ_ID, VER_NBR, NM, DESC_TXT, KIM_TYP_ID, ACTV_IND, NMSPC_CD) 
    VALUES('49', sys_guid(), 1, 'Send Ad Hoc Request', null, '5', 'Y', 'KR-NS')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('332', sys_guid(), 1, '49', 'Send Ad Hoc Request', null, 'Y', 'KR-SYS')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('478', sys_guid(), 1, '332', '5', '13', 'KualiDocument')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('479', sys_guid(), 1, '332', '5', '14', 'F')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('615', sys_guid(), 1, '83', '332', 'Y')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('333', sys_guid(), 1, '49', 'Send Ad Hoc Request', null, 'Y', 'KR-SYS')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('480', sys_guid(), 1, '333', '5', '13', 'KualiDocument')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('481', sys_guid(), 1, '333', '5', '14', 'K')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('616', sys_guid(), 1, '83', '333', 'Y')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('334', sys_guid(), 1, '49', 'Send Ad Hoc Request', null, 'Y', 'KR-SYS')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('482', sys_guid(), 1, '334', '5', '13', 'KualiDocument')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('483', sys_guid(), 1, '334', '5', '14', 'A')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('617', sys_guid(), 1, '66', '334', 'Y')
/
update krim_role_perm_id set role_id = '83' where perm_id = '259'
/

-- add campus as qualifier to purchasing processor role and set for existing assignees
update krim_role_t set kim_typ_id = '17' where role_id = '26'
/

INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3156', sys_guid(), 1, '1017', '17', '12', 'BL')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3157', sys_guid(), 1, '1018', '17', '12', 'BL')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3158', sys_guid(), 1, '1019', '17', '12', 'BL')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3159', sys_guid(), 1, '1020', '17', '12', 'BL')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3160', sys_guid(), 1, '1021', '17', '12', 'BL')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3161', sys_guid(), 1, '1022', '17', '12', 'BL')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3162', sys_guid(), 1, '1023', '17', '12', 'BL')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3163', sys_guid(), 1, '1024', '17', '12', 'BL')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3164', sys_guid(), 1, '1025', '17', '12', 'BL')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3165', sys_guid(), 1, '1026', '17', '12', 'BL')
/

INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3166', sys_guid(), 1, '1027', '17', '12', 'IN')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3167', sys_guid(), 1, '1028', '17', '12', 'IN')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3168', sys_guid(), 1, '1029', '17', '12', 'IN')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3169', sys_guid(), 1, '1030', '17', '12', 'IN')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3170', sys_guid(), 1, '1032', '17', '12', 'IN')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3171', sys_guid(), 1, '994', '17', '12', 'IN')
/

INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3172', sys_guid(), 1, '995', '17', '12', 'EA')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3173', sys_guid(), 1, '996', '17', '12', 'EA')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3174', sys_guid(), 1, '997', '17', '12', 'EA')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3175', sys_guid(), 1, '998', '17', '12', 'EA')
/

INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3176', sys_guid(), 1, '999', '17', '12', 'KO')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3177', sys_guid(), 1, '1000', '17', '12', 'KO')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3178', sys_guid(), 1, '1001', '17', '12', 'KO')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3179', sys_guid(), 1, '1002', '17', '12', 'KO')
/

INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3180', sys_guid(), 1, '1003', '17', '12', 'NW')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3181', sys_guid(), 1, '1004', '17', '12', 'NW')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3182', sys_guid(), 1, '1005', '17', '12', 'NW')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3183', sys_guid(), 1, '1006', '17', '12', 'NW')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3184', sys_guid(), 1, '1007', '17', '12', 'NW')
/

INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3185', sys_guid(), 1, '1008', '17', '12', 'SB')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3186', sys_guid(), 1, '1009', '17', '12', 'SB')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3187', sys_guid(), 1, '1010', '17', '12', 'SB')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3188', sys_guid(), 1, '1011', '17', '12', 'SB')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3189', sys_guid(), 1, '1012', '17', '12', 'SB')
/

INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3190', sys_guid(), 1, '1013', '17', '12', 'SE')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3191', sys_guid(), 1, '1014', '17', '12', 'SE')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3192', sys_guid(), 1, '1015', '17', '12', 'SE')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('3193', sys_guid(), 1, '1016', '17', '12', 'SE')
/

-- correcting doc type refs missed
update krim_dlgn_mbr_attr_data_t set attr_val = 'REQS' where attr_val = 'RequisitionDocument'
/

-- delete retired 2 employee status
update krim_entity_emp_info_t set emp_stat_cd = 'A' where emp_stat_cd = 'X' and entity_id in (select entity_id from krim_prncpl_t where prncpl_id in (select mbr_id from krim_role_mbr_t where mbr_typ_cd = 'P'))
/
delete from KRIM_ENTITY_ADDR_T where entity_id in (select entity_id from krim_entity_emp_info_t where emp_stat_cd = 'X')
/
delete from KRIM_ENTITY_AFLTN_T where entity_id in (select entity_id from krim_entity_emp_info_t where emp_stat_cd = 'X')
/
delete from KRIM_ENTITY_BIO_T where entity_id in (select entity_id from krim_entity_emp_info_t where emp_stat_cd = 'X')
/
delete from KRIM_ENTITY_CTZNSHP_T where entity_id in (select entity_id from krim_entity_emp_info_t where emp_stat_cd = 'X')
/
delete from KRIM_ENTITY_EMAIL_T where entity_id in (select entity_id from krim_entity_emp_info_t where emp_stat_cd = 'X')
/
delete from KRIM_ENTITY_ENT_TYP_T where entity_id in (select entity_id from krim_entity_emp_info_t where emp_stat_cd = 'X')
/
delete from KRIM_ENTITY_EXT_ID_T where entity_id in (select entity_id from krim_entity_emp_info_t where emp_stat_cd = 'X')
/
delete from KRIM_ENTITY_NM_T where entity_id in (select entity_id from krim_entity_emp_info_t where emp_stat_cd = 'X')
/
delete from KRIM_ENTITY_PHONE_T where entity_id in (select entity_id from krim_entity_emp_info_t where emp_stat_cd = 'X')
/
delete from KRIM_ENTITY_PRIV_PREF_T where entity_id in (select entity_id from krim_entity_emp_info_t where emp_stat_cd = 'X')
/
delete from KRIM_PRNCPL_T where entity_id in (select entity_id from krim_entity_emp_info_t where emp_stat_cd = 'X')
/
delete from KRIM_ENTITY_EMP_INFO_T where emp_stat_cd = 'X'
/
delete from KRIM_ENTITY_T where entity_id not in ('1', '2', '3', '4016', '4026', '4034', '4043', '4044', '4045', '4046') and entity_id not in (select entity_id from krim_entity_emp_info_t)
/
delete from KRIM_EMP_STAT_T where emp_stat_cd = 'X'
/

-- fix service billing edit accounting lines perm
update krim_perm_attr_data_t set attr_val = 'PreRoute' where attr_data_id = '266'
/
