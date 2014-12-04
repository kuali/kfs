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
package org.kuali.kfs.module.tem.document.service.impl;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.module.tem.businessobject.MileageRate;
import org.kuali.kfs.module.tem.document.service.CachingMileageRateService;
import org.kuali.kfs.module.tem.document.service.MileageRateService;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.krad.service.BusinessObjectService;

public class MileageRateServiceImpl implements MileageRateService {
    private CachingMileageRateService cachingMileageRateService;
    private BusinessObjectService businessObjectService;

    @Override
    public MileageRate getMileageRateByExpenseTypeCode(MileageRate mileageRate) {
       final Date fromDate = mileageRate.getActiveFromDate();
       final Date toDate = mileageRate.getActiveToDate();
       Map<String,Object> criteria = new HashMap<String,Object>();
       criteria.put("expenseTypeCode", mileageRate.getExpenseTypeCode());
       List<MileageRate>  mileageRates = (List<MileageRate>) businessObjectService.findMatching(MileageRate.class, criteria);
       for (MileageRate rate : mileageRates) {
           if(!(rate.getId().equals(mileageRate.getId())) && (DateUtils.truncatedCompareTo(fromDate, rate.getActiveToDate(), Calendar.DATE) <= 0  && DateUtils.truncatedCompareTo(toDate, rate.getActiveFromDate() , Calendar.DATE) >= 0)) {
               return rate;
           }
       }

       return null;
    }

    @Override
    public MileageRate findMileageRateByExpenseTypeCodeAndDate(String expenseTypeCode, Date effectiveDate) {
        for (MileageRate mileageRate : cachingMileageRateService.findAllMileageRates()) {
            if ((KfsDateUtils.isSameDay(effectiveDate, mileageRate.getActiveFromDate()) || effectiveDate.after(mileageRate.getActiveFromDate())) && (KfsDateUtils.isSameDay(effectiveDate, mileageRate.getActiveToDate()) || effectiveDate.before(mileageRate.getActiveToDate())) && mileageRate.getExpenseTypeCode().equals(expenseTypeCode)) {
                return mileageRate;
            }
        }

        return null;
    }

    public void setCachingMileageRateService(CachingMileageRateService cachingMileageRateService) {
        this.cachingMileageRateService = cachingMileageRateService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
