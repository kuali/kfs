/*
 * Copyright 2007 The Kuali Foundation.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.module.kra.routingform.bo.ControlAttributeType;
import org.kuali.module.kra.service.ResearchDocumentControlAttributeTypeService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is used to access and retrieve ControlAttributeType attributes and related information.
 */
@Transactional
public class ResearchDocumentControlAttributeTypeServiceImpl implements ResearchDocumentControlAttributeTypeService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ResearchDocumentControlAttributeTypeServiceImpl.class);
    
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.module.kra.service.ResearchDocumentControlAttributeTypeService#getControlAttributeTypeCodes()
     */
    public List<ControlAttributeType> getControlAttributeTypeCodes() {
        Collection<ControlAttributeType> allTypes = new ArrayList();
        allTypes = businessObjectService.findAll(ControlAttributeType.class);
        List<ControlAttributeType> controlAttributeTypes = new ArrayList();
        for(ControlAttributeType type: allTypes) {
            controlAttributeTypes.add(type);
        }
        return controlAttributeTypes;
    }

    /**
     * Setter for BusinessObjectService property.
     * 
     * @param BusinessObjectService businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
