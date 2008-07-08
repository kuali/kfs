/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.web.struts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase;

public class PaymentApplicationDocumentAction extends KualiAccountingDocumentActionBase {
    
    private static org.apache.log4j.Logger LOG = 
        org.apache.log4j.Logger.getLogger(PaymentApplicationDocumentAction.class);
    
    private DocumentService documentService;
    private DocumentTypeService documentTypeService;
    private WorkflowDocumentService workflowDocumentService;
    
    public PaymentApplicationDocumentAction() {
        super();
        documentService = SpringContext.getBean(DocumentService.class);
        documentTypeService = SpringContext.getBean(DocumentTypeService.class);
        workflowDocumentService = SpringContext.getBean(WorkflowDocumentService.class);
    }
    
    public ActionForward apply(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean trap = true;
        Object applyToInvoices = request.getParameter("quickApply");
        List<String> invoiceNumbers = new ArrayList<String>();
        if(!applyToInvoices.getClass().isArray()) {
            invoiceNumbers.add((String)applyToInvoices);
        } else {
            invoiceNumbers.addAll(Arrays.asList((String[])applyToInvoices));
        }
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    public ActionForward quickApply(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean trap = true;
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO Auto-generated method stub
        return super.execute(mapping, form, request, response);
    }

    public ActionForward goToInvoice(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentApplicationDocumentForm pform = (PaymentApplicationDocumentForm) form;
        pform.setSelectedInvoiceDocumentNumber(request.getParameter("goToInvoiceDocumentNumber"));
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    public ActionForward setCustomer(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentApplicationDocumentForm pform = (PaymentApplicationDocumentForm) form;
        Collection<NonAppliedHolding> holdings = pform.getNonAppliedHoldingsForCustomer();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
}
