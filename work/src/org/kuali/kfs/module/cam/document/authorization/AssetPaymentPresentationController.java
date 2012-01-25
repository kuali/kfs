/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.cam.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentPresentationControllerBase;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;

/**
 * Presentation Controller for Asset Payment Documents
 */
public class AssetPaymentPresentationController extends AccountingDocumentPresentationControllerBase {

    @Override
    public boolean canEdit(Document document) {
        WorkflowDocument workflowDocument = (WorkflowDocument) document.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.isEnroute()) {
            Set<String> nodeNames = SpringContext.getBean(AssetService.class).getCurrentRouteLevels(workflowDocument);

            if (nodeNames.contains(CamsConstants.RouteLevelNames.PLANT_FUND)) {
                return false;
            }
        }
        return super.canEdit(document);
    }

}
