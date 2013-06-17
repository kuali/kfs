/*
 * Copyright 2007-2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.fixture;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants.TaxParameters;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.fixture.TaxFixture.TaxTestCaseFixture;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.fixture.CommodityCodeFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;

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
    REQ_TWO_ITEMS(null, // requisitionOrganizationReference1Text
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
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_UNRESTRICTED_ITEM_1,
                                           RequisitionItemFixture.REQ_QTY_UNRESTRICTED_ITEM_2 } // requisitionItemMultiFixtures
    ),
    REQ_ONLY_REQUIRED_FIELDS_MULTIPLE_ACCOUNTS(null, // requisitionOrganizationReference1Text
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
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_UNRESTRICTED_ITEM_3 } // requisitionItemMultiFixtures
    ),
    REQ_WITH_NEGATIVE_AMOUNT(null, // requisitionOrganizationReference1Text
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
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_ITEM_NEGATIVE_AMOUNT } // requisitionItemMultiFixtures
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
    
    REQ_APO_INVALID_FAILS_CAPITAL_ASSET_RULE(null, // requisitionOrganizationReference1Text
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
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_ITEM_INVALID_CAPITAL_ASSET } // requisitionItemMultiFixtures
    ),
    
    REQ_APO_INVALID_APPROVAL_OUTSIDE_ALLOWED_DATE_RANGE(null, // requisitionOrganizationReference1Text
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
       
    REQ_B2B_WITH_PO_VENDOR(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_VALID_APO, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_B2B_VENDOR_NO_TOTAL_LIMIT, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_B2B_ITEM_1 } // requisitionItemMultiFixtures
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
    REQ_TAX(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_TAX, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_QTY_UNRESTRICTED_ITEM_1 } // requisitionItemMultiFixtures
    ),
    REQ_PERFORMANCE(null, // requisitionOrganizationReference1Text
            null, // requisitionOrganizationReference2Text
            null, // requisitionOrganizationReference3Text
            null, // alternate1VendorName
            null, // alternate2VendorName
            null, // alternate3VendorName
            null, // alternate4VendorName
            null, // alternate5VendorName
            null, // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_ONLY_REQUIRED_FIELDS, // purapDocumentFixture
            PurchasingDocumentFixture.REQ_PERFORMANCE, // purchasingDocumentFixture
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_ITEM_PERFORMANCE,
                                            RequisitionItemFixture.REQ_ITEM_PERFORMANCE,
                                            RequisitionItemFixture.REQ_ITEM_PERFORMANCE,
                                            RequisitionItemFixture.REQ_ITEM_PERFORMANCE,
                                            RequisitionItemFixture.REQ_ITEM_PERFORMANCE,
                                            RequisitionItemFixture.REQ_ITEM_PERFORMANCE,
                                            RequisitionItemFixture.REQ_ITEM_PERFORMANCE,
                                            RequisitionItemFixture.REQ_ITEM_PERFORMANCE,
                                            RequisitionItemFixture.REQ_ITEM_PERFORMANCE,
                                            RequisitionItemFixture.REQ_ITEM_PERFORMANCE,
                                            } // requisitionItemMultiFixtures
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
        doc.fixItemReferences();
        doc.setAccountDistributionMethod("S");
        doc.refreshNonUpdateableReferences();
        return doc;
    }

    public RequisitionDocument createRequisitionDocumentForTax(TaxTestCaseFixture taxTestCaseFixture) {
        
        RequisitionDocument doc = createRequisitionDocument();
        
        doc.getItem(0).getItemType().setTaxableIndicator(taxTestCaseFixture.isItemTypeTaxable());
        
        doc.setDeliveryStateCode("IN"); 
        
        if (taxTestCaseFixture.isItemTaxAmountNull()){
            doc.getItem(0).setItemTaxAmount(null);
        }else{
            doc.getItem(0).setItemTaxAmount(new KualiDecimal(100));
        }
        
        doc.setUseTaxIndicator(taxTestCaseFixture.isUseTax());
        
        if (taxTestCaseFixture.iscommodityCodeNull()){
            ((RequisitionItem)doc.getItem(0)).setPurchasingCommodityCode(null);
            ((RequisitionItem)doc.getItem(0)).setCommodityCode(null);
        }else{
            ((RequisitionItem)doc.getItem(0)).setCommodityCode(CommodityCodeFixture.COMMODITY_CODE_BASIC_ACTIVE.createCommodityCode());
        }
        
        String INVALID_VALUE = "XX";
        
        String fundGroupCode = doc.getItem(0).getSourceAccountingLines().get(0).getAccount().getSubFundGroup().getFundGroupCode();
        String subFundGroupCode = doc.getItem(0).getSourceAccountingLines().get(0).getAccount().getSubFundGroup().getSubFundGroupCode();
        String objectLevelCode = doc.getItem(0).getSourceAccountingLines().get(0).getObjectCode().getFinancialObjectLevelCode();
        String consolidationObjectCode = doc.getItem(0).getSourceAccountingLines().get(0).getObjectCode().getFinancialObjectLevel().getFinancialConsolidationObjectCode();
        
        String parameterSuffix;
        
        if (taxTestCaseFixture.isDeliveryStateTaxable()){
            TestUtils.setSystemParameter(KfsParameterConstants.PURCHASING_DOCUMENT.class, TaxParameters.TAXABLE_DELIVERY_STATES, doc.getDeliveryStateCode());
            parameterSuffix = "FOR_TAXABLE_STATES";
        } else {
            TestUtils.setSystemParameter(KfsParameterConstants.PURCHASING_DOCUMENT.class, TaxParameters.TAXABLE_DELIVERY_STATES, INVALID_VALUE);
            parameterSuffix = "FOR_NON_TAXABLE_STATES";
        }
        
        if (taxTestCaseFixture.isFundGroupCodeTaxable()){
            TestUtils.setSystemParameter(KfsParameterConstants.PURCHASING_DOCUMENT.class, "TAXABLE_FUND_GROUPS_" + parameterSuffix, fundGroupCode);
            TestUtils.setSystemParameter(KfsParameterConstants.PURCHASING_DOCUMENT.class, "TAXABLE_SUB_FUND_GROUPS_" + parameterSuffix, subFundGroupCode);
        }else{
           //Just put some invalid value
            TestUtils.setSystemParameter(KfsParameterConstants.PURCHASING_DOCUMENT.class, "TAXABLE_FUND_GROUPS_" + parameterSuffix, INVALID_VALUE);
            TestUtils.setSystemParameter(KfsParameterConstants.PURCHASING_DOCUMENT.class, "TAXABLE_SUB_FUND_GROUPS_" + parameterSuffix, INVALID_VALUE);
        }
        
        if (taxTestCaseFixture.isObjectCodeTaxable()){
            TestUtils.setSystemParameter(KfsParameterConstants.PURCHASING_DOCUMENT.class, "TAXABLE_OBJECT_LEVELS_" + parameterSuffix, objectLevelCode);
            TestUtils.setSystemParameter(KfsParameterConstants.PURCHASING_DOCUMENT.class, "TAXABLE_OBJECT_CONSOLIDATIONS_" + parameterSuffix, consolidationObjectCode);
        }else{
            TestUtils.setSystemParameter(KfsParameterConstants.PURCHASING_DOCUMENT.class, "TAXABLE_OBJECT_LEVELS_" + parameterSuffix, INVALID_VALUE);
            TestUtils.setSystemParameter(KfsParameterConstants.PURCHASING_DOCUMENT.class, "TAXABLE_OBJECT_CONSOLIDATIONS_" + parameterSuffix, INVALID_VALUE);
        }
        
        if (taxTestCaseFixture.isSalesTaxEnabled()){
            TestUtils.setSystemParameter(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_SALES_TAX_IND,"Y");
        }else{
            TestUtils.setSystemParameter(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_SALES_TAX_IND,"N");
        }
        
        return doc;
    }

    public String toString() {

        return new ToStringBuilder(this).append("requisitionOrganizationReference1Text", requisitionOrganizationReference1Text).append("requisitionOrganizationReference2Text", requisitionOrganizationReference2Text).append("requisitionOrganizationReference3Text", requisitionOrganizationReference3Text).append("alternate1VendorName", alternate1VendorName).append("alternate2VendorName", alternate2VendorName).append("alternate3VendorName", alternate3VendorName)
                .append("alternate4VendorName", alternate4VendorName).append("alternate5VendorName", alternate5VendorName).append("organizationAutomaticPurchaseOrderLimit", organizationAutomaticPurchaseOrderLimit).append("purapDocumentFixture", purapDocumentFixture).append("purchasingDocumentFixture", purchasingDocumentFixture).append("requisitionItemFixtures", requisitionItemFixtures).toString();
    }
}
