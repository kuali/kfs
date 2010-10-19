ALTER TABLE AR_ORG_OPTION_T ADD org_postal_cntry_cd varchar2(2);

UPDATE AR_ORG_OPTION_T SET org_postal_cntry_cd = 'US';

