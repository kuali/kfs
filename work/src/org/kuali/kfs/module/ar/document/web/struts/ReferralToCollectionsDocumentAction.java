/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.document.web.struts;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.ReferralToCollectionsDocument;
import org.kuali.kfs.module.ar.report.service.ReferralToCollectionsService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.lookup.LookupResultsService;
import org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Action class for Referral To Collections Document.
 */
public class ReferralToCollectionsDocumentAction extends KualiTransactionalDocumentActionBase {
    private volatile static LookupResultsService lookupResultsService;
    private volatile static ReferralToCollectionsService referralToCollectionsService;

    /**
     * Do initialization for a new Referral to Collections Document.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        showInvoices((ReferralToCollectionsDocumentForm) kualiDocumentFormBase);
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
            Collection<ContractsGrantsInvoiceDocument> invoices = getCGInvoiceDocumentsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, personId);
            getReferralToCollectionsService().populateReferralToCollectionsDocumentWithInvoices(referralToCollectionsDocument, invoices);
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

    /**
     * Gets the invoice documents from sequence number.
     * @param lookupResultsSequenceNumber The sequence number of search result.
     * @param personId The principal id of the person who searched.
     * @return Returns the list of invoice documents.
     */
    protected Collection<ContractsGrantsInvoiceDocument> getCGInvoiceDocumentsFromLookupResultsSequenceNumber(String lookupResultsSequenceNumber, String personId) {
        Collection<ContractsGrantsInvoiceDocument> invoiceDocuments = new ArrayList<ContractsGrantsInvoiceDocument>();
        try {
            for (PersistableBusinessObject obj : getLookupResultsService().retrieveSelectedResultBOs(lookupResultsSequenceNumber, ContractsGrantsInvoiceDocument.class, personId)) {
                invoiceDocuments.add((ContractsGrantsInvoiceDocument) obj);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return invoiceDocuments;
    }

    public static LookupResultsService getLookupResultsService() {
        if (lookupResultsService == null) {
            lookupResultsService = SpringContext.getBean(LookupResultsService.class);
        }
        return lookupResultsService;
    }

    public static ReferralToCollectionsService getReferralToCollectionsService() {
        if (referralToCollectionsService == null) {
            referralToCollectionsService = SpringContext.getBean(ReferralToCollectionsService.class);
        }
        return referralToCollectionsService;
    }
}
