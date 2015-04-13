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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsCollectionActivityInvoiceDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsCollectionActivityDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsCollectionActivityDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;
import org.kuali.kfs.sys.service.SegmentedLookupResultsService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;


/**
 * Action file for Collection Activity Document.
 */
public class ContractsGrantsCollectionActivityDocumentAction extends FinancialSystemTransactionalDocumentActionBase {
    protected static transient ContractsGrantsCollectionActivityDocumentService contractsGrantsCollectionActivityDocumentService;
    protected static transient BusinessObjectService businessObjectService;
    protected static transient SegmentedLookupResultsService segmentedLookupResultsService;
    protected static transient DateTimeService dateTimeService;

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsCollectionActivityDocumentAction.class);

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);

        final ContractsGrantsCollectionActivityDocumentForm cgCollectionActivityForm = (ContractsGrantsCollectionActivityDocumentForm) kualiDocumentFormBase;
        final ContractsGrantsCollectionActivityDocument document = cgCollectionActivityForm.getCollectionActivityDocument();
        document.setActivityDate(getDateTimeService().getCurrentSqlDate());

        if (!StringUtils.isBlank(cgCollectionActivityForm.getSelectedProposalNumber())) {
            document.setProposalNumber(Long.parseLong(cgCollectionActivityForm.getSelectedProposalNumber()));
            refreshAward(document);
        }
    }

    /**
     * This method deletes an invoice from the list.
     *
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward deleteInvoice(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsCollectionActivityDocumentForm colActDocForm = (ContractsGrantsCollectionActivityDocumentForm) form;
        ContractsGrantsCollectionActivityDocument colActDoc = colActDocForm.getCollectionActivityDocument();

        int indexOfLineToDelete = getLineToDelete(request);
        colActDoc.deleteInvoiceDetail(indexOfLineToDelete);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);
        ContractsGrantsCollectionActivityDocumentForm collectionActivityDocumentForm = (ContractsGrantsCollectionActivityDocumentForm) form;
        ContractsGrantsCollectionActivityDocument colActDoc = collectionActivityDocumentForm.getCollectionActivityDocument();
        Collection<PersistableBusinessObject> rawValues = null;

        // If multiple asset lookup was used to select the assets, then....
        if (StringUtils.equals(KFSConstants.MULTIPLE_VALUE, collectionActivityDocumentForm.getRefreshCaller())) {
            String lookupResultsSequenceNumber = collectionActivityDocumentForm.getLookupResultsSequenceNumber();
            refreshInvoices(colActDoc, lookupResultsSequenceNumber);
        }
        if (StringUtils.equals(ArConstants.AWARD_LOOKUP_IMPL, collectionActivityDocumentForm.getRefreshCaller())) {
            refreshAward(colActDoc);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Refreshes the multi-value lookup invoices
     * @param colActDoc the documet the invoice information should appear on
     * @param lookupResultsSequenceNumber the sequence number of the invoices returned from the lookup
     * @throws Exception if the invoices from the lookup cannot be retrieved for some reason
     */
    protected void refreshInvoices(ContractsGrantsCollectionActivityDocument colActDoc, String lookupResultsSequenceNumber) throws Exception {
        Set<String> selectedIds = getSegmentedLookupResultsService().retrieveSetOfSelectedObjectIds(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        if (ObjectUtils.isNotNull(selectedIds) && CollectionUtils.isNotEmpty(selectedIds)) {
            for (String invoiceDocumentNumber: selectedIds) {
                Map<String, String> criteria = new HashMap<String, String>();
                criteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, invoiceDocumentNumber);
                ContractsGrantsInvoiceDocument cgInvoiceDocument = getBusinessObjectService().findByPrimaryKey(ContractsGrantsInvoiceDocument.class, criteria);

                if (ObjectUtils.isNotNull(cgInvoiceDocument)) {
                    ContractsGrantsCollectionActivityInvoiceDetail invoiceDetail = new ContractsGrantsCollectionActivityInvoiceDetail();
                    invoiceDetail.setBillingPeriod(cgInvoiceDocument.getInvoiceGeneralDetail().getBillingPeriod());
                    invoiceDetail.setBillingDate(cgInvoiceDocument.getBillingDate());
                    invoiceDetail.setInvoiceNumber(cgInvoiceDocument.getDocumentNumber());
                    invoiceDetail.setDocumentNumber(colActDoc.getDocumentNumber());
                    if (!colActDoc.getInvoiceDetails().contains(invoiceDetail)) {
                        colActDoc.getInvoiceDetails().add(invoiceDetail);
                    }
                }
            }
        }
    }

    /**
     * Refreshes information on the C&G Collection Activity document related to the award
     * @param colActDoc the collection activity document
     */
    protected void refreshAward(ContractsGrantsCollectionActivityDocument colActDoc) {
        if (ObjectUtils.isNotNull(colActDoc.getProposalNumber())) {
            ContractsAndGrantsBillingAward award = getContractsGrantsCollectionActivityDocumentService().retrieveAwardByProposalNumber(colActDoc.getProposalNumber());
            if (ObjectUtils.isNotNull(award)) {
                colActDoc.setAgencyNumber(award.getAgencyNumber());
                colActDoc.setAgencyName(award.getAgency().getFullName());
                if (ObjectUtils.isNotNull(award.getAgency().getCustomer())) {
                    colActDoc.setCustomerNumber(award.getAgency().getCustomer().getCustomerNumber());
                    colActDoc.setCustomerName(award.getAgency().getCustomer().getCustomerName());
                }
            }
            else {
                colActDoc.setAgencyNumber("Award not found");
                colActDoc.setAgencyName(KFSConstants.EMPTY_STRING);
                colActDoc.setCustomerNumber(KFSConstants.EMPTY_STRING);
                colActDoc.setCustomerName(KFSConstants.EMPTY_STRING);
            }
        }
    }

    public static ContractsGrantsCollectionActivityDocumentService getContractsGrantsCollectionActivityDocumentService() {
        if (contractsGrantsCollectionActivityDocumentService == null) {
            contractsGrantsCollectionActivityDocumentService = SpringContext.getBean(ContractsGrantsCollectionActivityDocumentService.class);
        }
        return contractsGrantsCollectionActivityDocumentService;
    }

    public static SegmentedLookupResultsService getSegmentedLookupResultsService() {
        if (segmentedLookupResultsService == null) {
            segmentedLookupResultsService = SpringContext.getBean(SegmentedLookupResultsService.class);
        }
        return segmentedLookupResultsService;
    }

    public static DateTimeService getDateTimeService() {
        if (dateTimeService == null) {
            dateTimeService = SpringContext.getBean(DateTimeService.class);
        }
        return dateTimeService;
    }

}
