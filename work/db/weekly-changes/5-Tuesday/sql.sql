insert into krim_perm_t values ('363', sys_guid(), 1, '23', 'Look Up Records', '', 'Y', 'KFS-PDP');
insert into krim_perm_attr_data_t values ('550', sys_guid(), 1, '363', '10', '4', 'KFS-PDP');
insert into krim_perm_attr_data_t values ('551', sys_guid(), 1, '363', '10', '5', 'PurchasingPaymentDetail');
insert into krim_role_perm_t values ('666', sys_guid(), 1, '54', '363', 'Y');
insert into krim_role_perm_t values ('667', sys_guid(), 1, '32', '363', 'Y');

delete from krim_role_perm_t where perm_id = '88';

insert into krim_role_perm_t values ('668', sys_guid(), 1, '54', '88', 'Y');
insert into krim_role_perm_t values ('669', sys_guid(), 1, '32', '88', 'Y');
commit;
delete from krns_parm_t
where parm_nm = 'COST_SHARE_ENCUMBRANCE_DOCUMENT_TYPES';
commit;

insert into krns_parm_t (
SELECT 'KFS-SYS', 'Document', 'ACCOUNTING_LINE_IMPORT', SYS_GUID(), 1,
       'CONFG', 'default.htm?turl=WordDocuments%2Faccountinglineimporttemplates.htm',
        'Relative URL for the accounting line import help. ', 'A'
  FROM dual);
  commit;
