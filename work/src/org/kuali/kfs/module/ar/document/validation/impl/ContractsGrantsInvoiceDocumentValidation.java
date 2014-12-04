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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedSaveDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Document level validation for Contracts and Grants Invoice Document.
 */
public class ContractsGrantsInvoiceDocumentValidation extends GenericValidation {

    private ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {

        boolean isValid = true;

        if (!hasEmailAddress(event) ) {
            isValid = false;
        }
        if (!hasTrasmissionCode(event) ) {
            isValid = false;
        }
        if (!hasTemplate(event) ) {
            isValid = false;
        }

        return isValid;
    }

    private boolean hasEmailAddress(AttributedDocumentEvent event) {
        boolean isValid = true;

        int i = 0;
        for (InvoiceAddressDetail address : contractsGrantsInvoiceDocument.getInvoiceAddressDetails()) {
            if (StringUtils.isNotBlank(address.getInvoiceTransmissionMethodCode()) && address.getInvoiceTransmissionMethodCode().equals(ArConstants.InvoiceTransmissionMethod.EMAIL) && StringUtils.isBlank(address.getCustomerEmailAddress()) ) {
                if (event instanceof AttributedSaveDocumentEvent) {
                    GlobalVariables.getMessageMap().putWarning("document.invoiceAddressDetails[" + i + "].customerEmailAddress", ArKeyConstants.ContractsGrantsInvoiceConstants.ERROR_EMAIL_ADDRESS_REQUIRED_FOR_TRANSMISSION_CODE);
                }
                else {
                    GlobalVariables.getMessageMap().putError("document.invoiceAddressDetails[" + i + "].customerEmailAddress", ArKeyConstants.ContractsGrantsInvoiceConstants.ERROR_EMAIL_ADDRESS_REQUIRED_FOR_TRANSMISSION_CODE);
                    GlobalVariables.getMessageMap().getWarningMessages().remove("document.invoiceAddressDetails[" + i + "].customerEmailAddress");

                    isValid = false;
                }

            }
            i++;
        }

        return isValid;
    }

    private boolean hasTrasmissionCode(AttributedDocumentEvent event) {

        boolean isValid = true;

        int i = 0;
        for (InvoiceAddressDetail address : contractsGrantsInvoiceDocument.getInvoiceAddressDetails()) {
            if ( address.getCustomerAddressTypeCode().equals(ArConstants.AGENCY_PRIMARY_ADDRESSES_TYPE_CODE) && StringUtils.isBlank(address.getInvoiceTransmissionMethodCode()) ) {
                if (event instanceof AttributedSaveDocumentEvent) {
                    GlobalVariables.getMessageMap().putWarning("document.invoiceAddressDetails[" + i + "].invoiceTransmissionMethodCode", ArKeyConstants.ContractsGrantsInvoiceConstants.ERROR_TRANSMISSION_CODE_REQUIRED);
                }
                else {
                    GlobalVariables.getMessageMap().putError("document.invoiceAddressDetails[" + i + "].invoiceTransmissionMethodCode", ArKeyConstants.ContractsGrantsInvoiceConstants.ERROR_TRANSMISSION_CODE_REQUIRED);
                    GlobalVariables.getMessageMap().getWarningMessages().remove("document.invoiceAddressDetails[" + i + "].invoiceTransmissionMethodCode");

                    isValid = false;
                }
            }
            i++;
        }

        return isValid;
    }

    private boolean hasTemplate(AttributedDocumentEvent event) {

        boolean isValid = true;
        int i = 0;

        for (InvoiceAddressDetail address : contractsGrantsInvoiceDocument.getInvoiceAddressDetails()) {
            if ( address.getCustomerAddressTypeCode().equals(ArConstants.AGENCY_PRIMARY_ADDRESSES_TYPE_CODE) && StringUtils.isBlank(address.getCustomerInvoiceTemplateCode()) ) {
                if (event instanceof AttributedSaveDocumentEvent) {
                    GlobalVariables.getMessageMap().putWarning("document.invoiceAddressDetails[" + i + "].customerInvoiceTemplateCode", ArKeyConstants.ContractsGrantsInvoiceConstants.ERROR_TEMPLATE_REQUIRED);
                }
                else {
                    GlobalVariables.getMessageMap().putError("document.invoiceAddressDetails[" + i + "].customerInvoiceTemplateCode", ArKeyConstants.ContractsGrantsInvoiceConstants.ERROR_TEMPLATE_REQUIRED);
                    GlobalVariables.getMessageMap().getWarningMessages().remove("document.invoiceAddressDetails[" + i + "].customerInvoiceTemplateCode");

                    isValid = false;
                }
            }
            i++;
        }

        return isValid;
    }

    /**
     * Gets the contractsGrantsInvoiceDocument attribute.
     *
     * @return Returns the contractsGrantsInvoiceDocument.
     */
    public ContractsGrantsInvoiceDocument getContractsGrantsInvoiceDocument() {
        return contractsGrantsInvoiceDocument;
    }

    /**
     * Sets the contractsGrantsInvoiceDocument attribute value.
     *
     * @param contractsGrantsInvoiceDocument The contractsGrantsInvoiceDocument to set.
     */
    public void setContractsGrantsInvoiceDocument(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        this.contractsGrantsInvoiceDocument = contractsGrantsInvoiceDocument;
    }


}
