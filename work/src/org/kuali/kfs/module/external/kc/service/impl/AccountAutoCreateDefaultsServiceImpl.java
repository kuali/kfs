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
package org.kuali.kfs.module.external.kc.service.impl;

import org.kuali.kfs.module.external.kc.businessobject.AccountAutoCreateDefaults;
import org.kuali.kfs.module.external.kc.dataaccess.AccountAutoCreateDefaultsDao;
import org.kuali.kfs.module.external.kc.service.AccountAutoCreateDefaultsService;

public class AccountAutoCreateDefaultsServiceImpl implements AccountAutoCreateDefaultsService {
    
    private AccountAutoCreateDefaultsDao accountAutoCreateDefaultsDao;
    
    public AccountAutoCreateDefaults getByUnit(String unit) {
        
        //TODO: check unit in the hierarchy if unit is not found
        
        return accountAutoCreateDefaultsDao.getByUnit(unit);
    }

    /**
     * Sets the accountAutoCreateDefaultsDao attribute value.
     * @param accountAutoCreateDefaultsDao The accountAutoCreateDefaultsDao to set.
     */
    public void setAccountAutoCreateDefaultsDao(AccountAutoCreateDefaultsDao accountAutoCreateDefaultsDao) {
        this.accountAutoCreateDefaultsDao = accountAutoCreateDefaultsDao;
    }
        
}
