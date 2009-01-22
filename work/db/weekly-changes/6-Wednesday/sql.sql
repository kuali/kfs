update krim_rsp_attr_data_t set attr_val = 'PaymentRequestDocument' where TARGET_PRIMARY_KEY = '71' and kim_attr_defn_id = '13'
/

alter table KRNS_PARM_T drop column grp_nm
/

update krim_rsp_rqrd_attr_t set kim_attr_defn_id = '22' where rsp_id = '97'
/
INSERT INTO KRIM_RSP_RQRD_ATTR_T(RSP_RQRD_ATTR_ID, OBJ_ID, VER_NBR, RSP_ID, KIM_ATTR_DEFN_ID, ACTV_IND) 
    VALUES('125', sys_guid(), 1, '97', '24', 'Y')
/
DELETE KRNS_PARM_T WHERE PARM_NM = 'DEPARTMENT_VIEWABLE_FIELDS'
/
DELETE KRNS_PARM_T WHERE PARM_NM = 'MERGE_SEPARATE_VIEWABLE_FIELDS'
/
DELETE KRNS_PARM_T WHERE PARM_NM = 'MERGE_SEPARATE_EDITABLE_FIELDS'
/
DELETE KRNS_PARM_T WHERE PARM_NM = 'DEPARTMENT_EDITABLE_FIELDS'
/
DELETE KRNS_PARM_T WHERE PARM_NM = 'WORKGROUP_CM_ADMINISTRATORS'
/
DELETE KRNS_PARM_T WHERE PARM_NM = 'WORKGROUP_CM_SECURITY_ADMINISTRATORS'
/
DELETE KRNS_PARM_T WHERE PARM_NM = 'WORKGROUP_CM_ADD_PAYMENT_USERS'
/
DELETE KRNS_PARM_T WHERE PARM_NM = 'WORKGROUP_CM_ASSET_MERGE_SEPARATE_USERS'
/
DELETE KRNS_PARM_T WHERE PARM_NM = 'WORKGROUP_CM_SUPER_USERS'
/
DELETE KRNS_PARM_T WHERE PARM_NM = 'WORKGROUP_CM_BARCODE_USERS'
/
DELETE KRNS_PARM_T WHERE PARM_NM = 'WORKGROUP_CM_CAPITAL_ASSET_BUILDER_ADMINISTRATORS'
/
DELETE KRNS_PARM_T WHERE PARM_NM = 'WORKGROUP_RAZE_WORKGROUP'
/
DELETE KRNS_PARM_T WHERE PARM_NM = 'WORKGROUP_MERGE_SEPARATE_WORKGROUP'
/
DELETE KRNS_PARM_T WHERE PARM_NM = 'WORKGROUP_MULTIPLE_ASSET_RETIREMENT_WORKGROUP'
/
DELETE KRNS_PARM_T WHERE PARM_NM = 'NON_CAPITAL_ASSET_STATUS_CODES'
/
DELETE KRNS_PARM_T WHERE PARM_NM = 'UPLOAD_GROUP '
/
update krns_parm_t set TXT = 'R'
where PARM_NM='RAZE_RETIREMENT_REASONS' and NMSPC_CD='KFS-CAM'
/ 
update krns_parm_t set PARM_DESC_TXT = 'Restricted retirement reasons for plant fund activities (currently used as restricted retirement reasons for buildings.)'
where PARM_NM='RAZE_RETIREMENT_REASONS' and NMSPC_CD='KFS-CAM'
/ 
UPDATE KRNS_PARM_T SET NMSPC_CD = 'KFS-SYS' WHERE PARM_DTL_TYP_CD = 'FiscalYearMakerStep'
/
insert into krns_parm_t
(SELECT 'KFS-PURAP', 'Document',
'BLANK_ATTENTION_LINE_FOR_PO_TYPE_ADDRESS', sys_guid(),1,
'CONFG', 'Y',
'When the vendor address information is populated on a Payment Request or Credit
 Memo, indicate whether the attention line should be cleared when the default ad
dress is a PO type address.',
'A'
FROM dual)
/
insert into krns_parm_t(nmspc_cd, parm_dtl_typ_cd, parm_nm, obj_id, ver_nbr, cons_cd, txt, parm_desc_txt, parm_typ_cd) values('KFS-FP','DvToPdpExtractStep','CORPORATION_OWNERSHIP_TYPE ', sys_guid(), 1,'A','CP','Vendor Ownership Type representing Corporations','CONFG')
/
insert into krns_parm_t(nmspc_cd, parm_dtl_typ_cd, parm_nm, obj_id, ver_nbr, cons_cd, txt, parm_desc_txt, parm_typ_cd) values('KFS-FP','DvToPdpExtractStep','TAXABLE_PAYMENT_REASON_CODES_BY_OWNERSHIP_CODES', sys_guid(), 1,'A','NP=H,J;FC=H,J;NR=A,C,E,H,R,T,X,Y.L,J;ID=A,C,E,H,R,T,X,Y.L,J;PT=A,C,E,H,R,T,X,Y.L,J;','Taxable payment reason codes','CONFG')
/
insert into krns_parm_t(nmspc_cd, parm_dtl_typ_cd, parm_nm, obj_id, ver_nbr, cons_cd, txt, parm_desc_txt, parm_typ_cd) values('KFS-FP','DvToPdpExtractStep','NON_TAXABLE_PAYMENT_REASON_CODES_BY_OWNERSHIP_CODES', sys_guid(), 1,'D','','Non-taxable payment reason codes','CONFG')
/
insert into krns_parm_t(nmspc_cd, parm_dtl_typ_cd, parm_nm, obj_id, ver_nbr, cons_cd, txt, parm_desc_txt, parm_typ_cd) values('KFS-FP','DvToPdpExtractStep','TAXABLE_PAYMENT_REASON_CODES_FOR_BLANK_CORPORATION_OWNERSHIP_TYPE_CATEGORIES ', sys_guid(), 1,'A','H,J','Taxable payment reason codes for corporations with blank ownership type categories.','CONFG')
/
insert into krns_parm_t(nmspc_cd, parm_dtl_typ_cd, parm_nm, obj_id, ver_nbr, cons_cd, txt, parm_desc_txt, parm_typ_cd) values('KFS-FP','DvToPdpExtractStep','TAXABLE_PAYMENT_REASON_CODES_BY_CORPORATION_OWNERSHIP_TYPE_CATEGORY', sys_guid(), 1,'A','ME=A,C,E,H,R,T,X,Y,L,J;LE=A,X,E,H,R,T,L,J','Taxable payment reason codes by corporation ownership type category','CONFG')
/
insert into krns_parm_t(nmspc_cd, parm_dtl_typ_cd, parm_nm, obj_id, ver_nbr, cons_cd, txt, parm_desc_txt, parm_typ_cd) values('KFS-FP','DvToPdpExtractStep','NON_TAXABLE_PAYMENT_REASON_CODES_BY_CORPORATION_OWNERSHIP_TYPE_CATEGORY', sys_guid(), 1,'D','','Non-taxable payment reason codes by corporation ownership type category','CONFG')
/