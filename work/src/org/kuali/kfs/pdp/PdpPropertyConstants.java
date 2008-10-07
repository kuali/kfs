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
package org.kuali.kfs.pdp;

/**
 * Contains property name constants.
 */
public class PdpPropertyConstants {
    public static final String BATCH_ID = "batchId";
    public static final String CUSTOMER_ID = "customerId";
    public static final String CUSTOMER_FILE_CREATE_TIMESTAMP = "customerFileCreateTimestamp";
    public static final String DISBURSEMENT_TYPE_CODE = "disbursementTypeCode";
    public static final String DETAIL_COUNT = "detailCount";
    public static final String PAYMENT_COUNT = "paymentCount";
    public static final String PAYMENT_TOTAL_AMOUNT = "paymentTotalAmount";
    public static final String CREATION_DATE = "creationDate";
    public static final String LAST_ASSIGNED_DISBURSEMENT_NUMBER = "lastAssignedDisbNbr";
    public static final String SUB_UNIT = "subUnit";
    
    public static final String PAYEE_IDENTIFIER_TYPE_CODE = "payeeIdentifierTypeCode";
    public static final String PSD_TRANSACTION_CODE = "psdTransactionCode";
    public static final String PAYEE_SOCIAL_SECURITY_NUMBER = "payeeSocialSecurityNumber";
    public static final String PAYEE_FEDERAL_EMPLOYER_IDENTIFICATION_NUMBER = "payeeFederalEmployerIdentificationNumber";
    
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
    public static final String BEGIN_DISBURSEMENT_NBR = "beginDisbursementNbr";
    public static final String END_DISBURSEMENT_NBR = "endDisbursementNbr";
    public static final String PAYMENT_GROUP = "paymentGroup";
    public static final String PAYMENT_GROUP_HISTORY = "paymentGroupHistory";
    
    public static class BatchConstants{
        public static class Fields{
            public static final String BATCH_ID = "id";
            public static final String CHART_CODE = "customerProfile.chartCode";
            public static final String ORG_CODE = "customerProfile.orgCode";
            public static final String SUB_UNIT_CODE = "customerProfile.subUnitCode";
            public static final String PAYMENT_COUNT = "paymentCount";
            public static final String PAYMENT_TOTAL_AMOUNT = "paymentTotalAmount";
            public static final String FILE_CREATION_TIME = "customerFileCreateTimestamp";
        }
    }
    
    public static class PaymentDetail {
        public static class Fields {
            public static final String PAYMENT_GROUP_BATCH_ID = "paymentGroup.batch.id";
            public static final String PAYMENT_STATUS_CODE = "paymentGroup.paymentStatusCode";
            public static final String PAYMENT_DISBURSEMENT_NUMBER = "paymentGroup.disbursementNbr";
            public static final String PAYMENT_PAYEE_NAME = "paymentGroup.payeeName";
            public static final String PAYMENT_CHART_CODE = "paymentGroup.batch.customerProfile.chartCode";
            public static final String PAYMENT_ORG_CODE = "paymentGroup.batch.customerProfile.orgCode";
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
            public static final String PAYMENT_CUSTOMER_INSTITUTION_NUMBER = "paymentGroup.customerInstitutionNumber";
            public static final String PAYMENT_DISBURSEMENT_DATE = "paymentGroup.disbursementDate";
            public static final String PAYMENT_ATTACHMENT = "paymentGroup.pymtAttachment";
            public static final String PAYMENT_PROCESS_ID = "paymentGroup.processId";
            public static final String PAYMENT_DATE = "paymentGroup.paymentDate";
            public static final String PAYMENT_ID = "id";
            public static final String PAYMENT_NET_AMOUNT = "netPaymentAmount";
            public static final String PAYMENT_DISBURSEMENT_TYPE_NAME = "paymentGroup.disbursementType.name";
        }
    }
    
    public static class PaymenGroupHistory {
        public static class Fields {
            public static final String PAYMENT_GROUP_CUSTOMER_INSTITUTION_NUMBER = "paymentGroup.customerInstitutionNumber";
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
            public static final String PAYMENT_GROUP_ORG_CODE = "paymentGroup.batch.customerProfile.orgCode";
            public static final String PAYMENT_GROUP_SUB_UNIT_CODE = "paymentGroup.batch.customerProfile.subUnitCode";

        }
    }
}
