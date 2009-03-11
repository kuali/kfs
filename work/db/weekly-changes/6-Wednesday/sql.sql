update krim_typ_attr_t set sort_cd = 'd' where KIM_TYP_ATTR_ID = '79'
/
update krns_parm_t
set parm_desc_txt =  'Used as the default country code when relating records that do not have a country code to records that do have a country code, e.g. validating a zip code where the country is not collected.'
where nmspc_cd = 'KR-NS'
and parm_dtl_typ_cd = 'All'
and parm_nm = 'DEFAULT_COUNTRY'
/