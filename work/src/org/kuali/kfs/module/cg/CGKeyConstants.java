/*
 * Copyright 2005-2008 The Kuali Foundation
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
        public static final String ERROR_ONE_AR_INV_ACCT_REQD = "error.cg.one.ar.account.required";
        public static final String ERROR_MULTIPLE_INV_ACCT = "error.cg.multiple.inv.account";
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


    }
}
