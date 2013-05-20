/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.integration.cg;

public class ContractsAndGrantsConstants {

    public static final String MODULE_TARGET_NAMESPACE = "KFS";

    private ContractsAndGrantsConstants() {
    }

    public static class KcWebService {
        public static final String STATUS_KC_SUCCESS = "success";
        public static final String STATUS_KC_FAILURE = "failure";
        public static final String ERROR_KC_WEB_SERVICE_FAILURE = "error.kc.document.unable to access the KC web server: ";
    }

    public static class AwardCreationService {
        public static final String WEB_SERVICE_NAME = "awardCreationService";
        public static final String ERROR_KC_DOCUMENT_INVALID_USER = "error.kc.document.invalid.user";
        public static final String ERROR_UNABLE_TO_UPDATE_AWARD_RECORD_NOT_FOUND = "Unable to update Award because record is not found";
        public static final String AUTOMATIC_CREATE_CG_AWARD_MAINTENANCE_DOCUMENT_DESCRIPTION = "Automatic CG Award Document Creation";
        public static final String AUTOMATIC_UPDATE_CG_AWARD_MAINTENANCE_DOCUMENT_DESCRIPTION = "Automatic CG Award Document Update";
        public static final String WARNING_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS = "warning.kc.document.workflowException.document.actions";
        public static final String ERROR_CG_PROPOSAL_CREATION_FAILED = "error.kc.document.proposal.creation.failed";
        public static final String ERROR_CG_AWARD_TO_UPDATE_NOT_YET_FINAL = "error.cg.award.to.update.not.yet.final";
        public static final String ACCOUNT_NUMBER_AND_CHART_CODE_CANNOT_BE_BLANK = "Account Number and Chart Code cannot be blank";
        public static final String ACCOUNT_NUMBER_AND_CHART_CODE_CANNOT_DOES_NOT_EXIST = "error.cg.award.account.number.chart.code.does.not.exist";
        public static final String ERROR_SPONSOR_CODE_DOES_NOT_EXIST = "error.cg.sponsor.code.does.not.exist";
    }

    public static class AgencyCreationService {
        public static final String WEB_SERVICE_NAME = "agencyCreationService";
        public static final String ERROR_KC_DOCUMENT_INVALID_USER = "error.kc.document.invalid.user";
        public static final String AUTOMATIC_CREATE_CG_AGENCY_MAINTENANCE_DOCUMENT_DESCRIPTION = "Automatic CG Agency Document Creation";
        public static final String AUTOMATIC_UPDATE_CG_AGENCY_MAINTENANCE_DOCUMENT_DESCRIPTION = "Automatic CG Agency Document Update";
        public static final String WARNING_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS = "warning.kc.document.workflowException.document.actions";
        public static final String ERROR_UNABLE_TO_UPDATE_AGENCY_RECORD_NOT_FOUND = "Unable to update agency because record is not found";
        public static final String PARAMETER_DEFAULT_AGENCY_TYPE_CODE = "DEFAULT_AGENCY_TYPE_CODE";
        public static final String PARAMETER_COMPONENT_AGENCY_TYPE = "AgencyType";
    }

    public static class ProposalCreationService {
        public static final String WEB_SERVICE_NAME = "proposalCreationService";
        public static final String ERROR_KC_DOCUMENT_INVALID_USER = "error.kc.document.invalid.user";
        public static final String AUTOMATIC_CREATE_CG_PROPOSAL_MAINTENANCE_DOCUMENT_DESCRIPTION = "Automatic CG Proposal Document Creation";
        public static final String ERROR_KC_PROPOSAL_PARAMS_UNIT_NOTFOUND = "error.kc.proposal.params.unit.notfound";
        public static final String ERROR_KC_DOCUMENT_PROPOSAL_RULES_EXCEPTION = "error.kc.document.proposal.rules.exception";
    }

    public static class AccountCreationService {

        public static final String WEB_SERVICE_NAME = "accountCreationService";

        public static final String PARAMETER_KC_ACCOUNT_ADMIN_AUTO_CREATE_ACCOUNT_WORKFLOW_ACTION = "RESEARCH_ADMIN_AUTO_CREATE_ACCOUNT_WORKFLOW_ACTION";
        public static final String PARAMETER_KC_OVERRIDES_KFS_DEFAULT_ACCOUNT_IND = "KC_OVERRIDES_KFS_DEFAULT_ACCOUNT_IND";
        public static final String PARAMETER_KC_ACCOUNT_ADDRESS_TYPE = "ACCOUNT_ADDRESS_TYPE";
        public static final String PARAMETER_KC_ACCOUNT_CREATE_ROUTE = "ACCOUNT_AUTO_CREATE_ROUTE";

        public static final String ADMIN_ADDRESS_TYPE = "ADMIN";
        public static final String UNIT_ADDRESS_TYPE = "UNIT";
        public static final String PI_ADDRESS_TYPE = "PI";
        public static final String ERROR_KC_ACCOUNT_NOALLOWEDTOALTERUNIT = "error.kc.account.notAllowedToAlterUnit";
        public static final String ERROR_KC_ACCOUNT_ALREADY_DEFINED = "error.kc.account.params.KcUnitDefined";
        public static final String ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND = "error.kc.account.params.unit.notfound";
        public static final String ERROR_KC_DOCUMENT_NOT_ALLOWED_TO_CREATE_CG_MAINTENANCE_DOCUMENT = "error.kc.document.notAllowedToCreateCGMaintenanceDocument";
        public static final String ERROR_KC_DOCUMENT_UNABLE_TO_CREATE_CG_MAINTENANCE_DOCUMENT = "error.kc.document.unableToCreateCGMaintenanceDocument";
        public static final String ERROR_KC_DOCUMENT_UNABLE_TO_PROCESS_ROUTING = "error.kc.document.unableToProcessRouting";
        public static final String ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS = "error.kc.document.workflowException.document.actions";
        public static final String ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_UNABLE_TO_CREATE_DOCUMENT = "error.kc.document.workflowException.unableToCreateDocument";
        public static final String ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_UNABLE_TO_SAVE_DOCUMENT = "error.kc.document.workflowException.unableToSaveDocument";
        public static final String ERROR_KC_DOCUMENT_ACCOUNT_RULES_EXCEPTION = "error.kc.document.account.rules.exception";
        public static final String ERROR_KC_DOCUMENT_SYSTEM_PARAMETER_INCORRECT_DOCUMENT_ACTION_VALUE = "The system parameter value for ACCOUNT_AUTO_CREATE_ROUTE should be either Save or Submit or BlanketApprove.";
        public static final String ERROR_KC_ACCOUNT_PARAMS_UNIT_NOT_DEFINED = "Unit cannot be found in KFS";
        public static final String WARNING_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS = "warning.kc.document.workflowException.document.actions";
        public static final String ERROR_KR_ALPHANUMERIC_VALIDATION_EXACT_LENGTH = "error.format.org.kuali.rice.kns.datadictionary.validation.charlevel.AlphaNumericValidationPattern.exactLength";
        public static final String AUTOMATCICG_ACCOUNT_MAINTENANCE_CHART_NOT_DEFINED = "Chart of Accounts Code is not defined";
        public static final String AUTOMATCICG_ACCOUNT_MAINTENANCE_EFFECTIVEDATE_NOT_DEFINED = "Effective Date is not defined";
        public static final String AUTOMATCICG_ACCOUNT_MAINTENANCE_EXPIRATIONDATE_NOT_DEFINED = "Expiration Date is not defined";
        public static final String AUTOMATCICG_ACCOUNT_MAINTENANCE_CHART_REQUIRED_FIELD = " is a required field";
        public static final String AUTOMATCICG_ACCOUNT_MAINTENANCE_ACCT_ALREADY_DEFINED = "Account is already defined in KFS";
        public static final String AUTOMATCICG_ACCOUNT_MAINTENANCE_DOCUMENT_DESCRIPTION = "Automatic CG Account Document Creation";
        public static final String ERROR_KC_DOCUMENT_ACCOUNT_GENERATION_PROBLEM = "Unable to generate KFS Account from data parameters";
        public static final String ERROR_KC_DOCUMENT_ACCOUNT_MISSING_CHART_OR_ACCT_NBR = "The chart of accounts code or account number is missing.";
        public static final String ERROR_KC_DOCUMENT_INVALID_USER = "error.kc.document.invalid.user";
    }

    public static class BudgetAdjustmentService {

        public static final String WEB_SERVICE_NAME = "budgetAdjustmentService";
        public static final String PARAMETER_KC_ADMIN_AUTO_BA_DOCUMENT_WORKFLOW_ROUTE = "RESEARCH_ADMIN_BA_DOCUMENT_ROUTE_ACTION";
        public static final String PARAMETER_KC_ENABLE_RESEARCH_ADMIN_OBJECT_CODE_ATTRIBUTE_IND = "ENABLE_RESEARCH_ADMIN_OBJECT_CODE_ATTRIBUTE_IND";
        public static final String PARAMETER_INCOME_OBJECT_CODES_BY_SPONSOR_TYPE = "RESEARCH_ADMIN_INCOME_OBJECT_CODE_BY_SPONSOR_TYPE";
        public static final String SECTION_ID_RESEARCH_ADMIN_ATTRIBUTES = "researchAdminAttributes";

        public static final String ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND = "error.kc.account.params.unit.notfound";
        public static final String ERROR_KC_DOCUMENT_NOT_ALLOWED_TO_CREATE_CG_MAINTENANCE_DOCUMENT = "error.kc.document.notAllowedToCreateCGMaintenanceDocument";
        public static final String ERROR_KC_DOCUMENT_UNABLE_TO_CREATE_CG_MAINTENANCE_DOCUMENT = "error.kc.document.unableToCreateCGMaintenanceDocument";
        public static final String ERROR_KC_DOCUMENT_UNABLE_TO_PROCESS_ROUTING = "error.kc.document.unableToProcessRouting";
        public static final String ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS = "error.kc.document.workflowException.document.actions";
        public static final String ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_NOT_SAVED = "error.kc.document.workflowException.document.not.saved";
        public static final String ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_UNABLE_TO_CREATE_DOCUMENT = "error.kc.document.workflowException.unableToCreateDocument";
        public static final String ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_UNABLE_TO_SAVE_DOCUMENT = "error.kc.document.workflowException.unableToSaveDocument";
        public static final String ERROR_KC_DOCUMENT_SYSTEM_PARAMETER_INCORRECT_DOCUMENT_ACTION_VALUE = "The system parameter value for RESEARCH_ADMIN_BA_DOCUMENT_ROUTE_ACTION should be either Save or Submit or BlanketApprove.";
        public static final String ERROR_KC_DOCUMENT_SYSTEM_PARAMETER_INCORRECT_VALUE = "The KFS system parameter value is invalid : ";
        public static final String ERROR_KC_DOCUMENT_AMT_IS_NONUMERIC = "The current amount on the accounting line with object code {0} cannot be {1}";
        public static final String WARNING_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS = "warning.kc.document.workflowException.document.actions";
        public static final String ERROR_KC_DOCUMENT_BA_RULES_EXCEPTION = "error.kc.document.ba.rules.exception";

        public static final String AUTOMATCICG_ACCOUNT_MAINTENANCE_DOCUMENT_DESCRIPTION = "Automatic BA Document Creation";
        public static final String AUTOMATCICG_ACCOUNT_MAINTENANCE_CHART_REQUIRED_FIELD = "{0} is a required field";
        public static final String ERROR_KC_DOCUMENT_ACCOUNT_GENERATION_PROBLEM = "Unable to generate KFS BudgetAdjustment Doc from data parameters";
        public static final String ERROR_KC_DOCUMENT_INVALID_ACCT = "The KFS account {0} {1} is invalid: ";
        public static final String ERROR_KC_DOCUMENT_INVALID_OBJECTCODE = "The object code {0} {1} is not in the financial system";
        public static final String ERROR_KC_DOCUMENT_INACTIVE_OBJECTCODE = "The object code {0} {1} is inactive for the year {2}";
        public static final String ERROR_KC_DOCUMENT_INVALID_USER = "error.kc.document.invalid.user";
    }

    public static class ObjectCodeService {
        public static final String WEB_SERVICE_NAME = "kcObjectCodeService";
    }

    public static class BudgetCategoryService {
        public static final String WEB_SERVICE_NAME = "budgetCategoryDTOLookupableHelperService";

    }

    public static class AccountsCanCrossChartsIndService {
        public static final String WEB_SERVICE_NAME = "KcAccountService";
    }

    public static class RiceApplicationConfigurationService {
        public static final String WEB_SERVICE_NAME = "KcRiceApplicationConfigurationService";
    }
}
