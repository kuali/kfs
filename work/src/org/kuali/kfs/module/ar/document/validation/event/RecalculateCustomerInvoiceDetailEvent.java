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
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.rule.RecalculateCustomerInvoiceDetailRule;

public class RecalculateCustomerInvoiceDetailEvent extends CustomerInvoiceDetailEventBase implements CustomerInvoiceDetailEvent {

    public RecalculateCustomerInvoiceDetailEvent(String errorPathPrefix, Document document, CustomerInvoiceDetail customerInvoiceDetail) {
        super("Recalculating customer invoice detail for document " + getDocumentId(document), errorPathPrefix, document, customerInvoiceDetail);
        // TODO Auto-generated constructor stub
    }

    public Class getRuleInterfaceClass() {
        return RecalculateCustomerInvoiceDetailRule.class;
    }

    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((RecalculateCustomerInvoiceDetailRule) rule).processRecalculateCustomerInvoiceDetailRules((AccountingDocument)getDocument(), getCustomerInvoiceDetail());
    }

}
