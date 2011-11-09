/*
 * Copyright 2008-2009 The Kuali Foundation
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

import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.CreditMemoService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class VendorCreditMemoCancelValidation extends GenericValidation {

    private CreditMemoService creditMemoService;
    
    public boolean validate(AttributedDocumentEvent event) {
        VendorCreditMemoDocument creditMemoDocument = (VendorCreditMemoDocument) event.getDocument();
        //TODO hjs- need to review, but shouldn't need to check cancel rules here
//        return creditMemoService.canCancelCreditMemo(creditMemoDocument, GlobalVariables.getUserSession().getPerson());
        return true;
    }

    public CreditMemoService getCreditMemoService() {
        return creditMemoService;
    }

    public void setCreditMemoService(CreditMemoService creditMemoService) {
        this.creditMemoService = creditMemoService;
    }

}
