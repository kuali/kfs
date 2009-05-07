insert into krns_parm_t
(SELECT 'KFS-GL', 'PosterIndirectCostRecoveryEntriesStep',
'ICR_EXCLUSIONS_AT_TRANSACTION_AND_TOP_LEVEL_ONLY_IND', sys_guid(),1,
'CONFG', 'Y',
'Determines, when the Poster walks up the object code reports-to hierarchy to check if an Indirect Cost Recovery Exclusion by Type or Indirect Cost Recovery Exclusion by Account exists for the given transaction, if only the transaction''s object code and the top level object code of the hierarchy should be consulted, as opposed to seeking exclusions at every step of the object code reports-to hierarchy',
'A'
FROM dual);
commit;
update pdp_payee_typ_t
set payee_typ_desc = 'Employee ID'
where payee_typ_cd = 'E';
commit;
insert into pdp_payee_typ_t
(select 'T',sys_guid(),1,'Entity ID'
from dual);
commit;
update krns_parm_t set txt = 'B=4006,4030,4032,4079,4500,4501,4503,4504,4507,4512,4513,4514,4519,4525,4530,4532,4535,4540,4549,4559,4560,4563,4564,4565,4575,4576,4577,4873,4874,5014,5042,5046,5047,5048,5074,5075,5077,5083,5084,5090,5150,5151,5152,5154,5155,B450;Z=5070;E=5070;H=5070;C=5070;L=5070;A=5070;F=5070;B=5070;T=5070;K=5070;R=5070;W=5070;P=5070;N=5070;X=5070;G=5070' where parm_nm = 'INVALID_OBJECT_CODES_BY_PAYMENT_REASON'
update krns_parm_t set txt = 'B=4006,4030,4032,4079,4500,4501,4503,4504,4507,4512,4513,4514,4519,4525,4530,4532,4535,4540,4549,4559,4560,4563,4564,4565,4575,4576,4577,4873,4874,5014,5042,5046,5047,5048,5074,5075,5077,5083,5084,5090,5150,5151,5152,5154,5155,B450;Z=5070;E=5070;H=5070;C=5070;L=5070;A=5070;F=5070;B=5070;T=5070;K=5070;R=5070;W=5070;P=5070;N=5070;X=5070;G=5070' where parm_nm = 'INVALID_OBJECT_CODES_BY_PAYMENT_REASON'
/
