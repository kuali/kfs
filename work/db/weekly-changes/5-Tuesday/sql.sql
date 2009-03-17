update krim_rsp_t a set a.nm = (select b.nm from krim_rsp_tmpl_t b where a.rsp_tmpl_id = b.rsp_tmpl_id) where a.nm is null
/
update krew_doc_typ_t set lbl = 'Group' where doc_typ_nm = 'IdentityManagementGroupDocument'
/
update KRNS_PARM_T set TXT='N' where NMSPC_CD='KR-WKFLW' AND PARM_NM='ACTION_LIST_DOCUMENT_POPUP_IND'
/

