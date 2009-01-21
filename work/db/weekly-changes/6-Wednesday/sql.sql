update krim_rsp_attr_data_t set attr_val = 'PaymentRequestDocument' where TARGET_PRIMARY_KEY = '71' and kim_attr_defn_id = '13'
/

alter table KRNS_PARM_T drop column grp_nm
/

update krim_rsp_rqrd_attr_t set kim_attr_defn_id = '22' where rsp_id = '97'
/
INSERT INTO KRIM_RSP_RQRD_ATTR_T(RSP_RQRD_ATTR_ID, OBJ_ID, VER_NBR, RSP_ID, KIM_ATTR_DEFN_ID, ACTV_IND) 
    VALUES('125', sys_guid(), 1, '97', '24', 'Y')
/
