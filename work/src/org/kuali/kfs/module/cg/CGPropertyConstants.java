/*
 * Copyright 2006-2008 The Kuali Foundation
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



/**
 * Property Constants for CG module
 */
public class CGPropertyConstants{

    // Research Risk Type
    public static final String RESEARCH_RISK_TYPE_SORT_NUMBER = "researchRiskTypeSortNumber";

    // Award
    public static final String AWARD_FUND_MANAGERS = "awardFundManagers";
    public static final String AWARD_INVOICE_LINK_PROPOSAL_NUMBER_PATH = "documentAttribute.proposalNumber";



    public static final String BILLING_FREQUENCY_CODE = "billingFrequencyCode";
    public static final String AWARD_INSTRUMENT_TYPE = "instrumentTypeCode";
    public static final String AWARD_INVOICING_OPTION_CODE = "invoicingOptionCode";
    public static final String AWARD_INVOICE_ACCOUNTS = "awardInvoiceAccounts";
    public static final String PROPOSAL_LOOKUPABLE = "proposalLookupable";
    public static final String PROJECT_TITLE = "projectTitle";

    //Instrument Type
    public static final String INSTRUMENT_TYPE_CODE = "instrumentTypeCode";

    // Agency
    public static class AgencyFields {
        public static final String AGENCY_TAB_ADDRESSES = "agencyAddresses";
        public static final String AGENCY_NUMBER = "agencyNumber";
        public static final String AGENCY_ADDRESS_STATE_CODE = "agencyStateCode";
        public static final String AGENCY_ADDRESS_ZIP_CODE = "agencyZipCode";
        public static final String AGENCY_ADDRESS_INTERNATIONAL_PROVINCE_NAME = "agencyAddressInternationalProvinceName";
        public static final String AGENCY_ADDRESS_INTERNATIONAL_MAIL_CODE = "agencyInternationalMailCode";
        public static final String AGENCY_CUSTOMER_TYPE_CODE= "customerTypeCode";
    }

    public static class AgencyAddressFields {
        public static final String AGENCY_COUNTRY = "agencyCountry";
    }

    public static class AwardFields {
        public static final String MILESTONE_SCHEDULE_INQUIRY_TITLE = "milestoneSchedule.milestoneScheduleInquiryTitle";
        public static final String PREDETERMINED_BILLING_SCHEDULE_INQUIRY_TITLE = "predeterminedBillingSchedule.predeterminedBillingScheduleInquiryTitle";
    }

    // AR related constants
    public static final String CUSTOMER = "customer";
    public static final String CUSTOMER_NUMBER = "customerNumber";
    public static final String CUSTOMER_TYPE_CODE = "customerTypeCode";

      // ProposalAwardCloseDocument
    public static final String PROPOSAL_AWARD_CLOSE_DOC_USER_INITIATED_CLOSE_DATE = "userInitiatedCloseDate";

    public static final String LOOKUP_USER_ID_FIELD = "lookupPerson.principalName";
    public static final String LOOKUP_FUND_MGR_USER_ID_FIELD = "lookupFundMgrPerson.principalName";
    public static final String AWARD_LOOKUP_FUND_MGR_UNIVERSAL_USER_ID_FIELD = "awardFundManagers.principalId";
    public static final String AWARD_LOOKUP_UNIVERSAL_USER_ID_FIELD = "awardProjectDirectors.principalId";
    public static final String PROPOSAL_LOOKUP_UNIVERSAL_USER_ID_FIELD = "proposalProjectDirectors.principalId";

    public static class SectionId {
        public static final String AGENCY_ADDRESS_SECTION_ID = "addressSection";
        public static final String AGENCY_ADDRESSES_SECTION_ID = "addressesSection";
        public static final String AGENCY_COLLECTIONS_MAINTENANCE_SECTION_ID = "collectionsMaintenanceSection";
        public static final String AGENCY_CONTRACTS_AND_GRANTS_SECTION_ID = "contractsAndGrantsSection";
        public static final String AGENCY_CUSTOMER_SECTION_ID = "customerSection";
        public static final String AWARD_FUND_MANAGERS_SECTION_ID = "fundManagersSection";
        public static final String AWARD_INVOICE_ACCOUNTS_SECTION_ID = "invoiceAccountsSection";
        public static final String AWARD_INVOICING_SECTION_ID = "invoicingSection";
        public static final String AWARD_MILESTONE_SCHEDULE_SECTION_ID = "milestoneScheduleSection";
        public static final String AWARD_PREDETERMINED_BILLING_SCHEDULE_SECTION_ID = "predeterminedBillingScheduleSection";
        public static final String PROPOSAL_RESEARCH_RISKS = "proposalResearchRisks";
    }


    public static class AwardCreationDefaults {
        public static final String KcUnit = "kcUnit";
    }
}
