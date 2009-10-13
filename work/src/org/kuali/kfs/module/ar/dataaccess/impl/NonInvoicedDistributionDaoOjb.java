/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.dataaccess.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.ar.businessobject.NonInvoicedDistribution;
import org.kuali.kfs.module.ar.dataaccess.NonInvoicedDistributionDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.service.BusinessObjectService;

public class NonInvoicedDistributionDaoOjb extends PlatformAwareDaoBaseOjb implements NonInvoicedDistributionDao {
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.NonInvoicedDistributionDao#getNonInvoicedDistributionsForInvoice(java.lang.String)
     */
    public Collection<NonInvoicedDistribution> getNonInvoicedDistributionsForInvoice(String documentNumber) {
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("documentNumber", documentNumber);
        return businessObjectService.findMatching(NonInvoicedDistribution.class, criteria);
    }

    /**
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
}
