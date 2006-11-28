/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/document/validation/impl/RoutingFormDocumentRule.java,v $
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
package org.kuali.module.kra.routingform.rules;

import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.budget.document.ResearchDocument;
import org.kuali.module.kra.budget.rules.ResearchDocumentRuleBase;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;

/**
 * This class...
 * 
 * 
 */
public class RoutingFormDocumentRule extends ResearchDocumentRuleBase {
    /**
     * Checks business rules related to saving a ResearchDocument.
     * 
     * @param ResearchDocument researchDocument
     * @return boolean True if the researchDocument is valid, false otherwise.
     */
    @Override
    public boolean processCustomSaveDocumentBusinessRules(ResearchDocument researchDocument) {
        if (!(researchDocument instanceof RoutingFormDocument)) {
            return false;
        }

        boolean valid = true;

        RoutingFormDocument routingFormDocument = (RoutingFormDocument) researchDocument;

        SpringServiceLocator.getDictionaryValidationService().validateDocumentRecursively(routingFormDocument, 1);

        valid &= GlobalVariables.getErrorMap().isEmpty();
        
        // TODO

        return valid;
    }
}
