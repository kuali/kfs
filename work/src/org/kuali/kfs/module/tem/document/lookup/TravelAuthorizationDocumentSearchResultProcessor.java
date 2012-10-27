/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.tem.document.lookup;

import static org.kuali.kfs.module.tem.TemPropertyConstants.TRVL_IDENTIFIER_PROPERTY;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.docsearch.DocSearchDTO;
import org.kuali.rice.kew.docsearch.DocumentSearchResult;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.web.KeyValueSort;
import org.kuali.rice.kns.service.ParameterService;

/**
 * Produces custom search results for {@link TravelAuthorizationDocument}
 * 
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
public class TravelAuthorizationDocumentSearchResultProcessor extends AbstractDocumentSearchResultProcessor implements TravelDocumentSearchResultsProcessor {

    protected static Logger LOG = Logger.getLogger(TravelAuthorizationDocumentSearchResultProcessor.class);
    
    /**
     * Determines if the url column for actions should be rendered. This is done for {@link TravelAuthorizationDocument} instances
     * in the search results that have a workflow document status of FINAL or PROCESSED and on documents that do not have a workflow
     * App Doc Status of REIMB_HELD, CANCELLED, PEND_AMENDMENT, CLOSED, or RETIRED_VERSION.
     * 
     * @param docCriteriaDTO has the workflow document status and app doc status to determine if rendering of the link is necessary
     * @return true if the document should have a reimbursement link
     */
    private boolean showReimbursementURL(DocSearchDTO docCriteriaDTO) {
        // kualitem-401 ... check status of document and don't create if the status is not final or processed
        // KUALITEM check if document is on hold or cancelled. If on hold, no link.

        final String documentStatus = docCriteriaDTO.getDocRouteStatusCode();
        final String appDocStatus = docCriteriaDTO.getAppDocStatus();
        return (documentStatus.equals(KEWConstants.ROUTE_HEADER_FINAL_CD)
                || (documentStatus.equals(KEWConstants.ROUTE_HEADER_PROCESSED_CD)))
                && (!appDocStatus.equals(TravelAuthorizationStatusCodeKeys.REIMB_HELD))
                && (!appDocStatus.equals(TravelAuthorizationStatusCodeKeys.CANCELLED))
                && (!appDocStatus.equals(TravelAuthorizationStatusCodeKeys.PEND_AMENDMENT))
                && (!appDocStatus.equals(TravelAuthorizationStatusCodeKeys.RETIRED_VERSION))
                && (!appDocStatus.equals(TravelAuthorizationStatusCodeKeys.CLOSED));
    }

    /**
     * Other payment is allowed
     * 
     * @param docCriteriaDTO
     * @return
     */
    private boolean otherPaymentMethodsAllowed(DocSearchDTO docCriteriaDTO) {
        final String documentStatus = docCriteriaDTO.getDocRouteStatusCode();
        return (getParameterService().getIndicatorParameter(TemConstants.PARAM_NAMESPACE, TemConstants.TravelAuthorizationParameters.PARAM_DTL_TYPE, TemConstants.TravelAuthorizationParameters.ENABLE_VENDOR_PAYMENT_BEFORE_TA_FINAL_APPROVAL_IND) 
                || documentStatus.equals(KEWConstants.ROUTE_HEADER_FINAL_CD) 
                || documentStatus.equals(KEWConstants.ROUTE_HEADER_PROCESSED_CD));
    }
    
    /**
     * check if the trip has already started
     * 
     * @param docCriteriaDTO
     * @return
     */
    private boolean hasTripStarted(DocSearchDTO docCriteriaDTO) {
        
        //default that the trip has started
        boolean tripStarted = true;
        try {
            TravelDocument document = getDocument(docCriteriaDTO.getRouteHeaderId().toString());
            tripStarted = document.getTripBegin().before(new Date());
        }
        catch (WorkflowException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return tripStarted;
    }

    /**
     * 
     * @param docCriteriaDTO
     * @param tripID
     * @return
     */
    private String createPaymentsURL(DocSearchDTO docCriteriaDTO, String tripID) {
        String links = createDisbursementVoucherLink(docCriteriaDTO, TemConstants.TravelCustomSearchLinks.PRE_TRIP_VENDOR_PAYMENT);
        links += "<br />" + createRequisitionLink(docCriteriaDTO, TemConstants.TravelCustomSearchLinks.REQUISITION);
        String agencyLinks = createAgencySitesLinks(tripID);
        links += (StringUtils.isEmpty(agencyLinks) ? "" : "<br />") + agencyLinks;
        return links;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.lookup.TravelDocumentSearchResultsProcessor#addActionsColumn(org.kuali.rice.kew.docsearch.DocSearchDTO, org.kuali.rice.kew.docsearch.DocumentSearchResult)
     */
    @Override
    public DocumentSearchResult addActionsColumn(DocSearchDTO docCriteriaDTO, DocumentSearchResult result) {
        final String columnName = TemPropertyConstants.TRVL_DOC_SEARCH_RESULT_PROPERTY_NAME_ACTIONS;
        String tripID = docCriteriaDTO.getSearchableAttribute(TRVL_IDENTIFIER_PROPERTY).getUserDisplayValue();
        String actionsHTML = "";
        if (showReimbursementURL(docCriteriaDTO)) {
            actionsHTML += createTravelReimbursementLink(tripID);
        }
        if (otherPaymentMethodsAllowed(docCriteriaDTO) || !hasTripStarted(docCriteriaDTO)) {
            if (!StringUtils.isBlank(actionsHTML)) {
                actionsHTML += "<br />";
            }
            actionsHTML += createPaymentsURL(docCriteriaDTO, tripID);
        }

        final KeyValueSort actions = new KeyValueSort(columnName, "", actionsHTML, actionsHTML, null);
        result.getResultContainers().add(0, actions);

        return result;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.lookup.TravelDocumentSearchResultsProcessor#filterSearchResult(org.kuali.rice.kew.docsearch.DocSearchDTO)
     */
    @Override
    public boolean filterSearchResult(DocSearchDTO docCriteriaDTO) {
        return filterByUser(docCriteriaDTO);
    }

    private ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

}
