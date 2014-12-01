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
