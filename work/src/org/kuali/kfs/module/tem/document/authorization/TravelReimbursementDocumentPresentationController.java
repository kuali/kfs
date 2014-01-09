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

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelReimbursementService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Travel Reimbursement Document Presentation Controller
 *
 */
public class TravelReimbursementDocumentPresentationController extends TravelDocumentPresentationController {

    public static Logger LOG = Logger.getLogger(TravelReimbursementDocumentPresentationController.class);

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        addFullEntryEditMode(document, editModes);
        editModes.remove(TemConstants.EditModes.CHECK_AMOUNT_ENTRY);  // the check amount cannot be edited on travel reimbursements
        editModes.remove(TemConstants.EditModes.BLANKET_TRAVEL_ENTRY);
        final Set<String> nodeNames = document.getDocumentHeader().getWorkflowDocument().getNodeNames();
        if (document.getDocumentHeader().getWorkflowDocument().isInitiated() || document.getDocumentHeader().getWorkflowDocument().isSaved() || (nodeNames != null && !nodeNames.isEmpty() && (nodeNames.contains(TemWorkflowConstants.RouteNodeNames.TAX) || nodeNames.contains(TemWorkflowConstants.RouteNodeNames.AP_TRAVEL)))) {
            editModes.add(TemConstants.EditModes.EXPENSE_TAXABLE_MODE);
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
        return super.getDocumentActions(document);
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

        return super.canInitiate(documentTypeName);
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

    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }

    protected TravelReimbursementService getTravelReimbursementService() {
        return SpringContext.getBean(TravelReimbursementService.class);
    }

}
