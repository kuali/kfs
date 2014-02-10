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
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemConstants.TravelReimbursementStatusCodeKeys;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelReimbursementService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class TravelReimbursementDocumentCustomActionBuilder extends DocumentActionBuilderBase implements TravelDocumentCustomActionBuilder{
    protected volatile TravelDocumentService travelDocumentService;
    protected volatile ConfigurationService configurationService;
    protected volatile TravelReimbursementService travelReimbursementService;

    protected static Logger LOG = Logger.getLogger(TravelReimbursementDocumentCustomActionBuilder.class);

    /**
     * Determines if the URL column for actions should be rendered. This is done for {@link TravelReimbursementDocument} instances
     * in the search results that have a workflow document status of FINAL or PROCESSED and on documents that have a workflow
     * App Doc Status of DEPT_APPROVED. Also checks if the person has permission to initiate the target documents.
     * Will not show the new Reimbursement link if the TR already has a TR enroute.
     * Will not show the new Reimbursement link if the TR is not the root document.
     * Will not show the new Reimbursement link if a TA is required to initiate a TR.
     * Will not show the new Reimbursement link if all the Trip Types require a TA to initiate a TR.
     *
     * @param documentSearchResult
     * @param documentType
     * @param document
     * @return
     */
    private boolean showNewDocumentURL(DocumentSearchResult documentSearchResult, String documentType, TravelDocument document) {
        final DocumentStatus documentStatus = documentSearchResult.getDocument().getStatus();
        final String appDocStatus = documentSearchResult.getDocument().getApplicationDocumentStatus();
        boolean statusCheck = (documentStatus.equals(DocumentStatus.FINAL)
                || (documentStatus.equals(DocumentStatus.PROCESSED)))
                && (appDocStatus.equals(TravelReimbursementStatusCodeKeys.DEPT_APPROVED));

        Person user = GlobalVariables.getUserSession().getPerson();
        boolean hasInitAccess = false;
        if (getTemRoleService().canAccessTravelDocument(document, user) && !ObjectUtils.isNull(document.getTraveler()) && document.getTemProfileId() != null && !ObjectUtils.isNull(document.getTemProfile())){
            //check if user also can init other docs
            hasInitAccess = user.getPrincipalId().equals(document.getTraveler().getPrincipalId()) || getTemRoleService().isTravelDocumentArrangerForProfile(documentType, user.getPrincipalId(), document.getTemProfileId()) || getTemRoleService().isTravelArranger(user, document.getTemProfile().getHomeDepartment() , document.getTemProfileId().toString(), documentType);
        }

        boolean checkRelatedDocs = true;
        boolean originalDocumentCheck = true;
        boolean initiateReimbursementWithoutAuthorization = true;

        if (documentType.equals(TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT)) {
            //don't display link if there are enroute TRs
            List<Document> docs = getTravelDocumentService().getDocumentsRelatedTo(document, documentType);
            for (Document doc : docs) {
                TravelReimbursementDocument trDoc = (TravelReimbursementDocument)doc;
                if (trDoc.getDocumentHeader().getWorkflowDocument().isEnroute()) {
                    checkRelatedDocs &= false;
                }
            }

            //only allow the link to display if the TR is the root document
            originalDocumentCheck = document.isTripProgenitor();

            //only allow the link to display if a TR can be initiated without a TA
            initiateReimbursementWithoutAuthorization = getConfigurationService().getPropertyValueAsBoolean(TemKeyConstants.CONFIG_PROPERTY_REIMBURSEMENT_INITIATELINK_ENABLED);

            //check Trip Types to verify at least one type can initiate TR without TA
            initiateReimbursementWithoutAuthorization &= !getTravelReimbursementService().doAllReimbursementTripTypesRequireTravelAuthorization();
        }

        return statusCheck && hasInitAccess && checkRelatedDocs && originalDocumentCheck && initiateReimbursementWithoutAuthorization;
    }

    /**
     * Other payment is allowed
     *
     * @param documentSearchResult
     * @return
     */
    private boolean otherPaymentMethodsAllowed(DocumentSearchResult documentSearchResult) {
        final DocumentStatus status = documentSearchResult.getDocument().getStatus();
        return getParameterService().getParameterValueAsBoolean(TravelReimbursementDocument.class, TemConstants.TravelReimbursementParameters.VENDOR_PAYMENT_ALLOWED_BEFORE_FINAL_APPROVAL_IND)
                || status.equals(DocumentStatus.FINAL)
                || status.equals(DocumentStatus.PROCESSED);
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
        if (otherPaymentMethodsAllowed(documentSearchResult)) {
            actionsHTML.append(createPaymentsURL(documentSearchResult, tripId));
        }
        return actionsHTML.toString();
    }

    /**
     * @return the default implementation of TEM's TravelDocument Service
     */
    protected TravelDocumentService getTravelDocumentService() {
        if (travelDocumentService == null) {
            travelDocumentService = SpringContext.getBean(TravelDocumentService.class);
        }
        return travelDocumentService;
    }

    /**
     * @return the default implementation of KRAD's Configuration Service
     */
    @Override
    protected ConfigurationService getConfigurationService() {
        if (configurationService == null) {
            configurationService = KRADServiceLocator.getKualiConfigurationService();
        }
        return configurationService;
    }

    protected TravelReimbursementService getTravelReimbursementService() {
        if (travelReimbursementService == null) {
            travelReimbursementService = SpringContext.getBean(TravelReimbursementService.class);
        }

        return travelReimbursementService;
    }

}
