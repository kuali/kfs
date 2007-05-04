/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.purap.web.struts.form.RequisitionForm;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class handles specific Actions requests for the Requisition.
 * 
 */
public class RequisitionAction extends PurchasingActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RequisitionAction.class);

    /**
     * Do initialization for a new requisition
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        
        super.createDocument(kualiDocumentFormBase);
        
        ((RequisitionDocument) kualiDocumentFormBase.getDocument()).initiateDocument();
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.refresh(mapping, form, request, response);
    	RequisitionForm rqForm = (RequisitionForm) form;
        RequisitionDocument document = (RequisitionDocument) rqForm.getDocument();
    	
        // super.refresh() must occur before this line to get the correct APO limit
        document.setOrganizationAutomaticPurchaseOrderLimit(SpringServiceLocator.getRequisitionService().getApoLimit(document.getVendorContractGeneratedIdentifier(), document.getChartOfAccountsCode(), document.getOrganizationCode()));
        return forward;
    }

    public ActionForward viewRelatedDocuments(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("viewRelatedDocuments() enter action");

        //TODO add code

        return mapping.findForward("viewRelatedDocuments");
    }

    public ActionForward viewPaymentHistory(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("viewPaymentHistory() enter action");

        //TODO add code

        return mapping.findForward("viewPaymentHistory");
    }

}