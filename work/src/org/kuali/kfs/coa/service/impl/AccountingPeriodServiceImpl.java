/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.coa.service.impl;

import java.sql.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * This service implementation is the default implementation of the AccountingPeriod service that is delivered with Kuali.
 */
public class AccountingPeriodServiceImpl implements AccountingPeriodService {
    // member data
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountingPeriodServiceImpl.class);
    protected BusinessObjectService businessObjectService;
    protected DateTimeService dateTimeService;

    protected static final Set<String> _invalidPeriodCodes = new TreeSet<String>();

    static {
        _invalidPeriodCodes.add("13");
        _invalidPeriodCodes.add("AB");
        _invalidPeriodCodes.add("BB");
        _invalidPeriodCodes.add("CB");
    }

    /**
     * The default implementation.
     *
     * @see org.kuali.kfs.coa.service.AccountingPeriodService#getAllAccountingPeriods()
     */
    @Override
    @Cacheable(value=AccountingPeriod.CACHE_NAME, key="'{getAllAccountingPeriods}'")
    public Collection<AccountingPeriod> getAllAccountingPeriods() {
        return businessObjectService.findAll(AccountingPeriod.class);
    }

    /**
     * Implements by choosing only accounting periods that are active.
     *
     * @see org.kuali.kfs.coa.service.AccountingPeriodService#getOpenAccountingPeriods()
     */
    @Override
    @Cacheable(value=AccountingPeriod.CACHE_NAME, key="'{getOpenAccountingPeriods}'")
    public Collection<AccountingPeriod> getOpenAccountingPeriods() {
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put(KFSConstants.ACCOUNTING_PERIOD_ACTIVE_INDICATOR_FIELD, Boolean.TRUE);

        return businessObjectService.findMatchingOrderBy(AccountingPeriod.class, map, KFSPropertyConstants.ACCTING_PERIOD_UNIV_FISCAL_PERIOD_END_DATE, true);
    }

    /**
     * This method is a helper method to easily grab an accounting period by looking up it's period and fiscal year
     *
     * @param periodCode
     * @param fiscalYear
     * @return an accounting period
     */
    @Override
    @Cacheable(value=AccountingPeriod.CACHE_NAME, key="#p0+'-'+#p1")
    public AccountingPeriod getByPeriod(String periodCode, Integer fiscalYear) {
        // build up the hashmap to find the accounting period
        HashMap<String,Object> keys = new HashMap<String,Object>();
        keys.put( KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, periodCode);
        keys.put( KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        AccountingPeriod acctPeriod = businessObjectService.findByPrimaryKey(AccountingPeriod.class, keys);
        return acctPeriod;
    }

    /**
     * This method allows for AccountingPeriod retrieval via String date.
     *
     * @param String
     */
    @Override
    public AccountingPeriod getByStringDate(String dateString) {
        AccountingPeriod acctPeriod;
        try {
            acctPeriod = getByDate(dateTimeService.convertToSqlDate(dateString));
        }
        catch (Exception pe) {
            LOG.error("AccountingPeriod getByStringDate unable to convert string " + dateString + " into date.", pe);
            throw new RuntimeException("AccountingPeriod getByStringDate unable to convert string " + dateString + " into date.", pe);
        }
        return acctPeriod;
    }

    /**
     * This method is a helper method to get the current period.
     *
     * @see org.kuali.kfs.coa.service.AccountingPeriodService#getByDate(java.sql.Date)
     */
    @Override
    @Cacheable(value=AccountingPeriod.CACHE_NAME, key="#p0")
    public AccountingPeriod getByDate(Date date) {
        Map<String,Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(KFSPropertyConstants.UNIVERSITY_DATE, date);
        UniversityDate universityDate = businessObjectService.findByPrimaryKey(UniversityDate.class, primaryKeys);
        primaryKeys.clear();
        primaryKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityDate.getUniversityFiscalYear());
        primaryKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, universityDate.getUniversityFiscalAccountingPeriod());
        return businessObjectService.findByPrimaryKey(AccountingPeriod.class, primaryKeys);
    }

    /**
     * This checks to see if the period code is empty or invalid ("13", "AB", "BB", "CB")
     *
     * @param period
     * @return
     */
    protected boolean isInvalidPeriodCode(AccountingPeriod period) {
        String periodCode = period.getUniversityFiscalPeriodCode();
        if (periodCode == null) {
            throw new IllegalArgumentException("invalid (null) universityFiscalPeriodCode (" + periodCode + ")for" + period);
        }
        return _invalidPeriodCodes.contains(periodCode);
    }

    /**
     * @see org.kuali.kfs.coa.service.AccountingPeriodService#compareAccountingPeriodsByDate(org.kuali.kfs.coa.businessobject.AccountingPeriod,
     *      org.kuali.kfs.coa.businessobject.AccountingPeriod)
     */
    @Override
    public int compareAccountingPeriodsByDate(AccountingPeriod tweedleDee, AccountingPeriod tweedleDum) {
        // note the lack of defensive programming here. If you send a null accounting
        // period...then chances are, you deserve the NPE that you receive
        Date tweedleDeeClose = tweedleDee.getUniversityFiscalPeriodEndDate();
        Date tweedleDumClose = tweedleDum.getUniversityFiscalPeriodEndDate();

        return tweedleDeeClose.compareTo(tweedleDumClose);
    }

    @Override
    @CacheEvict(value=AccountingPeriod.CACHE_NAME,allEntries=true)
    public void clearCache() {
        // nothing to do - annotation does it all
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
