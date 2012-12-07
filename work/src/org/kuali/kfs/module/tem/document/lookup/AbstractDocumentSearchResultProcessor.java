/*
 * Copyright 2011 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.tem.document.lookup;

import org.apache.log4j.Logger;

public abstract class AbstractDocumentSearchResultProcessor {

    public static Logger LOG = Logger.getLogger(AbstractDocumentSearchResultProcessor.class);
//
//    /**
//     *
//     * @param travelDocumentIdentifier
//     * @return
//     */
//    public String createTravelReimbursementLink(String travelDocumentIdentifier) {
//        KeyValueSort retval = new KeyValueSort();
//        final DocumentType docType = getDocumentTypeService().findByName(TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT);
//        if (docType == null) {
//            throw new RuntimeException(String.format("DocType with name %s does not exist!", TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT));
//        }
//        String linkPopup = "target=\"_blank\"";
//
//        String userDisplaySortValue = TemConstants.TravelCustomSearchLinks.NEW_REIMBURSEMENT;
//        String link = String.format("<a href=\"%s&travelDocumentIdentifier=%s&command=initiate&docTypeName=%s\" %s>%s</a>",
//                docType.getDocHandlerUrl(),
//                travelDocumentIdentifier,
//                TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT,
//                linkPopup,
//                userDisplaySortValue);
//
//        return link;
//    }
//
//    /**
//     *
//     * @param travelDocumentIdentifier
//     * @return
//     */
//    public String createEntertainmentLink(String travelDocumentIdentifier,DocSearchDTO docCriteriaDTO) {
//        KeyValueSort retval = new KeyValueSort();
//        final DocumentType docType = getDocumentTypeService().findByName(TemConstants.TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT);
//        if (docType == null) {
//            throw new RuntimeException(String.format("DocType with name %s does not exist!", TemConstants.TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT));
//        }
//        String linkPopup = "target=\"_blank\"";
//
//        String userDisplaySortValue = TemConstants.TravelCustomSearchLinks.NEW_ENTERTAINMENT;
//        String link = String.format("<a href=\"%s&" + KFSPropertyConstants.DOCUMENT_NUMBER + "=" + docCriteriaDTO.getRouteHeaderId().toString() + "&travelDocumentIdentifier=%s&command=initiate&docTypeName=%s\" %s>%s</a>",
//                docType.getDocHandlerUrl(),
//                travelDocumentIdentifier,
//                TemConstants.TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT,
//                linkPopup,
//                userDisplaySortValue);
//
//        return link;
//    }
//
//    /**
//     *
//     * @param docCriteriaDTO
//     * @param title
//     * @return
//     */
//    public String createRequisitionLink(DocSearchDTO docCriteriaDTO, String title) {
//        final DocumentType docType = getDocumentTypeService().findByName(REQUISITION_DOCTYPE);
//
//        if (docType == null) {
//            throw new RuntimeException(String.format("DocType with name %s does not exist!", REQUISITION_DOCTYPE));
//        }
//        String linkPopup = "target=\"_blank\"";
//
//        String unresolvedREQSURL = "${application.url}/" + TemConstants.TravelCustomSearchLinks.REQ_URL + docCriteriaDTO.getRouteHeaderId().toString();
//
//        String REQSURL = Utilities.substituteConfigParameters(docType.getServiceNamespace(), unresolvedREQSURL);
//        String link = String.format("<a href=\"%s\" %s>%s</a>", REQSURL, linkPopup, title);
//        return link;
//    }
//
//    /**
//     *
//     * @param docCriteriaDTO
//     * @param title
//     * @return
//     */
//    public String createDisbursementVoucherLink(DocSearchDTO docCriteriaDTO, String title) {
//        final DocumentType docType = getDocumentTypeService().findByName(DISBURSEMENT_VOUCHER_DOCTYPE);
//
//        if (docType == null) {
//            throw new RuntimeException(String.format("DocType with name %s does not exist!", DISBURSEMENT_VOUCHER_DOCTYPE));
//        }
//        String linkPopup = "target=\"_blank\"";
//
//        String unresolvedDVURL = "${application.url}/" + TemConstants.TravelCustomSearchLinks.DV_URL + docCriteriaDTO.getRouteHeaderId().toString();
//
//        String DVURL = Utilities.substituteConfigParameters(docType.getServiceNamespace(), unresolvedDVURL);
//        String link = String.format("<a href=\"%s\" %s>%s</a>", DVURL, linkPopup, title);
//        return link;
//    }
//
//    /**
//     *
//     * @param tripID
//     * @return
//     */
//    public String createAgencySitesLinks(String tripID) {
//        String links = "";
//        if (getMessageFrom(TemKeyConstants.ENABLE_AGENCY_SITES_URL).equals("Y")){
//            String agencySitesURL = getMessageFrom(AGENCY_SITES_URL);
//            String linkPopup = "target=\"_blank\"";
//            boolean passTripID = getMessageFrom(TemKeyConstants.PASS_TRIP_ID_TO_AGENCY_SITES).equals("Y");
//            if(!StringUtils.isEmpty(agencySitesURL)){
//                String[] sites = agencySitesURL.split(";");
//                for (String site : sites){
//                    String[] siteInfo = site.split("=");
//                    String URL = siteInfo[1] + (passTripID?tripID:"");
//                    links += (StringUtils.isEmpty(links)?"":"<br />") + String.format("<a href=\"http://%s\" %s>%s</a>", URL,linkPopup, siteInfo[0]);
//                }
//            }
//        }
//
//        return links;
//    }
//
//    /**
//     * Do not filter IF the current user is
//     *
//     * 1. in the workflow?
//     * 2. also the traveler?
//     * 3. an arranger who created the doc for a traveler they authorize?
//     * 4. a travel Manager?
//     * 5. a TEM Profile Administrator within the traveler's org hierarchy?
//     *
//     * @param docCriteriaDTO
//     * @return
//     */
//    public boolean filterByUser(DocSearchDTO docCriteriaDTO){
//        Person currentUser = GlobalVariables.getUserSession().getPerson();
//        try {
//            TravelDocument document = getDocument(docCriteriaDTO.getRouteHeaderId().toString());
//
//            //check workflow
//            if (isWorkflowApprover(docCriteriaDTO.getRouteHeaderId(), currentUser)){
//                return false;
//            }
//            //check traveler
//            if (currentUser.getPrincipalId().equals(document.getTraveler().getPrincipalId())){
//                return false;
//            }
//            //check Travel Manager
//            if (getTravelDocumentService().isTravelManager(currentUser)){
//                return false;
//            }
//
//            //check if user is an arranger to the document
//            boolean arrangerAccess = getTemRoleService().canAccessTravelDocument(document, currentUser);
//            if (arrangerAccess){
//                return false;
//            }
//
//            //check if user is profile admin on the org
//            TEMProfile profile = getTemProfileService().findTemProfileById(document.getTemProfileId());
//            boolean profileAdminAccess = getTemRoleService().isProfileAdmin(currentUser, ObjectUtils.isNotNull(profile)? profile.getHomeDepartment() : null);
//            if (profileAdminAccess){
//                return false;
//            }
//        }
//        catch (WorkflowException ex) {
//            LOG.error(ex.getMessage(), ex);
//        }
//        return true;
//    }
//
//    /**
//     * Check if the user is in the workflow route log
//     *
//     * @param documentNumber
//     * @param user
//     * @return
//     * @throws WorkflowException
//     */
//    public boolean isWorkflowApprover(String documentNumber, Person user) throws WorkflowException{
//        List<String> approvers = KewApiServiceLocator.getWorkflowDocumentActionsService().getPrincipalIdsInRouteLog(documentNumber, true);
//        for (String principalID : approvers){
//            if (principalID.equals(user.getPrincipalId())){
//                return true;
//            }
//        }
//        return false;
//    }
//
//
//    /**
//     *
//     * @param documentNumber
//     * @return
//     * @throws WorkflowException
//     */
//    public TravelDocument getDocument(String documentNumber) {
//        TravelDocument document = null;
//        try {
//            document = (TravelDocument) getDocumentService().getByDocumentHeaderId(documentNumber);
//        }
//        catch (WorkflowException ex) {
//            LOG.error(ex.getMessage(), ex);
//        }
//        return document;
//    }
//
//    /**
//     *
//     * @param messageType
//     * @return
//     */
//    public String getMessageFrom(final String messageType) {
//        return getConfigurationService().getPropertyValueAsString(messageType);
//    }
//
//    protected DocumentTypeService getDocumentTypeService() {
//        return SpringContext.getBean(DocumentTypeService.class);
//    }
//
//    protected ConfigurationService getConfigurationService() {
//        return SpringContext.getBean(ConfigurationService.class);
//    }
//
//    protected TravelDocumentService getTravelDocumentService() {
//        return SpringContext.getBean(TravelDocumentService.class);
//    }
//
//    protected DocumentService getDocumentService() {
//        return SpringContext.getBean(DocumentService.class);
//    }
//
//    protected TravelService getTravelService() {
//        return SpringContext.getBean(TravelService.class);
//    }
//
//    protected TemProfileService getTemProfileService() {
//        return SpringContext.getBean(TemProfileService.class);
//    }
//
//    protected TEMRoleService getTemRoleService() {
//        return SpringContext.getBean(TEMRoleService.class);
//    }

}
