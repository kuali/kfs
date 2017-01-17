package edu.arizona.kfs.module.tax;

/**
 * This class houses constants used for the property names of various business objects used within the Tax Module.
 */
public class TaxPropertyConstants {

    public static class PayerFields {
        public static final String PAYER_ID = "id";
        public static final String TRANS_CD = "transCd";
        public static final String TEST_FLG = "testFlg";
        public static final String REPLACE_ALPHA = "replaceAlpha";
        public static final String TIN_TYPE = "tinType";
        public static final String TIN = "tin";
        public static final String NAME_CONTROL = "nameControl";
        public static final String NAME_1 = "name1";
        public static final String NAME_2 = "name2";
        public static final String CONTACT = "contact";
        public static final String PHONE_NUMBER = "phoneNumber";
        public static final String EXTENSION = "ext";
        public static final String COMPANY_NAME_1 = "companyName1";
        public static final String COMPANY_NAME_2 = "companyName2";
        public static final String ADDRESS = "address";
        public static final String CITY = "city";
        public static final String STATE = "state";
        public static final String ZIP_CODE = "zipCode";
        public static final String COUNTRY_CD = "countryCode";
        public static final String EMAIL = "email";
        public static final String OBJECT_ID = "objectId";
        public static final String VERSION_NUMBER = "versionNumber";
    }

    public static class PayeeFields {
        public static final String PAYEE_ID = "id";
        public static final String HEADER_TYPE_CODE = "headerTypeCode";
        public static final String HEADER_TAX_NUMBER = "headerTaxNumber";
        public static final String HEADER_OWNERSHIP_CODE = "headerOwnershipCode";
        public static final String HEADER_OWNERSHIP_CATEGORY_CODE = "headerOwnershipCategoryCode";
        public static final String VENDOR_NAME = "vendorName";
        public static final String VENDOR_NUMBER = "vendorNumber";
        public static final String ADDRESS_TYPE_CODE = "addressTypeCode";
        public static final String ADDRESS_LINE1_ADDRESS = "addressLine1Address";
        public static final String ADDRESS_LINE2_ADDRESS = "addressLine2Address";
        public static final String ADDRESS_CITY_NAME = "addressCityName";
        public static final String ADDRESS_STATE_CODE = "addressStateCode";
        public static final String ADDRESS_ZIP_CODE = "addressZipCode";
        public static final String ADDRESS_COUNTRY_CODE = "addressCountryCode";
        public static final String VENDOR_FOREIGN_INDICATOR = "vendorForeignIndicator";
        public static final String TAX_YEAR = "taxYear";
        public static final String CORRECTION_INDICATOR = "correctionIndicator";
        public static final String EXCLUDE_INDICATOR = "excludeIndicator";
        public static final String VENDOR_FEDERAL_WITHHOLDING_TAX_BEGINNING_DATE = "vendorFederalWithholdingTaxBeginningDate";
        public static final String VENDOR_FEDERAL_WITHHOLDING_TAX_END_DATE = "vendorFederalWithholdingTaxEndDate";
        public static final String VENDOR_HEADER_GENERATED_IDENTIFIER = "vendorHeaderGeneratedIdentifier";
        public static final String VENDOR_DETAIL_ASSIGNED_IDENTIFIER = "vendorDetailAssignedIdentifier";
        public static final String OBJECT_ID = "objectId";
        public static final String VERSION_NUMBER = "versionNumber";
        public static final String VENDOR_DETAIL = "vendorDetail";
        public static final String PAYMENTS = "payments";
        public static final String VENDOR_HEADER = "vendorHeader";
    }

    public static class PaymentFields {
        public static final String PAYMENT_ID = "id";
        public static final String PAYMENT_GROUP_ID = "paymentGroupId";
        public static final String DOCUMENT_NUMBER = "docNbr";
        public static final String PURCHASE_ORDER_NUMBER = "poNbr";
        public static final String DISBURSEMENT_DATE = "disbursementDt";
        public static final String DISBURSEMENT_NUMBER = "disbursementNbr";
        public static final String DOCUMENT_TYPE = "docType";
        public static final String INVOICE_NUMBER = "invoiceNbr";
        public static final String PAYMENT_ACCOUNT_DETAIL_ID = "paymentAcctDetailId";
        public static final String FINANCIAL_CHART_CODE = "finChartCode";
        public static final String PAYMENT_TYPE_CODE = "paymentTypeCode";
        public static final String ACCOUNT_NUMBER = "accountNbr";
        public static final String FINANCIAL_OBJECT_CODE = "finObjectCode";
        public static final String ACCOUNT_NET_AMOUNT = "acctNetAmount";
        public static final String OBJECT_ID = "objectId";
        public static final String VERSION_NUMBER = "versionNumber";
        public static final String EXCLUDE_INDICATOR = "excludeIndicator";
        public static final String PAYEE_ID = "payeeId";
        public static final String PAYEE = "payee";
    }

    public static class ExtractHistoryFields {
        public static final String EXTRACT_HISTORY_ID = "id";
        public static final String PAYMENTS_DELETED = "paymentsDeleted";
        public static final String PAYMENTS_EXTRACTED = "paymentsExtracted";
        public static final String PAYEES_EXTRACTED = "payeesExtracted";
        public static final String EXTRACT_START_DATE = "extractStartDt";
        public static final String EXTRACT_END_DATE = "extractEndDt";
        public static final String EXTRACT_DATE = "extractDt";
        public static final String REPLACE_DATA_INDICATOR = "replaceDataInd";
        public static final String TAX_YEAR = "taxYear";
        public static final String OBJECT_ID = "objectId";
        public static final String VERSION_NUMBER = "versionNumber";
    }

    public static class ElectronicFilingReportFields {
        public static final String TAX_YEAR = "taxYear";
    }

    public static class TaxFormFields {
        public static final String ID = "id";
        public static final String YEAR = "year";
    }

    public static final String PAYMENT_DISBURSEMENT_TYPE_CODE = "paymentGroup.disbursementType.code";
    public static final String PAYMENT_STATUS_CODE = "paymentGroup.paymentStatus.code";
    public static final String PAYMENT_CUSTMER_ORG_CODE = "paymentGroup.batch.customerProfile.orgCode";

    public static final String PAYMENT_DETAIL_PAYEE_ID = "payee.id";
    public static final String PAYEE_TAX_YEAR = "payee.taxYear";

}
