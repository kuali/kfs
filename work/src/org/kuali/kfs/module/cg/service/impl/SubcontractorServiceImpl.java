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
package org.kuali.module.cg.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.spring.Cached;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.cg.bo.Subcontractor;
import org.kuali.module.cg.service.SubcontractorService;

/**
 * Implementation of the Subcontractor service.
 */
public class SubcontractorServiceImpl implements SubcontractorService {

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.module.cg.service.SubcontractorService#getByPrimaryId(String)
     */
    @Cached
    public Subcontractor getByPrimaryId(String subcontractorNumber) {
        return (Subcontractor) businessObjectService.findByPrimaryKey(Subcontractor.class, mapPrimaryKeys(subcontractorNumber));
    }

    private Map<String, Object> mapPrimaryKeys(String subcontractorNumber) {
        Map<String, Object> primaryKeys = new HashMap();
        primaryKeys.put(KFSPropertyConstants.SUBCONTRACTOR_NUMBER, subcontractorNumber.trim());
        return primaryKeys;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}