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
package org.kuali.module.kra.routingform.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.kra.KraPropertyConstants;
import org.kuali.module.kra.routingform.bo.ResearchRiskType;
import org.kuali.module.kra.routingform.bo.RoutingFormResearchRisk;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.kuali.module.kra.routingform.service.RoutingFormResearchRiskService;
import org.springframework.transaction.annotation.Transactional;

public class RoutingFormResearchRiskServiceImpl implements RoutingFormResearchRiskService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RoutingFormResearchRiskServiceImpl.class);

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.module.kra.routingform.service.RoutingFormResearchRiskService#setupResearchRisks(RoutingFormDocument
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
     * @see org.kuali.module.kra.routingform.service.RoutingFormResearchRiskService#getResearchRiskTypes(String[])
     */
    public List<ResearchRiskType> getResearchRiskTypes(String[] exceptCodes) {
        Map criteria = new HashMap();
        criteria.put(KFSPropertyConstants.DATA_OBJECT_MAINTENANCE_CODE_ACTIVE_INDICATOR, true);
        List<ResearchRiskType> allActiveResearchRiskTypes = (List<ResearchRiskType>) this.businessObjectService.findMatchingOrderBy(ResearchRiskType.class, criteria, KraPropertyConstants.RESEARCH_RISK_TYPE_SORT_NUMBER, true);

        List<String> exceptCodesList = Arrays.asList(exceptCodes);
        List<ResearchRiskType> result = new ArrayList<ResearchRiskType>();
        for (ResearchRiskType type : allActiveResearchRiskTypes) {
            if (!exceptCodesList.contains(type.getResearchRiskTypeCode())) {
                result.add(type);
            }
        }
        return result;
    }

    public List<String> getNotificationWorkgroups(String documentNumber) {
        Map fieldValues = new HashMap();
        fieldValues.put("documentNumber", documentNumber);
        List<RoutingFormResearchRisk> researchRisks = new ArrayList<RoutingFormResearchRisk>(businessObjectService.findMatching(RoutingFormResearchRisk.class, fieldValues));
        List<String> workgroups = new ArrayList<String>();
        for (RoutingFormResearchRisk researchRisk : researchRisks) {
            if (!StringUtils.isBlank(researchRisk.getResearchRiskDescription()) || !researchRisk.getResearchRiskStudies().isEmpty()) {
                if (researchRisk.getResearchRiskType().getResearchRiskTypeNotificationGroupText() != null) {
                    workgroups.add(researchRisk.getResearchRiskType().getResearchRiskTypeNotificationGroupText());
                }
            }
        }
        return workgroups;
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
