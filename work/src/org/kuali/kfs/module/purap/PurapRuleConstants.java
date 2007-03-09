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
public interface PurapRuleConstants {

    // GROUP NAMES
    public static String PURAP_ADMIN_GROUP = "PurapAdminGroup";

    // RULE NAMES
    public static String PURAP_VENDOR_MIN_ORDER_AMOUNT = "PURAP.VENDOR_MIN_ORDER_AMOUNT";
    public static String PURAP_NOT_ALLOWED_TAX_NUMBERS = "PURAP.NOT_ALLOWED_TAX_NUMBERS";
    public static String PURAP_VNDR_MIN_NUM_LOOKUP_CRITERIA = "PURAP.VNDR_MIN_NUM_LOOKUP_CRITERIA";
    public static String PURAP_VNDR_LOOKUP_MIN_NAME_LENGTH = "PURAP.VNDR_LOOKUP_MIN_NAME_LENGTH";
    public static String PURAP_SSN_ALLOWED_OWNERSHIP_TYPES = "PURAP.SSN_ALLOWED_OWNERSHIP_TYPES";
    public static String PURAP_FEIN_ALLOWED_OWNERSHIP_TYPES = "PURAP.FEIN_ALLOWED_OWNERSHIP_TYPES";
    
    public static final String ALLOWED_EMPLOYEE_TYPE_RULE = "AllowedEmployeeTypes";

    public static final String PURAP_DOCUMENT_PO_ACTIONS = "PURAP.DOCUMENT.PO.ACTIONS";
    public static final String PURAP_DOCUMENT_PREQ_ACTIONS = "PURAP.DOCUMENT.PO.ACTIONS";
    public static final String PURAP_DOCUMENT_ASSIGN_CM_ACTIONS = "PURAP.DOCUMENT.ASSIGN.CM.ACTIONS";
}
