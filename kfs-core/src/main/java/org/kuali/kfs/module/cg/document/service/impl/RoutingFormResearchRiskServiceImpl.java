/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.cg.document.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.businessobject.ResearchRiskType;
import org.kuali.kfs.module.cg.document.service.RoutingFormResearchRiskService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.service.BusinessObjectService;

public class RoutingFormResearchRiskServiceImpl implements RoutingFormResearchRiskService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RoutingFormResearchRiskServiceImpl.class);

    private BusinessObjectService businessObjectService;

  

    /**
     * Get the list of all active research risk types from the database.
     * 
     * @return List<ResearchRiskType>
     */
    protected List<ResearchRiskType> getAllResearchRiskTypes() {
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
