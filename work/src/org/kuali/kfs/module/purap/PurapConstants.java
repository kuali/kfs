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

import org.kuali.core.util.KualiDecimal;

/**
 * Holds constants for PURAP.
 * 
 * @author PURAP Development Team (kualidev@oncourse.iu.edu)
 */
public class PurapConstants {

    //Miscellaneous generic constants
    public static final String NONE = "NONE";
    public static final String CREATE_NEW_DIVISION = "Create a New Division";
    public static int VNDR_MIN_NUM_LOOKUP_CRITERIA = 1;
    public static int VNDR_LOOKUP_MIN_NAME_LENGTH = 2;
    
    //Vendor Tax Types
    public static final String TAX_TYPE_FEIN = "FEIN";
    public static final String TAX_TYPE_SSN = "SSN";
    //public static final String TAX_TYPE_ITIN = "ITIN";  //are we implementing this in Kuali??
    
    public static final String PURCHASE_ORDER = "PO";
    public static final String REMIT = "RM";
    public static final String DISBURSEMENT_VOUCHER = "DV";
    
    //North American phone number formats as regular expressions
    public static final String GENERIC_PHONE_FORMAT_1 = "\\d{3}-\\d{3}-\\d{4}";
    public static final String GENERIC_PHONE_FORMAT_2 = "\\(\\d{3}\\)\\s\\d{3}-\\d{4}";
    public static final String GENERIC_PHONE_FORMAT_3 = "\\d{3}\\s\\d{3}\\s\\d{4}";
    
    public static final String[] GENERIC_PHONE_NUMBER_FORMATS = {
        GENERIC_PHONE_FORMAT_1,
        GENERIC_PHONE_FORMAT_2,
        GENERIC_PHONE_FORMAT_3
    };
    public static final int GENERIC_DEFAULT_PHONE_NUM_DIGITS = 10;
      
    //Tax Number formats as  regular expressions
    public static final String TAX_NUM_FORMAT_1 = "\\d{3}\\d{2}\\d{4}";
    public static final String TAX_NUM_FORMAT_2 = "\\d{3}-\\d{2}-\\d{4}";
    
    public static final String[] TAX_NUMBER_FORMATS = {
        TAX_NUM_FORMAT_1,
        TAX_NUM_FORMAT_2,
    };
    
    public static final int DEFAULT_TAX_NUM_DIGITS = 9;
    public static final String NOT_ALLOWED_TAX_NUMBER = "356001673";
    public static final String ALL_ZEROS_TAX_NUMBER = "000000000";
    public static final String THREE_ZEROES = "000";
    public static final String THREE_SIXES = "666";
    public static final String TWO_ZEROES = "00";
    public static final String FOUR_ZEROES = "0000";
    
    public static final KualiDecimal VENDOR_MIN_ORDER_AMOUNT = new KualiDecimal(100000);
    
    //Payment Terms  Types
    public static final String PAYMENT_TERMS_TYPE_DAYS = "Days";
    public static final String PAYMENT_TERMS_TYPE_DATE = "Date";
    
}