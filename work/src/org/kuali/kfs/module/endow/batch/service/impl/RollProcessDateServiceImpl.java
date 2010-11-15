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

import java.util.Date;
import java.text.SimpleDateFormat;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.bo.Parameter;

import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.batch.service.RollProcessDateService;
import org.kuali.kfs.module.endow.document.service.KEMService;

public class RollProcessDateServiceImpl implements RollProcessDateService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UpdateHistoryCashServiceImpl.class);
    private ParameterService parameterService;
    private BusinessObjectService businessObjectService;
    private KEMService kemService;
    
    /**
     * @see org.kuali.kfs.module.endow.batch.service.RollProcessDateService#rollDate()
     */
    public boolean rollDate() {
        int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
        
        boolean success = true;       
        Date currentProcessDate = kemService.getCurrentDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String nextDate = dateFormat.format(currentProcessDate.getTime() + MILLIS_IN_DAY);
        Parameter theCurrentProcessDate = parameterService.retrieveParameter("KFS-ENDOW", "All", EndowParameterKeyConstants.CURRENT_PROCESS_DATE);
        theCurrentProcessDate.setParameterValue(nextDate); 
        businessObjectService.save(theCurrentProcessDate);
        LOG.info("Roll the value of CURRENT_PROCESS_DATE to "+theCurrentProcessDate.getParameterValue());
        return success;
    }
    
    /**
     * Sets the kemService.
     * 
     * @param kemService
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }


    /**
     * Sets the parameterService.
     * 
     * @param parameterService
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
    /**
     * Sets the businessObjectService.
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
