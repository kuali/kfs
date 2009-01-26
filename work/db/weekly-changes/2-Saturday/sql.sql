update krim_role_rsp_t set ROLE_ID = '9' where role_id = '37' and rsp_id = '29'
/
update krim_role_rsp_t set ROLE_ID = '41' where role_id = '53' and rsp_id = '29'
/

update krim_role_mbr_attr_data_t set kim_attr_defn_id = '31', attr_val = '1*' where attr_data_id = '2956'
/
update krim_role_mbr_attr_data_t set kim_attr_defn_id = '31', attr_val = '112*' where attr_data_id = '2957'
/

ALTER TABLE KREW_DOC_TYP_T ADD HELP_DEF_URL VARCHAR2(255) 
/ 
ALTER TABLE KREW_DOC_TYP_T DROP COLUMN DOC_TYP_SUM 
/ 
ALTER TABLE KREW_DOC_TYP_T DROP COLUMN SHRT_LBL 
/ 
