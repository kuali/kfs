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
package org.kuali.kfs.module.cg.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.cg.businessobject.SubContractor;
import org.kuali.kfs.module.cg.service.SubcontractorService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * Implementation of the Subcontractor service.
 */
public class SubcontractorServiceImpl implements SubcontractorService {

    protected BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.cg.service.SubcontractorService#getByPrimaryId(String)
     */
    @Override
    public SubContractor getByPrimaryId(String subcontractorNumber) {
        return businessObjectService.findByPrimaryKey(SubContractor.class, mapPrimaryKeys(subcontractorNumber));
    }

    protected Map<String, Object> mapPrimaryKeys(String subcontractorNumber) {
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(KFSPropertyConstants.SUBCONTRACTOR_NUMBER, subcontractorNumber.trim());
        return primaryKeys;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
