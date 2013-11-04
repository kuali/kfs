/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service.impl;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.PooledFundControl;
import org.kuali.kfs.module.endow.businessobject.PooledFundValue;
import org.kuali.kfs.module.endow.dataaccess.PooledFundValueDao;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.PooledFundValueService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PooledFundValueServiceImpl implements PooledFundValueService {
    private BusinessObjectService businessObjectService;
    private PooledFundValueDao pooledFundValueDao;
    protected KEMService kemService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.PooledFundControlService#getByPrimaryKey(java.lang.String)
     */
    public PooledFundValue getByPrimaryKey(String id) {
        PooledFundValue pooledFundValue = null;
        if (StringUtils.isNotBlank(id)) {
            Map criteria = new HashMap();
            criteria.put("pooledSecurityID", id);

            pooledFundValue = (PooledFundValue) businessObjectService.findByPrimaryKey(PooledFundValue.class, criteria);
        }
        return pooledFundValue;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.PooledFundValueService#calculateValueEffectiveDateForAjax(java.lang.String,
     *      java.lang.String)
     */
    public String calculateValueEffectiveDateForAjax(String valuationDate, String pooledSecurityID) {
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        String valueEffectiveDate = null;

        try {
            Date valDate = dateTimeService.convertToSqlDate(valuationDate);
            Date computedValEffectiveDate = calculateValueEffectiveDate(valDate, pooledSecurityID);

            if (computedValEffectiveDate != null) {
                valueEffectiveDate = dateTimeService.toDateString(computedValEffectiveDate);
            }
        }
        catch (ParseException e) {
            // do nothing, the returned value will be empty string
        }
        return valueEffectiveDate;
    }

    public Date calculateValueEffectiveDate(Date valuationDate, String pooledSecurityID) {
        PooledFundControl pooledFundControl = null;
        if (StringUtils.isNotBlank(pooledSecurityID)) {
            Map criteria = new HashMap();
            criteria.put("pooledSecurityID", pooledSecurityID);

            pooledFundControl = (PooledFundControl) businessObjectService.findByPrimaryKey(PooledFundControl.class, criteria);
        }
        if (ObjectUtils.isNotNull(pooledFundControl)) {
            int incrementDays = pooledFundControl.getIncrementValuationDays().intValue();

            // Calculate the date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(valuationDate);
            calendar.add(Calendar.DATE, incrementDays);
            java.sql.Date valueEffectiveDate = new java.sql.Date(calendar.getTime().getTime());
            return valueEffectiveDate;
        }

        return null;
    }

    public void setIncomeDistributionCompleted(List<PooledFundValue> pooledFundValueList, boolean completed) {
        for (PooledFundValue pooledFundValue : pooledFundValueList) {
            pooledFundValue.setIncomeDistributionComplete(completed);
        }
        businessObjectService.save(pooledFundValueList);
    }

    /**
     * This method gets the businessObjectService.
     * 
     * @return businessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * This method sets the businessObjectService
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public boolean isValuationDateTheLatest(String pooledSecurityID, Date theValuationDate) {
        boolean isLatest = true;

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(EndowPropertyConstants.POOL_SECURITY_ID, pooledSecurityID);

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        Collection<PooledFundValue> pooledFundValues = businessObjectService.findMatching(PooledFundValue.class, fieldValues);

        if (pooledFundValues.isEmpty())
            return true;

        Calendar calendar = Calendar.getInstance();
        Calendar theCalendar = Calendar.getInstance();
        theCalendar.setTime(theValuationDate);

        for (PooledFundValue pooledFundValue : pooledFundValues) {
            Date valuationDate = pooledFundValue.getValuationDate();
            calendar.setTime(valuationDate);
            if (theCalendar.before(calendar)) {
                isLatest = false;
                break;
            }
        }

        return isLatest;
    }

    public Date getLatestValueEffectiveDate(String pooledSecurityID) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(EndowPropertyConstants.POOL_SECURITY_ID, pooledSecurityID);

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        Collection<PooledFundValue> pooledFundValues = businessObjectService.findMatching(PooledFundValue.class, fieldValues);

        if (pooledFundValues.isEmpty())
            return null;

        PooledFundValue thePooledFundValue = pooledFundValues.iterator().next();
        Date theLastestValueEffectiveDate = thePooledFundValue.getValueEffectiveDate();

        Calendar calendar = Calendar.getInstance();
        Calendar theLatestCalendar = Calendar.getInstance();
        theLatestCalendar.setTime(theLastestValueEffectiveDate);
        Date valueEffectiveDate = null;
        for (PooledFundValue pooledFundValue : pooledFundValues) {
            valueEffectiveDate = pooledFundValue.getValueEffectiveDate();
            calendar.setTime(valueEffectiveDate);
            if (theLatestCalendar.before(calendar)) {
                theLatestCalendar = calendar;
            }
        }

        return new java.sql.Date(theLatestCalendar.getTime().getTime());

    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.PooledFundValueService#getPooledFundValueWhereSTProcessOnDateIsCurrentDate()
     */
    public List<PooledFundValue> getPooledFundValueWhereSTProcessOnDateIsCurrentDate() {
        // this is a list of pooled fund values sorted by security ID and descending by value effective date
        List<PooledFundValue> fundValues = pooledFundValueDao.getPooledFundValueWhereSTProcessOnDateIsCurrentDate(kemService.getCurrentDate());
        List<PooledFundValue> resultList = new ArrayList<PooledFundValue>();

        String currentSecurity = null;
        // build a list of pooled fund values where we only get the entry with the most recent value effective date per security ID
        if (fundValues != null && fundValues.size() > 0) {

            for (PooledFundValue pooledFundValue : fundValues) {

                if (currentSecurity == null) {
                    currentSecurity = pooledFundValue.getPooledSecurityID();
                    resultList.add(pooledFundValue);
                }
                else {
                    if (!currentSecurity.equalsIgnoreCase(pooledFundValue.getPooledSecurityID())) {
                        currentSecurity = pooledFundValue.getPooledSecurityID();
                        resultList.add(pooledFundValue);
                    }
                }

            }
        }

        return resultList;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.PooledFundValueService#getPooledFundValueWhereLTProcessOnDateIsCurrentDate()
     */
    public List<PooledFundValue> getPooledFundValueWhereLTProcessOnDateIsCurrentDate() {

        // this is a list of pooled fund values sorted by security ID and descending by value effective date
        List<PooledFundValue> fundValues = pooledFundValueDao.getPooledFundValueWhereLTProcessOnDateIsCurrentDate(kemService.getCurrentDate());
        List<PooledFundValue> resultList = new ArrayList<PooledFundValue>();

        String currentSecurity = null;

        // build a list of pooled fund values where we only get the entry with the most recent value effective date per security ID
        if (fundValues != null && fundValues.size() > 0) {

            for (PooledFundValue pooledFundValue : fundValues) {

                if (currentSecurity == null) {
                    currentSecurity = pooledFundValue.getPooledSecurityID();
                    resultList.add(pooledFundValue);
                }
                else {
                    if (!currentSecurity.equalsIgnoreCase(pooledFundValue.getPooledSecurityID())) {
                        currentSecurity = pooledFundValue.getPooledSecurityID();
                        resultList.add(pooledFundValue);
                    }
                }

            }
        }

        return resultList;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.PooledFundValueService#getPooledFundValueWhereDistributionIncomeOnDateIsCurrentDate()
     */
    public List<PooledFundValue> getPooledFundValueWhereDistributionIncomeOnDateIsCurrentDate() {

        return pooledFundValueDao.getPooledFundValueWhereDistributionIncomeOnDateIsCurrentDate(kemService.getCurrentDate());
    }

    /**
     * Sets the pooledFundValueDao.
     * 
     * @param pooledFundValueDao
     */
    public void setPooledFundValueDao(PooledFundValueDao pooledFundValueDao) {
        this.pooledFundValueDao = pooledFundValueDao;
    }

    /**
     * Sets the kemService.
     * 
     * @param kemService
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }
}
