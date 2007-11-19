/*
 * Copyright 2007 The Kuali Foundation.
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


/**
 * Holds constants for Vendor.
 */
public class VendorConstants {

    public static class Workgroups {
        public static final String WORKGROUP_PURCHASING = "PURCHASING_GROUP";
        public static final String WORKGROUP_VENDOR_CONTRACT = "VENDOR_CONTRACT_GROUP";
        public static final String WORKGROUP_TAXNBR_ACCESSIBLE = "TAXNBR_ACCESSIBLE_GROUP";
    }

    public static final String ACKNOWLEDGE_NEW_VENDOR_INFO_TEXT = "document.vendor.question.acknolwege.text";
    public static final String ACKNOWLEDGE_NEW_VENDOR_INFO = "NewVendorAcknowledgeQuestion";

    // Miscellaneous generic constants
    public static final String NONE = "NONE";
    public static final String CREATE_NEW_DIVISION = "create division";
    public static final String NAME_DELIM = ", ";
    public static final String DASH = "-";
    public static final String VENDOR_HEADER_ATTR = "vendorHeader";
    public static final String VENDOR_LOOKUPABLE_IMPL = "vendorLookupable";
    public static final String VENDOR_ADDRESS_LOOKUPABLE_IMPL = "vendorAddressLookupable";
    public static final String VENDOR_CONTRACT_LOOKUPABLE_IMPL = "vendorContractLookupable";

    // Vendor Tax Types
    public static final String TAX_TYPE_FEIN = "FEIN";
    public static final String TAX_TYPE_SSN = "SSN";

    // VENDOR PHONE TYPES
    public static class PhoneTypes {
        public static final String TOLL_FREE = "TF";
        public static final String PHONE = "PH";
        public static final String FAX = "FX";
        public static final String PO = "PO";
    }

    public static class AddressTypes {
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