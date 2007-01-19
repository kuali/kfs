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
package org.kuali.module.labor.service.impl;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.module.labor.bo.LaborGeneralLedgerEntry;
import org.kuali.module.labor.service.LaborGeneralLedgerEntryService;

/**
 * This class...
 */
public class LaborGeneralLedgerEntryServiceImpl implements LaborGeneralLedgerEntryService {

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.module.labor.service.LaborGeneralLedgerEntryService#getMaxSequenceNumber()
     */
    public Integer getMaxSequenceNumber(LaborGeneralLedgerEntry laborGeneralLedgerEntry) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.module.labor.service.LaborGeneralLedgerEntryService#save(org.kuali.module.labor.bo.LaborGeneralLedgerEntry)
     */
    public void save(LaborGeneralLedgerEntry laborGeneralLedgerEntry) {
        businessObjectService.save(laborGeneralLedgerEntry);
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}