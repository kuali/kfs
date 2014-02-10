/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.cg;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Constants specific to the Contracts and Grants module.
 */
public class CGConstants{

    public static final String CG_NAMESPACE_CODE = "KFS-CG";

    /**
     * The key for the document error map to grab errors for the close document.
     */
    public static final String CLOSE_DOCUMENT_TAB_ERRORS = "document.userInitiatedCloseDate";

    public static final String SHORT_TIMESTAMP_FORMAT = "MM/dd/yyyy";
    public static final String LONG_TIMESTAMP_FORMAT = "MM/dd/yyyy HH:mm:ss";

    public static final int maximumPeriodLengthUnits = Calendar.YEAR;

    public static final String DATABASE_TRUE_VALUE = "Y";

    public static final String DROPDOWN_LIST_SELECT = "select:";

    // System Parameters
    public static final String ENABLE_CG_PRORATE_BILL_IND = "ENABLE_CG_PRORATE_BILL_IND";

    public static final String RESULT_SUMMARY_TO_EMAIL_ADDRESSES = "RESULT_SUMMARY_TO_EMAIL_ADDRESSES";

    public static final String SOURCE_URL_PARAMETER = "SOURCE_URL";

    public static final String MANUAL_BASE = "MN";
    public static final String MODIFIED_TOTAL_DIRECT_COST = "MT";

    public static final String ORG_REVIEW_NODE_NAME = "Org Review";
    public static final String ORG_REVIEW_TEMPLATE_NAME = "KualiResearchOrgReviewTemplate";
    public static final String DEFAULT_PREFERRED_BILLING_FREQUENCY = "DEFAULT_PREFERRED_BILLING_FREQUENCY";

    public static final String DEFAULT_PREFERRED_BILLING_FREQUENCY_PARAMETER = "DEFAULT_PREFERRED_BILLING_FREQUENCY";
    public static final String DEFAULT_DUNNING_CAMPAIGN_PARAMETER = "DEFAULT_DUNNING_CAMPAIGN";
    public static final String PARAMETER_DEFAULT_FUND_MANAGER_ID = "DEFAULT_FUND_MANAGER_ID";

    // Agency
    public static final String AGENCY_USE_EXISTING_CUSTOMER = "Use Existing Customer";
    public static final String AGENCY_CREATE_NEW_CUSTOMER = "Create New Customer";
    public static final String AGENCY_NO_CUSTOMER = "No Customer";
    public static final String AGENCY_USE_EXISTING_CUSTOMER_CODE = "E";
    public static final String AGENCY_CREATE_NEW_CUSTOMER_CODE = "C";
    public static final String AGENCY_NO_CUSTOMER_CODE = "N";

    // Agency Address
    public static final String AGENCY_DETAIL_TYPE_CODE = "Agency";
    public static final String AGENCY_PRIMARY_ADDRESSES_TYPE_CODE = "P";
    public static final String AGENCY_ALTERNATE_ADDRESSES_TYPE_CODE = "A";
    public static final String DEFAULT_NUMBER_OF_COPIES_TO_PRINT = "DEFAULT_NUMBER_OF_COPIES_TO_PRINT";

    // Agency Collections Maitenance
    public static final String CHAPTER7_CODE = "C7";
    public static final String CHAPTER11_CODE = "C11";
    public static final String CHAPTER13_CODE = "C13";
    public static final String JUDGMENT_OBTAINED_CODE = "JO";

    public static final String CHAPTER7  = "Chapter 7";
    public static final String CHAPTER11 = "Chapter 11";
    public static final String CHAPTER13 = "Chapter 13";
    public static final String JUDGMENT_OBTAINED= "Judgment Obtained";

    // Award
    public static final String AWARD_DETAIL_TYPE_CODE = "Award";
    public static final String LOC_BY_AWARD = "LOC By Award";
    public static final String LOC_BY_LOC_FUND = "LOC By Letter of Credit Fund";
    public static final String LOC_BY_LOC_FUND_GRP = "LOC By Letter of Credit Fund Group";
    public static final String AWARD_MILESTONE_CHECK_LIMIT_DAYS = "AWARD_MILESTONE_CHECK_LIMIT_DAYS";


    // Award Invoice
    public static final String MINIMUM_INVOICE_AMOUNT = "MINIMUM_INVOICE_AMOUNT";
    public static final String DEFAULT_INVOICE_TEMPLATE = "DEFAULT_INVOICE_TEMPLATE";
    public static final String DEFAULT_INVOICING_OPTION_PARAMETER = "DEFAULT_INVOICING_OPTION";

    //
    public static final String DOCUMENT_STATUS_FINAL = "F";

    // Research Risk Types
    public static final String RESEARCH_RISK_TYPE_ALL_COLUMNS = "A";
    public static final String RESEARCH_RISK_TYPE_SOME_COLUMNS = "S";
    public static final String RESEARCH_RISK_TYPE_DESCRIPTION = "D";

    // Study Statuses
    public static final String RESEARCH_RISK_STUDY_STATUS_APPROVED = "A";
    public static final String RESEARCH_RISK_STUDY_STATUS_PENDING = "P";

    // Study Review Statuses
    public static final String RESEARCH_RISK_STUDY_REVIEW_EXEMPT = "X";

    // Following are used in tags on Main Page.
    public static final String SUBMISSION_TYPE_CHANGE = "SUBMISSION_TYPE_CHANGE";
    public static final String PROJECT_TYPE_OTHER = "PROJECT_TYPE_OTHER";
    public static final String PURPOSE_RESEARCH = "PURPOSE_RESEARCH";
    public static final String PURPOSE_OTHER = "PURPOSE_OTHER";
    public static final String CONTACT_PERSON_PARAM = "PERSON_ROLE_CODE_CONTACT_PERSON";
    public static final String CO_PROJECT_DIRECTOR_PARAM = "PERSON_ROLE_CODE_CO_PROJECT_DIRECTOR";
    public static final String OTHER_PERSON_PARAM = "PERSON_ROLE_CODE_OTHER";
    public static final String PROJECT_DIRECTOR_PARAM = "PERSON_ROLE_CODE_PROJECT_DIRECTOR";

    public static final String MAXIMUM_ACCOUNT_RESPONSIBILITY_ID = "MAXIMUM_ACCOUNT_RESPONSIBILITY_ID";

    // Parameter name for the CG and Billing Enhancements Indicator
    public static final String ENABLE_CG_BILLING_ENHANCEMENTS_IND = "ENABLE_CG_BILLING_ENHANCEMENTS_IND";
    public static final String AWARD = "AWRD";
    public static final String CG_MILESTONE_SCHEDULE = "MILE";


    public static final String GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU = "3";

    public static class TemplateUploadSystem {
        static final public String EXTENSION = ".pdf";
        static final public String TEMPLATE_MIME_TYPE = "application/pdf";
    }

    public static class BillingFrequency {
        static final public String PREDETERMINED_SCHEDULE = "Predetermined Billing Schedule";
    }

    public static class CGKimApiConstants {
        public static final String AWARD_ROUTING_NODE_NAME = "Award";
        public static final String MANAGEMENT_ROUTING_NODE_NAME = "Management";
        public static final String UNPROCESSED_ROUTING_NODE_NAME = "Unprocessed";

    }

    public static class SectionId {
        public static final String PROPOSAL_RESEARCH_RISKS = "proposalResearchRisks";
        public static final String AGENCY_CUSTOMER_SECTION_ID = "Customer";
    }

    public static class ReportsConstants {
        public static final List<String> awardBalancesReportSubtotalFieldsList = new ArrayList<String>();

        static {
            awardBalancesReportSubtotalFieldsList.add("agency.fullName");
            awardBalancesReportSubtotalFieldsList.add("awardStatusCode");
            awardBalancesReportSubtotalFieldsList.add("awardPrimaryProjectDirector.projectDirector.name");
            awardBalancesReportSubtotalFieldsList.add("awardPrimaryFundManager.fundManager.name");
        }

        public static final List<String> reportSearchCriteriaExceptionList = new ArrayList<String>();

        static {
            reportSearchCriteriaExceptionList.add("backLocation");
            reportSearchCriteriaExceptionList.add("docFormKey");
            reportSearchCriteriaExceptionList.add("dummyBusinessObject.invoiceReportOption");
        }
    }

    public static class AwardCreationDefaults {
        public static final String CHART_OF_ACCOUNT_CODE = "chartOfAccountsCode";
        public static final String ACCOUNT_NUMBER = "accountNumber";
        public static final String KcUnit = "kcUnit";
    }
}
