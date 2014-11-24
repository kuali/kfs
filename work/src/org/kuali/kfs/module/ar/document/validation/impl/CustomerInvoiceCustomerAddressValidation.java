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
package org.kuali.kfs.module.ar.document.validation.impl;

import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerInvoiceCustomerAddressValidation extends GenericValidation {

    private CustomerInvoiceDocument customerInvoiceDocument;
    private CustomerAddressService customerAddressService;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean success = true;
        String customerNumber = customerInvoiceDocument.getAccountsReceivableDocumentHeader().getCustomerNumber();

        if (ObjectUtils.isNotNull(customerInvoiceDocument.getCustomerShipToAddressIdentifier())) {
            success &= isCustomerAddressValid(customerNumber, customerInvoiceDocument.getCustomerShipToAddressIdentifier(), true);
            success &= isCustomerAddressActive(customerNumber, customerInvoiceDocument.getCustomerShipToAddressIdentifier(), true);
        }
        if (ObjectUtils.isNotNull(customerInvoiceDocument.getCustomerBillToAddressIdentifier())) {
            success &= isCustomerAddressValid(customerNumber, customerInvoiceDocument.getCustomerBillToAddressIdentifier(), false);
            success &= isCustomerAddressActive(customerNumber, customerInvoiceDocument.getCustomerBillToAddressIdentifier(), false);
        }

        return success;
    }
    
    /**
     * This method validates if a customer address is valid
     * 
     * @param customerInvoiceDocument
     * @param isShipToAddress
     * @return
     */
    protected boolean isCustomerAddressValid(String customerNumber, Integer customerAddressIdentifier, boolean isShipToAddress) {

        if (!customerAddressService.customerAddressExists(customerNumber, customerAddressIdentifier)) {
            if (isShipToAddress) {
                GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceDocumentFields.SHIP_TO_ADDRESS_IDENTIFIER, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_SHIP_TO_ADDRESS_IDENTIFIER);
            }
            else {
                GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceDocumentFields.BILL_TO_ADDRESS_IDENTIFIER, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_BILL_TO_ADDRESS_IDENTIFIER);
            }
            return false;

        }
        return true;
    }    
    
    /**
     * This method validates if a customer address is active
     * 
     * @param customerInvoiceDocument
     * @param isShipToAddress
     * @return
     */
    protected boolean isCustomerAddressActive(String customerNumber, Integer customerAddressIdentifier, boolean isShipToAddress) {

        if (!customerAddressService.customerAddressActive(customerNumber, customerAddressIdentifier)) {
            if (isShipToAddress) {
                GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceDocumentFields.SHIP_TO_ADDRESS_IDENTIFIER, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INACTIVE_SHIP_TO_ADDRESS_IDENTIFIER);
            }
            else {
                GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceDocumentFields.BILL_TO_ADDRESS_IDENTIFIER, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INACTIVE_BILL_TO_ADDRESS_IDENTIFIER);
            }
            return false;
        }
        return true;
    }    
    
    public CustomerAddressService getCustomerAddressService() {
        return customerAddressService;
    }

    public void setCustomerAddressService(CustomerAddressService customerAddressService) {
        this.customerAddressService = customerAddressService;
    }    
    
    public CustomerInvoiceDocument getCustomerInvoiceDocument() {
        return customerInvoiceDocument;
    }

    public void setCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        this.customerInvoiceDocument = customerInvoiceDocument;
    }    

}
