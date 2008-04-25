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
package org.kuali.module.purap.fixtures;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.document.RequisitionDocument;

public enum RequisitionDocumentFixture {

    REQ_ONLY_REQUIRED_FIELDS(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_UNRESTRICTED_ITEM_1 } // requisitionItemMultiFixtures
    ),

    REQ_VALID_NO_APO_OVER_LIMIT(null,  // requisitionOrganizationReference1Text
            null,                   // requisitionOrganizationReference2Text
            null,                   // requisitionOrganizationReference3Text
            null,                   // alternate1VendorName
            null,                   // alternate2VendorName
            null,                   // alternate3VendorName
            null,                   // alternate4VendorName
            null,                   // alternate5VendorName
            null,                   // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_VALID_APO,  // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS,                 // purchasingDocumentFixture
            new RequisitionItemFixture[] {RequisitionItemFixture.REQ_ITEM_NO_APO}  // requisitionItemMultiFixtures
            ),
                                    
      REQ_VALID_NO_APO_RESTRICTED_ITEM(null,  // requisitionOrganizationReference1Text
            null,                   // requisitionOrganizationReference2Text
            null,                   // requisitionOrganizationReference3Text
            null,                   // alternate1VendorName
            null,                   // alternate2VendorName
            null,                   // alternate3VendorName
            null,                   // alternate4VendorName
            null,                   // alternate5VendorName
            null,                   // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_VALID_APO,  // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS,                 // purchasingDocumentFixture
            new RequisitionItemFixture[] {RequisitionItemFixture.REQ_ITEM_NO_APO_RESTRICTED_ITEM}  // requisitionItemMultiFixtures
            ),
                
    REQ_NO_APO_VALID(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_VALID_APO, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_ITEM_NO_APO } // requisitionItemMultiFixtures
    ),
    
    REQ_NO_APO_VALID_2(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_VALID_APO, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_ITEM_NO_APO } // requisitionItemMultiFixtures
    ),

    REQ_APO_VALID(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_VALID_APO, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_UNRESTRICTED_ITEM_1 } // requisitionItemMultiFixtures
    ),

    REQ_MULTI_QUANTITY(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_MULTI_QUANTITY, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_MULTI, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_MULTI_ITEM_QUANTITY } // requisitionItemMultiFixtures
    ),
    REQ_MULTI_NON_QUANTITY(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_MULTI_NON_QUANTITY, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_MULTI, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_MULTI_ITEM_NON_QUANTITY } // requisitionItemMultiFixtures
    ),

    REQ_ALTERNATE_APO(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_ALTERNATE_APO, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_APO_ITEM_1, RequisitionItemFixture.REQ_SERVICE_APO_ITEM_1, RequisitionItemFixture.REQ_FREIGHT_ITEM_1 } // requisitionItemMultiFixtures
    ),

    REQ_APO_INVALID_ALTERNATE_VENDOR_NAMES(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            "NFL Shop", // alternate1VendorName
            "Dicks Sporting Goods", // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_VALID_APO, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_UNRESTRICTED_ITEM_1 } // requisitionItemMultiFixtures
    ),
    
    REQ_APO_INVALID_TOTAL_NOT_GREATER_THAN_ZERO(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_ITEM_NO_APO_TOTAL_NOT_GREATER_THAN_ZERO } // requisitionItemMultiFixtures
    ),
    
    REQ_APO_INVALID_CONTAINS_RESTRICTED_ITEM (null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_VALID_APO, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_ITEM_NO_APO_CONTAIN_RESTRICTED_ITEM } // requisitionItemMultiFixtures
    ),

    REQ_APO_INVALID_ERROR_RETRIEVING_VENDOR_FROM_DATABASE (null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_WITH_VENDOR_NOT_IN_DATABASE, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_APO_ITEM_1 } // requisitionItemMultiFixtures
    ),
    
    REQ_APO_INVALID_WITH_RESTRICTED_VENDOR (null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_WITH_RESTRICTED_VENDOR, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_UNRESTRICTED_ITEM_1 } // requisitionItemMultiFixtures
    ), 
    
    REQ_APO_INVALID_PAYMENT_TYPE_RECURRING(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_VALID_APO, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_WITH_RECURRING_PAYMENT_TYPE, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_APO_ITEM_1 } // requisitionItemMultiFixtures
    ), 
    
    REQ_APO_INVALID_PO_TOTAL_LIMIT_NON_ZERO(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_VALID_APO, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_WITH_PO_TOTAL_LIMIT_NON_ZERO, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_APO_ITEM_1 } // requisitionItemMultiFixtures
    ),
    
    REQ_VALID_VENDOR_FAX_NUMBER(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_VALID_APO, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_VALID_VENDOR_FAX_NUMBER, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_APO_ITEM_1 } // requisitionItemMultiFixtures
    ), 
    
    REQ_B2B_WITH_DEBARRED_VENDOR(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_WITH_DEBARRED_VENDOR, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_B2B_VENDOR, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_APO_ITEM_1 } // requisitionItemMultiFixtures
    ), 

    REQ_B2B_WITH_INACTIVE_VENDOR(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_WITH_INACTIVE_VENDOR, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_B2B_VENDOR, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_APO_ITEM_1 } // requisitionItemMultiFixtures
    ), 
    
    REQ_B2B_WITH_DV_VENDOR(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_WITH_DV_VENDOR, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_B2B_VENDOR, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_APO_ITEM_1 } // requisitionItemMultiFixtures
    ),
    
    REQ_B2B_WITH_INVALID_US_VENDOR_ZIP_CODE_CONTAINS_LETTER(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_WITH_INVALID_US_VENDOR_ZIP_CODE_CONTAINS_LETTERS, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_B2B_VENDOR, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_APO_ITEM_1 } // requisitionItemMultiFixtures
    ),
       
    REQ_INVALID_VENDOR_FAX_NUMBER_CONTAINS_LETTER(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_VALID_APO, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_INVALID_VENDOR_FAX_NUMBER_CONTAINS_LETTER, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_APO_ITEM_1 } // requisitionItemMultiFixtures
    ), 

    REQ_INVALID_VENDOR_FAX_NUMBER_BAD_FORMAT(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_VALID_APO, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_INVALID_VENDOR_FAX_NUMBER_BAD_FORMAT, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_APO_ITEM_1 } // requisitionItemMultiFixtures
    ),

    REQ_WITH_INVALID_US_VENDOR_ZIP_CODE_CONTAINS_LETTER(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_WITH_INVALID_US_VENDOR_ZIP_CODE_CONTAINS_LETTERS, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_APO_ITEM_1 } // requisitionItemMultiFixtures
    ),
    
    REQ_WITH_INVALID_US_VENDOR_ZIP_CODE_BAD_FORMAT(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_WITH_INVALID_US_VENDOR_ZIP_CODE_BAD_FORMAT, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_APO_ITEM_1 } // requisitionItemMultiFixtures
    ),

    REQ_WITH_INVALID_NON_US_VENDOR_ZIP_CODE_CONTAINS_LETTER(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_WITH_INVALID_NON_US_VENDOR_ZIP_CODE_CONTAINS_LETTERS, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_APO_ITEM_1 } // requisitionItemMultiFixtures
    ),

    REQ_WITH_VALID_US_VENDOR_ZIP_CODE_WITH_4_TRAILING_NUMBERS(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_WITH_VALID_US_VENDOR_ZIP_CODE_WITH_4_TRAILING_NUMBERS, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_APO_ITEM_1 } // requisitionItemMultiFixtures
    ),
    
    REQ_PO_BEGIN_DATE_AFTER_END_DATE(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_PO_BEGIN_DATE_AFTER_END_DATE, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_APO_ITEM_1 } // requisitionItemMultiFixtures
    ),

    REQ_PO_BEGIN_DATE_NO_END_DATE(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_PO_BEGIN_DATE_NO_END_DATE, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_APO_ITEM_1 } // requisitionItemMultiFixtures
    ),
    
    REQ_PO_END_DATE_NO_BEGIN_DATE(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_PO_END_DATE_NO_BEGIN_DATE, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_APO_ITEM_1 } // requisitionItemMultiFixtures
    ), 
    
    REQ_PO_BEGIN_DATE_AND_END_DATE_NO_RECURRING_PAYMENT_TYPE(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_WITH_BEGIN_AND_END_DATE_WITHOUT_RECURRING_PAYMENT_TYPE, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_APO_ITEM_1 } // requisitionItemMultiFixtures
    ), 
    
    REQ_WITH_RECURRING_PAYMENT_TYPE_WITHOUT_BEGIN_NOR_END_DATE(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_WITH_RECURRING_PAYMENT_TYPE, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_APO_ITEM_1 } // requisitionItemMultiFixtures
    ),
    
    REQ_WITH_RECURRING_PAYMENT_TYPE_BEGIN_AND_END_DATE(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_WITH_RECURRING_PAYMENT_TYPE_BEGIN_AND_END_DATE, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_APO_ITEM_1 } // requisitionItemMultiFixtures
    ), 
    REQ_NO_APO_VALID_WITH_BASIC_ACTIVE_COMMODITY_CODE(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_VALID_APO, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_ITEM_NO_APO_BASIC_ACTIVE_COMMODITY_CODE } // requisitionItemMultiFixtures
    ),
    REQ_INVALID_ITEM_QUANTITY_BASED_NO_QUANTITY(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_ITEM_INVALID_QUANTITY_BASED_NO_QUANTITY } // requisitionItemMultiFixtures
    ),    
    ;
    
    public final String requisitionOrganizationReference1Text;
    public final String requisitionOrganizationReference2Text;
    public final String requisitionOrganizationReference3Text;
    public final String alternate1VendorName;
    public final String alternate2VendorName;
    public final String alternate3VendorName;
    public final String alternate4VendorName;
    public final String alternate5VendorName;
    public final KualiDecimal organizationAutomaticPurchaseOrderLimit;
    private PurchasingAccountsPayableDocumentFixture purapDocumentFixture;
    private PurchasingDocumentFixture purchasingDocumentFixture;
    private RequisitionItemFixture[] requisitionItemFixtures;

    private RequisitionDocumentFixture(String requisitionOrganizationReference1Text, String requisitionOrganizationReference2Text, String requisitionOrganizationReference3Text, String alternate1VendorName, String alternate2VendorName, String alternate3VendorName, String alternate4VendorName, String alternate5VendorName, KualiDecimal organizationAutomaticPurchaseOrderLimit, PurchasingAccountsPayableDocumentFixture purapDocumentFixture, PurchasingDocumentFixture purchasingDocumentFixture, RequisitionItemFixture[] requisitionItemFixtures) {
        this.requisitionOrganizationReference1Text = requisitionOrganizationReference1Text;
        this.requisitionOrganizationReference2Text = requisitionOrganizationReference2Text;
        this.requisitionOrganizationReference3Text = requisitionOrganizationReference3Text;
        this.alternate1VendorName = alternate1VendorName;
        this.alternate2VendorName = alternate2VendorName;
        this.alternate3VendorName = alternate3VendorName;
        this.alternate4VendorName = alternate4VendorName;
        this.alternate5VendorName = alternate5VendorName;
        this.organizationAutomaticPurchaseOrderLimit = organizationAutomaticPurchaseOrderLimit;
        this.purapDocumentFixture = purapDocumentFixture;
        this.purchasingDocumentFixture = purchasingDocumentFixture;
        this.requisitionItemFixtures = requisitionItemFixtures;
    }

    public RequisitionDocument createRequisitionDocument() {
        RequisitionDocument doc = purchasingDocumentFixture.createRequisitionDocument(purapDocumentFixture);
        doc.setRequisitionOrganizationReference1Text(this.requisitionOrganizationReference1Text);
        doc.setRequisitionOrganizationReference2Text(this.requisitionOrganizationReference2Text);
        doc.setRequisitionOrganizationReference3Text(this.requisitionOrganizationReference3Text);
        doc.setAlternate1VendorName(this.alternate1VendorName);
        doc.setAlternate2VendorName(this.alternate2VendorName);
        doc.setAlternate3VendorName(this.alternate3VendorName);
        doc.setAlternate4VendorName(this.alternate4VendorName);
        doc.setAlternate5VendorName(this.alternate5VendorName);
        doc.setOrganizationAutomaticPurchaseOrderLimit(this.organizationAutomaticPurchaseOrderLimit);

        for (RequisitionItemFixture requisitionItemFixture : requisitionItemFixtures) {
            requisitionItemFixture.addTo(doc);
        }

        return doc;
    }

}
