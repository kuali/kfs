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
}
