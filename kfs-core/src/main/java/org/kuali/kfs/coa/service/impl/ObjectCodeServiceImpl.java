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
package org.kuali.kfs.coa.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectCodeCurrent;
import org.kuali.kfs.coa.dataaccess.ObjectCodeDao;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.cache.annotation.Cacheable;

/**
 * This class is the service implementation for the ObjectCode structure. This is the default implementation, that is delivered with
 * Kuali.
 */

@NonTransactional
public class ObjectCodeServiceImpl implements ObjectCodeService {

    protected ObjectCodeDao objectCodeDao;
    protected UniversityDateService universityDateService;
    protected BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.coa.service.ObjectCodeService#getByPrimaryId(java.lang.Integer, java.lang.String, java.lang.String)
     */
    @Override
    @Cacheable(value=ObjectCode.CACHE_NAME, key="#p0+'-'+#p1+'-'+#p2")
    public ObjectCode getByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode) {
        Map<String, Object> keys = new HashMap<String, Object>(3);
        keys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        keys.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, financialObjectCode);
        return SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(ObjectCode.class, keys);
    }

    /**
     * @see org.kuali.kfs.coa.service.ObjectCodeService#getByPrimaryIdWithCaching(java.lang.Integer, java.lang.String,
     *      java.lang.String)
     */
    @Override
    @Cacheable(value=ObjectCode.CACHE_NAME, key="#p0+'-'+#p1+'-'+#p2")
    public ObjectCode getByPrimaryIdWithCaching(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode) {
        ObjectCode objectCode = getByPrimaryId(universityFiscalYear, chartOfAccountsCode, financialObjectCode);
        return objectCode;
    }

    public void setObjectCodeDao(ObjectCodeDao objectCodeDao) {
        this.objectCodeDao = objectCodeDao;
    }
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    /**
     * @see org.kuali.kfs.coa.service.ObjectCodeService#getYearList(java.lang.String, java.lang.String)
     */
    @Override
    public List getYearList(String chartOfAccountsCode, String financialObjectCode) {
        return objectCodeDao.getYearList(chartOfAccountsCode, financialObjectCode);
    }

    /**
     * @see org.kuali.kfs.coa.service.ObjectCodeService#getObjectCodeNamesByCharts(java.lang.Integer, java.lang.String[],
     *      java.lang.String)
     */
    @Override
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
     * @see org.kuali.kfs.coa.service.ObjectCodeService#getByPrimaryIdForCurrentYear(java.lang.String, java.lang.String)
     */
    @Override
    @Cacheable(value=ObjectCode.CACHE_NAME, key="'CurrentFY'+'-'+#p0+'-'+#p1")
    public ObjectCode getByPrimaryIdForCurrentYear(String chartOfAccountsCode, String financialObjectCode) {
        return getByPrimaryId(universityDateService.getCurrentFiscalYear(), chartOfAccountsCode, financialObjectCode);
    }

    @Override
    public List<ObjectCode> getObjectCodesByLevelIds(List<String> levelCodes){
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE, levelCodes);

        List<ObjectCode> results = new ArrayList<ObjectCode>();
        results.addAll(businessObjectService.findMatching(ObjectCode.class, fieldValues));
        return results;
    }

    /**
     * @see org.kuali.kfs.coa.service.ObjectCodeService#doesObjectConsolidationContainObjectCode(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean doesObjectConsolidationContainObjectCode(String chartOfAccountsCode, String consolidationCode, String objectChartOfAccountsCode, String objectCode) {
        Map<String, Object> fieldValues = new HashMap<>();
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, objectChartOfAccountsCode);
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL+"."+KFSPropertyConstants.FINANCIAL_CONSOLIDATION_OBJECT_CODE, consolidationCode);
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL+"."+KFSPropertyConstants.FINANCIAL_CONSOLIDATION_OBJECT+"."+KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        return getBusinessObjectService().countMatching(ObjectCodeCurrent.class, fieldValues) > 0;
    }

    /**
     * @see org.kuali.kfs.coa.service.ObjectCodeService#doesObjectLevelContainObjectCode(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean doesObjectLevelContainObjectCode(String chartOfAccountsCode, String levelCode, String objectChartOfAccountsCode, String objectCode) {
        Map<String, Object> fieldValues = new HashMap<>();
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, objectChartOfAccountsCode);
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE, levelCode);
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL+"."+KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        return getBusinessObjectService().countMatching(ObjectCodeCurrent.class, fieldValues) > 0;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
