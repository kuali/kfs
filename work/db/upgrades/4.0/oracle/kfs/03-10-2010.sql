ALTER TABLE AR_ORG_OPTION_T ADD org_postal_cntry_cd varchar2(2);

UPDATE AR_ORG_OPTION_T SET org_postal_cntry_cd = 'US';

alter table AR_ORG_OPTION_T
add constraint
  AR_ORG_OPTION_TR5 FOREIGN KEY (org_postal_zip_cd, org_postal_cntry_cd)
  references KR_POSTAL_CODE_T (postal_cd, postal_cntry_cd);