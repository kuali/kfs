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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceWriteoffLookupResult;
import org.kuali.kfs.module.ar.businessobject.lookup.CustomerInvoiceWriteoffLookupUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

public class CustomerInvoiceWriteoffLookupSummaryForm extends KualiForm {

    private String lookupResultsSequenceNumber;
    private Collection<CustomerInvoiceWriteoffLookupResult> customerInvoiceWriteoffLookupResults;
    private boolean sentToBatchInd;

    public CustomerInvoiceWriteoffLookupSummaryForm(){
        customerInvoiceWriteoffLookupResults = new ArrayList<CustomerInvoiceWriteoffLookupResult>();
        sentToBatchInd = false;
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

    public boolean isSentToBatchInd() {
        return sentToBatchInd;
    }

    public void setSentToBatchInd(boolean sentToBatchInd) {
        this.sentToBatchInd = sentToBatchInd;
    }
    @Override
    public void populate(HttpServletRequest request) {
        UserSession userSession = GlobalVariables.getUserSession();
        Person person = userSession.getPerson();
        String lookupResultsSequenceNumber =  (String) userSession.getObjectMap().get(KRADConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER);
        Map params = request.getParameterMap();
        if (!StringUtils.isEmpty(lookupResultsSequenceNumber)) {
            userSession.removeObject(KRADConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER);
            Collection<CustomerInvoiceWriteoffLookupResult> customerInvoiceWriteoffLookupResults = CustomerInvoiceWriteoffLookupUtil.getCustomerInvoiceWriteoffResutlsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber,person.getPrincipalId());
            this.setCustomerInvoiceWriteoffLookupResults(customerInvoiceWriteoffLookupResults);
            this.setLookupResultsSequenceNumber(lookupResultsSequenceNumber);

        }
        if (! WebUtils.parseMethodToCall(this, request).equals(KFSConstants.MAPPING_CANCEL)) {
            super.populate(request);
        }
     }
}
