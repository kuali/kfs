/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/document/service/impl/RoutingFormResearchRiskServiceImpl.java,v $
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
package org.kuali.module.kra.routingform.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.PropertyConstants;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.module.kra.routingform.bo.ResearchRiskType;
import org.kuali.module.kra.routingform.bo.RoutingFormResearchRisk;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.kuali.module.kra.routingform.service.RoutingFormResearchRiskService;

public class RoutingFormResearchRiskServiceImpl implements RoutingFormResearchRiskService {

    private BusinessObjectService businessObjectService;
    
    public void setupResearchRisks(RoutingFormDocument routingFormDocument) {
        List<ResearchRiskType> researchRiskTypes = getAllResearchRiskTypes();
        List<RoutingFormResearchRisk> researchRisks = new ArrayList<RoutingFormResearchRisk>();
        for (ResearchRiskType researchRiskType: researchRiskTypes) {
            researchRisks.add(new RoutingFormResearchRisk(routingFormDocument.getDocumentNumber(), researchRiskType));
        }
        routingFormDocument.setRoutingFormResearchRisks(researchRisks);
    }
    
    private List<ResearchRiskType> getAllResearchRiskTypes() {
        Map criteria = new HashMap();
        criteria.put(PropertyConstants.DATA_OBJECT_MAINTENANCE_CODE_ACTIVE_INDICATOR, true);
        List<ResearchRiskType> researchRiskTypes = (List<ResearchRiskType>) getBusinessObjectService().findMatchingOrderBy(
                ResearchRiskType.class, criteria, PropertyConstants.RESEARCH_RISK_TYPE_DESCRIPTION, true);
        return researchRiskTypes;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
