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

import java.util.Collection;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsLookupResult;
import org.kuali.kfs.module.ar.businessobject.lookup.ReferralToCollectionsDocumentUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Action class for Referral To Document Summary.
 */
public class ReferralToCollectionsSummaryAction extends KualiAction {

    /**
     * 1. This method passes the control from Contracts Grants Invoice lookup to the Contracts Grants Invoice
     * Summary page. 2. Retrieves the list of selected awards by agency for creating invoices.
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
            Collection<ReferralToCollectionsLookupResult> referralToCollectionsLookupResults = ReferralToCollectionsDocumentUtil.getReferralToCollectionsResultsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, personId);

            referralToCollectionsSummaryForm.setReferralToCollectionsLookupResults(referralToCollectionsLookupResults);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method would create invoices for the list of awards. It calls the batch process to reuse the functionality to create the
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
        parameters.put(KFSConstants.PARAMETER_COMMAND, "initiate");
        parameters.put(KFSConstants.DOCUMENT_TYPE_NAME, ArPropertyConstants.REFERRAL_TO_COLL_DOC_TYPE);
        String referralToCollectionsSummaryUrl = UrlFactory.parameterizeUrl("arReferralToCollectionsDocument.do", parameters);
        return new ActionForward(referralToCollectionsSummaryUrl, true);
    }

    /**
     * To cancel the document, invoices are not created when the cancel method is called.
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
}
