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

import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class PaymentRequestCancelValidation extends GenericValidation {

    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        PaymentRequestDocument preq = (PaymentRequestDocument) event.getDocument();
        
        // no errors for now since we are not showing the button if they can't cancel, if that changes we need errors
        // also this is different than CreditMemo even though the rules are almost identical we should merge and have one consistent
        // way to do this
//TODO hjs-cleanup; don't think we need this as presentation controller takes care of everything
//        PaymentRequestDocumentActionAuthorizer preqAuth = new PaymentRequestDocumentActionAuthorizer(preq, null);
//        valid = valid &= preqAuth.canCancel();
        
        return valid;
    }

}
