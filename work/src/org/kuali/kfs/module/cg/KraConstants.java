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

import org.kuali.core.JstlConstants;
import org.kuali.core.util.KualiInteger;

public class KraConstants extends JstlConstants {
    private static final long serialVersionUID = 5725060921632498564L;

    public static final int maximumPeriodLengthUnits = Calendar.YEAR;
    public static final int maximumNumberOfPeriods = 20; // used on budgetPeriods.tag

    public static final int maximumNumberOfTasks = 20; // used on budgetTasks.tag

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
    public static final String KRA_DEVELOPMENT_GROUP = "KraDevelopmentGroup";
    public static final String KRA_ADMIN_GROUP_NAME = "KraAdminGroup";
    public static final String BUDGET_PERIODS_HELP_PARAMETER_NAME = "budgetPeriodHelp";
    public static final String BUDGET_COSTSHARE_INDIRECT_HELP_PARAMETER_NAME = "budgetInstitutionCostShareIndirectHelp";
    public static final String BUDGET_OUTPUT_HELP_PARAMETER_NAME = "budgetOutputHelp";
    public static final String BUDGET_OVERVIEW_HELP_PARAMETER_NAME = "budgetOverviewHelp";
    public static final String BUDGET_NONPERSONNEL_COPY_OVER_HELP_PARAMETER_NAME = "budgetNonpersonnelCopyOverHelp";
    public static final String BUDGET_TEMPLATE_HELP_PARAMETER_NAME = "budgetTemplateHelp";
    public static final String BUDGET_BASE_CODE_DEFAULT_VALUE_PARAMETER_NAME = "budgetBaseCodeDefaultValue";
    public static final String BUDGET_MANUAL_RATE_INDICATOR_DEFAULT_VALUE_PARAMETER_NAME = "budgetManualRateIndicatorDefaultValue";
    public static final String BUDGET_PURPOSE_CODE_DEFAULT_VALUE_PARAMETER_NAME = "budgetPurposeCodeDefaultValue";
    public static final String BUDGET_MAX_INFLATION_RATE_PARAMETER_NAME = "budgetMaxInflationRate";
    public static final String PROJECT_DIRECTOR_BUDGET_PERMISSION = "projectDirectorBudgetPermission";
    public static final String PROJECT_DIRECTOR_ORG_BUDGET_PERMISSION = "projectDirectorOrgBudgetPermission";
    public static final String COST_SHARE_ORGS_BUDGET_PERMISSION = "costShareOrgsBudgetPermission";
    public static final String BUDGET_COST_SHARE_PERMISSION_CODE = "budgetCostSharePermissionCode";
    
    public static final String ALLOWED_EMPLOYEE_STATUS_RULE = "AllowedEmployeeStatuses";
    
    public static final String GRADUATE_ASSISTANT_NONPERSONNEL_DESCRIPTION = "graduateAssistantNonpersonnelDescription";
    public static final String GRADUATE_ASSISTANT_NONPERSONNEL_SUB_CATEGORY_CODE = "graduateAssistantNonpersonnelSubCategoryCode";
    public static final String GRADUATE_ASSISTANT_NONPERSONNEL_CATEGORY_CODE = "graduateAssistantNonpersonnelCategoryCode";

    public static final String KRA_BUDGET_PERSONNEL_SUMMER_GRID_APPOINTMENT_TYPES = "KraBudgetPersonnelSummerGridAppointmentTypes";
    public static final String KRA_BUDGET_PERSONNEL_SUMMER_GRID_APPOINTMENT_TYPE = "KraBudgetPersonnelSummerGridAppointmentType";
    public static final String KRA_BUDGET_PERSONNEL_FULL_YEAR_APPOINTMENT_TYPES = "KraBudgetPersonnelFullYearAppointmentTypes";
    public static final String KRA_BUDGET_PERSONNEL_GRADUATE_RESEARCH_ASSISTANT_APPOINTMENT_TYPES = "KraBudgetPersonnelGraduateResearchAssistantAppointmentTypes";
    public static final String KRA_BUDGET_PERSONNEL_HOURLY_APPOINTMENT_TYPES = "KraBudgetPersonnelHourlyAppointmentTypes";
    public static final String KRA_BUDGET_PERSONNEL_ACADEMIC_YEAR_APPOINTMENT_TYPE = "KraBudgetPersonnelAcademicYearAppointmentType";
    public static final String GRADUATE_ASSISTANT = "gradResAssistant";
    public static final String HOURLY = "hourly";
    public static final String ACADEMIC_YEAR_SUMMER = "academicYearSummer";
    public static final String ACADEMIC_YEAR_SUMMER_ARRAY = "academicYearSummerArray";
    public static final String FULL_YEAR = "fullYear";
    public static final String ACADEMIC_SUMMER = "academicSummer";
    public static final String ACADEMIC_YEAR = "academicYear";

    public static final String COST_SHARE_PERMISSION_CODE_OPTIONAL = "O";
    public static final String COST_SHARE_PERMISSION_CODE_TRUE = "Y";
    
    public static final String TO_BE_NAMED_LABEL = "toBeNamedLabel";
    
    public static final String KRA_BUDGET_NUMBER_OF_ACADEMIC_YEAR_SUBDIVISIONS = "KraBudgetNumberOfAcademicYearSubdivisions";
    public static final String KRA_BUDGET_ACADEMIC_YEAR_SUBDIVISION_NAMES = "KraBudgetAcademicYearSubdivisionNames";
    
    // Research Risks codes (system param names)
    public static final String RESEARCH_RISKS_HUMAN_SUBJECTS_ACTIVE_CODE = "researchRisksHumanSubjectsActiveCode";
    public static final String RESEARCH_RISKS_ANIMALS_ACTIVE_CODE = "researchRisksAnimalsActiveCode";
    
    public static final String MANUAL_BASE = "MN";
    public static final String MODIFIED_TOTAL_DIRECT_COST = "MT";
    
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
    
    public static final String DELETE_GRANTS_GOV_QUESTION_ID = "DeleteGrantsGovQuestion";
    
    // Research Risk Types
    public static final String RESEARCH_RISK_TYPE_ALL_COLUMNS = "A";
    public static final String RESEARCH_RISK_TYPE_SOME_COLUMNS = "S";
    public static final String RESEARCH_RISK_TYPE_DESCRIPTION = "D";
    
    // Study Statuses
    public static final String RESEARCH_RISK_STUDY_STATUS_APPROVED = "A";
    public static final String RESEARCH_RISK_STUDY_STATUS_PENDING = "P";
    
    // Study Review Statuses
    public static final String RESEARCH_RISK_STUDY_REVIEW_EXEMPT = "X";
    
    public static final String PERSON_ROLE_CODE_PD = "P";
    public static final String PERSON_ROLE_CODE_OTHER = "O";
    public static final String SUBMISSION_TYPE_CHANGE = "C";
    public static final String PROJECT_TYPE_NEW = "N";
    public static final String PROJECT_TYPE_TIME_EXTENTION = "T";
    public static final String PROJECT_TYPE_BUDGET_REVISION_ACTIVE = "A";
    public static final String PROJECT_TYPE_BUDGET_REVISION_PENDING = "P";
    public static final String PROJECT_TYPE_OTHER = "O";
    public static final String PURPOSE_RESEARCH = "R";
    public static final String PURPOSE_OTHER = "O";
}
