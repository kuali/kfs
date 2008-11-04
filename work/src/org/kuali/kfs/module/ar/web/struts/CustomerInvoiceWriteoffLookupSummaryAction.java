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

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceWriteoffLookupResult;
import org.kuali.kfs.module.ar.businessobject.lookup.CustomerInvoiceWriteoffLookupUtil;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceWriteoffDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.service.SchedulerService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.struts.action.KualiAction;

public class CustomerInvoiceWriteoffLookupSummaryAction extends KualiAction {

    public ActionForward viewSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerInvoiceWriteoffLookupSummaryForm customerInvoiceWriteoffLookupSummaryForm = (CustomerInvoiceWriteoffLookupSummaryForm) form;
        String lookupResultsSequenceNumber = customerInvoiceWriteoffLookupSummaryForm.getLookupResultsSequenceNumber();
        if (StringUtils.isNotBlank(lookupResultsSequenceNumber)) {
            String personId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
            Collection<CustomerInvoiceWriteoffLookupResult> customerInvoiceWriteoffLookupResults = CustomerInvoiceWriteoffLookupUtil.getCustomerInvoiceWriteoffResutlsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber,personId);
            customerInvoiceWriteoffLookupSummaryForm.setCustomerInvoiceWriteoffLookupResults(customerInvoiceWriteoffLookupResults);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward createCustomerInvoiceWriteoffs(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        CustomerInvoiceWriteoffLookupSummaryForm customerInvoiceWriteoffLookupSummaryForm = (CustomerInvoiceWriteoffLookupSummaryForm) form;        

        Person person = GlobalVariables.getUserSession().getPerson();

        CustomerInvoiceWriteoffDocumentService service = SpringContext.getBean(CustomerInvoiceWriteoffDocumentService.class);
        Collection<CustomerInvoiceWriteoffLookupResult> lookupResults = customerInvoiceWriteoffLookupSummaryForm.getCustomerInvoiceWriteoffLookupResults();

        //TODO Need to check every invoiceNumber submitted and make sure that:
        //      1. Invoice exists in the system.
        //      2. Invoice doesnt already have a writeoff in progress, either in route or final.
        
        //  make sure no null/blank invoiceNumbers get sent
        boolean anyFound = false;
        for( CustomerInvoiceWriteoffLookupResult customerInvoiceWriteoffLookupResult : lookupResults ){
            for (CustomerInvoiceDocument invoiceDocument : customerInvoiceWriteoffLookupResult.getCustomerInvoiceDocuments()) {
                if (StringUtils.isNotBlank(invoiceDocument.getDocumentNumber())) {
                    anyFound = true;
                }
            }
        }
        
        //  only submit this if there's at least one invoiceNumber in the stack
        if (!anyFound) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, 
                    KFSKeyConstants.ERROR_BATCH_UPLOAD_SAVE, "No invoices were selected to send to writeoff batch.  Please select at least one.");
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        //  send the batch file off
        String filename = service.sendCustomerInvoiceWriteoffDocumentsToBatch(person, lookupResults);
        
        //  manually fire off the batch job
        SchedulerService schedulerService = SpringContext.getBean(SchedulerService.class);
        schedulerService.runJob("customerInvoiceWriteoffBatchJob", person.getEmailAddress());
        
        GlobalVariables.getMessageList().add("Invoice Writeoff batch successfully sent.");
        return mapping.findForward(KFSConstants.MAPPING_CANCEL);
    }
    
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_CANCEL);
    }        
}

