/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
        public static final String AGENCY_CUSTOMER_NUMBER = "customerNumber";
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
        public static final String AWARD_INVOICING_SECTION_ID = "invoicingSection";
        public static final String AWARD_MILESTONE_SCHEDULE_SECTION_ID = "milestoneScheduleSection";
        public static final String AWARD_PREDETERMINED_BILLING_SCHEDULE_SECTION_ID = "predeterminedBillingScheduleSection";
        public static final String PROPOSAL_RESEARCH_RISKS = "proposalResearchRisks";
    }


    public static class AwardCreationDefaults {
        public static final String KcUnit = "kcUnit";
    }
}
