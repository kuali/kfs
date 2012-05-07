/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.cab;


public class CabPropertyConstants {
    public static class GeneralLedgerEntry {
        public static final String TRANSACTION_LEDGER_ENTRY_SEQUENCE_NUMBER = "transactionLedgerEntrySequenceNumber";
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static final String FINANCIAL_SYSTEM_ORIGINATION_CODE = "financialSystemOriginationCode";
        public static final String FINANCIAL_DOCUMENT_TYPE_CODE = "financialDocumentTypeCode";
        public static final String UNIVERSITY_FISCAL_PERIOD_CODE = "universityFiscalPeriodCode";
        public static final String FINANCIAL_OBJECT_TYPE_CODE = "financialObjectTypeCode";
        public static final String FINANCIAL_OBJECT = "financialObject";
        public static final String FINANCIAL_BALANCE_TYPE_CODE = "financialBalanceTypeCode";
        public static final String FINANCIAL_SUB_OBJECT_CODE = "financialSubObjectCode";
        public static final String FINANCIAL_OBJECT_CODE = "financialObjectCode";
        public static final String SUB_ACCOUNT_NUMBER = "subAccountNumber";
        public static final String ACCOUNT_NUMBER = "accountNumber";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String UNIVERSITY_FISCAL_YEAR = "universityFiscalYear";
        public static final String GENERAL_LEDGER_ACCOUNT_IDENTIFIER = "generalLedgerAccountIdentifier";
        public static final String ACTIVITY_STATUS_CODE = "activityStatusCode";
        public static final String TRANSACTION_DATE = "transactionDate";
        public static final String REFERENCE_FINANCIAL_DOCUMENT_NUMBER = "referenceFinancialDocumentNumber";
        public static final String TRANSACTION_LEDGER_ENTRY_AMOUNT = "transactionLedgerEntryAmount";
        public static final String TRANSACTION_LEDGER_SUBMIT_AMOUNT = "transactionLedgerSubmitAmount";
        public static final String TRANSACTION_DEBIT_CREDIT_CODE = "transactionDebitCreditCode";
        public static final String PURAP_LINE_ASSET_ACCOUNTS = "purApLineAssetAccounts";
        public static final String GENERAL_LEDGER_ENTRY_ASSETS = "generalLedgerEntryAssets";
        public static final String ORGNIZATION_REFERENCE_ID = "organizationReferenceId";
        public static final String PROJECT_CD = "projectCode";
    }

    public static class Entry {
        public static final String FINANCIAL_DOCUMENT_TYPE_CODE = "financialDocumentTypeCode";
        public static final String UNIVERSITY_FISCAL_PERIOD_CODE = "universityFiscalPeriodCode";
        public static final String FINANCIAL_OBJECT_FINANCIAL_OBJECT_SUB_TYPE_CODE = "financialObject.financialObjectSubTypeCode";
        public static final String FINANCIAL_BALANCE_TYPE_CODE = "financialBalanceTypeCode";
        public static final String ACCOUNT_SUB_FUND_GROUP_CODE = "account.subFundGroupCode";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String TRANSACTION_DATE_TIME_STAMP = "transactionDateTimeStamp";
        public static final String DOCUMENT_NUMBER = "documentNumber";
    }

    public static class PurchasingAccountsPayableDocument {
        public static final String PURCHASE_ORDER_IDENTIFIER = "purchaseOrderIdentifier";
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static final String PURCHASEING_ACCOUNTS_PAYABLE_ITEM_ASSETS = "purchasingAccountsPayableItemAssets";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String UNIVERSITY_FISCAL_YEAR = "universityFiscalYear";
        public static final String PURAP_DOCUMENT_IDENTIFIER = "purapDocumentIdentifier";
    }

    public static class PurchasingAccountsPayableItemAsset {
        public static final String ACCOUNTS_PAYABLE_LINE_ITEM_IDENTIFIER = "accountsPayableLineItemIdentifier";
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static final String ITEM_LINE_NUMBER = "itemLineNumber";
        public static final String CAPITAL_ASSET_BUILDER_LINE_NUMBER = "capitalAssetBuilderLineNumber";
        public static final String ACCOUNTS_PAYABLE_ITEM_QUANTITY = "accountsPayableItemQuantity";
        public static final String SPLIT_QTY = "splitQty";
        public static final String PURCHASING_ACCOUNTS_PAYABLE_DOCUMENT = "purchasingAccountsPayableDocument";
        public static final String CAMS_DOCUMENT_NUMBER = "capitalAssetManagementDocumentNumber";
    }

    public static class PurchasingAccountsPayableLineAssetAccount {
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static final String PUR_ITM_ID = "accountsPayableLineItemIdentifier";
        public static final String CAB_LINE_NUMBER = "capitalAssetBuilderLineNumber";
        public static final String GENERAL_LEDGER_ENTRY = "generalLedgerEntry";
        public static final String PURAP_ITEM_ASSET = "purchasingAccountsPayableItemAsset";
    }

    public static class Pretag {
        public static final String CAMPUS_TAG_NUMBER = "campusTagNumber";
        public static final String PRETAG_DETAIL_CAMPUS_TAG_NUMBER = "pretagDetail.campusTagNumber";
        public static final String ITEM_LINE_NUMBER = "itemLineNumber";
        public static final String PURCHASE_ORDER_NUMBER = "purchaseOrderNumber";
        public static final String PRE_TAG_DETAIS = "pretagDetails";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String ORGANIZATION_CODE = "organizationCode";
        public static final String REPRESENTATIVE_ID = "personUniversal.principalName";
    }

    public static class PretagDetail {
        public static final String CAMPUS_TAG_NUMBER = "campusTagNumber";
        public static final String ITEM_LINE_NUMBER = "itemLineNumber";
        public static final String PURCHASE_ORDER_NUMBER = "purchaseOrderNumber";
    }

    public static final String DOCUMENT_NUMBER = "documentNumber";

    public static class PurApLineForm {
        public static final String PURAP_DOCS = "purApDocs";
        public static final String MERGE_QTY = "mergeQty";
        public static final String MERGE_DESC = "mergeDesc";
    }

    public static class GeneralLedgerPendingEntry {
        public static final String FINANCIAL_DOCUMENT_TYPE_CODE = "financialDocumentTypeCode";
        public static final String UNIVERSITY_FISCAL_PERIOD_CODE = "universityFiscalPeriodCode";
        public static final String FINANCIAL_OBJECT_FINANCIAL_OBJECT_SUB_TYPE_CODE = "financialObject.financialObjectSubTypeCode";
        public static final String FINANCIAL_BALANCE_TYPE_CODE = "financialBalanceTypeCode";
        public static final String ACCOUNT_SUB_FUND_GROUP_CODE = "account.subFundGroupCode";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String TRANSACTION_ENTRY_PROCESSED_TS = "transactionEntryProcessedTs";
        public static final String DOCUMENT_NUMBER = "documentNumber";
    }

    public static class CreditMemoAccountRevision {
        public static final String ACCOUNT_REVISION_TIMESTAMP = "accountRevisionTimestamp";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String ACCOUNT_SUB_FUND_GROUP_CODE = "account.subFundGroupCode";
        public static final String FINANCIAL_OBJECT_FINANCIAL_OBJECT_SUB_TYPE_CODE = "objectCode.financialObjectSubTypeCode";
    }

    public static class PaymentRequestAccountRevision {
        public static final String ACCOUNT_REVISION_TIMESTAMP = "accountRevisionTimestamp";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String ACCOUNT_SUB_FUND_GROUP_CODE = "account.subFundGroupCode";
        public static final String FINANCIAL_OBJECT_FINANCIAL_OBJECT_SUB_TYPE_CODE = "objectCode.financialObjectSubTypeCode";
    }

    public static class Account {
        public static final String ACCOUNT_NUMBER = "accountNumber";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
    }

    public static class PreTagExtract {
        public static final String FINANCIAL_OBJECT_SUB_TYPE_CODE = "objectCode.financialObjectSubTypeCode";
        public static final String ACCOUNT_SUB_FUND_GROUP_CODE = "account.subFundGroupCode";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String PURAP_ITEM_UNIT_PRICE = "purapItem.itemUnitPrice";        
        public static final String PO_INITIAL_OPEN_TIMESTAMP = "purapItem.purapDocument.purchaseOrderInitialOpenTimestamp";
        public static final String PURAP_CAPITAL_ASSET_SYSTEM_STATE_CODE = "purapItem.purapDocument.capitalAssetSystemStateCode";
    }

    public static class purApRequisitionItem {
        public static final String ITEM_LINE_NUMBER = "itemLineNumber";
    }

    public static class CapitalAssetInformation {
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String FINANCIAL_OBJECT_CODE = "financialObjectCode";
        public static final String SUB_ACCOUNT_NUMBER = "subAccountNumber";
        public static final String ACCOUNT_NUMBER = "accountNumber";
        public static final String UNIVERSITY_FISCAL_YEAR = "universityFiscalYear";
        public static final String FINANCIAL_DOCUMENT_TYPE_CODE = "financialDocumentTypeCode";
        public static final String ACTION_INDICATOR = "capitalAssetActionIndicator";
        public static final String ASSET_LINE_NUMBER = "capitalAssetLineNumber";
        public static final String ASSET_PROCESSED_IND = "capitalAssetProcessedIndicator";
    }

    public static class PurchasingAccountsPayableProcessingReport {
        public static final String PURAP_DOCUMENT_IDENTIFIER = "purapDocumentIdentifier";
    }

    public static final String PURAP_DOCUMENT = "purapDocument";
    public static final String PURAP_ITEM = "purapItem";
    public static final String ACTIVE = "active";

    public static class AssetGlobalDocumentCreate {
        public static final String CAPITAL_ASSET_TYPE_CODE = "capitalAssetTypeCode";
        public static final String CAMPUS_CODE = "campusCode";
        public static final String BUILDING_CODE = "buildingCode";
        public static final String BUILDING_ROOM_NUMBER = "buildingRoomNumber";
    }

    public static class Parameter {
        public static final String PARAMETER_NAME = "name";
        public static final String PARAMETER_DETAIL_TYPE_CODE = "componentCode";
        public static final String PARAMETER_NAMESPACE_CODE = "namespaceCode";
        public static final String PARAMETER_VALUE = "value";

    }

    public static class CapitalAssetLock {
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static final String LOCKING_INFORMATION = "lockingInformation";
        public static final String CAPITAL_ASSET_NUMBER = "capitalAssetNumber";
        public static final String DOCUMENT_TYPE_NAME = "documentTypeName";
    }
}
