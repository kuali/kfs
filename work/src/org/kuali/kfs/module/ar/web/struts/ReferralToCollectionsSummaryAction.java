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
package org.kuali.kfs.module.ar.web.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsLookupResult;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.report.service.ReferralToCollectionsService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kns.lookup.LookupResultsService;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Action class for Referral To Document Summary.
 */
public class ReferralToCollectionsSummaryAction extends KualiAction {
    private volatile static ReferralToCollectionsService referralToCollectionsService;
    private volatile static LookupResultsService lookupResultsService;

    /**
     * 1. This method passes the control from the Referral To Collections lookup to the Referral To Collections
     * Summary page. 2. Retrieves the list of selected invoices by award for creating the Referral To Collections document.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward viewSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ReferralToCollectionsSummaryForm referralToCollectionsSummaryForm = (ReferralToCollectionsSummaryForm) form;
        String lookupResultsSequenceNumber = referralToCollectionsSummaryForm.getLookupResultsSequenceNumber();
        if (StringUtils.isNotBlank(lookupResultsSequenceNumber)) {
            String personId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
            Collection<ReferralToCollectionsLookupResult> referralToCollectionsLookupResults = getReferralToCollectionsResultsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, personId);

            referralToCollectionsSummaryForm.setReferralToCollectionsLookupResults(referralToCollectionsLookupResults);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Gets the ReferralToCollections Lookup Result objects.
     *
     * @param lookupResultsSequenceNumber The sequence number of result.
     * @param personId The id of logged in person.
     * @return Returns the collection of awards.
     */
    protected Collection<ReferralToCollectionsLookupResult> getReferralToCollectionsResultsFromLookupResultsSequenceNumber(String lookupResultsSequenceNumber, String personId) {
        return getReferralToCollectionsService().getPopulatedReferralToCollectionsLookupResults(getCGInvoiceDocumentsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, personId));
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

    /**
     * This method initiates a Referral To Collections document for the list of invoices.
     * invoices.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward createInvoices(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String lookupResultsSequenceNumber = "";
        String parameterName = (String) request.getAttribute(KRADConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            lookupResultsSequenceNumber = StringUtils.substringBetween(parameterName, ".number", ".");
        }

        Properties parameters = new Properties();
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.DOC_HANDLER_METHOD);
        parameters.put(KRADConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER, lookupResultsSequenceNumber);
        parameters.put(KFSConstants.PARAMETER_COMMAND, KewApiConstants.INITIATE_COMMAND);
        parameters.put(KFSConstants.DOCUMENT_TYPE_NAME, ArConstants.ArDocumentTypeCodes.REFERRAL_TO_COLLECTIONS);
        String referralToCollectionsSummaryUrl = UrlFactory.parameterizeUrl("arReferralToCollectionsDocument.do", parameters);
        return new ActionForward(referralToCollectionsSummaryUrl, true);
    }

    /**
     * To cancel the process, the Referral To Collections document is not created when the cancel method is called.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_CANCEL);
    }

    public static ReferralToCollectionsService getReferralToCollectionsService() {
        if (referralToCollectionsService == null) {
            referralToCollectionsService = SpringContext.getBean(ReferralToCollectionsService.class);
        }
        return referralToCollectionsService;
    }

    public static LookupResultsService getLookupResultsService() {
        if (lookupResultsService == null) {
            lookupResultsService = SpringContext.getBean(LookupResultsService.class);
        }
        return lookupResultsService;
    }
}