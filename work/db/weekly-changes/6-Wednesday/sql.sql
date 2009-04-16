update  krns_parm_t
set parm_desc_txt = 'Indicator whether commodity code functionality should be completely turned on for the Requisition and Purchase Order' 
where nmspc_cd = 'KFS-PURAP'
and parm_nm = 'ENABLE_COMMODITY_CODE_IND'
;
commit;
update krns_parm_t
set txt = '8160;8116;8118;5019'
where parm_nm = 'OBJECT_CODES'
AND NMSPC_CD = 'KFS-FP'
AND PARM_DTL_TYP_CD = 'Document';
commit;
