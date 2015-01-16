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
 * Holds error key constants for CG.
 */
public class CGKeyConstants {

    public static final String SUBJECT_CLOSE_JOB_FAILED = "message.subject.closeJobFailed";
    public static final String SUBJECT_CLOSE_JOB_SUCCEEDED = "message.subject.closeJobSucceeded";
    public static final String MESSAGE_CLOSE_JOB_SUCCEEDED = "message.closeJob.succeeded";
    public static final String ERROR_CLOSE_JOB_FAILED = "error.cg.closeJobFailed";

    public static final String ERROR_DUPLICATE_AWARD_ACCOUNT = "error.cg.duplicate.awardAccount";
    public static final String ERROR_DUPLICATE_AWARD_PROJECT_DIRECTOR = "error.cg.duplicate.awardProjectDirector";
    public static final String ERROR_DUPLICATE_AWARD_ORGANIZATION = "error.cg.duplicate.awardOrganization";

    // Award Constants and errors
    public static class AwardConstants {
        public static final String ERROR_MILESTONE_AND_PREDETERMINED_BILLING_FREQUENCY_MUST_HAVE_ONLY_ONE_AWARD_ACCOUNT = "error.cg.award.milestone.and.predetermined.billingFrequency.must.have.one.account";
        public static final String ERROR_CG_ACTIVE_MILESTONES_EXIST = "error.cg.active.milestones.exist";
        public static final String ERROR_CG_ACTIVE_BILLS_EXIST = "error.cg.active.bills.exist";
    }

    // Agency constants and errors
    public static class AgencyConstants {
        public static final String MESSAGE_AGENCY_WITH_SAME_NAME_EXISTS = "message.document.agencyMaintenance.agencyWithSameNameExists";
        public static final String GENERATE_AGENCY_QUESTION_ID = "GenerateAgencyQuestionID";
        public static final String ERROR_AT_LEAST_ONE_ADDRESS = "error.document.agency.addressRequired";
        public static final String ERROR_ONLY_ONE_PRIMARY_ADDRESS = "error.document.agency.oneAndOnlyOnePrimaryAddressRequired";
        public static final String ERROR_AGENCY_NAME_LESS_THAN_THREE_CHARACTERS = "error.document.agency.agencyNameLessThanThreeCharacters";
        public static final String MESSAGE_SELECT_PRIMARY_AGENCY_ADDRESS_TYPE = "message.document.agency.selectPrimaryAddressType";
        public static final String ERROR_AGENCY_NAME_NO_SPACES_IN_FIRST_THREE_CHARACTERS = "error.document.agency.agencyNameNoSpacesInFirstThreeCharacters";

        public static final String AGENCY_ADDRESS_TYPE_CODE_PRIMARY = "P";
        public static final String AGENCY_ADDRESS_TYPE_CODE_ALTERNATE = "A";
        public static final String AGENCY_ADDRESS_TYPE_CODE_US = "US";
        public static final String ERROR_AGENCY_ADDRESS_STATE_CODE_REQUIRED_WHEN_COUNTTRY_US = "error.document.agency.stateCodeIsRequiredWhenCountryUS";
        public static final String ERROR_AGENCY_ADDRESS_ZIP_CODE_REQUIRED_WHEN_COUNTTRY_US = "error.document.agency.zipCodeIsRequiredWhenCountryUS";
        public static final String ERROR_AGENCY_ADDRESS_INTERNATIONAL_PROVINCE_NAME_REQUIRED_WHEN_COUNTTRY_NON_US = "error.document.agency.addressInternationalProvinceNameIsRequiredWhenCountryNonUS";
        public static final String ERROR_AGENCY_ADDRESS_INTERNATIONAL_MAIL_CODE_REQUIRED_WHEN_COUNTTRY_NON_US = "error.document.agency.internationalMailCodeIsRequiredWhenCountryNonUS";
        public static final String ERROR_TAX_NUMBER_IS_REQUIRED = "error.document.agency.taxNumberRequired";
        public static final String ACTIONS_REPORT = "report";
        public static final String ACTIONS_UPLOAD = "upload";
        public static final String ACTIONS_DOWNLOAD = "download";
        public static final String ERROR_AGENCY_ADDRESS_END_DATE_MUST_BE_FUTURE_DATE = "error.document.agency.endDateMustBeFutureDate";
        public static final String ERROR_AGENCY_ADDRESS_END_DATE_MUST_BE_CURRENT_OR_FUTURE_DATE = "error.document.agency.endDateMustBeCurrenOrFutureDate";
        public static final String ERROR_AGENCY_PRIMARY_ADDRESS_MUST_HAVE_FUTURE_END_DATE = "error.document.agency.primaryAddressMustHaveFutureEndDate";
        public static final String ERROR_AGENCY_CUSTOMER_TYPE_CODE_REQUIRED_WHEN_AGENCY_CUSTOMER_NEW = "error.document.agency.customerTypeCodeisRequiredWhenNewCustomer";
        public static final String ERROR_AGECNY_CUSTOMER_NUMBER_REQUIRED_WHEN_AGENCY_CUSTOMER_EXISTING = "error.document.agency.customerNumberRequiredWhenExistingCustomer";
        public static final String ERROR_AGENCY_ACTUAL_CUSTOMER_REQUIRED_WHEN_AGENCY_CUSTOMER_EXISTING = "error.document.agency.actualCustomerRequiredWhenExistingCustomer";


    }
}
