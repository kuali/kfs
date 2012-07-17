/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys;

/**
 * Parameter name constants for system parameters used by the kfs sys.
 */
public class KFSParameterKeyConstants {
    
    public static final String ENABLE_BANK_SPECIFICATION_IND = "ENABLE_BANK_SPECIFICATION_IND";
    public static final String DEFAULT_BANK_BY_DOCUMENT_TYPE = "DEFAULT_BANK_BY_DOCUMENT_TYPE";
    public static final String BANK_CODE_DOCUMENT_TYPES = "BANK_CODE_DOCUMENT_TYPES";
    
    public static class YearEndAutoDisapprovalConstants {
        public static final String YEAR_END_AUTO_DISAPPROVE_ANNOTATION = "YEAR_END_AUTO_DISAPPROVE_ANNOTATION";
        public static final String YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE = "YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE";
        public static final String YEAR_END_AUTO_DISAPPROVE_DOCUMENT_STEP_RUN_DATE = "YEAR_END_AUTO_DISAPPROVE_DOCUMENT_RUN_DATE";
        public static final String YEAR_END_AUTO_DISAPPROVE_DOCUMENT_TYPES = "YEAR_END_AUTO_DISAPPROVE_DOCUMENT_TYPES";
        public static final String YEAR_END_AUTO_DISAPPROVE_PARENT_DOCUMENT_TYPE = "YEAR_END_AUTO_DISAPPROVE_PARENT_DOCUMENT_TYPE";
    }
    
    public static class PurapPdpParameterConstants {
        public static final String PURAP_PDP_ORG_CODE = "PRE_DISBURSEMENT_EXTRACT_ORGANIZATION";
        public static final String PURAP_PDP_SUB_UNIT_CODE = "PRE_DISBURSEMENT_EXTRACT_SUB_UNIT";
    }
 
    public static class GlParameterConstants {
        public static final String PLANT_INDEBTEDNESS_OFFSET_CODE = "PLANT_INDEBTEDNESS_OFFSET_CODE";
        public static final String CAPITALIZATION_OFFSET_CODE = "CAPITALIZATION_OFFSET_CODE";
        public static final String LIABILITY_OFFSET_CODE = "LIABILITY_OFFSET_CODE";
    }
    
    public static class LdParameterConstants {
        public static final String DEMERGE_DOCUMENT_TYPES = "DEMERGE_DOCUMENT_TYPES";
        public static final String ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND = "ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND";
        public static final String DEFAULT_BENEFIT_RATE_CATEGORY_CODE = "DEFAULT_BENEFIT_RATE_CATEGORY_CODE";
    }
    
    public static class CamParameterConstants {
        public static final String OBJECT_SUB_TYPE_GROUPS = "OBJECT_SUB_TYPE_GROUPS";
    }
 
}
