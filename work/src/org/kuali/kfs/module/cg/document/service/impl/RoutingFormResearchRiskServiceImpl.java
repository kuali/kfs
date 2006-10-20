/*
 * Copyright 2005-2006 The Kuali Foundation.
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

import java.util.List;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.module.kra.routingform.bo.ResearchRiskType;
import org.kuali.module.kra.routingform.service.RoutingFormResearchRiskService;

public class RoutingFormResearchRiskServiceImpl implements RoutingFormResearchRiskService {

    private BusinessObjectService businessObjectService;
    
    public List<ResearchRiskType> getAllResearchRiskTypes() {
        
        List<ResearchRiskType> researchRiskTypes = (List<ResearchRiskType>)getBusinessObjectService().findAll(ResearchRiskType.class);
        return researchRiskTypes;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
