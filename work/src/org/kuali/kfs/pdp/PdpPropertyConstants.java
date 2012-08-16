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
package org.kuali.kfs.pdp;


/**
 * Contains property name constants.
 */
public class PdpPropertyConstants {
    public static final String PAYMENT_TYPE_CODE = "paymentTypeCode";
    public static final String PAYMENT_ID = "paymentId";
    public static final String BEGIN_DATE = "beginDate";
    public static final String END_DATE = "endDate";
    public static final String ORG_CODE = "orgCode";
    public static final String SUB_UNIT_CODE = "subUnitCode";
    public static final String CHART_CODE = "chartCode";
    public static final String ACH_ACCOUNT_GENERATED_IDENTIFIER = "achAccountGeneratedIdentifier";
    public static final String BATCH_ID = "batchId";
    public static final String BANK_ROUTING_NUMBER = "bankRoutingNumber";
    public static final String CUSTOMER_ID = "customerId";
    public static final String CUSTOMER_FILE_CREATE_TIMESTAMP = "customerFileCreateTimestamp";
    public static final String DISBURSEMENT_TYPE_CODE = "disbursementTypeCode";
    public static final String CUSTOMER_BANK = "bank";
    public static final String DETAIL_COUNT = "detailCount";
    public static final String PAYMENT_COUNT = "paymentCount";
    public static final String PAYMENT_TOTAL_AMOUNT = "paymentTotalAmount";
    public static final String CREATION_DATE = "creationDate";
    public static final String LAST_ASSIGNED_DISBURSEMENT_NUMBER = "lastAssignedDisbNbr";
    public static final String UNIT = "unit";
    public static final String SUB_UNIT = "subUnit";
    public static final String DISBURSEMENT_NUMBER_RANGE_START_DATE = "disbNbrRangeStartDt";    
    
    public static final String PAYEE_IDENTIFIER_TYPE_CODE = "payeeIdentifierTypeCode";
    public static final String PAYEE_ID_NUMBER = "payeeIdNumber";
    public static final String PAYEE_NAME = "payeeName";
    public static final String PAYEE_EMAIL_ADDRESS = "payeeEmailAddress";
    public static final String ACH_TRANSACTION_TYPE = "achTransactionType";
    public static final String PAYEE_CODE = "code";
    
    public static final String CHART_DB_COLUMN_NAME = "fin_coa_cd";
    public static final String ACCOUNT_DB_COLUMN_NAME = "account_nbr";
    public static final String SUB_ACCOUNT_DB_COLUMN_NAME = "sub_acct_nbr";
    public static final String OBJECT_DB_COLUMN_NAME = "fin_object_cd";
    public static final String SUB_OBJECT_DB_COLUMN_NAME = "fin_sub_obj_cd";
    public static final String PROJECT_DB_COLUMN_NAME = "project_cd";
    public static final String ORIG_BANK_CODE = "origBankCode";
    public static final String DISBURSEMENT_NBR = "disbursementNbr";
    public static final String PAYMENT_CHANGE_CODE = "paymentChangeCode";
    public static final String PHYS_CAMPUS_PROC_CODE = "physCampusProcCode";
    public static final String PHYS_CAMPUS_PROCESS_CODE = "physicalCampusProcessCode";
    public static final String BEGIN_DISBURSEMENT_NBR = "beginDisbursementNbr";
    public static final String END_DISBURSEMENT_NBR = "endDisbursementNbr";
    public static final String PAYMENT_GROUP = "paymentGroup";
    public static final String PAYMENT_GROUP_HISTORY = "paymentGroupHistory";
    public static final String PROCESS_IND = "processInd";
    public static final String PAYMENT_STATUS_CODE = "paymentStatusCode";
    public static final String PAYMENT_STATUS = "paymentStatus";
    public static final String ADVICE_EMAIL_SENT_DATE = "adviceEmailSentDate";
    public static final String CUSTOMER_INSTITUTION_NUMBER = "customerInstitutionNumber";
    public static final String PAYMENT_PROC_IDENTIFIER = "paymentProcIdentifier";
    
    public static class BatchConstants{
            public static final String BATCH_ID = "id";
            public static final String CHART_CODE = "customerProfile.chartCode";
            public static final String ORG_CODE = "customerProfile.orgCode";
            public static final String SUB_UNIT_CODE = "customerProfile.subUnitCode";
            public static final String PAYMENT_COUNT = "paymentCount";
            public static final String PAYMENT_TOTAL_AMOUNT = "paymentTotalAmount";
            public static final String FILE_CREATION_TIME = "customerFileCreateTimestamp";
            public static final String CUSTOMER_ID = "customerId";
        }
    
    public static class PaymentDetail {
            public static final String PAYMENT_GROUP_BATCH_ID = "paymentGroup.batchId";
            public static final String PAYMENT_STATUS_CODE = "paymentGroup.paymentStatusCode";
            public static final String PAYMENT_DISBURSEMENT_NUMBER = "paymentGroup.disbursementNbr";
            public static final String PAYMENT_PAYEE_NAME = "paymentGroup.payeeName";
            public static final String PAYMENT_CHART_CODE = "paymentGroup.batch.customerProfile.chartCode";
            public static final String PAYMENT_UNIT_CODE = "paymentGroup.batch.customerProfile.unitCode";
            public static final String PAYMENT_CUSTOMER_DOC_NUMBER = "custPaymentDocNbr";
            public static final String PAYMENT_PAYEE_ID_TYPE_CODE = "paymentGroup.payeeIdTypeCd";
            public static final String PAYMENT_PURCHASE_ORDER_NUMBER = "purchaseOrderNbr";
            public static final String PAYMENT_PAYEE_ID = "paymentGroup.payeeId";
            public static final String PAYMENT_SUBUNIT_CODE = "paymentGroup.batch.customerProfile.subUnitCode";
            public static final String PAYMENT_INVOICE_NUMBER = "invoiceNbr";
            public static final String PAYMENT_DISBURSEMENT_TYPE_CODE = "paymentGroup.disbursementTypeCode";
            public static final String PAYMENT_PROCESS_IMEDIATE = "paymentGroup.processImmediate";
            public static final String PAYMENT_REQUISITION_NUMBER = "requisitionNbr";
            public static final String PAYMENT_SPECIAL_HANDLING = "paymentGroup.pymtSpecialHandling";
            public static final String PAYMENT_CUSTOMER_INSTITUTION_NUMBER = "customerInstitutionNumber";
            public static final String PAYMENT_DISBURSEMENT_DATE = "paymentGroup.disbursementDate"; 
            public static final String PAYMENT_ATTACHMENT = "paymentGroup.pymtAttachment";
            public static final String PAYMENT_PROCESS_ID = "paymentGroup.processId";
            public static final String PAYMENT_DATE = "paymentGroup.paymentDate";
            public static final String PAYMENT_ID = "id";
            public static final String PAYMENT_NET_AMOUNT = "netPaymentAmount";
            public static final String PAYMENT_DISBURSEMENT_FINANCIAL_DOCUMENT_TYPE_CODE = "financialDocumentTypeCode";
            public static final String PAYMENT_DISBURSEMENT_CUST_PAYMENT_DOC_NBR = "custPaymentDocNbr";
            public static final String PAYMENT_DISBURSEMENT_TYPE_NAME = "paymentGroup.disbursementType.name";
            public static final String PAYMENT_ORIGINAL_INVOICE_AMOUNT = "origInvoiceAmount";
            public static final String PAYMENT_INVOICE_TOTAL_DISCOUNT_AMOUNT = "invTotDiscountAmount";
            public static final String PAYMENT_INVOICE_TOTAL_SHIPPING_AMOUNT = "invTotShipAmount";
            public static final String PAYMENT_INVOICE_TOTAL_OTHER_DEBIT_AMOUNT = "invTotOtherDebitAmount";
            public static final String PAYMENT_INVOICE_TOTAL_OTHER_CREDIT_AMOUNT = "invTotOtherCreditAmount";
            public static final String PAYMENT_GROUP = "paymentGroup";
            public static final String PAYMENT_EPIC_PAYMENT_CANCELLED_DATE = "paymentGroup.epicPaymentCancelledExtractedDate";
            public static final String PAYMENT_EPIC_PAYMENT_PAID_EXTRACTED_DATE = "paymentGroup.epicPaymentPaidExtractedDate";
            public static final String PAYMENT_DETAIL_NUMBER_OF_PAYMENTS_IN_PAYMENT_GROUP = "nbrOfPaymentsInPaymentGroup";
            public static final String PAYMENT_DETAIL_NUMBER_OF_PAYMENTS_IN_DISBURSEMENT = "nbrOfPaymentsInDisbursement";
            public static final String PAYMENT_DETAIL_PAYMENT_GROUP_ID = "paymentGroupId";
            public static final String BEGIN_DISBURSEMENT_DATE = "beginDisbursementDate";
            public static final String END_DISBURSEMENT_DATE = "endDisbursementDate";
            public static final String BEGIN_PAYMENT_DATE = "beginPaymentDate";
            public static final String END_PAYMENT_DATE = "endPaymentDate";
        }
    
    public static class PaymentGroupHistory {
            public static final String PAYMENT_GROUP_PAYEE_NAME = "paymentGroup.payeeName";
            public static final String PAYMENT_GROUP_PAYEE_ID = "paymentGroup.payeeId";
            public static final String PAYMENT_GROUP_PAYEE_ID_TYPE_CODE = "paymentGroup.payeeIdTypeCd";
            public static final String PAYMENT_GROUP_PAYMENT_ATTACHMENT = "paymentGroup.pymtAttachment";
            public static final String PAYMENT_GROUP_ORIGIN_PAYMENT_SPECIAL_HANDLING = "origPmtSpecHandling";
            public static final String PAYMENT_GROUP_ORIGIN_PROCESS_IMMEDIATE = "origProcessImmediate";
            public static final String PAYMENT_GROUP_ORIGIN_DISBURSEMENT_NUMBER = "origDisburseNbr";
            public static final String PAYMENT_GROUP_PAYMENT_PROCESS_ID = "processId";
            public static final String PAYMENT_GROUP_PAYMENT_DETAILS_NET_AMOUNT = "paymentGroup.paymentDetails.netPaymentAmount";
            public static final String PAYMENT_GROUP_ORIGIN_DISBURSE_DATE = "origDisburseDate";
            public static final String PAYMENT_GROUP_ORIGIN_PAYMENT_DATE = "origPaymentDate";
            public static final String PAYMENT_GROUP_ORIGIN_PAYMENT_STATUS_CODE = "origPaymentStatus.code";
            public static final String PAYMENT_GROUP_DISBURSEMENT_TYPE_CODE = "disbursementType.code";
            public static final String PAYMENT_GROUP_CHART_CODE = "paymentGroup.batch.customerProfile.chartCode";
            public static final String PAYMENT_GROUP_ORG_CODE = "paymentGroup.batch.customerProfile.unitCode";
            public static final String PAYMENT_GROUP_SUB_UNIT_CODE = "paymentGroup.batch.customerProfile.subUnitCode";
            public static final String PMT_CANCEL_EXTRACT_DATE = "pmtCancelExtractDate";

        }
    
    public static class PaymentGroup {
        public static final String PAYMENT_GROUP_ID = "id";
        public static final String PAYMENT_GROUP_DISBURSEMENT_TYPE_CODE = "disbursementTypeCode";
        public static final String PAYMENT_GROUP_PAYMENT_STATUS_CODE = "paymentStatusCode";
        public static final String PAYMENT_GROUP_DISBURSEMENT_NBR = "disbursementNbr";
        public static final String PAYMENT_GROUP_PROCESS_ID = "processId";
        public static final String PAYMENT_GROUP_SORT_VALUE = "sortValue";
        public static final String PAYMENT_GROUP_PAYEE_NAME = "payeeName";
        public static final String PAYMENT_GROUP_LINE1_ADDRESS = "line1Address";
        public static final String PAYMENT_GROUP_BATCH_ID = "batchId";
        public static final String PAYMENT_GROUP_BATCH = "batch";
        public static final String PAYMENT_GROUP_BANK_CODE = "bankCode";
        public static final String PAYMENT_GROUP_PAYEE_ID = "payeeId";
        public static final String PAYMENT_GROUP_PAYEE_ID_TYPE_CODE = "payeeIdTypeCd";
        public static final String PAYMENT_ATTACHMENT = "pymtAttachment";
        public static final String PAYMENT_SPECIAL_HANDLING = "pymtSpecialHandling";
        public static final String TAXABLE_PAYMENT = "taxablePayment";
        public static final String NRA_PAYMENT = "nraPayment";
        public static final String PROCESS_IMMEDIATE = "processImmediate";
        public static final String PAYMENT_DATE = "paymentDate"; 
        public static final String NOTES_LINES = "noteLines";
    }
    
    public static class PaymentProcess {
        public static final String PAYMENT_PROCESS_ID = "id";
        public static final String EXTRACTED_IND = "extractedInd";
        public static final String FORMATTED_IND = "formattedIndicator";
    }
    
    public static class ProcessSummary {
        public static final String PROCESS_SUMMARY_PROCESS_ID = "processId";
    }
    
    public static class CustomerProfile {
        public static final String CUSTOMER_PROFILE_CHART_CODE = "chartCode";
        public static final String CUSTOMER_PROFILE_UNIT_CODE = "unitCode";
        public static final String CUSTOMER_PROFILE_SUB_UNIT_CODE = "subUnitCode";
        public static final String CUSTOMER_PROFILE_BANKS = "customerBanks";
        public static final String CUSTOMER_DEFAULT_SUB_OBJECT_CODE = "---";
        public static final String CUSTOMER_DEFAULT_SUB_ACCOUNT_NUMBER = "-----";
    }
    
    public static class DisbursementNumberRange {
        public static final String DISBURSEMENT_NUMBER_RANGE_PHYS_CAMPUS_PROC_CODE = "physCampusProcCode";
        public static final String DISBURSEMENT_NUMBER_RANGE_TYPE_CODE = "disbursementTypeCode";
        
    }
    
    public static class DailyReport {
        public static final String CUSTOMER = "customer";
        public static final String AMOUNT = "amount";
        public static final String PAYMENTS = "payments";
        public static final String PAYEES = "payees";
    }

    public static class FormatProcessSummary {
        public static final String PROCESS_SUMMARY = "processSummary";
    }
    
    public static class FormatResult {
        public static final String PROC_ID = "procId";
    }
    
    public static class FormatSelection {
        public static final String CAMPUS = "campus";
        public static final String START_DATE = "startDate";
        public static final String CUSTOMER_LIST = "customerList";
        public static final String RANGE_LIST = "rangeList";
    }
}
