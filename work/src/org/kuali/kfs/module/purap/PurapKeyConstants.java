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
 * Holds error key constants for PURAP.
 * 
 * @author PURAP Development Team (kualidev@oncourse.iu.edu)
 */
public class PurapKeyConstants {

    public static final String PURAP_GENERAL_POTENTIAL_DUPLICATE = "error.document.purap.potentialDuplicate";
    
    //Vendor Maintenance
    public static final String ERROR_OWNERSHIP_REQUIRES_TAX_NUMBER = "error.vendorMaint.OwnershipRequiresTaxNumber";
    public static final String ERROR_VENDOR_TAX_TYPE_AND_NUMBER_COMBO_EXISTS = "error.vendorMaint.addVendor.vendor.exists";
    public static final String ERROR_VENDOR_NAME_REQUIRED = "error.vendorMaint.vendorName.required";
    public static final String ERROR_VENDOR_BOTH_NAME_REQUIRED = "error.vendorMaint.bothNameRequired";
    public static final String ERROR_VENDOR_NAME_INVALID = "error.vendorMaint.nameInvalid";
    public static final String ERROR_VENDOR_TAX_TYPE_ALLOWED = "error.vendorMaint.tax.type.allowed";
    public static final String ERROR_VENDOR_MAX_MIN_ORDER_AMOUNT = "error.vendorMaint.minimumAmt.invalid";
    public static final String ERROR_VENDOR_TAX_TYPE_CANNOT_BE_BLANK = "error.vendorMaint.tax.type.cannot.be.blank";
    public static final String ERROR_VENDOR_TAX_TYPE_CANNOT_BE_SET = "error.vendorMaint.tax.type.cannot.be.set";
    public static final String ERROR_VENDOR_PARENT_NEEDS_CHANGED = "error.vendorMaint.vendorParent.needs.changed";
    public static final String ERROR_TAX_NUMBER_ALL_DIGITS_AND_LENGTH= "error.vendorMaint.taxNumber.isAllDigits.and.length";
    public static final String ERROR_TAX_NUMBER_ALL_ZEROES="error.vendorMaint.taxNumber.isAllZeroes";
    public static final String ERROR_FIRST_THREE_SSN = "error.vendorMaint.first.three.ssn";
    public static final String ERROR_MIDDLE_TWO_SSN = "error.vendorMaint.middle.two.ssn";
    public static final String ERROR_LAST_FOUR_SSN = "error.vendorMaint.last.four.ssn";
    public static final String ERROR_FIRST_TWO_FEIN = "error.vendorMaint.first.two.fein";
    
    //Vendor Lookup
    public static final String ERROR_VENDOR_LOOKUP_NAME_TOO_SHORT = "error.vendorLookup.name.too.short";
    public static final String ERROR_VENDOR_LOOKUP_FEWER_THAN_MIN_CRITERIA = "error.vendorLookup.min.criteria.fewer";
    public static final String ERROR_VENDOR_LOOKUP_PAYEE_ID_NO_STARTING_P = "error.vendorLookup.payeeId.no.starting.p";
    public static final String ERROR_VENDOR_LOOKUP_TYPE_NO_NAME_OR_STATE = "error.vendorLookup.type.no.name.or.state";
    public static final String ERROR_VENDOR_LOOKUP_STATUS_NO_NAME = "error.vendorLookup.status.no.name";
    public static final String ERROR_VENDOR_LOOKUP_TAX_NUM_ALL_ZEROES = "error.vendorLookup.taxNum.all.zeroes";
    public static final String ERROR_VENDOR_LOOKUP_VNDR_NUM_TOO_MANY_DASHES = "error.vendorLookup.vndrNum.dashes.tooMany";
    public static final String ERROR_VENDOR_LOOKUP_VNDR_NUM_DASHES_ONLY = "error.vendorLookup.vndrNum.dashes.only";
    public static final String ERROR_VENDOR_LOOKUP_STATE_NO_TYPE = "error.vendorLookup.state.no.type";
    
    //Vendor Maintenance Address
    public static final String ERROR_US_REQUIRES_STATE = "error.vendorMaint.vendorAddress.USRequiresState";
    public static final String ERROR_US_REQUIRES_ZIP = "error.vendorMaint.vendorAddress.USRequiresZip";
    public static final String ERROR_FAX_NUMBER = "error.vendorMaint.vendorAddress.faxNumber";
    public static final String ERROR_ADDRESS_TYPE = "error.vendorMaint.vendorAddress.addressType";
    
}