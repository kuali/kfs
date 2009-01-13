/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.businessobject.ResearchRiskType;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.ObjectUtils;

public class ResearchRiskTypeMaintenanceDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {

    /**
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyReadOnlyPropertyNames(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> conditionallyReadOnlyPropertyNames = super.getConditionallyReadOnlyPropertyNames(document);

        ResearchRiskType researchRisk = (ResearchRiskType) document.getNewMaintainableObject().getBusinessObject();
        BusinessObjectService service = SpringContext.getBean(BusinessObjectService.class);
        ResearchRiskType persistedResearchRisk = (ResearchRiskType) service.retrieve(researchRisk);

        // If the research risk exists in db, set read-only fields
        if (ObjectUtils.isNotNull(persistedResearchRisk) && persistedResearchRisk.getResearchRiskTypeCode() != null) {
            conditionallyReadOnlyPropertyNames.add(CGPropertyConstants.RESEARCH_RISK_TYPE_DESCRIPTION);
            conditionallyReadOnlyPropertyNames.add(CGPropertyConstants.CONTROL_ATTRIBUTE_TYPE_CODE);
        }

        return conditionallyReadOnlyPropertyNames;
    }
}
