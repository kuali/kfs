/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.chart.service.impl;

import org.kuali.kfs.annotation.NonTransactional;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.dao.SubObjectCodeDao;
import org.kuali.module.chart.service.SubObjectCodeService;
import org.kuali.module.financial.service.UniversityDateService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is the service implementation for the SubObjectCode structure. This is the default implementation that gets delivered
 * with Kuali.
 */

@NonTransactional
public class SubObjectCodeServiceImpl implements SubObjectCodeService {
    private SubObjectCodeDao subObjectCodeDao;
    private UniversityDateService universityDateService;

    /**
     * @see org.kuali.module.chart.service.SubObjectCodeService#getByPrimaryId(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public SubObjCd getByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectCode, String financialSubObjectCode) {
        return subObjectCodeDao.getByPrimaryId(universityFiscalYear, chartOfAccountsCode, accountNumber, financialObjectCode, financialSubObjectCode);
    }

    /**
     * @see org.kuali.module.chart.service.SubObjectCodeService#getByPrimaryIdForCurrentYear(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public SubObjCd getByPrimaryIdForCurrentYear(String chartOfAccountsCode, String accountNumber, String financialObjectCode, String financialSubObjectCode) {
        return this.getByPrimaryId(universityDateService.getCurrentFiscalYear(), chartOfAccountsCode, accountNumber, financialObjectCode, financialSubObjectCode);
    }

    /**
     * 
     * This method injects SubObjectCodeDao
     * @param subObjectCodeDao
     */
    public void setSubObjectCodeDao(SubObjectCodeDao subObjectCodeDao) {
        this.subObjectCodeDao = subObjectCodeDao;
    }

    /**
     * 
     * This method injects UniversityDateService
     * @param universityDateService
     */
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }
}