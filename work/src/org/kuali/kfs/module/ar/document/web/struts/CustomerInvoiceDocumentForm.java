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
package org.kuali.module.ar.web.struts.form;

import java.sql.Date;
import java.text.ParseException;
import java.util.Calendar;

import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.lookup.valuefinder.ValueFinderUtil;

public class CustomerInvoiceDocumentForm extends KualiAccountingDocumentFormBase {
    private CustomerInvoiceDetail newCustomerInvoiceDetail; 
    
    public CustomerInvoiceDocumentForm() {
        super();
        setDocument(new CustomerInvoiceDocument());
        setNewCustomerInvoiceDetail(new CustomerInvoiceDetail());
    }

    public CustomerInvoiceDetail getNewCustomerInvoiceDetail() {
        return newCustomerInvoiceDetail;
    }

    public void setNewCustomerInvoiceDetail(CustomerInvoiceDetail newCustomerInvoiceDetail) {
        this.newCustomerInvoiceDetail = newCustomerInvoiceDetail;
    }
    
    public CustomerInvoiceDocument getCustomerInvoiceDocument( ) {
        return (CustomerInvoiceDocument)getDocument();
    }
}