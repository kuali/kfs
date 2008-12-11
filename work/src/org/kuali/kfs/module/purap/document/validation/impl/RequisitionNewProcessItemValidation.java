/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.PurapWorkflowConstants.RequisitionDocument.NodeDetailEnum;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.PurApWorkflowIntegrationService;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class RequisitionNewProcessItemValidation extends PurchasingNewProcessItemValidation {

    private RequisitionService requisitionService;
    private PurApWorkflowIntegrationService purapWorkflowIntegrationService;
    
    public boolean validate(AttributedDocumentEvent event) {
        return super.validate(event);
    }
    
    /**
     * Overrides the method in PurchasingAccountsPayableDocumentRuleBase class to check to see if the Requisition is going to stop
     * at content review route level. If so, then this method returns false, otherwise it will call the
     * requiresAccountValidationOnAllEnteredItems of the superclass, which returns true.
     * 
     * @param document the requisition document to be validated
     * @return boolean false when the Requisition is going to stop at content review route level.
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingAccountsPayableDocumentRuleBase#requiresAccountValidationOnAllEnteredItems(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    protected boolean requiresAccountValidationOnAllEnteredItems(PurchasingAccountsPayableDocument document) {
        if (purapWorkflowIntegrationService.willDocumentStopAtGivenFutureRouteNode(document, NodeDetailEnum.CONTENT_REVIEW)) {

            return false;
        }

        return super.requiresAccountValidationOnAllEnteredItems(document);
    }
    
    public RequisitionService getRequisitionService() {
        return requisitionService;
    }

    public void setRequisitionService(RequisitionService requisitionService) {
        this.requisitionService = requisitionService;
    }

    public PurApWorkflowIntegrationService getPurapWorkflowIntegrationService() {
        return purapWorkflowIntegrationService;
    }

    public void setPurapWorkflowIntegrationService(PurApWorkflowIntegrationService purapWorkflowIntegrationService) {
        this.purapWorkflowIntegrationService = purapWorkflowIntegrationService;
    }    

}
