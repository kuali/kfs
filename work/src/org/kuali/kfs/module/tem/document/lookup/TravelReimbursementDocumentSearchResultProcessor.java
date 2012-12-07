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



public class TravelReimbursementDocumentSearchResultProcessor extends AbstractDocumentSearchResultProcessor /*implements TravelDocumentSearchResultsCustomizer*/ {
//
//    /**
//     * This method determines whether a reimbursement has been completely approved.
//     * In order to accurately keep track on disencumbrance, the appDocStatus needs to be Department Approved.
//     * @param docCriteriaDTO
//     * @return
//     */
//    private boolean showNewDocumentURL(DocSearchDTO docCriteriaDTO, String docType) {
//        final String documentStatus = docCriteriaDTO.getDocRouteStatusCode();
//        final String appDocStatus = docCriteriaDTO.getAppDocStatus();
//        boolean statusCheck = (documentStatus.equals(DocumentStatus.FINAL.getCode())
//                || (documentStatus.equals(DocumentStatus.PROCESSED.getCode())))
//                && (appDocStatus.equals(TravelReimbursementStatusCodeKeys.DEPT_APPROVED));
//
//
//        TravelDocument document = getDocument(docCriteriaDTO.getRouteHeaderId().toString());
//        Person user = GlobalVariables.getUserSession().getPerson();
//
//        boolean hasInitAccess = true;
//        if ((!docType.equals(TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT)) && getTemRoleService().canAccessTravelDocument(document, user) && document.getTemProfileId() != null){
//            hasInitAccess = getTemRoleService().isTravelDocumentArrangerForProfile(docType, user.getPrincipalId(), document.getTemProfileId());
//        }
//
//        return statusCheck && hasInitAccess;
//    }
//
//    private boolean otherPaymentMethodsAllowed(DocSearchDTO docCriteriaDTO) {
//        final String documentStatus = docCriteriaDTO.getDocRouteStatusCode();
//        return (getParameterService().getParameterValueAsBoolean(TemConstants.PARAM_NAMESPACE, TemConstants.TravelReimbursementParameters.PARAM_DTL_TYPE, TemConstants.TravelReimbursementParameters.ENABLE_VENDOR_PAYMENT_BEFORE_FINAL_TR_APPROVAL_IND)
//                || documentStatus.equals(DocumentStatus.FINAL.getCode())
//                || documentStatus.equals(DocumentStatus.PROCESSED.getCode()));
//    }
//
//    private String createPaymentsURL(DocSearchDTO docCriteriaDTO, String tripID) {
//        String links = createDisbursementVoucherLink(docCriteriaDTO, TemConstants.TravelCustomSearchLinks.VENDOR_PAYMENT);
//        links += "<br />" + createRequisitionLink(docCriteriaDTO, TemConstants.TravelCustomSearchLinks.REQUISITION);
//        String agencyLinks = createAgencySitesLinks(tripID);
//        links += (StringUtils.isEmpty(agencyLinks)?"":"<br />") + agencyLinks;
//        return links;
//    }
//
//    @Override
//    public DocumentSearchResult addActionsColumn(DocSearchDTO docCriteriaDTO, DocumentSearchResult result) {
//        final String columnName = TemPropertyConstants.TRVL_DOC_SEARCH_RESULT_PROPERTY_NAME_ACTIONS;
//        String tripID = docCriteriaDTO.getSearchableAttribute(TRVL_IDENTIFIER_PROPERTY).getUserDisplayValue();
//        String documentNumber = docCriteriaDTO.getSearchableAttribute(KRADConstants.DOCUMENT_DOCUMENT_NUMBER).getUserDisplayValue();
//        String actionsHTML = "";
//        if (showNewDocumentURL(docCriteriaDTO, TemConstants.TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT)) {
//            actionsHTML += createEntertainmentLink(tripID, docCriteriaDTO);
//            actionsHTML += "<br />";
//        }
//        if (showNewDocumentURL(docCriteriaDTO, TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT)) {
//            actionsHTML += createTravelReimbursementLink(tripID);
//        }
//        if (otherPaymentMethodsAllowed(docCriteriaDTO)) {
//            if (!StringUtils.isBlank(actionsHTML)) {
//                actionsHTML += "<br />";
//            }
//            actionsHTML += createPaymentsURL(docCriteriaDTO, tripID);
//        }
//
//
//        final KeyValueSort actions = new KeyValueSort(columnName, "", actionsHTML, actionsHTML,null);
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
//        return filterByUser(docCriteriaDTO);
//    }
//
//    private ParameterService getParameterService() {
//        return SpringContext.getBean(ParameterService.class);
//
//    }
//
//    @Override
//    protected DocumentTypeService getDocumentTypeService() {
//        return SpringContext.getBean(DocumentTypeService.class);
//    }
}
