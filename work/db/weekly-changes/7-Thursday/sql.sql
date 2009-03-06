delete from krim_role_perm_t where perm_id in ('309', '310', '311', '312')
/
delete from krim_perm_attr_data_t where target_primary_key in ('309', '310', '311', '312')
/
delete from krim_perm_t where perm_id in ('309', '310', '311', '312')
/
alter table AR_INV_DOC_T add (RECURRED_INV_IND varchar(1))
/
