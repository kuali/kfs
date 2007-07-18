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
package org.kuali.module.purap.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants.CREDIT_MEMO_TYPES;
import org.kuali.module.purap.bo.CreditMemoItem;
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.service.CreditMemoCreateService;
import org.kuali.module.purap.service.CreditMemoService;
import org.kuali.module.vendor.VendorConstants;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.service.VendorService;
import org.kuali.module.vendor.util.VendorUtils;

/**
 * Performs inital population of the credit memo document.
 */
public class CreditMemoCreateServiceImpl implements CreditMemoCreateService {
    private VendorService vendorService;
    private CreditMemoService creditMemoService;

    /**
     * @see org.kuali.module.purap.service.CreditMemoCreateService#populateDocumentAfterInit(org.kuali.module.purap.document.CreditMemoDocument)
     */
    public void populateDocumentAfterInit(CreditMemoDocument cmDocument) {
        if (StringUtils.equals(cmDocument.getCreditMemoType(), CREDIT_MEMO_TYPES.TYPE_PREQ)) {
            populateDocumentFromPreq(cmDocument);
        }
        else if (StringUtils.equals(cmDocument.getCreditMemoType(), CREDIT_MEMO_TYPES.TYPE_PO)) {
            populateDocumentFromPO(cmDocument);
        }
        else {
            populateDocumentFromVendor(cmDocument);
        }

        populateDocumentDescription(cmDocument);
    }

    /**
     * Populate Credit Memo of type PREQ.
     * 
     * @param cmDocument - Credit Memo Document to Populate
     */
    protected void populateDocumentFromPreq(CreditMemoDocument cmDocument) {
        PaymentRequestDocument paymentRequestDocument = SpringServiceLocator.getPaymentRequestService().getPaymentRequestById(cmDocument.getPaymentRequestIdentifier());
        cmDocument.setPaymentRequest(paymentRequestDocument);
        cmDocument.setPurchaseOrder(paymentRequestDocument.getPurchaseOrderDocument());

        // credit memo address taken directly from payment request
        cmDocument.setVendorHeaderGeneratedIdentifier(paymentRequestDocument.getVendorHeaderGeneratedIdentifier());
        cmDocument.setVendorDetailAssignedIdentifier(paymentRequestDocument.getVendorDetailAssignedIdentifier());
        cmDocument.setVendorAddressGeneratedIdentifier(paymentRequestDocument.getVendorAddressGeneratedIdentifier());
        cmDocument.setVendorCustomerNumber(paymentRequestDocument.getVendorCustomerNumber());
        cmDocument.setVendorName(paymentRequestDocument.getVendorName());
        cmDocument.setVendorLine1Address(paymentRequestDocument.getVendorLine1Address());
        cmDocument.setVendorLine2Address(paymentRequestDocument.getVendorLine2Address());
        cmDocument.setVendorCityName(paymentRequestDocument.getVendorCityName());
        cmDocument.setVendorStateCode(paymentRequestDocument.getVendorStateCode());
        cmDocument.setVendorPostalCode(paymentRequestDocument.getVendorPostalCode());
        cmDocument.setVendorCountryCode(paymentRequestDocument.getVendorCountryCode());
        
        populateItemLinesFromPreq(cmDocument);
    }

    /**
     * Populates the credit memo items from the payment request items.
     * 
     * @param cmDocument - Credit Memo Document to Populate
     */
    protected void populateItemLinesFromPreq(CreditMemoDocument cmDocument) {
        PaymentRequestDocument preqDocument = cmDocument.getPaymentRequest();

        List<PurchaseOrderItem> invoicedItems = creditMemoService.getPOInvoicedItems(cmDocument.getPurchaseOrder());
        for (PurchaseOrderItem poItem : invoicedItems) {
            PaymentRequestItem preqItemToTemplate = (PaymentRequestItem) preqDocument.getItemByLineNumber(poItem.getItemLineNumber());

            if (preqItemToTemplate != null && preqItemToTemplate.getItemType().isItemTypeAboveTheLineIndicator()) {
                if (preqItemToTemplate.getItemQuantity() != null || preqItemToTemplate.getExtendedPrice().isNonZero()) {
                    cmDocument.getItems().add(new CreditMemoItem(cmDocument, preqItemToTemplate, poItem));
                }
            }
        }

        // add below the line items
        SpringServiceLocator.getPurapService().addBelowLineItems(cmDocument);
    }

    /**
     * Populate Credit Memo of type PO.
     * 
     * @param cmDocument - Credit Memo Document to Populate
     */
    protected void populateDocumentFromPO(CreditMemoDocument cmDocument) {
        PurchaseOrderDocument purchaseOrderDocument = (SpringServiceLocator.getPurchaseOrderService().getCurrentPurchaseOrder(cmDocument.getPurchaseOrderIdentifier()));
        cmDocument.setPurchaseOrder(purchaseOrderDocument);

        cmDocument.setVendorHeaderGeneratedIdentifier(purchaseOrderDocument.getVendorHeaderGeneratedIdentifier());
        cmDocument.setVendorDetailAssignedIdentifier(purchaseOrderDocument.getVendorDetailAssignedIdentifier());
        cmDocument.setVendorCustomerNumber(purchaseOrderDocument.getVendorCustomerNumber());
        cmDocument.setVendorName(purchaseOrderDocument.getVendorName());
        cmDocument.setAccountsPayablePurchasingDocumentLinkIdentifier(purchaseOrderDocument.getAccountsPayablePurchasingDocumentLinkIdentifier());


        // populate cm vendor address with the default remit address type for the vendor if found
        String userCampus = GlobalVariables.getUserSession().getUniversalUser().getCampusCode();
        VendorAddress vendorAddress = vendorService.getVendorDefaultAddress(purchaseOrderDocument.getVendorHeaderGeneratedIdentifier(), purchaseOrderDocument.getVendorDetailAssignedIdentifier(), VendorConstants.AddressTypes.REMIT, userCampus);
        if (vendorAddress != null) {
            cmDocument.templateVendorAddress(vendorAddress);
            cmDocument.setVendorAddressGeneratedIdentifier(vendorAddress.getVendorAddressGeneratedIdentifier());
        }
        else {
            // set address from PO
            cmDocument.setVendorAddressGeneratedIdentifier(purchaseOrderDocument.getVendorAddressGeneratedIdentifier());
            cmDocument.setVendorLine1Address(purchaseOrderDocument.getVendorLine1Address());
            cmDocument.setVendorLine2Address(purchaseOrderDocument.getVendorLine2Address());
            cmDocument.setVendorCityName(purchaseOrderDocument.getVendorCityName());
            cmDocument.setVendorStateCode(purchaseOrderDocument.getVendorStateCode());
            cmDocument.setVendorPostalCode(purchaseOrderDocument.getVendorPostalCode());
            cmDocument.setVendorCountryCode(purchaseOrderDocument.getVendorCountryCode());
        }
        
        populateItemLinesFromPO(cmDocument);
    }

    /**
     * Populates the credit memo items from the payment request items.
     * 
     * @param cmDocument - Credit Memo Document to Populate
     */
    protected void populateItemLinesFromPO(CreditMemoDocument cmDocument) {
        List<PurchaseOrderItem> invoicedItems = creditMemoService.getPOInvoicedItems(cmDocument.getPurchaseOrder());
        for (PurchaseOrderItem poItem : invoicedItems) {
            cmDocument.getItems().add(new CreditMemoItem(cmDocument, poItem));
        }

        // add below the line items
        SpringServiceLocator.getPurapService().addBelowLineItems(cmDocument);
        
        // TODO: account distribution?
    }

    /**
     * Populate Credit Memo of type Vendor.
     * 
     * @param cmDocument - Credit Memo Document to Populate
     */
    protected void populateDocumentFromVendor(CreditMemoDocument cmDocument) {
        Integer vendorHeaderId = VendorUtils.getVendorHeaderId(cmDocument.getVendorNumber());
        Integer vendorDetailId = VendorUtils.getVendorDetailId(cmDocument.getVendorNumber());

        VendorDetail vendorDetail = SpringServiceLocator.getVendorService().getVendorDetail(vendorHeaderId, vendorDetailId);
        cmDocument.setVendorDetail(vendorDetail);

        cmDocument.setVendorHeaderGeneratedIdentifier(vendorDetail.getVendorHeaderGeneratedIdentifier());
        cmDocument.setVendorDetailAssignedIdentifier(vendorDetail.getVendorDetailAssignedIdentifier());
        cmDocument.setVendorCustomerNumber(vendorDetail.getVendorNumber());
        cmDocument.setVendorName(vendorDetail.getVendorName());


        // credit memo type vendor uses the default remit type address for the vendor if found
        String userCampus = GlobalVariables.getUserSession().getUniversalUser().getCampusCode();
        VendorAddress vendorAddress = vendorService.getVendorDefaultAddress(vendorHeaderId, vendorDetailId, VendorConstants.AddressTypes.REMIT, userCampus);
        if (vendorAddress == null) {
            // pick up the default vendor po address type
            vendorAddress = vendorService.getVendorDefaultAddress(vendorHeaderId, vendorDetailId, VendorConstants.AddressTypes.PURCHASE_ORDER, userCampus);
        }

        cmDocument.setVendorAddressGeneratedIdentifier(vendorAddress.getVendorAddressGeneratedIdentifier());
        cmDocument.templateVendorAddress(vendorAddress);
        
        // add below the line items
        SpringServiceLocator.getPurapService().addBelowLineItems(cmDocument);
    }

    /**
     * Defaults the document description based on the cm type.
     * 
     * @param cmDocument - Credit Memo Document to Populate
     */
    private void populateDocumentDescription(CreditMemoDocument cmDocument) {
        String description = "";
        if (StringUtils.equals(cmDocument.getCreditMemoType(), CREDIT_MEMO_TYPES.TYPE_VENDOR)) {
            description = "Vendor: " + cmDocument.getVendorName();
        }
        else {
            description = "PO: " + cmDocument.getPurchaseOrder().getPurapDocumentIdentifier() + " Vendor: " + cmDocument.getVendorName();
        }

        cmDocument.getDocumentHeader().setFinancialDocumentDescription(description);
    }

    /**
     * Sets the creditMemoService attribute value.
     * 
     * @param creditMemoService The creditMemoService to set.
     */
    public void setCreditMemoService(CreditMemoService creditMemoService) {
        this.creditMemoService = creditMemoService;
    }

    /**
     * Sets the vendorService attribute value.
     * 
     * @param vendorService The vendorService to set.
     */
    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }
}
