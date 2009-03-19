alter table krns_att_t modify att_typ_cd VARCHAR2(40)
/
update krns_parm_t
set txt = 'TRDI'
where nmspc_cd = 'KFS-PURAP'
AND PARM_NM = 'ITEM_TYPES_RESTRICTING_ACCOUNT_EDIT'
and parm_dtl_typ_cd = 'PurchaseOrder'
/
update krns_parm_t
set txt = 'TRDI;FDTX;FDGR;STTX;STGR'
where nmspc_cd = 'KFS-PURAP'
AND PARM_NM = 'ITEM_TYPES_RESTRICTING_ACCOUNT_EDIT'
and parm_dtl_typ_cd = 'PaymentRequest'
/ 
ALTER TABLE ld_bcn_build_objtsumm02_mt
drop CONSTRAINT ld_bcn_build_objtsumm02_mtp1
/
alter table LD_BCN_BUILD_LEVLSUMM02_MT
drop constraint LD_BCN_BUILD_LEVLSUMM02_MTP1
/
update krew_doc_typ_t set doc_typ_nm = 'KFS' where doc_typ_nm = 'FinancialSystemDocument' and cur_ind = 1
/
update krim_perm_attr_data_t set attr_val = 'KFS' where attr_val = 'FinancialSystemDocument' and kim_attr_defn_id = '13'
/
update krim_rsp_attr_data_t set attr_val = 'KFS' where attr_val = 'FinancialSystemDocument' and kim_attr_defn_id = '13'
/
drop sequence VNDR_CONTR_GNRTD_ID
/
CREATE SEQUENCE VNDR_CONTR_GNRTD_ID INCREMENT BY 1 START WITH 4105 NOMAXVALUE NOCYCLE NOCACHE ORDER
/
drop sequence VNDR_ADDR_GNRTD_ID
/
CREATE SEQUENCE VNDR_ADDR_GNRTD_ID INCREMENT BY 1 START WITH 4105 NOMAXVALUE NOCYCLE NOCACHE ORDER
/
drop sequence VNDR_HDR_GNRTD_ID
/
CREATE SEQUENCE VNDR_HDR_GNRTD_ID INCREMENT BY 1 START WITH 4105 NOMAXVALUE NOCYCLE NOCACHE ORDER
/

