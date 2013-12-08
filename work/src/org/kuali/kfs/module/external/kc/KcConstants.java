/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc;

import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

public class KcConstants {

    public static final String KC_NAMESPACE_URI = "http://kc.kuali.org/kc-kfs/v2_0";
    public static final String KFS_NAMESPACE_URI = "http://kfs.kuali.org/kc-kfs/v2_0";
    public static final String MAXIMUM_ACCOUNT_RESPONSIBILITY_ID = "MAXIMUM_ACCOUNT_RESPONSIBILITY_ID";
    public static final String FEDERAL_SPONSOR_TYPE_CODES= "FEDERAL_SPONSOR_TYPE_CODES";
    public static final String ACCOUNT_CREATE_DEFAULT_IDENTIFIER = "accountDefaultId";
    public static final String WEBSERVICE_UNREACHABLE = "Access to the web service is unreachable: ";

    public static class AccountCreationDefaults {
        public static final String CHART_OF_ACCOUNT_CODE = "chartOfAccountsCode";
        public static final String ACCOUNT_NUMBER = "accountNumber";
        public static final String KcUnit = "kcUnit";
    }
    public static class BudgetAdjustment {
        public static final String SOAP_SERVICE_NAME = "budgetAdjustmentServiceSOAP";
        public static QName SERVICE = new QName("KFS", SOAP_SERVICE_NAME);
    }
    public static class BudgetCategory {
        public static final String SOAP_SERVICE_NAME = "budgetCategorySoapService";
        public static final String SERVICE_PORT = "budgetCategoryServicePort";
        public static final String SERVICE_NAME = "budgetCategoryService";
        public static QName SERVICE = new QName(KC_NAMESPACE_URI, SOAP_SERVICE_NAME);
        public static final List <String> KC_ALLOWABLE_CRITERIA_PARAMETERS = Arrays.asList("budgetCategoryTypeCode","description","budgetCategoryCode");
    }
    public static class Cfda {
        public static final String SOAP_SERVICE_NAME = "cfdaNumberSoapService";
        public static final String SERVICE_PORT = "CfdaNumberServicePort";
        public static final String SERVICE_NAME ="CfdaNumberService";
        public static QName SERVICE = new QName(KC_NAMESPACE_URI, SOAP_SERVICE_NAME);
        public static final List <String> KC_ALLOWABLE_CRITERIA_PARAMETERS= Arrays.asList("cfdaNumber", "cfdaMaintenanceTypeId", "cfdaProgramTitleName", "active");
    }
    public static class Unit {
        public static final String SOAP_SERVICE_NAME = "institutionalUnitSoapService";
        public static final String SERVICE_PORT = "institutionalUnitServicePort";
        public static QName SERVICE = new QName(KC_NAMESPACE_URI, SOAP_SERVICE_NAME);
        public static final List <String> KC_ALLOWABLE_CRITERIA_PARAMETERS = Arrays.asList("unitName","unitNumber","parentUnitNumber","organizationId");
    }
    public static class EffortReporting {
        public static final String SOAP_SERVICE_NAME = "effortReportingServiceSoapService";
        public static final String SERVICE_PORT = "effortReportingServicePort";
        public static final String SERVICE_NAME = "effortReportingService";
        public static QName SERVICE = new QName(KC_NAMESPACE_URI, SOAP_SERVICE_NAME);
    }
    public static class AwardAccount {
        public static final String SOAP_SERVICE_NAME = "awardAccountSoapService";
        public static final String SERVICE_PORT = "awardAccountServicePort";
        public static final String SERVICE_NAME ="awardAccountService";

        public static QName SERVICE = new QName(KC_NAMESPACE_URI, SOAP_SERVICE_NAME);
        public static final List <String> KC_ALLOWABLE_CRITERIA_PARAMETERS = Arrays.asList("accountNumber", "chartOfAccountsCode");
    }
    public static class Sponsor {
        public static final String SOAP_SERVICE_NAME = "sponsorWebSoapService";
        public static final String SERVICE_PORT = "sponsorWebServicePort";
        public static QName SERVICE = new QName(KC_NAMESPACE_URI, SOAP_SERVICE_NAME);
        public static final List <String> KC_ALLOWABLE_CRITERIA_PARAMETERS = Arrays.asList("sponsorCode", "sponsorName");
    }
    public static class Award {
        public static final String SOAP_SERVICE_NAME = "awardWebSoapService";
        public static final String SERVICE_PORT = "awardWebServicePort";
        public static QName SERVICE = new QName(KC_NAMESPACE_URI, SOAP_SERVICE_NAME);
        public static final List <String> KC_ALLOWABLE_CRITERIA_PARAMETERS = Arrays.asList("awardId", "sponsorCode");
    }
    public static class AwardType {
        public static final String SOAP_SERVICE_NAME = "awardTypeWebSoapService";
        public static final String SERVICE_PORT = "awardTypeWebServicePort";
        public static QName SERVICE = new QName(KC_NAMESPACE_URI, SOAP_SERVICE_NAME);
    }
    public static class AwardPayment {
        public static final String SOAP_SERVICE_NAME = "awardPaymentWebSoapService";
        public static final String SERVICE_PORT = "awardPaymentWebServicePort";
        public static QName SERVICE = new QName(KC_NAMESPACE_URI, SOAP_SERVICE_NAME);
    }

    public static class KcWebService {
        public static final String STATUS_KC_SUCCESS = "success";
        public static final String STATUS_KC_FAILURE = "failure";
        public static final String ERROR_KC_WEB_SERVICE_FAILURE = "error.kc.document.unable to access the KC web server: ";
    }
    public static class AccountCreationService {

        public static final String WEB_SERVICE_NAME = "accountCreationService";

        public static final String PARAMETER_KC_ACCOUNT_ADMIN_AUTO_CREATE_ACCOUNT_WORKFLOW_ACTION = "RESEARCH_ADMIN_AUTO_CREATE_ACCOUNT_WORKFLOW_ACTION";
        public static final String PARAMETER_KC_OVERRIDES_KFS_DEFAULT_ACCOUNT_IND="KC_OVERRIDES_KFS_DEFAULT_ACCOUNT_IND";
        public static final String PARAMETER_KC_ACCOUNT_ADDRESS_TYPE = "RESEARCH_ADMIN_ACCOUNT_ADDRESS_TYPE";
        public static final String PARAMETER_KC_ACCOUNT_CREATE_ROUTE = "ACCOUNT_AUTO_CREATE_ROUTE";

        public static final String ADMIN_ADDRESS_TYPE = "ADMIN";
        public static final String UNIT_ADDRESS_TYPE = "UNIT";
        public static final String PI_ADDRESS_TYPE = "PI";
        public static final String ERROR_KC_ACCOUNT_NOALLOWEDTOALTERUNIT="error.kc.account.notAllowedToAlterUnit";
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
        public static final String ERROR_KC_DOCUMENT_INVALID_USER="error.kc.document.invalid.user";
    }

    public static class BudgetAdjustmentService {

        public static final String WEB_SERVICE_NAME = "budgetAdjustmentService";
        public static final String PARAMETER_KC_ADMIN_AUTO_BA_DOCUMENT_WORKFLOW_ROUTE = "RESEARCH_ADMIN_BA_DOCUMENT_ROUTE_ACTION";
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
        public static final String ERROR_KC_DOCUMENT_INVALID_ACCT="The KFS account {0} {1} is invalid: ";
        public static final String ERROR_KC_DOCUMENT_INVALID_OBJECTCODE="The object code {0} {1} is not in the financial system";
        public static final String ERROR_KC_DOCUMENT_INACTIVE_OBJECTCODE="The object code {0} {1} is inactive for the year {2}";
        public static final String ERROR_KC_DOCUMENT_INVALID_USER="error.kc.document.invalid.user";
    }

    public static class DunningCampaignService {
        public static final String WEB_SERVICE_NAME = "dunningCampaignService";
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
