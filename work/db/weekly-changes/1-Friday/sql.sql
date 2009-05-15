-- add additional identifier to review responsibility
INSERT INTO KRIM_ATTR_DEFN_T ( KIM_ATTR_DEFN_ID, OBJ_ID, NM, NMSPC_CD, CMPNT_NM, APPL_URL ) 
    VALUES ('46', SYS_GUID(), 'qualifierResolverProvidedIdentifier', 'KR-IDM', 'org.kuali.rice.kim.bo.impl.KimAttributes', '${application.url}' )
/
INSERT INTO krim_typ_attr_t ( KIM_TYP_ATTR_ID, OBJ_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, SORT_CD ) 
    VALUES('115', SYS_GUID(), '7', '46', 'e' )
/
-- responsibility for the group routing
INSERT INTO krim_rsp_t ( RSP_ID, OBJ_ID, RSP_TMPL_ID, NMSPC_CD ) VALUES ('117', SYS_GUID(), '1', 'KFS-SYS' )
/
INSERT INTO krim_rsp_attr_data_t ( ATTR_DATA_ID, OBJ_ID, rsp_id, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL ) 
    VALUES ('432', SYS_GUID(), '117', '7', '13', 'IdentityManagementDocument' )
/
INSERT INTO krim_rsp_attr_data_t ( ATTR_DATA_ID, OBJ_ID, rsp_id, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL ) 
    VALUES ('433', SYS_GUID(), '117', '7', '16', 'GroupType' )
/
INSERT INTO krim_rsp_attr_data_t ( ATTR_DATA_ID, OBJ_ID, rsp_id, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL ) 
    VALUES ('434', SYS_GUID(), '117', '7', '40', 'false' )
/
INSERT INTO krim_rsp_attr_data_t ( ATTR_DATA_ID, OBJ_ID, rsp_id, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL ) 
    VALUES ('435', SYS_GUID(), '117', '7', '41', 'true' )
/
INSERT INTO krim_rsp_attr_data_t ( ATTR_DATA_ID, OBJ_ID, rsp_id, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL ) 
    VALUES ('436', SYS_GUID(), '117', '7', '46', '68' )
/
-- assign responsibility to role
INSERT INTO krim_role_rsp_t ( ROLE_RSP_ID, OBJ_ID, ROLE_ID, RSP_ID ) VALUES ('1118', SYS_GUID(), '7', '117' )
/
-- alter role resp table to remove constraint on role resp id
ALTER TABLE KRIM_ROLE_RSP_ACTN_T
    DROP CONSTRAINT KRIM_ROLE_RSP_ACTN_TR2
/
-- make the role rsp actn rows for org review apply to all responsibilities assigned to the role
update krim_role_rsp_actn_t set role_rsp_id = '*' where role_mbr_id in (select role_mbr_id from krim_role_mbr_t where role_id = '7')
/
drop sequence GL_ID_BILL_T_SEQ;