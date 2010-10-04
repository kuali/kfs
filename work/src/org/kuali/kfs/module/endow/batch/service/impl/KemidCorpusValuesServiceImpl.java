/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.batch.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.batch.service.KemidCorpusValueService;
import org.kuali.kfs.module.endow.businessobject.EndowmentCorpusValues;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.util.KEMCalculationRoundingHelper;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * This class is the service implementation for the KemidCorpusValuesService. This is the default, Kuali provided implementation.
 */
public class KemidCorpusValuesServiceImpl implements KemidCorpusValueService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KemidCorpusValuesServiceImpl.class);

    private BusinessObjectService businessObjectService;
    protected KEMService kemService;
    
    /**
     * @see KemidCorpusValueService#KemidExists(String)
     */
    public boolean canFeeBeChargedToKemid(String kemid, KualiDecimal corpusPctTolerance) {
        Map primaryKey = new HashMap();
        primaryKey.put(EndowPropertyConstants.KEMID, kemid);
        
        EndowmentCorpusValues endowmentCorpusValue = (EndowmentCorpusValues) businessObjectService.findByPrimaryKey(EndowmentCorpusValues.class, primaryKey);
        
        if (endowmentCorpusValue.getCurrentPrincipalMarketValue().divide(endowmentCorpusValue.getEndowmentCorpus(), true).isLessThan(corpusPctTolerance)) {
            // does not need the fee to be charged...
            return false;
        }
        
        return true;
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

    /**
     * Gets the kemService.
     * @return kemService
     */
    protected KEMService getKemService() {
        return kemService;
    }

    /**
     * Sets the kemService.
     * @param kemService
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

}
