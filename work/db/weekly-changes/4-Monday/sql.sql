 UPDATE KREW_DOC_TYP_T
 SET DOC_TYP_NM = 'NamespaceMaintenanceDocument'
 WHERE DOC_TYP_NM = 'RiceNamespaceMaintenanceDocument'
 ;
 commit;
 insert into krns_parm_t  (select 'KFS-GL','ScrubberStep', 
 'DOCUMENT_TYPES_REQUIRING_FLEXIBLE_OFFSET_BALANCING_ENTRIES',sys_guid(),1,
'CONFG','',
'Determines if the Scrubber should generate offsets for entries of the given document type',
'A' from dual);
commit;
Update krew_doc_typ_t
set doc_typ_nm = 'ARG'
where doc_typ_nm = 'AR'
and cur_ind ='1';

Update krew_doc_typ_t
set doc_typ_nm = 'FSSM'
where doc_typ_nm = 'FinancialSystemSimpleMaintenanceDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'COSM'
where doc_typ_nm = 'ChartSimpleMaintenanceDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'FPSM'
where doc_typ_nm = 'FinancialProcessingSimpleMaintenanceDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'PDSM'
where doc_typ_nm = 'PreDisbursementProcessorSimpleMaintenanceDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'VNSM'
where doc_typ_nm = 'VendorSimpleMaintenanceDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'ARSM'
where doc_typ_nm = 'AccountsReceivableSimpleMaintenanceDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'CMSM'
where doc_typ_nm = 'CapitalAssetManagementSimpleMaintenanceDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'ECSM'
where doc_typ_nm = 'EffortCertificationSimpleMaintenanceDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'LDSM'
where doc_typ_nm = 'LaborDistributionSimpleMaintenanceDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'PRSM'
where doc_typ_nm = 'PurchasingAccountsPayableSimpleMaintenanceDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'CGSM'
where doc_typ_nm = 'ContractsAndGrantsSimpleMaintenanceDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'BCSM'
where doc_typ_nm = 'BudgetConstructionSimpleMaintenanceDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'KFSM'
where doc_typ_nm = 'FinancialSystemComplexMaintenanceDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'COA'
where doc_typ_nm = 'ChartComplexMaintenanceDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'VEND'
where doc_typ_nm = 'VendorComplexMaintenanceDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'ARM'
where doc_typ_nm = 'AccountsReceivableComplexMaintenanceDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'CAMM'
where doc_typ_nm = 'CapitalAssetManagementComplexMaintenanceDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'CGM'
where doc_typ_nm = 'ContractsAndGrantsComplexMaintenanceDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'CAB'
where doc_typ_nm = 'CapitalAssetBuilderComplexMaintenanceDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'KFST'
where doc_typ_nm = 'FinancialSystemTransactionalDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'GL'
where doc_typ_nm = 'GeneralLedgerTransactionalDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'AR'
where doc_typ_nm = 'AccountsReceivableTransactionalDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'CAM'
where doc_typ_nm = 'CapitalAssetManagementTransactionalDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'CG'
where doc_typ_nm = 'ContractsAndGrantsTransactionalDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'RA'
where doc_typ_nm = 'ResearchTransactionalDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'EC'
where doc_typ_nm = 'EffortCertificationTransactionalDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'LD'
where doc_typ_nm = 'LaborDistributionTransactionalDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'LDYE'
where doc_typ_nm = 'LaborDistributionYearEndTransactionalDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'FPYE'
where doc_typ_nm = 'FinancialProcessingYearEndTransactionalDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'PRAP'
where doc_typ_nm = 'PurchasingAccountsPayableTransactionalDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'AP'
where doc_typ_nm = 'AccountsPayableTransactionalDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'PUR'
where doc_typ_nm = 'PurchasingTransactionalDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'RCV'
where doc_typ_nm = 'ReceivingTransactionalDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'FP'
where doc_typ_nm = 'FinancialProcessingTransactionalDocument'
and cur_ind = 1;

Update krew_doc_typ_t
set doc_typ_nm = 'BCT'
where doc_typ_nm = 'BudgetConstructionTransactionalDocument'
and cur_ind = 1;

commit;
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'ARG'
where attr_val = 'AR';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'FSSM'
where attr_val = 'FinancialSystemSimpleMaintenanceDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'COSM'
where attr_val = 'ChartSimpleMaintenanceDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'FPSM'
where attr_val = 'FinancialProcessingSimpleMaintenanceDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'PDSM'
where attr_val = 'PreDisbursementProcessorSimpleMaintenanceDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'VNSM'
where attr_val = 'VendorSimpleMaintenanceDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'ARSM'
where attr_val = 'AccountsReceivableSimpleMaintenanceDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'CMSM'
where attr_val = 'CapitalAssetManagementSimpleMaintenanceDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'ECSM'
where attr_val = 'EffortCertificationSimpleMaintenanceDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'LDSM'
where attr_val = 'LaborDistributionSimpleMaintenanceDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'PRSM'
where attr_val = 'PurchasingAccountsPayableSimpleMaintenanceDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'CGSM'
where attr_val = 'ContractsANDGrantsSimpleMaintenanceDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'BCSM'
where attr_val = 'BudgetConstructionSimpleMaintenanceDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'KFSM'
where attr_val = 'FinancialSystemComplexMaintenanceDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'COA'
where attr_val = 'ChartComplexMaintenanceDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'VEND'
where attr_val = 'VendorComplexMaintenanceDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'ARM'
where attr_val = 'AccountsReceivableComplexMaintenanceDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'CAMM'
where attr_val = 'CapitalAssetManagementComplexMaintenanceDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'CGM'
where attr_val = 'ContractsAndGrantsComplexMaintenanceDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'CAB'
where attr_val = 'CapitalAssetBuilderComplexMaintenanceDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'KFST'
where attr_val = 'FinancialSystemTransactionalDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'GL'
where attr_val = 'GeneralLedgerTransactionalDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'AR'
where attr_val = 'AccountsReceivableTransactionalDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'CAM'
where attr_val = 'CapitalAssetManagementTransactionalDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'CG'
where attr_val = 'ContractsAndGrantsTransactionalDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'RA'
where attr_val = 'ResearchTransactionalDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'EC'
where attr_val = 'EffortCertificationTransactionalDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'LD'
where attr_val = 'LaborDistributionTransactionalDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'LDYE'
where attr_val = 'LaborDistributionYearEndTransactionalDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'FPYE'
where attr_val = 'FinancialProcessingYearEndTransactionalDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'PRAP'
where attr_val = 'PurchasingAccountsPayableTransactionalDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'AP'
where attr_val = 'AccountsPayableTransactionalDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'PUR'
where attr_val = 'PurchasingTransactionalDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'RCV'
where attr_val = 'ReceivingTransactionalDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'FP'
where attr_val = 'FinancialProcessingTransactionalDocument';
Update krim_dlgn_mbr_attr_data_t   
set attr_val = 'BCT'
where attr_val = 'BudgetConstructionTransactionalDocument';
commit;
Update krim_perm_attr_data_t 
set attr_val = 'ARG'
where attr_val = 'AR';
Update krim_perm_attr_data_t 
set attr_val = 'FSSM'
where attr_val = 'FinancialSystemSimpleMaintenanceDocument';
Update krim_perm_attr_data_t 
set attr_val = 'COSM'
where attr_val = 'ChartSimpleMaintenanceDocument';
Update krim_perm_attr_data_t 
set attr_val = 'FPSM'
where attr_val = 'FinancialProcessingSimpleMaintenanceDocument';
Update krim_perm_attr_data_t 
set attr_val = 'PDSM'
where attr_val = 'PreDisbursementProcessorSimpleMaintenanceDocument';
Update krim_perm_attr_data_t 
set attr_val = 'VNSM'
where attr_val = 'VendorSimpleMaintenanceDocument';
Update krim_perm_attr_data_t 
set attr_val = 'ARSM'
where attr_val = 'AccountsReceivableSimpleMaintenanceDocument';
Update krim_perm_attr_data_t 
set attr_val = 'CMSM'
where attr_val = 'CapitalAssetManagementSimpleMaintenanceDocument';
Update krim_perm_attr_data_t 
set attr_val = 'ECSM'
where attr_val = 'EffortCertificationSimpleMaintenanceDocument';
Update krim_perm_attr_data_t 
set attr_val = 'LDSM'
where attr_val = 'LaborDistributionSimpleMaintenanceDocument';
Update krim_perm_attr_data_t 
set attr_val = 'PRSM'
where attr_val = 'PurchasingAccountsPayableSimpleMaintenanceDocument';
Update krim_perm_attr_data_t 
set attr_val = 'CGSM'
where attr_val = 'ContractsANDGrantsSimpleMaintenanceDocument';
Update krim_perm_attr_data_t 
set attr_val = 'BCSM'
where attr_val = 'BudgetConstructionSimpleMaintenanceDocument';
Update krim_perm_attr_data_t 
set attr_val = 'KFSM'
where attr_val = 'FinancialSystemComplexMaintenanceDocument';
Update krim_perm_attr_data_t 
set attr_val = 'COA'
where attr_val = 'ChartComplexMaintenanceDocument';
Update krim_perm_attr_data_t 
set attr_val = 'VEND'
where attr_val = 'VendorComplexMaintenanceDocument';
Update krim_perm_attr_data_t 
set attr_val = 'ARM'
where attr_val = 'AccountsReceivableComplexMaintenanceDocument';
Update krim_perm_attr_data_t 
set attr_val = 'CAMM'
where attr_val = 'CapitalAssetManagementComplexMaintenanceDocument';
Update krim_perm_attr_data_t 
set attr_val = 'CGM'
where attr_val = 'ContractsAndGrantsComplexMaintenanceDocument';
Update krim_perm_attr_data_t 
set attr_val = 'CAB'
where attr_val = 'CapitalAssetBuilderComplexMaintenanceDocument';
Update krim_perm_attr_data_t 
set attr_val = 'KFST'
where attr_val = 'FinancialSystemTransactionalDocument';
Update krim_perm_attr_data_t 
set attr_val = 'GL'
where attr_val = 'GeneralLedgerTransactionalDocument';
Update krim_perm_attr_data_t 
set attr_val = 'AR'
where attr_val = 'AccountsReceivableTransactionalDocument';
Update krim_perm_attr_data_t 
set attr_val = 'CAM'
where attr_val = 'CapitalAssetManagementTransactionalDocument';
Update krim_perm_attr_data_t 
set attr_val = 'CG'
where attr_val = 'ContractsAndGrantsTransactionalDocument';
Update krim_perm_attr_data_t 
set attr_val = 'RA'
where attr_val = 'ResearchTransactionalDocument';
Update krim_perm_attr_data_t 
set attr_val = 'EC'
where attr_val = 'EffortCertificationTransactionalDocument';
Update krim_perm_attr_data_t 
set attr_val = 'LD'
where attr_val = 'LaborDistributionTransactionalDocument';
Update krim_perm_attr_data_t 
set attr_val = 'LDYE'
where attr_val = 'LaborDistributionYearEndTransactionalDocument';
Update krim_perm_attr_data_t 
set attr_val = 'FPYE'
where attr_val = 'FinancialProcessingYearEndTransactionalDocument';
Update krim_perm_attr_data_t 
set attr_val = 'PRAP'
where attr_val = 'PurchasingAccountsPayableTransactionalDocument';
Update krim_perm_attr_data_t 
set attr_val = 'AP'
where attr_val = 'AccountsPayableTransactionalDocument';
Update krim_perm_attr_data_t 
set attr_val = 'PUR'
where attr_val = 'PurchasingTransactionalDocument';
Update krim_perm_attr_data_t 
set attr_val = 'RCV'
where attr_val = 'ReceivingTransactionalDocument';
Update krim_perm_attr_data_t 
set attr_val = 'FP'
where attr_val = 'FinancialProcessingTransactionalDocument';
Update krim_perm_attr_data_t 
set attr_val = 'BCT'
where attr_val = 'BudgetConstructionTransactionalDocument';
commit;
Update krim_role_mbr_attr_data_t  
set attr_val = 'ARG'
where attr_val = 'AR';
Update krim_role_mbr_attr_data_t  
set attr_val = 'FSSM'
where attr_val = 'FinancialSystemSimpleMaintenanceDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'COSM'
where attr_val = 'ChartSimpleMaintenanceDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'FPSM'
where attr_val = 'FinancialProcessingSimpleMaintenanceDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'PDSM'
where attr_val = 'PreDisbursementProcessorSimpleMaintenanceDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'VNSM'
where attr_val = 'VendorSimpleMaintenanceDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'ARSM'
where attr_val = 'AccountsReceivableSimpleMaintenanceDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'CMSM'
where attr_val = 'CapitalAssetManagementSimpleMaintenanceDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'ECSM'
where attr_val = 'EffortCertificationSimpleMaintenanceDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'LDSM'
where attr_val = 'LaborDistributionSimpleMaintenanceDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'PRSM'
where attr_val = 'PurchasingAccountsPayableSimpleMaintenanceDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'CGSM'
where attr_val = 'ContractsANDGrantsSimpleMaintenanceDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'BCSM'
where attr_val = 'BudgetConstructionSimpleMaintenanceDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'KFSM'
where attr_val = 'FinancialSystemComplexMaintenanceDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'COA'
where attr_val = 'ChartComplexMaintenanceDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'VEND'
where attr_val = 'VendorComplexMaintenanceDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'ARM'
where attr_val = 'AccountsReceivableComplexMaintenanceDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'CAMM'
where attr_val = 'CapitalAssetManagementComplexMaintenanceDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'CGM'
where attr_val = 'ContractsAndGrantsComplexMaintenanceDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'CAB'
where attr_val = 'CapitalAssetBuilderComplexMaintenanceDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'KFST'
where attr_val = 'FinancialSystemTransactionalDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'GL'
where attr_val = 'GeneralLedgerTransactionalDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'AR'
where attr_val = 'AccountsReceivableTransactionalDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'CAM'
where attr_val = 'CapitalAssetManagementTransactionalDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'CG'
where attr_val = 'ContractsAndGrantsTransactionalDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'RA'
where attr_val = 'ResearchTransactionalDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'EC'
where attr_val = 'EffortCertificationTransactionalDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'LD'
where attr_val = 'LaborDistributionTransactionalDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'LDYE'
where attr_val = 'LaborDistributionYearEndTransactionalDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'FPYE'
where attr_val = 'FinancialProcessingYearEndTransactionalDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'PRAP'
where attr_val = 'PurchasingAccountsPayableTransactionalDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'AP'
where attr_val = 'AccountsPayableTransactionalDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'PUR'
where attr_val = 'PurchasingTransactionalDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'RCV'
where attr_val = 'ReceivingTransactionalDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'FP'
where attr_val = 'FinancialProcessingTransactionalDocument';
Update krim_role_mbr_attr_data_t  
set attr_val = 'BCT'
where attr_val = 'BudgetConstructionTransactionalDocument';
commit;
Update krim_rsp_attr_data_t
set attr_val = 'ARG'
where attr_val = 'AR';
Update krim_rsp_attr_data_t
set attr_val = 'FSSM'
where attr_val = 'FinancialSystemSimpleMaintenanceDocument';
Update krim_rsp_attr_data_t
set attr_val = 'COSM'
where attr_val = 'ChartSimpleMaintenanceDocument';
Update krim_rsp_attr_data_t
set attr_val = 'FPSM'
where attr_val = 'FinancialProcessingSimpleMaintenanceDocument';
Update krim_rsp_attr_data_t
set attr_val = 'PDSM'
where attr_val = 'PreDisbursementProcessorSimpleMaintenanceDocument';
Update krim_rsp_attr_data_t
set attr_val = 'VNSM'
where attr_val = 'VendorSimpleMaintenanceDocument';
Update krim_rsp_attr_data_t
set attr_val = 'ARSM'
where attr_val = 'AccountsReceivableSimpleMaintenanceDocument';
Update krim_rsp_attr_data_t
set attr_val = 'CMSM'
where attr_val = 'CapitalAssetManagementSimpleMaintenanceDocument';
Update krim_rsp_attr_data_t
set attr_val = 'ECSM'
where attr_val = 'EffortCertificationSimpleMaintenanceDocument';
Update krim_rsp_attr_data_t
set attr_val = 'LDSM'
where attr_val = 'LaborDistributionSimpleMaintenanceDocument';
Update krim_rsp_attr_data_t
set attr_val = 'PRSM'
where attr_val = 'PurchasingAccountsPayableSimpleMaintenanceDocument';
Update krim_rsp_attr_data_t
set attr_val = 'CGSM'
where attr_val = 'ContractsANDGrantsSimpleMaintenanceDocument';
Update krim_rsp_attr_data_t
set attr_val = 'BCSM'
where attr_val = 'BudgetConstructionSimpleMaintenanceDocument';
Update krim_rsp_attr_data_t
set attr_val = 'KFSM'
where attr_val = 'FinancialSystemComplexMaintenanceDocument';
Update krim_rsp_attr_data_t
set attr_val = 'COA'
where attr_val = 'ChartComplexMaintenanceDocument';
Update krim_rsp_attr_data_t
set attr_val = 'VEND'
where attr_val = 'VendorComplexMaintenanceDocument';
Update krim_rsp_attr_data_t
set attr_val = 'ARM'
where attr_val = 'AccountsReceivableComplexMaintenanceDocument';
Update krim_rsp_attr_data_t
set attr_val = 'CAMM'
where attr_val = 'CapitalAssetManagementComplexMaintenanceDocument';
Update krim_rsp_attr_data_t
set attr_val = 'CGM'
where attr_val = 'ContractsAndGrantsComplexMaintenanceDocument';
Update krim_rsp_attr_data_t
set attr_val = 'CAB'
where attr_val = 'CapitalAssetBuilderComplexMaintenanceDocument';
Update krim_rsp_attr_data_t
set attr_val = 'KFST'
where attr_val = 'FinancialSystemTransactionalDocument';
Update krim_rsp_attr_data_t
set attr_val = 'GL'
where attr_val = 'GeneralLedgerTransactionalDocument';
Update krim_rsp_attr_data_t
set attr_val = 'AR'
where attr_val = 'AccountsReceivableTransactionalDocument';
Update krim_rsp_attr_data_t
set attr_val = 'CAM'
where attr_val = 'CapitalAssetManagementTransactionalDocument';
Update krim_rsp_attr_data_t
set attr_val = 'CG'
where attr_val = 'ContractsAndGrantsTransactionalDocument';
Update krim_rsp_attr_data_t
set attr_val = 'RA'
where attr_val = 'ResearchTransactionalDocument';
Update krim_rsp_attr_data_t
set attr_val = 'EC'
where attr_val = 'EffortCertificationTransactionalDocument';
Update krim_rsp_attr_data_t
set attr_val = 'LD'
where attr_val = 'LaborDistributionTransactionalDocument';
Update krim_rsp_attr_data_t
set attr_val = 'LDYE'
where attr_val = 'LaborDistributionYearEndTransactionalDocument';
Update krim_rsp_attr_data_t
set attr_val = 'FPYE'
where attr_val = 'FinancialProcessingYearEndTransactionalDocument';
Update krim_rsp_attr_data_t
set attr_val = 'PRAP'
where attr_val = 'PurchasingAccountsPayableTransactionalDocument';
Update krim_rsp_attr_data_t
set attr_val = 'AP'
where attr_val = 'AccountsPayableTransactionalDocument';
Update krim_rsp_attr_data_t
set attr_val = 'PUR'
where attr_val = 'PurchasingTransactionalDocument';
Update krim_rsp_attr_data_t
set attr_val = 'RCV'
where attr_val = 'ReceivingTransactionalDocument';
Update krim_rsp_attr_data_t
set attr_val = 'FP'
where attr_val = 'FinancialProcessingTransactionalDocument';
Update krim_rsp_attr_data_t
set attr_val = 'BCT'
where attr_val = 'BudgetConstructionTransactionalDocument';
commit;
update krim_rsp_attr_data_t set attr_val = 'AccountFullEdit' where attr_val = 'AccountFullEntry'
commit;
update krns_parm_t
set txt=''
where nmspc_cd = 'KFS-PURAP'
and parm_nm in ('DAILY_SUMMARY_REPORT_FROM_EMAIL_ADDRESS',
'DAILY_SUMMARY_REPORT_TO_EMAIL_ADDRESSES');
commit;
alter table ER_RSRCH_RSK_TYP_T
drop column RSRCH_RSK_TYP_NTFCTN_GRP_TXT
;
alter table AR_DOC_RCURRNC_T
drop column WRKGRP_NM
;
alter table AR_SYS_INFO_T
drop column CSH_CTRL_WRKGRP_NM
; 
