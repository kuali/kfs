/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsDetail;
import org.kuali.kfs.module.ar.businessobject.lookup.ReferralToCollectionsDocumentUtil;
import org.kuali.kfs.module.ar.document.ReferralToCollectionsDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

/**
 * Action class for Referral To Collections Document.
 */
public class ReferralToCollectionsDocumentAction extends KualiTransactionalDocumentActionBase {

    /**
     * Default Constructor.
     */
    public ReferralToCollectionsDocumentAction() {
        super();
    }

    /**
     * Do initialization for a new Referral to Collections Document.
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        this.showInvoices((ReferralToCollectionsDocumentForm) kualiDocumentFormBase);
    }

    /**
     * This method is used to show invoices.
     * 
     * @param referralToCollectionsDocumentForm
     */
    public void showInvoices(ReferralToCollectionsDocumentForm referralToCollectionsDocumentForm) {
        ReferralToCollectionsDocument referralToCollectionsDocument = referralToCollectionsDocumentForm.getReferralToCollectionsDocument();

        String lookupResultsSequenceNumber = referralToCollectionsDocumentForm.getLookupResultsSequenceNumber();
        if (StringUtils.isNotBlank(lookupResultsSequenceNumber)) {
            String personId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
            ReferralToCollectionsDocumentUtil.setReferralToCollectionsDetailsFromLookupResultsSequenceNumber(referralToCollectionsDocument, lookupResultsSequenceNumber, personId);
        }
    }

    /**
     * Deletes the invoice from document list.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteInvoice(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ReferralToCollectionsDocumentForm referralToCollectionsDocumentForm = (ReferralToCollectionsDocumentForm) form;
        ReferralToCollectionsDocument referralToCollectionsDocument = referralToCollectionsDocumentForm.getReferralToCollectionsDocument();

        int indexOfLineToDelete = getLineToDelete(request);
        ReferralToCollectionsDetail referralToCollectionsDetail = referralToCollectionsDocument.getReferralToCollectionsDetail(indexOfLineToDelete);
        referralToCollectionsDocument.deleteReferralToCollectionsDetail(indexOfLineToDelete);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}
