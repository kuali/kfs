/*
 * Copyright 2008 The Kuali Foundation
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
