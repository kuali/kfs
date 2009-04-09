update krns_parm_t
set (txt, cons_cd) = (select 'FEDERE', 'D' from dual)
where parm_nm = 'TAXABLE_SUB_FUND_GROUPS_FOR_TAXABLE_STATES'
and nmspc_cd = 'KFS-PURAP'
and parm_dtl_typ_cd = 'Document'
;
update krns_parm_t
set (txt, cons_cd) = (select 'RESA', 'D' from dual)
where parm_nm = 'TAXABLE_OBJECT_LEVELS_FOR_TAXABLE_STATES'
and nmspc_cd = 'KFS-PURAP'
and parm_dtl_typ_cd = 'Document'
;
update krns_parm_t
set (txt, cons_cd) = (select 'OTRE', 'D' from dual)
where parm_nm = 'TAXABLE_OBJECT_CONSOLIDATIONS_FOR_TAXABLE_STATES'
and nmspc_cd = 'KFS-PURAP'
and parm_dtl_typ_cd = 'Document'
;
update krns_parm_t
set (txt, cons_cd) = (select 'RF', 'D' from dual)
where parm_nm = 'TAXABLE_FUND_GROUPS_FOR_TAXABLE_STATES'
and nmspc_cd = 'KFS-PURAP'
and parm_dtl_typ_cd = 'Document'
;
update krns_parm_t
set (txt, cons_cd) = (select 'IN;NY', 'D' from dual)
where parm_nm = 'TAXABLE_DELIVERY_STATES'
and nmspc_cd = 'KFS-PURAP'
and parm_dtl_typ_cd = 'Document'
;
commit;
insert into krns_parm_t
SELECT 'KFS-PURAP', 'PaymentRequest' , a.parm_nm, SYS_GUID(), 1,
       a.parm_typ_cd, a.txt, a.parm_desc_txt, a.cons_cd
  FROM krns_parm_t a
  where nmspc_cd = 'KFS-FP'
  AND PARM_NM LIKE 'NON_RESIDENT_ALIEN_TAX%';
commit;
