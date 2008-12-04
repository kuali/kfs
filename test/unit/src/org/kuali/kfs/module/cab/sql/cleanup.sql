delete from cb_pur_ln_ast_acct_t;
delete from cb_pur_itm_ast_t;
delete from cb_pur_doc_t;
delete from cb_gl_entry_ast_t;
delete from cb_gl_entry_t;
delete from gl_entry_t where fdoc_nbr in ('11','12','13','21','22','23','31','32','33','34','35','36','41','51','52');
delete from gl_pending_entry_t where fdoc_nbr in ('11','12','13','21','22','23','31','32','33','34','35','36','41','51','52');
-----
delete from ap_crdt_memo_acct_chg_t where crdt_memo_itm_id < 1000;
delete from ap_crdt_memo_acct_t where crdt_memo_acct_id < 1000;
delete from ap_crdt_memo_itm_t where crdt_memo_itm_id < 1000;
delete from ap_crdt_memo_t where crdt_memo_id < 1000;
-----
delete from ap_pmt_rqst_acct_chg_t where pmt_rqst_itm_id < 1000;
delete from ap_pmt_rqst_acct_t where pmt_rqst_acct_id < 1000;
delete from ap_pmt_rqst_itm_t where pmt_rqst_itm_id < 1000;
delete from ap_pmt_rqst_t where pmt_rqst_id < 1000;
-----
delete from pur_po_cptl_ast_itm_t where fdoc_nbr in ('11','12','13','21','22','23','31','32','33','34','35','36','41','51','52');
delete from pur_po_acct_t where fdoc_nbr in ('11','12','13','21','22','23','31','32','33','34','35','36','41','51','52');
delete from pur_po_itm_t where fdoc_nbr in ('11','12','13','21','22','23','31','32','33','34','35','36','41','51','52');
delete from pur_po_t where fdoc_nbr in ('11','12','13','21','22','23','31','32','33','34','35','36','41','51','52');
delete from pur_po_cptl_ast_itm_t where CPTL_AST_ITM_ID <1010 and CPTL_AST_ITM_ID>999;
delete from pur_po_cptl_ast_itm_ast_t where PO_ITM_CPTL_AST_ID > 1199 and PO_ITM_CPTL_AST_ID<1210;
delete from pur_po_cptl_ast_loc_t where CPTL_AST_LOC_ID > 1299 and CPTL_AST_LOC_ID < 1310;
delete from pur_po_cptl_ast_sys_t where CPTL_AST_SYS_ID > 1099 and CPTL_AST_SYS_ID<1110;
-------
delete from pur_reqs_acct_t where reqs_acct_id < 1000;
delete from pur_reqs_itm_t where reqs_itm_id < 1000;
delete from pur_asgn_contr_mgr_dtl_t where reqs_id < 1000;
delete from pur_reqs_t where reqs_id < 1000;
--------
delete from krns_doc_hdr_t where doc_hdr_id in ('11','12','13','21','22','23','31','32','33','34','35','36','41','51','52');
delete from fs_doc_header_t where fdoc_nbr in ('11','12','13','21','22','23','31','32','33','34','35','36','41','51','52');
delete from krew_doc_hdr_t where doc_hdr_id < 1000;
