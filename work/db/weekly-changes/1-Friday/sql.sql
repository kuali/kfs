insert into krns_parm_t
(SELECT 'KFS-GL', 'PosterEntriesStep',
'ENCUMBRANCE_OPEN_AMOUNT_OVERRIDING_DOCUMENT_TYPES', sys_guid(),1,
'CONFG', 'PO;POA;POC;POPH;PORH;POR;PORT;POSP;POV',
'Encumbrance transactions with these document types will always update the Open Amount field on the GL Open Encumbrance table. As with other encumbrance transactions, the GL poster will use the document information, when the encumbrance update code = D, or the reference document information, when the encumbrance update code = R, when determining the key values needed to insert into or update the Open Encumbrance table.',
'A'
FROM dual);
commit;
update krns_parm_t
set txt = 'RSTO;MSCR'
where nmspc_cd = 'KFS-PURAP'
and parm_dtl_typ_cd = 'VendorCreditMemo'
and parm_nm = 'ITEM_TYPES_ALLOWING_NEGATIVE';
commit;
update krns_parm_t
set txt = 'MSCR'
where nmspc_cd = 'KFS-PURAP'
and parm_dtl_typ_cd = 'VendorCreditMemo'
and parm_nm = 'ITEM_TYPES_ALLOWING_POSITIVE';
commit;
update krns_parm_t
set txt = 'FRHT;SPHD;MNOR;FDGR;STGR;MISC'
where nmspc_cd = 'KFS-PURAP'
and parm_dtl_typ_cd = 'PaymentRequest'
and parm_nm = 'ITEM_TYPES_ALLOWING_POSITIVE';
commit;
update ca_acct_delegate_t
set acct_dlgt_start_dt = to_date('5/1/2009','mm/dd/yyyy')
where fdoc_typ_cd = 'PREQ';
COMMIT;
drop table krns_maint_lock_t
;
CREATE TABLE krns_maint_lock_t
    (maint_lock_id                  VARCHAR2(14) NOT NULL,
    obj_id                         VARCHAR2(36) DEFAULT NULL NOT NULL,
    ver_nbr                        NUMBER(8,0) DEFAULT 1 NOT NULL,
    doc_hdr_id                     VARCHAR2(14) NOT NULL,
    maint_lock_rep_txt             VARCHAR2(500) NOT NULL
    )
;

CREATE INDEX krns_maint_lock_ti2 ON krns_maint_lock_t
  (
    doc_hdr_id                      ASC
  )
;
ALTER TABLE krns_maint_lock_t
ADD constraint krns_maint_lock_tp1 PRIMARY KEY (maint_lock_id)
;
ALTER TABLE krns_maint_lock_t
ADD CONSTRAINT krns_maint_lock_tc1 UNIQUE (obj_id)
;
GRANT INSERT ON krns_maint_lock_t TO fis_kuali_usr
;
GRANT SELECT ON krns_maint_lock_t TO fis_kuali_usr
;
GRANT SELECT ON krns_maint_lock_t TO kultst2
;
GRANT SELECT ON krns_maint_lock_t TO kulreg
;
insert into ca_object_code_t
select 2008, FIN_COA_CD, FIN_OBJECT_CD, sy  +> s_guid(), VER_NBR, FIN_OBJ_CD_NM, FIN_OBJ_CD_SHRT_NM, FIN_OBJ_LEVEL_CD, RPTS_TO_FIN_COA_CD, RPTS_TO_FIN_OBJ_CD, FIN_OBJ_TYP_CD, FIN_OBJ_SUB_TYP_CD, HIST_FIN_OBJECT_CD, FIN_OBJ_ACTIVE_CD, FOBJ_BDGT_AGGR_CD, FOBJ_MNXFR_ELIM_CD, FIN_FED_FUNDED_CD, NXT_YR_FIN_OBJ_CD
from ca_object_code_t
where univ_fiscal_yr = 2009
;
commit;
