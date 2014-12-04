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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * A validation that checks whether the given accounting line is accessible to the given user or not
 */
public class PurchaseOrderAddQuoteToVendorValidation extends GenericValidation {

    private PurchaseOrderDocument accountingDocumentForValidation;
    private PurchaseOrderVendorQuote vendorQuote;
    private VendorService vendorService;
    
    /**
     * Applies rules for validation of the Split of PO and PO child documents
     * 
     * @param document  A PurchaseOrderDocument (or one of its children)
     * @return      True if all relevant validation rules are passed.
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        valid &= isVendorQuoteActiveNotDebarredVendor(vendorQuote.getVendorHeaderGeneratedIdentifier(), vendorQuote.getVendorDetailAssignedIdentifier());
        valid &= vendorQuoteHasRequiredFields(vendorQuote);
        return valid;
    }

    protected boolean isVendorQuoteActiveNotDebarredVendor(Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifer) {
        VendorDetail vendorDetail = vendorService.getVendorDetail(vendorHeaderGeneratedIdentifier, vendorDetailAssignedIdentifer);    
        if (vendorDetail != null) {
            if (!vendorDetail.isActiveIndicator()) {
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.NEW_PURCHASE_ORDER_VENDOR_QUOTE_VENDOR_NAME, PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_INACTIVE_VENDOR);
                return false;
            }
            else if (vendorDetail.isVendorDebarred()) {
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.NEW_PURCHASE_ORDER_VENDOR_QUOTE_VENDOR_NAME, PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_DEBARRED_VENDOR);
                return false;
            }
        }
        return true;
    }
    
    protected boolean vendorQuoteHasRequiredFields (PurchaseOrderVendorQuote vendorQuote) {
        boolean valid = true;
        if ( StringUtils.isBlank(vendorQuote.getVendorName()) ) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.NEW_PURCHASE_ORDER_VENDOR_QUOTE_VENDOR_NAME, KFSKeyConstants.ERROR_REQUIRED, "Vendor Name");
            valid = false;
        }
        if (StringUtils.isBlank(vendorQuote.getVendorLine1Address())) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.NEW_PURCHASE_ORDER_VENDOR_QUOTE_VENDOR_LINE_1_ADDR, KFSKeyConstants.ERROR_REQUIRED, "Vendor Line 1 Address");
            valid = false;
        }
        if (StringUtils.isBlank(vendorQuote.getVendorCityName())) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.NEW_PURCHASE_ORDER_VENDOR_QUOTE_VENDOR_CITY_NAME, KFSKeyConstants.ERROR_REQUIRED, "Vendor City Name");
            valid = false;
        }
        return valid;
    }

    public PurchaseOrderDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    public void setAccountingDocumentForValidation(PurchaseOrderDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    public PurchaseOrderVendorQuote getVendorQuote() {
        return vendorQuote;
    }

    public void setVendorQuote(PurchaseOrderVendorQuote vendorQuote) {
        this.vendorQuote = vendorQuote;
    }

    public VendorService getVendorService() {
        return vendorService;
    }

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }
    
}

