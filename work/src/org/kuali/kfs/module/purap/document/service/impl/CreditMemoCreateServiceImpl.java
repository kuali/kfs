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
package org.kuali.kfs.module.purap.document.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccount;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.AccountsPayableService;
import org.kuali.kfs.module.purap.document.service.CreditMemoCreateService;
import org.kuali.kfs.module.purap.document.service.CreditMemoService;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.util.ExpiredOrClosedAccountEntry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.VendorUtils;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.KualiDecimal;


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
    private PurapAccountingService purapAccountingService;
    
    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoCreateService#populateDocumentAfterInit(org.kuali.kfs.module.purap.document.CreditMemoDocument)
     */
    public void populateDocumentAfterInit(VendorCreditMemoDocument cmDocument) {

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
    protected void populateDocumentFromPreq(VendorCreditMemoDocument cmDocument, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        PaymentRequestDocument paymentRequestDocument = paymentRequestService.getPaymentRequestById(cmDocument.getPaymentRequestIdentifier());
        cmDocument.getDocumentHeader().setOrganizationDocumentNumber(paymentRequestDocument.getDocumentHeader().getOrganizationDocumentNumber());
        cmDocument.setPaymentRequestDocument(paymentRequestDocument);
        cmDocument.setPurchaseOrderDocument(paymentRequestDocument.getPurchaseOrderDocument());
        cmDocument.setUseTaxIndicator(paymentRequestDocument.isUseTaxIndicator());
        
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
        cmDocument.setVendorAttentionName(paymentRequestDocument.getVendorAttentionName());
        cmDocument.setAccountsPayablePurchasingDocumentLinkIdentifier(paymentRequestDocument.getAccountsPayablePurchasingDocumentLinkIdentifier());

        // prep the item lines (also collect warnings for later display) this is only done on paymentRequest
        purapAccountingService.convertMoneyToPercent(paymentRequestDocument);
        populateItemLinesFromPreq(cmDocument, expiredOrClosedAccountList);
    }

    /**
     * Populates the credit memo items from the payment request items.
     * 
     * @param cmDocument - Credit Memo Document to Populate
     */
    protected void populateItemLinesFromPreq(VendorCreditMemoDocument cmDocument, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        PaymentRequestDocument preqDocument = cmDocument.getPaymentRequestDocument();

        for (PaymentRequestItem preqItemToTemplate : (List<PaymentRequestItem>) preqDocument.getItems()) {
            if (preqItemToTemplate.getItemType().isLineItemIndicator()) {
                cmDocument.getItems().add(new CreditMemoItem(cmDocument, preqItemToTemplate, preqItemToTemplate.getPurchaseOrderItem(), expiredOrClosedAccountList));
            }
        }

        // add below the line items
        purapService.addBelowLineItems(cmDocument);
        
        cmDocument.fixItemReferences();
    }

    /**
     * Populate Credit Memo of type Purchase Order.
     * 
     * @param cmDocument - Credit Memo Document to Populate
     */
    protected void populateDocumentFromPO(VendorCreditMemoDocument cmDocument, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        PurchaseOrderDocument purchaseOrderDocument = purchaseOrderService.getCurrentPurchaseOrder(cmDocument.getPurchaseOrderIdentifier());
        cmDocument.setPurchaseOrderDocument(purchaseOrderDocument);
        cmDocument.getDocumentHeader().setOrganizationDocumentNumber(purchaseOrderDocument.getDocumentHeader().getOrganizationDocumentNumber());
        cmDocument.setUseTaxIndicator(cmDocument.isUseTaxIndicator());
        
        cmDocument.setVendorHeaderGeneratedIdentifier(purchaseOrderDocument.getVendorHeaderGeneratedIdentifier());
        cmDocument.setVendorDetailAssignedIdentifier(purchaseOrderDocument.getVendorDetailAssignedIdentifier());
        cmDocument.setVendorCustomerNumber(purchaseOrderDocument.getVendorCustomerNumber());
        cmDocument.setVendorName(purchaseOrderDocument.getVendorName());
        cmDocument.setAccountsPayablePurchasingDocumentLinkIdentifier(purchaseOrderDocument.getAccountsPayablePurchasingDocumentLinkIdentifier());

        // populate cm vendor address with the default remit address type for the vendor if found
        String userCampus = GlobalVariables.getUserSession().getPerson().getCampusCode();
        VendorAddress vendorAddress = vendorService.getVendorDefaultAddress(purchaseOrderDocument.getVendorHeaderGeneratedIdentifier(), purchaseOrderDocument.getVendorDetailAssignedIdentifier(), VendorConstants.AddressTypes.REMIT, userCampus);
        if (vendorAddress != null) {
            cmDocument.templateVendorAddress(vendorAddress);
            cmDocument.setVendorAddressGeneratedIdentifier(vendorAddress.getVendorAddressGeneratedIdentifier());
            cmDocument.setVendorAttentionName(StringUtils.defaultString(vendorAddress.getVendorAttentionName()));
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
            
            boolean blankAttentionLine = StringUtils.equalsIgnoreCase("Y",SpringContext.getBean(KualiConfigurationService.class).getParameterValue("KFS-PURAP", "Document", PurapParameterConstants.BLANK_ATTENTION_LINE_FOR_PO_TYPE_ADDRESS));
            if (blankAttentionLine){
                cmDocument.setVendorAttentionName(StringUtils.EMPTY);
            }else{
                cmDocument.setVendorAttentionName(StringUtils.defaultString(purchaseOrderDocument.getVendorAttentionName()));
            }
        }

        populateItemLinesFromPO(cmDocument, expiredOrClosedAccountList);
    }

    /**
     * Populates the credit memo items from the payment request items.
     * 
     * @param cmDocument - Credit Memo Document to Populate
     */
    protected void populateItemLinesFromPO(VendorCreditMemoDocument cmDocument, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        List<PurchaseOrderItem> invoicedItems = creditMemoService.getPOInvoicedItems(cmDocument.getPurchaseOrderDocument());
        for (PurchaseOrderItem poItem : invoicedItems) {
            CreditMemoItem creditMemoItem = new CreditMemoItem(cmDocument, poItem, expiredOrClosedAccountList);
            cmDocument.getItems().add(creditMemoItem);
            PurchasingCapitalAssetItem purchasingCAMSItem = cmDocument.getPurchaseOrderDocument().getPurchasingCapitalAssetItemByItemIdentifier(poItem.getItemIdentifier());
            if(purchasingCAMSItem!=null) {
                creditMemoItem.setCapitalAssetTransactionTypeCode(purchasingCAMSItem.getCapitalAssetTransactionTypeCode());
            } 
            
        }

        // add below the line items
        purapService.addBelowLineItems(cmDocument);
        
        cmDocument.fixItemReferences();
    }

    /**
     * Populate Credit Memo of type Vendor.
     * 
     * @param cmDocument - Credit Memo Document to Populate
     */
    protected void populateDocumentFromVendor(VendorCreditMemoDocument cmDocument) {
        Integer vendorHeaderId = VendorUtils.getVendorHeaderId(cmDocument.getVendorNumber());
        Integer vendorDetailId = VendorUtils.getVendorDetailId(cmDocument.getVendorNumber());

        VendorDetail vendorDetail = vendorService.getVendorDetail(vendorHeaderId, vendorDetailId);
        cmDocument.setVendorDetail(vendorDetail);
        
        cmDocument.setVendorHeaderGeneratedIdentifier(vendorDetail.getVendorHeaderGeneratedIdentifier());
        cmDocument.setVendorDetailAssignedIdentifier(vendorDetail.getVendorDetailAssignedIdentifier());
        cmDocument.setVendorCustomerNumber(vendorDetail.getVendorNumber());
        cmDocument.setVendorName(vendorDetail.getVendorName());


        // credit memo type vendor uses the default remit type address for the vendor if found
        String userCampus = GlobalVariables.getUserSession().getPerson().getCampusCode();
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
     * Defaults the document description based on the credit memo source type.
     * 
     * @param cmDocument - Credit Memo Document to Populate
     */
    private void populateDocumentDescription(VendorCreditMemoDocument cmDocument) {
        String description = "";
        if (cmDocument.isSourceVendor()) {
            description = "Vendor: " + cmDocument.getVendorName();
        }
        else {
            description = "PO: " + cmDocument.getPurchaseOrderDocument().getPurapDocumentIdentifier() + " Vendor: " + cmDocument.getVendorName();
        }

        // trim description if longer than whats specified in the data dictionary
        int noteTextMaxLength = dataDictionaryService.getAttributeMaxLength(DocumentHeader.class, KNSPropertyConstants.DOCUMENT_DESCRIPTION).intValue();
        if (noteTextMaxLength < description.length()) {
            description = description.substring(0, noteTextMaxLength);
        }

        cmDocument.getDocumentHeader().setDocumentDescription(description);
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

    public void setPurapAccountingService(PurapAccountingService purapAccountingService) {
        this.purapAccountingService = purapAccountingService;
    }
    
    
}

