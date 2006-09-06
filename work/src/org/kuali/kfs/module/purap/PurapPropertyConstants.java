/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.purap;

/**
 * Property name constants.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class PurapPropertyConstants {
    
    public static final String DATA_OBJ_MAINT_CD_ACTIVE_IND = "dataObjectMaintenanceCodeActiveIndicator";
    
    public static final String VENDOR_NUMBER = "vendorNumber";
    public static final String VENDOR_TAX_NUMBER = "vendorHeader.vendorTaxNumber";
    public static final String VENDOR_FAX_NUMBER = "vendorFaxNumber";
    public static final String VENDOR_ADDRESS = "vendorAddresses";
    public static final String VENDOR_ADDRESS_STATE = "vendorStateCode";
    public static final String VENDOR_STATE_FOR_LOOKUP = "vendorStateForLookup";
    public static final String VENDOR_ADDRESS_ZIP = "vendorZipCode";
    public static final String VENDOR_ADDRESS_TYPE_CODE = "vendorAddresses.vendorAddressTypeCode";
    public static final String VENDOR_ADDRESS_EMAIL = "vendorAddresses.vendorAddressEmailAddress";
    public static final String VENDOR_FEIN_SSN_INDICATOR = "vendorFeinSocialSecurityNumberIndicator";
    public static final String VENDOR_TAX_NUMBER_ONLY = "vendorTaxNumber";
    public static final String VENDOR_NAME = "vendorName";
    public static final String VENDOR_FIRST_NAME = "vendorFirstName";
    public static final String VENDOR_LAST_NAME = "vendorLastName";
    public static final String VENDOR_TAX_TYPE_CODE = "vendorHeader.vendorTaxTypeCode";
    public static final String VENDOR_TAX_TYPE_CODE_WITHOUT_HEADER = "vendorTaxTypeCode";
    public static final String VENDOR_MIN_ORDER_AMOUNT = "vendorMinimumOrderAmount";
    public static final String VENDOR_TYPE_CODE = "vendorHeader.vendorTypeCode";
    public static final String VENDOR_DETAIL_ASSIGNED_ID = "vendorDetailAssignedIdentifier";
    public static final String VENDOR_HEADER_GENERATED_ID = "vendorHeaderGeneratedIdentifier";
 
    public static final String CONTRACT_LANGUAGE_CREATE_DATE = "contractLanguageCreateDate";

    public static final String VENDOR_LEGACY_PAYEE_ID = "vendorLegacyPayeeIdentifier";
}