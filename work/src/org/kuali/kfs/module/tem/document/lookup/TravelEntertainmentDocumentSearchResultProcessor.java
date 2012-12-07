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



public class TravelEntertainmentDocumentSearchResultProcessor extends AbstractDocumentSearchResultProcessor /*implements TravelDocumentSearchResultsCustomizer*/ {
//
//    private String createPaymentsURL(DocSearchDTO docCriteriaDTO, String tripID) {
//        String links = createDisbursementVoucherLink(docCriteriaDTO, TemConstants.TravelCustomSearchLinks.VENDOR_PAYMENT);
//        links += "<br />" + createRequisitionLink(docCriteriaDTO, TemConstants.TravelCustomSearchLinks.REQUISITION);
//        String agencyLinks = createAgencySitesLinks(tripID);
//        links += (StringUtils.isEmpty(agencyLinks)?"":"<br />") + agencyLinks;
//        return links;
//    }
//
//    public String createEntertainmentLink(DocSearchDTO docCriteriaDTO) {
//        final DocumentType docType = getDocumentTypeService().findByName(TemConstants.TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT);
//        final String travelDocumentIdentifier = docCriteriaDTO.getSearchableAttribute(TRVL_IDENTIFIER_PROPERTY).getUserDisplayValue();
//        if (docType == null) {
//            throw new RuntimeException(String.format("DocType with name %s does not exist!", TemConstants.TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT));
//        }
//        String linkPopup = "target=\"_blank\"";
//
//
//        String link = String.format("<a href=\"%s&from=ENT&travelDocumentIdentifier=%s&command=initiate&docTypeName=%s\" %s>%s</a>",
//                docType.getDocHandlerUrl(),
//                docCriteriaDTO.getRouteHeaderId().toString(),
//                TemConstants.TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT,
//                linkPopup,
//                TravelCustomSearchLinks.NEW_ENTERTAINMENT);
//        return link;
//    }
//
//
//    @Override
//    public DocumentSearchResult addActionsColumn(DocSearchDTO docCriteriaDTO, DocumentSearchResult result) {
//        final String columnName = TemPropertyConstants.TRVL_DOC_SEARCH_RESULT_PROPERTY_NAME_ACTIONS;
//        String tripID = docCriteriaDTO.getSearchableAttribute(TRVL_IDENTIFIER_PROPERTY).getUserDisplayValue();
//        String actionsHTML = "";
//        actionsHTML += createEntertainmentLink(docCriteriaDTO);
//        actionsHTML += "<br />";
//        actionsHTML += createPaymentsURL(docCriteriaDTO, tripID);
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
//    @Override
//    protected DocumentTypeService getDocumentTypeService() {
//        return SpringContext.getBean(DocumentTypeService.class);
//    }
}
