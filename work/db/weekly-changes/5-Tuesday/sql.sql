update krim_attr_defn_t set appl_url = '${application.url}'
/

update krim_typ_attr_t set sort_cd = 'a' where kim_typ_attr_id = '79'
/
update krim_typ_attr_t set sort_cd = 'b' where kim_typ_attr_id = '76'
/
update krim_typ_attr_t set sort_cd = 'c' where kim_typ_attr_id = '77'
/
update krim_typ_attr_t set sort_cd = 'd' where kim_typ_attr_id = '78'
/

update krim_role_t set kim_typ_id = '17', role_nm = 'Disbursement Manager' where ROLE_ID = '12'
/
