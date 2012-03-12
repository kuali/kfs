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
package org.kuali.kfs.fp.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherWireTransfer;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

public class DisbursementVoucherWireTransferValidation extends GenericValidation implements DisbursementVoucherConstants {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherWireTransferValidation.class);

    private AccountingDocument accountingDocumentForValidation;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        LOG.debug("validate start");
        boolean isValid = true;
        
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) accountingDocumentForValidation;
        DisbursementVoucherPayeeDetail payeeDetail = document.getDvPayeeDetail();
        DisbursementVoucherWireTransfer wireTransfer = document.getDvWireTransfer();

        if (!PAYMENT_METHOD_WIRE.equals(document.getDisbVchrPaymentMethodCode())) {
            return isValid;
        }

        MessageMap errors = GlobalVariables.getMessageMap(); 
        errors.addToErrorPath(KFSPropertyConstants.DOCUMENT);
        errors.addToErrorPath(KFSPropertyConstants.DV_WIRE_TRANSFER);
        
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(wireTransfer);

        if (KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(wireTransfer.getDisbVchrBankCountryCode()) && StringUtils.isBlank(wireTransfer.getDisbVchrBankRoutingNumber())) {
            errors.putError(KFSPropertyConstants.DISB_VCHR_BANK_ROUTING_NUMBER, KFSKeyConstants.ERROR_DV_BANK_ROUTING_NUMBER);
            isValid = false;
        }

        if (KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(wireTransfer.getDisbVchrBankCountryCode()) && StringUtils.isBlank(wireTransfer.getDisbVchrBankStateCode())) {
            errors.putError(KFSPropertyConstants.DISB_VCHR_BANK_STATE_CODE, KFSKeyConstants.ERROR_REQUIRED, "Bank State");
            isValid = false;
        }

        /* cannot have attachment checked for wire transfer */
        if (document.isDisbVchrAttachmentCode()) {
            errors.putErrorWithoutFullErrorPath(KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.DISB_VCHR_ATTACHMENT_CODE, KFSKeyConstants.ERROR_DV_WIRE_ATTACHMENT);
            isValid = false;
        }

        errors.removeFromErrorPath(KFSPropertyConstants.DV_WIRE_TRANSFER);
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
