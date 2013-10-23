/*
 * Copyright 2012 The Kuali Foundation
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

import java.util.List;

import org.apache.commons.lang.text.StrBuilder;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.authorization.TravelAuthorizationFamilyDocumentPresentationController;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.DocumentPresentationController;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

public class TravelAuthorizationDocumentCustomActionBuilder extends DocumentActionBuilderBase implements TravelDocumentCustomActionBuilder{

    protected static Logger LOG = Logger.getLogger(TravelAuthorizationDocumentCustomActionBuilder.class);

    protected volatile DocumentService documentService;
    protected volatile DocumentHelperService documentHelperService;
    protected volatile TravelDocumentService travelDocumentService;

    /**
     * Determines if the url column for actions should be rendered. This is done for {@link TravelAuthorizationDocument} instances
     * in the search results that have a workflow document status of FINAL or PROCESSED and on documents that do not have a workflow
     * App Doc Status of REIMB_HELD, CANCELLED, PEND_AMENDMENT, CLOSED, or RETIRED_VERSION. Also checks if the person has permission to
     * initiate the target documents. Will not show the new Reimbursement link if a TA already has a TR enroute.
     *
     * check status of document and don't create if the status is not final or processed
     *
     * @param documentSearchResult
     * @param documentType
     * @param document
     * @return
     */
    private boolean showNewDocumentURL(DocumentSearchResult documentSearchResult, String documentType, TravelDocument document) {
        final DocumentStatus documentStatus = documentSearchResult.getDocument().getStatus();
        boolean documentStatusCheck = (documentStatus.equals(DocumentStatus.FINAL)
                                      || documentStatus.equals(DocumentStatus.PROCESSED));

        final String appDocStatus = documentSearchResult.getDocument().getApplicationDocumentStatus();
        boolean appDocStatusCheck = (!appDocStatus.equals(TravelAuthorizationStatusCodeKeys.REIMB_HELD)
                                    && !appDocStatus.equals(TravelAuthorizationStatusCodeKeys.CANCELLED)
                                    && !appDocStatus.equals(TravelAuthorizationStatusCodeKeys.PEND_AMENDMENT)
                                    && !appDocStatus.equals(TravelAuthorizationStatusCodeKeys.RETIRED_VERSION)
                                    && !appDocStatus.equals(TravelAuthorizationStatusCodeKeys.CLOSED));

        boolean statusCheck = documentStatusCheck && appDocStatusCheck;

        Person user = GlobalVariables.getUserSession().getPerson();
        boolean hasInitAccess = false;
        if (getTemRoleService().canAccessTravelDocument(document, user) && document.getTemProfileId() != null){
            //check if user also can init other docs
            hasInitAccess = user.getPrincipalId().equals(document.getTraveler().getPrincipalId()) || getTemRoleService().isTravelDocumentArrangerForProfile(documentType, user.getPrincipalId(), document.getTemProfileId()) || getTemRoleService().isTravelArranger(user, document.getTemProfile().getHomeDepartment() , document.getTemProfileId().toString(), documentType);
        }

        boolean checkRelatedDocs = true;
        if (documentType.equals(TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT)) {

            //check whether there is already an ENROUTE TR
            List<Document> docs = getTravelDocumentService().getDocumentsRelatedTo(document, documentType);
            for (Document doc : docs) {
                TravelReimbursementDocument trDoc = (TravelReimbursementDocument)doc;
                if (trDoc.getDocumentHeader().getWorkflowDocument().isEnroute()) {
                    checkRelatedDocs &= false;
                }
            }

            //a TR document can be processed against a closed TA. If the TAC is Final/Closed display the TR link.
            if (document.getDocumentTypeName().equals(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_CLOSE_DOCUMENT)) {
                documentStatusCheck = documentStatus.equals(DocumentStatus.FINAL);
                appDocStatusCheck = appDocStatus.equals(TravelAuthorizationStatusCodeKeys.CLOSED);
                statusCheck = documentStatusCheck && appDocStatusCheck;
            }
        }

        return statusCheck && hasInitAccess && checkRelatedDocs;
    }

    /**
     * Other payment is allowed
     *
     * @param documentSearchResult
     * @return
     */
    protected boolean canPayVendor(DocumentSearchResult documentSearchResult) {
        try {
            final TravelAuthorizationDocument document = (TravelAuthorizationDocument)getDocumentService().getByDocumentHeaderIdSessionless(documentSearchResult.getDocument().getDocumentId());
            final DocumentPresentationController presController = getDocumentHelperService().getDocumentPresentationController(document);
            if (!(presController instanceof TravelAuthorizationFamilyDocumentPresentationController)) {
                return false; // doesn't use a travel auth family doc presentation controller?  Then it can't pay to dv...
            }
            return ((TravelAuthorizationFamilyDocumentPresentationController)presController).canPayVendor(document);
        }
        catch (WorkflowException we) {
            throw new RuntimeException("Could not open document #"+documentSearchResult.getDocument().getDocumentId());
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.lookup.TravelDocumentCustomActionBuilder#buildCustomActionHTML(org.kuali.rice.kew.api.document.search.DocumentSearchResult, org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public String buildCustomActionHTML(DocumentSearchResult documentSearchResult, TravelDocument document) {
        String tripId = document.getTravelDocumentIdentifier();
        StrBuilder actionsHTML = new StrBuilder();
        actionsHTML.setNewLineText("<br/>");
        if (showNewDocumentURL(documentSearchResult, TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT, document)) {
            actionsHTML.appendln(createReimbursementLink(tripId));
        }

        //last action does not need an additional line break
        if (canPayVendor(documentSearchResult)) {
            actionsHTML.append(createPaymentsURL(documentSearchResult, tripId));
        }

        return actionsHTML.toString();
    }

    /**
     * @return the default implementation of DocumentService
     */
    protected DocumentService getDocumentService() {
        if (documentService == null) {
            documentService = SpringContext.getBean(DocumentService.class);
        }
        return documentService;
    }

    /**
     * @return the default implementation of the DocumentHelperService
     */
    protected DocumentHelperService getDocumentHelperService() {
        if (documentHelperService == null) {
            documentHelperService = SpringContext.getBean(DocumentHelperService.class);
        }
        return documentHelperService;
    }

    protected TravelDocumentService getTravelDocumentService() {
        if (travelDocumentService == null) {
            travelDocumentService = SpringContext.getBean(TravelDocumentService.class);
        }
        return travelDocumentService;
    }
}
