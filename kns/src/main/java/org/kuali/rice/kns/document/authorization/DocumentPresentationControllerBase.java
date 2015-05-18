/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.document.authorization;

import java.util.HashSet;
import java.util.Set;

import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * KNS version of the DocumentPresentationControllerBase - adds #getDocumentActions via {@link DocumentPresentationController}
 */
public class DocumentPresentationControllerBase extends org.kuali.rice.krad.document.DocumentPresentationControllerBase implements DocumentPresentationController {
    /**
     * @see DocumentPresentationController#getDocumentActions(Document)
     */
    @Override
    public Set<String> getDocumentActions(Document document){
    	Set<String> documentActions = new HashSet<String>();
    	if (canEdit(document)){
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_EDIT);
    	}
    	
    	if(canAnnotate(document)){
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_ANNOTATE);
    	}
    	 
    	if(canClose(document)){
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_CLOSE);
    	}
    	 
    	if(canSave(document)){
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_SAVE);
    	}
    	if(canRoute(document)){
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_ROUTE);
    	}
    	 
    	if(canCancel(document)){
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_CANCEL);
    	}

        if(canRecall(document)){
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_RECALL);
        }
    	 
    	if(canReload(document)){
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_RELOAD);
    	}
    	if(canCopy(document)){
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_COPY);
    	}
    	if(canPerformRouteReport(document)){
    		documentActions.add(KRADConstants.KUALI_ACTION_PERFORM_ROUTE_REPORT);
    	}
    	
    	if(canAddAdhocRequests(document)){
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_ADD_ADHOC_REQUESTS);
    	}

        // KULRICE-8762: Approve & Blanket Approve should be disabled for a person who is doing COMPLETE action
        boolean canComplete = this.canComplete(document);
        if(!canComplete && canBlanketApprove(document)){
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_BLANKET_APPROVE);
        }
        if (!canComplete && canApprove(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_APPROVE);
        }

    	if (canDisapprove(document)) {
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_DISAPPROVE);
    	}
    	if (canSendAdhocRequests(document)) {
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_SEND_ADHOC_REQUESTS);
    	}
    	if(canSendNoteFyi(document)){
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_SEND_NOTE_FYI);
    	}
    	if(this.canEditDocumentOverview(document)){
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_EDIT_DOCUMENT_OVERVIEW);
    	}
    	if (canFyi(document)) {
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_FYI);
    	}
    	if (canAcknowledge(document)) {
    		documentActions.add(KRADConstants.KUALI_ACTION_CAN_ACKNOWLEDGE);
    	}
        if (canComplete(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_COMPLETE);
        }

    	return documentActions;
    }
}
