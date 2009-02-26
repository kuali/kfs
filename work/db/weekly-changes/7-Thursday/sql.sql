update krns_parm_t
set txt = 'MISC;ORDS;TRDI'
where nmspc_cd = 'KFS-PURAP'
and parm_dtl_typ_cd = 'PaymentRequest'
and parm_nm = 'ITEM_TYPES_REQUIRING_USER_ENTERED_DESCRIPTION'
/
