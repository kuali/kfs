/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.authorization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemConstants.TravelReimbursementStatusCodeKeys;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelReimbursementService;
import org.kuali.kfs.module.tem.document.web.struts.TravelReimbursementForm;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Travel Reimbursement Document Presentation Controller
 *
 */
public class TravelReimbursementDocumentPresentationController extends TravelDocumentPresentationController {

    public static Logger LOG = Logger.getLogger(TravelReimbursementDocumentPresentationController.class);

    protected volatile static BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        addFullEntryEditMode(document, editModes);
        editModes.remove(TemConstants.EditModes.CHECK_AMOUNT_ENTRY);  // the check amount cannot be edited on travel reimbursements
        editModes.remove(TemConstants.EditModes.BLANKET_TRAVEL_ENTRY);
        editModes.remove(TemConstants.EditModes.BLANKET_TRAVEL_VIEW);
        final Set<String> nodeNames = document.getDocumentHeader().getWorkflowDocument().getNodeNames();
        if (document.getDocumentHeader().getWorkflowDocument().isInitiated() || document.getDocumentHeader().getWorkflowDocument().isSaved() || (nodeNames != null && !nodeNames.isEmpty() && (nodeNames.contains(TemWorkflowConstants.RouteNodeNames.TAX) || nodeNames.contains(TemWorkflowConstants.RouteNodeNames.AP_TRAVEL)))) {
            editModes.add(TemConstants.EditModes.EXPENSE_TAXABLE_MODE);
        }

        if (document.getDocumentHeader().getWorkflowDocument().isInitiated() || document.getDocumentHeader().getWorkflowDocument().isSaved() || (nodeNames != null && !nodeNames.isEmpty() && nodeNames.contains(TemWorkflowConstants.RouteNodeNames.AP_TRAVEL))) {
            editModes.add(TemConstants.EditModes.EXPENSE_LIMIT_ENTRY);
        }

        return editModes;
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getDocumentActions(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getDocumentActions(Document document) {
        TravelReimbursementDocument tr = (TravelReimbursementDocument)document;
        TravelAuthorizationDocument ta = null;
        ta = getTravelDocumentService().findCurrentTravelAuthorization(tr);

        if (ta != null){
            if(ta.getDelinquentAction() != null && ta.getDelinquentAction().equals(TemConstants.DELINQUENT_STOP) && !ta.getDelinquentTRException()){
                throw new DocumentInitiationException(TemKeyConstants.ERROR_AUTHORIZATION_TR_DELINQUENT, new String[] { TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT }, true);
            }
        }

        Set<String> actions = super.getDocumentActions(document);
        if (canNewReimbursement(tr)) {
            actions.add(TemConstants.TravelAuthorizationActions.CAN_NEW_REIMBURSEMENT);
        }

        return actions;
    }

    /**
     * Determines if a reimbursement can be initiated for this document. This is done for {@link TravelReimbursementDocument} instances
     * that have a workflow document status of FINAL or PROCESSED and on documents that have a workflow
     * App Doc Status of DEPT_APPROVED. Also checks if the person has permission to initiate the target documents.
     * Will not show the new Reimbursement link if the TR already has a TR enroute.
     * Will not show the new Reimbursement link if the TR is not the root document.
     * Will not show the new Reimbursement link if a TA is required to initiate a TR.
     * Will not show the new Reimbursement link if all the Trip Types require a TA to initiate a TR.
     *
     * @param document
     * @return
     */
    public boolean canNewReimbursement(TravelReimbursementDocument document) {
        final String documentType = document.getDocumentTypeName();

        final String appDocStatus = document.getApplicationDocumentStatus();
        boolean statusCheck = (document.getDocumentHeader().getWorkflowDocument().isProcessed() || document.getDocumentHeader().getWorkflowDocument().isFinal())
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
     * @see org.kuali.rice.krad.document.DocumentPresentationControllerBase#canInitiate(java.lang.String)
     */
    @Override
    public boolean canInitiate(String documentTypeName) {
        //only allow if a TR can be initiated without a TA
        boolean initiateReimbursementWithoutAuthorization = getConfigurationService().getPropertyValueAsBoolean(TemKeyConstants.CONFIG_PROPERTY_REIMBURSEMENT_INITIATELINK_ENABLED);
        //check Trip Types to verify at least one type can initiate TR without TA
        initiateReimbursementWithoutAuthorization &= !getTravelReimbursementService().doAllReimbursementTripTypesRequireTravelAuthorization();
        if (!initiateReimbursementWithoutAuthorization) {
            throw new DocumentInitiationException(TemKeyConstants.ERROR_TA_REQUIRED_FOR_TR_INIT,new String[] {},true);
        }

        KualiForm form = KNSGlobalVariables.getKualiForm();
        if (form instanceof TravelReimbursementForm) {
            final TravelReimbursementForm reimbForm = (TravelReimbursementForm)form;
            if (!StringUtils.isBlank(reimbForm.getTravelDocumentIdentifier())) {
                // we're basing this document off of another document; let's look for any other TR's in the trip to verify they are not enroute
                final List<TravelReimbursementDocument> trDocsInTrip = getTravelReimbursementsInTrip(reimbForm.getTravelDocumentIdentifier());
                if (!trDocsInTrip.isEmpty()) {
                    for (TravelReimbursementDocument trDoc : trDocsInTrip) {
                        if (StringUtils.equals(trDoc.getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode(), KFSConstants.DocumentStatusCodes.ENROUTE)) {
                            throw new DocumentInitiationException(TemKeyConstants.ERROR_TR_ENROUTE_DURING_TR_INIT, new String[] {reimbForm.getTravelDocumentIdentifier(), trDoc.getDocumentNumber()}, true);
                        }
                    }
                }
            }
        }

        return super.canInitiate(documentTypeName);
    }

    /**
     * Look up any TravelReimbursementDocuments associated with the given trip
     * @param tripId the travel document identifier for the trip we're thinking about initiating a new TR for
     * @return a List of any existing TravelReimbursementDocuments in that trip
     */
    protected List<TravelReimbursementDocument> getTravelReimbursementsInTrip(String tripId) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(TemPropertyConstants.TRAVEL_DOCUMENT_IDENTIFIER, tripId);
        Collection<TravelReimbursementDocument> trDocs = getBusinessObjectService().findMatching(TravelReimbursementDocument.class, fieldValues);
        List<TravelReimbursementDocument> trDocsList = new ArrayList<TravelReimbursementDocument>();
        if (!trDocs.isEmpty()) {
            trDocsList.addAll(trDocs);
        }
        return trDocsList;
    }

    /**
     * @see org.kuali.rice.krad.document.DocumentPresentationControllerBase#canCopy(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canCopy(Document document) {
        boolean canCopy = super.canCopy(document);

        //1. TR has been saved
        if (canCopy) {
            canCopy = !isDocumentInitiated(document);
        }

        //2. TR was not created from a TA
        if (canCopy) {
            canCopy = !isReimbursementChildOfAuthorization(document);
        }

        return canCopy;
    }

    protected boolean isDocumentInitiated(Document document) {
        return document.getDocumentHeader().getWorkflowDocument().isInitiated();
    }

    protected boolean isReimbursementChildOfAuthorization(Document document) {
        TravelReimbursementDocument tr = (TravelReimbursementDocument)document;
        List<TravelAuthorizationDocument> travelAuthorizations = getTravelDocumentService().findAuthorizationDocuments(tr.getTravelDocumentIdentifier());

        if (ObjectUtils.isNotNull(travelAuthorizations) && !travelAuthorizations.isEmpty()) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }

    protected TravelReimbursementService getTravelReimbursementService() {
        return SpringContext.getBean(TravelReimbursementService.class);
    }

    protected BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }
}
