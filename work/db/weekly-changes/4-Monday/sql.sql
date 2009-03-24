insert into krns_parm_t
(SELECT 'KFS-LD', 'LaborBalancingStep',
'NUMBER_OF_PAST_FISCAL_YEARS_TO_CONSIDER', sys_guid(),1,
'CONFG', '1',
'Number of fiscal years to subtract from current fiscal year to represent what t
he start range on the balancing batch job is. This is used for laborBalancingSte
p.',
'A'
FROM dual)
;
insert into krns_parm_t
(SELECT 'KFS-LD', 'LaborBalancingStep',
'NUMBER_OF_COMPARISON_FAILURES_TO_PRINT_PER_REPORT', sys_guid(),1,
'CONFG', '10',
'Total number of failures to print on the report for each category of balancing
failures on the balancing batch job. This is used for laborBalancingStep.',
'A'
FROM dual)
;
insert into krns_parm_t
(SELECT 'KFS-GL', 'PosterBalancingStep',
'NUMBER_OF_PAST_FISCAL_YEARS_TO_CONSIDER', sys_guid(),1,
'CONFG', '1',
'Number of fiscal years to subtract from current fiscal year to represent what t
he start range on the balancing batch job is. This is used for posterBalancingSt
ep.',
'A'
FROM dual)
;
insert into krns_parm_t
(SELECT 'KFS-GL', 'PosterBalancingStep',
'NUMBER_OF_COMPARISON_FAILURES_TO_PRINT_PER_REPORT', sys_guid(),1,
'CONFG', '10',
'Total number of failures to print on the report for each category of balancing
failures on the balancing batch job. This is used for posterBalancingStep.',
'A'
FROM dual)
;
alter table FS_DOC_HEADER_T
drop constraint FS_DOC_HEADER_TR1;
drop table FS_DOC_TYP_CD_T;
