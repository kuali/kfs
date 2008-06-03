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
package org.kuali.module.ar.web.struts.action;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.document.Document;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase;
import org.kuali.module.ar.bo.NonAppliedHolding;
import org.kuali.module.ar.web.struts.form.PaymentApplicationDocumentForm;
import org.kuali.rice.KNSServiceLocator;

import edu.iu.uis.eden.exception.WorkflowException;

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
    
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO Auto-generated method stub
        return super.execute(mapping, form, request, response);
    }

    public ActionForward setCustomer(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentApplicationDocumentForm pform = (PaymentApplicationDocumentForm) form;
        Collection<NonAppliedHolding> holdings = pform.getNonAppliedHoldingsForCustomer();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
}
