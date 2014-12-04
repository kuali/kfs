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
package org.kuali.kfs.module.endow.batch.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.batch.service.KemidCorpusValueService;
import org.kuali.kfs.module.endow.businessobject.EndowmentCorpusValues;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class is the service implementation for the KemidCorpusValuesService. This is the default, Kuali provided implementation.
 */
public class KemidCorpusValuesServiceImpl implements KemidCorpusValueService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KemidCorpusValuesServiceImpl.class);

    private BusinessObjectService businessObjectService;
    protected KEMService kemService;
    
    /**
     * @see KemidCorpusValueService#canFeeBeChargedToKemid(String)
     */
    public boolean canFeeBeChargedToKemid(String kemid, KualiDecimal corpusPctTolerance) {
        Map primaryKey = new HashMap();
        primaryKey.put(EndowPropertyConstants.KEMID, kemid);
        
        EndowmentCorpusValues endowmentCorpusValue = (EndowmentCorpusValues) businessObjectService.findByPrimaryKey(EndowmentCorpusValues.class, primaryKey);
        if (ObjectUtils.isNull(endowmentCorpusValue)) {
            return false;
        }
        
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
