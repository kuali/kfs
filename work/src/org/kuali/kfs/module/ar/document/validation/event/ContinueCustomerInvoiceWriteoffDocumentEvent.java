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
