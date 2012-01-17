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
package org.kuali.kfs.module.purap.document;

import static org.kuali.kfs.sys.fixture.UserNameFixture.appleton;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItemBase;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderDocumentFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.module.purap.fixture.SystemParameterBelowTheLineItemCMFixture;
import org.kuali.kfs.module.purap.fixture.SystemParameterBelowTheLineItemPaymentRequestFixture;
import org.kuali.kfs.module.purap.fixture.SystemParameterBelowTheLineItemPurchaseOrderFixture;
import org.kuali.kfs.module.purap.fixture.SystemParameterBelowTheLineItemRequisitionFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * This class is used to test the below the line item type rules 
 * that exist in System Parameter for Requisition. It does not test any 
 * other below the line item type rules that aren't in System Parameter.
 * The tests in this class are for RequisitionDocument, PurchaseOrderDocument,
 * PaymentRequestDocument and CreditMemoDocument.
 * If the values in the System Parameter change, someone will need to also
 * update this test as well as the fixtures relevant to the change.
 */
@ConfigureContext(session = UserNameFixture.parke)
public class SystemParameterBelowTheLineItemTypeTest extends KualiTestBase {

    private RequisitionDocument requisitionDocument = null;
    ParameterService parameterService;
    
    protected void setUp() throws Exception {
        super.setUp();
        parameterService = SpringContext.getBean(ParameterService.class);
    }

    protected void tearDown() throws Exception {
        requisitionDocument = null;
        super.tearDown();
    }

    /**
     * Tests that the existing item types in the Requisition match with the "ADDITIONAL_CHARGES_ITEM_TYPES" System Parameter.
     * 
     * @throws Exception
     */
    public final void testRequisitionExistingItemTypesAreValid() throws Exception {
        RequisitionDocument requisitionWithValidBelowLineItems = SystemParameterBelowTheLineItemRequisitionFixture.REQ_VALID_BELOW_LINE_ITEMS.createRequisitionDocument();
        RequisitionDocument requisitionWithInvalidBelowLineItems = SystemParameterBelowTheLineItemRequisitionFixture.REQ_INVALID_BELOW_LINE_ITEMS.createRequisitionDocument();
        
        testExistingItemTypesAreValid(RequisitionDocument.class, requisitionWithValidBelowLineItems, requisitionWithInvalidBelowLineItems);    
    }
    
    /**
     * Tests the "ITEM_TYPES_ALLOWING_NEGATIVE" System Parameter for Requisition.
     * 
     * @throws Exception
     */
    public final void testRequisitionAllowsNegative() throws Exception {
        RequisitionDocument requisitionWithNoBelowLineItems = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
        RequisitionDocument requisitionWithNegativeBelowLineItems = SystemParameterBelowTheLineItemRequisitionFixture.REQ_WITH_NEGATIVE_BELOW_LINE_ITEMS.createRequisitionDocument();
        testAllowsNegative(RequisitionDocument.class, requisitionWithNoBelowLineItems, requisitionWithNegativeBelowLineItems);
    }
    
    /**
     * Tests the "ITEM_TYPES_ALLOWING_POSITIVE" System Parameter for Requisition.
     * 
     * @throws Exception
     */
    public final void testRequisitionAllowsPositive() throws Exception {
        RequisitionDocument requisitionWithNoBelowLineItems = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
        RequisitionDocument requisitionWithValidBelowLineItems = SystemParameterBelowTheLineItemRequisitionFixture.REQ_VALID_BELOW_LINE_ITEMS.createRequisitionDocument();
        testAllowsPositive(RequisitionDocument.class, requisitionWithValidBelowLineItems, requisitionWithNoBelowLineItems);
    }
    
    /**
     * Tests the "ITEM_TYPES_ALLOWING_ZERO" System Parameter for Requisition.
     * 
     * @throws Exception
     */
    public final void testRequisitionAllowsZero() throws Exception {
        RequisitionDocument requisitionWithZeroBelowLineItems = SystemParameterBelowTheLineItemRequisitionFixture.REQ_WITH_ZERO_BELOW_LINE_ITEMS.createRequisitionDocument();
        RequisitionDocument requisitionWithNoBelowLineItems = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
        testAllowsZero(RequisitionDocument.class, requisitionWithZeroBelowLineItems, requisitionWithNoBelowLineItems);
    }
    
    /**
     * Tests the "ITEM_TYPES_REQUIRING_USER_ENTERED_DESCRIPTION" System Parameter for Requisition.
     * 
     * @throws Exception
     */
    public final void testRequisitionRequiringDescription() throws Exception {
        RequisitionDocument requisitionWithBelowLineItemsWithoutDescription = SystemParameterBelowTheLineItemRequisitionFixture.REQ_WITH_BELOW_LINE_ITEMS_WITHOUT_DESCRIPTION.createRequisitionDocument();
        RequisitionDocument requisitionWithBelowLineItemsWithDescription = SystemParameterBelowTheLineItemRequisitionFixture.REQ_WITH_BELOW_LINE_ITEMS_WITH_DESCRIPTION.createRequisitionDocument();
        testRequiringDescription(RequisitionDocument.class, requisitionWithBelowLineItemsWithoutDescription, requisitionWithBelowLineItemsWithDescription);
    }
    
    /**
     * Tests that the existing item types in the Purchase Order match with the "ADDITIONAL_CHARGES_ITEM_TYPES" System Parameter.
     * 
     * @throws Exception
     */
    public final void testPurchaseOrderExistingItemTypesAreValid() throws Exception {
        PurchaseOrderDocument purchaseOrderWithValidBelowLineItems = SystemParameterBelowTheLineItemPurchaseOrderFixture.PO_VALID_BELOW_LINE_ITEMS.createPurchaseOrderDocument();
        PurchaseOrderDocument purchaseOrderWithInvalidBelowLineItems = SystemParameterBelowTheLineItemPurchaseOrderFixture.PO_INVALID_BELOW_LINE_ITEMS.createPurchaseOrderDocument();
        
        testExistingItemTypesAreValid(PurchaseOrderDocument.class, purchaseOrderWithValidBelowLineItems, purchaseOrderWithInvalidBelowLineItems);    
    }
    
    /**
     * Tests the "ITEM_TYPES_ALLOWING_NEGATIVE" System Parameter for Purchase Order.
     * 
     * @throws Exception
     */
    public final void testPurchaseOrderAllowsNegative() throws Exception {
        PurchaseOrderDocument purchaseOrderWithNoBelowLineItems = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS.createPurchaseOrderDocument();
        PurchaseOrderDocument purchaseOrderWithNegativeBelowLineItems = SystemParameterBelowTheLineItemPurchaseOrderFixture.PO_WITH_NEGATIVE_BELOW_LINE_ITEMS.createPurchaseOrderDocument();
        testAllowsNegative(PurchaseOrderDocument.class, purchaseOrderWithNoBelowLineItems, purchaseOrderWithNegativeBelowLineItems);
    }
    
    /**
     * Tests the "ITEM_TYPES_ALLOWING_POSITIVE" System Parameter for Purchase Order.
     * 
     * @throws Exception
     */
    public final void testPurchaseOrderAllowsPositive() throws Exception {
        PurchaseOrderDocument purchaseOrderWithValidBelowLineItems = SystemParameterBelowTheLineItemPurchaseOrderFixture.PO_VALID_BELOW_LINE_ITEMS.createPurchaseOrderDocument();
        PurchaseOrderDocument purchaseOrderWithNoBelowLineItems = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS.createPurchaseOrderDocument();
        testAllowsPositive(PurchaseOrderDocument.class, purchaseOrderWithValidBelowLineItems, purchaseOrderWithNoBelowLineItems);
    }
    
    /**
     * Tests the "ITEM_TYPES_ALLOWING_ZERO" System Parameter for Purchase Order.
     * 
     * @throws Exception
     */
    public final void testPurchaseOrderAllowsZero() throws Exception {
        PurchaseOrderDocument purchaseOrderWithZeroBelowLineItems = SystemParameterBelowTheLineItemPurchaseOrderFixture.PO_WITH_ZERO_BELOW_LINE_ITEMS.createPurchaseOrderDocument();
        PurchaseOrderDocument purchaseOrderWithNoBelowLineItems = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS.createPurchaseOrderDocument();
        testAllowsZero(PurchaseOrderDocument.class, purchaseOrderWithZeroBelowLineItems, purchaseOrderWithNoBelowLineItems);
    }
    
    /**
     * Tests the "ITEM_TYPES_REQUIRING_USER_ENTERED_DESCRIPTION" System Parameter for Purchase Order.
     * 
     * @throws Exception
     */
    public final void testPurchaseOrderRequiringDescription() throws Exception {
        PurchaseOrderDocument purchaseOrderWithBelowLineItemsWithoutDescription = SystemParameterBelowTheLineItemPurchaseOrderFixture.PO_WITH_BELOW_LINE_ITEMS_WITHOUT_DESCRIPTION.createPurchaseOrderDocument();
        PurchaseOrderDocument purchaseOrderWithBelowLineItemsWithDescription = SystemParameterBelowTheLineItemPurchaseOrderFixture.PO_WITH_BELOW_LINE_ITEMS_WITH_DESCRIPTION.createPurchaseOrderDocument();
        testRequiringDescription(PurchaseOrderDocument.class, purchaseOrderWithBelowLineItemsWithoutDescription, purchaseOrderWithBelowLineItemsWithDescription);
    }
    
    /**
     * Tests that the existing item types in the Payment Request match with the "ADDITIONAL_CHARGES_ITEM_TYPES" System Parameter.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = appleton)
    public final void testPaymentRequestExistingItemTypesAreValid() throws Exception {
        PaymentRequestDocument paymentRequestWithValidBelowLineItems = SystemParameterBelowTheLineItemPaymentRequestFixture.PREQ_VALID_BELOW_LINE_ITEMS.createPaymentRequestDocument();
        PaymentRequestDocument paymentRequestWithInvalidBelowLineItems = SystemParameterBelowTheLineItemPaymentRequestFixture.PREQ_INVALID_BELOW_LINE_ITEMS.createPaymentRequestDocument();
        testExistingItemTypesAreValid(PaymentRequestDocument.class, paymentRequestWithValidBelowLineItems, paymentRequestWithInvalidBelowLineItems);    
    }
    
    /**
     * Tests the "ITEM_TYPES_ALLOWING_NEGATIVE" System Parameter for Payment Request.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = appleton)
    public final void testPaymentRequestAllowsNegative() throws Exception {
        PaymentRequestDocument paymentRequestWithValidNegativeBelowLineItems = SystemParameterBelowTheLineItemPaymentRequestFixture.PREQ_WITH_VALID_NEGATIVE_BELOW_LINE_ITEMS.createPaymentRequestDocument();
        PaymentRequestDocument paymentRequestWithInvalidNegativeBelowLineItems = SystemParameterBelowTheLineItemPaymentRequestFixture.PREQ_WITH_INVALID_NEGATIVE_BELOW_LINE_ITEMS.createPaymentRequestDocument();
        testAllowsNegative(PaymentRequestDocument.class, paymentRequestWithValidNegativeBelowLineItems, paymentRequestWithInvalidNegativeBelowLineItems);
    }
    
    /**
     * Tests the "ITEM_TYPES_ALLOWING_POSITIVE" System Parameter for Payment Request.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = appleton)
    public final void testPaymentRequestAllowsPositive() throws Exception {
        PaymentRequestDocument paymentRequestWithValidPositiveBelowLineItems = SystemParameterBelowTheLineItemPaymentRequestFixture.PREQ_WITH_VALID_POSITIVE_BELOW_LINE_ITEMS.createPaymentRequestDocument();
        PaymentRequestDocument paymentRequestWithInvalidPositiveBelowLineItems = SystemParameterBelowTheLineItemPaymentRequestFixture.PREQ_WITH_INVALID_POSITIVE_BELOW_LINE_ITEMS.createPaymentRequestDocument();
        testAllowsPositive(PaymentRequestDocument.class, paymentRequestWithValidPositiveBelowLineItems, paymentRequestWithInvalidPositiveBelowLineItems);
    }
    
    /**
     * Tests the "ITEM_TYPES_ALLOWING_ZERO" System Parameter for Payment Request.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = appleton)
    public final void testPaymentRequestAllowsZero() throws Exception {
        PaymentRequestDocument paymentRequestWithValidZeroBelowLineItems = SystemParameterBelowTheLineItemPaymentRequestFixture.PREQ_WITH_VALID_ZERO_BELOW_LINE_ITEMS.createPaymentRequestDocument();
        PaymentRequestDocument paymentRequestWithInvalidZeroBelowLineItems = SystemParameterBelowTheLineItemPaymentRequestFixture.PREQ_WITH_INVALID_ZERO_BELOW_LINE_ITEMS.createPaymentRequestDocument();
        testAllowsZero(PaymentRequestDocument.class, paymentRequestWithValidZeroBelowLineItems, paymentRequestWithInvalidZeroBelowLineItems);
    }
    
    /**
     * Tests the "ITEM_TYPES_REQUIRING_USER_ENTERED_DESCRIPTION" System Parameter for Payment Request.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = appleton)
    public final void testPaymentRequestRequiringDescription() throws Exception {
        PaymentRequestDocument paymentRequestWithBelowLineItemsWithoutDescription = SystemParameterBelowTheLineItemPaymentRequestFixture.PREQ_WITH_BELOW_LINE_ITEMS_WITHOUT_DESCRIPTION.createPaymentRequestDocument();
        PaymentRequestDocument paymentRequestWithBelowLineItemsWithDescription = SystemParameterBelowTheLineItemPaymentRequestFixture.PREQ_WITH_BELOW_LINE_ITEMS_WITH_DESCRIPTION.createPaymentRequestDocument();
        testRequiringDescription(PaymentRequestDocument.class, paymentRequestWithBelowLineItemsWithoutDescription, paymentRequestWithBelowLineItemsWithDescription);
    }
    
    /**
     * Tests that the existing item types in the CreditMemo match with the "ADDITIONAL_CHARGES_ITEM_TYPES" System Parameter.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = appleton)
    public final void testCreditMemoExistingItemTypesAreValid() throws Exception {
        VendorCreditMemoDocument creditMemoWithValidBelowLineItems = SystemParameterBelowTheLineItemCMFixture.CM_VALID_BELOW_LINE_ITEMS.createCreditMemoDocument();
        VendorCreditMemoDocument creditMemoWithInvalidBelowLineItems = SystemParameterBelowTheLineItemCMFixture.CM_INVALID_BELOW_LINE_ITEMS.createCreditMemoDocument();
        
        testExistingItemTypesAreValid(VendorCreditMemoDocument.class, creditMemoWithValidBelowLineItems, creditMemoWithInvalidBelowLineItems);    
    }
    
    /**
     * Tests the "ITEM_TYPES_ALLOWING_NEGATIVE" System Parameter for CreditMemo.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = appleton)
    public final void testCreditMemoAllowsNegative() throws Exception {
        VendorCreditMemoDocument creditMemoWithValidNegativeBelowLineItems = SystemParameterBelowTheLineItemCMFixture.CM_VALID_NEGATIVE_BELOW_LINE_ITEMS.createCreditMemoDocument();
        VendorCreditMemoDocument creditMemoWithInvalidNegativeBelowLineItems = SystemParameterBelowTheLineItemCMFixture.CM_INVALID_NEGATIVE_BELOW_LINE_ITEMS.createCreditMemoDocument();
        testAllowsNegative(VendorCreditMemoDocument.class, creditMemoWithValidNegativeBelowLineItems, creditMemoWithInvalidNegativeBelowLineItems);
    }
    
    /**
     * Tests the "ITEM_TYPES_ALLOWING_POSITIVE" System Parameter for Credit Memo.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = appleton)
    public final void testCreditMemoAllowsPositive() throws Exception {
        VendorCreditMemoDocument creditMemoWithValidPositiveBelowLineItems = SystemParameterBelowTheLineItemCMFixture.CM_VALID_POSITIVE_BELOW_LINE_ITEMS.createCreditMemoDocument();
        VendorCreditMemoDocument creditMemoWithInvalidPositiveBelowLineItems = SystemParameterBelowTheLineItemCMFixture.CM_INVALID_POSITIVE_BELOW_LINE_ITEMS.createCreditMemoDocument();
        testAllowsPositive(VendorCreditMemoDocument.class, creditMemoWithValidPositiveBelowLineItems, creditMemoWithInvalidPositiveBelowLineItems);
    }

    /**
     * Tests the "ITEM_TYPES_ALLOWING_ZERO" System Parameter for Credit Memo.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = appleton)
    public final void testCreditMemoAllowsZero() throws Exception {
        VendorCreditMemoDocument creditMemoWithValidZeroBelowLineItems = SystemParameterBelowTheLineItemCMFixture.CM_VALID_ZERO_BELOW_LINE_ITEMS.createCreditMemoDocument();
        VendorCreditMemoDocument creditMemoWithInvalidZeroBelowLineItems = SystemParameterBelowTheLineItemCMFixture.CM_INVALID_ZERO_BELOW_LINE_ITEMS.createCreditMemoDocument();
        testAllowsZero(VendorCreditMemoDocument.class, creditMemoWithValidZeroBelowLineItems, creditMemoWithInvalidZeroBelowLineItems);
    }
    
    /**
     * Tests the "ITEM_TYPES_REQUIRING_USER_ENTERED_DESCRIPTION" System Parameter for Credit Memo.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = appleton)
    public final void testCreditMemoRequiringDescription() throws Exception {
        VendorCreditMemoDocument creditMemoWithBelowLineItemsWithoutDescription = SystemParameterBelowTheLineItemCMFixture.CM_WITH_BELOW_LINE_ITEMS_WITHOUT_DESCRIPTION.createCreditMemoDocument();
        VendorCreditMemoDocument creditMemoWithBelowLineItemsWithDescription = SystemParameterBelowTheLineItemCMFixture.CM_WITH_BELOW_LINE_ITEMS_WITH_DESCRIPTION.createCreditMemoDocument();
        testRequiringDescription(VendorCreditMemoDocument.class, creditMemoWithBelowLineItemsWithoutDescription, creditMemoWithBelowLineItemsWithDescription);
    }
    
    /**
     * Verifies that the existing item types in the documents match with the "ADDITIONAL_CHARGES_ITEM_TYPES" as 
     * specified in the System Parameter and does not contain any other item types that aren't specified in
     * the System Parameter. It will loop through the list of items from the validDocument and assertTrue
     * on each of the item's type, then it will loop through the list of items from the invalidDocument and
     * assertFalse on each of the item's type.
     * 
     * @param documentClass    The class of the document we want to test, for example, RequisitionDocument, PurchaseOrderDocument, PaymentRequestDocument, CreditMemoDocument.
     * @param validDocument    The document containing a list of items whose types are supposed to be valid according to the System Parameter. 
     * @param invalidDocument  The document containing a list of items whose types are supposed to be invalid according to the System Parameter.
     * @throws Exception
     */
    private final void testExistingItemTypesAreValid(Class documentClass, PurchasingAccountsPayableDocumentBase validDocument, PurchasingAccountsPayableDocumentBase invalidDocument) throws Exception {

        List<String> validTypes = new ArrayList<String>( parameterService.getParameterValuesAsString(documentClass, PurapConstants.BELOW_THE_LINES_PARAMETER) );
        
        for (PurApItemBase theItem :(List<PurApItemBase>)validDocument.getItems()) {
            String theItemType = theItem.getItemTypeCode();
            assertTrue(validTypes.contains(theItemType));
        }
        
        for (PurApItemBase theItem :(List<PurApItemBase>)invalidDocument.getItems()) {
            String theItemType = theItem.getItemTypeCode();
            assertFalse(validTypes.contains(theItemType));
        }
    }
    
    /**
     * Verifies that the existing items in the documents, if their extended price is negative and if the item type
     * is below the line items, should match with item types specified in the "ITEM_TYPES_ALLOWING_NEGATIVE"
     * System Parameter. It will loop through the list of items from the validDocument and assertTrue
     * on each of the item's type, then it will loop through the list of items from the invalidDocument and
     * assertFalse on each of the item's type.
     * 
     * @param documentClass    The class of the document we want to test, for example, RequisitionDocument, PurchaseOrderDocument, PaymentRequestDocument, CreditMemoDocument.
     * @param validDocument    The document containing a list of items whose types are supposed to be valid according to the System Parameter. 
     * @param invalidDocument  The document containing a list of items whose types are supposed to be invalid according to the System Parameter.
     * @throws Exception
     */
    private final void testAllowsNegative(Class documentClass, PurchasingAccountsPayableDocumentBase validDocument, PurchasingAccountsPayableDocumentBase invalidDocument) throws Exception {
    
        List<String> allowNegativeTypes = new ArrayList<String>( parameterService.getParameterValuesAsString(documentClass, "ITEM_TYPES_ALLOWING_NEGATIVE") );

        for (PurApItemBase theItem :(List<PurApItemBase>)validDocument.getItems()) {
            if (theItem.getTotalAmount().isNegative() && theItem.getItemType().isAdditionalChargeIndicator()) {
                String theItemType = theItem.getItemTypeCode();
                assertTrue(allowNegativeTypes.contains(theItemType));
            }
        }
        for (PurApItemBase theItem :(List<PurApItemBase>)invalidDocument.getItems()) {
            if (theItem.getTotalAmount().isNegative() && theItem.getItemType().isAdditionalChargeIndicator()) {
                String theItemType = theItem.getItemTypeCode();
                assertFalse(allowNegativeTypes.contains(theItemType));
            }
        }
    }

    /**
     * Verifies that the existing items in the documents, if their extended price is positive and if the item type
     * is below the line items, should match with item types specified in the "ITEM_TYPES_ALLOWING_POSITIVE"
     * System Parameter. It will loop through the list of items from the validDocument and assertTrue
     * on each of the item's type, then it will loop through the list of items from the invalidDocument and
     * assertFalse on each of the item's type.
     * 
     * @param documentClass    The class of the document we want to test, for example, RequisitionDocument, PurchaseOrderDocument, PaymentRequestDocument, CreditMemoDocument.
     * @param validDocument    The document containing a list of items whose types are supposed to be valid according to the System Parameter. 
     * @param invalidDocument  The document containing a list of items whose types are supposed to be invalid according to the System Parameter.
     * @throws Exception
     */
    private final void testAllowsPositive(Class documentClass, PurchasingAccountsPayableDocumentBase validDocument, PurchasingAccountsPayableDocumentBase invalidDocument) throws Exception {
    
        List<String> allowPositiveTypes = new ArrayList<String>( parameterService.getParameterValuesAsString(documentClass, "ITEM_TYPES_ALLOWING_POSITIVE") );
        
        for (PurApItemBase theItem :(List<PurApItemBase>)validDocument.getItems()) {
            if (theItem.getTotalAmount().isPositive() && theItem.getItemType().isAdditionalChargeIndicator()) {
                String theItemType = theItem.getItemTypeCode();
                assertTrue(allowPositiveTypes.contains(theItemType));
            }
        }
        for (PurApItemBase theItem :(List<PurApItemBase>)invalidDocument.getItems()) {
            if (theItem.getTotalAmount().isPositive() && theItem.getItemType().isAdditionalChargeIndicator()) {
                String theItemType = theItem.getItemTypeCode();
                assertFalse(allowPositiveTypes.contains(theItemType));
            }
        }
    }
    
    /**
     * Verifies that the existing items in the documents, if their extended price is zero and if the item type
     * is below the line items, should match with item types specified in the "ITEM_TYPES_ALLOWING_ZERO"
     * System Parameter. It will loop through the list of items from the validDocument and assertTrue
     * on each of the item's type, then it will loop through the list of items from the invalidDocument and
     * assertFalse on each of the item's type.
     * 
     * @param documentClass    The class of the document we want to test, for example, RequisitionDocument, PurchaseOrderDocument, PaymentRequestDocument, CreditMemoDocument.
     * @param validDocument    The document containing a list of items whose types are supposed to be valid according to the System Parameter. 
     * @param invalidDocument  The document containing a list of items whose types are supposed to be invalid according to the System Parameter.
     * @throws Exception
     */
    private final void testAllowsZero(Class documentClass, PurchasingAccountsPayableDocumentBase validDocument, PurchasingAccountsPayableDocumentBase invalidDocument) throws Exception {

        List<String> allowZeroTypes = new ArrayList<String>( parameterService.getParameterValuesAsString(documentClass, "ITEM_TYPES_ALLOWING_ZERO") );
        
        for (PurApItemBase theItem :(List<PurApItemBase>)validDocument.getItems()) {
            if (theItem.getTotalAmount().isZero() && theItem.getItemType().isAdditionalChargeIndicator()) {
                String theItemType = theItem.getItemTypeCode();
                assertTrue(allowZeroTypes.contains(theItemType));
            }
        }
        for (PurApItemBase theItem :(List<PurApItemBase>)invalidDocument.getItems()) {
            if (theItem.getTotalAmount().isZero() && theItem.getItemType().isAdditionalChargeIndicator()) {
                String theItemType = theItem.getItemTypeCode();
                assertFalse(allowZeroTypes.contains(theItemType));
            }
        }
    }
    
    /**
     * Verifies that the existing items in the documents, if the items do not contain description,
     * then their item types cannot be specified within the "REQUIRING_USER_ENTERED_DESCRIPTION" System Parameter. 
     * It will loop through the list of items from docBlankDesc and checks that if the item description is blank,
     * then assertFalse each of the item's type that the item type is not specified in the "REQUIRING_USER_ENTERED_DESCRIPTION"
     * System Parameter.
     * It will then loop through the list of items from docItemTypesRequiredDesc and checks that if the 
     * item type is specified in the "REQUIRING_USER_ENTERED_DESCRIPTION", then assertFalse that the description
     * is not blank.
     * 
     * @param documentClass             The class of the document we want to test, for example, RequisitionDocument, PurchaseOrderDocument, PaymentRequestDocument, CreditMemoDocument.
     * @param docBlankDesc              The document containing a list of items whose description is blank. 
     * @param docItemTypesRequiredDesc  The document containing a list of items whose item types are specified in the System Parameter as the items requiring description.
     * @throws Exception
     */
    private final void testRequiringDescription(Class documentClass, PurchasingAccountsPayableDocumentBase docBlankDesc, PurchasingAccountsPayableDocumentBase docItemTypesRequiredDesc) throws Exception {

        List<String> requiringDescriptionTypes = new ArrayList<String>( parameterService.getParameterValuesAsString(documentClass, "ITEM_TYPES_REQUIRING_USER_ENTERED_DESCRIPTION") );
        
        for (PurApItemBase theItem :(List<PurApItemBase>)docBlankDesc.getItems()) {
            String theItemType = theItem.getItemTypeCode();
            if (StringUtils.isBlank(theItem.getItemDescription())) {
                assertFalse(requiringDescriptionTypes.contains(theItemType));
            }
        }
        
        for (PurApItemBase theItem :(List<PurApItemBase>)docItemTypesRequiredDesc.getItems()) {
            String theItemType = theItem.getItemTypeCode();
            if (requiringDescriptionTypes.contains(theItemType)) {
                assertFalse(StringUtils.isBlank(theItem.getItemDescription()));
            }
        }
    }
}

