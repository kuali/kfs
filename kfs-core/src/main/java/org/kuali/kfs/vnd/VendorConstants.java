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
package org.kuali.kfs.vnd;


/**
 * Holds constants for Vendor.
 */
public class VendorConstants {

    public static class VendorTypes {
        public static final String DISBURSEMENT_VOUCHER = "DV";
        public static final String PURCHASE_ORDER = "PO";
        public static final String SUBJECT_PAYMENT = "SP";
        public static final String REVOLVING_FUND = "RF";
        public static final String REFUND_PAYMENT = "RV";
    }

    public static final String CHANGE_TO_PARENT_QUESTION_ID = "confirmVendorChangeToParent";
    public static final String ACKNOWLEDGE_NEW_VENDOR_INFO_TEXT = "document.vendor.question.acknolwege.text";
    public static final String ACKNOWLEDGE_NEW_VENDOR_INFO = "NewVendorAcknowledgeQuestion";

    // Miscellaneous generic constants
    public static final String NONE = "NONE";
    public static final String CREATE_DIVISION = "create division";
    public static final String NAME_DELIM = ", ";
    public static final String DASH = "-";
    public static final String VENDOR_HEADER_ATTR = "vendorHeader";
    public static final String VENDOR_LOOKUPABLE_IMPL = "vendorLookupable";
    public static final String VENDOR_ADDRESS_LOOKUPABLE_IMPL = "vendorAddressLookupable";
    public static final String VENDOR_CONTRACT_LOOKUPABLE_IMPL = "vendorContractLookupable";
    public static final int MAX_VENDOR_NAME_LENGTH = 40;
    public static final String VENDOR_EXCLUDE_FILE_TYPE_INDENTIFIER = "vendorExcludeInputFileType";
    public static final String EXCLUDED_MATCHED_VENDOR_STATUS = "M";
    public static final String NON_EXCLUDED_MATCHED_VENDOR_STATUS = "N";
    public static final String DEBARRED_VENDOR_UNPROCESSED = "U";
    public static final String DEBARRED_VENDOR_CONFIRMED = "C";
    public static final String DEBARRED_VENDOR_DENIED = "D";
    public static final String DEBARRED_VENDOR_UNPROCESSED_LABEL = "Unprocessed";
    public static final String DEBARRED_VENDOR_CONFIRMED_LABEL = "Confirmed";
    public static final String DEBARRED_VENDOR_DENIED_LABEL = "Denied";

    // Vendor Tax Types
    public static final String TAX_TYPE_FEIN = "FEIN";
    public static final String TAX_TYPE_SSN = "SSN";
    public static final String TAX_TYPE_TAX = "TAX";

    // VENDOR PHONE TYPES
    public static class PhoneTypes {
        public static final String TOLL_FREE = "TF";
        public static final String PHONE = "PH";
        public static final String FAX = "FX";
        public static final String PO = "PO";
    }

    public static class AddressTypes {
        public static final String QUOTE = "QT";
        public static final String PURCHASE_ORDER = "PO";
        public static final String REMIT = "RM";
    }

    // Vendor Ownership Types
    public static class OwnerTypes {
        public static final String NR = "NR";
    }

    public static class VendorCreateAndUpdateNotePrefixes {
        public static final String ADD = "Add";
        public static final String CHANGE = "Change";
    }
}
