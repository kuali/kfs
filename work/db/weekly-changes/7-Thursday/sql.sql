INSERT INTO KR_POSTAL_CODE_T
(SELECT '91010','US',sys_guid(),1,
'CA','37','Duarte','Y' from dual);
commit;
update krim_role_rsp_actn_t set actn_typ_cd = 'F' where role_rsp_actn_id = 132; 
commit;
update krns_parm_t
set txt = 'Y'
where nmspc_cd = 'KFS-PURAP'
and parm_dtl_typ_cd = 'Document'
and parm_nm = 'ENABLE_SALES_TAX_IND';
commit;
