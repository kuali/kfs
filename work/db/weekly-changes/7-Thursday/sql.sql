alter table fs_doc_header_t drop column fs_doc_typ_cd
/

update ca_acct_delegate_t set fdoc_typ_cd = 'KFS' where fdoc_typ_cd = 'ALL'
/
update ca_dlgt_chg_doc_t set fdoc_typ_cd = 'KFS' where fdoc_typ_cd = 'ALL'
/
update ca_org_rtng_mdl_t set fdoc_typ_cd = 'KFS' where fdoc_typ_cd = 'ALL'
/
