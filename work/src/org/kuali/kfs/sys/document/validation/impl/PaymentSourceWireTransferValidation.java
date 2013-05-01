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
