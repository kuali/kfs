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

import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.document.validation.RecalculateCustomerCreditMemoDetailRule;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.TransactionalDocument;
import org.kuali.rice.krad.rules.rule.BusinessRule;

public class RecalculateCustomerCreditMemoDetailEvent extends CustomerCreditMemoDetailEventBase {
    
    protected CustomerCreditMemoDetail customerCreditMemoDetail;

    public RecalculateCustomerCreditMemoDetailEvent(String errorPathPrefix, Document document, CustomerCreditMemoDetail customerCreditMemoDetail) {
        super("Recalculating customer credit memo detail for document " + getDocumentId(document), errorPathPrefix, document, customerCreditMemoDetail);
        this.customerCreditMemoDetail = customerCreditMemoDetail;
    }
    
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return RecalculateCustomerCreditMemoDetailRule.class;
    }
    
    @SuppressWarnings("unchecked")
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((RecalculateCustomerCreditMemoDetailRule) rule).processRecalculateCustomerCreditMemoDetailRules((TransactionalDocument)document, customerCreditMemoDetail);
    }
}
