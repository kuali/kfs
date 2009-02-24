ALTER TABLE FP_CSH_DRWR_T DROP CONSTRAINT FP_CSH_DRWR_TR1
/
ALTER TABLE PUR_DFLT_PRNCPL_ADDR_T DROP CONSTRAINT PUR_DFLT_PRNCPL_ADDR_TR1
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
-- mods to kim test data
update krim_role_rsp_actn_t set actn_typ_cd = 'A' where role_rsp_actn_id = '5'
/
delete from krim_role_rsp_actn_t where role_rsp_id = '1063' and role_mb_id in ('1301', '1303')
/
update krim_role_rsp_actn_t set actn_typ_cd = 'K' where role_rsp_id = '1063' and role_mbr_id = '1302'
/
delete from krim_role_mbr_attr_data_t where target_primary_key in ('1301', '1303')
/
delete from krim_role_mbr_t where role_mbr in ('1301', '1303')
/