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
package org.kuali.kfs.module.ar.document.service.impl;

import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.dataaccess.SystemInformationDao;
import org.kuali.kfs.module.ar.document.service.SystemInformationService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class...
 */
@Transactional
public class SystemInformationServiceImpl implements SystemInformationService {

    private SystemInformationDao systemInformationDao;  
    private UniversityDateService universityDateService;
    
    public SystemInformation getByLockboxNumberForCurrentFiscalYear(String lockboxNumber) {
        Integer universityFiscalYear = universityDateService.getCurrentFiscalYear();
        return getByLockboxNumber(lockboxNumber, universityFiscalYear);
    }
    
    public SystemInformation getByLockboxNumber(String lockboxNumber, Integer universityFiscalYear) {
        return systemInformationDao.getByLockboxNumber(lockboxNumber, universityFiscalYear);
    }

    public SystemInformation getByProcessingChartOrgAndFiscalYear(String chartCode, String orgCode, Integer fiscalYear) {
        return getSystemInformationDao().getByProcessingChartOrgAndFiscalYear(chartCode, orgCode, fiscalYear);
    }
    
    public int getCountByChartOrgAndLockboxNumber(String processingChartCode, String processingOrgCode, String lockboxNumber) {
        return getSystemInformationDao().getCountByChartOrgAndLockboxNumber(processingChartCode, processingOrgCode, lockboxNumber);
    }
    
    public SystemInformationDao getSystemInformationDao() {
        return systemInformationDao;
    }
    
    public void setSystemInformationDao(SystemInformationDao systemInformationDao) {
        this.systemInformationDao = systemInformationDao;
    }
    
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

}
