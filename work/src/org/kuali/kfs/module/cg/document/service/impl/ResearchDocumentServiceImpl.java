/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.kra.service.impl;

import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.budget.service.BudgetService;
import org.kuali.module.kra.document.ResearchDocument;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.kuali.module.kra.routingform.service.RoutingFormService;
import org.kuali.module.kra.service.ResearchDocumentService;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;

@Transactional
public class ResearchDocumentServiceImpl implements ResearchDocumentService {

    private BudgetService budgetService;
    private RoutingFormService routingFormService;

    /**
     * @see org.kuali.module.kra.budget.service.ResearchDocumentService#prepareResearchDocumentForSave(org.kuali.module.kra.budget.document.ResearchDocument)
     */
    public void prepareResearchDocumentForSave(ResearchDocument researchDocument) throws WorkflowException {
        if (researchDocument instanceof BudgetDocument) {
            BudgetDocument budgetDocument = (BudgetDocument) researchDocument;
            budgetService.prepareBudgetForSave(budgetDocument);
            budgetDocument.setForceRefreshOfBOSubListsForSave(false);
        }
        else if (researchDocument instanceof RoutingFormDocument) {
            RoutingFormDocument routingFormDocument = (RoutingFormDocument) researchDocument;
            routingFormService.prepareRoutingFormForSave(routingFormDocument);
        }
    }

    public void setBudgetService(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    public void setRoutingFormService(RoutingFormService routingFormService) {
        this.routingFormService = routingFormService;
    }
}
