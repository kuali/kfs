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
package org.kuali.kfs.module.tem.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.tem.TemAuthorizationConstants;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class TravelEntertainmentDocumentPresentationController extends TravelDocumentPresentationController {

    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        addFullEntryEntryMode(document, editModes);

        return editModes;
    }

    @Override
    protected void addFullEntryEntryMode(Document document, Set<String> editModes) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if ((workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved())) {
            editModes.add(TemAuthorizationConstants.TravelEditMode.FULL_ENTRY);
        }
    }

    @Override
    public boolean canInitiate(String documentTypeName) {
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        if (!getTravelDocumentService().isTravelArranger(currentUser)) {
            TEMProfile temProfile = getTravelService().findTemProfileByPrincipalId(currentUser.getPrincipalId());
            if (temProfile == null) {
                throw new DocumentInitiationException(TemKeyConstants.ERROR_TRAVEL_DOCUMENT_INITIATION, new String[] { TemConstants.TravelEntertainment.DOCUMENT_NAME }, true);
            }
        }

        return super.canInitiate(documentTypeName);
    }

}
