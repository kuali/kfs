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
package org.kuali.kfs.coa.dataaccess.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.AccountAutoCreateDefaults;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.dataaccess.AccountAutoCreateDefaultsDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.service.BusinessObjectService;

public class AccountAutoCreateDefaultsDaoOjb extends PlatformAwareDaoBaseOjb implements AccountAutoCreateDefaultsDao {

    private BusinessObjectService businessObjectService;
    
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    public AccountAutoCreateDefaults getByUnit(String unit) {
        
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("kcUnit", unit);
        return (AccountAutoCreateDefaults) businessObjectService.findByPrimaryKey(AccountAutoCreateDefaults.class, criteria);
    }
    
}
