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
package org.kuali.kfs.module.ar.web.struts;

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
import org.kuali.kfs.module.ar.report.service.ReferralToCollectionsService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Action class for Referral To Document Summary.
 */
public class ReferralToCollectionsSummaryAction extends ContractsGrantsBillingSummaryActionBase {
    private volatile static ReferralToCollectionsService referralToCollectionsService;

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
}
