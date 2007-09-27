/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap;

/**
 * Holds constants for PURAP business rules.
 */
public class PurapRuleConstants {

    // GROUP NAMES
    public static String CREDIT_MEMO_RULES_GROUP = "Kuali.Purchasing.CreditMemoDocument";

    // RULE NAMES
    public static final String ALLOW_ENCUMBER_NEXT_YEAR_DAYS = "ALLOW_ENCUMBER_NEXT_YEAR_DAYS";
    public static final String ALLOW_APO_NEXT_FY_DAYS = "ALLOW_APO_NEXT_FY_DAYS";
    public static final String ALLOW_BACKPOST_DAYS = "ALLOW_BACKPOST_DAYS";
    
    public static final String RESTRICTED_OBJECT_TYPE_PARM_NM = "RESTRICTED_OBJECT_TYPE";
    public static final String RESTRICTED_OBJECT_CONSOLIDATION_PARM_NM = "OBJECT_CONSOLIDATIONS";
    public static final String RESTRICTED_OBJECT_LEVEL_PARM_NM = "OBJECT_LEVELS";
    //public static final String RESTRICTED_OBJECT_LEVEL_BY_TYPE_PARM_PREFIX = "RESTRICTED_OBJECT_LEVEL_OBJECT_TYPE_";
    public static final String INVALID_OBJECT_LEVELS_BY_OBJECT_TYPE_PARM_NM = "INVALID_OBJECT_LEVELS_BY_OBJECT_TYPE";
    public static final String VALID_OBJECT_LEVELS_BY_OBJECT_TYPE_PARM_NM = "VALID_OBJECT_LEVELS_BY_OBJECT_TYPE";
    public static final String RESTRICTED_OBJECT_SUB_TYPE_PARM_NM = "OBJECT_SUB_TYPES";
    
    public static final String PURAP_VENDOR_TYPE_ALLOWED_ON_REQ_AND_PO  = "REQ_AND_PO_VENDOR_TYPES";
}
