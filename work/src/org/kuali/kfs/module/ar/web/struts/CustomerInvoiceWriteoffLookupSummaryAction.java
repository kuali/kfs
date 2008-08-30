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
package org.kuali.kfs.module.ar.web.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceWriteoffLookupResult;
import org.kuali.kfs.module.ar.businessobject.lookup.CustomerInvoiceWriteoffLookupUtil;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.lookup.LookupResultsService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.struts.action.KualiAction;

public class CustomerInvoiceWriteoffLookupSummaryAction extends KualiAction {

    private LookupResultsService lookupResultsService;

    public void setLookupResultsService(LookupResultsService lookupResultsService) {
        this.lookupResultsService = lookupResultsService;
    }

    public ActionForward viewSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerInvoiceWriteoffLookupSummaryForm customerInvoiceWriteoffLookupSummaryForm = (CustomerInvoiceWriteoffLookupSummaryForm) form;
        String lookupResultsSequenceNumber = customerInvoiceWriteoffLookupSummaryForm.getLookupResultsSequenceNumber();
        if (StringUtils.isNotBlank(lookupResultsSequenceNumber)) {
            String universalUserId = GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUniversalIdentifier();
            Collection<CustomerInvoiceWriteoffLookupResult> customerInvoiceWriteoffLookupResults = getCustomerInvoiceWriteoffResutlsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber,universalUserId);
            customerInvoiceWriteoffLookupSummaryForm.setCustomerInvoiceWriteoffLookupResults(customerInvoiceWriteoffLookupResults);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward createCustomerInvoiceWriteoffs(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    protected LookupResultsService getLookupResultsService() {
        if (ObjectUtils.isNull(lookupResultsService)) {
            lookupResultsService = SpringContext.getBean(LookupResultsService.class);
        }

        return lookupResultsService;
    }

    protected Collection<CustomerInvoiceDocument> getCustomerInvoiceDocumentsFromLookupResultsSequenceNumber(String lookupResultsSequenceNumber, String universalUserId) {
        Collection<CustomerInvoiceDocument> customerInvoiceDocuments = new ArrayList<CustomerInvoiceDocument>();
        try {
            for (PersistableBusinessObject obj : getLookupResultsService().retrieveSelectedResultBOs(lookupResultsSequenceNumber, CustomerInvoiceDocument.class, universalUserId)) {
                customerInvoiceDocuments.add((CustomerInvoiceDocument) obj);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return customerInvoiceDocuments;
    }
    
    protected Collection<CustomerInvoiceWriteoffLookupResult> getCustomerInvoiceWriteoffResutlsFromLookupResultsSequenceNumber(String lookupResultsSequenceNumber, String universalUserId){
        return CustomerInvoiceWriteoffLookupUtil.getPopulatedCustomerInvoiceWriteoffLookupResults(getCustomerInvoiceDocumentsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, universalUserId));
    }
}
