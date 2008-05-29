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

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.annotation.NonTransactional;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.dao.ObjectCodeDao;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.financial.service.UniversityDateService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is the service implementation for the ObjectCode structure. This is the default implementation, that is delivered with
 * Kuali.
 */

@NonTransactional
public class ObjectCodeServiceImpl implements ObjectCodeService {

    private ObjectCodeDao objectCodeDao;
    private UniversityDateService universityDateService;

    /**
     * 
     * @see org.kuali.module.chart.service.ObjectCodeService#getByPrimaryId(java.lang.Integer, java.lang.String, java.lang.String)
     */
    public ObjectCode getByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode) {
        return objectCodeDao.getByPrimaryId(universityFiscalYear, chartOfAccountsCode, financialObjectCode);
    }

    /**
     * @return ObjectCodeDao
     */
    public ObjectCodeDao getObjectCodeDao() {
        return objectCodeDao;
    }

    /**
     * Injects the ObjectCodeDao
     * @param objectCodeDao
     */
    public void setObjectCodeDao(ObjectCodeDao objectCodeDao) {
        this.objectCodeDao = objectCodeDao;
    }

    /**
     * Gets the universityDateService attribute.
     * 
     * @return Returns the universityDateService.
     */
    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    /**
     * Sets the universityDateService attribute value.
     * 
     * @param universityDateService The universityDateService to set.
     */
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    /**
     * 
     * @see org.kuali.module.chart.service.ObjectCodeService#getYearList(java.lang.String, java.lang.String)
     */
    public List getYearList(String chartOfAccountsCode, String financialObjectCode) {
        return objectCodeDao.getYearList(chartOfAccountsCode, financialObjectCode);
    }

    /**
     * @see org.kuali.module.chart.service.ObjectCodeService#getObjectCodeNamesByCharts(java.lang.Integer, java.lang.String[],
     *      java.lang.String)
     */
    public String getObjectCodeNamesByCharts(Integer universityFiscalYear, String[] chartOfAccountCodes, String financialObjectCode) {
        String onlyObjectCodeName = "";
        SortedSet<String> objectCodeNames = new TreeSet<String>();
        List<String> objectCodeNameList = new ArrayList<String>();
        for (String chartOfAccountsCode : chartOfAccountCodes) {
            ObjectCode objCode = this.getByPrimaryId(universityFiscalYear, chartOfAccountsCode, financialObjectCode);
            if (objCode != null) {
                onlyObjectCodeName = objCode.getFinancialObjectCodeName();
                objectCodeNames.add(objCode.getFinancialObjectCodeName());
                objectCodeNameList.add(chartOfAccountsCode + ": " + objCode.getFinancialObjectCodeName());
            }
            else {
                onlyObjectCodeName = "Not Found";
                objectCodeNameList.add(chartOfAccountsCode + ": Not Found");
            }
        }
        if (objectCodeNames.size() > 1) {
            return StringUtils.join(objectCodeNames.toArray(), ", ");
        }
        else {
            return onlyObjectCodeName;
        }
    }

    /**
     * @see org.kuali.module.chart.service.ObjectCodeService#getByPrimaryIdForCurrentYear(java.lang.String, java.lang.String)
     */
    public ObjectCode getByPrimaryIdForCurrentYear(String chartOfAccountsCode, String financialObjectCode) {
        return this.getByPrimaryId(universityDateService.getCurrentFiscalYear(), chartOfAccountsCode, financialObjectCode);
    }
}
