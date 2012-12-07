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


/**
 * Produces custom search results for {@link TravelAuthorizationDocument}
 */
public class TravelAuthorizationDocumentSearchResultProcessor extends AbstractDocumentSearchResultProcessor /*implements TravelDocumentSearchResultsCustomizer*/ {
//
//    protected static Logger LOG = Logger.getLogger(TravelAuthorizationDocumentSearchResultProcessor.class);
//
//    /**
//     * Determines if the url column for actions should be rendered. This is done for {@link TravelAuthorizationDocument} instances
//     * in the search results that have a workflow document status of FINAL or PROCESSED and on documents that do not have a workflow
//     * App Doc Status of REIMB_HELD, CANCELLED, PEND_AMENDMENT, CLOSED, or RETIRED_VERSION.
//     *
//     * check status of document and don't create if the status is not final or processed
//     *
//     * @param docCriteriaDTO has the workflow document status and app doc status to determine if rendering of the link is necessary
//     * @return true if the document should have a reimbursement link
//     */
//    private boolean showNewDocumentURL(DocSearchDTO docCriteriaDTO, String docType) {
//        final String documentStatus = docCriteriaDTO.getDocRouteStatusCode();
//        final String appDocStatus = docCriteriaDTO.getAppDocStatus();
//        boolean statusCheck = (documentStatus.equals(DocumentStatus.FINAL.getCode())
//                || (documentStatus.equals(DocumentStatus.PROCESSED.getCode())))
//                && (!appDocStatus.equals(TravelAuthorizationStatusCodeKeys.REIMB_HELD))
//                && (!appDocStatus.equals(TravelAuthorizationStatusCodeKeys.CANCELLED))
//                && (!appDocStatus.equals(TravelAuthorizationStatusCodeKeys.PEND_AMENDMENT))
//                && (!appDocStatus.equals(TravelAuthorizationStatusCodeKeys.RETIRED_VERSION))
//                && (!appDocStatus.equals(TravelAuthorizationStatusCodeKeys.CLOSED));
//
//        TravelDocument document = getDocument(docCriteriaDTO.getRouteHeaderId().toString());
//        Person user = GlobalVariables.getUserSession().getPerson();
//
//        boolean hasInitAccess = true;
//        if (getTemRoleService().canAccessTravelDocument(document, user) && document.getTemProfileId() != null){
//            //check if user also can init other docs
//            hasInitAccess = user.getPrincipalId().equals(document.getTraveler().getPrincipalId()) || getTemRoleService().isTravelDocumentArrangerForProfile(docType, user.getPrincipalId(), document.getTemProfileId());
//
//        }
//
//        return statusCheck && hasInitAccess;
//    }
//
//    /**
//     * Other payment is allowed
//     *
//     * @param docCriteriaDTO
//     * @return
//     */
//    private boolean otherPaymentMethodsAllowed(DocSearchDTO docCriteriaDTO) {
//        final String documentStatus = docCriteriaDTO.getDocRouteStatusCode();
//        return (getParameterService().getParameterValueAsBoolean(TemConstants.PARAM_NAMESPACE, TemConstants.TravelAuthorizationParameters.PARAM_DTL_TYPE, TemConstants.TravelAuthorizationParameters.ENABLE_VENDOR_PAYMENT_BEFORE_TA_FINAL_APPROVAL_IND)
//                || documentStatus.equals(DocumentStatus.FINAL.getCode())
//                || documentStatus.equals(DocumentStatus.PROCESSED.getCode()));
//    }
//
//    /**
//     * check if the trip has already started
//     *
//     * @param docCriteriaDTO
//     * @return
//     */
//    private boolean hasTripStarted(DocSearchDTO docCriteriaDTO) {
//        //default that the trip has started
//        boolean tripStarted = true;
//        TravelDocument document = getDocument(docCriteriaDTO.getRouteHeaderId().toString());
//        tripStarted = document.getTripBegin().before(new Date());
//        return tripStarted;
//    }
//
//    /**
//     *
//     * @param docCriteriaDTO
//     * @param tripID
//     * @return
//     */
//    private String createPaymentsURL(DocSearchDTO docCriteriaDTO, String tripID) {
//        String links = createDisbursementVoucherLink(docCriteriaDTO, TemConstants.TravelCustomSearchLinks.PRE_TRIP_VENDOR_PAYMENT);
//        links += "<br />" + createRequisitionLink(docCriteriaDTO, TemConstants.TravelCustomSearchLinks.REQUISITION);
//        String agencyLinks = createAgencySitesLinks(tripID);
//        links += (StringUtils.isEmpty(agencyLinks) ? "" : "<br />") + agencyLinks;
//        return links;
//    }
//
//    /**
//     * @see org.kuali.kfs.module.tem.document.lookup.TravelDocumentSearchResultsProcessor#addActionsColumn(org.kuali.rice.kew.docsearch.DocSearchDTO, org.kuali.rice.kew.docsearch.DocumentSearchResult)
//     */
//    @Override
//    public DocumentSearchResult addActionsColumn(DocSearchDTO docCriteriaDTO, DocumentSearchResult result) {
//        final String columnName = TemPropertyConstants.TRVL_DOC_SEARCH_RESULT_PROPERTY_NAME_ACTIONS;
//        String tripID = docCriteriaDTO.getSearchableAttribute(TRVL_IDENTIFIER_PROPERTY).getUserDisplayValue();
//        String actionsHTML = "";
//        if (showNewDocumentURL(docCriteriaDTO, TemConstants.TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT)) {
//            actionsHTML += createEntertainmentLink(tripID, docCriteriaDTO);
//            actionsHTML += "<br />";
//        }
//        if (showNewDocumentURL(docCriteriaDTO, TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT)) {
//            actionsHTML += createTravelReimbursementLink(tripID);
//        }
//        if (otherPaymentMethodsAllowed(docCriteriaDTO) || !hasTripStarted(docCriteriaDTO)) {
//            if (!StringUtils.isBlank(actionsHTML)) {
//                actionsHTML += "<br />";
//            }
//            actionsHTML += createPaymentsURL(docCriteriaDTO, tripID);
//        }
//
//        final KeyValueSort actions = new KeyValueSort(columnName, "", actionsHTML, actionsHTML, null);
//        result.getResultContainers().add(0, actions);
//
//        return result;
//    }
//
//    /**
//     * @see org.kuali.kfs.module.tem.document.lookup.TravelDocumentSearchResultsProcessor#filterSearchResult(org.kuali.rice.kew.docsearch.DocSearchDTO)
//     */
//    @Override
//    public boolean filterSearchResult(DocSearchDTO docCriteriaDTO) {
//        boolean filtered = filterByUser(docCriteriaDTO);
//
//        ///TA doc allows search result IF user has TR arranger access
//        TravelDocument document = getDocument(docCriteriaDTO.getRouteHeaderId().toString());
//        Person user = GlobalVariables.getUserSession().getPerson();
//        //check if user is an TR arranger to the document
//        boolean arrangerAccess = true;
//
//        if (!user.getPrincipalId().equals(document.getTraveler().getPrincipalId())){
//            arrangerAccess = getTemRoleService().isTravelArranger(user, "", document.getTemProfileId().toString(), TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT);
//        }
//
//        return filtered && !arrangerAccess;
//    }
//
//    private ParameterService getParameterService() {
//        return SpringContext.getBean(ParameterService.class);
//    }

}
