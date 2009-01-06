/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * Determines permissions for a user to view the buttons on Requisition Document.
 */
public class RequisitionDocumentActionAuthorizer extends PurchasingDocumentActionAuthorizer {

    private RequisitionDocument requisition;
    private Map documentActions;

    /**
     * Constructs a RequisitionDocumentActionAuthorizer.
     * 
     * @param req A RequisitionDocument
     */
    public RequisitionDocumentActionAuthorizer(RequisitionDocument req, Map documentActions) {
        this.requisition = req;
        this.documentActions = documentActions;
        this.docStatus = req.getStatusCode();
        this.documentType = req.getDocumentHeader().getWorkflowDocument().getDocumentType();
    }


    @Override
    public boolean canCalculate() {
        boolean canUserEdit = documentActions.containsKey(KNSConstants.KUALI_ACTION_CAN_EDIT);

        return canUserEdit && (requisition.getStatusCode().equals(PurapConstants.RequisitionStatuses.IN_PROCESS) || 
                requisition.getStatusCode().equals(PurapConstants.RequisitionStatuses.AWAIT_CONTENT_REVIEW) || 
                requisition.getStatusCode().equals(PurapConstants.RequisitionStatuses.AWAIT_FISCAL_REVIEW));
    }
}
