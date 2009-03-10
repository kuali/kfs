delete krns_parm_t where parm_nm = 'VERIFICATION_UNIT_GROUP_PREFIX'
/
delete krns_parm_t where parm_nm = 'TRADE_IN_OBJECT_CODE_FOR_CAPITAL_LEASE_OBJECT_SUB_TYPE'
/
delete krns_parm_t where parm_nm = 'TRADE_IN_OBJECT_CODE_FOR_CAPITAL_ASSET_OBJECT_SUB_TYPE'
/
delete krns_parm_t where parm_nm = 'TAXABLE_SUB_FUND_GROUPS_FOR_TAXABLE_STATES'
/
delete krns_parm_t where parm_nm = 'TAXABLE_SUB_FUND_GROUPS_FOR_NON_TAXABLE_STATES'
/
delete krns_parm_t where parm_nm = 'TAXABLE_OBJECT_LEVELS_FOR_TAXABLE_STATES'
/
delete krns_parm_t where parm_nm = 'TAXABLE_OBJECT_LEVELS_FOR_NON_TAXABLE_STATES'
/
delete krns_parm_t where parm_nm = 'TAXABLE_OBJECT_CONSOLIDATIONS_FOR_TAXABLE_STATES'
/
delete krns_parm_t where parm_nm = 'TAXABLE_OBJECT_CONSOLIDATIONS_FOR_NON_TAXABLE_STATES'
/
delete krns_parm_t where parm_nm = 'TAXABLE_FUND_GROUPS_FOR_TAXABLE_STATES'
/
delete krns_parm_t where parm_nm = 'TAXABLE_FUND_GROUPS_FOR_NON_TAXABLE_STATES'
/
delete krns_parm_t where parm_nm = 'SYSTEM_WORKGROUP_TYPE_AREAS'
/
delete krns_parm_t where parm_nm = 'SORT_GROUP_SELECTION_3'
/
delete krns_parm_t where parm_nm = 'SORT_GROUP_SELECTION_2'
/
delete krns_parm_t where parm_nm = 'SORT_GROUP_SELECTION_1'
/
delete krns_parm_t where parm_nm = 'SELECTION_6'
/
delete krns_parm_t where parm_nm = 'SELECTION_5'
/
delete krns_parm_t where parm_nm = 'SELECTION_4'
/
delete krns_parm_t where parm_nm = 'SELECTION_3'
/
delete krns_parm_t where parm_nm = 'SELECTION_2'
/
delete krns_parm_t where parm_nm = 'SELECTION_1'
/
delete krns_parm_t where parm_nm = 'NON_RESIDENT_ALIEN_TAX_STATE_OBJECT_CODE_BY_INCOME_CLASS'
/
delete krns_parm_t where parm_nm = 'NON_RESIDENT_ALIEN_TAX_STATE_CHART'
/
delete krns_parm_t where parm_nm = 'NON_RESIDENT_ALIEN_TAX_STATE_ACCOUNT'
/
delete krns_parm_t where parm_nm = 'NON_RESIDENT_ALIEN_TAX_FEDERAL_OBJECT_CODE_BY_INCOME_CLASS'
/
delete krns_parm_t where parm_nm = 'NON_RESIDENT_ALIEN_TAX_FEDERAL_CHART'
/
delete krns_parm_t where parm_nm = 'NON_RESIDENT_ALIEN_TAX_FEDERAL_ACCOUNT'
/
delete krns_parm_t where parm_nm = 'EXTENDED_DEFINITIONS_INCLUDE_OBJECT_TYPES_BY_ORGANIZATION_REVERSION_CATEGORY'
/
delete krns_parm_t where parm_nm = 'EXTENDED_DEFINITIONS_INCLUDE_OBJECT_SUB_TYPES_BY_ORGANIZATION_REVERSION_CATEGORY'
/
delete krns_parm_t where parm_nm = 'EXTENDED_DEFINITIONS_INCLUDE_OBJECT_LEVELS_BY_ORGANIZATION_REVERSION_CATEGORY'
/
delete krns_parm_t where parm_nm = 'EXTENDED_DEFINITIONS_INCLUDE_OBJECT_CONSOLIDATIONS_BY_ORGANIZATION_REVERSION_CATEGORY'
/
delete krns_parm_t where parm_nm = 'EXTENDED_DEFINITIONS_EXCLUDE_OBJECT_TYPES_BY_ORGANIZATION_REVERSION_CATEGORY'
/
delete krns_parm_t where parm_nm = 'EXTENDED_DEFINITIONS_EXCLUDE_OBJECT_SUB_TYPES_BY_ORGANIZATION_REVERSION_CATEGORY'
/
delete krns_parm_t where parm_nm = 'EXTENDED_DEFINITIONS_EXCLUDE_OBJECT_LEVELS_BY_ORGANIZATION_REVERSION_CATEGORY'
/
delete krns_parm_t where parm_nm = 'EXTENDED_DEFINITIONS_EXCLUDE_OBJECT_CONSOLIDATIONS_BY_ORGANIZATION_REVERSION_CATEGORY'
/
delete krns_parm_t where parm_nm = 'EMPLOYEE_TYPES'
/
delete krns_parm_t where parm_nm = 'ELECTRONIC_FUNDS_CLAIM_EXPIRATION_DAYS'
/
delete krns_parm_t where parm_nm = 'DEFAULT_SORT_GROUP_ID'
/
delete krns_parm_t where parm_nm = 'DEFAULT_COUNTRY'
/
delete krns_parm_t where parm_nm = 'CASH_OFFSET_BANK_ACCOUNT'
/
insert into krns_parm_t (
SELECT 'KR-NS', 'All', 'DEFAULT_COUNTRY', sys_guid(), 1,
       'CONFG', 'US', 
       'Used as the default country code when relating records that do not have a country code to records that do have a country code, e.g. validating a zip code where the country is not collected."',
        'A'
  FROM  dual )
  /
insert into krns_parm_t (
SELECT 'KFS-FP', 'DisbursementVoucher', 'NON_RESIDENT_ALIEN_TAX_FEDERAL_ACCOUNT', sys_guid(), 1,
       'CONFG', '9612729', 
       'Allowed Account on the Non-resident Alien Federal Tax line.',
        'A'
  FROM  dual )
  /
insert into krns_parm_t (
SELECT 'KFS-FP', 'DisbursementVoucher', 'NON_RESIDENT_ALIEN_TAX_FEDERAL_CHART', sys_guid(), 1,
       'CONFG', 'UA', 
       'Allowed Chart on the Non-resident Alien Federal Tax line.',
        'A'
  FROM  dual )
  /
insert into krns_parm_t (
SELECT 'KFS-COA', 'OrganizationReversionCategory', 
'EXTENDED_DEFINITIONS_INCLUDE_OBJECT_CONSOLIDATIONS_BY_ORGANIZATION_REVERSION_CATEGORY', sys_guid(), 1,
       'CONFG', 
       'C01=CMPN;C02=CMPN;C03=SCHL;C04=CPTL;C05=RSRX;C06=TRSF;C07=TRSF;C08=TRVL;C09=GENX,IDEX;C10=ASEX;C11=ASRE,IDIN,OTRE,SAPR,STFE', 
       'Object consolidations to include for a given organization reversion category. Format of list is organization reversion category 1=object consolidation 1, object consolidation 2;organization reversion category 2=object consolidation 3,object consolidation 4.',
        'A'
  FROM dual )
/
insert into krns_parm_t (
SELECT 'KFS-PURAP', 'Document', 
'TAXABLE_FUND_GROUPS_FOR_TAXABLE_STATES', sys_guid(), 1,
       'CONFG', 
       'RF', 
       'A list of fund groups that are allowed/denied from being taxed based on the value of the constraint code when the delivery state is taxable',
        'A'
  FROM dual )
/
insert into krns_parm_t (
SELECT 'KFS-FP', 'DisbursementVoucher', 'NON_RESIDENT_ALIEN_TAX_FEDERAL_OBJECT_CODE_BY_INCOME_CLASS', sys_guid(), 1,
       'CONFG', 'F=9021;I=9022;R=9022', 
       'NRA Federal Tax Line objects by income class.  Format of list is income class 1=object 1;income class 2=object 2;?',
        'A'
  FROM  dual )
  /
insert into krns_parm_t (
SELECT 'KFS-FP', 'DisbursementVoucher', 'NON_RESIDENT_ALIEN_TAX_STATE_ACCOUNT', sys_guid(), 1,
       'CONFG', '9612732', 
       'Allowed Account on the Non-resident Alien State Tax line.',
        'A'
  FROM  dual )
  /
insert into krns_parm_t (
SELECT 'KFS-FP', 'DisbursementVoucher', 'NON_RESIDENT_ALIEN_TAX_STATE_CHART', sys_guid(), 1,
       'CONFG', 'UA', 
       'Allowed Chart on the Non-resident Alien State Tax line.',
        'A'
  FROM  dual )
  /
insert into krns_parm_t (
SELECT 'KFS-FP', 'DisbursementVoucher', 'NON_RESIDENT_ALIEN_TAX_STATE_OBJECT_CODE_BY_INCOME_CLASS', sys_guid(), 1,
       'CONFG', 'F=9021;I=9022;R=9022', 
       'Allowed Object Code(s) on the Non-resident Alien State Tax Line by income class. Format of list is income class 1=object 1;income class 2=object 2.',
        'A'
  FROM  dual )
  /
  
  
insert into krns_parm_t 
(SELECT 'KFS-PURAP', 'Document',
'TAXABLE_FUND_GROUPS_FOR_NON_TAXABLE_STATES', sys_guid(),1,
'CONFG', 'AE',
'A list of fund groups that are allowed/denied from being taxed based on the value of the constraint code when the delivery state is not taxable',
'A'
FROM dual)
/
insert into krns_parm_t 
(SELECT 'KFS-PURAP', 'Document',
'TAXABLE_OBJECT_CONSOLIDATIONS_FOR_NON_TAXABLE_STATES', sys_guid(),1,
'CONFG', 'ASEX',
'A list of object code consolidations that are allowed/denied from being taxed based on the value of the constraint code when the delivery state is not taxable',
'A'
FROM dual)
/
insert into krns_parm_t 
(SELECT 'KFS-PURAP', 'Document',
'TAXABLE_OBJECT_CONSOLIDATIONS_FOR_TAXABLE_STATES', sys_guid(),1,
'CONFG', 'OTRE',
'A list of object code consolidations that are allowed/denied from being taxed based on the value of the constraint code when the delivery state is taxable',
'A'
FROM dual)
/
insert into krns_parm_t 
(SELECT 'KFS-PURAP', 'Document',
'TAXABLE_OBJECT_LEVELS_FOR_NON_TAXABLE_STATES', sys_guid(),1,
'CONFG', 'CASH',
'A list of object code levels that are allowed/denied from being taxed based on the value of the constraint code when the delivery state is not taxable',
'A'
FROM dual)
/
insert into krns_parm_t 
(SELECT 'KFS-PURAP', 'Document',
'TAXABLE_OBJECT_LEVELS_FOR_TAXABLE_STATES', sys_guid(),1,
'CONFG', 'RESA',
'A list of object code levels that are allowed/denied from being taxed based on the value of the constraint code when the delivery state is taxable',
'A'
FROM dual)
/
insert into krns_parm_t 
(SELECT 'KFS-PURAP', 'Document',
'TAXABLE_SUB_FUND_GROUPS_FOR_NON_TAXABLE_STATES', sys_guid(),1,
'CONFG', 'DFRES',
'A list of sub-fund groups that are allowed/denied from being taxed based on the value of the constraint code when the delivery state is not taxable',
'A'
FROM dual)
/
insert into krns_parm_t 
(SELECT 'KFS-PURAP', 'Document',
'TAXABLE_SUB_FUND_GROUPS_FOR_TAXABLE_STATES', sys_guid(),1,
'CONFG', 'FEDERE',
'A list of sub-fund groups that are allowed/denied from being taxed based on the value of the constraint code when the delivery state is taxable',
'A'
FROM dual)
/
  
