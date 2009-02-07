update krew_doc_typ_t set lbl = 'Sufficient Funds Code' where doc_typ_nm = 'SFC'
/
insert into krew_doc_typ_t (DOC_TYP_ID, PARNT_ID, DOC_TYP_NM, DOC_TYP_VER_NBR, ACTV_IND, CUR_IND, LBL, VER_NBR, RTE_VER_NBR, OBJ_ID)
    values (KREW_DOC_HDR_S.NEXTVAL, null, 'FSLO', 0, 1, 1, 'Financial System - Ledger Only', 1, 2, sys_guid())
/
update krew_doc_typ_t set PARNT_ID = (select doc_typ_id from krew_doc_typ_t where doc_typ_nm = 'FSLO') where doc_typ_nm in ('AVAD', 'AVAE', 'AVRC', 'DVWF', 'DVCA')
/
