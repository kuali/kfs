INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('688', sys_guid(), 1, '53', '61', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('689', sys_guid(), 1, '53', '62', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('690', sys_guid(), 1, '53', '63', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('691', sys_guid(), 1, '53', '64', 'Y')
/

CREATE TABLE CA_ACCT_DELEGATE_GBL_T (FDOC_NBR VARCHAR2(14), OBJ_ID VARCHAR2(36) NOT NULL, VER_NBR NUMBER(8,0) default 1 NOT NULL)
/

ALTER TABLE KRIM_DLGN_MBR_T
    DROP CONSTRAINT KRIM_DLGN_MBR_TR1
/
update krim_dlgn_mbr_t set dlgn_mbr_id = '3', role_mbr_id = '*' where dlgn_mbr_id = '-1'
/
update krim_dlgn_mbr_t set dlgn_mbr_id = '4', role_mbr_id = '*' where dlgn_mbr_id = '-2'
/
update krim_dlgn_mbr_t set role_mbr_id = '1292' where dlgn_mbr_id in ('1', '2')
/

update krns_parm_t set TXT = 'A=N' where parm_nm = 'VALID_ASSET_STATUSES_BY_ACQUISITION_TYPE'
/
