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
import org.kuali.kfs.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class PaymentRequestInvoiceValidation extends GenericValidation {

    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        PaymentRequestDocument document = (PaymentRequestDocument)event.getDocument();
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        
        if (ObjectUtils.isNull(document.getPurchaseOrderIdentifier())) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, KFSKeyConstants.ERROR_REQUIRED, PREQDocumentsStrings.PURCHASE_ORDER_ID);
            valid &= false;
        }
        if (ObjectUtils.isNull(document.getInvoiceDate())) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.INVOICE_DATE, KFSKeyConstants.ERROR_REQUIRED, PREQDocumentsStrings.INVOICE_DATE);
            valid &= false;
        }
        if (StringUtils.isBlank(document.getInvoiceNumber())) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.INVOICE_NUMBER, KFSKeyConstants.ERROR_REQUIRED, PREQDocumentsStrings.INVOICE_NUMBER);
            valid &= false;
        }
        if (ObjectUtils.isNull(document.getVendorInvoiceAmount())) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.VENDOR_INVOICE_AMOUNT, KFSKeyConstants.ERROR_REQUIRED, PREQDocumentsStrings.VENDOR_INVOICE_AMOUNT);
            valid &= false;
        }
        GlobalVariables.getMessageMap().clearErrorPath();
        return valid;
    }

}
