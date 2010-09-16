/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service.impl;

import java.sql.Date;
import java.text.ParseException;
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
import org.kuali.kfs.module.endow.document.service.PooledFundValueService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.KNSConstants;

public class PooledFundValueServiceImpl implements PooledFundValueService {
    private BusinessObjectService businessObjectService;
    private PooledFundValueDao pooledFundValueDao;

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
        String valueEffectiveDate = KNSConstants.EMPTY_STRING;

        try {
            Date valDate = dateTimeService.convertToSqlDate(valuationDate);
            valueEffectiveDate = dateTimeService.toDateString(calculateValueEffectiveDate(valDate, pooledSecurityID));
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
        int incrementDays = pooledFundControl.getIncrementValuationDays().intValue();

        // Calculate the date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(valuationDate);
        calendar.add(Calendar.DATE, incrementDays);
        java.sql.Date valueEffectiveDate = new java.sql.Date(calendar.getTime().getTime());

        return valueEffectiveDate;
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

    public Date getLastestValueEffectiveDate(String pooledSecurityID) {
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

        return pooledFundValueDao.getPooledFundValueWhereSTProcessOnDateIsCurrentDate();
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.PooledFundValueService#getPooledFundValueWhereLTProcessOnDateIsCurrentDate()
     */
    public List<PooledFundValue> getPooledFundValueWhereLTProcessOnDateIsCurrentDate() {

        return pooledFundValueDao.getPooledFundValueWhereLTProcessOnDateIsCurrentDate();
    }

    /**
     * Sets the pooledFundValueDao.
     * 
     * @param pooledFundValueDao
     */
    public void setPooledFundValueDao(PooledFundValueDao pooledFundValueDao) {
        this.pooledFundValueDao = pooledFundValueDao;
    }
}
