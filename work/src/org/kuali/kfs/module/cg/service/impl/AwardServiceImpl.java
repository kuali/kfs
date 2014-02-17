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

import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.service.AwardService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * Implementation of the Award service.
 */
public class AwardServiceImpl implements AwardService {

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.cg.service.AwardService#getByPrimaryId(String)
     */
    @Override
    public Award getByPrimaryId(Long proposalNumber) {
        return businessObjectService.findByPrimaryKey(Award.class, mapPrimaryKeys(proposalNumber));
    }

    protected Map<String, Object> mapPrimaryKeys(Long proposalNumber) {
        Map<String, Object> primaryKeys = new HashMap();
        primaryKeys.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        return primaryKeys;
    }

    /**
     * Sets the BusinessObjectService. Provides Spring compatibility.
     *
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}