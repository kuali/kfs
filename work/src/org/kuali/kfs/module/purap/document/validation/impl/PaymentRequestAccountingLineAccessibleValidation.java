/*
 * Copyright 2012 The Kuali Foundation.
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

import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kew.api.WorkflowDocument;

/**
 * Overridden to force an account identifier (thus forcing the validation) into an accounting line when the accounting
 * line must be checked (basically, once the doc is in route)
 */
public class PaymentRequestAccountingLineAccessibleValidation extends PurchasingAccountsPayableAccountingLineAccessibleValidation {

    /**
     * Overridden to potentially set a dummy account identifier on the accounting line; this will force a KIM check
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingLineAccessibleValidation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = false;
        boolean setDummyAccountIdentifier = false;
        
        if (needsDummyAccountIdentifier()) {
            ((PurApAccountingLine)getAccountingLineForValidation()).setAccountIdentifier(Integer.MAX_VALUE);  // avoid conflicts with any accouting identifier on any other accounting lines in the doc because, you know, you never know...
            setDummyAccountIdentifier = true;
        }
        
        result = super.validate(event);
        
        if (setDummyAccountIdentifier) {
            ((PurApAccountingLine)getAccountingLineForValidation()).setAccountIdentifier(null);
        }
        
        return result;
    }

    /**
     * @return true if a dummy account identifier should be set on the accounting line, false otherwise
     */
    protected boolean needsDummyAccountIdentifier() {
        if (((PurApAccountingLine)getAccountingLineForValidation()).getAccountIdentifier() != null) {
            return false;
        }
        
        final WorkflowDocument workflowDocument = getAccountingDocumentForValidation().getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isInitiated() || workflowDocument.isSaved()) {
            return false;
        }
        
        return true;
    }
}
