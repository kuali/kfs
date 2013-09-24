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

import java.util.Set;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Document;

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

        final Set<String> nodeNames = document.getDocumentHeader().getWorkflowDocument().getNodeNames();
        if (document.getDocumentHeader().getWorkflowDocument().isInitiated() || document.getDocumentHeader().getWorkflowDocument().isSaved() || (nodeNames != null && !nodeNames.isEmpty() && (nodeNames.contains(TemWorkflowConstants.RouteNodeNames.TAX) || nodeNames.contains(TemWorkflowConstants.RouteNodeNames.AP_TRAVEL)))) {
            editModes.add(TemConstants.EditModes.ACTUAL_EXPENSE_TAXABLE_MODE);
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
        try {
            ta = getTravelDocumentService().findCurrentTravelAuthorization(tr);
        }
        catch (WorkflowException ex) {
            LOG.error(ex);
        }

        if (ta != null){
            if(ta.getDelinquentAction() != null && ta.getDelinquentAction().equals(TemConstants.DELINQUENT_STOP) && !ta.getDelinquentTRException()){
                throw new DocumentInitiationException(TemKeyConstants.ERROR_AUTHORIZATION_TR_DELINQUENT, new String[] { TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT }, true);
            }
        }
        return super.getDocumentActions(document);
    }

    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }

}
