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
package org.kuali.kfs.module.ar.web.struts;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceWriteoffLookupResult;
import java.util.ArrayList;
import org.kuali.rice.kns.web.struts.form.KualiForm;

public class CustomerInvoiceWriteoffLookupSummaryForm extends KualiForm {
    
    private String lookupResultsSequenceNumber;
    private Collection<CustomerInvoiceWriteoffLookupResult> customerInvoiceWriteoffLookupResults;
    private boolean sentToBatch;
    
    public CustomerInvoiceWriteoffLookupSummaryForm(){
        customerInvoiceWriteoffLookupResults = new ArrayList<CustomerInvoiceWriteoffLookupResult>();
        sentToBatch = false;
    }

    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    public Collection<CustomerInvoiceWriteoffLookupResult> getCustomerInvoiceWriteoffLookupResults() {
        return customerInvoiceWriteoffLookupResults;
    }

    public void setCustomerInvoiceWriteoffLookupResults(Collection<CustomerInvoiceWriteoffLookupResult> customerInvoiceWriteoffLookupResults) {
        this.customerInvoiceWriteoffLookupResults = customerInvoiceWriteoffLookupResults;
    }

    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultsSequenceNumber;
    } 
    
    public CustomerInvoiceWriteoffLookupResult getCustomerInvoiceWriteoffLookupResult(int index){
        CustomerInvoiceWriteoffLookupResult customerInvoiceWriteoffLookupResult = ((List<CustomerInvoiceWriteoffLookupResult>)getCustomerInvoiceWriteoffLookupResults()).get(index);
        return customerInvoiceWriteoffLookupResult;
    }

    public boolean isSentToBatch() {
        return sentToBatch;
    }

    public void setSentToBatch(boolean sentToBatch) {
        this.sentToBatch = sentToBatch;
    }

}
