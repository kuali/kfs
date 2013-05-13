/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.CashSweepModel;
import org.kuali.kfs.module.endow.document.service.CashSweepModelService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class is the service implementation for the CashSweepModelService. This is the default, Kuali provided implementation.
 */
public class CashSweepModelServiceImpl implements CashSweepModelService {
    
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.CashSweepModelService#getByPrimaryKey(java.lang.String)
     */
    public CashSweepModel getByPrimaryKey(Integer cashSweepModelID) {
        CashSweepModel cashSweepModel = null;
        if (StringUtils.isNotBlank(cashSweepModelID.toString())) {
            Map criteria = new HashMap();
            criteria.put(EndowPropertyConstants.CASH_SWEEP_MODEL_ID, cashSweepModelID);

            cashSweepModel = (CashSweepModel) businessObjectService.findByPrimaryKey(CashSweepModel.class, criteria);
        }
        return cashSweepModel;
    }

    /**
     * This method gets the businessObjectService.
     * 
     * @return businessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * This method sets the businessObjectService
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
