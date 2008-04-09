/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.ar.service.impl;

import org.kuali.module.ar.bo.SystemInformation;
import org.kuali.module.ar.dao.SystemInformationDao;
import org.kuali.module.ar.service.SystemInformationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class...
 */
@Transactional
public class SystemInformationServiceImpl implements SystemInformationService {

    private SystemInformationDao systemInformationDao;  
    /**
     * @see org.kuali.module.ar.service.SystemInformationService#getByPrimaryKey(java.lang.Integer, java.lang.String, java.lang.String)
     */
    public SystemInformation getByPrimaryKey(Integer univFiscalYear, String chartOfAccountsCode, String organizationCode) {
       return systemInformationDao.getByPrimaryId(univFiscalYear, chartOfAccountsCode, organizationCode);
    }
 
    public SystemInformation getByLockboxNumber(String lockboxNumber) {
        return systemInformationDao.getByLockboxNumber(lockboxNumber);
    }
    
    public SystemInformationDao getSystemInformationDao() {
        return systemInformationDao;
    }
    
    public void setSystemInformationDao(SystemInformationDao systemInformationDao) {
        this.systemInformationDao = systemInformationDao;
    }
    
 
    

}
