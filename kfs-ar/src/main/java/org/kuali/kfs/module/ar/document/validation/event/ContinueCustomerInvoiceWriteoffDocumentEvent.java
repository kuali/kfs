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
package org.kuali.kfs.module.ar.document.validation.event;

import org.kuali.kfs.module.ar.document.validation.ContinueCustomerInvoiceWriteoffDocumentRule;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.TransactionalDocument;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEventBase;

public class ContinueCustomerInvoiceWriteoffDocumentEvent extends KualiDocumentEventBase {

    public ContinueCustomerInvoiceWriteoffDocumentEvent(String errorPathPrefix, Document document) {
        super("Continue customer invoice writeoff document " + getDocumentId(document), errorPathPrefix, document);
    }
    
    public Class getRuleInterfaceClass() {
        return ContinueCustomerInvoiceWriteoffDocumentRule.class;
    }

    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((ContinueCustomerInvoiceWriteoffDocumentRule) rule).processContinueCustomerInvoiceWriteoffDocumentRules((TransactionalDocument)getDocument());
    }
}
