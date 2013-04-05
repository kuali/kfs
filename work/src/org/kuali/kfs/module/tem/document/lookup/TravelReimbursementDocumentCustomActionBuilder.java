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

import org.apache.commons.lang.text.StrBuilder;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemConstants.TravelReimbursementStatusCodeKeys;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.GlobalVariables;

public class TravelReimbursementDocumentCustomActionBuilder extends DocumentActionBuilderBase implements TravelDocumentCustomActionBuilder{

    protected static Logger LOG = Logger.getLogger(TravelReimbursementDocumentCustomActionBuilder.class);

    /**
     * This method determines whether a reimbursement has been completely approved.
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
        boolean hasInitAccess = true;
        if (getTemRoleService().canAccessTravelDocument(document, user) && document.getTemProfileId() != null){
            //check if user also can init other docs
            hasInitAccess = user.getPrincipalId().equals(document.getTraveler().getPrincipalId()) || getTemRoleService().isTravelDocumentArrangerForProfile(documentType, user.getPrincipalId(), document.getTemProfileId());
        }
        return statusCheck && hasInitAccess;
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
        if (showNewDocumentURL(documentSearchResult, TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT, document)) {
            actionsHTML.appendln(createEntertainmentLink(tripId, documentSearchResult));
        }
        if (showNewDocumentURL(documentSearchResult, TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT, document)) {
            actionsHTML.appendln(createReimbursementLink(tripId));
        }
        //last action does not need an additional line break
        if (otherPaymentMethodsAllowed(documentSearchResult)) {
            actionsHTML.append(createPaymentsURL(documentSearchResult, tripId));
        }
        return actionsHTML.toString();
    }

}
