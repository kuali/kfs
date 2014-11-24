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

import java.util.Calendar;


/**
 * Constants specific to the Contracts and Grants module.
 */
public class CGConstants {
    
    public static final String CG_NAMESPACE_CODE = "KFS-CG";

    /**
     * The key for the document error map to grab errors for the close document.
     */
    public static final String CLOSE_DOCUMENT_TAB_ERRORS = "document.userInitiatedCloseDate";
    
    public static final String SHORT_TIMESTAMP_FORMAT = "MM/dd/yyyy";
    public static final String LONG_TIMESTAMP_FORMAT = "MM/dd/yyyy HH:mm:ss";

    public static final int maximumPeriodLengthUnits = Calendar.YEAR;

    public static final String DATABASE_TRUE_VALUE = "Y";

    public static final String DROPDOWN_LIST_SELECT = "select:";

    
    public static final String MANUAL_BASE = "MN";
    public static final String MODIFIED_TOTAL_DIRECT_COST = "MT";

    public static final String ORG_REVIEW_NODE_NAME = "Org Review";
    public static final String ORG_REVIEW_TEMPLATE_NAME = "KualiResearchOrgReviewTemplate";

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
    public static final String CONTACT_PERSON_PARAM = "PERSON_ROLE_CODE_CONTACT_PERSON";
    public static final String CO_PROJECT_DIRECTOR_PARAM = "PERSON_ROLE_CODE_CO_PROJECT_DIRECTOR";
    public static final String OTHER_PERSON_PARAM = "PERSON_ROLE_CODE_OTHER";
    public static final String PROJECT_DIRECTOR_PARAM = "PERSON_ROLE_CODE_PROJECT_DIRECTOR";

    public static final String MAXIMUM_ACCOUNT_RESPONSIBILITY_ID = "MAXIMUM_ACCOUNT_RESPONSIBILITY_ID";

    public static class CGKimApiConstants{
        public static final String AWARD_ROUTING_NODE_NAME = "Award";
        public static final String MANAGEMENT_ROUTING_NODE_NAME = "Management";
        public static final String UNPROCESSED_ROUTING_NODE_NAME = "Unprocessed";
        
    }
    
    public static class SectionId{
        public static final String PROPOSAL_RESEARCH_RISKS = "proposalResearchRisks";
    }

}
