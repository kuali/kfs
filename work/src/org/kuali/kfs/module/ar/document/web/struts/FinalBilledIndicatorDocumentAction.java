/*
 * Copyright 2011 The Kuali Foundation.
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.businessobject.FinalBilledIndicatorEntry;
import org.kuali.kfs.module.ar.document.FinalBilledIndicatorDocument;
import org.kuali.kfs.module.ar.document.validation.impl.FinalBilledIndicatorValidation;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.rice.kns.web.struts.form.KualiForm;

/**
 * Action class for Final Billed Indicator Document.
 */
public class FinalBilledIndicatorDocumentAction extends KualiTransactionalDocumentActionBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FinalBilledIndicatorDocumentAction.class);

    /**
     * This method adds an entry to the list of entries.
     *
     * @param mapping
     * @param actionForm
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addInvoiceEntry(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        FinalBilledIndicatorDocumentForm form = (FinalBilledIndicatorDocumentForm) actionForm;
        FinalBilledIndicatorDocument document = form.getFinalBilledIndicatorDocument();
        FinalBilledIndicatorEntry newUnfinalizeInvoiceEntry = form.getInvoiceEntry();
        if (FinalBilledIndicatorValidation.validateEntry(newUnfinalizeInvoiceEntry)) {
            document.addInvoiceEntry(newUnfinalizeInvoiceEntry);
            form.setInvoiceEntry(new FinalBilledIndicatorEntry());
        }
        return mapping.findForward("basic");
    }

    /**
     * This method removes an entry to the list of entries.
     *
     * @param mapping
     * @param actionForm
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteInvoiceEntry(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        FinalBilledIndicatorDocumentForm form = (FinalBilledIndicatorDocumentForm) actionForm;
        FinalBilledIndicatorDocument document = form.getFinalBilledIndicatorDocument();
        int deleteIndex = getLineToDelete(request);
        document.removeInvoiceEntry(deleteIndex);
        return mapping.findForward("basic");
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#doProcessingAfterPost(org.kuali.rice.kns.web.struts.form.KualiForm,
     *      javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void doProcessingAfterPost(KualiForm actionForm, HttpServletRequest request) {
        FinalBilledIndicatorDocumentForm form = (FinalBilledIndicatorDocumentForm) actionForm;
        FinalBilledIndicatorDocument document = form.getFinalBilledIndicatorDocument();
        if (FinalBilledIndicatorValidation.validateDocument(document)) {
            try {
                document.updateContractsGrantsInvoiceDocument();
            }
            catch (WorkflowException ex) {
                LOG.error("problem during FinalBilledIndicatorDocumentAction.doProcessingAfterPost()", ex);
            }
        }
        super.doProcessingAfterPost(actionForm, request);
    }

}
