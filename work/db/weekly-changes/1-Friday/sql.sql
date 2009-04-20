insert into krns_parm_t
(select nmspc_cd, parm_dtl_typ_cd, 'COST_SHARE_DOCUMENT_TYPES',
sys_guid(), 1, parm_typ_cd, txt, parm_desc_txt, cons_cd
from krns_parm_t
where parm_nm = 'COST_SHARE_ENCUMBRANCE_DOCUMENT_TYPES')
;
commit;
