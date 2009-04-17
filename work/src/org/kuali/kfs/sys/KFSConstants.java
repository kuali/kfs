/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.sys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.util.JSTLConstants;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.KualiInteger;

/**
 * This class is used to define global constants.
 */
public class KFSConstants extends JSTLConstants implements ParameterKeyConstants {
    private static final long serialVersionUID = 2882277719647128949L;

    public static class ParameterNamespaces {
        public static final String KFS = "KFS-SYS";
        public static final String CHART = "KFS-COA";
        public static final String FINANCIAL = "KFS-FP";
        public static final String GL = "KFS-GL";
        public static final String VENDOR = "KFS-VND";
        public static final String PDP = "KFS-PDP";
        public static final String KNS = KNSConstants.KNS_NAMESPACE;
    }
    
    public static class DocumentTypeAttributes {
        public static final String INDICATOR_ATTRIBUTE_TRUE_VALUE = "Y";

        public static final String TRANSACTION_SCRUBBER_OFFSET_INDICATOR_ATTRIBUTE_KEY = "TRANSACTION_SCRUBBER_OFFSET_GENERATION";
    }
    
    // special user used in the post-processor
    public static final String SYSTEM_USER = "kfs";

    public static final String ENVIRONMENT_KEY = "environment";
    public static final String VERSION_KEY = "version";
    public static final String LOG4J_SETTINGS_FILE_KEY = "log4j.settings.file";
    public static final String LOGS_DIRECTORY_KEY = "logs.directory";
    public static final String LOG4J_RELOAD_MINUTES_KEY = "log4j.reload.minutes";
    public static final String APPLICATION_URL_KEY = "application.url";
    public static final String ATTACHMENTS_DIRECTORY_KEY = "attachments.directory";
    public static final String ATTACHMENTS_PENDING_DIRECTORY_KEY = "attachments.pending.directory";
    public static final String HTDOCS_LOGS_URL_KEY = "htdocs.logs.url";
    public static final String HTDOCS_STAGING_URL_KEY = "htdocs.staging.url";
    public static final String HTDOCS_REPORTS_URL_KEY = "htdocs.reports.url";
    public static final String STAGING_DIRECTORY_KEY = "staging.directory";
    public static final String TEMP_DIRECTORY_KEY = "temp.directory";
    public static final String EXTERNALIZABLE_HELP_URL_KEY = "externalizable.help.url";
    public static final String EXTERNALIZABLE_IMAGES_URL_KEY = "externalizable.images.url";
    public static final String EXTERNALIZABLE_XML_URL_KEY = "externalizable.xml.url";
    public static final String RICE_EXTERNALIZABLE_IMAGES_URL_KEY = "kr.externalizable.images.url";
    public static final String REPORTS_DIRECTORY_KEY = "reports.directory";
    public static final String WORKFLOW_URL_KEY = "workflow.url";
    public static final String PROD_ENVIRONMENT_CODE_KEY = "production.environment.code";
    public static final String MAINTAIN_USERS_LOCALLY_KEY = "maintain.users.locally";
    public static final String MAINTAIN_KFS_USERS_LOCALLY_KEY = "maintain.kfs.users.locally";
    public static final String USE_STANDALONE_WORKFLOW = "rice.use.standalone.workflow";

    public static final String DATABASE_REPOSITORY_FILES_LIST_NAME = "databaseRepositoryFilePaths";
    public static final String SCRIPT_CONFIGURATION_FILES_LIST_NAME = "scriptConfigurationFilePaths";
    public static final String JOB_NAMES_LIST_NAME = "jobNames";
    public static final String TRIGGER_NAMES_LIST_NAME = "triggerNames";

    public static final String LOOKUP_RESULTS_LIMIT_URL_KEY = "RESULTS_LIMIT";
    public static final String DOCHANDLER_DO_URL = "/DocHandler.do?docId=";
    public static final String DOCHANDLER_URL_CHUNK = "&command=displayDocSearchView";

    public static final String ACCOUNT_NUMBER_PROPERTY_NAME = "accountNumber";
    public static final String MODULE_ID_PROPERTY_NAME = "moduleId";
    public static final String MODULE_CODE_PROPERTY_NAME = "moduleCode";
    public static final String ACCOUNT_STATUS_CLOSED = "Y";
    public static final String ACCOUNTING_PERIOD_ACTIVE_INDICATOR_FIELD = "active";
    public static final String ACTION_FORM_UTIL_MAP_METHOD_PARM_DELIMITER = "~";
    public static final String ADD_LINE_METHOD = "addLine";
    public static final String ADD_PREFIX = "add";
    public static final String ACTIVE_INDICATOR = "Y";
    public static final String AGGREGATE_ENCUMBRANCE_BALANCE_TYPE_CODE = "EN";
    public static final String AMOUNT_PROPERTY_NAME = "amount";
    public static final String APPROVE_METHOD = "approve";
    public static final String NON_ACTIVE_INDICATOR = "N";
    public static final String BLANK_SPACE = " ";
    public static final String BACK_LOCATION = "backLocation";
    public static final String BACKDOOR_PARAMETER = "backdoorId";
    public static final String BALANCE_INQUIRY_REPORT_MENU_ACTION = "balanceInquiryReportMenu.do";
    public static final String BALANCE_TYPE_PROPERTY_NAME = "balanceTypeCode";
    public static final String BALANCE_TYPE_CURRENT_BUDGET = "CB";
    public static final String BALANCE_TYPE_BASE_BUDGET = "BB";
    public static final String BALANCE_TYPE_MONTHLY_BUDGET = "MB";
    public static final String BALANCE_TYPE_EXTERNAL_ENCUMBRANCE = "EX";
    public static final String BALANCE_TYPE_INTERNAL_ENCUMBRANCE = "IE";
    public static final String BALANCE_TYPE_COST_SHARE_ENCUMBRANCE = "CE";
    public static final String BALANCE_TYPE_ACTUAL = "AC";
    public static final String BALANCE_TYPE_AUDIT_TRAIL = "NB";
    public static final String BALANCE_TYPE_A21 = "A2";
    public static final String BALANCE_TYPE_PRE_ENCUMBRANCE = "PE";
    public static final String BLANKET_APPROVE_METHOD = "blanketApprove";
    public static final String BUSINESS_OBJECT_CLASS_ATTRIBUTE = "businessObjectClassName";
    public static final String CALLING_METHOD = "caller";
    public static final String CASH_MANAGEMENT_DOCUMENT_ACTION = "financialCashManagement.do";
    public static final String CHANGE_JOURNAL_VOUCHER_BALANCE_TYPE_METHOD = "changeBalanceType";
    public static final String CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME = "chartOfAccountsCode";
    public static final String CONFIRMATION_QUESTION = "confirmationQuestion";
    public static final String CONSOLIDATED_SUBACCOUNT = "*ALL*";
    public static final String CONVERSION_FIELDS_PARAMETER = "conversionFields";
    public static final String LOOKUP_READ_ONLY_FIELDS = "readOnlyFields";
    public static final String LOOKUP_AUTO_SEARCH = "autoSearch";
    public static final String CREDIT_AMOUNT_PROPERTY_NAME = "newSourceLineCredit";
    public static final String DEBIT_AMOUNT_PROPERTY_NAME = "newSourceLineDebit";
    public static final String DELETE_LINE_METHOD = "deleteLine";
    public static final String DICTIONARY_BO_NAME = "dictionaryBusinessObjectName";
    public static final String DISBURSEMENT_VOUCHER_PDP_EXTRACT_FILE_NAME = "extr_fr_disb_voucher";
    public static final String DISENCUMBRANCE = "Disencumbrance";
    public static final String DISPATCH_REQUEST_PARAMETER = "methodToCall";
    public static final String DOC_FORM_KEY = "docFormKey";
    public static final String FORM_KEY = "formKey";
    public static final String BALANCE_INQUIRY_REPORT_MENU_CALLER_DOC_FORM_KEY = "balanceInquiryReportMenuCallerDocFormKey";
    public static final String DOCUMENT_CANCEL_QUESTION = "DocCancel";
    public static final String DOCUMENT_DELETE_QUESTION = "DocDelete";
    public static final String DOCUMENT_DISAPPROVE_QUESTION = "DocDisapprove";
    public static final String DOCUMENT_HEADER_ID = "documentHeaderId";
    public static final String DOCUMENT_HEADER_DOCUMENT_STATUS_CODE_PROPERTY_NAME = "financialDocumentStatusCode";
    public static final String NOTE_TEXT_PROPERTY_NAME = "noteText";
    public static final String DOCUMENT_HEADER_PROPERTY_NAME = "documentHeader";
    public static final String DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION = "DocSaveBeforeClose";
    public static final String EMPLOYEE_ACTIVE_STATUS = "A";
    public static final String EXISTING_SOURCE_ACCT_LINE_PROPERTY_NAME = "sourceAccountingLine";
    public static final String EXISTING_TARGET_ACCT_LINE_PROPERTY_NAME = "targetAccountingLine";
    public static final String SOURCE_ACCT_LINE_TYPE_CODE = "F"; // F = From, the label for this on most documents
    public static final String TARGET_ACCT_LINE_TYPE_CODE = "T"; // T = To, the label for this on most documents
    public static final String EXTRA_BUTTON_SOURCE = "extraButtonSource";
    public static final String EXTRA_BUTTON_PARAMS = "extraButtonParams";
    public static final String NEW_DOCUMENT_NOTE_PROPERTY_NAME = "newDocumentNote";
    public static final String NEW_AD_HOC_ROUTE_PERSON_PROPERTY_NAME = "newAdHocRoutePerson";
    public static final String NEW_AD_HOC_ROUTE_WORKGROUP_PROPERTY_NAME = "newAdHocRouteWorkgroup";
    public static final String EXISTING_AD_HOC_ROUTE_PERSON_PROPERTY_NAME = "adHocRoutePerson";
    public static final String EXISTING_AD_HOC_ROUTE_WORKGROUP_PROPERTY_NAME = "adHocRouteWorkgroup";
    public static final String NEW_SOURCE_ACCT_LINE_PROPERTY_NAME = "newSourceLine";
    public static final String NEW_TARGET_ACCT_LINES_PROPERTY_NAME = "newTargetLines";
    public static final String NEW_TARGET_ACCT_LINE_PROPERTY_NAME = "newTargetLine";
    public static final String DOCUMENT_PROPERTY_NAME = "document";
    public static final String DOCUMENT_TYPE_NAME = "docTypeName";
    public static final String EDIT_PREFIX = "edit";
    public static final String DASH = "-";
    public static final String EMPTY_STRING = "";
    public static final String ENCUMBRANCE = "Encumbrance";
    public static final String EXPENSE = "Expense";
    public static final String FIELD_CONVERSION_PAIR_SEPERATOR = ":";
    public static final String FIELD_CONVERSIONS_SEPERATOR = ",";
    public static final String REFERENCES_TO_REFRESH_SEPARATOR = ",";
    public static final String FIELD_CONVERSION_PREFIX_PARAMETER = "fieldConversionPrefix";
    public static final String FINANCIAL_OBJECT_CODE_PROPERTY_NAME = "financialObjectCode";
    public static final String FINANCIAL_OBJECT_LEVEL_CODE_PROPERTY_NAME = "financialObjectLevelCode";
    public static final String FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME = "financialSubObjectCode";
    public static final String FISCAL_CHART_NAME = "fiscalChartOfAccountsCode";
    public static final String FISCAL_ORG_NAME = "fiscalOrganizationCode";
    public static final String FROM = "From";
    public static final String GENERIC_FIELD_NAME = "Field";
    public static final String GENERIC_CODE_PROPERTY_NAME = "code";
    public static final String GL_BALANCE_INQUIRY_FLAG = "inquiryFlag";
    public static final String GL_ACCOUNT_BALANCE_BY_CONSOLIDATION_LOOKUP_ACTION = "glAccountBalanceByConsolidationLookup.do";
    public static final String AR_CUSTOMER_AGING_REPORT_LOOKUP_ACTION = "arCustomerAgingReportLookup.do";
    public static final String GL_BALANCE_INQUIRY_ACTION = "glBalanceInquiry.do";
    public static final String GL_MODIFIED_INQUIRY_ACTION = "glModifiedInquiry.do";
    public static final String GL_PE_OFFSET_STRING = "TP Generated Offset";
    public static final String SUB_OBJECT_CODE_PROPERTY_NAME = "subObjectCode";
    public static final String UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME = "universityFiscalYear";
    public static final String UNIVERSITY_FISCAL_PERIOD_CODE_PROPERTY_NAME = "universityFiscalPeriodCode";
    public static final String FINANCIAL_BALANCE_TYPE_CODE_PROPERTY_NAME = "financialBalanceTypeCode";
    public static final String ACCOUNT_SUFFICIENT_FUNDS_CODE_PROPERTY_NAME = "accountSufficientFundsCode";
    public static final String CURRENT_BUDGET_BALANCE_AMOUNT_PROPERTY_NAME = "currentBudgetBalanceAmount";
    public static final String ACCOUNT_ENCUMBRANCE_AMOUNT_PROPERTY_NAME = "accountEncumbranceAmount";
    public static final String TRANSACTION_DEBIT_CREDIT_CODE = "transactionDebitCreditCode";
    public static final String TRANSACTION_LEDGER_ENTRY_AMOUNT = "transactionLedgerEntryAmount";
    public static final String ACCOUNT_SUFFICIENT_FUNDS_FINANCIAL_OBJECT_CODE_PROPERTY_NAME = "acctSufficientFundsFinObjCd";
    public static final String FINANCIAL_OBJECT_TYPE_CODE = "financialObjectTypeCode";
    public static final String FINANCIAL_DOCUMENT_TYPE_CODE = "financialDocumentTypeCode";
    public static final String ORGANIZATION_CODE_PROPERTY_NAME = "organizationCode";
    public static final String ORIGIN_CODE_KUALI = "01";
    public static final String TRANSFER_FUNDS = "TF";
    public static final String[] ENCUMBRANCE_BALANCE_TYPE = new String[] { BALANCE_TYPE_EXTERNAL_ENCUMBRANCE, BALANCE_TYPE_INTERNAL_ENCUMBRANCE, BALANCE_TYPE_PRE_ENCUMBRANCE };
    public static final String LABOR_DISTRIBUTION_ORIGIN_CODE = "LD";
    public static final String LABOR_MODULE_CODE = "KFS-LD";
    public static final String STAND_IN_BUSINESS_OBJECT_FOR_ATTRIBUTES = "GenericAttributes";
    public static final String LABOR_MODIFIED_INQUIRY_ACTION = "laborModifiedInquiry.do";
    public static final String LABOR_A2_BALANCE_TYPE = "A2";
    public static final String EMPLOYEE_FUNDING_INQUIRY_ACTION = "employeeFundingInquiry.do";
    public static final String OVERRIDE_KEYS = "overrideKeys";
    public static final String[] LLCP_GROUP_FILTER_EXCEPTION = new String[] { "LLGL" };
    public static final String PERCENTAGE_SIGN = "%";
    public static final String RICE_PATH_PREFIX = "kr/";
    public static final String USE_CONTINUATION_BANK_QUESTION = "UseContinuationBankQuestion";
    public static final String NEW_WINDOW_URL_TARGET = "_blank";
    public static final String AND = "and";
    public static final String COMMA = ",";
    public static final String PERFORMANCE_BALANCE_INQUIRY_FOR_METHOD = "performBalanceInquiryFor";
    public static final String INSERT_METHOD = "insert";
    public static final String SOURCE_ACCOUNTING_LINES_GROUP_NAME = "source";
    public static final String TARGET_ACCOUNTING_LINES_GROUP_NAME = "target";

    /**
     * This value denotes that a max length has not been defined for a given lookup results field
     */
    public static final int LOOKUP_RESULT_FIELD_MAX_LENGTH_NOT_DEFINED = -1;

    /**
     * The number of levels BusinessObjectDictionaryServiceImpl will recurse. If this number is high, it may lead to serious
     * performance problems
     */
    public static final int BUSINESS_OBJECT_DICTIONARY_SERVICE_PERFORM_FORCE_UPPERCASE_RECURSION_MAX_DEPTH = 3;


    /**
     * When checkboxes are rendered on the form, a hidden field will also be rendered corresponding to each checkbox with the
     * checkbox's name suffixed with the value of this constant. No real fields should have names that contain this suffix, since
     * this may lead to undesired results.
     */
    public static final String CHECKBOX_PRESENT_ON_FORM_ANNOTATION = "{CheckboxPresentOnFormAnnotation}";

    public static class OrgReversion {
        public static final String VALID_PREFIX = "EXTENDED_DEFINITIONS_INCLUDE_";
        public static final String INVALID_PREFIX = "EXTENDED_DEFINITIONS_EXCLUDE_";
        public static final String OBJECT_CONSOL_PARAM_SUFFIX = "OBJECT_CONSOLIDATIONS_BY_ORGANIZATION_REVERSION_CATEGORY";
        public static final String OBJECT_LEVEL_PARAM_SUFFIX = "OBJECT_LEVELS_BY_ORGANIZATION_REVERSION_CATEGORY";
        public static final String OBJECT_TYPE_PARAM_SUFFIX = "OBJECT_TYPES_BY_ORGANIZATION_REVERSION_CATEGORY";
        public static final String OBJECT_SUB_TYPE_PARAM_SUFFIX = "OBJECT_SUB_TYPES_BY_ORGANIZATION_REVERSION_CATEGORY";
        public static final String IS_EXPENSE_PARAM = "EXTENDED_DEFINITIONS_EXPENSE_CATEGORIES";
    }

    // CR doc properties
    public static final String NEW_CHECK_PROPERTY_NAME = "newCheck";
    public static final String EXISTING_CHECK_PROPERTY_NAME = "check";

    public static final int DOCUMENT_ANNOTATION_MAX_LENGTH = 2000;

    // TRN_LDGR_DEBIT_CRDT_CD valid values
    public static final String GL_DEBIT_CODE = "D";
    public static final String GL_CREDIT_CODE = "C";
    public static final String GL_BUDGET_CODE = " ";

    // TRN_ENCUM_UPDT_CD value values
    public static final String ENCUMB_UPDT_DOCUMENT_CD = "D";
    public static final String ENCUMB_UPDT_REFERENCE_DOCUMENT_CD = "R";
    public static final String ENCUMB_UPDT_NO_ENCUMBRANCE_CD = "N";

    // GL Reversal Generated Entry Description Prefix
    public static final String GL_REVERSAL_DESCRIPTION_PREFIX = "AUTO REVERSAL-";

    // Misc GL text.
    public static final String PLANT_INDEBTEDNESS_ENTRY_DESCRIPTION = "GENERATED TRANSFER TO NET PLANT";

    // Sufficient Funds Type Codes
    public static final String SF_TYPE_NO_CHECKING = "N";
    public static final String SF_TYPE_OBJECT = "O";
    public static final String SF_TYPE_LEVEL = "L";
    public static final String SF_TYPE_CONSOLIDATION = "C";
    public static final String SF_TYPE_CASH_AT_ACCOUNT = "H";
    public static final String SF_TYPE_ACCOUNT = "A";

    public static final String BUDGET_CHECKING_OPTIONS_CD_ACTIVE = "Y";

    public static final String GRANT = "Grant";
    public static final String HIDE_LOOKUP_RETURN_LINK = "hideReturnLink";
    public static final String SUPPRESS_ACTIONS = "suppressActions";
    public static final String SHOW_MAINTENANCE_LINKS = "showMaintenanceLinks";
    public static final String REFERENCES_TO_REFRESH = "referencesToRefresh";
    public static final String INCOME = "Income";
    public static final String INITIAL_KUALI_DOCUMENT_STATUS_CD = "?";
    public static final String INSERT_SOURCE_LINE_METHOD = "insertSourceLine";
    public static final String INSERT_TARGET_LINE_METHOD = "insertTargetLine";
    public static final String ICR = "Receipt";
    public static final String PARM_SECTION_NAME_FIELD = "FS_SCR_NM";
    public static final String PARM_PARM_NAME_FIELD = "FS_PARM_NM";
    public static final String PROJECT_CODE_PROPERTY_NAME = "projectCode";

    public static final String INQUIRABLE_ATTRIBUTE_NAME = "kualiInquirable";
    public static final String INQUIRY_ACTION = "kr/inquiry.do";
    public static final String INQUIRY_IMPL_ATTRIBUTE_NAME = "inquirableImplServiceName";
    public static final String JOURNAL_VOUCHER_CHANGE_BALANCE_TYPE_QUESTION = "JournalVoucherChangeBalanceTypeQuestion";
    public static final String JOURNAL_VOUCHER_ROUTE_OUT_OF_BALANCE_DOCUMENT_QUESTION = "JournalVoucherRouteOutOfBalanceDocumentQuestion";
    public static final String JOURNAL_VOUCHER_ENCUMBRANCE_UPDATE_CODE_BALANCE_TYPE_EXTERNAL_ENCUMBRANCE = "R";
    public static final String JOURNAL_LINE_HELPER_PROPERTY_NAME = "journalLineHelper";
    public static final String AUXILIARY_LINE_HELPER_PROPERTY_NAME = "auxiliaryLineHelper";
    public static final String VOUCHER_LINE_HELPER_CREDIT_PROPERTY_NAME = ".credit";
    public static final String VOUCHER_LINE_HELPER_DEBIT_PROPERTY_NAME = ".debit";
    public static final String KUALI_WORKFLOW_APPLICATION_CODE = "kuali";
    public static final String LOOKUP_ACTION = "kr/lookup.do";
    public static final String LOOKUP_RESULTS_SEQUENCE_NUMBER = "lookupResultsSequenceNumber";
    public static final String LOOKUP_RESULTS_BO_CLASS_NAME = "lookupResultsBOClassName";
    public static final String LOOKED_UP_COLLECTION_NAME = "lookedUpCollectionName";
    public static final String MULTIPLE_VALUE_LOOKUP_PREVIOUSLY_SELECTED_OBJ_IDS_PARAM = "previouslySelectedObjectIds";
    public static final String MULTIPLE_VALUE_LOOKUP_OBJ_IDS_SEPARATOR = "||";
    public static final String MULTIPLE_VALUE_LOOKUP_DISPLAYED_OBJ_ID_PARAM_PREFIX = "displayedObjId-";
    public static final String MULTIPLE_VALUE_LOOKUP_SELECTED_OBJ_ID_PARAM_PREFIX = "selectedObjId-";
    public static final String LOOKUP_ANCHOR = "lookupAnchor";
    public static final String LOOKUPABLE_IMPL_ATTRIBUTE_NAME = "lookupableImplServiceName";
    public static final String LOOKUPABLE_SUFFIX = "Lookupable";
    public static final String KUALI_LOOKUPABLE_IMPL = "kualiLookupable";
    public static final String KUALI_VENDOR_LOOKUPABLE_IMPL = "vendorLookupable";
    public static final String KUALI_USER_LOOKUPABLE_IMPL = "personLookupable";
    public static final String KUALI_DISBURSEMENT_PAYEE_LOOKUPABLE_IMPL = "disbursementPayeeLookupable";
    public static final String KUALI_VENDOR_ADDRESS_LOOKUPABLE_IMPL = "vendorAddressLookupable";
    public static final String DOC_HANDLER_ACTION = "DocHandler.do";
    public static final String DOC_HANDLER_METHOD = "docHandler";
    public static final String PARAMETER_DOC_ID = "docId";
    public static final String PARAMETER_COMMAND = "command";
    public static final String LOOKUP_METHOD = "performLookup";
    public static final String METHOD_DISPLAY_DOC_SEARCH_VIEW = "displayDocSearchView";
    public static final String MAINTENANCE_ACTION = "maintenance.do";
    public static final String MAINTENANCE_ADD_PREFIX = "add.";
    public static final String MAINTENANCE_COPY_ACTION = "Copy";
    public static final String MAINTENANCE_EDIT_ACTION = "Edit";
    public static final String MAINTENANCE_NEW_ACTION = "New";
    public static final String MAINTENANCE_COPY_METHOD_TO_CALL = "copy";
    public static final String MAINTENANCE_EDIT_METHOD_TO_CALL = "edit";
    public static final String MAINTENANCE_NEW_METHOD_TO_CALL = "start";
    public static final String MAINTENANCE_NEWWITHEXISTING_ACTION = "newWithExisting";
    public static final String MAINTENANCE_NEW_MAINTAINABLE = "document.newMaintainableObject.";
    public static final String MAINTENANCE_OLD_MAINTAINABLE = "document.oldMaintainableObject.";
    public static final String MAPPING_BASIC = "basic";
    public static final String MAPPING_CANCEL = "cancel";
    public static final String MAPPING_CLOSE = "close";
    public static final String MAPPING_DISAPPROVE = "disapprove";
    public static final String MAPPING_DELETE = "delete";
    public static final String MAPPING_ERROR = "error";
    public static final String MAPPING_PORTAL = "portal";
    public static final String MAPPING_MULTIPLE_VALUE_LOOKUP = "multipleValueLookup";
    public static final String MAPPING_BALANCE_INQUIRY_REPORT_MENU = "balanceInquiryReportMenu";
    public static final String MAPPING_DV_PER_DIEM_LINKS = "dvPerDiemLinks";
    public static final String MAXLENGTH_SUFFIX = ".maxLength";
    public static final String METHOD_TO_CALL_ATTRIBUTE = "methodToCallAttribute";
    public static final String METHOD_TO_CALL_PATH = "methodToCallPath";
    public static final String METHOD_TO_CALL_BOPARM_LEFT_DEL = "(!!";
    public static final String METHOD_TO_CALL_BOPARM_RIGHT_DEL = "!!)";
    public static final String METHOD_TO_CALL_PARM1_LEFT_DEL = "(((";
    public static final String METHOD_TO_CALL_PARM1_RIGHT_DEL = ")))";
    public static final String METHOD_TO_CALL_PARM2_LEFT_DEL = "((#";
    public static final String METHOD_TO_CALL_PARM2_RIGHT_DEL = "#))";
    public static final String METHOD_TO_CALL_PARM3_LEFT_DEL = "((<";
    public static final String METHOD_TO_CALL_PARM3_RIGHT_DEL = ">))";
    public static final String METHOD_TO_CALL_PARM4_LEFT_DEL = "(([";
    public static final String METHOD_TO_CALL_PARM4_RIGHT_DEL = "]))";
    public static final String METHOD_TO_CALL_PARM5_LEFT_DEL = "((*";
    public static final String METHOD_TO_CALL_PARM5_RIGHT_DEL = "*))";
    public static final String METHOD_TO_CALL_PARM6_LEFT_DEL = "((%";
    public static final String METHOD_TO_CALL_PARM6_RIGHT_DEL = "%))";
    public static final String METHOD_TO_CALL_PARM7_LEFT_DEL = "((^";
    public static final String METHOD_TO_CALL_PARM7_RIGHT_DEL = "^))";
    public static final String METHOD_TO_CALL_PARM8_LEFT_DEL = "((&";
    public static final String METHOD_TO_CALL_PARM8_RIGHT_DEL = "&))";
    public static final String METHOD_TO_CALL_PARM9_LEFT_DEL = "((~";
    public static final String METHOD_TO_CALL_PARM9_RIGHT_DEL = "~))";
    public static final String METHOD_TO_CALL_PARM10_LEFT_DEL = "((/";
    public static final String METHOD_TO_CALL_PARM10_RIGHT_DEL = "/))";
    public static final String METHOD_TO_CALL_PARM11_LEFT_DEL = "(:;";
    public static final String METHOD_TO_CALL_PARM11_RIGHT_DEL = ";:)";
    public static final String METHOD_TO_CALL_PARM12_LEFT_DEL = "(::;";
    public static final String METHOD_TO_CALL_PARM12_RIGHT_DEL = ";::)";
    public static final String METHOD_TO_CALL_PARM13_LEFT_DEL = "(:::;";
    public static final String METHOD_TO_CALL_PARM13_RIGHT_DEL = ";:::)";
    // if more strings needed, then add more colons to the PARM11 strings above, e.g. (::; (:::;, etc.

    public static final String ACTION_EXTENSION_DOT_DO = ".do";
    public static final String ANCHOR = "anchor";
    public static final String ANCHOR_TOP_OF_FORM = "topOfForm";
    public static final String QUESTION_ANCHOR = "questionAnchor";
    public static final String NOT_AVAILABLE_STRING = "N/A";
    public static final int NEGATIVE_ONE = -1;
    public static final String OBJECT_CODE_STATUS_ACTIVE = "Y";
    public static final String OBJECT_TYPE_CODE_PROPERTY_NAME = "objectTypeCode";
    public static final String CONTEXT_PATH = "contextPath";
    public static final String QUESTION_ACTION = "questionPrompt.do";
    public static final String QUESTION_CLICKED_BUTTON = "buttonClicked";
    public static final String QUESTION_ERROR_KEY = "questionErrorKey";
    public static final String QUESTION_ERROR_PROPERTY_NAME = "questionErrorPropertyName";
    public static final String QUESTION_ERROR_PARAMETER = "questionErrorParameter";
    public static final String QUESTION_IMPL_ATTRIBUTE_NAME = "questionType";
    public static final String QUESTION_INST_ATTRIBUTE_NAME = "questionIndex";
    public static final String QUESTION_PAGE_TITLE = "Question Dialog Page";
    public static final String QUESTION_REFRESH = "QuestionRefresh";
    public static final String QUESTION_CONTEXT = "context";
    public static final String QUESTION_TEXT_ATTRIBUTE_NAME = "questionText";
    public static final String QUESTION_REASON_ATTRIBUTE_NAME = "reason";
    public static final String QUESTION_SHOW_REASON_FIELD = "showReasonField";
    public static final String RELOAD_METHOD_TO_CALL = "reload";
    public static final String REFRESH_CALLER = "refreshCaller";
    public static final String REFRESH_MAPPING_PREFIX = "/Refresh";
    public static final String REQUIRED_FIELD_SYMBOL = "*";
    public static final String RETURN_LOCATION_PARAMETER = "returnLocation";
    public static final String RETURN_METHOD_TO_CALL = "refresh";
    public static final String ROUTE_METHOD = "route";
    public static final String SAVE_METHOD = "save";
    public static final String START_METHOD = "start";
    public static final String SEARCH_METHOD = "search";
    public static final String COPY_METHOD = "copy";
    public static final String ERRORCORRECT_METHOD = "correct";
    public static final String SOURCE = "Source";
    public static final String SQUARE_BRACKET_LEFT = "[";
    public static final String SQUARE_BRACKET_RIGHT = "]";
    public static final String SUB_ACCOUNT_STATUS_ACTIVE = "Y";
    public static final String SUB_ACCOUNT_NUMBER_PROPERTY_NAME = "subAccountNumber";
    public static final String SUB_OBJECT_CODE_STATUS_ACTIVE = "Y";
    public static final String TARGET = "Target";
    public static final String TO = "To";
    public static final String USER_SESSION_KEY = "UserSession";
    public static final String VERSION_NUMBER = "versionNumber";

    public static final String SEARCH_LIST_KEY_PREFIX = "searchResults";
    public static final String SEARCH_LIST_REQUEST_KEY = "searchResultKey";

    public static final String CORRECTION_FORM_KEY = "correctionFormKey";
    public static final int CORRECTION_RECENT_GROUPS_DAY = 10;

    public static final String SEARCH_DATA_KEY_PREFIX = "dataSearchResults";
    public static final String SEARCH_DATA_REQUEST_KEY = "searchResultDataKey";


    public static final String GLOBAL_ERRORS = "GLOBAL_ERRORS";
    public static final String GLOBAL_MESSAGES = "GlobalMessages";
    public static final String AD_HOC_ROUTE_PERSON_ERRORS = "newAdHocRoutePerson*,adHocRoutePerson*";
    public static final String AD_HOC_ROUTE_WORKGROUP_ERRORS = "newAdHocRouteWorkgroup*,adHocRouteWorkgroup*";
    public static final String DOCUMENT_DOCUMENT_ERRORS = "document.document*";
    public static final String DOCUMENT_EXPLANATION_ERRORS = "document.explanation*";
    public static final String DOCUMENT_REVERSAL_ERRORS = "document.reversal*";
    public static final String DOCUMENT_SELECTED_ERRORS = "document.selected*";
    public static final String DOCUMENT_HEADER_ERRORS = "document.header*";
    public static final String DOCUMENT_ERRORS_LESS_DOCUMENT = DOCUMENT_EXPLANATION_ERRORS + "," + DOCUMENT_REVERSAL_ERRORS + "," + DOCUMENT_SELECTED_ERRORS + "," + DOCUMENT_HEADER_ERRORS;
    public static final String DOCUMENT_ERRORS = DOCUMENT_DOCUMENT_ERRORS + "," + DOCUMENT_EXPLANATION_ERRORS + "," + DOCUMENT_REVERSAL_ERRORS + "," + DOCUMENT_SELECTED_ERRORS + "," + DOCUMENT_HEADER_ERRORS;
    public static final String DOCUMENT_NOTES_ERRORS = "newDocumentNote*";
    public static final String BUDGET_PARAMETERS_ERRORS = "newBudgetParameters*";
    public static final String BUDGET_PERMISSIONS_ERRORS = "newBudgetPermissions*";
    public static final String BUDGET_SPREADSHEET_ERRORS = "newBudgetPermissions*";
    public static final String BUDGET_OUTPUT_ERRORS = "newBudgetPermissions*";
    public static final String BUDGET_COSTING_ERRORS = "newBudgetPermissions*";

    public enum NoteTypeEnum {
        BUSINESS_OBJECT_NOTE_TYPE("BO", "documentBusinessObject"), DOCUMENT_HEADER_NOTE_TYPE("DH", "documentHeader");
        private String noteTypeCode;
        private String noteTypePath;

        private NoteTypeEnum(String noteTypeCode, String noteTypePath) {
            this.noteTypeCode = noteTypeCode;
            this.noteTypePath = noteTypePath;
        }

        public String getCode() {
            return this.noteTypeCode;
        }

        public String getPath() {
            return this.noteTypePath;
        }

        public String getFullPath() {
            return KFSConstants.DOCUMENT_PROPERTY_NAME + "." + getPath();
        }
    }

    public static final String EDIT_JOURNAL_VOUCHER_ERRORS = "EditJournalVoucherErrors";
    public static final String EDIT_AUXILIARY_VOUCHER_ERRORS = "EditAuxiliaryVoucherErrors";
    public static final String EDIT_PRE_ENCUMBRANCE_ERRORS = "EditPreEncumbranceErrors";

    public static final String ACCOUNTING_LINE_ERRORS = "document.accountingLines";
    public static final String SOURCE_ACCOUNTING_LINE_ERROR_PATTERN = "document.sourceAccounting*,sourceAccountingLines,newSourceLine*,journalLineHelper*,auxiliaryLineHelper*";
    public static final String TARGET_ACCOUNTING_LINE_ERROR_PATTERN = "document.targetAccounting*,targetAccountingLines,newTargetLine*";
    public static final String ACCOUNTING_LINE_GROUP_SUFFIX = "s";
    public static final String SOURCE_ACCOUNTING_LINE_ERRORS = EXISTING_SOURCE_ACCT_LINE_PROPERTY_NAME + ACCOUNTING_LINE_GROUP_SUFFIX;
    public static final String TARGET_ACCOUNTING_LINE_ERRORS = EXISTING_TARGET_ACCT_LINE_PROPERTY_NAME + ACCOUNTING_LINE_GROUP_SUFFIX;
    public static final String ITEM_LINE_ERRORS = "newItem*,document.item*";
    public static final String CREDIT_CARD_RECEIPTS_LINE_ERRORS = "newCreditCardReceipt*,document.creditCardReceipt*";
    public static final String ADVANCE_DEPOSITS_LINE_ERRORS = "newAdvanceDeposit*,document.advanceDeposit*";
    public static final String GENERAL_LEDGER_PENDING_ENTRIES_TAB_ERRORS = "document.generalLedgerPendingEntr*";
    public static final String BUDGET_CONSTRUCTION_SALARY_SETTING_TAB_ERRORS = "document.budgetConstructionSalarySetting*";
    public static final String BUDGET_CONSTRUCTION_REVENUE_TAB_ERRORS = "document.budgetConstructionRevenue*";
    public static final String BUDGET_CONSTRUCTION_EXPENDITURE_TAB_ERRORS = "document.budgetConstructionExpenditure*";
    public static final String BUDGET_CONSTRUCTION_MONTHLY_BUDGET_ERRORS = "document.budgetConstructionMonthlyBudget*";

    public static final String CASHIER_CLOSING_DRAWER_ERRORS = "document.bursarControl*";
    public static final String AND_LOGICAL_OPERATOR = "&&";
    public static final String OR_LOGICAL_OPERATOR = "|";
    public static final String NOT_LOGICAL_OPERATOR = "!";
    // add AND operator to thest if it is uncommented below
    public static final String[] LOGICAL_OPERATORS = { OR_LOGICAL_OPERATOR, NOT_LOGICAL_OPERATOR };
    public static final String WILDCARD_CHARACTER = "*";
    public static final String[] QUERY_CHARACTERS = { WILDCARD_CHARACTER, "?", "%", ">", "<", "..", OR_LOGICAL_OPERATOR, NOT_LOGICAL_OPERATOR, "=" };
    public static final String WILDCARD_NOT_ALLOWED_ON_FIELD = "error.fieldDoNotAllowWildcard";

    // disbursement voucher error fields
    public static final String DV_PAYEE_TAB_ERRORS = "DVPayeeErrors,document.dvPayeeDetail.disbVchrPayeeIdNumber,document.dvPayeeDetail.disbVchrPayeeCityName,document.dvPayeeDetail.disbVchrPayeePersonName," + "document.dvPayeeDetail.disbVchrPayeeStateCode,document.dvPayeeDetail.disbVchrPayeeLine1Addr,document.dvPayeeDetail.disbVchrPayeeZipCode,document.dvPayeeDetail.disbVchrPayeeLine2Addr,document.dvPayeeDetail.disbVchrPayeeCountryCode,document.dvPayeeDetail.disbursementVoucherPayeeTypeCode,";
    public static final String DV_PAYEE_INIT_TAB_ERRORS = "DVPayeeErrors,payeeIdNumber,payeePersonName,payeeTypeCode";
    public static final String DV_PAYMENT_TAB_ERRORS = "DVPaymentErrors,document.dvPayeeDetail.disbVchrPaymentReasonCode,document.disbVchrCheckTotalAmount,document.disbursementVoucherDueDate,document.dvPayeeDetail.disbVchrAlienPaymentCode," + "document.dvPayeeDetail.disbVchrPayeeEmployeeCode,document.disbVchrAttachmentCode,document.disbVchrSpecialHandlingCode,document.disbVchrPayeeW9CompleteCode" + "document.disbVchrPaymentMethodCode,document.disbursementVoucherDocumentationLocationCode,document.disbVchrCheckStubText";
    public static final String DV_NRATAX_TAB_ERRORS = "DVNRATaxErrors,document.dvNonResidentAlienTax.incomeClassCode,document.dvNonResidentAlienTax.incomeTaxTreatyExemptCode,document.dvNonResidentAlienTax.federalIncomeTaxPercent," + "document.dvNonResidentAlienTax.foreignSourceIncomeCode,document.dvNonResidentAlienTax.stateIncomeTaxPercent,document.dvNonResidentAlienTax.incomeTaxGrossUpCode,document.dvNonResidentAlienTax.postalCountryCode," + "document.dvNonResidentAlienTax.referenceFinancialDocumentNumber";
    public static final String DV_FOREIGNDRAFTS_TAB_ERRORS = "DVForeignDraftErrors,document.dvWireTransfer.disbursementVoucherForeignCurrencyTypeCode,document.dvWireTransfer.disbursementVoucherForeignCurrencyTypeName";
    public static final String DV_CONTACT_TAB_ERRORS = "DVContactErrors,document.disbVchrContact*";
    public static final String DV_SPECHAND_TAB_ERRORS = "DVSpecialHandlingErrors,document.dvPayeeDetail.disbVchrSpecialHandlingPersonName,document.dvPayeeDetail.disbVchrSpecialHandlingCityName,document.dvPayeeDetail.disbVchrSpecialHandlingLine1Addr,document.dvPayeeDetail.disbVchrSpecialHandlingStateCode," + "document.dvPayeeDetail.disbVchrSpecialHandlingLine2Addr,document.dvPayeeDetail.disbVchrSpecialHandlingZipCode,document.dvPayeeDetail.disbVchrSpecialHandlingCountryName";
    public static final String DV_WIRETRANSFER_TAB_ERRORS = "DVWireTransfersErrors,document.dvWireTransfer.disbursementVoucherBankName,document.dvWireTransfer.disbVchrBankRoutingNumber,document.dvWireTransfer.disbVchrBankCityName,document.dvWireTransfer.disbVchrBankStateCode," + "document.dvWireTransfer.disbVchrBankCountryCode,document.dvWireTransfer.disbVchrAttentionLineText,document.dvWireTransfer.disbVchrAdditionalWireText,document.dvWireTransfer.disbVchrPayeeAccountNumber,document.dvWireTransfer.disbVchrCurrencyTypeName,document.dvWireTransfer.disbVchrCurrencyTypeCode," + "document.dvWireTransfer.disbursementVoucherWireTransferFeeWaiverIndicator,document.dvWireTransfer.disbursementVoucherPayeeAccountName,document.dvWireTransfer.disbursementVoucherPayeeAccountTypeCode,document.dvWireTransfer.disbursementVoucherAutomatedClearingHouseProfileNumber";
    public static final String DV_NON_EMPL_TRAVEL_TAB_ERRORS = "DVNonEmployeeTravelErrors,newPrePaidNonEmployeeExpenseLine.*,newNonEmployeeExpenseLine.*,document.dvNonEmployeeTravel.*";
    public static final String DV_PREPAID_TAB_ERRORS = "DVPrePaidTravelErrors,newPreConferenceRegistrantLine.*,document.dvPreConferenceDetail.*";
    public static final String PAYEE_W9_QUESTION = "PayeeW9Question";
    public static final String PAYEE_NAME_EXIST_QUESTION = "PayeeNameExistQuestion";
    public static final String COPY_NEW_PAYEE_ADDRESS_LINES = "NewPayeeCopyAddressLinesQuestion";
    public static final String COPY_CHANGE_PAYEE_ADDRESS_LINES = "ChangePayeeCopyAddressLinesQuestion";
    public static final String DV_PAYMENT_REASON_NONEMPLOYEE = "N";
    public static final String DV_PAYMENT_REASON_NONEMPLOYEE_HONORARIUM = "X";
    public static final String GENERAL_PAYEE_TAB_ERRORS = "DVPayeeErrors";
    public static final String GENERAL_PAYMENT_TAB_ERRORS = "DVPaymentErrors";
    public static final String GENERAL_NRATAX_TAB_ERRORS = "DVNRATaxErrors";
    public static final String GENERAL_FOREIGNDRAFTS_TAB_ERRORS = "DVForeignDraftErrors";
    public static final String GENERAL_CONTACT_TAB_ERRORS = "DVContactErrors";
    public static final String GENERAL_SPECHAND_TAB_ERRORS = "DVSpecialHandlingErrors";
    public static final String GENERAL_WIRETRANSFER_TAB_ERRORS = "DVWireTransfersErrors";
    public static final String GENERAL_PREPAID_TAB_ERRORS = "DVPrePaidTravelErrors";
    public static final String GENERAL_NONEMPLOYEE_TAB_ERRORS = "DVNonEmployeeTravelErrors,document.dvNonEmployeeTravel.totalTravelAmount";
    public static final String DV_PAYEE_ID_FIELD_NAME = "dvPayeeDetail.disbVchrPayeeIdNumber";
    public static final String DV_PAYMENT_REASON_FIELD_NAME = "dvPayeeDetail.disbVchrPaymentReasonCode";
    public static final String DV_CHECK_TRAVEL_TOTAL_ERROR = "document.dvNonEmployeeTravel.totalTravelAmount";

    // KRA-related constant values
    public static final KualiDecimal CONTRACTS_AND_GRANTS_FRINGE_RATE_MAX = new KualiDecimal("100.0");
    public static final KualiDecimal CONTRACTS_AND_GRANTS_COST_SHARE_MAX = new KualiDecimal("100.0");
    public static final KualiDecimal GRADUATE_ASSISTANT_RATE_MAX = new KualiDecimal("9999.99");
    public static final String AUDIT_ERRORS = "AuditErrors";

    // Header Tab navigation constant values
    public static final String NAVIGATE_TO = "navigateTo.";
    public static final String HEADER_DISPATCH = "headerDispatch.";

    // country
    public static final String COUNTRY_CODE_UNITED_STATES = "US";

    // CashManagement tab errors
    public static final String CASH_MANAGEMENT_DEPOSIT_ERRORS = "document.deposit*";

    // Coin and Currency Amounts
    public static class CoinTypeAmounts {
        public static final KualiDecimal HUNDRED_CENT_AMOUNT = new KualiDecimal(1.0);
        public static final KualiDecimal FIFTY_CENT_AMOUNT = new KualiDecimal(0.5);
        public static final KualiDecimal TWENTY_FIVE_CENT_AMOUNT = new KualiDecimal(0.25);
        public static final KualiDecimal TEN_CENT_AMOUNT = new KualiDecimal(0.1);
        public static final KualiDecimal FIVE_CENT_AMOUNT = new KualiDecimal(0.05);
        public static final KualiDecimal ONE_CENT_AMOUNT = new KualiDecimal(0.01);
    }

    public static class CurrencyTypeAmounts {
        public static final KualiDecimal HUNDRED_DOLLAR_AMOUNT = new KualiDecimal(100.0);
        public static final KualiDecimal FIFTY_DOLLAR_AMOUNT = new KualiDecimal(50.0);
        public static final KualiDecimal TWENTY_DOLLAR_AMOUNT = new KualiDecimal(20.0);
        public static final KualiDecimal TEN_DOLLAR_AMOUNT = new KualiDecimal(10.0);
        public static final KualiDecimal FIVE_DOLLAR_AMOUNT = new KualiDecimal(5.0);
        public static final KualiDecimal TWO_DOLLAR_AMOUNT = new KualiDecimal(2.0);
        public static final KualiDecimal ONE_DOLLAR_AMOUNT = new KualiDecimal(1.0);
    }

    // Cashiering source constants
    public static class CurrencyCoinSources {
        public static final String CASH_MANAGEMENT_IN = "R"; // money coming in through cashiering activity
        public static final String DEPOSITS = "D"; // money going out through deposits
        public static final String CASH_RECEIPTS = "C"; // money coming in through cash receipts
        public static final String CASH_MANAGEMENT_OUT = "O"; // money going out through cashiering activity
        public static final String CASH_MANAGEMENT_MASTER = "M"; // an amalgamation of a cashiering transaction
    }

    // Constants for check sources
    // Why are these constants different from the Currency/Coin constants?
    // Why, I ask you in return, is the sky blue? That's right, because of
    // the effect of Rayleigh scattering on atmospheric particles. That's why.
    public static class CheckSources {
        public static final String CASH_RECEIPTS = "R";
        public static final String CASH_MANAGEMENT = "I";
    }

    public static final String CASHIERING_TRANSACTION_OPEN_ITEM_IN_PROCESS_PROPERTY = "document.currentTransaction.openItemInProcess";

    // Tab error patterns must be at the top level; JSPs do not have access to the nested classes.
    public static final String EDIT_CASH_RECEIPT_CASH_RECONCILIATION_ERRORS = "document.totalCashAmount,document.totalCheckAmount,document.totalCoinAmount,document.sumTotalAmount";
    public static final String EDIT_CASH_RECEIPT_CHECK_DETAIL_ERRORS = "newCheck*,document.check*";
    public static final String EDIT_CASH_RECEIPT_CURRENCY_COIN_ERRORS = "document.currencyDetail.*,document.coinDetail.*";
    public static final String EDIT_CASH_MANAGEMENT_CASHIERING_TRANSACTION_ERRORS = "document.currentTransaction.*";
    public static final String EDIT_CAPITAL_ASSET_INFORMATION_ERRORS = "document.capitalAssetInformation*";
    
    public static final String MULTIPLE_VALUE = "multipleValues";
    public static final String MULTIPLE_VALUE_LABEL = "Lookup initial values";
    public static final String MULTIPLE_VALUE_NAME = "Multiple Value Name";

    // Agency type codes
    public static final String AGENCY_TYPE_CODE_FEDERAL = "F";

    // special chars that I don't know how to put into string literals in JSP expression language
    public static final String NEWLINE = "\n";

    // Workflow constants
    public static final String WORKFLOW_FYI_REQUEST = "F";
    public static final String WORKFLOW_APPROVE_REQUEST = "A";

    public static class DocumentStatusCodes {
        public static final String INITIATED = "?";
        public static final String CANCELLED = "X";
        public static final String ENROUTE = "R";
        public static final String DISAPPROVED = "D";
        public static final String APPROVED = "A";

        public static class CashReceipt {
            // once a CashReceipt gets approved, its financialDocumentStatus goes to "verified"
            public static final String VERIFIED = "V";

            // when a CashReceipt associated with a Deposit, its financialDocumentStatus changes to "interim" or "final"
            public static final String INTERIM = "I";
            public static final String FINAL = "F";

            // when the CMDoc is finalized, the CRs of its Deposits change to status "approved"
        }
    }

    public static class AdvanceDepositConstants {
        public static final String CASH_RECEIPT_ADVANCE_DEPOSIT_COLUMN_TYPE_CODE = "R";
    }

    public static class AuxiliaryVoucher {
        public static final String ADJUSTMENT_DOC_TYPE = "AVAD";
        public static final String ADJUSTMENT_DOC_TYPE_NAME = "Adjustment";
        public static final String RECODE_DOC_TYPE = "AVRC";
        public static final String RECODE_DOC_TYPE_NAME = "Recode";
        public static final String ACCRUAL_DOC_TYPE = "AVAE";
        public static final String ACCRUAL_DOC_TYPE_NAME = "Accrual";
        public static final String ERROR_DOCUMENT_RECODE_DISTRIBUTION_OF_INCOME_AND_EXPENSE_UNSUCCESSFUL = "Unable to auto-generate Distribution of Income and Expense for document with number \"%s.\" Please contact your System Administrator for a Distribution of Income and Expense to be created manually.";
        public static final String ERROR_DOCUMENT_HAS_TARGET_LINES = "AV document doesn't have target accounting lines. This method should have never been entered";
        public static final String RECODE_DISTRIBUTION_OF_INCOME_AND_EXPENSE_DESCRIPTION = "Auto-generated for Auxiliary Voucher";
        public static final String RECODE_DISTRIBUTION_OF_INCOME_AND_EXPENSE_EXPLANATION = "Auxiliary Voucher recode document type was chosen. A Distribution of Income And Expense needs to be routed to FINAL along with it. This Document is routed by Auxiliary Voucher \"%s\".";
        public static final String CHANGE_VOUCHER_TYPE = "changeVoucherType";
    }

    public static class CashDrawerConstants {
        public static final String STATUS_CLOSED = "C";
        public static final String STATUS_OPEN = "O";
        public static final String STATUS_LOCKED = "L";
    }

    public static class CashReceiptConstants {
        public static final String TEST_CASH_RECEIPT_VERIFICATION_UNIT = "HAWAII_CR_VERIFICATION_UNIT";
        public static final String TEST_CASH_RECEIPT_CAMPUS_LOCATION_CODE = "HI";

        public static final String DEFAULT_CASH_RECEIPT_CAMPUS_LOCATION_CODE = "??";

        public static final String CASH_RECEIPT_CAMPUS_LOCATION_CODE_PROPERTY_NAME = "campusLocationCode";
        public static final String CASH_RECEIPT_DOC_HEADER_STATUS_CODE_PROPERTY_NAME = KFSConstants.DOCUMENT_HEADER_PROPERTY_NAME + "." + KFSConstants.DOCUMENT_HEADER_DOCUMENT_STATUS_CODE_PROPERTY_NAME;
    }

    public static class DepositConstants {
        public static final String DEPOSIT_TYPE_INTERIM = "I";
        public static final String DEPOSIT_TYPE_FINAL = "F";

        public static final String DEPOSIT_WIZARD_CASHRECEIPT_ERROR = "cashReceiptErrors";
        public static final String DEPOSIT_WIZARD_DEPOSITHEADER_ERROR = "depositHeaderErrors";
    }

    public static class CreditCardReceiptConstants {
        public static final String CASH_RECEIPT_CREDIT_CARD_RECEIPT_COLUMN_TYPE_CODE = "R";
    }

    public static class BudgetAdjustmentDocumentConstants {
        public static final String SOURCE_BA = "From/Decrease";
        public static final String TARGET_BA = "To/Increase";
        public static final String GENERATE_BENEFITS_QUESTION_ID = "GenerateBenefitsQuestion";
        public static final String ADJUSTMENT_RESTRICTION_LEVEL_FUND = "F";
        public static final String ADJUSTMENT_RESTRICTION_LEVEL_CHART = "C";
        public static final String ADJUSTMENT_RESTRICTION_LEVEL_ORGANIZATION = "O";
        public static final String ADJUSTMENT_RESTRICTION_LEVEL_ACCOUNT = "A";
        public static final String ADJUSTMENT_RESTRICTION_LEVEL_SUBFUND = "S";
        public static final String ADJUSTMENT_RESTRICTION_LEVEL_NONE = "N";
        public static final String CROSS_INCOME_STREAM_GLPE_TRANSFER_GENERATING_FUND_GROUPS = "CROSS_INCOME_STREAM_GLPE_TRANSFER_GENERATING_FUND_GROUPS";
        public static final String CROSS_INCOME_STREAM_GLPE_TRANSFER_GENERATING_SUB_FUND_GROUPS = "CROSS_INCOME_STREAM_GLPE_TRANSFER_GENERATING_SUB_FUND_GROUPS";
    }

    public static class BudgetConstructionPositionConstants {
        public static final String POSITION_REGULAR_TEMPORARY_REGULAR = "R";
        public static final String POSITION_REGULAR_TEMPORARY_TEMPORARY = "T";
        public static final String POSITION_EFFECTIVE_STATUS_ACTIVE = "A";
        public static final String POSITION_EFFECTIVE_STATUS_INACTIVE = "I";
        public static final String POSITION_STATUS_APPROVED = "A";
        public static final String POSITION_STATUS_DELETED = "D";
        public static final String POSITION_STATUS_FROZEN = "F";
        public static final String POSITION_STATUS_TEMPORARILY_INACTIVE = "T";
    }

    public static class DisbursementVoucherDocumentConstants {
        public static final String CLEAR_NON_EMPLOYEE_TAB_QUESTION_ID = "ClearNonEmplTravTabQuestion";
        public static final String CLEAR_WIRE_TRANSFER_TAB_QUESTION_ID = "ClearWireTransferTabQuestion";
        public static final String CLEAR_FOREIGN_DRAFT_TAB_QUESTION_ID = "ClearForeignDraftTabQuestion";
    }

    public static final String ACCOUNTING_LINE_IMPORT_MAX_FILE_SIZE_PARM_NM = "MAX_FILE_SIZE_ACCOUNTING_LINE_IMPORT";
    public static final String ORIGIN_ENTRY_IMPORT_MAX_FILE_SIZE_PARM_NM = "MAX_FILE_SIZE_ORIGIN_ENTRY_IMPORT";

    public static class ChartApcParms {

        public static final String FISCAL_YEAR_MAKER_REPLACE_MODE = "OVERRIDE_TARGET_YEAR_DATA_IND";
        public static final String FISCAL_YEAR_MAKER_SOURCE_FISCAL_YEAR = "SOURCE_FISCAL_YEAR";

        // added from parameter refactoring.
        public static final String APC_HRMS_ACTIVE_KEY = "USE_HRMS_ORGANIZATION_ATTRIBUTES_IND";
        public final static String OBJECT_CODE_ILLEGAL_VALUES = "OBJECT_CODES";
        public static final String DOCTYPE_AND_OBJ_CODE_ACTIVE = "DOCUMENT_TYPES_REQUIRING_ACTIVE_OBJECT_CODES";
        public static final String INVALID_DOCUMENT_TYPES_BY_OBJECT_SUB_TYPE = "INVALID_DOCUMENT_TYPES_BY_OBJECT_SUB_TYPE";
        public static final String VALID_DOCUMENT_TYPES_BY_OBJECT_SUB_TYPE = "VALID_DOCUMENT_TYPES_BY_OBJECT_SUB_TYPE";
        public static final String CG_ALLOWED_SUBACCOUNT_TYPE_CODES = "SUB_ACCOUNT_TYPES";

        // Account parms
        public static final String INCOME_STREAM_ACCOUNT_REQUIRING_FUND_GROUPS = "INCOME_STREAM_ACCOUNT_REQUIRING_FUND_GROUPS";
        public static final String INCOME_STREAM_ACCOUNT_REQUIRING_SUB_FUND_GROUPS = "INCOME_STREAM_ACCOUNT_REQUIRING_SUB_FUND_GROUPS";

        // Org parms
        public static final String DEFAULT_ACCOUNT_NOT_REQUIRED_ORG_TYPES = "ORGANIZATION_TYPES_NOT_REQUIRING_DEFAULT_ACCOUNT";
        public static final String ORG_MUST_REPORT_TO_SELF_ORG_TYPES = "ORGANIZATION_TYPES_THAT_MUST_REPORT_TO_SELF";

        public static final String ACCOUNT_FUND_GROUP_DENOTES_CG = "FUND_GROUP_DENOTES_CG_IND";
        public static final String ACCOUNT_CG_DENOTING_VALUE = "CG_DENOTING_VALUE";
    }

    public static class FinancialApcParms {
        public static final String ACCOUNTING_LINE_IMPORT_HELP = "ACCOUNTING_LINE_IMPORT";
        
        public static final String DV_CAMPUS_BY_PAYMENT_REASON_PARAM = "CAMPUS_BY_PAYMENT_REASON";
    }

    public static class SystemGroupParameterNames {

        public static final String FLEXIBLE_OFFSET_ENABLED_FLAG = "USE_FLEXIBLE_OFFSET_IND";
        public static final String ICR_ENCUMBRANCE_ENABLED_FLAG = "USE_ICR_ENCUMBRANCE_IND";
        public static final String PURGE_GL_ACCT_BALANCES_T_BEFORE_YEAR = "PRIOR_TO_YEAR";
        public static final String PURGE_GL_ENCUMBRANCE_T_BEFORE_YEAR = "PRIOR_TO_YEAR";
        public static final String PURGE_GL_SF_BALANCES_T_BEFORE_YEAR = "PRIOR_TO_YEAR";
        public static final String PURGE_GL_BALANCE_T_BEFORE_YEAR = "PRIOR_TO_YEAR";
        public static final String PURGE_GL_ENTRY_T_BEFORE_YEAR = "PRIOR_TO_YEAR";
        public static final String PURGE_GL_ID_BILL_T_BEFORE_YEAR = "PRIOR_TO_YEAR";

        public static final String GL_ANNUAL_CLOSING_DOC_TYPE = "ANNUAL_CLOSING_DOCUMENT_TYPE";
        public static final String GL_INDIRECT_COST_RECOVERY = "INDIRECT_COST_RECOVERY_DOCUMENT_TYPE";
        public static final String GL_NET_EXPENSE_OBJECT_TYPE_CODE = "NET_EXPENSE_OBJECT_TYPE_CODE";
        public static final String GL_ORIGINATION_CODE = "MANUAL_FEED_ORIGINATION";
        public static final String GL_SCRUBBER_VALIDATION_DAYS_OFFSET = "CG_ACCOUNT_EXPIRATION_EXTENSION_DAYS";

        public static final String MULTIPLE_VALUE_LOOKUP_RESULTS_PER_PAGE = "MULTIPLE_VALUE_RESULTS_PER_PAGE";
        public static final String MULTIPLE_VALUE_LOOKUP_RESULTS_EXPIRATION_AGE = "MULTIPLE_VALUE_RESULTS_EXPIRATION_SECONDS";

        public static final String ACTIVE_INPUT_TYPES_PARAMETER_NAME = "ACTIVE_FILE_TYPES";

        public static final String COLLECTOR_VALIDATOR_EMAIL_SUBJECT_PARAMETER_NAME = "VALIDATION_EMAIL_SUBJECT_LINE";
        public static final String COLLECTOR_DEMERGER_EMAIL_SUBJECT_PARAMETER_NAME = "ERROR_EMAIL_SUBJECT_LINE";
        public static final String COLLECTOR_EQUAL_DC_TOTAL_DOCUMENT_TYPES = "EQUAL_DEBIT_CREDIT_TOTAL_DOCUMENT_TYPES";
        public static final String COLLECTOR_PERFORM_DUPLICATE_HEADER_CHECK = "PERFORM_DUPLICATE_HEADER_CHECK_IND";

        public static final String BATCH_SCHEDULE_CUTOFF_TIME = "CUTOFF_TIME";
        public static final String BATCH_SCHEDULE_CUTOFF_TIME_IS_NEXT_DAY = "CUTOFF_TIME_NEXT_DAY_IND";
        public static final String BATCH_SCHEDULE_STATUS_CHECK_INTERVAL = "STATUS_CHECK_INTERVAL";
        

        /**
         * Used by PurgePendingAttachmentsJob to compute the maximum amount of time a pending attachment is allowed to persist on
         * the file system before being deleted.
         */
        public static final String PURGE_PENDING_ATTACHMENTS_STEP_MAX_AGE = "MAX_AGE";

        public static final String JOB_ADMIN_WORKGROUP = "SCHEDULE_ADMIN_GROUP";
        
        public static final String NUMBER_OF_DAYS_SINCE_LAST_UPDATE  = "NUMBER_OF_DAYS_SINCE_LAST_UPDATE";
    }

    public static class GeneralLedgerApplicationParameterKeys {
        public static final String INCOME_OBJECT_TYPE_CODES = "INCOME_OBJECT_TYPE_CODES";
        public static final String INCOME_TRANSFER_OBJECT_TYPE_CODES = "INCOME_TRANSFER_OBJECT_TYPE_CODES";
        public static final String EXPENSE_OBJECT_TYPE_CODES = "EXPENSE_OBJECT_TYPE_CODES";
        public static final String EXPENSE_TRANSFER_OBJECT_TYPE_CODES = "EXPENSE_TRANSFER_OBJECT_TYPE_CODES";
    }

    public static class GeneralLedgerCorrectionProcessApplicationParameterKeys {
        public static final String RECORD_COUNT_FUNCTIONALITY_LIMIT = "RECORD_COUNT_FUNCTIONALITY_LIMIT";
        public static final String RECORDS_PER_PAGE = "RECORDS_PER_PAGE";
    }

    public static class EnterpriseFeederApplicationParameterKeys {
        public static final String TO_ADDRESS = "INVALID_FILE_TO_EMAIL_ADDRESSES";
    }

    public static class ParameterValues {
        public static final String YES = "Y";
        public static final String NO = "N";
    }

    public static class Maintenance {
        public static final String AFTER_CLASS_DELIM = "!!";
        public static final String AFTER_FIELDNAME_DELIM = "^^";
        public static final String AFTER_VALUE_DELIM = "::";
    }

    public static class ObjectCodeConstants {
        public static final String INACTIVE_OBJECT_LEVEL_QUESTION_ID = "InactiveObjectLevelQuestion";
    }

    public static final String MONTH1 = "01";
    public static final String MONTH2 = "02";
    public static final String MONTH3 = "03";
    public static final String MONTH4 = "04";
    public static final String MONTH5 = "05";
    public static final String MONTH6 = "06";
    public static final String MONTH7 = "07";
    public static final String MONTH8 = "08";
    public static final String MONTH9 = "09";
    public static final String MONTH10 = "10";
    public static final String MONTH11 = "11";
    public static final String MONTH12 = "12";
    public static final String MONTH13 = "13";
    public static final String PERIOD_CODE_ANNUAL_BALANCE = "AB";
    public static final String PERIOD_CODE_BEGINNING_BALANCE = "BB";
    public static final String PERIOD_CODE_CG_BEGINNING_BALANCE = "CB";

    public static final String REQUEST_SEARCH_RESULTS = "reqSearchResults";
    public static final String REQUEST_SEARCH_RESULTS_SIZE = "reqSearchResultsSize";
    public static final String GL_COLLECTOR_STAGING_DIRECTORY = "collector.staging.directory";

    public static final String DISBURSEMENT_VOUCHER_DOCUMENTATION_LOCATION_CODE_PROPERTY_NAME = "disbursementVoucherDocumentationLocationCode";
    public static final String FUND_GROUP_CODE_PROPERTY_NAME = "code";
    public static final String SUB_FUND_GROUP_CODE_PROPERTY_NAME = "subFundGroupCode";

    public static final String ACCOUNT_TYPE_S3 = "S3";
    public static final String RULE_CODE_R1 = "R1";
    public static final String RULE_CODE_R2 = "R2";
    public static final String RULE_CODE_N1 = "N1";
    public static final String RULE_CODE_N2 = "N2";
    public static final String RULE_CODE_C1 = "C1";
    public static final String RULE_CODE_C2 = "C2";
    public static final String RULE_CODE_A = "A";
    public static final String TRANSACTION_DT = "TRANSACTION_DT";
    public static final String UNALLOC_OBJECT_CD = "UNALLOC_OBJECT_CD";
    public static final String BEG_BUD_CASH_OBJECT_CD = "BEG_BUD_CASH_OBJECT_CD";
    public static final String FUND_BAL_OBJECT_CD = "FUND_BAL_OBJECT_CD";
    public static final String UNIV_FISCAL_YR = "UNIV_FISCAL_YR";

    public static final String EMPLOYEE_LOOKUP_ERRORS = "document.employeeLookups";

    public static class BudgetConstructionConstants {

        /* KFSConstants for the budget construction flag names */
        public final static String BUDGET_ADMINSTRATION_ACTIVE = "BAACTV";
        public final static String BASE_BUDGET_UPDATES_OK = "BASEAD";
        public final static String BUDGET_BATCH_SYNCHRONIZATION_OK = "BSSYNC";
        public final static String CSF_UPDATES_OK = "CSFUPD";
        public final static String BUDGET_CONSTRUCTION_ACTIVE = "BCACTV";
        public final static String BUDGET_CONSTRUCTION_GENESIS_RUNNING = "BCGENE";
        public final static String BUDGET_CONSTRUCTION_UPDATES_OK = "BCUPDT";
        public final static String BUDGET_ON_LINE_SYNCHRONIZATION_OK = "PSSYNC";

        /*
         * object code which stores amounts by which pending general ledger rows in budget construction are out of balance
         */
        public final static String OBJECT_CODE_2PLG = "2PLG";
        
    }

    public static class OperationType {
        public static final String READ = "read";
        public static final String REPORT_ERROR = "with error";
        public static final String INSERT = "insert";
        public static final String UPDATE = "update";
        public static final String DELETE = "delete";
        public static final String SELECT = "select";
        public static final String BYPASS = "bypassed";
    }

    public static class PENDING_ENTRY_APPROVED_STATUS_CODE {
        public static final String APPROVED = "A";
        public static final String PROCESSED = "X";
    }

    public static class TableRenderConstants {
        public static final String SWITCH_TO_PAGE_METHOD = "switchToPage";
        public static final String SORT_METHOD = "sort";
        public static final String SELECT_ALL_METHOD = "selectAll";
        public static final String UNSELECT_ALL_METHOD = "unselectAll";

        public static final String PREVIOUSLY_SORTED_COLUMN_INDEX_PARAM = "previouslySortedColumnIndex";
        public static final String VIEWED_PAGE_NUMBER = "viewedPageNumber";
    }

    public static final String PCDO_FILE_TYPE_INDENTIFIER = "procurementCardInputFileType";
    public static final String COLLECTOR_FILE_TYPE_INDENTIFIER = "collectorInputFileType";
    public static final String ENTERPRISE_FEEDER_FILE_SET_TYPE_INDENTIFIER = "enterpriseFeederFileSetType";

    // next 2 variables for the enterprise feeder batch upload
    public static final String DATA_FILE_TYPE = "DATA";
    public static final String RECON_FILE_TYPE = "RECON";
    
    // next variable used by the batch upload framework
    public static final String DONE_FILE_TYPE = "DONE_FILE";
    
    /**
     * The base implementation of {@link org.kuali.kfs.gl.batch.service.impl.EnterpriseFeederStatusBase} uses strings contained within
     * ApplicationResources.properties to store the human-readable descriptions of each status object. The fully qualified class
     * name is appended to the end of this key to generate the true key. For example,
     * gl.EnterpriseFeeder.StatusDescriptionPrefix.org.kuali.kfs.gl.batch.service.impl.FileReconBadLoadAbortedStatus
     */
    public static final String ENTERPRISE_FEEDER_STATUS_DESCRIPTION_PREFIX = "gl.EnterpriseFeeder.StatusDescription.";

    public static final String BATCH_STEP_RUNNER_JOB_NAME = "stepRunByBatchStepRunner";

    // Some static method calls below that could be done in static variables instead but isn't safe to do during class loading
    // w/SpringContext.
    private static String DASH_FINANCIAL_OBJECT_CODE = null;

    public static String getDashFinancialObjectCode() {
        if (DASH_FINANCIAL_OBJECT_CODE == null) {
            DASH_FINANCIAL_OBJECT_CODE = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(OriginEntryFull.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE), '-');
        }
        return DASH_FINANCIAL_OBJECT_CODE;
    }

    private static String DASH_FINANCIAL_SUB_OBJECT_CODE = null;

    public static String getDashFinancialSubObjectCode() {
        if (DASH_FINANCIAL_SUB_OBJECT_CODE == null) {
            DASH_FINANCIAL_SUB_OBJECT_CODE = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(OriginEntryFull.class, KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE), '-');
        }
        return DASH_FINANCIAL_SUB_OBJECT_CODE;
    }

    private static String DASH_SUB_ACCOUNT_NUMBER = null;

    public static String getDashSubAccountNumber() {
        if (DASH_SUB_ACCOUNT_NUMBER == null) {
            DASH_SUB_ACCOUNT_NUMBER = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(OriginEntryFull.class, KFSPropertyConstants.SUB_ACCOUNT_NUMBER), '-');
        }
        return DASH_SUB_ACCOUNT_NUMBER;
    }

    private static String SPACE_SUB_ACCOUNT_NUMBER = null;

    public static String getSpaceSubAccountNumber() {
        if (SPACE_SUB_ACCOUNT_NUMBER == null) {
            SPACE_SUB_ACCOUNT_NUMBER = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(OriginEntryFull.class, KFSPropertyConstants.SUB_ACCOUNT_NUMBER), ' ');
        }
        return SPACE_SUB_ACCOUNT_NUMBER;
    }

    private static String DASH_PROJECT_CODE = null;

    public static String getDashProjectCode() {
        if (DASH_PROJECT_CODE == null) {
            DASH_PROJECT_CODE = StringUtils.rightPad("", SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(OriginEntryFull.class, KFSPropertyConstants.PROJECT_CODE), '-');
        }
        return DASH_PROJECT_CODE;
    }
    
    //AR TAB ERROR KEYS
    
    //Customer Invoice Document
    public static final String CUSTOMER_INVOICE_DOCUMENT_ORGANIZATION_ERRORS = "document.billByChartOfAccountCode,document.billedByOrganizationCode,document.accountsReceivableDocumentHeader.processingChartOfAccountCode";
    public static final String CUSTOMER_INVOICE_DOCUMENT_GENERAL_ERRORS = "document.accountsReceivableDocumentHeader.customerNumber,document.invoice*,document.billingDate,document.invoiceDueDate";
    public static final String CUSTOMER_INVOICE_DOCUMENT_ADDRESS = "document.customerBillToAddressIdentifier,document.customerShipToAddressIdentifier"; 
    public static final String CUSTOMER_INVOICE_DOCUMENT_RECEIVABLE_ACCOUNTING_LINE = "document.payment*";
    public static final String CUSTOMER_INVOICE_DETAIL_ERRORS = "newCustomerInvoiceDetail*,document.sourceAccountingLine*";
    //public static final String CUSTOMER_INVOICE_DOCUMENT_RECURRENCE_DETAILS_ERRORS = "document.customerInvoiceRecurrenceDetails,document.customerInvoiceRecurrenceDetails.active,document.customerInvoiceRecurrenceDetails.workgroup,document.customerInvoiceRecurrenceDetails.document*,document.customerInvoiceRecurrenceDetails.documentInitiatorUser.principalName";
    public static final String CUSTOMER_INVOICE_DOCUMENT_RECURRENCE_DETAILS_ERRORS = "document.customerInvoiceRecurrenceDetails,document.customerInvoiceRecurrenceDetails.*,document.customerInvoiceRecurrenceDetails.document*";

    
    //Cash Control Document
    public static final String CASH_CONTROL_DOCUMENT_ERRORS = "document.accountsReceivableDocumentHeader.processingChartOfAccountCode,document.referenceFinancialDocumentNumber,document.customerPaymentMediumCode,document.organizationCode,document.bankCode";
    public static final String CASH_CONTROL_DETAILS_ERRORS = "newCashControl*,document.cashControlDetail*";
    
    // Customer Credit Memo Document
    public static final String CUSTOMER_CREDIT_MEMO_DETAILS_ERRORS = "document,document.creditMemoDetails*";
    public static final String CUSTOMER_CREDIT_MEMO_DETAIL_PROPERTY_NAME = "creditMemoDetails";
    
    // Customer Invoice Writeoff Document
    public static final String CUSTOMER_INVOICE_WRITEOFF_INIT_ERRORS = "document.financialDocumentReferenceInvoiceNumber*";
    public static final String CUSTOMER_INVOICE_WRITEOFF_DETAILS_ERRORS = "document.customerInvoiceDetailsForWriteoff*";
    
    // Customer Invoice Writeoff Lookup
    public static final String CUSTOMER_INVOICE_WRITEOFF_LOOKUP_RESULT_ERRORS = "customerInvoiceWriteoffLookupResults";
    
    public static final class ReportGeneration{
        public final static String PARAMETER_NAME_SUBREPORT_DIR = "SUBREPORT_DIR";
        public final static String PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME = "SUBREPORT_TEMPLATE_NAMES";
        public final static String DESIGN_FILE_EXTENSION = ".jrxml";
        public final static String JASPER_REPORT_EXTENSION = ".jasper";
        public final static String PDF_FILE_EXTENSION = ".pdf";
        public final static String PDF_MIME_TYPE = "application/pdf";
        public final static String TEXT_MIME_TYPE = "text/plain";
        public final static String ACCOUNT_EXPORT_FILE_NAME = "account_export.txt";
        public final static String MONTHLY_EXPORT_FILE_NAME = "monthly_export.txt";
        public final static String FUNDING_EXPORT_FILE_NAME = "funding_export.txt";
    }
    
    public final static KualiInteger ONE_HUNDRED = new KualiInteger(100);
    public final static KualiInteger ONE = new KualiInteger(1);
    
    // effort certification period status codes
    public static final class PeriodStatusCodes {
        public static final String CLOSED = "C";
        public static final String NOT_OPEN = "N";
        public static final String OPEN = "O";
    }
    
    public static final class CustomerParameter {
        public static final String TAX_NUMBER_REQUIRED_IND = "TAX_NUMBER_REQUIRED_IND";
    }
    
    // financial document type codes
    public static final class FinancialDocumentTypeCodes {
        public static final String ALL = "ALL";
        public static final String BUDGET_CONSTRUCTION = "BC";
        public static final String GENERAL_ERROR_CORRECTION = "GEC";
        public static final String YEAR_END_GENERAL_ERROR_CORRECTION = "YEGE";
        public static final String DISTRIBUTION_OF_INCOME_AND_EXPENSE = "DI";
        public static final String YEAR_END_DISTRIBUTION_OF_INCOME_AND_EXPENSE = "YEDI";
        public static final String SERVICE_BILLING = "SB";
        public static final String INTERNAL_BILLING = "BC";
        public static final String CASH_RECEIPT = "CR";
    }
    
    public static final class AccountingLineViewStandardBlockNames {
        public static final String ACTION_BLOCK = "actions";
        public static final String SEQUENCE_NUMBER_BLOCK = "sequenceNumber";
        public static final String SALES_TAX_BLOCK = "salesTaxInformation";
    }
    
    public static final class TaxRegionConstants {
        public static final String TAX_REGION_RATES = "taxRegionRates";
        public static final String TAX_REGION_COUNTIES = "taxRegionCounties";
        public static final String TAX_REGION_STATES = "taxRegionStates";
        public static final String TAX_REGION_POSTAL_CODES = "taxRegionPostalCodes";
        public static final String TAX_REGION_STATE_CODE = "stateCode";
        public static final String TAX_REGION_COUNTY_CODE = "countyCode";
        public static final String TAX_REGION_POSTAL_CODE = "postalCode";
        public static final String TAX_REGION_TAX_RATE = "taxRate";
        public static final String TAX_REGION_EFFECTIVE_DATE = "effectiveDate";
        public static final String TAX_REGION_TYPE_CODE = "taxRegionTypeCode";
        public static final String TAX_REGION_COUNTIES_SECTION_ID = "TaxRegionCounties";
        public static final String TAX_REGION_STATES_SECTION_ID = "TaxRegionStates";
        public static final String TAX_REGION_POSTAL_CODES_SECTION_ID = "TaxRegionPostalCodes";
        public static final String TAX_REGION_RATES_SECTION_ID = "TaxRegionRates";
        public static final String TAX_REGION_CREATE_SECTION_ID = "CreateTaxRegion";
        
        public static final String TAX_REGION_TYPE_CODE_COUNTY = "CNTY";
        public static final String TAX_REGION_TYPE_CODE_POSTAL_CODE = "POST";
        public static final String TAX_REGION_TYPE_CODE_STATE = "ST";
    }
    
    public static final class SubAccountType {
        public static final String COST_SHARE = "CS";
        public static final String EXPENSE = "EX";
        
        public static final List<String> ELIGIBLE_SUB_ACCOUNT_TYPE_CODES = getEligibleSubAccountTypeCodes();
        
        private static final List<String> getEligibleSubAccountTypeCodes(){
            List<String> subAccountTypeCodesList = new ArrayList<String>();
            subAccountTypeCodesList.add(KFSConstants.SubAccountType.COST_SHARE);
            subAccountTypeCodesList.add(KFSConstants.SubAccountType.EXPENSE);
            return subAccountTypeCodesList;
        }
    }

    public static final class CustomerOpenItemReport {
        public static final String CUSTOMER_NUMBER = "customerNumber";
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static final String REPORT_NAME = "reportName";
        public static final String CHART_CODE = "chartCode";
        public static final String ORGANIZATION_CODE = "orgCode";
        public static final String REPORT_OPTION = "reportOption";
        public static final String COLUMN_TITLE = "columnTitle";
        public static final String REPORT_RUN_DATE = "reportRunDate";
        public static final String REPORT_BEGIN_DATE = "startDate";
        public static final String REPORT_END_DATE = "endDate";
        public static final String ACCOUNT_NUMBER = "accountNumber";
        public static final String ALL_DAYS = "All days";
        public static final String HISTORY_REPORT_NAME = "Customer History Report";
        public static final String OPEN_ITEM_REPORT_NAME = "Customer Open Item Report";
        public static final String REPORT_OPTION_ACCT = "Account";
        
        public static final String PROCESSING_COA_CODE = "processingChartOfAccountCode";
        public static final String PROCESSING_ORGANIZATION_CODE = "processingOrganizationCode";
    }
    
    public static final class CustomerAgingReport {
        public static final String CUSTOMER_NAME = "customerName";
        public static final String UNPAID_BALANCE_0_TO_30 = "unpaidBalance0to30";
        public static final String UNPAID_BALANCE_31_TO_60 = "unpaidBalance31to60";
        public static final String UNPAID_BALANCE_61_TO_90 = "unpaidBalance61to90";
        public static final String UNPAID_BALANCE_91_TO_SYSPR = "unpaidBalance91toSYSPR";
        public static final String UNPAID_BALANCE_SYSPR_PLUS_1_OR_MORE = "unpaidBalanceSYSPRplus1orMore";
    }
    
    public static final class PdpConstants {
        public static final String PAYMENT_OPEN_STATUS_CODE = "OPEN";
    }

    public static final String PAYMENT_APPLICATION_DOCUMENT_ERRORS = "document";
    public static class PaymentApplicationTabErrorCodes {
        public static final String NON_AR_TAB = "nonInvoicedAddLine*";
        public static final String UNAPPLIED_TAB = "document.nonAppliedHolding*";
        public static final String APPLY_TO_INVOICE_DETAIL_TAB = "customerInvoiceDetail*";
    }
    
    // define a set of indicators related to payments, including payee types, tax review requirements and others
    public static class AdHocPaymentIndicator{
        public static final String EMPLOYEE_VENDOR = "E";
        public static final String ALIEN_VENDOR = "A"; 
        public static final String EMPLOYEE_PAYEE = "E";
        public static final String ALIEN_PAYEE = "A"; 
        public static final String TAX_CONTROL_REQUIRING_TAX_REVIEW = "T"; 
        public static final String PAYMENT_REASON_REQUIRING_TAX_REVIEW = "P";
        public static final String OTHER = "N";
    }
    
    public static class SysKimConstants{
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
        public static final String CHART_MANAGER_KIM_ROLE_NAME = "Chart Manager";
        public static final String ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE = "KFS-SYS";
        public static final String ACCOUNTING_REVIEWER_ROLE_NAME = "Accounting Reviewer";
        public static final String ORGANIZATION_REVIEWER_ROLE_NAME = "Organization Reviewer";
    } 

    public enum PermissionAttributeValue {
        SOURCE_ACCOUNTING_LINES("sourceAccountingLines"), TARGET_ACCOUNTING_LINES("targetAccountingLines");

        public final String value;

        private PermissionAttributeValue(String value) {
            this.value = value;
        }
    }

    public enum PermissionTemplate {
        DEFAULT("Default"), ERROR_CORRECT_DOCUMENT("Error Correct Document"),
        MODIFY_ACCOUNTING_LINES("Modify Accounting Lines"), CLAIM_ELECTRONIC_PAYMENT("Claim Electronic Payment"),
        MODIFY_BATCH_JOB("Modify Batch Job"), EDIT_BANK_CODE("Edit Bank Code");

        public final String name;
        private PermissionTemplate(String name) {
            this.name = name;
        }
    }
    
    public class PermissionNames {
        public static final String EDIT_INACTIVE_ACCOUNT = "Edit Inactive Account";
        public static final String SERVE_AS_ACCOUNT_MANAGER = "Serve As Account Manager";
        public static final String SERVE_AS_ACCOUNT_SUPERVISOR = "Serve As Account Supervisor";
        public static final String SERVE_AS_FISCAL_OFFICER = "Serve As Fiscal Officer";
        public static final String SERVE_AS_FISCAL_OFFICER_DELEGATE = "Serve As Fiscal Officer Delegate";
    }

    public static final String KFS_ACTION_CAN_ERROR_CORRECT = "canErrorCorrect";
    public static final String KFS_ACTION_CAN_EDIT_BANK = "canEditBank";
    

    public static final String AMOUNT_TOTALING_EDITING_MODE = "amountTotaling";
    public static final String BANK_ENTRY_VIEWABLE_EDITING_MODE = "bankEntryViewable";
    public static final String BANK_ENTRY_EDITABLE_EDITING_MODE = "bankEntry";
    
    public class RouteLevelNames {
        public static final String ACCOUNT = "Account";
        public static final String ACCOUNTING_ORGANIZATION_HIERARCHY = "AccountingOrganizationHierarchy";
        public static final String ACCOUNT_REVIEW_FULL_EDIT = "Account Review Full Edit";
        public static final String PROJECT_MANAGEMENT = "ProjectManagement";
    }
    
    public static final String ROOT_DOCUMENT_TYPE = "KFS";
    
    public static class COAConstants{
        public static final String FINANCIAL_SYSTEM_DOCUMENT = "KFS";
        public static final String FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT = "KFST";
        public static final String FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT = "KFSM";
        public static final String FINANCIAL_SYSTEM_SIMPLE_MAINTENANCE_DOCUMENT = "FSSM";
        
        public static final String NODE_NAME_ORGANIZATION_HIERARCHY = "OrganizationHierarchy";
        public static final String NODE_NAME_ACCOUNTING_ORGANIZATION_HIERARCHY = "AccountingOrganizationHierarchy";
        
        public static final String ORG_REVIEW_ROLE_ORG_ACC_ONLY_CODE = "A";
        public static final String ORG_REVIEW_ROLE_ORG_ACC_ONLY_TEXT = "Organization Accounting Only";
        public static final String ORG_REVIEW_ROLE_ORG_ONLY_CODE = "O";
        public static final String ORG_REVIEW_ROLE_ORG_ONLY_TEXT = "Organization Only";
        public static final String ORG_REVIEW_ROLE_ORG_ACC_BOTH_CODE = "B";
        public static final String ORG_REVIEW_ROLE_ORG_ACC_BOTH_TEXT = "Both";
        public static final String ORG_REVIEW_ROLE_DOCUMENT_TYPE_NAME = "ORR";
        public static final String ORG_REVIEW_ROLE_CREATE_DELEGATION_DISPLAY_TEXT = "create delegation";

    }
    
}

