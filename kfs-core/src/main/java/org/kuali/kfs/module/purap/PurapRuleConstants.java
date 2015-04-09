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
package org.kuali.kfs.module.purap;

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

    public static final String INVALID_OBJECT_LEVELS_BY_OBJECT_TYPE_PARM_NM = "INVALID_OBJECT_LEVELS_BY_OBJECT_TYPE";
    public static final String VALID_OBJECT_LEVELS_BY_OBJECT_TYPE_PARM_NM = "VALID_OBJECT_LEVELS_BY_OBJECT_TYPE";
    public static final String RESTRICTED_OBJECT_SUB_TYPE_PARM_NM = "OBJECT_SUB_TYPES";

    public static final String PURAP_VENDOR_TYPE_ALLOWED_ON_REQ_AND_PO = "REQ_AND_PO_VENDOR_TYPES";
    public static final String ITEMS_REQUIRE_COMMODITY_CODE_IND = "ITEMS_REQUIRE_COMMODITY_CODE_IND";
}
