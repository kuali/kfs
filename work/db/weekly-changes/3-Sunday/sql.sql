ALTER TABLE KRIM_ROLE_RSP_ACTN_T
    ADD ( IGNORE_PREV_IND VARCHAR2(1) DEFAULT 'Y' )
/

INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('551', sys_guid(), 1, '60', '101', 'Y')
/

update krim_rsp_attr_data_t set attr_val = 'PurchasingTransactionalDocument' where attr_val = 'PurchasingTransactionalDocument '
/
