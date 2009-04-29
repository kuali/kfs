update krim_perm_attr_data_t set attr_val = 'KualiDocument' where perm_id in (select perm_id from krim_perm_t where nm = 'Ad Hoc Review Document') and attr_val = 'Kuali Document'
/
