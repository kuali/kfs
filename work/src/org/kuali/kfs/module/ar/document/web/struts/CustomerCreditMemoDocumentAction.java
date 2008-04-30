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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase;
import org.kuali.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.module.ar.web.struts.form.CustomerCreditMemoDocumentForm;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.web.struts.form.CreditMemoForm;

import edu.iu.uis.eden.exception.WorkflowException;

public class CustomerCreditMemoDocumentAction extends KualiAccountingDocumentActionBase {
    
    public CustomerCreditMemoDocumentAction() {
        super();
    }
    
    /**
     * Do initialization for a new customer credit memo.
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        ((CustomerCreditMemoDocument) kualiDocumentFormBase.getDocument()).initiateDocument();
    }
    
    /**
     * Clears out init tab.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward clearInitTab(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerCreditMemoDocumentForm customerCreditMemoDocumentForm = (CustomerCreditMemoDocumentForm) form;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) customerCreditMemoDocumentForm.getDocument();
        customerCreditMemoDocument.clearInitFields();
        
        return super.refresh(mapping, form, request, response);
    }   
}
