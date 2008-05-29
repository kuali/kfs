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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.bo.CreditMemoItem;
import org.kuali.module.purap.bo.PaymentRequestAccount;
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.service.AccountsPayableService;
import org.kuali.module.purap.service.CreditMemoCreateService;
import org.kuali.module.purap.service.CreditMemoService;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.purap.util.ExpiredOrClosedAccountEntry;
import org.kuali.module.vendor.VendorConstants;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.service.VendorService;
import org.kuali.module.vendor.util.VendorUtils;


/**
 * Performs initial population of the credit memo document.
 */
public class CreditMemoCreateServiceImpl implements CreditMemoCreateService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreditMemoServiceImpl.class);
    private VendorService vendorService;
    private CreditMemoService creditMemoService;
    private AccountsPayableService accountsPayableService;
    private PurapService purapService;
    private PurchaseOrderService purchaseOrderService;
    private PaymentRequestService paymentRequestService;
    private DataDictionaryService dataDictionaryService;
    
    /**
     * @see org.kuali.module.purap.service.CreditMemoCreateService#populateDocumentAfterInit(org.kuali.module.purap.document.CreditMemoDocument)
     */
    public void populateDocumentAfterInit(CreditMemoDocument cmDocument) {

        // make a call to search for expired/closed accounts
        HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList = accountsPayableService.getExpiredOrClosedAccountList(cmDocument);

        if (cmDocument.isSourceDocumentPaymentRequest()) {
            populateDocumentFromPreq(cmDocument, expiredOrClosedAccountList);
        }
        else if (cmDocument.isSourceDocumentPurchaseOrder()) {
            populateDocumentFromPO(cmDocument, expiredOrClosedAccountList);
        }
        else {
            populateDocumentFromVendor(cmDocument);
        }

        populateDocumentDescription(cmDocument);

        // write a note for expired/closed accounts if any exist and add a message stating there were expired/closed accounts at the
        // top of the document
        accountsPayableService.generateExpiredOrClosedAccountNote(cmDocument, expiredOrClosedAccountList);

        // set indicator so a message is displayed for accounts that were replaced due to expired/closed status
        if (!expiredOrClosedAccountList.isEmpty()) {
            cmDocument.setContinuationAccountIndicator(true);
        }

    }   

    /**
     * Populate Credit Memo of type Payment Request.
     * 
     * @param cmDocument - Credit Memo Document to Populate
     */
    protected void populateDocumentFromPreq(CreditMemoDocument cmDocument, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        PaymentRequestDocument paymentRequestDocument = paymentRequestService.getPaymentRequestById(cmDocument.getPaymentRequestIdentifier());
        cmDocument.getDocumentHeader().setOrganizationDocumentNumber(paymentRequestDocument.getDocumentHeader().getOrganizationDocumentNumber());
        cmDocument.setPaymentRequestDocument(paymentRequestDocument);
        cmDocument.setPurchaseOrderDocument(paymentRequestDocument.getPurchaseOrderDocument());

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
        cmDocument.setAccountsPayablePurchasingDocumentLinkIdentifier(paymentRequestDocument.getAccountsPayablePurchasingDocumentLinkIdentifier());

        // prep the item lines (also collect warnings for later display) this is only done on paymentRequest
        convertMoneyToPercent(paymentRequestDocument);
        populateItemLinesFromPreq(cmDocument, expiredOrClosedAccountList);
    }

    /**
     * Populates the credit memo items from the payment request items.
     * 
     * @param cmDocument - Credit Memo Document to Populate
     */
    protected void populateItemLinesFromPreq(CreditMemoDocument cmDocument, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        PaymentRequestDocument preqDocument = cmDocument.getPaymentRequestDocument();

        for (PaymentRequestItem preqItemToTemplate : (List<PaymentRequestItem>) preqDocument.getItems()) {
            if (preqItemToTemplate.getItemType().isItemTypeAboveTheLineIndicator()) {
                cmDocument.getItems().add(new CreditMemoItem(cmDocument, preqItemToTemplate, preqItemToTemplate.getPurchaseOrderItem(), expiredOrClosedAccountList));
            }
        }

        // add below the line items
        purapService.addBelowLineItems(cmDocument);
    }

    /**
     * Populate Credit Memo of type Purchase Order.
     * 
     * @param cmDocument - Credit Memo Document to Populate
     */
    protected void populateDocumentFromPO(CreditMemoDocument cmDocument, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        PurchaseOrderDocument purchaseOrderDocument = purchaseOrderService.getCurrentPurchaseOrder(cmDocument.getPurchaseOrderIdentifier());
        cmDocument.setPurchaseOrderDocument(purchaseOrderDocument);
        cmDocument.getDocumentHeader().setOrganizationDocumentNumber(purchaseOrderDocument.getDocumentHeader().getOrganizationDocumentNumber());

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

        populateItemLinesFromPO(cmDocument, expiredOrClosedAccountList);
    }

    /**
     * Populates the credit memo items from the payment request items.
     * 
     * @param cmDocument - Credit Memo Document to Populate
     */
    protected void populateItemLinesFromPO(CreditMemoDocument cmDocument, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        List<PurchaseOrderItem> invoicedItems = creditMemoService.getPOInvoicedItems(cmDocument.getPurchaseOrderDocument());
        for (PurchaseOrderItem poItem : invoicedItems) {
            cmDocument.getItems().add(new CreditMemoItem(cmDocument, poItem, expiredOrClosedAccountList));
        }

        // add below the line items
        purapService.addBelowLineItems(cmDocument);
    }

    /**
     * Populate Credit Memo of type Vendor.
     * 
     * @param cmDocument - Credit Memo Document to Populate
     */
    protected void populateDocumentFromVendor(CreditMemoDocument cmDocument) {
        Integer vendorHeaderId = VendorUtils.getVendorHeaderId(cmDocument.getVendorNumber());
        Integer vendorDetailId = VendorUtils.getVendorDetailId(cmDocument.getVendorNumber());

        VendorDetail vendorDetail = vendorService.getVendorDetail(vendorHeaderId, vendorDetailId);
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
        purapService.addBelowLineItems(cmDocument);
    }

    /**
     * Converts the amount to percent and updates the percent field on the CreditMemoAccount
     * 
     * @param pr The payment request document containing the accounts whose percentage would be set.
     */
    private void convertMoneyToPercent(PaymentRequestDocument pr) {
        LOG.debug("convertMoneyToPercent() started");
        Collection errors = new ArrayList();
        int itemNbr = 0;

        for (Iterator iter = pr.getItems().iterator(); iter.hasNext();) {
            PaymentRequestItem item = (PaymentRequestItem) iter.next();

            itemNbr++;
            String identifier = item.getItemIdentifierString();

            if (item.getExtendedPrice()!=null && item.getExtendedPrice().isNonZero()) {

                KualiDecimal accountTotal = KualiDecimal.ZERO;
                int accountIdentifier = 0;
                for (Iterator iterator = item.getSourceAccountingLines().iterator(); iterator.hasNext();) {
                    accountIdentifier++;
                    PaymentRequestAccount account = (PaymentRequestAccount) iterator.next();
                    KualiDecimal accountAmount = account.getAmount();
                    BigDecimal tmpPercent = BigDecimal.ZERO;
                    KualiDecimal extendedPrice = item.getExtendedPrice();
                    tmpPercent = accountAmount.bigDecimalValue().divide(extendedPrice.bigDecimalValue(), PurapConstants.PRORATION_SCALE.intValue(), KualiDecimal.ROUND_BEHAVIOR);
                    // test that the above amount is correct, if so just check that the total of all these matches the item total

                    KualiDecimal calcAmount = new KualiDecimal(tmpPercent.multiply(extendedPrice.bigDecimalValue()));
                    if (calcAmount.compareTo(accountAmount) != 0) {
                        // rounding error
                        LOG.debug("convertMoneyToPercent() Rounding error on " + account);
                        String param1 = identifier + "." + accountIdentifier;
                        String param2 = calcAmount.bigDecimalValue().subtract(accountAmount.bigDecimalValue()).toString();
                        GlobalVariables.getErrorMap().putError(item.getItemIdentifierString(), PurapKeyConstants.ERROR_ITEM_ACCOUNTING_ROUNDING, param1, param2);
                        account.setAmount(calcAmount);
                    }

                    // update percent
                    LOG.debug("convertMoneyToPercent() updating percent to " + tmpPercent);
                    account.setAccountLinePercent(tmpPercent.multiply(new BigDecimal(100)));

                    // check total based on adjusted amount
                    accountTotal = accountTotal.add(calcAmount);

                }
                if (!accountTotal.equals(item.getExtendedPrice())) {
                    GlobalVariables.getErrorMap().putError(item.getItemIdentifierString(), PurapKeyConstants.ERROR_ITEM_ACCOUNTING_DOLLAR_TOTAL, identifier, accountTotal.toString(), item.getExtendedPrice()+"");
                    LOG.debug("Invalid Totals");
                }
            }
        }
    }

    /**
     * Defaults the document description based on the credit memo source type.
     * 
     * @param cmDocument - Credit Memo Document to Populate
     */
    private void populateDocumentDescription(CreditMemoDocument cmDocument) {
        String description = "";
        if (cmDocument.isSourceVendor()) {
            description = "Vendor: " + cmDocument.getVendorName();
        }
        else {
            description = "PO: " + cmDocument.getPurchaseOrderDocument().getPurapDocumentIdentifier() + " Vendor: " + cmDocument.getVendorName();
        }

        // trim description if longer than whats specified in the data dictionary
        int noteTextMaxLength = dataDictionaryService.getAttributeMaxLength(DocumentHeader.class, KFSPropertyConstants.FINANCIAL_DOCUMENT_DESCRIPTION).intValue();
        if (noteTextMaxLength < description.length()) {
            description = description.substring(0, noteTextMaxLength);
        }

        cmDocument.getDocumentHeader().setFinancialDocumentDescription(description);
    }

    public void setCreditMemoService(CreditMemoService creditMemoService) {
        this.creditMemoService = creditMemoService;
    }

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public void setAccountsPayableService(AccountsPayableService accountsPayableService) {
        this.accountsPayableService = accountsPayableService;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public void setPaymentRequestService(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
    
    
}
