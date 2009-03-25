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
package org.kuali.kfs.module.cg.document.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.businessobject.ResearchRiskType;
import org.kuali.kfs.module.cg.businessobject.RoutingFormResearchRisk;
import org.kuali.kfs.module.cg.document.RoutingFormDocument;
import org.kuali.kfs.module.cg.document.service.RoutingFormResearchRiskService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.service.BusinessObjectService;

public class RoutingFormResearchRiskServiceImpl implements RoutingFormResearchRiskService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RoutingFormResearchRiskServiceImpl.class);

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.cg.document.service.RoutingFormResearchRiskService#setupResearchRisks(RoutingFormDocument
     *      routingFormDocument)
     */
    public void setupResearchRisks(RoutingFormDocument routingFormDocument) {
        List<ResearchRiskType> researchRiskTypes = getAllResearchRiskTypes();
        List<RoutingFormResearchRisk> researchRisks = new ArrayList<RoutingFormResearchRisk>();
        for (ResearchRiskType researchRiskType : researchRiskTypes) {
            researchRisks.add(new RoutingFormResearchRisk(routingFormDocument.getDocumentNumber(), researchRiskType));
        }
        routingFormDocument.setRoutingFormResearchRisks(researchRisks);
    }

    /**
     * Get the list of all active research risk types from the database.
     * 
     * @return List<ResearchRiskType>
     */
    private List<ResearchRiskType> getAllResearchRiskTypes() {
        return getResearchRiskTypes(new String[0]);
    }

    /**
     * @see org.kuali.kfs.module.cg.document.service.RoutingFormResearchRiskService#getResearchRiskTypes(String[])
     */
    public List<ResearchRiskType> getResearchRiskTypes(String[] exceptCodes) {
        Map criteria = new HashMap();
        criteria.put(KFSPropertyConstants.ACTIVE, true);
        List<ResearchRiskType> allActiveResearchRiskTypes = (List<ResearchRiskType>) this.businessObjectService.findMatchingOrderBy(ResearchRiskType.class, criteria, CGPropertyConstants.RESEARCH_RISK_TYPE_SORT_NUMBER, true);

        List<String> exceptCodesList = Arrays.asList(exceptCodes);
        List<ResearchRiskType> result = new ArrayList<ResearchRiskType>();
        for (ResearchRiskType type : allActiveResearchRiskTypes) {
            if (!exceptCodesList.contains(type.getResearchRiskTypeCode())) {
                result.add(type);
            }
        }
        return result;
    }

    /**
     * Setter for BusinessObjectService property.
     * 
     * @param businessObjectService businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
