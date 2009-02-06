/* updating FunctionControlCodeMaintenanceDocument to BFNC */
update krew_doc_typ_t set doc_typ_nm = 'BFNC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320570
/
/* updating FiscalYearFunctionControlMaintenanceDocument to BFYC */
update krew_doc_typ_t set doc_typ_nm = 'BFYC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320569
/
/* updating PurchaseOrderQuoteStatusMaintenanceDocument to PMQS */
update krew_doc_typ_t set doc_typ_nm = 'PMQS', ver_nbr = ver_nbr + 1 where doc_typ_id = 320762
/
/* updating OffsetAccountMaintenanceDocument to OFAC */
update krew_doc_typ_t set doc_typ_nm = 'OFAC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320535
/
/* updating RecurringPaymentFrequencyMaintenanceDocument to PMRF */
update krew_doc_typ_t set doc_typ_nm = 'PMRF', ver_nbr = ver_nbr + 1 where doc_typ_id = 320773
/
/* updating FundingSourceMaintenanceDocument to PMFS */
update krew_doc_typ_t set doc_typ_nm = 'PMFS', ver_nbr = ver_nbr + 1 where doc_typ_id = 320748
/
/* updating FinancialSystemDocumentTypeCodeMaintenanceDocument to FSDT */
update krew_doc_typ_t set doc_typ_nm = 'FSDT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320502
/
/* updating LineItemReceivingStatusMaintenanceDocument to PMLI */
update krew_doc_typ_t set doc_typ_nm = 'PMLI', ver_nbr = ver_nbr + 1 where doc_typ_id = 320802
/
/* updating UniversityBudgetOfficeFunctionMaintenanceDocument to UFUN */
update krew_doc_typ_t set doc_typ_nm = 'UFUN', ver_nbr = ver_nbr + 1 where doc_typ_id = 320553
/
/* updating CustomerAddressTypeMaintenanceDocument to CATY */
update krew_doc_typ_t set doc_typ_nm = 'CATY', ver_nbr = ver_nbr + 1 where doc_typ_id = 320631
/
/* updating ShippingTitleMaintenanceDocument to PMST */
update krew_doc_typ_t set doc_typ_nm = 'PMST', ver_nbr = ver_nbr + 1 where doc_typ_id = 320622
/
/* updating AssetStatusMaintenanceDocument to ASTA */
update krew_doc_typ_t set doc_typ_nm = 'ASTA', ver_nbr = ver_nbr + 1 where doc_typ_id = 320671
/
/* updating YearEndBenefitExpenseTransferDocument to YEBT */
update krew_doc_typ_t set doc_typ_nm = 'YEBT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320730
/
/* updating AccountingChangeCodeMaintenanceDocument to ACTC */
update krew_doc_typ_t set doc_typ_nm = 'ACTC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320597
/
/* updating CashControlDocument to CTRL */
update krew_doc_typ_t set doc_typ_nm = 'CTRL', ver_nbr = ver_nbr + 1 where doc_typ_id = 320630
/
/* updating perm attr data  from attr val CashControlDocument to CTRL */
update krim_perm_attr_data_t set attr_val = 'CTRL', ver_nbr = ver_nbr + 1 where attr_data_id = '1'
/
/* updating perm attr data  from attr val CashControlDocument to CTRL */
update krim_perm_attr_data_t set attr_val = 'CTRL', ver_nbr = ver_nbr + 1 where attr_data_id = '9'
/
/* updating rsp attr data  from attr val CashControlDocument to CTRL */
update krim_rsp_attr_data_t set attr_val = 'CTRL', ver_nbr = ver_nbr + 1 where attr_data_id = '6'
/
/* updating rsp attr data  from attr val CashControlDocument to CTRL */
update krim_rsp_attr_data_t set attr_val = 'CTRL', ver_nbr = ver_nbr + 1 where attr_data_id = '2'
/
/* updating TravelExpenseTypeCodeMaintenanceDocument to DVET */
update krew_doc_typ_t set doc_typ_nm = 'DVET', ver_nbr = ver_nbr + 1 where doc_typ_id = 320586
/
/* updating PurchaseOrderContractLanguageMaintenanceDocument to PMCL */
update krew_doc_typ_t set doc_typ_nm = 'PMCL', ver_nbr = ver_nbr + 1 where doc_typ_id = 320758
/
/* updating OrganizationAccountingDefaultMaintenanceDocument to OADF */
update krew_doc_typ_t set doc_typ_nm = 'OADF', ver_nbr = ver_nbr + 1 where doc_typ_id = 320639
/
/* updating perm attr data  from attr val OrganizationAccountingDefaultMaintenanceDocument to OADF */
update krim_perm_attr_data_t set attr_val = 'OADF', ver_nbr = ver_nbr + 1 where attr_data_id = '10'
/
/* updating ObjectConsolidationMaintenanceDocument to OBJC */
update krew_doc_typ_t set doc_typ_nm = 'OBJC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320531
/
/* updating NonPersonnelCategoryMaintenanceDocument to NPC */
update krew_doc_typ_t set doc_typ_nm = 'NPC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320697
/
/* updating CFDACloseDocument to CLOS */
update krew_doc_typ_t set doc_typ_nm = 'CLOS', ver_nbr = ver_nbr + 1 where doc_typ_id = 320685
/
/* updating perm attr data  from attr val CFDACloseDocument to CLOS */
update krim_perm_attr_data_t set attr_val = 'CLOS', ver_nbr = ver_nbr + 1 where attr_data_id = '68'
/
/* updating rsp attr data  from attr val CFDACloseDocument to CLOS */
update krim_rsp_attr_data_t set attr_val = 'CLOS', ver_nbr = ver_nbr + 1 where attr_data_id = '409'
/
/* updating rsp attr data  from attr val CFDACloseDocument to CLOS */
update krim_rsp_attr_data_t set attr_val = 'CLOS', ver_nbr = ver_nbr + 1 where attr_data_id = '413'
/
/* updating BalanceTypeMaintenanceDocument to BTYP */
update krew_doc_typ_t set doc_typ_nm = 'BTYP', ver_nbr = ver_nbr + 1 where doc_typ_id = 320519
/
/* updating AccountDelegateGlobalMaintenanceDocument to GDLG */
update krew_doc_typ_t set doc_typ_nm = 'GDLG', ver_nbr = ver_nbr + 1 where doc_typ_id = 320511
/
/* updating rsp attr data  from attr val AccountDelegateGlobalMaintenanceDocument to GDLG */
update krim_rsp_attr_data_t set attr_val = 'GDLG', ver_nbr = ver_nbr + 1 where attr_data_id = '66'
/
/* updating PaymentChangeCodeMaintenanceDocument to PMTC */
update krew_doc_typ_t set doc_typ_nm = 'PMTC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320605
/
/* updating CashDrawerMaintenanceDocument to CDS */
update krew_doc_typ_t set doc_typ_nm = 'CDS', ver_nbr = ver_nbr + 1 where doc_typ_id = 320560
/
/* updating PhoneTypeMaintenanceDocument to PMPT */
update krew_doc_typ_t set doc_typ_nm = 'PMPT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320618
/
/* updating ServiceBillingDocument to SB */
update krew_doc_typ_t set doc_typ_nm = 'SB', ver_nbr = ver_nbr + 1 where doc_typ_id = 320581
/
/* updating perm attr data  from attr val ServiceBillingDocument to SB */
update krim_perm_attr_data_t set attr_val = 'SB', ver_nbr = ver_nbr + 1 where attr_data_id = '94'
/
/* updating perm attr data  from attr val ServiceBillingDocument to SB */
update krim_perm_attr_data_t set attr_val = 'SB', ver_nbr = ver_nbr + 1 where attr_data_id = '265'
/
/* updating AssetDepreciationMethodMaintenanceDocument to DPRM */
update krew_doc_typ_t set doc_typ_nm = 'DPRM', ver_nbr = ver_nbr + 1 where doc_typ_id = 320661
/
/* updating SubObjectCodeMaintenanceDocument to SOBJ */
update krew_doc_typ_t set doc_typ_nm = 'SOBJ', ver_nbr = ver_nbr + 1 where doc_typ_id = 320551
/
/* updating rsp attr data  from attr val SubObjectCodeMaintenanceDocument to SOBJ */
update krim_rsp_attr_data_t set attr_val = 'SOBJ', ver_nbr = ver_nbr + 1 where attr_data_id = '118'
/
/* updating PaymentApplicationDocument to APP */
update krew_doc_typ_t set doc_typ_nm = 'APP', ver_nbr = ver_nbr + 1 where doc_typ_id = 320641
/
/* updating perm attr data  from attr val PaymentApplicationDocument to APP */
update krim_perm_attr_data_t set attr_val = 'APP', ver_nbr = ver_nbr + 1 where attr_data_id = '8'
/
/* updating rsp attr data  from attr val PaymentApplicationDocument to APP */
update krim_rsp_attr_data_t set attr_val = 'APP', ver_nbr = ver_nbr + 1 where attr_data_id = '22'
/
/* updating PaymentTermTypeMaintenanceDocument to PMPA */
update krew_doc_typ_t set doc_typ_nm = 'PMPA', ver_nbr = ver_nbr + 1 where doc_typ_id = 320617
/
/* updating EffortCertificationPeriodStatusCodeMaintenanceDocument to ECPS */
update krew_doc_typ_t set doc_typ_nm = 'ECPS', ver_nbr = ver_nbr + 1 where doc_typ_id = 320714
/
/* updating PaymentMediumMaintenanceDocument to PMED */
update krew_doc_typ_t set doc_typ_nm = 'PMED', ver_nbr = ver_nbr + 1 where doc_typ_id = 320642
/
/* updating EffortCertificationReportEarnPaygroupMaintenanceDocument to ECPG */
update krew_doc_typ_t set doc_typ_nm = 'ECPG', ver_nbr = ver_nbr + 1 where doc_typ_id = 320716
/
/* updating UnitOfMeasureMaintenanceDocument to PMUM */
update krew_doc_typ_t set doc_typ_nm = 'PMUM', ver_nbr = ver_nbr + 1 where doc_typ_id = 320779
/
/* updating OrganizationReversionCategoryMaintenanceDocument to ORGC */
update krew_doc_typ_t set doc_typ_nm = 'ORGC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320538
/
/* updating AssetMaintenanceDocument to CASM */
update krew_doc_typ_t set doc_typ_nm = 'CASM', ver_nbr = ver_nbr + 1 where doc_typ_id = 320666
/
/* updating perm attr data  from attr val AssetMaintenanceDocument to CASM */
update krim_perm_attr_data_t set attr_val = 'CASM', ver_nbr = ver_nbr + 1 where attr_data_id = '365'
/
/* updating DistributionOfIncomeAndExpenseDocument to DI */
update krew_doc_typ_t set doc_typ_nm = 'DI', ver_nbr = ver_nbr + 1 where doc_typ_id = 320834
/
/* updating perm attr data  from attr val DistributionOfIncomeAndExpenseDocument to DI */
update krim_perm_attr_data_t set attr_val = 'DI', ver_nbr = ver_nbr + 1 where attr_data_id = '90'
/
/* updating rsp attr data  from attr val DistributionOfIncomeAndExpenseDocument to DI */
update krim_rsp_attr_data_t set attr_val = 'DI', ver_nbr = ver_nbr + 1 where attr_data_id = '170'
/
/* updating rsp attr data  from attr val DistributionOfIncomeAndExpenseDocument to DI */
update krim_rsp_attr_data_t set attr_val = 'DI', ver_nbr = ver_nbr + 1 where attr_data_id = '174'
/
/* updating BudgetDocument to KBD */
update krew_doc_typ_t set doc_typ_nm = 'KBD', ver_nbr = ver_nbr + 1 where doc_typ_id = 320684
/
/* updating perm attr data  from attr val BudgetDocument to KBD */
update krim_perm_attr_data_t set attr_val = 'KBD', ver_nbr = ver_nbr + 1 where attr_data_id = '324'
/
/* updating AgencyMaintenanceDocument to AGCY */
update krew_doc_typ_t set doc_typ_nm = 'AGCY', ver_nbr = ver_nbr + 1 where doc_typ_id = 320679
/
/* updating ItemReasonAddedMaintenanceDocument to IRAD */
update krew_doc_typ_t set doc_typ_nm = 'IRAD', ver_nbr = ver_nbr + 1 where doc_typ_id = 320749
/
/* updating ObjectTypeMaintenanceDocument to OTYP */
update krew_doc_typ_t set doc_typ_nm = 'OTYP', ver_nbr = ver_nbr + 1 where doc_typ_id = 320534
/
/* updating AICPAFunctionMaintenanceDocument to AFUN */
update krew_doc_typ_t set doc_typ_nm = 'AFUN', ver_nbr = ver_nbr + 1 where doc_typ_id = 320518
/
/* updating AssetObjectCodeMaintenanceDocument to COBJ */
update krew_doc_typ_t set doc_typ_nm = 'COBJ', ver_nbr = ver_nbr + 1 where doc_typ_id = 320667
/
/* updating SupplierDiversityMaintenanceDocument to PMSD */
update krew_doc_typ_t set doc_typ_nm = 'PMSD', ver_nbr = ver_nbr + 1 where doc_typ_id = 320623
/
/* updating EffortCertificationReportTypeMaintenanceDocument to ECRT */
update krew_doc_typ_t set doc_typ_nm = 'ECRT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320717
/
/* updating MessageOfTheDayMaintenanceDocument to MOTD */
update krew_doc_typ_t set doc_typ_nm = 'MOTD', ver_nbr = ver_nbr + 1 where doc_typ_id = 320503
/
/* updating PurchaseOrderDocument to PO */
update krew_doc_typ_t set doc_typ_nm = 'PO', ver_nbr = ver_nbr + 1 where doc_typ_id = 320737
/
/* updating perm attr data  from attr val PurchaseOrderDocument to PO */
update krim_perm_attr_data_t set attr_val = 'PO', ver_nbr = ver_nbr + 1 where attr_data_id = '117'
/
/* updating perm attr data  from attr val PurchaseOrderDocument to PO */
update krim_perm_attr_data_t set attr_val = 'PO', ver_nbr = ver_nbr + 1 where attr_data_id = '124'
/
/* updating perm attr data  from attr val PurchaseOrderDocument to PO */
update krim_perm_attr_data_t set attr_val = 'PO', ver_nbr = ver_nbr + 1 where attr_data_id = '300'
/
/* updating rsp attr data  from attr val PurchaseOrderDocument to PO */
update krim_rsp_attr_data_t set attr_val = 'PO', ver_nbr = ver_nbr + 1 where attr_data_id = '266'
/
/* updating rsp attr data  from attr val PurchaseOrderDocument to PO */
update krim_rsp_attr_data_t set attr_val = 'PO', ver_nbr = ver_nbr + 1 where attr_data_id = '270'
/
/* updating rsp attr data  from attr val PurchaseOrderDocument to PO */
update krim_rsp_attr_data_t set attr_val = 'PO', ver_nbr = ver_nbr + 1 where attr_data_id = '274'
/
/* updating rsp attr data  from attr val PurchaseOrderDocument to PO */
update krim_rsp_attr_data_t set attr_val = 'PO', ver_nbr = ver_nbr + 1 where attr_data_id = '278'
/
/* updating rsp attr data  from attr val PurchaseOrderDocument to PO */
update krim_rsp_attr_data_t set attr_val = 'PO', ver_nbr = ver_nbr + 1 where attr_data_id = '282'
/
/* updating ChartMaintenanceDocument to COAT */
update krew_doc_typ_t set doc_typ_nm = 'COAT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320523
/
/* updating ProposalStatusMaintenanceDocument to PRST */
update krew_doc_typ_t set doc_typ_nm = 'PRST', ver_nbr = ver_nbr + 1 where doc_typ_id = 320704
/
/* updating TaxIncomeClassCodeMaintenanceDocument to DVIC */
update krew_doc_typ_t set doc_typ_nm = 'DVIC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320583
/
/* updating ACHBankMaintenanceDocument to ABNK */
update krew_doc_typ_t set doc_typ_nm = 'ABNK', ver_nbr = ver_nbr + 1 where doc_typ_id = 320598
/
/* updating InvoiceRecurrenceMaintenanceDocument to INVR */
update krew_doc_typ_t set doc_typ_nm = 'INVR', ver_nbr = ver_nbr + 1 where doc_typ_id = 320638
/
/* updating rsp attr data  from attr val InvoiceRecurrenceMaintenanceDocument to INVR */
update krim_rsp_attr_data_t set attr_val = 'INVR', ver_nbr = ver_nbr + 1 where attr_data_id = '18'
/
/* updating rsp attr data  from attr val InvoiceRecurrenceMaintenanceDocument to INVR */
update krim_rsp_attr_data_t set attr_val = 'INVR', ver_nbr = ver_nbr + 1 where attr_data_id = '364'
/
/* updating ContactTypeMaintenanceDocument to PMCT */
update krew_doc_typ_t set doc_typ_nm = 'PMCT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320613
/
/* updating AssetConditionMaintenanceDocument to ACON */
update krew_doc_typ_t set doc_typ_nm = 'ACON', ver_nbr = ver_nbr + 1 where doc_typ_id = 320658
/
/* updating OwnershipTypeCodeMaintenanceDocument to DVOT */
update krew_doc_typ_t set doc_typ_nm = 'DVOT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320577
/
/* updating PaymentReasonCodeMaintenanceDocument to DVPR */
update krew_doc_typ_t set doc_typ_nm = 'DVPR', ver_nbr = ver_nbr + 1 where doc_typ_id = 320578
/
/* updating RoomMaintenanceDocument to ROOM */
update krew_doc_typ_t set doc_typ_nm = 'ROOM', ver_nbr = ver_nbr + 1 where doc_typ_id = 320504
/
/* updating VendorTypeMaintenanceDocument to PMVT */
update krew_doc_typ_t set doc_typ_nm = 'PMVT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320626
/
/* updating OrganizationReversionGlobalMaintenanceDocument to GORV */
update krew_doc_typ_t set doc_typ_nm = 'GORV', ver_nbr = ver_nbr + 1 where doc_typ_id = 320539
/
/* updating rsp attr data  from attr val OrganizationReversionGlobalMaintenanceDocument to GORV */
update krim_rsp_attr_data_t set attr_val = 'GORV', ver_nbr = ver_nbr + 1 where attr_data_id = '102'
/
/* updating TaxRegionTypeMaintenanceDocument to TRTP */
update krew_doc_typ_t set doc_typ_nm = 'TRTP', ver_nbr = ver_nbr + 1 where doc_typ_id = 320507
/
/* updating SystemOptionsMaintenanceDocument to SOPT */
update krew_doc_typ_t set doc_typ_nm = 'SOPT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320505
/
/* updating AdvanceDepositDocument to AD */
update krew_doc_typ_t set doc_typ_nm = 'AD', ver_nbr = ver_nbr + 1 where doc_typ_id = 320557
/
/* updating ResponsibilityCenterMaintenanceDocument to RCEN */
update krew_doc_typ_t set doc_typ_nm = 'RCEN', ver_nbr = ver_nbr + 1 where doc_typ_id = 320545
/
/* updating GrantDescriptionMaintenanceDocument to GDES */
update krew_doc_typ_t set doc_typ_nm = 'GDES', ver_nbr = ver_nbr + 1 where doc_typ_id = 320690
/
/* updating OffsetDefinitionMaintenanceDocument to OFSD */
update krew_doc_typ_t set doc_typ_nm = 'OFSD', ver_nbr = ver_nbr + 1 where doc_typ_id = 320536
/
/* updating PurchaseOrderQuoteListMaintenanceDocument to PMQT */
update krew_doc_typ_t set doc_typ_nm = 'PMQT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320761
/
/* updating FederalFundedCodeMaintenanceDocument to FFC */
update krew_doc_typ_t set doc_typ_nm = 'FFC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320525
/
/* updating PurchaseOrderTransmissionMethodMaintenanceDocument to PMTM */
update krew_doc_typ_t set doc_typ_nm = 'PMTM', ver_nbr = ver_nbr + 1 where doc_typ_id = 320768
/
/* updating ContractManagerMaintenanceDocument to PMCO */
update krew_doc_typ_t set doc_typ_nm = 'PMCO', ver_nbr = ver_nbr + 1 where doc_typ_id = 320614
/
/* updating OrganizationParameterMaintenanceDocument to PMOP */
update krew_doc_typ_t set doc_typ_nm = 'PMOP', ver_nbr = ver_nbr + 1 where doc_typ_id = 320753
/
/* updating TaxControlCodeMaintenanceDocument to DVCC */
update krew_doc_typ_t set doc_typ_nm = 'DVCC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320582
/
/* updating CalculatedSalaryFoundationTrackerOverrideMaintenanceDocument to CSFO */
update krew_doc_typ_t set doc_typ_nm = 'CSFO', ver_nbr = ver_nbr + 1 where doc_typ_id = 320651
/
/* updating CampusParameterMaintenanceDocument to PMCP */
update krew_doc_typ_t set doc_typ_nm = 'PMCP', ver_nbr = ver_nbr + 1 where doc_typ_id = 320611
/
/* updating ResearchRiskTypeMaintenanceDocument to RRT */
update krew_doc_typ_t set doc_typ_nm = 'RRT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320707
/
/* updating QuestionTypeMaintenanceDocument to QNT */
update krew_doc_typ_t set doc_typ_nm = 'QNT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320706
/
/* updating AssetDepreciationDocument to DEPR */
update krew_doc_typ_t set doc_typ_nm = 'DEPR', ver_nbr = ver_nbr + 1 where doc_typ_id = 320660
/
/* updating perm attr data  from attr val AssetDepreciationDocument to DEPR */
update krim_perm_attr_data_t set attr_val = 'DEPR', ver_nbr = ver_nbr + 1 where attr_data_id = '426'
/
/* updating PurchaseOrderCloseDocument to POC */
update krew_doc_typ_t set doc_typ_nm = 'POC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320757
/
/* updating perm attr data  from attr val PurchaseOrderCloseDocument to POC */
update krim_perm_attr_data_t set attr_val = 'POC', ver_nbr = ver_nbr + 1 where attr_data_id = '118'
/
/* updating AssetTypeMaintenanceDocument to ASST */
update krew_doc_typ_t set doc_typ_nm = 'ASST', ver_nbr = ver_nbr + 1 where doc_typ_id = 320673
/
/* updating PretagMaintenanceDocument to PTAG */
update krew_doc_typ_t set doc_typ_nm = 'PTAG', ver_nbr = ver_nbr + 1 where doc_typ_id = 320782
/
/* updating PaymentRequestStatusMaintenanceDocument to PMPR */
update krew_doc_typ_t set doc_typ_nm = 'PMPR', ver_nbr = ver_nbr + 1 where doc_typ_id = 320755
/
/* updating KeywordMaintenanceDocument to KEYW */
update krew_doc_typ_t set doc_typ_nm = 'KEYW', ver_nbr = ver_nbr + 1 where doc_typ_id = 320695
/
/* updating CarrierMaintenanceDocument to PMCA */
update krew_doc_typ_t set doc_typ_nm = 'PMCA', ver_nbr = ver_nbr + 1 where doc_typ_id = 320742
/
/* updating NonPersonnelSubCategoryMaintenanceDocument to NPSC */
update krew_doc_typ_t set doc_typ_nm = 'NPSC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320699
/
/* updating EffortCertificationReportDefinitionMaintenanceDocument to ECRD */
update krew_doc_typ_t set doc_typ_nm = 'ECRD', ver_nbr = ver_nbr + 1 where doc_typ_id = 320715
/
/* updating ProjectTypeMaintenanceDocument to PRJT */
update krew_doc_typ_t set doc_typ_nm = 'PRJT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320700
/
/* updating AccountDelegateMaintenanceDocument to ADEL */
update krew_doc_typ_t set doc_typ_nm = 'ADEL', ver_nbr = ver_nbr + 1 where doc_typ_id = 320512
/
/* updating rsp attr data  from attr val AccountDelegateMaintenanceDocument to ADEL */
update krim_rsp_attr_data_t set attr_val = 'ADEL', ver_nbr = ver_nbr + 1 where attr_data_id = '70'
/
/* updating PayeeACHAccountMaintenanceDocument to PAAT */
update krew_doc_typ_t set doc_typ_nm = 'PAAT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320801
/
/* updating CreditCardTypeMaintenanceDocument to CCTY */
update krew_doc_typ_t set doc_typ_nm = 'CCTY', ver_nbr = ver_nbr + 1 where doc_typ_id = 320564
/
/* updating AssetDepreciationConventionMaintenanceDocument to DPRC */
update krew_doc_typ_t set doc_typ_nm = 'DPRC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320659
/
/* updating OrganizationReversionMaintenanceDocument to ORGR */
update krew_doc_typ_t set doc_typ_nm = 'ORGR', ver_nbr = ver_nbr + 1 where doc_typ_id = 320540
/
/* updating perm attr data  from attr val OrganizationReversionMaintenanceDocument to ORGR */
update krim_perm_attr_data_t set attr_val = 'ORGR', ver_nbr = ver_nbr + 1 where attr_data_id = '75'
/
/* updating AwardMaintenanceDocument to AWRD */
update krew_doc_typ_t set doc_typ_nm = 'AWRD', ver_nbr = ver_nbr + 1 where doc_typ_id = 320682
/
/* updating PurchaseOrderAmendmentDocument to POA */
update krew_doc_typ_t set doc_typ_nm = 'POA', ver_nbr = ver_nbr + 1 where doc_typ_id = 320756
/
/* updating rsp attr data  from attr val PurchaseOrderAmendmentDocument to POA */
update krim_rsp_attr_data_t set attr_val = 'POA', ver_nbr = ver_nbr + 1 where attr_data_id = '262'
/
/* updating rsp attr data  from attr val PurchaseOrderAmendmentDocument to POA */
update krim_rsp_attr_data_t set attr_val = 'POA', ver_nbr = ver_nbr + 1 where attr_data_id = '393'
/
/* updating AccountTypeMaintenanceDocument to ATYP */
update krew_doc_typ_t set doc_typ_nm = 'ATYP', ver_nbr = ver_nbr + 1 where doc_typ_id = 320517
/
/* updating SufficientFundsCodeMaintenanceDocument to SFC */
update krew_doc_typ_t set doc_typ_nm = 'SFC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320552
/
/* updating SystemInformationMaintenanceDocument to ARSI */
update krew_doc_typ_t set doc_typ_nm = 'ARSI', ver_nbr = ver_nbr + 1 where doc_typ_id = 320643
/
/* updating BenefitsCalculationMaintenanceDocument to BCAL */
update krew_doc_typ_t set doc_typ_nm = 'BCAL', ver_nbr = ver_nbr + 1 where doc_typ_id = 320722
/
/* updating RestrictedStatusMaintenanceDocument to RSTA */
update krew_doc_typ_t set doc_typ_nm = 'RSTA', ver_nbr = ver_nbr + 1 where doc_typ_id = 320546
/
/* updating TaxRegionMaintenanceDocument to TAXR */
update krew_doc_typ_t set doc_typ_nm = 'TAXR', ver_nbr = ver_nbr + 1 where doc_typ_id = 320506
/
/* updating FundGroupMaintenanceDocument to FGRP */
update krew_doc_typ_t set doc_typ_nm = 'FGRP', ver_nbr = ver_nbr + 1 where doc_typ_id = 320526
/
/* updating PurposeMaintenanceDocument to PRPS */
update krew_doc_typ_t set doc_typ_nm = 'PRPS', ver_nbr = ver_nbr + 1 where doc_typ_id = 320705
/
/* updating AccountMaintenanceDocument to ACCT */
update krew_doc_typ_t set doc_typ_nm = 'ACCT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320516
/
/* updating rsp attr data  from attr val AccountMaintenanceDocument to ACCT */
update krim_rsp_attr_data_t set attr_val = 'ACCT', ver_nbr = ver_nbr + 1 where attr_data_id = '78'
/
/* updating rsp attr data  from attr val AccountMaintenanceDocument to ACCT */
update krim_rsp_attr_data_t set attr_val = 'ACCT', ver_nbr = ver_nbr + 1 where attr_data_id = '82'
/
/* updating rsp attr data  from attr val AccountMaintenanceDocument to ACCT */
update krim_rsp_attr_data_t set attr_val = 'ACCT', ver_nbr = ver_nbr + 1 where attr_data_id = '86'
/
/* updating PurchaseOrderVendorChoiceMaintenanceDocument to PMVC */
update krew_doc_typ_t set doc_typ_nm = 'PMVC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320769
/
/* updating IndirectCostAdjustmentDocument to ICA */
update krew_doc_typ_t set doc_typ_nm = 'ICA', ver_nbr = ver_nbr + 1 where doc_typ_id = 320836
/
/* updating rsp attr data  from attr val IndirectCostAdjustmentDocument to ICA */
update krim_rsp_attr_data_t set attr_val = 'ICA', ver_nbr = ver_nbr + 1 where attr_data_id = '190'
/
/* updating rsp attr data  from attr val IndirectCostAdjustmentDocument to ICA */
update krim_rsp_attr_data_t set attr_val = 'ICA', ver_nbr = ver_nbr + 1 where attr_data_id = '194'
/
/* updating YearEndGeneralErrorCorrectionDocument to YEGE */
update krew_doc_typ_t set doc_typ_nm = 'YEGE', ver_nbr = ver_nbr + 1 where doc_typ_id = 320592
/
/* updating ACHTransactionTypeMaintenanceDocument to ACHT */
update krew_doc_typ_t set doc_typ_nm = 'ACHT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320600
/
/* updating ItemTypeMaintenanceDocument to PMIT */
update krew_doc_typ_t set doc_typ_nm = 'PMIT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320750
/
/* updating SalaryExpenseTransferDocument to ST */
update krew_doc_typ_t set doc_typ_nm = 'ST', ver_nbr = ver_nbr + 1 where doc_typ_id = 320846
/
/* updating perm attr data  from attr val SalaryExpenseTransferDocument to ST */
update krim_perm_attr_data_t set attr_val = 'ST', ver_nbr = ver_nbr + 1 where attr_data_id = '289'
/
/* updating perm attr data  from attr val SalaryExpenseTransferDocument to ST */
update krim_perm_attr_data_t set attr_val = 'ST', ver_nbr = ver_nbr + 1 where attr_data_id = '299'
/
/* updating perm attr data  from attr val SalaryExpenseTransferDocument to ST */
update krim_perm_attr_data_t set attr_val = 'ST', ver_nbr = ver_nbr + 1 where attr_data_id = '302'
/
/* updating rsp attr data  from attr val SalaryExpenseTransferDocument to ST */
update krim_rsp_attr_data_t set attr_val = 'ST', ver_nbr = ver_nbr + 1 where attr_data_id = '238'
/
/* updating rsp attr data  from attr val SalaryExpenseTransferDocument to ST */
update krim_rsp_attr_data_t set attr_val = 'ST', ver_nbr = ver_nbr + 1 where attr_data_id = '242'
/
/* updating InternalBillingDocument to IB */
update krew_doc_typ_t set doc_typ_nm = 'IB', ver_nbr = ver_nbr + 1 where doc_typ_id = 320837
/
/* updating rsp attr data  from attr val InternalBillingDocument to IB */
update krim_rsp_attr_data_t set attr_val = 'IB', ver_nbr = ver_nbr + 1 where attr_data_id = '198'
/
/* updating AddressTypeMaintenanceDocument to PMAT */
update krew_doc_typ_t set doc_typ_nm = 'PMAT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320610
/
/* updating LaborLedgerCorrectionProcessDocument to LLCP */
update krew_doc_typ_t set doc_typ_nm = 'LLCP', ver_nbr = ver_nbr + 1 where doc_typ_id = 320725
/
/* updating ObjectSubTypeMaintenanceDocument to OSTY */
update krew_doc_typ_t set doc_typ_nm = 'OSTY', ver_nbr = ver_nbr + 1 where doc_typ_id = 320533
/
/* updating PurchaseOrderSplitDocument to POSP */
update krew_doc_typ_t set doc_typ_nm = 'POSP', ver_nbr = ver_nbr + 1 where doc_typ_id = 320766
/
/* updating PurchaseOrderStatusMaintenanceDocument to PMPS */
update krew_doc_typ_t set doc_typ_nm = 'PMPS', ver_nbr = ver_nbr + 1 where doc_typ_id = 320767
/
/* updating LaborObjectMaintenanceDocument to LOBJ */
update krew_doc_typ_t set doc_typ_nm = 'LOBJ', ver_nbr = ver_nbr + 1 where doc_typ_id = 320726
/
/* updating BenefitExpenseTransferDocument to BT */
update krew_doc_typ_t set doc_typ_nm = 'BT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320844
/
/* updating rsp attr data  from attr val BenefitExpenseTransferDocument to BT */
update krim_rsp_attr_data_t set attr_val = 'BT', ver_nbr = ver_nbr + 1 where attr_data_id = '230'
/
/* updating rsp attr data  from attr val BenefitExpenseTransferDocument to BT */
update krim_rsp_attr_data_t set attr_val = 'BT', ver_nbr = ver_nbr + 1 where attr_data_id = '226'
/
/* updating BillingAddressMaintenanceDocument to PMBA */
update krew_doc_typ_t set doc_typ_nm = 'PMBA', ver_nbr = ver_nbr + 1 where doc_typ_id = 320739
/
/* updating PositionObjectBenefitMaintenanceDocument to LOBN */
update krew_doc_typ_t set doc_typ_nm = 'LOBN', ver_nbr = ver_nbr + 1 where doc_typ_id = 320727
/
/* updating DisbursementVoucherDocumentationLocationMaintenanceDocument to DVDL */
update krew_doc_typ_t set doc_typ_nm = 'DVDL', ver_nbr = ver_nbr + 1 where doc_typ_id = 320567
/
/* updating CashReceiptDocument to CR */
update krew_doc_typ_t set doc_typ_nm = 'CR', ver_nbr = ver_nbr + 1 where doc_typ_id = 320562
/
/* updating rsp attr data  from attr val CashReceiptDocument to CR */
update krim_rsp_attr_data_t set attr_val = 'CR', ver_nbr = ver_nbr + 1 where attr_data_id = '138'
/
/* updating CustomerCreditMemoDocument to CRM */
update krew_doc_typ_t set doc_typ_nm = 'CRM', ver_nbr = ver_nbr + 1 where doc_typ_id = 320632
/
/* updating rsp attr data  from attr val CustomerCreditMemoDocument to CRM */
update krim_rsp_attr_data_t set attr_val = 'CRM', ver_nbr = ver_nbr + 1 where attr_data_id = '10'
/
/* updating BudgetConstructionPositionMaintenanceDocument to BCPS */
update krew_doc_typ_t set doc_typ_nm = 'BCPS', ver_nbr = ver_nbr + 1 where doc_typ_id = 320650
/
/* updating PurchaseOrderVoidDocument to POV */
update krew_doc_typ_t set doc_typ_nm = 'POV', ver_nbr = ver_nbr + 1 where doc_typ_id = 320770
/
/* updating BudgetRecordingLevelMaintenanceDocument to BRL */
update krew_doc_typ_t set doc_typ_nm = 'BRL', ver_nbr = ver_nbr + 1 where doc_typ_id = 320522
/
/* updating AppointmentTypeMaintenanceDocument to APPT */
update krew_doc_typ_t set doc_typ_nm = 'APPT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320681
/
/* updating VendorCreditMemoDocument to CM */
update krew_doc_typ_t set doc_typ_nm = 'CM', ver_nbr = ver_nbr + 1 where doc_typ_id = 320824
/
/* updating rsp attr data  from attr val VendorCreditMemoDocument to CM */
update krim_rsp_attr_data_t set attr_val = 'CM', ver_nbr = ver_nbr + 1 where attr_data_id = '397'
/
/* updating DueDateTypeMaintenanceDocument to DDT */
update krew_doc_typ_t set doc_typ_nm = 'DDT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320688
/
/* updating AssetTransactionTypeMaintenanceDocument to PMTT */
update krew_doc_typ_t set doc_typ_nm = 'PMTT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320783
/
/* updating AccountDelegateModelMaintenanceDocument to GDLM */
update krew_doc_typ_t set doc_typ_nm = 'GDLM', ver_nbr = ver_nbr + 1 where doc_typ_id = 320513
/
/* updating PurchaseOrderRemoveHoldDocument to PORH */
update krew_doc_typ_t set doc_typ_nm = 'PORH', ver_nbr = ver_nbr + 1 where doc_typ_id = 320763
/
/* updating rsp attr data  from attr val PurchaseOrderRemoveHoldDocument to PORH */
update krim_rsp_attr_data_t set attr_val = 'PORH', ver_nbr = ver_nbr + 1 where attr_data_id = '286'
/
/* updating AccountingPeriodMaintenanceDocument to APRD */
update krew_doc_typ_t set doc_typ_nm = 'APRD', ver_nbr = ver_nbr + 1 where doc_typ_id = 320515
/
/* updating ProposalAwardTypeMaintenanceDocument to PATY */
update krew_doc_typ_t set doc_typ_nm = 'PATY', ver_nbr = ver_nbr + 1 where doc_typ_id = 320701
/
/* updating FunctionalFieldDescriptionMaintenanceDocument to FFD */
update krew_doc_typ_t set doc_typ_nm = 'FFD', ver_nbr = ver_nbr + 1 where doc_typ_id = 320501
/
/* updating YearEndTransferOfFundsDocument to YETF */
update krew_doc_typ_t set doc_typ_nm = 'YETF', ver_nbr = ver_nbr + 1 where doc_typ_id = 320593
/
/* updating ObjectCodeGlobalMaintenanceDocument to GOBJ */
update krew_doc_typ_t set doc_typ_nm = 'GOBJ', ver_nbr = ver_nbr + 1 where doc_typ_id = 320529
/
/* updating perm attr data  from attr val ObjectCodeGlobalMaintenanceDocument to GOBJ */
update krim_perm_attr_data_t set attr_val = 'GOBJ', ver_nbr = ver_nbr + 1 where attr_data_id = '74'
/
/* updating rsp attr data  from attr val ObjectCodeGlobalMaintenanceDocument to GOBJ */
update krim_rsp_attr_data_t set attr_val = 'GOBJ', ver_nbr = ver_nbr + 1 where attr_data_id = '90'
/
/* updating CommodityCodeMaintenanceDocument to PMCC */
update krew_doc_typ_t set doc_typ_nm = 'PMCC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320612
/
/* updating AutoApproveExcludeMaintenanceDocument to PMAA */
update krew_doc_typ_t set doc_typ_nm = 'PMAA', ver_nbr = ver_nbr + 1 where doc_typ_id = 320738
/
/* updating AgencyTypeMaintenanceDocument to AGTY */
update krew_doc_typ_t set doc_typ_nm = 'AGTY', ver_nbr = ver_nbr + 1 where doc_typ_id = 320680
/
/* updating UniversityDateMaintenanceDocument to UDAT */
update krew_doc_typ_t set doc_typ_nm = 'UDAT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320508
/
/* updating FederalFunctionMaintenanceDocument to FFUN */
update krew_doc_typ_t set doc_typ_nm = 'FFUN', ver_nbr = ver_nbr + 1 where doc_typ_id = 320524
/
/* updating SensitiveDataMaintenanceDocument to PMSN */
update krew_doc_typ_t set doc_typ_nm = 'PMSN', ver_nbr = ver_nbr + 1 where doc_typ_id = 320778
/
/* updating NonPersonnelObjectCodeMaintenanceDocument to NPOC */
update krew_doc_typ_t set doc_typ_nm = 'NPOC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320698
/
/* updating AssetGlobalMaintenanceDocument to AA */
update krew_doc_typ_t set doc_typ_nm = 'AA', ver_nbr = ver_nbr + 1 where doc_typ_id = 320663
/
/* updating rsp attr data  from attr val AssetGlobalMaintenanceDocument to AA */
update krim_rsp_attr_data_t set attr_val = 'AA', ver_nbr = ver_nbr + 1 where attr_data_id = '377'
/
/* updating ReceivingAddressMaintenanceDocument to PMRA */
update krew_doc_typ_t set doc_typ_nm = 'PMRA', ver_nbr = ver_nbr + 1 where doc_typ_id = 320771
/
/* updating SubFundGroupTypeMaintenanceDocument to SFGT */
update krew_doc_typ_t set doc_typ_nm = 'SFGT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320549
/
/* updating AssetLocationTypeMaintenanceDocument to ASLT */
update krew_doc_typ_t set doc_typ_nm = 'ASLT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320665
/
/* updating DisbursementNumberRangeMaintenanceDocument to DBRG */
update krew_doc_typ_t set doc_typ_nm = 'DBRG', ver_nbr = ver_nbr + 1 where doc_typ_id = 320602
/
/* updating SubObjectCodeGlobalMaintenanceDocument to GSOB */
update krew_doc_typ_t set doc_typ_nm = 'GSOB', ver_nbr = ver_nbr + 1 where doc_typ_id = 320550
/
/* updating rsp attr data  from attr val SubObjectCodeGlobalMaintenanceDocument to GSOB */
update krim_rsp_attr_data_t set attr_val = 'GSOB', ver_nbr = ver_nbr + 1 where attr_data_id = '110'
/
/* updating rsp attr data  from attr val SubObjectCodeGlobalMaintenanceDocument to GSOB */
update krim_rsp_attr_data_t set attr_val = 'GSOB', ver_nbr = ver_nbr + 1 where attr_data_id = '114'
/
/* updating ObjectLevelMaintenanceDocument to OBJL */
update krew_doc_typ_t set doc_typ_nm = 'OBJL', ver_nbr = ver_nbr + 1 where doc_typ_id = 320532
/
/* updating NegativePaymentRequestApprovalLimitMaintenanceDocument to PMNP */
update krew_doc_typ_t set doc_typ_nm = 'PMNP', ver_nbr = ver_nbr + 1 where doc_typ_id = 320752
/
/* updating RequisitionDocument to REQS */
update krew_doc_typ_t set doc_typ_nm = 'REQS', ver_nbr = ver_nbr + 1 where doc_typ_id = 320848
/
/* updating perm attr data  from attr val RequisitionDocument to REQS */
update krim_perm_attr_data_t set attr_val = 'REQS', ver_nbr = ver_nbr + 1 where attr_data_id = '115'
/
/* updating perm attr data  from attr val RequisitionDocument to REQS */
update krim_perm_attr_data_t set attr_val = 'REQS', ver_nbr = ver_nbr + 1 where attr_data_id = '123'
/
/* updating rsp attr data  from attr val RequisitionDocument to REQS */
update krim_rsp_attr_data_t set attr_val = 'REQS', ver_nbr = ver_nbr + 1 where attr_data_id = '298'
/
/* updating rsp attr data  from attr val RequisitionDocument to REQS */
update krim_rsp_attr_data_t set attr_val = 'REQS', ver_nbr = ver_nbr + 1 where attr_data_id = '302'
/
/* updating rsp attr data  from attr val RequisitionDocument to REQS */
update krim_rsp_attr_data_t set attr_val = 'REQS', ver_nbr = ver_nbr + 1 where attr_data_id = '306'
/
/* updating AssetRetirementGlobalMaintenanceDocument to AR */
update krew_doc_typ_t set doc_typ_nm = 'AR', ver_nbr = ver_nbr + 1 where doc_typ_id = 320669
/
/* updating rsp attr data  from attr val AssetRetirementGlobalMaintenanceDocument to AR */
update krim_rsp_attr_data_t set attr_val = 'AR', ver_nbr = ver_nbr + 1 where attr_data_id = '30'
/
/* updating rsp attr data  from attr val AssetRetirementGlobalMaintenanceDocument to AR */
update krim_rsp_attr_data_t set attr_val = 'AR', ver_nbr = ver_nbr + 1 where attr_data_id = '38'
/
/* updating rsp attr data  from attr val AssetRetirementGlobalMaintenanceDocument to AR */
update krim_rsp_attr_data_t set attr_val = 'AR', ver_nbr = ver_nbr + 1 where attr_data_id = '42'
/
/* updating AccountReportsMaintenanceDocument to BCAR */
update krew_doc_typ_t set doc_typ_nm = 'BCAR', ver_nbr = ver_nbr + 1 where doc_typ_id = 320646
/
/* updating RequisitionStatusMaintenanceDocument to PMRS */
update krew_doc_typ_t set doc_typ_nm = 'PMRS', ver_nbr = ver_nbr + 1 where doc_typ_id = 320777
/
/* updating VendorInactiveReasonMaintenanceDocument to PMIR */
update krew_doc_typ_t set doc_typ_nm = 'PMIR', ver_nbr = ver_nbr + 1 where doc_typ_id = 320624
/
/* updating CustomerInvoiceWriteoffDocument to INVW */
update krew_doc_typ_t set doc_typ_nm = 'INVW', ver_nbr = ver_nbr + 1 where doc_typ_id = 320635
/
/* updating rsp attr data  from attr val CustomerInvoiceWriteoffDocument to INVW */
update krim_rsp_attr_data_t set attr_val = 'INVW', ver_nbr = ver_nbr + 1 where attr_data_id = '14'
/
/* updating LetterOfCreditFundGroupMaintenanceDocument to LFGR */
update krew_doc_typ_t set doc_typ_nm = 'LFGR', ver_nbr = ver_nbr + 1 where doc_typ_id = 320696
/
/* updating GraduateAssistantRateMaintenanceDocument to GAR */
update krew_doc_typ_t set doc_typ_nm = 'GAR', ver_nbr = ver_nbr + 1 where doc_typ_id = 320689
/
/* updating WireChargeMaintenanceDocument to DVWT */
update krew_doc_typ_t set doc_typ_nm = 'DVWT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320589
/
/* updating PurchaseOrderCostSourceMaintenanceDocument to PMCS */
update krew_doc_typ_t set doc_typ_nm = 'PMCS', ver_nbr = ver_nbr + 1 where doc_typ_id = 320619
/
/* updating RoutingFormDocument to KRFD */
update krew_doc_typ_t set doc_typ_nm = 'KRFD', ver_nbr = ver_nbr + 1 where doc_typ_id = 320709
/
/* updating perm attr data  from attr val RoutingFormDocument to KRFD */
update krim_perm_attr_data_t set attr_val = 'KRFD', ver_nbr = ver_nbr + 1 where attr_data_id = '67'
/
/* updating perm attr data  from attr val RoutingFormDocument to KRFD */
update krim_perm_attr_data_t set attr_val = 'KRFD', ver_nbr = ver_nbr + 1 where attr_data_id = '389'
/
/* updating perm attr data  from attr val RoutingFormDocument to KRFD */
update krim_perm_attr_data_t set attr_val = 'KRFD', ver_nbr = ver_nbr + 1 where attr_data_id = '323'
/
/* updating rsp attr data  from attr val RoutingFormDocument to KRFD */
update krim_rsp_attr_data_t set attr_val = 'KRFD', ver_nbr = ver_nbr + 1 where attr_data_id = '62'
/
/* updating rsp attr data  from attr val RoutingFormDocument to KRFD */
update krim_rsp_attr_data_t set attr_val = 'KRFD', ver_nbr = ver_nbr + 1 where attr_data_id = '345'
/
/* updating BenefitsTypeMaintenanceDocument to BENT */
update krew_doc_typ_t set doc_typ_nm = 'BENT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320723
/
/* updating ReportingCodeMaintenanceDocument to RPTC */
update krew_doc_typ_t set doc_typ_nm = 'RPTC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320544
/
/* updating AssetRetirementReasonMaintenanceDocument to RRSN */
update krew_doc_typ_t set doc_typ_nm = 'RRSN', ver_nbr = ver_nbr + 1 where doc_typ_id = 320670
/
/* updating OwnershipCategoryMaintenanceDocument to PMOC */
update krew_doc_typ_t set doc_typ_nm = 'PMOC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320615
/
/* updating VendorMaintenanceDocument to PVEN */
update krew_doc_typ_t set doc_typ_nm = 'PVEN', ver_nbr = ver_nbr + 1 where doc_typ_id = 320625
/
/* updating perm attr data  from attr val VendorMaintenanceDocument to PVEN */
update krim_perm_attr_data_t set attr_val = 'PVEN', ver_nbr = ver_nbr + 1 where attr_data_id = '172'
/
/* updating rsp attr data  from attr val VendorMaintenanceDocument to PVEN */
update krim_rsp_attr_data_t set attr_val = 'PVEN', ver_nbr = ver_nbr + 1 where attr_data_id = '326'
/
/* updating rsp attr data  from attr val VendorMaintenanceDocument to PVEN */
update krim_rsp_attr_data_t set attr_val = 'PVEN', ver_nbr = ver_nbr + 1 where attr_data_id = '330'
/
/* updating AssetPaymentDocument to MPAY */
update krew_doc_typ_t set doc_typ_nm = 'MPAY', ver_nbr = ver_nbr + 1 where doc_typ_id = 320668
/
/* updating perm attr data  from attr val AssetPaymentDocument to MPAY */
update krim_perm_attr_data_t set attr_val = 'MPAY', ver_nbr = ver_nbr + 1 where attr_data_id = '22'
/
/* updating perm attr data  from attr val AssetPaymentDocument to MPAY */
update krim_perm_attr_data_t set attr_val = 'MPAY', ver_nbr = ver_nbr + 1 where attr_data_id = '262'
/
/* updating rsp attr data  from attr val AssetPaymentDocument to MPAY */
update krim_rsp_attr_data_t set attr_val = 'MPAY', ver_nbr = ver_nbr + 1 where attr_data_id = '34'
/
/* updating ResearchTypeCodeMaintenanceDocument to RTCM */
update krew_doc_typ_t set doc_typ_nm = 'RTCM', ver_nbr = ver_nbr + 1 where doc_typ_id = 320708
/
/* updating AssetAcquisitionTypeMaintenanceDocument to ACQT */
update krew_doc_typ_t set doc_typ_nm = 'ACQT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320657
/
/* updating OrganizationTypeMaintenanceDocument to ORTY */
update krew_doc_typ_t set doc_typ_nm = 'ORTY', ver_nbr = ver_nbr + 1 where doc_typ_id = 320541
/
/* updating IndirectCostRecoveryTypeMaintenanceDocument to ITYP */
update krew_doc_typ_t set doc_typ_nm = 'ITYP', ver_nbr = ver_nbr + 1 where doc_typ_id = 320694
/
/* updating YearEndBudgetAdjustmentDocument to YEBA */
update krew_doc_typ_t set doc_typ_nm = 'YEBA', ver_nbr = ver_nbr + 1 where doc_typ_id = 320590
/
/* updating CustomerInvoiceItemCodeMaintenanceDocument to IICO */
update krew_doc_typ_t set doc_typ_nm = 'IICO', ver_nbr = ver_nbr + 1 where doc_typ_id = 320634
/
/* updating PurchaseOrderReopenDocument to POR */
update krew_doc_typ_t set doc_typ_nm = 'POR', ver_nbr = ver_nbr + 1 where doc_typ_id = 320764
/
/* updating PurchaseOrderQuoteLanguageMaintenanceDocument to PMQL */
update krew_doc_typ_t set doc_typ_nm = 'PMQL', ver_nbr = ver_nbr + 1 where doc_typ_id = 320760
/
/* updating DisbursementTypeMaintenanceDocument to DSTP */
update krew_doc_typ_t set doc_typ_nm = 'DSTP', ver_nbr = ver_nbr + 1 where doc_typ_id = 320603
/
/* updating ShippingPaymentTermsMaintenanceDocument to PMSP */
update krew_doc_typ_t set doc_typ_nm = 'PMSP', ver_nbr = ver_nbr + 1 where doc_typ_id = 320620
/
/* updating ShippingSpecialConditionMaintenanceDocument to PMSS */
update krew_doc_typ_t set doc_typ_nm = 'PMSS', ver_nbr = ver_nbr + 1 where doc_typ_id = 320621
/
/* updating EquipmentLoanOrReturnDocument to ELR */
update krew_doc_typ_t set doc_typ_nm = 'ELR', ver_nbr = ver_nbr + 1 where doc_typ_id = 320675
/
/* updating rsp attr data  from attr val EquipmentLoanOrReturnDocument to ELR */
update krim_rsp_attr_data_t set attr_val = 'ELR', ver_nbr = ver_nbr + 1 where attr_data_id = '46'
/
/* updating rsp attr data  from attr val EquipmentLoanOrReturnDocument to ELR */
update krim_rsp_attr_data_t set attr_val = 'ELR', ver_nbr = ver_nbr + 1 where attr_data_id = '50'
/
/* updating SubAccountMaintenanceDocument to SACC */
update krew_doc_typ_t set doc_typ_nm = 'SACC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320547
/
/* updating rsp attr data  from attr val SubAccountMaintenanceDocument to SACC */
update krim_rsp_attr_data_t set attr_val = 'SACC', ver_nbr = ver_nbr + 1 where attr_data_id = '106'
/
/* updating BankMaintenanceDocument to BANK */
update krew_doc_typ_t set doc_typ_nm = 'BANK', ver_nbr = ver_nbr + 1 where doc_typ_id = 320499
/
/* updating perm attr data  from attr val BankMaintenanceDocument to BANK */
update krim_perm_attr_data_t set attr_val = 'BANK', ver_nbr = ver_nbr + 1 where attr_data_id = '145'
/
/* updating CustomerMaintenanceDocument to CUS */
update krew_doc_typ_t set doc_typ_nm = 'CUS', ver_nbr = ver_nbr + 1 where doc_typ_id = 320636
/
/* updating perm attr data  from attr val CustomerMaintenanceDocument to CUS */
update krim_perm_attr_data_t set attr_val = 'CUS', ver_nbr = ver_nbr + 1 where attr_data_id = '358'
/
/* updating rsp attr data  from attr val CustomerMaintenanceDocument to CUS */
update krim_rsp_attr_data_t set attr_val = 'CUS', ver_nbr = ver_nbr + 1 where attr_data_id = '389'
/
/* updating AssetLocationGlobalMaintenanceDocument to ALOC */
update krew_doc_typ_t set doc_typ_nm = 'ALOC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320664
/
/* updating OrganizationOptionsMaintenanceDocument to OOPT */
update krew_doc_typ_t set doc_typ_nm = 'OOPT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320640
/
/* updating perm attr data  from attr val OrganizationOptionsMaintenanceDocument to OOPT */
update krim_perm_attr_data_t set attr_val = 'OOPT', ver_nbr = ver_nbr + 1 where attr_data_id = '363'
/
/* updating perm attr data  from attr val OrganizationOptionsMaintenanceDocument to OOPT */
update krim_perm_attr_data_t set attr_val = 'OOPT', ver_nbr = ver_nbr + 1 where attr_data_id = '361'
/
/* updating rsp attr data  from attr val OrganizationOptionsMaintenanceDocument to OOPT */
update krim_rsp_attr_data_t set attr_val = 'OOPT', ver_nbr = ver_nbr + 1 where attr_data_id = '368'
/
/* updating BuildingMaintenanceDocument to BLDG */
update krew_doc_typ_t set doc_typ_nm = 'BLDG', ver_nbr = ver_nbr + 1 where doc_typ_id = 320500
/
/* updating ObjectCodeMaintenanceDocument to OBJT */
update krew_doc_typ_t set doc_typ_nm = 'OBJT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320530
/
/* updating rsp attr data  from attr val ObjectCodeMaintenanceDocument to OBJT */
update krim_rsp_attr_data_t set attr_val = 'OBJT', ver_nbr = ver_nbr + 1 where attr_data_id = '94'
/
/* updating CapitalAssetSystemTypeMaintenanceDocument to PMSY */
update krew_doc_typ_t set doc_typ_nm = 'PMSY', ver_nbr = ver_nbr + 1 where doc_typ_id = 320741
/
/* updating TravelCompanyCodeMaintenanceDocument to DVTC */
update krew_doc_typ_t set doc_typ_nm = 'DVTC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320585
/
/* updating LineItemReceivingDocument to RCVL */
update krew_doc_typ_t set doc_typ_nm = 'RCVL', ver_nbr = ver_nbr + 1 where doc_typ_id = 320751
/
/* updating rsp attr data  from attr val LineItemReceivingDocument to RCVL */
update krim_rsp_attr_data_t set attr_val = 'RCVL', ver_nbr = ver_nbr + 1 where attr_data_id = '337'
/
/* updating TravelPerDiemMaintenanceDocument to DVPD */
update krew_doc_typ_t set doc_typ_nm = 'DVPD', ver_nbr = ver_nbr + 1 where doc_typ_id = 320588
/
/* updating CashManagementDocument to CMD */
update krew_doc_typ_t set doc_typ_nm = 'CMD', ver_nbr = ver_nbr + 1 where doc_typ_id = 320561
/
/* updating perm attr data  from attr val CashManagementDocument to CMD */
update krim_perm_attr_data_t set attr_val = 'CMD', ver_nbr = ver_nbr + 1 where attr_data_id = '93'
/
/* updating perm attr data  from attr val CashManagementDocument to CMD */
update krim_perm_attr_data_t set attr_val = 'CMD', ver_nbr = ver_nbr + 1 where attr_data_id = '252'
/
/* updating PaymentStatusMaintenanceDocument to PMTS */
update krew_doc_typ_t set doc_typ_nm = 'PMTS', ver_nbr = ver_nbr + 1 where doc_typ_id = 320606
/
/* updating IndirectCostLookupMaintenanceDocument to IDCL */
update krew_doc_typ_t set doc_typ_nm = 'IDCL', ver_nbr = ver_nbr + 1 where doc_typ_id = 320691
/
/* updating PurchaseOrderPaymentHoldDocument to POPH */
update krew_doc_typ_t set doc_typ_nm = 'POPH', ver_nbr = ver_nbr + 1 where doc_typ_id = 320759
/
/* updating IndirectCostRecoveryRateMaintenanceDocument to ICRE */
update krew_doc_typ_t set doc_typ_nm = 'ICRE', ver_nbr = ver_nbr + 1 where doc_typ_id = 320693
/
/* updating CreditCardVendorMaintenanceDocument to CCVN */
update krew_doc_typ_t set doc_typ_nm = 'CCVN', ver_nbr = ver_nbr + 1 where doc_typ_id = 320565
/
/* updating ProcurementCardDocument to PCDO */
update krew_doc_typ_t set doc_typ_nm = 'PCDO', ver_nbr = ver_nbr + 1 where doc_typ_id = 320840
/
/* updating perm attr data  from attr val ProcurementCardDocument to PCDO */
update krim_perm_attr_data_t set attr_val = 'PCDO', ver_nbr = ver_nbr + 1 where attr_data_id = '92'
/
/* updating perm attr data  from attr val ProcurementCardDocument to PCDO */
update krim_perm_attr_data_t set attr_val = 'PCDO', ver_nbr = ver_nbr + 1 where attr_data_id = '96'
/
/* updating perm attr data  from attr val ProcurementCardDocument to PCDO */
update krim_perm_attr_data_t set attr_val = 'PCDO', ver_nbr = ver_nbr + 1 where attr_data_id = '406'
/
/* updating perm attr data  from attr val ProcurementCardDocument to PCDO */
update krim_perm_attr_data_t set attr_val = 'PCDO', ver_nbr = ver_nbr + 1 where attr_data_id = '409'
/
/* updating rsp attr data  from attr val ProcurementCardDocument to PCDO */
update krim_rsp_attr_data_t set attr_val = 'PCDO', ver_nbr = ver_nbr + 1 where attr_data_id = '214'
/
/* updating rsp attr data  from attr val ProcurementCardDocument to PCDO */
update krim_rsp_attr_data_t set attr_val = 'PCDO', ver_nbr = ver_nbr + 1 where attr_data_id = '360'
/
/* updating ProjectCodeMaintenanceDocument to PROJ */
update krew_doc_typ_t set doc_typ_nm = 'PROJ', ver_nbr = ver_nbr + 1 where doc_typ_id = 320543
/
/* updating AppointmentFundingDurationMaintenanceDocument to DURA */
update krew_doc_typ_t set doc_typ_nm = 'DURA', ver_nbr = ver_nbr + 1 where doc_typ_id = 320647
/
/* updating PaymentRequestDocument to PREQ */
update krew_doc_typ_t set doc_typ_nm = 'PREQ', ver_nbr = ver_nbr + 1 where doc_typ_id = 320847
/
/* updating perm attr data  from attr val PaymentRequestDocument to PREQ */
update krim_perm_attr_data_t set attr_val = 'PREQ', ver_nbr = ver_nbr + 1 where attr_data_id = '114'
/
/* updating perm attr data  from attr val PaymentRequestDocument to PREQ */
update krim_perm_attr_data_t set attr_val = 'PREQ', ver_nbr = ver_nbr + 1 where attr_data_id = '374'
/
/* updating perm attr data  from attr val PaymentRequestDocument to PREQ */
update krim_perm_attr_data_t set attr_val = 'PREQ', ver_nbr = ver_nbr + 1 where attr_data_id = '377'
/
/* updating perm attr data  from attr val PaymentRequestDocument to PREQ */
update krim_perm_attr_data_t set attr_val = 'PREQ', ver_nbr = ver_nbr + 1 where attr_data_id = '391'
/
/* updating rsp attr data  from attr val PaymentRequestDocument to PREQ */
update krim_rsp_attr_data_t set attr_val = 'PREQ', ver_nbr = ver_nbr + 1 where attr_data_id = '250'
/
/* updating rsp attr data  from attr val PaymentRequestDocument to PREQ */
update krim_rsp_attr_data_t set attr_val = 'PREQ', ver_nbr = ver_nbr + 1 where attr_data_id = '254'
/
/* updating rsp attr data  from attr val PaymentRequestDocument to PREQ */
update krim_rsp_attr_data_t set attr_val = 'PREQ', ver_nbr = ver_nbr + 1 where attr_data_id = '258'
/
/* updating LaborJournalVoucherDocument to LLJV */
update krew_doc_typ_t set doc_typ_nm = 'LLJV', ver_nbr = ver_nbr + 1 where doc_typ_id = 320724
/
/* updating perm attr data  from attr val LaborJournalVoucherDocument to LLJV */
update krim_perm_attr_data_t set attr_val = 'LLJV', ver_nbr = ver_nbr + 1 where attr_data_id = '106'
/
/* updating DisbursementVoucherDocument to DV */
update krew_doc_typ_t set doc_typ_nm = 'DV', ver_nbr = ver_nbr + 1 where doc_typ_id = 320833
/
/* updating perm attr data  from attr val DisbursementVoucherDocument to DV */
update krim_perm_attr_data_t set attr_val = 'DV', ver_nbr = ver_nbr + 1 where attr_data_id = '274'
/
/* updating perm attr data  from attr val DisbursementVoucherDocument to DV */
update krim_perm_attr_data_t set attr_val = 'DV', ver_nbr = ver_nbr + 1 where attr_data_id = '277'
/
/* updating perm attr data  from attr val DisbursementVoucherDocument to DV */
update krim_perm_attr_data_t set attr_val = 'DV', ver_nbr = ver_nbr + 1 where attr_data_id = '280'
/
/* updating perm attr data  from attr val DisbursementVoucherDocument to DV */
update krim_perm_attr_data_t set attr_val = 'DV', ver_nbr = ver_nbr + 1 where attr_data_id = '283'
/
/* updating rsp attr data  from attr val DisbursementVoucherDocument to DV */
update krim_rsp_attr_data_t set attr_val = 'DV', ver_nbr = ver_nbr + 1 where attr_data_id = '142'
/
/* updating rsp attr data  from attr val DisbursementVoucherDocument to DV */
update krim_rsp_attr_data_t set attr_val = 'DV', ver_nbr = ver_nbr + 1 where attr_data_id = '146'
/
/* updating rsp attr data  from attr val DisbursementVoucherDocument to DV */
update krim_rsp_attr_data_t set attr_val = 'DV', ver_nbr = ver_nbr + 1 where attr_data_id = '150'
/
/* updating rsp attr data  from attr val DisbursementVoucherDocument to DV */
update krim_rsp_attr_data_t set attr_val = 'DV', ver_nbr = ver_nbr + 1 where attr_data_id = '154'
/
/* updating rsp attr data  from attr val DisbursementVoucherDocument to DV */
update krim_rsp_attr_data_t set attr_val = 'DV', ver_nbr = ver_nbr + 1 where attr_data_id = '158'
/
/* updating rsp attr data  from attr val DisbursementVoucherDocument to DV */
update krim_rsp_attr_data_t set attr_val = 'DV', ver_nbr = ver_nbr + 1 where attr_data_id = '162'
/
/* updating rsp attr data  from attr val DisbursementVoucherDocument to DV */
update krim_rsp_attr_data_t set attr_val = 'DV', ver_nbr = ver_nbr + 1 where attr_data_id = '166'
/
/* updating PreEncumbranceDocument to PE */
update krew_doc_typ_t set doc_typ_nm = 'PE', ver_nbr = ver_nbr + 1 where doc_typ_id = 320839
/
/* updating rsp attr data  from attr val PreEncumbranceDocument to PE */
update krim_rsp_attr_data_t set attr_val = 'PE', ver_nbr = ver_nbr + 1 where attr_data_id = '210'
/
/* updating ReceivingThresholdMaintenanceDocument to THLD */
update krew_doc_typ_t set doc_typ_nm = 'THLD', ver_nbr = ver_nbr + 1 where doc_typ_id = 320772
/
/* updating TravelMileageRateMaintenanceDocument to DVML */
update krew_doc_typ_t set doc_typ_nm = 'DVML', ver_nbr = ver_nbr + 1 where doc_typ_id = 320587
/
/* updating SubContractorMaintenanceDocument to SUBC */
update krew_doc_typ_t set doc_typ_nm = 'SUBC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320710
/
/* updating CustomerProfileMaintenanceDocument to CSPR */
update krew_doc_typ_t set doc_typ_nm = 'CSPR', ver_nbr = ver_nbr + 1 where doc_typ_id = 320601
/
/* updating ContractsAndGrantsRoleCodeMaintenanceDocument to PRSN */
update krew_doc_typ_t set doc_typ_nm = 'PRSN', ver_nbr = ver_nbr + 1 where doc_typ_id = 320687
/
/* updating BasicAccountingCategoryMaintenanceDocument to ACTY */
update krew_doc_typ_t set doc_typ_nm = 'ACTY', ver_nbr = ver_nbr + 1 where doc_typ_id = 320520
/
/* updating GeneralErrorCorrectionDocument to GEC */
update krew_doc_typ_t set doc_typ_nm = 'GEC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320835
/
/* updating rsp attr data  from attr val GeneralErrorCorrectionDocument to GEC */
update krim_rsp_attr_data_t set attr_val = 'GEC', ver_nbr = ver_nbr + 1 where attr_data_id = '182'
/
/* updating rsp attr data  from attr val GeneralErrorCorrectionDocument to GEC */
update krim_rsp_attr_data_t set attr_val = 'GEC', ver_nbr = ver_nbr + 1 where attr_data_id = '186'
/
/* updating IndirectCostRecoveryExclusionAccountMaintenanceDocument to ICRA */
update krew_doc_typ_t set doc_typ_nm = 'ICRA', ver_nbr = ver_nbr + 1 where doc_typ_id = 320692
/
/* updating IntendedIncumbentMaintenanceDocument to IINC */
update krew_doc_typ_t set doc_typ_nm = 'IINC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320652
/
/* updating OwnershipTypeMaintenanceDocument to PMOT */
update krew_doc_typ_t set doc_typ_nm = 'PMOT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320616
/
/* updating PurchaseOrderRetransmitDocument to PORT */
update krew_doc_typ_t set doc_typ_nm = 'PORT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320765
/
/* updating perm attr data  from attr val PurchaseOrderRetransmitDocument to PORT */
update krim_perm_attr_data_t set attr_val = 'PORT', ver_nbr = ver_nbr + 1 where attr_data_id = '119'
/
/* updating AssetFabricationMaintenanceDocument to FR */
update krew_doc_typ_t set doc_typ_nm = 'FR', ver_nbr = ver_nbr + 1 where doc_typ_id = 320662
/
/* updating rsp attr data  from attr val AssetFabricationMaintenanceDocument to FR */
update krim_rsp_attr_data_t set attr_val = 'FR', ver_nbr = ver_nbr + 1 where attr_data_id = '26'
/
/* updating rsp attr data  from attr val AssetFabricationMaintenanceDocument to FR */
update krim_rsp_attr_data_t set attr_val = 'FR', ver_nbr = ver_nbr + 1 where attr_data_id = '373'
/
/* updating CreditCardReceiptDocument to CCR */
update krew_doc_typ_t set doc_typ_nm = 'CCR', ver_nbr = ver_nbr + 1 where doc_typ_id = 320563
/
/* updating OriginationCodeMaintenanceDocument to ORIG */
update krew_doc_typ_t set doc_typ_nm = 'ORIG', ver_nbr = ver_nbr + 1 where doc_typ_id = 320542
/
/* updating RequisitionSourceMaintenanceDocument to PMSO */
update krew_doc_typ_t set doc_typ_nm = 'PMSO', ver_nbr = ver_nbr + 1 where doc_typ_id = 320776
/
/* updating AssetTransferDocument to AT */
update krew_doc_typ_t set doc_typ_nm = 'AT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320672
/
/* updating rsp attr data  from attr val AssetTransferDocument to AT */
update krim_rsp_attr_data_t set attr_val = 'AT', ver_nbr = ver_nbr + 1 where attr_data_id = '381'
/
/* updating PositionObjectGroupMaintenanceDocument to POGR */
update krew_doc_typ_t set doc_typ_nm = 'POGR', ver_nbr = ver_nbr + 1 where doc_typ_id = 320728
/
/* updating DeliveryRequiredDateReasonMaintenanceDocument to PMDR */
update krew_doc_typ_t set doc_typ_nm = 'PMDR', ver_nbr = ver_nbr + 1 where doc_typ_id = 320746
/
/* updating JournalVoucherDocument to JV */
update krew_doc_typ_t set doc_typ_nm = 'JV', ver_nbr = ver_nbr + 1 where doc_typ_id = 320574
/
/* updating perm attr data  from attr val JournalVoucherDocument to JV */
update krim_perm_attr_data_t set attr_val = 'JV', ver_nbr = ver_nbr + 1 where attr_data_id = '95'
/
/* updating NonCheckDisbursementDocument to ND */
update krew_doc_typ_t set doc_typ_nm = 'ND', ver_nbr = ver_nbr + 1 where doc_typ_id = 320838
/
/* updating rsp attr data  from attr val NonCheckDisbursementDocument to ND */
update krim_rsp_attr_data_t set attr_val = 'ND', ver_nbr = ver_nbr + 1 where attr_data_id = '202'
/
/* updating rsp attr data  from attr val NonCheckDisbursementDocument to ND */
update krim_rsp_attr_data_t set attr_val = 'ND', ver_nbr = ver_nbr + 1 where attr_data_id = '206'
/
/* updating OrganizationMaintenanceDocument to ORGN */
update krew_doc_typ_t set doc_typ_nm = 'ORGN', ver_nbr = ver_nbr + 1 where doc_typ_id = 320537
/
/* updating rsp attr data  from attr val OrganizationMaintenanceDocument to ORGN */
update krim_rsp_attr_data_t set attr_val = 'ORGN', ver_nbr = ver_nbr + 1 where attr_data_id = '98'
/
/* updating AwardStatusMaintenanceDocument to AWDS */
update krew_doc_typ_t set doc_typ_nm = 'AWDS', ver_nbr = ver_nbr + 1 where doc_typ_id = 320683
/
/* updating RecurringPaymentTypeMaintenanceDocument to PMRP */
update krew_doc_typ_t set doc_typ_nm = 'PMRP', ver_nbr = ver_nbr + 1 where doc_typ_id = 320774
/
/* updating BudgetAdjustmentDocument to BA */
update krew_doc_typ_t set doc_typ_nm = 'BA', ver_nbr = ver_nbr + 1 where doc_typ_id = 320832
/
/* updating rsp attr data  from attr val BudgetAdjustmentDocument to BA */
update krim_rsp_attr_data_t set attr_val = 'BA', ver_nbr = ver_nbr + 1 where attr_data_id = '134'
/
/* updating rsp attr data  from attr val BudgetAdjustmentDocument to BA */
update krim_rsp_attr_data_t set attr_val = 'BA', ver_nbr = ver_nbr + 1 where attr_data_id = '356'
/
/* updating BarcodeInventoryErrorDocument to BCIE */
update krew_doc_typ_t set doc_typ_nm = 'BCIE', ver_nbr = ver_nbr + 1 where doc_typ_id = 320674
/
/* updating perm attr data  from attr val BarcodeInventoryErrorDocument to BCIE */
update krim_perm_attr_data_t set attr_val = 'BCIE', ver_nbr = ver_nbr + 1 where attr_data_id = '23'
/
/* updating CFDAMaintenanceDocument to CFDA */
update krew_doc_typ_t set doc_typ_nm = 'CFDA', ver_nbr = ver_nbr + 1 where doc_typ_id = 320686
/
/* updating OrganizationReportsMaintenanceDocument to BCOR */
update krew_doc_typ_t set doc_typ_nm = 'BCOR', ver_nbr = ver_nbr + 1 where doc_typ_id = 320653
/
/* updating CreditMemoStatusMaintenanceDocument to PMCM */
update krew_doc_typ_t set doc_typ_nm = 'PMCM', ver_nbr = ver_nbr + 1 where doc_typ_id = 320745
/
/* updating CustomerTypeMaintenanceDocument to CTY */
update krew_doc_typ_t set doc_typ_nm = 'CTY', ver_nbr = ver_nbr + 1 where doc_typ_id = 320637
/
/* updating CorrectionReceivingDocument to RCVC */
update krew_doc_typ_t set doc_typ_nm = 'RCVC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320744
/
/* updating AccountGlobalMaintenanceDocument to GACC */
update krew_doc_typ_t set doc_typ_nm = 'GACC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320514
/
/* updating rsp attr data  from attr val AccountGlobalMaintenanceDocument to GACC */
update krim_rsp_attr_data_t set attr_val = 'GACC', ver_nbr = ver_nbr + 1 where attr_data_id = '74'
/
/* updating GeneralLedgerCorrectionProcessDocument to GLCP */
update krew_doc_typ_t set doc_typ_nm = 'GLCP', ver_nbr = ver_nbr + 1 where doc_typ_id = 320595
/
/* updating perm attr data  from attr val GeneralLedgerCorrectionProcessDocument to GLCP */
update krim_perm_attr_data_t set attr_val = 'GLCP', ver_nbr = ver_nbr + 1 where attr_data_id = '101'
/
/* updating ContractManagerAssignmentDocument to ACM */
update krew_doc_typ_t set doc_typ_nm = 'ACM', ver_nbr = ver_nbr + 1 where doc_typ_id = 320743
/
/* updating perm attr data  from attr val ContractManagerAssignmentDocument to ACM */
update krim_perm_attr_data_t set attr_val = 'ACM', ver_nbr = ver_nbr + 1 where attr_data_id = '414'
/
/* updating BudgetAggregationCodeMaintenanceDocument to BAMD */
update krew_doc_typ_t set doc_typ_nm = 'BAMD', ver_nbr = ver_nbr + 1 where doc_typ_id = 320521
/
/* updating PaymentTypeMaintenanceDocument to PMTP */
update krew_doc_typ_t set doc_typ_nm = 'PMTP', ver_nbr = ver_nbr + 1 where doc_typ_id = 320607
/
/* updating ElectronicInvoiceRejectDocument to EIRT */
update krew_doc_typ_t set doc_typ_nm = 'EIRT', ver_nbr = ver_nbr + 1 where doc_typ_id = 320823
/
/* updating perm attr data  from attr val ElectronicInvoiceRejectDocument to EIRT */
update krim_perm_attr_data_t set attr_val = 'EIRT', ver_nbr = ver_nbr + 1 where attr_data_id = '122'
/
/* updating perm attr data  from attr val ElectronicInvoiceRejectDocument to EIRT */
update krim_perm_attr_data_t set attr_val = 'EIRT', ver_nbr = ver_nbr + 1 where attr_data_id = '420'
/
/* updating rsp attr data  from attr val ElectronicInvoiceRejectDocument to EIRT */
update krim_rsp_attr_data_t set attr_val = 'EIRT', ver_nbr = ver_nbr + 1 where attr_data_id = '401'
/
/* updating ACHTransactionCodeMaintenanceDocument to ACTR */
update krew_doc_typ_t set doc_typ_nm = 'ACTR', ver_nbr = ver_nbr + 1 where doc_typ_id = 320599
/
/* updating SubFundGroupMaintenanceDocument to SFGR */
update krew_doc_typ_t set doc_typ_nm = 'SFGR', ver_nbr = ver_nbr + 1 where doc_typ_id = 320548
/
/* updating AppointmentFundingReasonCodeMaintenanceDocument to BCRC */
update krew_doc_typ_t set doc_typ_nm = 'BCRC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320648
/
/* updating HigherEducationFunctionMaintenanceDocument to HEFN */
update krew_doc_typ_t set doc_typ_nm = 'HEFN', ver_nbr = ver_nbr + 1 where doc_typ_id = 320527
/
/* updating ProposalMaintenanceDocument to PRPL */
update krew_doc_typ_t set doc_typ_nm = 'PRPL', ver_nbr = ver_nbr + 1 where doc_typ_id = 320702
/
/* updating perm attr data  from attr val ProposalMaintenanceDocument to PRPL */
update krim_perm_attr_data_t set attr_val = 'PRPL', ver_nbr = ver_nbr + 1 where attr_data_id = '69'
/
/* updating perm attr data  from attr val ProposalMaintenanceDocument to PRPL */
update krim_perm_attr_data_t set attr_val = 'PRPL', ver_nbr = ver_nbr + 1 where attr_data_id = '298'
/
/* updating ProposalPurposeMaintenanceDocument to PURP */
update krew_doc_typ_t set doc_typ_nm = 'PURP', ver_nbr = ver_nbr + 1 where doc_typ_id = 320703
/
/* updating VendorStipulationMaintenanceDocument to PMSI */
update krew_doc_typ_t set doc_typ_nm = 'PMSI', ver_nbr = ver_nbr + 1 where doc_typ_id = 320781
/
/* updating YearEndSalaryExpenseTransferDocument to YEST */
update krew_doc_typ_t set doc_typ_nm = 'YEST', ver_nbr = ver_nbr + 1 where doc_typ_id = 320731
/
/* updating BudgetConstructionDocument to BC */
update krew_doc_typ_t set doc_typ_nm = 'BC', ver_nbr = ver_nbr + 1 where doc_typ_id = 320649
/
/* updating perm attr data  from attr val BudgetConstructionDocument to BC */
update krim_perm_attr_data_t set attr_val = 'BC', ver_nbr = ver_nbr + 1 where attr_data_id = '387'
/
/* updating perm attr data  from attr val BudgetConstructionDocument to BC */
update krim_perm_attr_data_t set attr_val = 'BC', ver_nbr = ver_nbr + 1 where attr_data_id = '322'
/
/* updating perm attr data  from attr val BudgetConstructionDocument to BC */
update krim_perm_attr_data_t set attr_val = 'BC', ver_nbr = ver_nbr + 1 where attr_data_id = '360'
/
/* updating NonResidentAlienTaxPercentMaintenanceDocument to DVTX */
update krew_doc_typ_t set doc_typ_nm = 'DVTX', ver_nbr = ver_nbr + 1 where doc_typ_id = 320576
/
/* updating EffortCertificationDocument to ECD */
update krew_doc_typ_t set doc_typ_nm = 'ECD', ver_nbr = ver_nbr + 1 where doc_typ_id = 320843
/
/* updating perm attr data  from attr val EffortCertificationDocument to ECD */
update krim_perm_attr_data_t set attr_val = 'ECD', ver_nbr = ver_nbr + 1 where attr_data_id = '89'
/
/* updating rsp attr data  from attr val EffortCertificationDocument to ECD */
update krim_rsp_attr_data_t set attr_val = 'ECD', ver_nbr = ver_nbr + 1 where attr_data_id = '122'
/
/* updating rsp attr data  from attr val EffortCertificationDocument to ECD */
update krim_rsp_attr_data_t set attr_val = 'ECD', ver_nbr = ver_nbr + 1 where attr_data_id = '126'
/
/* updating rsp attr data  from attr val EffortCertificationDocument to ECD */
update krim_rsp_attr_data_t set attr_val = 'ECD', ver_nbr = ver_nbr + 1 where attr_data_id = '341'
/
/* updating rsp attr data  from attr val EffortCertificationDocument to ECD */
update krim_rsp_attr_data_t set attr_val = 'ECD', ver_nbr = ver_nbr + 1 where attr_data_id = '405'
/
/* updating YearEndDistributionOfIncomeAndExpenseDocument to YEDI */
update krew_doc_typ_t set doc_typ_nm = 'YEDI', ver_nbr = ver_nbr + 1 where doc_typ_id = 320591
/
/* updating perm attr data  from attr val YearEndDistributionOfIncomeAndExpenseDocument to YEDI */
update krim_perm_attr_data_t set attr_val = 'YEDI', ver_nbr = ver_nbr + 1 where attr_data_id = '91'
/
/* updating PayeeTypeMaintenanceDocument to PYTP */
update krew_doc_typ_t set doc_typ_nm = 'PYTP', ver_nbr = ver_nbr + 1 where doc_typ_id = 320604
/
/* updating MandatoryTransferEliminationCodeMaintenanceDocument to MTE */
update krew_doc_typ_t set doc_typ_nm = 'MTE', ver_nbr = ver_nbr + 1 where doc_typ_id = 320528
/
/* updating TransferOfFundsDocument to TF */
update krew_doc_typ_t set doc_typ_nm = 'TF', ver_nbr = ver_nbr + 1 where doc_typ_id = 320841
/
/* updating rsp attr data  from attr val TransferOfFundsDocument to TF */
update krim_rsp_attr_data_t set attr_val = 'TF', ver_nbr = ver_nbr + 1 where attr_data_id = '218'
/
/* updating rsp attr data  from attr val TransferOfFundsDocument to TF */
update krim_rsp_attr_data_t set attr_val = 'TF', ver_nbr = ver_nbr + 1 where attr_data_id = '222'
/
/* updating AuxiliaryVoucherDocument to AV */
update krew_doc_typ_t set doc_typ_nm = 'AV', ver_nbr = ver_nbr + 1 where doc_typ_id = 320831
/
/* updating rsp attr data  from attr val AuxiliaryVoucherDocument to AV */
update krim_rsp_attr_data_t set attr_val = 'AV', ver_nbr = ver_nbr + 1 where attr_data_id = '130'
/
/* updating BulkReceivingDocument to RCVB */
update krew_doc_typ_t set doc_typ_nm = 'RCVB', ver_nbr = ver_nbr + 1 where doc_typ_id = 320740
/
/* updating CustomerInvoiceDocument to INV */
update krew_doc_typ_t set doc_typ_nm = 'INV', ver_nbr = ver_nbr + 1 where doc_typ_id = 320633
/
/* updating perm attr data  from attr val CustomerInvoiceDocument to INV */
update krim_perm_attr_data_t set attr_val = 'INV', ver_nbr = ver_nbr + 1 where attr_data_id = '259'
/
/* updating perm attr data  from attr val CustomerInvoiceDocument to INV */
update krim_perm_attr_data_t set attr_val = 'INV', ver_nbr = ver_nbr + 1 where attr_data_id = '352'
/
/* updating perm attr data  from attr val CustomerInvoiceDocument to INV */
update krim_perm_attr_data_t set attr_val = 'INV', ver_nbr = ver_nbr + 1 where attr_data_id = '359'
/
/* updating rsp attr data  from attr val CustomerInvoiceDocument to INV */
update krim_rsp_attr_data_t set attr_val = 'INV', ver_nbr = ver_nbr + 1 where attr_data_id = '349'
/
/* updating rsp attr data  from attr val CustomerInvoiceDocument to INV */
update krim_rsp_attr_data_t set attr_val = 'INV', ver_nbr = ver_nbr + 1 where attr_data_id = '385'
/
insert into krew_doc_typ_t (DOC_TYP_ID, PARNT_ID, DOC_TYP_NM, DOC_TYP_VER_NBR, ACTV_IND, CUR_IND, LBL, VER_NBR, RTE_VER_NBR, OBJ_ID)
    values (KREW_DOC_HDR_S.NEXTVAL, 320830, 'AVAD', 0, 1, 1, 'Auxiliary Voucher - Adjustment', 1, 2, sys_guid())
/
insert into krew_doc_typ_t (DOC_TYP_ID, PARNT_ID, DOC_TYP_NM, DOC_TYP_VER_NBR, ACTV_IND, CUR_IND, LBL, VER_NBR, RTE_VER_NBR, OBJ_ID)
    values (KREW_DOC_HDR_S.NEXTVAL, 320830, 'AVAE', 0, 1, 1, 'Auxiliary Voucher - Accrual Entry', 1, 2, sys_guid())
/
insert into krew_doc_typ_t (DOC_TYP_ID, PARNT_ID, DOC_TYP_NM, DOC_TYP_VER_NBR, ACTV_IND, CUR_IND, LBL, VER_NBR, RTE_VER_NBR, OBJ_ID)
    values (KREW_DOC_HDR_S.NEXTVAL, 320830, 'AVRC', 0, 1, 1, 'Auxiliary Voucher - Re-code', 1, 2, sys_guid())
/
insert into krew_doc_typ_t (DOC_TYP_ID, PARNT_ID, DOC_TYP_NM, DOC_TYP_VER_NBR, ACTV_IND, CUR_IND, LBL, VER_NBR, RTE_VER_NBR, OBJ_ID)
    values (KREW_DOC_HDR_S.NEXTVAL, 320830, 'DVWF', 0, 1, 1, 'Disbursement Voucher - WT/FD', 1, 2, sys_guid())
/
insert into krew_doc_typ_t (DOC_TYP_ID, PARNT_ID, DOC_TYP_NM, DOC_TYP_VER_NBR, ACTV_IND, CUR_IND, LBL, VER_NBR, RTE_VER_NBR, OBJ_ID)
    values (KREW_DOC_HDR_S.NEXTVAL, 320830, 'DVCA', 0, 1, 1, 'Disbursement Voucher - Check/ACH', 1, 2, sys_guid())
/
