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
package org.kuali.kfs.sys.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.PaymentSourceWireTransfer;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.PaymentSource;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

public class PaymentSourceWireTransferValidation extends GenericValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentSourceWireTransferValidation.class);

    private AccountingDocument accountingDocumentForValidation;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        LOG.debug("validate start");
        boolean isValid = true;

        PaymentSource document = (PaymentSource) accountingDocumentForValidation;
        PaymentSourceWireTransfer wireTransfer = document.getWireTransfer();

        if (!KFSConstants.PaymentSourceConstants.PAYMENT_METHOD_WIRE.equals(document.getPaymentMethodCode())) {
            return isValid;
        }

        MessageMap errors = GlobalVariables.getMessageMap();
        errors.addToErrorPath(KFSPropertyConstants.DOCUMENT);
        errors.addToErrorPath(KFSPropertyConstants.PAYMENT_SOURCE_WIRE_TRANSFER);

        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(wireTransfer);

        if (KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(wireTransfer.getBankCountryCode()) && StringUtils.isBlank(wireTransfer.getBankRoutingNumber())) {
            errors.putError(KFSPropertyConstants.BANK_ROUTING_NUMBER, KFSKeyConstants.ERROR_PAYMENT_SOURCE_BANK_ROUTING_NUMBER);
            isValid = false;
        }

        if (KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(wireTransfer.getBankCountryCode()) && StringUtils.isBlank(wireTransfer.getBankStateCode())) {
            errors.putError(KFSPropertyConstants.BANK_STATE_CODE, KFSKeyConstants.ERROR_REQUIRED, "Bank State");
            isValid = false;
        }

        /* cannot have attachment checked for wire transfer */
        if (document.hasAttachment()) {
            errors.putErrorWithoutFullErrorPath(KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.DISB_VCHR_ATTACHMENT_CODE, KFSKeyConstants.ERROR_PAYMENT_SOURCE_WIRE_ATTACHMENT);
            isValid = false;
        }

        errors.removeFromErrorPath(KFSPropertyConstants.PAYMENT_SOURCE_WIRE_TRANSFER);
        errors.removeFromErrorPath(KFSPropertyConstants.DOCUMENT);

        return isValid;
    }

    /**
     * Gets the accountingDocumentForValidation attribute.
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     *
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
}
