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
