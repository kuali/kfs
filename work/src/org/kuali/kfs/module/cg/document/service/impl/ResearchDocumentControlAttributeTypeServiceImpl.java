/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.cg.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.cg.businessobject.ControlAttributeType;
import org.kuali.kfs.module.cg.document.service.ResearchDocumentControlAttributeTypeService;
import org.kuali.rice.kns.service.BusinessObjectService;

/**
 * This class is used to access and retrieve ControlAttributeType attributes and related information.
 */
public class ResearchDocumentControlAttributeTypeServiceImpl implements ResearchDocumentControlAttributeTypeService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ResearchDocumentControlAttributeTypeServiceImpl.class);

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.cg.document.service.ResearchDocumentControlAttributeTypeService#getControlAttributeTypeCodes()
     */
    public List<ControlAttributeType> getControlAttributeTypeCodes() {
        Collection<ControlAttributeType> allTypes = new ArrayList();
        allTypes = businessObjectService.findAll(ControlAttributeType.class);
        List<ControlAttributeType> controlAttributeTypes = new ArrayList();
        for (ControlAttributeType type : allTypes) {
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
