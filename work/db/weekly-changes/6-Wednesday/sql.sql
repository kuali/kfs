update krim_rsp_attr_data_t set attr_val = 'ImageAttachment' where attr_data_id = '245'
/
update krim_rsp_attr_data_t set attr_val = 'AccountsPayableTransactionalDocument' where attr_data_id = '246'
/

update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'N'
/

INSERT INTO KRIM_RSP_T(RSP_ID, OBJ_ID, VER_NBR, RSP_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD)
    VALUES('107', sys_guid(), 1, '1', null, null, 'Y', 'KFS-PURAP')
/ 
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('392', sys_guid(), 1, '107', '7', '16', 'Account')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('393', sys_guid(), 1, '107', '7', '13', 'PurchaseOrderAmendmentDocument')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('394', sys_guid(), 1, '107', '7', '41', 'false')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('395', sys_guid(), 1, '107', '7', '40', 'true')
/
INSERT INTO KRIM_RSP_RQRD_ATTR_T(RSP_RQRD_ATTR_ID, OBJ_ID, VER_NBR, RSP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)
    VALUES('128', sys_guid(), 1, '107', '22', 'Y')
/
INSERT INTO KRIM_RSP_RQRD_ATTR_T(RSP_RQRD_ATTR_ID, OBJ_ID, VER_NBR, RSP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)
    VALUES('129', sys_guid(), 1, '107', '23', 'Y')
/
INSERT INTO KRIM_ROLE_RSP_T(ROLE_RSP_ID, OBJ_ID, VER_NBR, ROLE_ID, RSP_ID, ACTV_IND)
    VALUES('1107', sys_guid(), 1, '41', '107', 'Y')
/
INSERT INTO KRIM_ROLE_RSP_ACTN_T(ROLE_RSP_ACTN_ID, OBJ_ID, VER_NBR, ACTN_TYP_CD, PRIORITY_NBR, ACTN_PLCY_CD, ROLE_MBR_ID, ROLE_RSP_ID, IGNORE_PREV_IND)
    VALUES('132', sys_guid(), 1, 'A', null, 'F', '*', '1107', 'Y')
/

INSERT INTO KRIM_RSP_T(RSP_ID, OBJ_ID, VER_NBR, RSP_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD)
    VALUES('108', sys_guid(), 1, '1', null, null, 'Y', 'KFS-PURAP')
/ 
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('396', sys_guid(), 1, '108', '7', '16', 'Account')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('397', sys_guid(), 1, '108', '7', '13', 'VendorCreditMemoDocument')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('398', sys_guid(), 1, '108', '7', '41', 'false')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('399', sys_guid(), 1, '108', '7', '40', 'true')
    /
INSERT INTO KRIM_RSP_RQRD_ATTR_T(RSP_RQRD_ATTR_ID, OBJ_ID, VER_NBR, RSP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)
    VALUES('130', sys_guid(), 1, '108', '22', 'Y')
/
INSERT INTO KRIM_RSP_RQRD_ATTR_T(RSP_RQRD_ATTR_ID, OBJ_ID, VER_NBR, RSP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)
    VALUES('131', sys_guid(), 1, '108', '23', 'Y')
/
INSERT INTO KRIM_ROLE_RSP_T(ROLE_RSP_ID, OBJ_ID, VER_NBR, ROLE_ID, RSP_ID, ACTV_IND)
    VALUES('1108', sys_guid(), 1, '41', '108', 'Y')
/
INSERT INTO KRIM_ROLE_RSP_ACTN_T(ROLE_RSP_ACTN_ID, OBJ_ID, VER_NBR, ACTN_TYP_CD, PRIORITY_NBR, ACTN_PLCY_CD, ROLE_MBR_ID, ROLE_RSP_ID, IGNORE_PREV_IND)
    VALUES('133', sys_guid(), 1, 'A', null, 'F', '*', '1108', 'Y')
/

--Recurrence    CustomerInvoiceDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '97')
/
--ProjectManagement    EffortCertificationDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '41')
/
--ImageAttachment    AccountsPayableTransactionalDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '71')
/
--Tax    PaymentRequestDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '74')
/
--Budget    PurchaseOrderDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '76')
/
--Award    PurchaseOrderDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '77')
/
--PrintTransmission    PurchaseOrderDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '79')
/
--Tax    PurchaseOrderDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '80')
/
--AccountsPayable    PurchaseOrderRemoveHoldDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '81')
/
--SeparationOfDuties    RequisitionDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '85')
/
--Organization    RequisitionDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '3')
/
--Initiator    VendorMaintenanceDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '91')
/
--Management    VendorMaintenanceDocument
update krim_role_rsp_actn_t set IGNORE_PREV_IND = 'Y' where ROLE_RSP_ID in (select role_rsp_id from krim_role_rsp_t where rsp_id = '92')
/

INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('291', sys_guid(), 1, '40', null, null, 'Y', 'KFS-PURAP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('420', sys_guid(), 1, '291', '3', '13', 'ElectronicInvoiceRejectDocument')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('554', sys_guid(), 1, '22', '291', 'Y')
/
ALTER TABLE KRIM_ROLE_RSP_ACTN_T
    MODIFY IGNORE_PREV_IND DEFAULT 'N'
/
delete from KREW_DOC_TYP_ATTR_T
/
delete from KREW_DOC_TYP_PLCY_RELN_T
/
delete from KREW_DOC_TYP_PROC_T
/
delete from KREW_RTE_BRCH_PROTO_T
/
delete from KREW_RTE_NODE_CFG_PARM_T
/
delete from KREW_RTE_NODE_LNK_T
/
delete from KREW_RTE_NODE_T
/
delete from KREW_DOC_TYP_T
/

-- Start Label Updates

UPDATE KREW_DOC_TYP_T SET LBL='Account Delegate Global' WHERE DOC_TYP_NM='AccountDelegateGlobalMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Account Delegate' WHERE DOC_TYP_NM='AccountDelegateMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Account Delegate Model' WHERE DOC_TYP_NM='AccountDelegateModelMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Account Global' WHERE DOC_TYP_NM='AccountGlobalMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Accounting Period' WHERE DOC_TYP_NM='AccountingPeriodMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Account' WHERE DOC_TYP_NM='AccountMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Account Type' WHERE DOC_TYP_NM='AccountTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='AICPA Function' WHERE DOC_TYP_NM='AICPAFunctionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Balance Type' WHERE DOC_TYP_NM='BalanceTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Basic Accounting Category' WHERE DOC_TYP_NM='BasicAccountingCategoryMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Budget Aggregation Code' WHERE DOC_TYP_NM='BudgetAggregationCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Budget Recording Level Maintenance Document' WHERE DOC_TYP_NM='BudgetRecordingLevelMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Chart' WHERE DOC_TYP_NM='ChartMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Federal Function' WHERE DOC_TYP_NM='FederalFunctionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Federal Funded Code' WHERE DOC_TYP_NM='FederalFundedCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Fund Group' WHERE DOC_TYP_NM='FundGroupMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Higher Education Function' WHERE DOC_TYP_NM='HigherEducationFunctionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Indirect Cost Recovery Exclusion By Account' WHERE DOC_TYP_NM='IndirectCostRecoveryExclusionAccountMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Indirect Cost Recovery Rate' WHERE DOC_TYP_NM='IndirectCostRecoveryRateMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Indirect Cost Recovery Type' WHERE DOC_TYP_NM='IndirectCostRecoveryTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Mandatory Transfer Elimination' WHERE DOC_TYP_NM='MandatoryTransferEliminationCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Object Code Global' WHERE DOC_TYP_NM='ObjectCodeGlobalMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Object Code' WHERE DOC_TYP_NM='ObjectCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Object Consolidation' WHERE DOC_TYP_NM='ObjectConsolidationMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Object Level' WHERE DOC_TYP_NM='ObjectLevelMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Object Sub-Type' WHERE DOC_TYP_NM='ObjectSubTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Object Type' WHERE DOC_TYP_NM='ObjectTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Offset Definition' WHERE DOC_TYP_NM='OffsetDefinitionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Organization' WHERE DOC_TYP_NM='OrganizationMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Organization Reversion Category' WHERE DOC_TYP_NM='OrganizationReversionCategoryMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Organization Reversion Global' WHERE DOC_TYP_NM='OrganizationReversionGlobalMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Organization Reversion' WHERE DOC_TYP_NM='OrganizationReversionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Organization Type' WHERE DOC_TYP_NM='OrganizationTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Project Code' WHERE DOC_TYP_NM='ProjectCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Reporting Code' WHERE DOC_TYP_NM='ReportingCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Responsibility Center' WHERE DOC_TYP_NM='ResponsibilityCenterMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Restricted Status' WHERE DOC_TYP_NM='RestrictedStatusMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Sub-Account' WHERE DOC_TYP_NM='SubAccountMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Sub-Fund Group' WHERE DOC_TYP_NM='SubFundGroupMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Sub-Fund Group Type' WHERE DOC_TYP_NM='SubFundGroupTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Sub-Object Code Global' WHERE DOC_TYP_NM='SubObjectCodeGlobalMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Sub-Object Code' WHERE DOC_TYP_NM='SubObjectCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Sufficient Funds Code Maintenance Document' WHERE DOC_TYP_NM='SufficientFundsCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='University Budget Office Function' WHERE DOC_TYP_NM='UniversityBudgetOfficeFunctionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Advance Deposit' WHERE DOC_TYP_NM='AdvanceDepositDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Auxiliary Voucher' WHERE DOC_TYP_NM='AuxiliaryVoucherDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Budget Adjustment' WHERE DOC_TYP_NM='BudgetAdjustmentDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Cash Drawer' WHERE DOC_TYP_NM='CashDrawerMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Cash Management' WHERE DOC_TYP_NM='CashManagementDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Cash Receipt' WHERE DOC_TYP_NM='CashReceiptDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Credit Card Receipt' WHERE DOC_TYP_NM='CreditCardReceiptDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Credit Card Type' WHERE DOC_TYP_NM='CreditCardTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Credit Card Vendor' WHERE DOC_TYP_NM='CreditCardVendorMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Disbursement Voucher' WHERE DOC_TYP_NM='DisbursementVoucherDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Disbursement Voucher Documentation Location' WHERE DOC_TYP_NM='DisbursementVoucherDocumentationLocationMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Distribution Of Income And Expense' WHERE DOC_TYP_NM='DistributionOfIncomeAndExpenseDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Fiscal Year Function Control' WHERE DOC_TYP_NM='FiscalYearFunctionControlMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Function Control Code' WHERE DOC_TYP_NM='FunctionControlCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='General Error Correction' WHERE DOC_TYP_NM='GeneralErrorCorrectionDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Indirect Cost Adjustment' WHERE DOC_TYP_NM='IndirectCostAdjustmentDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Internal Billing' WHERE DOC_TYP_NM='InternalBillingDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Journal Voucher' WHERE DOC_TYP_NM='JournalVoucherDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Message Of The Day' WHERE DOC_TYP_NM='MessageOfTheDayMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Non-Check Disbursement' WHERE DOC_TYP_NM='NonCheckDisbursementDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Non-Resident Alien Tax Percent' WHERE DOC_TYP_NM='NonResidentAlienTaxPercentMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Offset Account' WHERE DOC_TYP_NM='OffsetAccountMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Disbursement Voucher Ownership Type' WHERE DOC_TYP_NM='OwnershipTypeCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Disbursement Voucher Payment Reason' WHERE DOC_TYP_NM='PaymentReasonCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Pre-Encumbrance' WHERE DOC_TYP_NM='PreEncumbranceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Procurement Card' WHERE DOC_TYP_NM='ProcurementCardDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Service Billing' WHERE DOC_TYP_NM='ServiceBillingDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Disbursement Voucher Tax Control' WHERE DOC_TYP_NM='TaxControlCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Disbursement Voucher Tax Income Class' WHERE DOC_TYP_NM='TaxIncomeClassCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Transfer Of Funds' WHERE DOC_TYP_NM='TransferOfFundsDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Disbursement Voucher Travel Company' WHERE DOC_TYP_NM='TravelCompanyCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Disbursement Voucher Travel Expense Type' WHERE DOC_TYP_NM='TravelExpenseTypeCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Disbursement Voucher Travel Mileage Rate' WHERE DOC_TYP_NM='TravelMileageRateMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Disbursement Voucher Travel Per Diem' WHERE DOC_TYP_NM='TravelPerDiemMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Disbursement Voucher Wire Charge' WHERE DOC_TYP_NM='WireChargeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Year End Budget Adjustment' WHERE DOC_TYP_NM='YearEndBudgetAdjustmentDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Year End Distribution Of Income And Expense' WHERE DOC_TYP_NM='YearEndDistributionOfIncomeAndExpenseDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Year End General Error Correction' WHERE DOC_TYP_NM='YearEndGeneralErrorCorrectionDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Year End Transfer Of Funds' WHERE DOC_TYP_NM='YearEndTransferOfFundsDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='General Ledger Correction Process' WHERE DOC_TYP_NM='GeneralLedgerCorrectionProcessDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='University Date' WHERE DOC_TYP_NM='UniversityDateMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Cash Control' WHERE DOC_TYP_NM='CashControlDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Customer Address Type' WHERE DOC_TYP_NM='CustomerAddressTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Customer Credit Memo' WHERE DOC_TYP_NM='CustomerCreditMemoDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Customer Invoice' WHERE DOC_TYP_NM='CustomerInvoiceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Customer Invoice Item Code' WHERE DOC_TYP_NM='CustomerInvoiceItemCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Customer Invoice Writeoff' WHERE DOC_TYP_NM='CustomerInvoiceWriteoffDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Customer' WHERE DOC_TYP_NM='CustomerMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Customer Type' WHERE DOC_TYP_NM='CustomerTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Invoice Recurrence' WHERE DOC_TYP_NM='InvoiceRecurrenceMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Organization Accounting Default' WHERE DOC_TYP_NM='OrganizationAccountingDefaultMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Organization Options' WHERE DOC_TYP_NM='OrganizationOptionsMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Application' WHERE DOC_TYP_NM='PaymentApplicationDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Payment Medium' WHERE DOC_TYP_NM='PaymentMediumMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='System Information' WHERE DOC_TYP_NM='SystemInformationMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Account Reports' WHERE DOC_TYP_NM='AccountReportsMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Appointment Funding Reason Code' WHERE DOC_TYP_NM='AppointmentFundingReasonCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Budget Construction Document' WHERE DOC_TYP_NM='BudgetConstructionDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Appointment Funding Duration' WHERE DOC_TYP_NM='AppointmentFundingDurationMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Intended Incumbent' WHERE DOC_TYP_NM='IntendedIncumbentMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Organization Reports' WHERE DOC_TYP_NM='OrganizationReportsMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Budget Construction Position' WHERE DOC_TYP_NM='BudgetConstructionPositionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Calculated Salary Foundation Tracker Override' WHERE DOC_TYP_NM='CalculatedSalaryFoundationTrackerOverrideMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Asset Transaction Type' WHERE DOC_TYP_NM='AssetTransactionTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Pre-Asset Tagging' WHERE DOC_TYP_NM='PretagMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Asset Acquisition Type' WHERE DOC_TYP_NM='AssetAcquisitionTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Asset Condition' WHERE DOC_TYP_NM='AssetConditionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Asset Depreciation Convention' WHERE DOC_TYP_NM='AssetDepreciationConventionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Asset Depreciation' WHERE DOC_TYP_NM='AssetDepreciationDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Asset Depreciation Method' WHERE DOC_TYP_NM='AssetDepreciationMethodMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Asset Global' WHERE DOC_TYP_NM='AssetGlobalMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Asset Location Global' WHERE DOC_TYP_NM='AssetLocationGlobalMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Asset Location Type' WHERE DOC_TYP_NM='AssetLocationTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Asset' WHERE DOC_TYP_NM='AssetMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Asset Object Code' WHERE DOC_TYP_NM='AssetObjectCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Asset Payments' WHERE DOC_TYP_NM='AssetPaymentDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Asset Retirement Global' WHERE DOC_TYP_NM='AssetRetirementGlobalMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Asset Retirement Reason' WHERE DOC_TYP_NM='AssetRetirementReasonMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Asset Status' WHERE DOC_TYP_NM='AssetStatusMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Asset Transfer' WHERE DOC_TYP_NM='AssetTransferDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Asset Type' WHERE DOC_TYP_NM='AssetTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Barcode Inventory Error' WHERE DOC_TYP_NM='BarcodeInventoryErrorDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Equipment Loan/Return' WHERE DOC_TYP_NM='EquipmentLoanOrReturnDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Agency' WHERE DOC_TYP_NM='AgencyMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Agency Type' WHERE DOC_TYP_NM='AgencyTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Appointment Type' WHERE DOC_TYP_NM='AppointmentTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Award' WHERE DOC_TYP_NM='AwardMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Award Status' WHERE DOC_TYP_NM='AwardStatusMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Budget' WHERE DOC_TYP_NM='BudgetDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='CFDA Close' WHERE DOC_TYP_NM='CFDACloseDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='CFDA' WHERE DOC_TYP_NM='CFDAMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Contracts And Grants Role Code' WHERE DOC_TYP_NM='ContractsAndGrantsRoleCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Due Date Type' WHERE DOC_TYP_NM='DueDateTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Graduate Assistant Rate' WHERE DOC_TYP_NM='GraduateAssistantRateMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Grant Description' WHERE DOC_TYP_NM='GrantDescriptionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Indirect Cost Lookup Maintenance Document' WHERE DOC_TYP_NM='IndirectCostLookupMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Keyword' WHERE DOC_TYP_NM='KeywordMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Letter Of Credit Fund Group' WHERE DOC_TYP_NM='LetterOfCreditFundGroupMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Non-Personnel Category' WHERE DOC_TYP_NM='NonPersonnelCategoryMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Non-Personnel Object Code' WHERE DOC_TYP_NM='NonPersonnelObjectCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Non-Personnel Sub-Category' WHERE DOC_TYP_NM='NonPersonnelSubCategoryMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Project Type' WHERE DOC_TYP_NM='ProjectTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Proposal Type' WHERE DOC_TYP_NM='ProposalAwardTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Proposal' WHERE DOC_TYP_NM='ProposalMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Proposal Purpose' WHERE DOC_TYP_NM='ProposalPurposeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Proposal Status' WHERE DOC_TYP_NM='ProposalStatusMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Purpose' WHERE DOC_TYP_NM='PurposeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Question Type' WHERE DOC_TYP_NM='QuestionTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Research Risk Type' WHERE DOC_TYP_NM='ResearchRiskTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Research Type' WHERE DOC_TYP_NM='ResearchTypeCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Routing Form' WHERE DOC_TYP_NM='RoutingFormDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Sub-Contractor' WHERE DOC_TYP_NM='SubContractorMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Effort Certification' WHERE DOC_TYP_NM='EffortCertificationDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Report Period Status Code' WHERE DOC_TYP_NM='EffortCertificationPeriodStatusCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Report Definition' WHERE DOC_TYP_NM='EffortCertificationReportDefinitionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Report Earn Paygroup' WHERE DOC_TYP_NM='EffortCertificationReportEarnPaygroupMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Report Type' WHERE DOC_TYP_NM='EffortCertificationReportTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Benefit Expense Transfer' WHERE DOC_TYP_NM='BenefitExpenseTransferDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Labor Benefits Calculation' WHERE DOC_TYP_NM='BenefitsCalculationMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Labor Benefits Type' WHERE DOC_TYP_NM='BenefitsTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Labor Journal Voucher' WHERE DOC_TYP_NM='LaborJournalVoucherDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Labor Ledger Correction Process' WHERE DOC_TYP_NM='LaborLedgerCorrectionProcessDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Labor Object Code' WHERE DOC_TYP_NM='LaborObjectMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Labor Object Code Benefits Maintenance Document' WHERE DOC_TYP_NM='PositionObjectBenefitMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Labor Position Object Group Code Maintenance Document' WHERE DOC_TYP_NM='PositionObjectGroupMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Salary Expense Transfer' WHERE DOC_TYP_NM='SalaryExpenseTransferDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Year End Benefit Expense Transfer' WHERE DOC_TYP_NM='YearEndBenefitExpenseTransferDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Year End Salary Expense Transfer' WHERE DOC_TYP_NM='YearEndSalaryExpenseTransferDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Payment Request Auto Approve Exclusions' WHERE DOC_TYP_NM='AutoApproveExcludeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Billing Address' WHERE DOC_TYP_NM='BillingAddressMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Bulk Receiving' WHERE DOC_TYP_NM='BulkReceivingDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Capital Asset System Type' WHERE DOC_TYP_NM='CapitalAssetSystemTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Carrier' WHERE DOC_TYP_NM='CarrierMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Contract Manager Assignment' WHERE DOC_TYP_NM='ContractManagerAssignmentDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Correction Receiving' WHERE DOC_TYP_NM='CorrectionReceivingDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Credit Memo Status' WHERE DOC_TYP_NM='CreditMemoStatusMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Delivery Required Date Reason' WHERE DOC_TYP_NM='DeliveryRequiredDateReasonMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Electronic Invoice Reject Document' WHERE DOC_TYP_NM='ElectronicInvoiceRejectDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Funding Source' WHERE DOC_TYP_NM='FundingSourceMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Item Reason Added' WHERE DOC_TYP_NM='ItemReasonAddedMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Item Type' WHERE DOC_TYP_NM='ItemTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Line Item Receiving' WHERE DOC_TYP_NM='LineItemReceivingDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Negative Payment Request Approval Limit' WHERE DOC_TYP_NM='NegativePaymentRequestApprovalLimitMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Organization Parameter' WHERE DOC_TYP_NM='OrganizationParameterMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Payment Request' WHERE DOC_TYP_NM='PaymentRequestDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Payment Request Status' WHERE DOC_TYP_NM='PaymentRequestStatusMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Purchase Order Amendment' WHERE DOC_TYP_NM='PurchaseOrderAmendmentDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Purchase Order Close' WHERE DOC_TYP_NM='PurchaseOrderCloseDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Purchase Order Contract Language' WHERE DOC_TYP_NM='PurchaseOrderContractLanguageMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Purchase Order' WHERE DOC_TYP_NM='PurchaseOrderDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Purchase Order Payment Hold' WHERE DOC_TYP_NM='PurchaseOrderPaymentHoldDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Purchase Order Quote Language' WHERE DOC_TYP_NM='PurchaseOrderQuoteLanguageMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Purchase Order Quote List' WHERE DOC_TYP_NM='PurchaseOrderQuoteListMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Purchase Order Quote Status' WHERE DOC_TYP_NM='PurchaseOrderQuoteStatusMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Purchase Order Remove Payment Hold' WHERE DOC_TYP_NM='PurchaseOrderRemoveHoldDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Purchase Order Reopen' WHERE DOC_TYP_NM='PurchaseOrderReopenDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Purchase Order Retransmit' WHERE DOC_TYP_NM='PurchaseOrderRetransmitDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Purchase Order Split' WHERE DOC_TYP_NM='PurchaseOrderSplitDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Purchase Order Status' WHERE DOC_TYP_NM='PurchaseOrderStatusMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Method of PO Transmission' WHERE DOC_TYP_NM='PurchaseOrderTransmissionMethodMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Purchase Order Vendor Choice' WHERE DOC_TYP_NM='PurchaseOrderVendorChoiceMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Purchase Order Void' WHERE DOC_TYP_NM='PurchaseOrderVoidDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Receiving Address' WHERE DOC_TYP_NM='ReceivingAddressMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Receiving Threshold' WHERE DOC_TYP_NM='ReceivingThresholdMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Recurring Payment Frequency' WHERE DOC_TYP_NM='RecurringPaymentFrequencyMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Recurring Payment Type' WHERE DOC_TYP_NM='RecurringPaymentTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Requisition' WHERE DOC_TYP_NM='RequisitionDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Requisition Source' WHERE DOC_TYP_NM='RequisitionSourceMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Requisition Status' WHERE DOC_TYP_NM='RequisitionStatusMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Sensitive Data' WHERE DOC_TYP_NM='SensitiveDataMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Vendor Credit Memo' WHERE DOC_TYP_NM='VendorCreditMemoDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Vendor Stipulation' WHERE DOC_TYP_NM='VendorStipulationMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Accounting Change Code' WHERE DOC_TYP_NM='AccountingChangeCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='ACH Bank' WHERE DOC_TYP_NM='ACHBankMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='ACH Transaction Code' WHERE DOC_TYP_NM='ACHTransactionCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='ACH Transaction Type' WHERE DOC_TYP_NM='ACHTransactionTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Customer Profile' WHERE DOC_TYP_NM='CustomerProfileMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Disbursement Number Range' WHERE DOC_TYP_NM='DisbursementNumberRangeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Disbursement Type' WHERE DOC_TYP_NM='DisbursementTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Payee ACH Account Maintenance Document' WHERE DOC_TYP_NM='PayeeACHAccountMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Payee Type' WHERE DOC_TYP_NM='PayeeTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Payment Change Code' WHERE DOC_TYP_NM='PaymentChangeCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Payment Status' WHERE DOC_TYP_NM='PaymentStatusMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Payment Type' WHERE DOC_TYP_NM='PaymentTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Bank' WHERE DOC_TYP_NM='BankMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Building' WHERE DOC_TYP_NM='BuildingMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Functional Field Description' WHERE DOC_TYP_NM='FunctionalFieldDescriptionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='General Ledger Input Type' WHERE DOC_TYP_NM='GeneralLedgerInputTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Origination Code' WHERE DOC_TYP_NM='OriginationCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Parameter Component' WHERE DOC_TYP_NM='ParameterDetailTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Parameter' WHERE DOC_TYP_NM='ParameterMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Parameter Module' WHERE DOC_TYP_NM='ParameterNamespaceMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Parameter Type' WHERE DOC_TYP_NM='ParameterTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Room' WHERE DOC_TYP_NM='RoomMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='System Options' WHERE DOC_TYP_NM='SystemOptionsMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Tax Region' WHERE DOC_TYP_NM='TaxRegionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Tax Region Type' WHERE DOC_TYP_NM='TaxRegionTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Unit Of Measure' WHERE DOC_TYP_NM='UnitOfMeasureMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Address Type' WHERE DOC_TYP_NM='AddressTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Campus Parameter' WHERE DOC_TYP_NM='CampusParameterMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Commodity Code' WHERE DOC_TYP_NM='CommodityCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Contact Type' WHERE DOC_TYP_NM='ContactTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Contract Manager' WHERE DOC_TYP_NM='ContractManagerMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Ownership Type Category' WHERE DOC_TYP_NM='OwnershipCategoryMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Ownership Type' WHERE DOC_TYP_NM='OwnershipTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Payment Terms Type' WHERE DOC_TYP_NM='PaymentTermTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Phone Type' WHERE DOC_TYP_NM='PhoneTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Cost Source' WHERE DOC_TYP_NM='PurchaseOrderCostSourceMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Shipping Payment Terms' WHERE DOC_TYP_NM='ShippingPaymentTermsMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Shipping Special Conditions' WHERE DOC_TYP_NM='ShippingSpecialConditionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Shipping Title' WHERE DOC_TYP_NM='ShippingTitleMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Supplier Diversity' WHERE DOC_TYP_NM='SupplierDiversityMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Vendor Inactive Reason' WHERE DOC_TYP_NM='VendorInactiveReasonMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Vendor' WHERE DOC_TYP_NM='VendorMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET LBL='Vendor Type' WHERE DOC_TYP_NM='VendorTypeMaintenanceDocument'
/





-- Start Help URL Updates

UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='DELEGATE_GLOBAL' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='AccountDelegateGlobalMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='ACCOUNT_DELEGATE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='AccountDelegateMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='DELEGATE_MODEL' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='AccountDelegateModelMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='ACCOUNT_GLOBAL' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='AccountGlobalMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='ACCOUNTING_PERIOD' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='AccountingPeriodMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='ACCOUNT' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='AccountMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='ACCOUNT_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='AccountTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='AICPA_FUNCTION' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='AICPAFunctionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='BALANCE_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='BalanceTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='BASIC_ACCOUNTING_CATEGORY' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='BasicAccountingCategoryMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='BUDGET_AGGREGATION_CODE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='BudgetAggregationCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='BUDGET_RECORDING_LEVEL' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='BudgetRecordingLevelMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='CHART' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ChartMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='FEDERAL_FUNCTION' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='FederalFunctionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='FEDERAL_FUNDED_CODE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='FederalFundedCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='FUND_GROUP' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='FundGroupMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='HIGHER_EDUCATION_FUNCTION' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='HigherEducationFunctionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='INDIRECT_COST_RECOVERY_EXCLUSION_BY_ACCOUNT' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='IndirectCostRecoveryExclusionAccountMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='INDIRECT_COST_RECOVERY_RATE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='IndirectCostRecoveryRateMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='INDIRECT_COST_RECOVERY_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='IndirectCostRecoveryTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='MANDATORY_TRANSFER_ELIMINATION' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='MandatoryTransferEliminationCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='OBJECT_CODE_GLOBAL' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ObjectCodeGlobalMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='OBJECT_CODE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ObjectCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='OBJECT_CONSOLIDATION' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ObjectConsolidationMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='OBJECT_LEVEL' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ObjectLevelMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='OBJECT_SUB_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ObjectSubTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='OBJECT_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ObjectTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='OFFSET_DEFINITION' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='OffsetDefinitionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='ORGANIZATION' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='OrganizationMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='ORGANIZATION_REVERSION_CATEGORY' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='OrganizationReversionCategoryMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='ORGANIZATION_REVERSION_GLOBAL' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='OrganizationReversionGlobalMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='ORGANIZATION_REVERSION' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='OrganizationReversionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='ORGANIZATION_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='OrganizationTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PROJECT' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ProjectCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='FINANCIAL_REPORTING_CODE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ReportingCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='RESPONSIBILITY_CENTER' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ResponsibilityCenterMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='RESTRICTED_STATUS' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='RestrictedStatusMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='SUB_ACCOUNT' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='SubAccountMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='SUB_FUND_GROUP' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='SubFundGroupMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='SUB_FUND_GROUP_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='SubFundGroupTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='SUB_OBJECT_CODE_GLOBAL' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='SubObjectCodeGlobalMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='SUB_OBJECT_CODE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='SubObjectCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='SUFFICIENT_FUNDS_CODE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='SufficientFundsCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='UNIVERSITY_BUDGET_OFFICE_FUNCTION' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='UniversityBudgetOfficeFunctionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='ADVANCE_DEPOSIT' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='AdvanceDepositDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='AUXILIARY_VOUCHER' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='AuxiliaryVoucherDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='BUDGET_ADJUSTMENT' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='BudgetAdjustmentDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='CASH_MANAGEMENT' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='CashManagementDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='CASH_RECEIPT' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='CashReceiptDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='CREDIT_CARD_RECEIPT' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='CreditCardReceiptDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='CREDIT_CARD_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='CreditCardTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='CREDIT_CARD_VENDOR' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='CreditCardVendorMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='DISBURSEMENT_VOUCHER' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='DisbursementVoucherDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='DOCUMENTATION_LOCATION' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='DisbursementVoucherDocumentationLocationMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='DISTRIBUTION_OF_INCOME_AND_EXPENSE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='DistributionOfIncomeAndExpenseDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='FUNCTION_CONTROL_CODE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='FiscalYearFunctionControlMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='FUNCTION_CONTROL_CODE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='FunctionControlCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='GENERAL_ERROR_CORRECTION' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='GeneralErrorCorrectionDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='INDIRECT_COST_ADJUSTMENT' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='IndirectCostAdjustmentDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='INTERNAL_BILLING' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='InternalBillingDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='JOURNAL_VOUCHER' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='JournalVoucherDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='MESSAGE_OF_THE_DAY' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='MessageOfTheDayMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='NON_CHECK_DISBURSEMENT' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='NonCheckDisbursementDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='NON_RESIDENT_ALIEN_TAX_PERCENT' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='NonResidentAlienTaxPercentMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='OFFSET_ACCOUNT' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='OffsetAccountMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='OWNERSHIP_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='OwnershipTypeCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PAYMENT_REASON' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PaymentReasonCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PRE_ENCUMBRANCE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PreEncumbranceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='SERVICE_BILLING' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ServiceBillingDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='TAX_CONTROL' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='TaxControlCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='TAX_INCOME_CLASS_CODE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='TaxIncomeClassCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='TRANSFER_OF_FUNDS' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='TransferOfFundsDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='TRAVEL_COMPANY' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='TravelCompanyCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='TRAVEL_EXPENSE_TYPE_CODE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='TravelExpenseTypeCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='TRAVEL_MILEAGE_RATE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='TravelMileageRateMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='TRAVEL_PER_DIEM' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='TravelPerDiemMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='WIRE_CHARGE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='WireChargeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='YEAR_END_BUDGET_ADJUSTMENT' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='YearEndBudgetAdjustmentDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='YEAR_END_DISTRIBUTION_OF_INCOME_AND_EXPENSE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='YearEndDistributionOfIncomeAndExpenseDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='YEAR_END_GENERAL_ERROR_CORRECTION' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='YearEndGeneralErrorCorrectionDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='YEAR_END_TRANSFER_OF_FUNDS' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='YearEndTransferOfFundsDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='GENERAL_LEDGER_CORRECTION_PROCESS' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='GeneralLedgerCorrectionProcessDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='UNIVERSITY_DATE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='UniversityDateMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='CRM' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='CustomerCreditMemoDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='CUSTOMER' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='CustomerMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='ASSET_TRANSACTION_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='AssetTransactionTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='AGENCY' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='AgencyMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='AGENCY_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='AgencyTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='APPOINTMENT_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='AppointmentTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='AWARD' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='AwardMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='AWARD_STATUS' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='AwardStatusMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='BUDGET' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='BudgetDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='CFDACLOSE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='CFDACloseDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='CATALOG_OF_FEDERAL_DOMESTIC_ASSISTANCE_REFERENCE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='CFDAMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='CONTRACTS_AND_GRANTS_ROLE_CODE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ContractsAndGrantsRoleCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='DUE_DATE_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='DueDateTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='GRADUATE_ASSISTANT_RATE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='GraduateAssistantRateMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='GRANT_DESCRIPTION' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='GrantDescriptionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='INDIRECT_COST_LOOKUP' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='IndirectCostLookupMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='KEYWORD' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='KeywordMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='LETTER_OF_CREDIT_FUND_GROUP' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='LetterOfCreditFundGroupMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='NONPERSONNEL_CATEGORY' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='NonPersonnelCategoryMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='NONPERSONNEL_OBJECT_CODE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='NonPersonnelObjectCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='NONPERSONNEL_SUB_CATEGORY' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='NonPersonnelSubCategoryMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PROJECT_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ProjectTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PROPOSAL_AWARD_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ProposalAwardTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PROPOSAL' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ProposalMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PROPOSAL_PURPOSE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ProposalPurposeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PROPOSAL_STATUS' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ProposalStatusMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PURPOSE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PurposeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='QUESTION_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='QuestionTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='RESEARCH_RISK_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ResearchRiskTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='RESEARCH_TYPE_CODE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ResearchTypeCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='ROUTING_FORM' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='RoutingFormDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='SUBCONTRACTOR' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='SubContractorMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='LABOR_BENEFITS_CALCULATION' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='BenefitsCalculationMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='LABOR_BENEFITS_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='BenefitsTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='JOURNAL_VOUCHER' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='LaborJournalVoucherDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='GENERAL_LEDGER_CORRECTION_PROCESS' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='LaborLedgerCorrectionProcessDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='LABOR_OBJECT_BENEFITS' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='LaborObjectMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='AUTO_APPROVE_EXCLUDE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='AutoApproveExcludeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='BILLING_ADDRESS' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='BillingAddressMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='BULK_RECEIVING' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='BulkReceivingDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='CAPITAL_ASSET_SYSTEM_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='CapitalAssetSystemTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='CARRIER' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='CarrierMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='CONTRACT_MANAGER_ASSIGNMENT' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ContractManagerAssignmentDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='RECEIVING_CORRECTION' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='CorrectionReceivingDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='CREDIT_MEMO_STATUS' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='CreditMemoStatusMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='DELIVERY_REQUIRED_DATE_REASON' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='DeliveryRequiredDateReasonMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='ELECTRONIC_INVOICE_REJECT' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ElectronicInvoiceRejectDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='FUNDING_SOURCE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='FundingSourceMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='ITEM_REASON_ADDED' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ItemReasonAddedMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='ITEM_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ItemTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='RECEIVING_LINE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='LineItemReceivingDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='NEGATIVE_PAYMENT_REQUEST_APPROVAL_LIMIT' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='NegativePaymentRequestApprovalLimitMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='ORGANIZATION_PARAMETER' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='OrganizationParameterMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PAYMENT_REQUEST' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PaymentRequestDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PAYMENT_REQUEST_STATUS' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PaymentRequestStatusMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PURCHASE_ORDER' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PurchaseOrderAmendmentDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PURCHASE_ORDER' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PurchaseOrderCloseDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PURCHASE_ORDER_CONTRACT_LANGUAGE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PurchaseOrderContractLanguageMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PURCHASE_ORDER' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PurchaseOrderDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PURCHASE_ORDER' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PurchaseOrderPaymentHoldDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PURCHASE_ORDER_QUOTE_LANGUAGE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PurchaseOrderQuoteLanguageMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PURCHASE_ORDER_QUOTE_LIST' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PurchaseOrderQuoteListMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PURCHASE_ORDER_QUOTE_STATUS' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PurchaseOrderQuoteStatusMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PURCHASE_ORDER' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PurchaseOrderRemoveHoldDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PURCHASE_ORDER' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PurchaseOrderReopenDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PURCHASE_ORDER' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PurchaseOrderRetransmitDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PURCHASE_ORDER' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PurchaseOrderSplitDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PURCHASE_ORDER_STATUS' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PurchaseOrderStatusMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PURCHASE_ORDER_TRANSMISSION_METHOD' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PurchaseOrderTransmissionMethodMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PURCHASE_ORDER_VENDOR_CHOICE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PurchaseOrderVendorChoiceMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PURCHASE_ORDER' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PurchaseOrderVoidDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='RECEIVING_ADDRESS' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ReceivingAddressMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='RECEIVING_THRESHOLD' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ReceivingThresholdMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='RECURRING_PAYMENT_FREQUENCY' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='RecurringPaymentFrequencyMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='RECURRING_PAYMENT_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='RecurringPaymentTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='REQUISITION' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='RequisitionDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='REQUISITION_SOURCE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='RequisitionSourceMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='REQUISITION_STATUS' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='RequisitionStatusMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='SENSITIVE_DATA' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='SensitiveDataMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='CREDIT_MEMO' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='VendorCreditMemoDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='VENDOR_STIPULATION' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='VendorStipulationMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='BANK' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ACHBankMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='BUILDING' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='BuildingMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='FUNCTIONAL_FIELD_DESCRIPTION' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='FunctionalFieldDescriptionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='ORIGINATION' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='OriginationCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='OPTIONS' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='SystemOptionsMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='TAX_REGION' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='TaxRegionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='UNIT_OF_MEASURE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='UnitOfMeasureMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='ADDRESS_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='AddressTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='CAMPUS_PARAMETER' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='CampusParameterMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='COMMODITY_CODE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='CommodityCodeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='CONTACT_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ContactTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='CONTRACT_MANAGER' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ContractManagerMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='OWNERSHIP_CATEGORY' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='OwnershipCategoryMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='OWNERSHIP_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='OwnershipTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PAYMENT_TERM_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PaymentTermTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PHONE_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PhoneTypeMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='PURCHASE_ORDER_COST_SOURCE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='PurchaseOrderCostSourceMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='SHIPPING_PAYMENT_TERMS' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ShippingPaymentTermsMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='SHIPPING_SPECIAL_CONDITION' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ShippingSpecialConditionMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='SHIPPING_TITLE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='ShippingTitleMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='SUPPLIER_DIVERSITY' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='SupplierDiversityMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='VENDOR_INACTIVE_REASON' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='VendorInactiveReasonMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='VENDOR' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='VendorMaintenanceDocument'
/
UPDATE KREW_DOC_TYP_T SET HELP_DEF_URL=(SELECT TXT FROM KRNS_PARM_T WHERE PARM_NM='VENDOR_TYPE' AND PARM_TYP_CD='HELP') WHERE DOC_TYP_NM='VendorTypeMaintenanceDocument'
/

update krew_doc_typ_t set lbl = doc_typ_nm where lbl = 'Undefined'
/