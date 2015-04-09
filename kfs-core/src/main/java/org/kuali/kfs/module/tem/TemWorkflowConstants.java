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
package org.kuali.kfs.module.tem;

public class TemWorkflowConstants {

    //Split nodes
    public static final String SPECIAL_REQUEST = "TravelHasSpecialRequest";
    public static final String INTL_TRAVEL = "TravelIsInternational";
    public static final String RISK_MANAGEMENT = "TravelNeedsRiskManagement";
    public static final String TRVL_ADV_REQUESTED = "TravelAdvanceRequested";
    public static final String DIVISION_APPROVAL_REQUIRED = "TravelRequiresDivisionApproval";
    public static final String SEPARATION_OF_DUTIES = "RequiresSeparationOfDuties";
    public static final String REQUIRES_SUB_FUND = "RequiresSubFund";
    public static final String REQUIRES_AWARD = "RequiresAward";
    public static final String REQUIRES_AP_TRAVEL = "RequiresTravel";
    public static final String REQUIRES_TRAVELER_REVIEW = "TravelRequiresTravelerApproval";
    public static final String REQUIRES_PROFILE_REVIEW = "ProfileReviewRequired";
    public static final String REQUIRES_BUDGET_REVIEW = "RequiresBudgetReview";
    // TemProfile constants
    public static final String TAX_MANAGER_REQUIRED = "ProfileIsTaxManagerRequired";
    //RELO/TR tax manager
    public static final String TAX_MANAGER_APPROVAL_REQUIRED = "TaxManagerApproval";
    //ENT's tax manager
    public static final String TAX_MANAGER = "TaxManager";

    public static class RouteNodeNames {
        public static final String AP_TRAVEL = "Travel";
        public static final String TRAVELER = "Traveler";
        public static final String DIVISION_REVIEWER = "DivisionReviewer";
        public static final String SPECIAL_REQUEST_REVIEWER = "SpecialRequestReviewer";
        public static final String INTERNATIONAL_TRAVEL_REVIEWER = "InternationalTravelReviewer";
        public static final String SUB_FUND = "SubFund";
        public static final String AWARD = "Award";
        public static final String TAX = "Tax";
        public static final String SEPARATION_OF_DUTIES_REVIEWER = "SeparationOfDutiesReviewer";
        public static final String ORGANIZATION_HIERARCHY = "OrganizationHierarchy";
        public static final String RISK_MANAGEMENT = "RiskManagement";
        public static final String EXECUTIVE_APPROVAL = "ExecutiveApproval";
        public static final String MOVING_AND_RELOCATION_MANAGER = "MovingAndRelocationManager";
        public static final String ENTERTAINMENT_MANAGER = "EntertainmentManager";
        public static final String APPLICATION = "Application";
        public static final String ACCOUNT_REVIEW = "Account Review";
        public static final String TRAVEL_OFFICE_REVIEW = "Travel Office Review";
        public static final String FISCAL_OFFICER_REVIEW = "Fiscal Officer Review";
        public static final String PENDING_BANK_APPLICATION = "Pending Bank Application";
        public static final String APPLIED_TO_BANK = "Applied To Bank";
        public static final String APPROVED_BY_BANK = "Approved By Bank";
        public static final String DECLINED = "Declined";
        public static final String APPROVED = "Approved";
        public static final String REQUIRED_ACCOUNT_NOTIFICATION = "RequireAccountNotification";
        public static final String PROFILE = "Profile";
    }

}
