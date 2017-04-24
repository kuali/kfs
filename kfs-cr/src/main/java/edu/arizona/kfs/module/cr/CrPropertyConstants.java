package edu.arizona.kfs.module.cr;

/**
 * This class houses constants used for the property names of various business objects used within the Check Reconciliation Module.
 */
public class CrPropertyConstants {

    public static class CheckReconciliation {
        public static final String ID = "id";
        public static final String CHECK_NUMBER = "checkNumber";
        public static final String BANK_ACCOUNT_NUMBER = "bankAccountNumber";
        public static final String CHECK_DATE = "checkDate";
        public static final String AMOUNT = "amount";
        public static final String STATUS = "status";
        public static final String LAST_UPDATE = "lastUpdate";
        public static final String GL_TRANS_INDICATOR = "glTransIndicator";
        public static final String SOURCE_CODE = "sourceCode";
        public static final String PAYEE_ID = "payeeId";
        public static final String PAYEE_TYPE_CODE = "payeeTypeCode";
        public static final String CLEARED_DATE = "clearedDate";
        public static final String STATUS_CHANGE_DATE = "statusChangeDate";
        public static final String OBJECT_ID = "objectId";
        public static final String VERSION_NUMBER = "versionNumber";
    }

    public static class CheckReconSource {
        public static final String ID = "id";
        public static final String SOURCE_CODE = "sourceCd";
        public static final String SOURCE_NAME = "sourceName";
        public static final String OBJECT_ID = "objectId";
        public static final String VERSION_NUMBER = "versionNumber";
    }

    public static class PaymentGroup {
        public static final String ID = "id";
        public static final String CITY = "city";
        public static final String COUNTRY = "country";
        public static final String STATE = "state";
        public static final String ACH_BANK_ROUTING_NUMBER = "achBankRoutingNbr";
        public static final String ACH_ACCOUNT_TYPE = "achAccountType";
        public static final String ADVICE_EMAIL_ADDRESS = "adviceEmailAddress";
        public static final String ALTERNATE_PAYEE_ID = "alternatePayeeId";
        public static final String ALTERNATE_PAYEE_ID_TYPE_CD = "alternatePayeeIdTypeCd";
        public static final String CAMPUS_ADDRESS = "campusAddress";
        public static final String DISBURSEMENT_DATE = "disbursementDate";
        public static final String ADVICE_EMAIL_SENT_DATE = "adviceEmailSentDate";
        public static final String DISBURSEMENT_NBR = "disbursementNbr";
        public static final String EMPLOYEE_INDICATOR = "employeeIndicator";
        public static final String LINE1_ADDRESS = "line1Address";
        public static final String LINE2_ADDRESS = "line2Address";
        public static final String LINE3_ADDRESS = "line3Address";
        public static final String LINE4_ADDRESS = "line4Address";
        public static final String NRA_PAYMENT = "nraPayment";
        public static final String PAYEE_ID = "payeeId";
        public static final String PAYEE_ID_TYPE_CODE = "payeeIdTypeCd";
        public static final String PAYEE_NAME = "payeeName";
        public static final String PAYEE_OWNER_CD = "payeeOwnerCd";
        public static final String PAYMENT_DATE = "paymentDate";
        public static final String PHYS_CAMPUS_PROCESS_CD = "physCampusProcessCd";
        public static final String PROCESS_IMMEDIATE = "processImmediate";
        public static final String PAYMENT_ATTACHMENT = "pymtAttachment";
        public static final String PAYMENT_SPECIAL_HANDLING = "pymtSpecialHandling";
        public static final String TAXABLE_PAYMENT = "taxablePayment";
        public static final String COMBINE_GROUPS = "combineGroups";
        public static final String ZIP_CD = "zipCd";
        public static final String SORT_VALUE = "sortValue";
        public static final String LAST_UPDATE = "lastUpdate";
        public static final String VERSION_NUMBER = "versionNumber";
        public static final String PROCESS_ID = "processId";
        public static final String BANK_CODE = "bankCode";
        public static final String BATCH_ID = "batchId";
        public static final String DISBURSEMENT_TYPE_CODE = "disbursementTypeCode";
        public static final String PAYMENT_STATUS_CODE = "paymentStatusCode";
        public static final String EPIC_PAYMENT_CANCELLED_EXTRACTED_DATE = "epicPaymentCancelledExtractedDate";
        public static final String PAYMENT_GROUP_EPIC_PAYMENT_PAID_EXTRACTED_DATE = "epicPaymentPaidExtractedDate";
        public static final String PAYMENT_GROUP_OBJECT_ID = "objectId";
    }

    public static class PaymentGroupHistory {
        public static final String ID = "id";
        public static final String PMT_CANCEL_EXTRACT_DATE = "pmtCancelExtractDate";
        public static final String CHANGE_NOTE_TEXT = "changeNoteText";
        public static final String CHANGE_TIME = "changeTime";
        public static final String ORIG_ACH_BANK_ROUTE_NBR = "origAchBankRouteNbr";
        public static final String ORIG_ADVICE_EMAIL = "origAdviceEmail";
        public static final String ORIGIN_DISBURSE_DATE = "origDisburseDate";
        public static final String ORIGIN_DISBURSEMENT_NUMBER = "origDisburseNbr";
        public static final String ORIGIN_PAYMENT_DATE = "origPaymentDate";
        public static final String ORIGIN_PAYMENT_SPECIAL_HANDLING = "origPmtSpecHandling";
        public static final String ORIGIN_PROCESS_IMMEDIATE = "origProcessImmediate";
        public static final String PMT_CANCEL_EXTRACT_STAT = "pmtCancelExtractStat";
        public static final String CHANGE_USER_ID = "changeUserId";
        public static final String PAYMENT_GROUP_ID = "paymentGroupId";
        public static final String PAYMENT_CHANGE_CODE = "paymentChangeCode";
        public static final String PAYMENT_STATUS_CODE = "paymentStatusCode";
        public static final String DISBURSEMENT_TYPE_CODE = "disbursementTypeCode";
        public static final String ORIG_BANK_CODE = "origBankCode";
        public static final String PAYMENT_PROCESS_ID = "processId";
        public static final String LAST_UPDATE = "lastUpdate";
        public static final String VERSION_NUMBER = "versionNumber";
        public static final String OBJECT_ID = "objectId";

        public static final String PAYMENT_GROUP_PAYEE_NAME = "paymentGroup.payeeName";
        public static final String PAYMENT_GROUP_PAYEE_ID = "paymentGroup.payeeId";
        public static final String PAYMENT_GROUP_PAYEE_ID_TYPE_CODE = "paymentGroup.payeeIdTypeCd";
        public static final String PAYMENT_GROUP_PAYMENT_ATTACHMENT = "paymentGroup.pymtAttachment";
        public static final String PAYMENT_GROUP_PAYMENT_DETAILS_NET_AMOUNT = "paymentGroup.paymentDetails.netPaymentAmount";
        public static final String PAYMENT_GROUP_BANK_CODE = "paymentGroup.bankCode";
        public static final String PAYMENT_GROUP_ORIGIN_PAYMENT_STATUS_CODE = "origPaymentStatus.code";
        public static final String PAYMENT_GROUP_DISBURSEMENT_TYPE_CODE = "disbursementType.code";
        public static final String PAYMENT_GROUP_CHART_CODE = "paymentGroup.batch.customerProfile.chartCode";
        public static final String PAYMENT_GROUP_ORG_CODE = "paymentGroup.batch.customerProfile.unitCode";
        public static final String PAYMENT_GROUP_SUB_UNIT_CODE = "paymentGroup.batch.customerProfile.subUnitCode";

    }

    public static class CheckReconciliationReport {
        public static final String END_DATE = "endDate";
        public static final String FORMAT = "format";
    }
}
