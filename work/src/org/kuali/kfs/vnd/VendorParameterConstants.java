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
package org.kuali.module.vendor;

import org.kuali.kfs.ParameterKeyConstants;

/**
 * Holds constants for Vendor business parameters.
 */
public class VendorParameterConstants implements ParameterKeyConstants {
    public static final String DEFAULT_TAX_NUMBER_DIGITS = "DEFAULT_TAX_NUMBER_DIGITS";
    public static final String PAYMENT_TERMS_DUE_TYPE_DESC = "PAYMENT_TERMS_DUE_TYPE_DESC";
    public static String PURAP_VENDOR_MIN_ORDER_AMOUNT = "MIN_ORDER_AMOUNT";
    public static String PURAP_NOT_ALLOWED_TAX_NUMBERS = "TAX_NUMBERS";
    public static String PURAP_VNDR_MIN_NUM_LOOKUP_CRITERIA = "LOOKUP_MINIMUM_NUMBER_OF_CRITERIA";
    public static String PURAP_VNDR_LOOKUP_MIN_NAME_LENGTH = "LOOKUP_MINIMUM_NAME_LENGTH";
    public static String PURAP_SSN_ALLOWED_OWNERSHIP_TYPES = "SSN_OWNERSHIP_TYPES";
    public static String PURAP_FEIN_ALLOWED_OWNERSHIP_TYPES = "FEIN_OWNERSHIP_TYPES";
}
