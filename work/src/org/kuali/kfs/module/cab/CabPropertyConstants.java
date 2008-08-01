/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
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
        public static final String FINANCIAL_BALANCE_TYPE_CODE = "financialBalanceTypeCode";
        public static final String FINANCIAL_SUB_OBJECT_CODE = "financialSubObjectCode";
        public static final String FINANCIAL_OBJECT_CODE = "financialObjectCode";
        public static final String SUB_ACCOUNT_NUMBER = "subAccountNumber";
        public static final String ACCOUNT_NUMBER = "accountNumber";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String UNIVERSITY_FISCAL_YEAR = "universityFiscalYear";
    }

    public static class Parameter {
        public static final String PARAMETER_NAME = "parameterName";
        public static final String PARAMETER_DETAIL_TYPE_CODE = "parameterDetailTypeCode";
        public static final String PARAMETER_NAMESPACE_CODE = "parameterNamespaceCode";

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
    }
}
