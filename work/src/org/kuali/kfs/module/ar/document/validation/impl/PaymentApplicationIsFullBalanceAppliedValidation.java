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

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.MessageMap;

public class PaymentApplicationIsFullBalanceAppliedValidation extends GenericValidation {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentApplicationIsFullBalanceAppliedValidation.class);
    private PaymentApplicationDocument document;
    
    public boolean validate(AttributedDocumentEvent event) {
        
        boolean isValid = true;
        MessageMap errorMap = GlobalVariables.getMessageMap();
        PaymentApplicationDocument paymentApplicationDocument = getDocument();

        //  dont let PayApp docs started from CashControl docs through if not all funds are applied
        if (paymentApplicationDocument.hasCashControlDetail()) {
            KualiDecimal balanceToBeApplied;
            balanceToBeApplied = paymentApplicationDocument.getUnallocatedBalance();
            if (!KualiDecimal.ZERO.equals(balanceToBeApplied)) {
                isValid &= false;
                errorMap.putError(
                    KRADConstants.GLOBAL_ERRORS,
                    ArKeyConstants.PaymentApplicationDocumentErrors.FULL_AMOUNT_NOT_APPLIED);
            }
        }
        
        return isValid;
    }

    public PaymentApplicationDocument getDocument() {
        return document;
    }

    public void setDocument(PaymentApplicationDocument document) {
        this.document = document;
    }

}
