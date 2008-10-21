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
/*
 * Created on Sep 8, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.kuali.kfs.pdp.service.impl;

import java.util.List;

import org.kuali.kfs.pdp.businessobject.DisbursementNumberRange;
import org.kuali.kfs.pdp.dataaccess.DisbursementNumberRangeDao;
import org.kuali.kfs.pdp.service.DisbursementNumberRangeService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DisbursementNumberRangeServiceImpl implements DisbursementNumberRangeService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementNumberRangeServiceImpl.class);

    private DisbursementNumberRangeDao disbursementNumberRangeDao;
    private BusinessObjectService businessObjectService;
    
    public void setDisbursementNumberRangeDao(DisbursementNumberRangeDao d) {
        disbursementNumberRangeDao = d;
    }

    public List getAll() {
        return (List) this.businessObjectService.findAll(DisbursementNumberRange.class);
    }

    public DisbursementNumberRange get(Integer id) {
        return disbursementNumberRangeDao.get(id);
    }

    public void save(DisbursementNumberRange dnr) {
        this.businessObjectService.save(dnr);
    }
    
    /**
     * Gets the business object service
     * 
     * @return
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }
    
    /**
     * Sets the business object service
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
