/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.service.impl;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.module.tem.businessobject.MileageRate;
import org.kuali.kfs.module.tem.dataaccess.MileageRateDao;
import org.kuali.kfs.module.tem.document.service.MileageRateService;

public class MileageRateServiceImpl implements MileageRateService {
    private MileageRateDao mileageRateDao;

    @Override
    public MileageRate getMileageRateByExpenseTypeCode(MileageRate mileageRate) {
           final Date fromDate = mileageRate.getActiveFromDate();
           final Date toDate = mileageRate.getActiveToDate();
           List<MileageRate>  mileageRates = mileageRateDao.findMileageRatesByExpenseTypeCode(mileageRate.getExpenseTypeCode());
           for (MileageRate rate : mileageRates) {

               if(!(rate.getId().equals(mileageRate.getId())) && (DateUtils.truncatedCompareTo(fromDate, rate.getActiveFromDate(), Calendar.DATE) >= 0  || DateUtils.truncatedCompareTo(toDate, rate.getActiveToDate(), Calendar.DATE) <= 0)) {
                   return rate;
               }
           }

           return null;

       }

    @Override
    public MileageRate findMileageRatesByExpenseTypeCodeAndDate(String expenseTypeCode, Date effectiveDate) {
        return mileageRateDao.findMileageRatesByExpenseTypeCodeAndDate(expenseTypeCode, effectiveDate);
    }

    public void setMileageRateDao(MileageRateDao mileageRateDao) {
        this.mileageRateDao = mileageRateDao;
    }



}
