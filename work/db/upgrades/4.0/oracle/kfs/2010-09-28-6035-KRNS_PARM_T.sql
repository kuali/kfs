insert into krns_parm_t (nmspc_cd, parm_dtl_typ_cd, parm_nm, appl_nmspc_cd, obj_id, ver_nbr, txt, parm_desc_txt, parm_typ_cd, cons_cd)
  values ('KFS-PURAP', 'ElectronicInvoiceReject','SUPPRESS_REJECT_REASON_CODES_ON_EIRT_APPROVAL','KFS',sys_guid(), 1, 'AGUV', 'Specifies which types of EIRT reject reasons should be ignored when an EIRT document is Approved.', 'CONFG', 'A');
