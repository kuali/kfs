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


import java.util.HashMap;
import java.util.Map;

/**
 * Property Constants for CG module
 */
public class CGPropertyConstants{

    // Common document values
    public static final String DOCUMENT = "document";
    public static final String DOCUMENT_NUMBER = "documentNumber";
    public static final String NEW_MAINTAINABLE_OBJECT = "newMaintainableObject";

    // Research Risk Type
    public static final String RESEARCH_RISK_TYPE_DESCRIPTION = "researchRiskTypeDescription";
    public static final String RESEARCH_RISK_TYPE_SORT_NUMBER = "researchRiskTypeSortNumber";

    // Award
    public static final String AWARD_FUND_MANAGERS = "awardFundManagers";
    public static final String PREDETERMINED_BILLING_SCHEDULE_CODE = "PDBS";
    public static final String MILESTONE_BILLING_SCHEDULE_CODE = "MILE";
    public static final String MONTHLY_BILLING_SCHEDULE_CODE = "MNTH";
    public static final String QUATERLY_BILLING_SCHEDULE_CODE = "QUAR";
    public static final String SEMI_ANNUALLY_BILLING_SCHEDULE_CODE = "SEMI";
    public static final String ANNUALLY_BILLING_SCHEDULE_CODE = "ANNU";
    public static final String LOC_BILLING_SCHEDULE_CODE = "LOCB";
    public static final String BILLED_AT_TERM = "AT_TERM";
    public static final String BILLING_SCHEDULE_SECTION = "Predetermined Schedule";
    public static final String MILESTONE_SCHEDULE_SECTION = "Milestone Schedule";
    public static final String INVOICE_ACCOUNT_SECTION = "Invoice Accounts";

    public static final String AWARD_MAINTENANCE_SECTION = "Award Maintenance";
    public static final String AWARD_INVOICING_SECTION = "Invoicing";
    public static final String AWARD_INVOICE_LINK_PROPOSAL_NUMBER_PATH = "documentAttribute.proposalNumber";


    public static final String INCOME_ACCOUNT = "Income";
    public static final String AR_ACCOUNT = "Accounts Receivable";
    public static final String INV_AWARD = "Invoice by Award";
    public static final String INV_ACCOUNT = "Invoice by Account";
    public static final String INV_CONTRACT_CONTROL_ACCOUNT = "Invoice by Contract Control Account";
    public static final String PREFERRED_BILLING_FREQUENCY = "preferredBillingFrequency";
    public static final String AWARD_INSTRUMENT_TYPE = "instrumentTypeCode";
    public static final String AWARD_INVOICING_OPTIONS = "invoicingOptions";
    public static final String AWARD_INVOICE_ACCOUNTS = "awardInvoiceAccounts";
    public static final String PROPOSAL_NUMBER = "proposalNumber";
    public static final String LOC_FUND_GROUP = "letterOfCreditFundGroupCode";
    public static final String LOC_FUND = "letterOfCreditFundCode";

    // Agency
    public static class AgencyFields {
        public static final String AGENCY_TAB_GENERAL_INFORMATION = "agencyGeneralInformation";
        public static final String AGENCY_TAB_ADDRESSES = "agencyAddresses";
        public static final String AGENCY_TAB_ADDRESSES_ADD_NEW_ADDRESS = "add.agencyAddresses";
        public static final String AGENCY_CUSTOMER_ADDRESS_TYPE_CODE = "customerAddressTypeCode";
        public static final String AGENCY_ADDRESS_IDENTIFIER = "agencyAddressIdentifier";
        public static final String AGENCY_NUMBER = "agencyNumber";
        public static final String AGENCY_NAME = "agencyName";
        public static final String AGENCY_ADDRESS_STATE_CODE = "agencyStateCode";
        public static final String AGENCY_ADDRESS_ZIP_CODE = "agencyZipCode";
        public static final String AGENCY_ADDRESS_INTERNATIONAL_PROVINCE_NAME = "agencyAddressInternationalProvinceName";
        public static final String AGENCY_ADDRESS_INTERNATIONAL_MAIL_CODE = "agencyInternationalMailCode";
        public static final String AGENCY_ADDRESS_END_DATE = "agencyAddressEndDate";
        public static final String AGENCY_CUSTOMER_TYPE_CODE= "customerTypeCode";
    }


    // Award Invoicing Option
    public static class AwardInvoicingOption {
        public static final Map<String, String> invoicingCode = new HashMap<String, String>();
        static {
            invoicingCode.put("1", CGPropertyConstants.INV_AWARD);
            invoicingCode.put("2", CGPropertyConstants.INV_ACCOUNT);
            invoicingCode.put("3", CGPropertyConstants.INV_CONTRACT_CONTROL_ACCOUNT);
        }
    }

    // AR related constants
    public static final String CUSTOMER = "customer";
    public static final String CUSTOMER_NUMBER = "customerNumber";
    public static final String CUSTOMER_TYPE_CODE = "customerTypeCode";

      // ProposalAwardCloseDocument
    public static final String PROPOSAL_AWARD_CLOSE_DOC_CLOSE_ON_OR_BEFORE_DATE = "closeOnOrBeforeDate";
    public static final String PROPOSAL_AWARD_CLOSE_DOC_USER_INITIATED_CLOSE_DATE = "userInitiatedCloseDate";
    public static final String PROPOSAL_AWARD_CLOSE_DOC_AWARD_CLOSED_COUNT = "awardClosedCount";
    public static final String PROPOSAL_AWARD_CLOSE_DOC_PROPOSAL_CLOSED_COUNT = "proposalClosedCount";
    public static final String PPROPOSAL_AWARD_CLOSE_DOC_RINCIPAL_NAME = "principalName";
}
