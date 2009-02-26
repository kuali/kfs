update krns_parm_t
set txt = 'FRHT;SPHD;MNOR;ORDS;TRDI;FDGR;STGR;MISC'
where parm_nm = 'ITEM_TYPES_ALLOWING_POSITIVE'
and nmspc_cd = 'KFS-PURAP'
and parm_dtl_typ_cd = 'PaymentRequest'
/