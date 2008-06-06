/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.ar.rule.event;

import org.kuali.core.document.Document;
import org.kuali.core.rule.BusinessRule;
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.ar.rule.ContinueCustomerCreditMemoDocumentRule;

public class ContinueCustomerCreditMemoDocumentEvent extends KualiDocumentEventBase {
    
    public ContinueCustomerCreditMemoDocumentEvent(String errorPathPrefix, Document document) {
        super("Continue customer credit memo document " + getDocumentId(document), errorPathPrefix, document);
    }

    public Class getRuleInterfaceClass() {
        return ContinueCustomerCreditMemoDocumentRule.class;
    }

    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((ContinueCustomerCreditMemoDocumentRule) rule).processContinueCustomerCreditMemoDocumentRules((AccountingDocument)getDocument());
    }

}
