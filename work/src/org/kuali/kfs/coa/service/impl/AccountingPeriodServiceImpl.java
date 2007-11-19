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

import java.sql.Date;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.spring.Cached;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.service.AccountingPeriodService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service implementation is the default implementation of the AccountingPeriod service that is delivered with Kuali.
 */
@Transactional
public class AccountingPeriodServiceImpl implements AccountingPeriodService {
    // member data
    private BusinessObjectService businessObjectService;

    protected static final Set _invalidPeriodCodes = new TreeSet();

    static {
        _invalidPeriodCodes.add("13");
        _invalidPeriodCodes.add("AB");
        _invalidPeriodCodes.add("BB");
        _invalidPeriodCodes.add("CB");
    }

    /**
     * The default implementation.
     * 
     * @see org.kuali.module.chart.service.AccountingPeriodService#getAllAccountingPeriods()
     */
    public Collection getAllAccountingPeriods() {
        return businessObjectService.findAll(AccountingPeriod.class);
    }

    /**
     * Implements by choosing only accounting periods that have a status that is open ("O").
     * 
     * @see org.kuali.module.chart.service.AccountingPeriodService#getOpenAccountingPeriods()
     */
    @Cached
    public Collection getOpenAccountingPeriods() {
        HashMap map = new HashMap();
        map.put(KFSConstants.ACCOUNTING_PERIOD_STATUS_CODE_FIELD, KFSConstants.ACCOUNTING_PERIOD_STATUS_OPEN);

        return businessObjectService.findMatchingOrderBy(AccountingPeriod.class, map, KFSPropertyConstants.ACCTING_PERIOD_UNIV_FISCAL_PERIOD_END_DATE, true);
    }

    /**
     * This method is a helper method to easily grab an accounting period by looking up it's period and fiscal year
     * 
     * @param periodCode
     * @param fiscalYear
     * @return an accounting period
     */
    public AccountingPeriod getByPeriod(String periodCode, Integer fiscalYear) {
        // build up the hashmap to find the accounting period
        Map keys = new HashMap();
        keys.put("universityFiscalPeriodCode", periodCode);
        keys.put("universityFiscalYear", fiscalYear);
        AccountingPeriod acctPeriod = (AccountingPeriod) getBusinessObjectService().findByPrimaryKey(AccountingPeriod.class, keys);
        return acctPeriod;
    }

    /**
     * This method is a helper method to get the current period.
     * 
     * @see org.kuali.module.chart.service.AccountingPeriodService#getByDate(java.sql.Date)
     */
    public AccountingPeriod getByDate(Date date) {
        // first we need to figure out the last day for a given date
        java.util.Date myDate = new java.util.Date(date.getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        // get the current month
        int month = cal.get(Calendar.MONTH);
        cal.set(Calendar.MONTH, month + 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        // this should roll us back to the last day of the month for the previous month
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date lookupDate = new Date(cal.getTimeInMillis());
        Map keys = new HashMap();
        keys.put("universityFiscalPeriodEndDate", lookupDate);
        Collection coll = getBusinessObjectService().findMatching(AccountingPeriod.class, keys);
        if (coll.size() == 1) {
            Iterator iter = coll.iterator();
            AccountingPeriod period = (AccountingPeriod) iter.next();
            return period;
        }
        else {
            Iterator iter = coll.iterator();
            while (iter.hasNext()) {
                AccountingPeriod period = (AccountingPeriod) iter.next();
                if (!isInvalidPeriodCode(period)) {
                    return period;
                }
            }
        }
        return null;
    }

    /**
     * 
     * This checks to see if the period code is empty or invalid ("13", "AB", "BB", "CB")
     * @param period
     * @return
     */
    private boolean isInvalidPeriodCode(AccountingPeriod period) {
        String periodCode = period.getUniversityFiscalPeriodCode();
        if (periodCode == null) {
            throw new IllegalArgumentException("invalid (null) universityFiscalPeriodCode (" + periodCode + ")for" + period);
        }
        return _invalidPeriodCodes.contains(periodCode);
    }

    /**
     * @see org.kuali.module.chart.service.AccountingPeriodService#compareAccountingPeriodsByDate(org.kuali.module.chart.bo.AccountingPeriod,
     *      org.kuali.module.chart.bo.AccountingPeriod)
     */
    public int compareAccountingPeriodsByDate(AccountingPeriod tweedleDee, AccountingPeriod tweedleDum) {
        // note the lack of defensive programming here. If you send a null accounting
        // period...then chances are, you deserve the NPE that you receive
        Date tweedleDeeClose = tweedleDee.getUniversityFiscalPeriodEndDate();
        Date tweedleDumClose = tweedleDum.getUniversityFiscalPeriodEndDate();

        return tweedleDeeClose.compareTo(tweedleDumClose);
    }

    /**
     * This method retrieves an instance of the businessObjectService.
     * 
     * @return
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * This method sets the instance of the businessObjectService.
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}