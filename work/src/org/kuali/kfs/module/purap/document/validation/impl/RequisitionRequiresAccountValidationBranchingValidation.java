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
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PurApWorkflowIntegrationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.validation.BranchingValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class RequisitionRequiresAccountValidationBranchingValidation extends BranchingValidation {
    
    public static final String NEEDS_ACCOUNT_VALIDATION = "needsAccountValidation";
    
    private PurApItem itemForValidation;
    private PurApWorkflowIntegrationService purapWorkflowIntegrationService;
    
    @Override
    protected String determineBranch(AttributedDocumentEvent event) {
        //FIXME hjs put this back in once simulation engine is working again KFSMI-2239
        if (!(false) || //purapWorkflowIntegrationService.willDocumentStopAtGivenFutureRouteNode((PurchasingAccountsPayableDocument)event.getDocument(), NodeDetailEnum.CONTENT_REVIEW) ||
            (!itemForValidation.getSourceAccountingLines().isEmpty())) {
            return NEEDS_ACCOUNT_VALIDATION;
        } else {
            return KFSConstants.EMPTY_STRING;
        }
    }

    public PurApItem getItemForValidation() {
        return itemForValidation;
    }

    public void setItemForValidation(PurApItem itemForValidation) {
        this.itemForValidation = itemForValidation;
    }

    public PurApWorkflowIntegrationService getPurapWorkflowIntegrationService() {
        return purapWorkflowIntegrationService;
    }

    public void setPurapWorkflowIntegrationService(PurApWorkflowIntegrationService purapWorkflowIntegrationService) {
        this.purapWorkflowIntegrationService = purapWorkflowIntegrationService;
    }

}
