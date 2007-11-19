/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.kra;


/**
 * Holds error key constants for KRA.
 */
public class KraKeyConstants {

    // KRA confirmation messages
    public static final String QUESTION_KRA_DELETE_CONFIRMATION = "document.question.deleteContext.text";

    // KRA specific Error Codes
    public static final String ERROR_INVALID_PERIOD_LENGTH = "error.invalidPeriodLength";
    public static final String ERROR_INVALID_ORDERING = "error.invalidOrdering";
    public static final String ERROR_NONCONSECUTIVE = "error.nonconsecutive";
    public static final String ERROR_NOT_ENOUGH = "error.notEnough";
    public static final String ERROR_TOO_MANY = "error.tooMany";
    public static final String ERROR_MODULAR_TOO_MANY = "error.modular.notEnough";
    public static final String ERROR_MISSING = "error.missing";
    public static final String ERROR_INVALID_VALUE = "error.invalidValue";
    public static final String ERROR_MODULAR_VARIABLE = "error.modular.variable";
    public static final String ERROR_MODULAR_PERSONNEL = "error.modular.personnel";
    public static final String ERROR_GRAD_RATE_CHANGE_JUSTIFICATION_REQUIRED = "error.gradRateChangeJustificationRequired";
    public static final String ERROR_GRAD_RATE_TOO_HIGH = "error.gradRateTooHigh";
    public static final String ERROR_FRINGE_RATE_CHANGE_JUSTIFICATION_REQUIRED = "error.fringeRateChangeJustificationRequired";
    public static final String ERROR_MODULAR_TOO_LARGE = "error.modular.tooLarge";
    public static final String ERROR_PERSONNEL_SALARY_CHANGE_JUSTIFICATION_REQUIRED = "error.personnel.salary.change.justification.required";
    public static final String ERROR_PERSONNEL_PERIOD_EFFORT_TOO_MUCH = "error.personnel.period.effort.tooMuch";
    public static final String ERROR_PERSONNEL_PERIOD_EFFORT_TOO_MUCH_SUMMER = "error.personnel.period.effort.summer.tooMuch";
    public static final String ERROR_PERSONNEL_SUMMER_WEEKS_TOO_MUCH = "errors.summer.weeks.tooMuch";
    public static final String ERROR_PERSON_ALREADY_EXISTS_ON_BUDGET = "errors.person.already.exists";
    public static final String ERROR_PERSON_NOT_SELECTED = "errors.person.not.selected";
    public static final String ERROR_UNRECOVERED_INDIRECT_COST_NOT_POSSIBLE = "error.unrecoveredIndirectCostNotPossible";
    public static final String ERROR_INDIRECT_COST_MANUAL_JUSTIFICATION_REQUIRED = "error.indirectCostManualJustificationRequired";
    public static final String ERROR_FEE_REMISSION_DISTRIBUTION = "error.feeRemissionDistribution";
    public static final String ERROR_PER_CREDIT_HOUR_AMOUNT_ABOVE_MAXIMUM = "error.perCreditHourAmount.aboveMaximum";
    public static final String ERROR_NO_PERSON_SELECTED = "error.noPermissionSelected";
    public static final String ERROR_NO_ORG_SELECTED = "error.noOrgSelected";
    public static final String ERROR_INDIRECT_COST_MANUAL_RATE_TOO_BIG = "error.indirectCost.manualRate.tooBig";
    public static final String ERROR_INDIRECT_COST_RATE_MALFORMED = "error.indirectCost.costRate.invalidFormat";
    public static final String ERROR_APPOINTMENT_TYPE_RELATED_TYPE_CODE = "error.appointmentType.relatedTypeCode.invalid";
    public static final String ERROR_PARAMETERS_DATES_MISSING = "error.parameters.dates.missing";

    // KRA Audit Errors
    public static final String AUDIT_COST_SHARE_INSTITUTION_DISTRIBUTED = "audit.costShare.institution.distributed";
    public static final String AUDIT_COST_SHARE_3P_DISTRIBUTED = "audit.costShare.3p.distributed";
    public static final String AUDIT_MODULAR_CONSORTIUM = "audit.modular.consortium";
    public static final String AUDIT_PERSONNEL_STATUS = "audit.personnel.status";
    public static final String AUDIT_NONPERSONNEL_SUBCONTRACTOR_EXCESS_AMOUNT = "audit.nonpersonnel.subcontractorExceesAmount";
    public static final String AUDIT_PARAMETERS_NEGATIVE_IDC = "audit.parameters.negativeIdc";
    public static final String AUDIT_OTHER_PROJECT_DETAILS_NOT_SELECTED = "audit.other.project.details.not.selected";

    // Agency/Delivery Info
    public static final String AUDIT_MAIN_PAGE_AGENCY_REQUIRED = "audit.mainPage.agency.required";
    public static final String AUDIT_MAIN_PAGE_DUE_DATE_TYPE_REQUIRED = "audit.mainPage.due.date.type.required";
    public static final String AUDIT_MAIN_PAGE_COPIES_REQUIRED = "audit.mainPage.copies.required";
    public static final String AUDIT_MAIN_PAGE_ADDRESS_REQUIRED = "audit.mainPage.address.required";

    // Personnel and Units/Orgs
    public static final String AUDIT_MAIN_PAGE_PERSON_REQUIRED = "audit.mainPage.person.required";
    public static final String AUDIT_MAIN_PAGE_PERSON_NOT_PD = "audit.mainPage.person.not.pd";
    public static final String AUDIT_MAIN_PAGE_PERSON_ROLE_CODE_REQUIRED = "audit.mainPage.person.role.code.required";
    public static final String AUDIT_MAIN_PAGE_PERSON_ROLE_TEXT_REQUIRED = "audit.mainPage.person.role.text.required";
    public static final String AUDIT_MAIN_PAGE_PERSON_FA_REQUIRED = "audit.mainPage.person.fa.required";
    public static final String AUDIT_MAIN_PAGE_PERSON_CREDIT_REQUIRED = "audit.mainPage.person.credit.required";
    public static final String AUDIT_MAIN_PAGE_PD_REQUIRED = "audit.mainPage.pd.required";
    public static final String AUDIT_MAIN_PAGE_ONLY_ONE_PD = "audit.mainPage.only.one.pd.required";
    public static final String AUDIT_MAIN_PAGE_ORG_FA_REQUIRED = "audit.mainPage.org.fa.required";
    public static final String AUDIT_MAIN_PAGE_ORG_CREDIT_REQUIRED = "audit.mainPage.org.credit.required";
    public static final String AUDIT_MAIN_PAGE_TOTAL_CREDIT_PERCENT_NOT_100 = "audit.mainPage.total.credit.percent.not.100";
    public static final String AUDIT_MAIN_PAGE_TOTAL_FA_PERCENT_NOT_100 = "audit.mainPage.total.fa.percent.not.100";

    // Submission Details
    public static final String AUDIT_MAIN_PAGE_SUBMISSION_TYPE_REQUIRED = "audit.mainPage.submission.type.required";
    public static final String AUDIT_MAIN_PAGE_SUBMISSION_TYPE_FEDID_REQUIRED = "audit.mainPage.submission.type.fedid.required";
    public static final String AUDIT_MAIN_PAGE_PROJECT_TYPE_REQUIRED = "audit.mainPage.project.type.required";
    public static final String AUDIT_MAIN_PAGE_PROJECT_TYPE_OTHER_REQUIRED = "audit.mainPage.project.type.other.required";
    public static final String AUDIT_MAIN_PAGE_PROJECT_TYPE_INVALID = "audit.mainPage.project.type.invalid";
    public static final String AUDIT_MAIN_PAGE_PROJECT_TYPE_NEW_AND_PRIOR_GRANT = "audit.mainPage.project.type.new.and.prior.grant";
    public static final String AUDIT_MAIN_PAGE_PROJECT_TYPE_SELECTION_AND_GRANT = "audit.mainPage.project.type.selection.and.grant";
    public static final String AUDIT_MAIN_PAGE_PURPOSE_REQUIRED = "audit.mainPage.purpose.required";
    public static final String AUDIT_MAIN_PAGE_PURPOSE_RESEARCH_TYPE_REQUIRED = "audit.mainPage.purpose.research.type.required";
    public static final String AUDIT_MAIN_PAGE_PURPOSE_OTHER_REQUIRED = "audit.mainPage.purpose.other.required";
    public static final String AUDIT_MAIN_PAGE_TITLE_REQUIRED = "audit.mainPage.title.required";
    public static final String AUDIT_MAIN_PAGE_LAY_DESCRIPTION_REQUIRED = "audit.mainPage.lay.description.required";
    public static final String AUDIT_MAIN_PAGE_ABSTRACT_REQUIRED = "audit.mainPage.abstract.required";

    // Amounts & Dates
    public static final String AUDIT_MAIN_PAGE_DIRECT_REQUIRED = "audit.mainPage.direct.required";
    public static final String AUDIT_MAIN_PAGE_INDIRECT_REQUIRED = "audit.mainPage.indirect.required";
    public static final String AUDIT_MAIN_PAGE_START_DATE_REQUIRED = "audit.mainPage.start.date.required";
    public static final String AUDIT_MAIN_PAGE_END_DATE_REQUIRED = "audit.mainPage.end.date.required";
    public static final String AUDIT_MAIN_PAGE_TOTAL_DIRECT_REQUIRED = "audit.mainPage.total.direct.required";
    public static final String AUDIT_MAIN_PAGE_TOTAL_INDIRECT_REQUIRED = "audit.mainPage.total.indirect.required";
    public static final String AUDIT_MAIN_PAGE_TOTAL_START_DATE_REQUIRED = "audit.mainPage.total.start.date.required";
    public static final String AUDIT_MAIN_PAGE_TOTAL_END_DATE_REQUIRED = "audit.mainPage.total.end.date.required";
    public static final String AUDIT_MAIN_PAGE_DIRECT_GREATER_TOTAL_DIRECT = "audit.mainPage.direct.greater.total.direct";
    public static final String AUDIT_MAIN_PAGE_INDIRECT_GREATER_TOTAL_INDIRECT = "audit.mainPage.indirect.greater.total.indirect";
    public static final String AUDIT_MAIN_PAGE_START_DATE_GREATER_TOTAL_START_DATE = "audit.mainPage.start.date.greater.total.start.date";
    public static final String AUDIT_MAIN_PAGE_END_DATE_LESS_TOTAL_END_DATE = "audit.mainPage.end.date.less.total.end.date";
    public static final String AUDIT_MAIN_PAGE_DIRECT_LESS_INDIRECT = "audit.mainPage.direct.less.indirect";
    public static final String AUDIT_MAIN_PAGE_TOTAL_DIRECT_LESS_TOTAL_INDIRECT = "audit.mainPage.total.direct.less.total.indirect";
    public static final String AUDIT_MAIN_PAGE_START_DATE_BEFORE_END_DATE = "audit.mainPage.start.date.before.end.date";
    public static final String AUDIT_MAIN_PAGE_TOTAL_START_DATE_BEFORE_TOTAL_END_DATE = "audit.mainPage.total.start.date.before.total.end.date";
    public static final String AUDIT_MAIN_PAGE_SUBCONTRACTOR_TOTAL_GREATER_DIRECT = "audit.mainPage.subcontractor.total.greater.direct";

    public static final String ERROR_INVALID_AMOUNT_POSITIVE_ONLY = "error.invalid.amount.positive.only";
    public static final String ERROR_INVALID_AMOUNT_NOT_NEGATIVE = "error.invalid.amount.not.negative";
    public static final String ERROR_ORG_ALREADY_EXISTS_ON_RF = "error.org.already.exists.on.rf";
    public static final String ERROR_ACCOUNT_ALREADY_EXISTS_ON_RF = "error.account.already.exists.on.rf";
    public static final String ERROR_ORG_NOT_FOUND = "error.org.not.found";
    public static final String ERROR_SUBCONTRACTOR_NOT_FOUND = "error.subcontractor.not.found";
    public static final String ERROR_SUBCONTRACTOR_NOT_SELECTED = "error.subcontractor.not.selected";
    public static final String ERROR_SUBCONTRACTOR_ALREADY_EXISTS_ON_RF = "error.subcontractor.already.exists.on.rf";
    public static final String ERROR_FRINGE_RATE_TOO_LARGE = "error.fringeRate.tooLarge";
    public static final String ERROR_PERSON_NOT_NAMED = "error.person.not.named";

    // Routing Form Research Risks page errors
    public static final String ERROR_APPROVAL_DATE_REQUIRED = "error.approvalDate.required";
    public static final String ERROR_APPROVAL_DATE_REMOVE = "error.approvalDate.remove";
    public static final String ERROR_EXPIRATION_DATE_REMOVE = "error.expirationDate.remove";
    public static final String ERROR_EXEMPTION_NUMBER_REQUIRED = "error.exemptionNumber.required";
    public static final String ERROR_EXEMPTION_NUMBER_REMOVE = "error.exemptionNumber.remove";
    public static final String ERROR_HUMAN_SUBJECTS_APPROVAL_DATE_TOO_OLD = "error.humanSubjects.approvalDate.tooOld";
    public static final String ERROR_ANIMALS_APPROVAL_DATE_TOO_OLD = "error.animals.approvalDate.tooOld";
    public static final String ERROR_EXPIRATION_DATE_TOO_EARLY = "error.expiration.tooEarly";

    // Module Link page
    public static final String ERROR_BUDGET_ALREADY_LINKED = "error.budget.already.linked";
    public static final String ERROR_DOCUMENT_NUMBER_NOT_BUDGET_DOCUMENT = "error.document.number.not.budget";
    public static final String ERROR_DOCUMENT_NUMBER_NOT_EXIST = "error.document.number.not.exist";
    public static final String ERROR_SELECTED_PERIODS_CONSECUTIVE = "error.selected.periods.consecutive";
    public static final String ERROR_AT_LEAST_ONE_PERIOD = "error.at.least.one.period";

    // Global Messages
    public static final Object BUDGET_OVERRIDE = "document.budget.override";

}