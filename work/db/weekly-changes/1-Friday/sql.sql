delete from krew_doc_typ_t
where doc_typ_nm ='UniversalUserMaintenanceDocument'
/
delete from krew_doc_typ_t
where doc_typ_nm = 'EDENSERVICE-DOCS.DocumentType'
/
delete from krew_doc_typ_t
where doc_typ_nm ='PayeeMaintenanceDocument'
/
delete from krew_doc_typ_t
where doc_typ_nm = 'KualiBaseDocument'
/
delete from krew_doc_typ_t
where doc_typ_nm = 'EmployeeStatusMaintenanceDocument'
/
delete from krew_doc_typ_t
where doc_typ_nm = 'EmployeeTypeMaintenanceDocument'
/
delete from krew_doc_typ_t
where doc_typ_nm = 'KualiEmployeeStatusMaintenanceDocument'
/
delete from krew_doc_typ_t
where doc_typ_nm = 'KualiEmployeeTypeMaintenanceDocument'
/
delete from krew_doc_typ_t
where doc_typ_nm = 'BankAccountMaintenanceDocument'
/
delete from krew_doc_typ_t
where doc_typ_nm = 'EDENSERVICE-DOCS'
/
delete from krew_doc_typ_t
where doc_typ_nm = 'EDENSERVICE-DOCS.RuleDocument'
/
delete from krew_doc_typ_t
where doc_typ_nm ='NonpersonnelSubCategoryMaintenanceDocument'
/
delete from krew_doc_typ_t
where doc_typ_nm ='UniversalUserMaintenanceDocument'
/
delete from krew_doc_typ_t
where doc_typ_nm = 'EDENSERVICE-DOCS.DocumentType'
/
delete from krew_doc_typ_t
where doc_typ_nm ='PayeeMaintenanceDocument'
/
delete from krew_doc_typ_t
where doc_typ_nm = 'KualiBaseDocument'
/
delete from krew_doc_typ_t
where doc_typ_nm = 'EmployeeStatusMaintenanceDocument'
/
delete from krew_doc_typ_t
where doc_typ_nm = 'EmployeeTypeMaintenanceDocument'
/
delete from krew_doc_typ_t
where doc_typ_nm = 'KualiEmployeeStatusMaintenanceDocument'
/
delete from krew_doc_typ_t
where doc_typ_nm = 'KualiEmployeeTypeMaintenanceDocument'
/
delete from krew_doc_typ_t
where doc_typ_nm = 'BankAccountMaintenanceDocument'
/
delete from krew_doc_typ_t
where doc_typ_nm = 'EDENSERVICE-DOCS'
/
delete from krew_doc_typ_t
where doc_typ_nm = 'EDENSERVICE-DOCS.RuleDocument'
/
delete from krew_doc_typ_t
where doc_typ_nm ='NonpersonnelSubCategoryMaintenanceDocument'
/
insert into krns_parm_t 
(SELECT 'KFS-AR', 'CustomerInvoiceWriteoff',
'APPROVAL_THRESHOLD', sys_guid(),1,
'CONFG', '50',
'Writing off invoices of this value or higher requires Fiscal Officer approval.',
'A'
FROM dual)
/
alter table CG_AWD_T drop column WRKGRP_NM
/ 