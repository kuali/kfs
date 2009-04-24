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
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.spring.Cached;

/**
 * This service implementation is the default implementation of the AccountingPeriod service that is delivered with Kuali.
 */
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
     * @see org.kuali.kfs.coa.service.AccountingPeriodService#getAllAccountingPeriods()
     */
    public Collection getAllAccountingPeriods() {
        return businessObjectService.findAll(AccountingPeriod.class);
    }

    /**
     * Implements by choosing only accounting periods that are active.
     * 
     * @see org.kuali.kfs.coa.service.AccountingPeriodService#getOpenAccountingPeriods()
     */
    @Cached
    public Collection getOpenAccountingPeriods() {
        HashMap map = new HashMap();
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
    @Cached
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
     * @see org.kuali.kfs.coa.service.AccountingPeriodService#getByDate(java.sql.Date)
     */
    @Cached
    public AccountingPeriod getByDate(Date date) {
        Map primaryKeys = new HashMap();
        primaryKeys.put("universityDate", date);
        UniversityDate universityDate = (UniversityDate) getBusinessObjectService().findByPrimaryKey(UniversityDate.class, primaryKeys);
        primaryKeys.clear();
        primaryKeys.put("universityFiscalYear", universityDate.getUniversityFiscalYear());
        primaryKeys.put("universityFiscalPeriodCode", universityDate.getUniversityFiscalAccountingPeriod());
        return (AccountingPeriod) getBusinessObjectService().findByPrimaryKey(AccountingPeriod.class, primaryKeys); 
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
     * @see org.kuali.kfs.coa.service.AccountingPeriodService#compareAccountingPeriodsByDate(org.kuali.kfs.coa.businessobject.AccountingPeriod,
     *      org.kuali.kfs.coa.businessobject.AccountingPeriod)
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
