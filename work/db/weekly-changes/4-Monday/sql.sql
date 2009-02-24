-- mods to kim test data
update krim_role_rsp_actn_t set actn_typ_cd = 'A' where role_rsp_actn_id = '5'
/
delete from krim_role_rsp_actn_t where role_rsp_id = '1063' and role_mb_id in ('1301', '1303')
/
update krim_role_rsp_actn_t set actn_typ_cd = 'K' where role_rsp_id = '1063' and role_mbr_id = '1302'
/
delete from krim_role_mbr_attr_data_t where target_primary_key in ('1301', '1303')
/
delete from krim_role_mbr_t where role_mbr in ('1301', '1303')
/

