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
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.ar.bo.CustomerCreditMemoDetail;
import org.kuali.module.ar.rule.RecalculateCustomerCreditMemoDetailRule;

public class RecalculateCustomerCreditMemoDetailEvent extends CustomerCreditMemoDetailEventBase {

    public RecalculateCustomerCreditMemoDetailEvent(String errorPathPrefix, Document document, CustomerCreditMemoDetail customerCreditMemoDetail) {
        super("Recalculating customer credit memo detail for document " + getDocumentId(document), errorPathPrefix, document, customerCreditMemoDetail);
        // TODO Auto-generated constructor stub
    }
    
    public Class getRuleInterfaceClass() {
        return RecalculateCustomerCreditMemoDetailRule.class;
    }
    
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((RecalculateCustomerCreditMemoDetailRule) rule).processRecalculateCustomerCreditMemoDetailRules((AccountingDocument)getDocument(), getCustomerCreditMemoDetail());
    }
}