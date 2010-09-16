/*
 * Copyright 2010 The Kuali Foundation.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.batch.service.IncomeDistributionForPooledFundService;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.PooledFundValue;
import org.kuali.kfs.module.endow.dataaccess.IncomeDistributionForPooledFundDao;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.ParameterService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class IncomeDistributionForPooledFundServiceImpl implements IncomeDistributionForPooledFundService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IncomeDistributionForPooledFundServiceImpl.class);
    
    protected BusinessObjectService businessObjectService;
    protected DocumentService documentService;
    protected ParameterService parameterService;
    protected KEMService kemService;
    
    protected IncomeDistributionForPooledFundDao incomeDistributionForPooledFundDao;
    
    public boolean createIncomeDistributionForPooledFund() {
        
        LOG.info("Begin Income Distribution for Pooled Fund Transactions ..."); 

        List<HoldingTaxLot>  holdingTaxLotList = incomeDistributionForPooledFundDao.getHoldingTaxLotList();
        
//        Map<String, Object> fieldValues = new HashMap<String, Object>();
//        fieldValues.put("distributeIncomeOnDate", kemService.getCurrentDate());
//        fieldValues.put("incomeDistributionComplete", "N");
//        List<PooledFundValue> pooledFundValues = (List<PooledFundValue>) businessObjectService.findMatching(PooledFundValue.class, fieldValues);
//        if (pooledFundValues == null || pooledFundValues.isEmpty()) {
//            return true;
//        }        
        
        return true;
    }
    

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the documentService attribute value.
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the kemService attribute value.
     * @param kemService The kemService to set.
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

    /**
     * Sets the incomeDistributionForPooledFundDao attribute value.
     * @param incomeDistributionForPooledFundDao The incomeDistributionForPooledFundDao to set.
     */
    public void setIncomeDistributionForPooledFundDao(IncomeDistributionForPooledFundDao incomeDistributionForPooledFundDao) {
        this.incomeDistributionForPooledFundDao = incomeDistributionForPooledFundDao;
    }
    
}
