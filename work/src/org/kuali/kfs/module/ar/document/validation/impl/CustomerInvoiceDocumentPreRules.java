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
package org.kuali.kfs.module.ar.document.validation.impl;

import java.util.Iterator;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.krad.document.Document;

public class CustomerInvoiceDocumentPreRules extends PromptBeforeValidationBase {

    /**
     * @see org.kuali.rice.kns.rules.PromptBeforeValidationBase#doRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean doPrompts(Document document) {

        CustomerInvoiceDocument doc = (CustomerInvoiceDocument)document;
        CustomerInvoiceDetailService service = SpringContext.getBean(CustomerInvoiceDetailService.class);
        updateCustomerInvoiceDetails(service, doc);
        
        return true;
    }

    /**
     * @param service
     * @param document
     */
    @SuppressWarnings("unchecked")
    protected void updateCustomerInvoiceDetails(CustomerInvoiceDetailService service, CustomerInvoiceDocument document){
        CustomerInvoiceDetail customerInvoiceDetail;
        for( Iterator i = document.getSourceAccountingLines().iterator(); i.hasNext(); ){
            customerInvoiceDetail = (CustomerInvoiceDetail)i.next();
            service.recalculateCustomerInvoiceDetail(document, customerInvoiceDetail);
            service.updateAccountsForCorrespondingDiscount(customerInvoiceDetail);
        }
    }
}
