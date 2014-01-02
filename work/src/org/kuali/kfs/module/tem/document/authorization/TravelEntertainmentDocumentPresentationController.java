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

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.rice.krad.document.Document;

public class TravelEntertainmentDocumentPresentationController extends TravelDocumentPresentationController {

    /**
     * @see org.kuali.kfs.module.tem.document.authorization.TravelDocumentPresentationController#getEditModes(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        addFullEntryEditMode(document, editModes);
        editModes.remove(TemConstants.EditModes.CHECK_AMOUNT_ENTRY);  // the check amount cannot be edited on entertainment documents

        final Set<String> nodeNames = document.getDocumentHeader().getWorkflowDocument().getNodeNames();
        if (document.getDocumentHeader().getWorkflowDocument().isInitiated() || document.getDocumentHeader().getWorkflowDocument().isSaved() || (nodeNames != null && !nodeNames.isEmpty() && (nodeNames.contains(TemWorkflowConstants.RouteNodeNames.TAX) || nodeNames.contains(TemWorkflowConstants.RouteNodeNames.ENTERTAINMENT_MANAGER)))) {
            editModes.add(TemConstants.EditModes.ACTUAL_EXPENSE_TAXABLE_MODE);
        }

        if (isRootTravelDocument((TravelDocument)document)) {
            editModes.add(TemConstants.EditModes.REQUESTER_LOOKUP_MODE);
        }

        return editModes;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.authorization.TravelDocumentPresentationController#getDocumentManagerApprovalNode()
     */
    @Override
    public String getDocumentManagerApprovalNode(){
        return TemWorkflowConstants.RouteNodeNames.ENTERTAINMENT_MANAGER;
    }

}
