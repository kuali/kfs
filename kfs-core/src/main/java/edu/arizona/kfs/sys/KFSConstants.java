package edu.arizona.kfs.sys;

import java.util.HashSet;
import java.util.Set;

import org.kuali.rice.kew.api.KewApiConstants;

public class KFSConstants extends org.kuali.kfs.sys.KFSConstants {

    public static final String INVOICE_NUMBER = "Invoice Number";
    public static final String DUPLICATE_INVOICE_QUESTION_ID = "DVDuplicateInvoice";

    public static final String GL_ENTRY_IMPORTING = "glEntryImporting";
    public static final String DOC_FORM_KEY_VALUE_88888888 = "88888888";
    public static final String NULL_STRING = "null";
    public static final String PATH_SEPERATOR = "/";
    public static final String BLANK_SUBACCOUNT = "-----";
    public static final String BLANK_SUBOBJECT = "---";
    public static final String BLANK_PROJECT_CODE = "----------";
    public static final String STAGING_DIRECTORY_KEY = "staging.directory";
    
    //Docuware Constants
    public static final String VIEW_DOCUWARE = "viewImages";
    public static final String DOCUWARE_TABLE_PARAMETER = "DOCUWARE_TABLE_PARAMETER";
    public static final String DOCUWARE_TABLE = "table";
    public static final String DOCUWARE_IDVALUE = "idvalue";
    
    //Shipping Constants   
    public static final String SHIP_FILE_TYPE_IDENTIFIER = "shippingInputFileType";
    public static final String SHIPPING_FILE_NAME = "extract_for_shipping";
    public static final String SHIPPING_INVOICE_ORIGIN_CODE = "SH";
    
    public static final String DOCUWARE_DV_DOC_TYPE = "DV";
    public static final String DOCUWARE_PREQ_DOC_TYPE = "PREQ";

    // General Error Correction Constants
    public static final String GEC_ENTRY_OBJ_ID = "OBJ_ID";
    public static final Set<String> GEC_ACTIVE_ROUTE_STATUS_CODES;
    static {
        GEC_ACTIVE_ROUTE_STATUS_CODES = new HashSet<String>();
        GEC_ACTIVE_ROUTE_STATUS_CODES.add(KewApiConstants.ROUTE_HEADER_ENROUTE_CD);
        GEC_ACTIVE_ROUTE_STATUS_CODES.add(KewApiConstants.ROUTE_HEADER_INITIATED_CD);
        GEC_ACTIVE_ROUTE_STATUS_CODES.add(KewApiConstants.ROUTE_HEADER_PROCESSED_CD);
        GEC_ACTIVE_ROUTE_STATUS_CODES.add(KewApiConstants.ROUTE_HEADER_SAVED_CD);
    }

    public static class SysKimApiConstants {
        public static final String ACCOUNT_SUPERVISOR_KIM_ROLE_NAME = "Account Supervisor";
        public static final String CONTRACTS_AND_GRANTS_PROJECT_DIRECTOR = "Contracts & Grants Project Director";
        public static final String FISCAL_OFFICER_KIM_ROLE_NAME = "Fiscal Officer";
        public static final String FISCAL_OFFICER_PRIMARY_DELEGATE_KIM_ROLE_NAME = "Fiscal Officer Primary Delegate";
        public static final String FISCAL_OFFICER_SECONDARY_DELEGATE_KIM_ROLE_NAME = "Fiscal Officer Secondary Delegate";
        public static final String AWARD_SECONDARY_DIRECTOR_KIM_ROLE_NAME = "Award Project Director";
        public static final String ACTIVE_FACULTY_OR_STAFF_KIM_ROLE_NAME = "Active Faculty or Staff";
        public static final String ACTIVE_PROFESSIONAL_EMPLOYEE_KIM_ROLE_NAME = "Active Professional Employee";
        public static final String ACTIVE_EMPLOYEE_AND_KFS_USER_KIM_ROLE_NAME = "Active Employee & Financial System User";
        public static final String ACTIVE_PROFESSIONAL_EMPLOYEE_AND_KFS_USER_KIM_ROLE_NAME = "Active Professional Employee & Financial System User";
        public static final String CHART_MANAGER_KIM_ROLE_NAME = "UA Chart Manager";
        public static final String ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE = CoreModuleNamespaces.KFS;
        public static final String ACCOUNTING_REVIEWER_ROLE_NAMESPACECODE = CoreModuleNamespaces.KFS;
        public static final String ACCOUNTING_REVIEWER_ROLE_NAME = "Accounting Reviewer";
        public static final String ORGANIZATION_REVIEWER_ROLE_NAME = "Organization Reviewer";
        public static final String ACCOUNTS_RECEIVABLE_COLLECTOR = "CGB Collector";
        public static final String KFS_USER_ROLE_NAME = "User";
        public static final String CONTRACTS_AND_GRANTS_PROCESSOR = "Contracts & Grants Processor";
        public static final String SUB_FUND_REVIEWER = "Sub-Fund Reviewer";
        public static final String ORGANIZATION_FUND_REVIEWER_ROLE_NAME = "Organization Fund Reviewer";
    }

    public class RouteLevelNames {
        public static final String ACCOUNT = "Account";
        public static final String ACCOUNTING_ORGANIZATION_HIERARCHY = "AccountingOrganizationHierarchy";
        public static final String ACCOUNT_REVIEW_FULL_EDIT = "AccountFullEdit";
        public static final String PROJECT_MANAGEMENT = "ProjectManagement";
        public static final String ORGANIZATION_HIERARCHY = "OrganizationHierarchy";
        public static final String PAYMENT_METHOD = "PaymentMethod";
        public static final String ORGANIZATION_FUND_REVIEW = "OrganizationFundReview";
    }

    public static class COAConstants {
        public static final String ORG_REVIEW_ROLE_ORG_ACC_ONLY_CODE = "A";
        public static final String ORG_REVIEW_ROLE_ORG_ACC_ONLY_TEXT = "Organization Accounting Only";
        public static final String ORG_REVIEW_ROLE_ORG_ONLY_CODE = "O";
        public static final String ORG_REVIEW_ROLE_ORG_ONLY_TEXT = "Organization Only";
        public static final String ORG_REVIEW_ROLE_ORG_FUND_ONLY_CODE = "F";
        public static final String ORG_REVIEW_ROLE_ORG_FUND_ONLY_TEXT = "Organization Fund Only";
        public static final String ORG_REVIEW_ROLE_ORG_ACC_BOTH_CODE = "B";
        public static final String ORG_REVIEW_ROLE_ORG_ACC_BOTH_TEXT = "Both";
        public static final String ORG_REVIEW_ROLE_CREATE_DELEGATION_DISPLAY_TEXT = "create delegation";

        public final static String DEFAULT_CHART_METHOD = "1";
        public final static String DEFAULT_PRIMARY_DEPT_METHOD = "2";
        public final static String DEFAULT_PRIMARY_DEPT_CHART_METHOD = "3";

    }

    public static class Authorization {
        public static String PAYMENT_METHOD_EDIT_MODE = "paymentMethodEditMode";
    }

    public static class GeneralErrorCorrectionDocumentConstants {
    	public static final String GENERATE_ERROR_CERTIFICATION_STMT_ID = "GenerateErrorCertStmt";
    }
    
    public static class ErrorCertificationConstants {
    	public static final int NUM_ERROR_CERT_FIELDS = 4;
    }
    
    public static class GeneralErrorCorrectionEditMode {
    	public static final String ERROR_CERTIFICATE_TAB_ENTRY = "errorCertTabEntry";
    }
}
