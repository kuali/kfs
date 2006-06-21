/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
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

import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.service.AccountingPeriodService;

/**
 * This service implementation is the default implementation of the AccountingPeriod service that is delivered with Kuali.
 * 
 * @author Kuali Financial Transactions Red Team (kualidev@oncourse.iu.edu)
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
    public Collection getOpenAccountingPeriods() {
        HashMap map = new HashMap();
        map.put(Constants.ACCOUNTING_PERIOD_STATUS_CODE_FIELD, Constants.ACCOUNTING_PERIOD_STATUS_OPEN);

        return businessObjectService.findMatchingOrderBy(AccountingPeriod.class, map, PropertyConstants.ACCTING_PERIOD_UNIV_FISCAL_PERIOD_END_DATE, true);
    }

    /**
     * 
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

    private boolean isInvalidPeriodCode(AccountingPeriod period) {
        String periodCode = period.getUniversityFiscalPeriodCode();
        if (periodCode == null) {
            throw new IllegalArgumentException("invalid (null) universityFiscalPeriodCode ("+periodCode+")for" + period);
        }
        return _invalidPeriodCodes.contains(periodCode);
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