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

import java.util.Calendar;

import org.kuali.core.util.KualiInteger;
import org.kuali.rice.util.JSTLConstants;

public class KraConstants extends JSTLConstants {
    private static final long serialVersionUID = 5725060921632498564L;

    public static final String SHORT_TIMESTAMP_FORMAT = "MM/dd/yyyy";
    public static final String LONG_TIMESTAMP_FORMAT = "MM/dd/yyyy HH:mm:ss";

    public static final int maximumPeriodLengthUnits = Calendar.YEAR;
    public static final int maximumNumberOfPeriods = 20; // used on budgetPeriods.tag

    public static final int maximumNumberOfTasks = 20; // used on budgetTasks.tag

    public static final int projectDirectorRouteLevel = 1;

    public static final String SUBCONTRACTOR_CATEGORY_CODE = "SC"; // used on budgetCostShare*.tag and budgetNonpersonnel.tag

    public static final Integer TASK_SUMMATION = new Integer(0); // budgetDetailSelection.tag defines 0 as a task summation
    public static final Integer PERIOD_SUMMATION = new Integer(0); // budgetDetailSelection.tag defines 0 as a period summation

    public static final KualiInteger PERSONNEL_MAX_PERCENTAGE = new KualiInteger(100);

    public static final String INSTITUTION_COST_SHARE_CODE = "I";
    public static final String THIRD_PARTY_COST_SHARE_CODE = "T";

    public static final String PERMISSION_MOD_CODE = "M";
    public static final String PERMISSION_READ_CODE = "R";

    public static final String DATABASE_TRUE_VALUE = "Y";

    public static final String QUESTION_ROUTE_DOCUMENT_TO_COMPLETE = "Completing this document will remove it from your Action List.<br/><br/>  Are you sure you want to continue?";
    public static final String PROJECT_DIRECTOR = "Project Director";

    public static final String PERIOD = "period";

    public static final String TASK = "task";

    public static final String DROPDOWN_LIST_SELECT = "select:";

    // System Parameters
    public static final String BUDGET_PERIODS_HELP_PARAMETER_NAME = "PERIOD";
    public static final String BUDGET_COSTSHARE_INDIRECT_HELP_PARAMETER_NAME = "INSTITUTION_COST_SHARE_INDIRECT";
    public static final String BUDGET_OUTPUT_HELP_PARAMETER_NAME = "OUTPUT";
    public static final String BUDGET_OVERVIEW_HELP_PARAMETER_NAME = "OVERVIEW";
    public static final String BUDGET_NONPERSONNEL_COPY_OVER_HELP_PARAMETER_NAME = "NONPERSONNEL_COPY_OVER";
    public static final String BUDGET_TEMPLATE_HELP_PARAMETER_NAME = "TEMPLATE";
    public static final String BUDGET_BASE_CODE_DEFAULT_VALUE_PARAMETER_NAME = "BASE_CODE_DEFAULT_VALUE";
    public static final String BUDGET_MANUAL_RATE_INDICATOR_DEFAULT_VALUE_PARAMETER_NAME = "MANUAL_RATE_INDICATOR_DEFAULT_VALUE";
    public static final String BUDGET_PURPOSE_CODE_DEFAULT_VALUE_PARAMETER_NAME = "PURPOSE_CODE_DEFAULT_VALUE";
    public static final String BUDGET_MAX_INFLATION_RATE_PARAMETER_NAME = "MAX_INFLATION_RATE";
    public static final String PROJECT_DIRECTOR_BUDGET_PERMISSION = "PROJECT_DIRECTOR_PERMISSION";
    public static final String PROJECT_DIRECTOR_ORG_BUDGET_PERMISSION = "PROJECT_DIRECTOR_ORG_PERMISSION";
    public static final String COST_SHARE_ORGS_BUDGET_PERMISSION = "COST_SHARE_ORGS_BUDGET_PERMISSION";
    public static final String BUDGET_COST_SHARE_PERMISSION_CODE = "COST_SHARE_PERMISSION_CODE";
    public static final String DEFAULT_BUDGET_TASK_NAME = "DEFAULT_BUDGET_TASK_NAME";
    public static final String INDIRECT_COST_MAX_MANUAL_RATE = "INDIRECT_COST_MAX_MANUAL_RATE";

    public static final String DEFAULT_APPOINTMENT_TYPE = "DEFAULT_APPOINTMENT_TYPE";
    public static final String DEFAULT_PERSONNEL_INFLATION_RATE = "DEFAULT_PERSONNEL_INFLATION_RATE";
    public static final String DEFAULT_NONPERSONNEL_INFLATION_RATE = "DEFAULT_NONPERSONNEL_INFLATION_RATE";

    public static final String FIRST25K_SUBCATEGORY_CODES = "FIRST25K_SUBCATEGORY_CODES";
    public static final String NEW_PERIOD_IDENTIFIER = "NEW_PERIOD_IDENTIFIER";
    public static final String ALLOWED_EMPLOYEE_STATUS_RULE = "EMPLOYEE_STATUSES";
    public static final String PERIOD_IDENTIFIER = "PERIOD_IDENTIFIER";

    public static final String APPROVALS_DEFAULT_WORDING = "APPROVALS_DEFAULT_WORDING";
    public static final String APPROVALS_INITIATOR_WORDING = "APPROVALS_INITIATOR_WORDING";
    public static final String APPROVALS_PROJECT_DIRECTOR_WORDING = "APPROVALS_PROJECT_DIRECTOR_WORDING";

    public static final String GRADUATE_ASSISTANT_NONPERSONNEL_DESCRIPTION = "GRADUATE_ASSISTANT_NONPERSONNEL_DESCRIPTION";
    public static final String GRADUATE_ASSISTANT_NONPERSONNEL_SUB_CATEGORY_CODE = "GRADUATE_ASSISTANT_NONPERSONNEL_SUB_CATEGORY_CODE";
    public static final String GRADUATE_ASSISTANT_NONPERSONNEL_CATEGORY_CODE = "GRADUATE_ASSISTANT_NONPERSONNEL_CATEGORY_CODE";

    public static final String KRA_BUDGET_INDIRECT_COST_PROVIDED_SYSTEM = "INDIRECT_COST_PROVIDED_SYSTEM";
    public static final String KRA_BUDGET_INDIRECT_COST_PROVIDED_MANUALLY = "INDIRECT_COST_PROVIDED_MANUALLY";
    public static final String KRA_BUDGET_PERSONNEL_SUMMER_GRID_APPOINTMENT_TYPES = "PERSONNEL_SUMMER_GRID_APPOINTMENT_TYPES";
    public static final String KRA_BUDGET_PERSONNEL_SUMMER_GRID_APPOINTMENT_TYPE = "PERSONNEL_SUMMER_GRID_APPOINTMENT_TYPE";
    public static final String KRA_BUDGET_PERSONNEL_FULL_YEAR_APPOINTMENT_TYPES = "PERSONNEL_FULL_YEAR_APPOINTMENT_TYPES";
    public static final String KRA_BUDGET_PERSONNEL_GRADUATE_RESEARCH_ASSISTANT_APPOINTMENT_TYPES = "PERSONNEL_GRADUATE_RESEARCH_ASSISTANT_APPOINTMENT_TYPES";
    public static final String KRA_BUDGET_PERSONNEL_HOURLY_APPOINTMENT_TYPES = "PERSONNEL_HOURLY_APPOINTMENT_TYPES";
    public static final String KRA_BUDGET_PERSONNEL_ACADEMIC_YEAR_APPOINTMENT_TYPE = "PERSONNEL_ACADEMIC_YEAR_APPOINTMENT_TYPE";
    public static final String GRADUATE_ASSISTANT = "gradResAssistant";
    public static final String HOURLY = "hourly";
    public static final String ACADEMIC_YEAR_SUMMER = "academicYearSummer";
    public static final String ACADEMIC_YEAR_SUMMER_ARRAY = "academicYearSummerArray";
    public static final String FULL_YEAR = "fullYear";
    public static final String ACADEMIC_SUMMER = "academicSummer";
    public static final String ACADEMIC_YEAR = "academicYear";

    public static final String MAXIMUM_NUMBER_OF_PERIODS = "MAXIMUM_NUMBER_OF_PERIODS";
    public static final String MAXIMUM_NUMBER_OF_TASKS = "MAXIMUM_NUMBER_OF_TASKS";
    public static final String MAXIMUM_PERIOD_LENGTH = "MAXIMUM_PERIOD_LENGTH";
    public static final String MINIMUM_NUMBER_OF_PERIODS = "MINIMUM_NUMBER_OF_PERIODS";
    public static final String MINIMUM_NUMBER_OF_TASKS = "MINIMUM_NUMBER_OF_TASKS";

    public static final String MAXIMUM_NUMBER_MODULAR_PERIODS = "MAXIMUM_NUMBER_MODULAR_PERIODS";

    public static final String COST_SHARE_PERMISSION_CODE_OPTIONAL = "O";
    public static final String COST_SHARE_PERMISSION_CODE_TRUE = "Y";

    public static final String TO_BE_NAMED_LABEL = "TO_BE_NAMED_LABEL";

    public static final String KRA_BUDGET_NUMBER_OF_ACADEMIC_YEAR_SUBDIVISIONS = "NUMBER_OF_ACADEMIC_YEAR_SUBDIVISIONS";
    public static final String KRA_BUDGET_ACADEMIC_YEAR_SUBDIVISION_NAMES = "ACADEMIC_YEAR_SUBDIVISION_NAMES";

    public static final String PERSONNEL_STATUSES = "PERSONNEL_STATUSES";
    public static final String CREATE_PROPOSAL_PROJECT_TYPES = "CREATE_PROPOSAL_PROJECT_TYPES";
    public static final String PROJECT_TYPES = "PROJECT_TYPES";

    public static final String ROUTING_FORM_COST_SHARE_PERMISSION_CODE = "ROUTE_TO_COST_SHARE_ORGANIZATIONS_IND";

    public static final String PERSON_ROLE_CODE_CO_PROJECT_DIRECTOR = "PERSON_ROLE_CODE_CO_PROJECT_DIRECTOR";
    public static final String PERSON_ROLE_CODE_CONTACT_PERSON = "PERSON_ROLE_CODE_CONTACT_PERSON";
    public static final String PERSON_ROLE_CODE_OTHER = "PERSON_ROLE_CODE_OTHER";
    public static final String PERSON_ROLE_CODE_PROJECT_DIRECTOR = "PERSON_ROLE_CODE_PROJECT_DIRECTOR";

    public static final String PROJECT_TYPE_BUDGET_REVISION_ACTIVE = "PROJECT_TYPE_BUDGET_REVISION_ACTIVE";
    public static final String PROJECT_TYPE_BUDGET_REVISION_PENDING = "PROJECT_TYPE_BUDGET_REVISION_PENDING";
    public static final String PROJECT_TYPE_NEW = "PROJECT_TYPE_NEW";

    // Research Risks codes (system param names)
    public static final String RESEARCH_RISKS_HUMAN_SUBJECTS_ACTIVE_CODE = "RESEARCH_RISKS_HUMAN_SUBJECTS_ACTIVE_CODE";
    public static final String RESEARCH_RISKS_ANIMALS_ACTIVE_CODE = "RESEARCH_RISKS_ANIMALS_ACTIVE_CODE";

    public static final String MANUAL_BASE = "MN";
    public static final String MODIFIED_TOTAL_DIRECT_COST = "MT";

    public static final String PROJECT_DIRECTOR_REVIEW_NODE_NAME = "Project Director";
    public static final String PROJECT_DIRECTOR_TEMPLATE_NAME = "KualiRoutingFormProjectDirectorTemplate";
    public static final String ADHOC_REVIEW_NODE_NAME = "Adhoc Approvers";
    public static final String ADHOC_REVIEW_TEMPLATE_NAME = "KualiResearchAdhocApproverTemplate";
    public static final String ORG_REVIEW_NODE_NAME = "Org Review";
    public static final String ORG_REVIEW_TEMPLATE_NAME = "KualiResearchOrgReviewTemplate";

    // UI Header Tab names
    public static final String PARAMETERS_HEADER_TAB = "Parameters";
    public static final String OVERVIEW_HEADER_TAB = "Overview";
    public static final String PERSONNEL_HEADER_TAB = "Personnel";
    public static final String NONPERSONNEL_HEADER_TAB = "Nonpersonnel";
    public static final String COST_SHARE_HEADER_TAB = "Cost Share";
    public static final String MODULAR_HEADER_TAB = "Modular";
    public static final String INDIRECT_COST_HEADER_TAB = "Indirect Cost";
    public static final String PERMISSIONS_HEADER_TAB = "Permissions";
    public static final String OUTPUT_HEADER_TAB = "Output";
    public static final String TEMPLATE_HEADER_TAB = "Template";
    public static final String AUDIT_MODE_HEADER_TAB = "Audit Mode";
    public static final String NOTES_HEADER_TAB = "Notes";

    public static final String DELETE_PERIOD_QUESTION_ID = "DeletePeriodQuestion";
    public static final String DELETE_TASK_QUESTION_ID = "DeleteTaskQuestion";
    public static final String DELETE_COST_SHARE_QUESTION_ID = "DeleteCostShareQuestion";

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
    public static final String PROJECT_TYPE_TIME_EXTENTION = "PROJECT_TYPE_TIME_EXTENTION";
    public static final String CONTACT_PERSON_PARAM = "PERSON_ROLE_CODE_CONTACT_PERSON";
    public static final String CO_PROJECT_DIRECTOR_PARAM = "PERSON_ROLE_CODE_CO_PROJECT_DIRECTOR";
    public static final String OTHER_PERSON_PARAM = "PERSON_ROLE_CODE_OTHER";
    public static final String PROJECT_DIRECTOR_PARAM = "PERSON_ROLE_CODE_PROJECT_DIRECTOR";

    // Role Codes
    public static final String PROJECT_DIRECTOR_CODE = "P";
    public static final String CO_PROJECT_DIRECTOR_CODE = "C";
    public static final String CONTACT_PERSON_ADMINISTRATIVE_CODE = "M";
    public static final String CONTACT_PERSON_PROPOSAL_CODE = "N";

    // Ad hoc types
    public static final String AD_HOC_PERMISSION = "P";
    public static final String AD_HOC_APPROVER = "A";

    // XSLT Stylesheet (Output)
    public static final String OUTPUT_PATH_PREFIX = "OUTPUT_PATH_PREFIX";

    public static final String OUTPUT_XSL_FILENAME = "OUTPUT_XSL_FILENAME";

    public static final String OUTPUT_GENERIC_BY_PERIOD_XSL_FILENAME = "OUTPUT_GENERIC_BY_PERIOD_XSL_FILENAME";
    public static final String OUTPUT_GENERIC_BY_TASK_XSL_FILENAME = "OUTPUT_GENERIC_BY_TASK_XSL_FILENAME";
    public static final String OUTPUT_NIH_MODULAR_XSL_FILENAME = "OUTPUT_NIH_MODULAR_XSL_FILENAME";
    public static final String OUTPUT_NIH2590_XSL_FILENAME = "OUTPUT_NIH2590_XSL_FILENAME";
    public static final String OUTPUT_NIH398_XSL_FILENAME = "OUTPUT_NIH398_XSL_FILENAME";
    public static final String OUTPUT_NSF_SUMMARY_XSL_FILENAME = "OUTPUT_NSF_SUMMARY_XSL_FILENAME";
    public static final String OUTPUT_SF424_XSL_FILENAME = "OUTPUT_SF424_XSL_FILENAME";

    public static class AuthorizationConstants extends org.kuali.core.authorization.AuthorizationConstants.EditMode {
        public static final String BUDGET_LINKED = "budgetLinked";
    }
}
