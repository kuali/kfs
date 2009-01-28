drop table ER_ADHOC_WKGRP_T
/
insert into krns_parm_t
(SELECT 'KFS-PURAP', 'PaymentRequest',
'NON_RESIDENT_ALIEN_TAX_FEDERAL_CHART', sys_guid(),1,
'CONFG', 'UA',
'Allowed Chart on the Non-resident Alien Federal',
'A'
FROM dual)
/
insert into krns_parm_t
(SELECT 'KFS-PURAP', 'PaymentRequest',
'NON_RESIDENT_ALIEN_TAX_FEDERAL_ACCOUNT', sys_guid(),1,
'CONFG', '9612729',
'Allowed Account on the Non-resident Alien Federal',
'A'
FROM dual)
/
insert into krns_parm_t
(SELECT 'KFS-PURAP', 'PaymentRequest',
'NON_RESIDENT_ALIEN_TAX_FEDERAL_OBJECT_CODE_BY_INCOME_CLASS', sys_guid(),1,
'CONFG', 'F=9021;I=9022;R=9022',
'Allowed Object Code(s) by income class on the',
'A'
FROM dual)
/
insert into krns_parm_t
(SELECT 'KFS-PURAP', 'PaymentRequest',
'NON_RESIDENT_ALIEN_TAX_STATE_CHART', sys_guid(),1,
'CONFG', 'UA',
'Allowed Chart on the Non-resident Alien State Tax',
'A'
FROM dual)
/
insert into krns_parm_t
(SELECT 'KFS-PURAP', 'PaymentRequest',
'NON_RESIDENT_ALIEN_TAX_STATE_ACCOUNT', sys_guid(),1,
'CONFG', '9612732',
'Allowed Account on the Non-resident Alien State',
'A'
FROM dual)
/
insert into krns_parm_t
(SELECT 'KFS-PURAP', 'PaymentRequest',
'NON_RESIDENT_ALIEN_TAX_STATE_OBJECT_CODE_BY_INCOME_CLASS', sys_guid(),1,
'CONFG', 'F=9021;I=9022;R=9022',
'Allowed Object Code(s) by income class on the',
'A'
FROM dual)
/ 
delete from krew_doc_typ_t where doc_typ_nm = 'DocumentTypeMaintenanceDocument'
/

update krim_perm_attr_data_t set attr_val = 'org.kuali.kfs.sys.web.struts.ElectronicFundTransferAction' where attr_data_id = '395'
/
update krim_role_mbr_attr_data_t set target_primary_key = '1290', attr_val = 'UA' where attr_data_id = '2912'
/
update krim_role_mbr_attr_data_t set target_primary_key = '1290', attr_val = 'VPIT' where attr_data_id = '2913'
/

insert into krns_parm_t
SELECT 'KFS-SYS', a.parm_dtl_typ_cd, a.parm_nm, sys_guid(), 1,
       a.parm_typ_cd, a.txt, a.parm_desc_txt, a.cons_cd
  FROM krns_parm_t a
  where nmspc_cd = 'KFS-GL'
  and parm_dtl_typ_cd = 'UniversityDate'
  and parm_nm = 'UNIVERSITY_DATE'
/
delete from krns_parm_t
where nmspc_cd = 'KFS-GL'
  and parm_dtl_typ_cd = 'UniversityDate'
  and parm_nm = 'UNIVERSITY_DATE'
/

INSERT INTO KRIM_ROLE_T(ROLE_ID, OBJ_ID, VER_NBR, ROLE_NM, NMSPC_CD, DESC_TXT, KIM_TYP_ID, ACTV_IND, LAST_UPDT_DT) 
    VALUES('90', sys_guid(), 1, 'Rice', 'KR-SYS', null, '1', 'Y', SYSDATE)
/
update krim_role_t set role_nm = 'Financial System', nmspc_cd = 'KFS-SYS', kim_typ_id = '1' where role_id = '62'
/
update krim_role_mbr_t set role_id = '90' where role_id = '62' and mbr_id = '1'
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('290', sys_guid(), 1, '15', null, null, 'Y', 'KR-SYS')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('419', sys_guid(), 1, '290', '8', '13', 'RiceDocument')
/
update krim_perm_t set nmspc_cd = 'KFS-SYS' where perm_id = '169'
/
update krim_perm_attr_data_t set attr_val = 'FinancialSystemDocument' where attr_data_id = '222'
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('552', sys_guid(), 1, '90', '290', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('553', sys_guid(), 1, '66', '290', 'Y')
/
delete from krim_role_mbr_attr_data_t where kim_typ_id = '44'
/
delete from krim_typ_attr_t where kim_typ_id = '44'
/
delete from krim_typ_t where kim_typ_id = '44'
/

UPDATE krim_perm_tmpl_t
    SET KIM_TYP_ID = '5'
    WHERE KIM_TYP_ID = '64'
/
DELETE FROM KRIM_TYP_T
    WHERE KIM_TYP_ID = '64'
/

update krew_doc_typ_t set lbl = 'Financial System Document Type Code' where doc_typ_id = '319989'
/

