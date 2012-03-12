/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.coa.batch.dataaccess.impl;

import java.sql.Date;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.batch.dataaccess.impl.FiscalYearMakerImpl;
import org.kuali.kfs.sys.businessobject.FiscalYearBasedBusinessObject;

/**
 * Performs custom population of accounting periods records for a new year being created in the fiscal year maker process
 */
public class AccountingPeriodFiscalYearMakerImpl extends FiscalYearMakerImpl {
    
    public AccountingPeriodFiscalYearMakerImpl() {
        super();
        
        super.setAllowOverrideTargetYear(false);
    }

    /**
     * Updates the year on the fiscal period name and sets status to open for next year records
     * 
     * @see org.kuali.kfs.coa.batch.dataaccess.impl.FiscalYearMakerHelperImpl#changeForNewYear(java.lang.Integer,
     *      org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public void changeForNewYear(Integer baseFiscalYear, FiscalYearBasedBusinessObject currentRecord) {
        super.changeForNewYear(baseFiscalYear, currentRecord);

        AccountingPeriod accountingPeriod = (AccountingPeriod) currentRecord;

        // update fiscal period name which contains the fiscal year
        String fiscalPeriodName = accountingPeriod.getUniversityFiscalPeriodName();

        String oldCalendarStartYear = new Integer(accountingPeriod.getUniversityFiscalYear() - 2).toString();
        String oldCalendarEndYear = new Integer(accountingPeriod.getUniversityFiscalYear() - 1).toString();

        String newCalendarStartYear = new Integer(accountingPeriod.getUniversityFiscalYear() - 1).toString();
        String newCalendarEndYear = new Integer(accountingPeriod.getUniversityFiscalYear()).toString();

        // replace 4 digit year in name if found, else replace 2 digit
        if (StringUtils.contains(fiscalPeriodName, oldCalendarEndYear)) {
            fiscalPeriodName = StringUtils.replace(fiscalPeriodName, oldCalendarEndYear, newCalendarEndYear);
        }
        else if (StringUtils.contains(fiscalPeriodName, oldCalendarStartYear)) {
            fiscalPeriodName = StringUtils.replace(fiscalPeriodName, oldCalendarStartYear, newCalendarStartYear);
        }
        else {
            fiscalPeriodName = updateTwoDigitYear(newCalendarEndYear.substring(2, 4), oldCalendarEndYear.substring(2, 4), fiscalPeriodName);
            fiscalPeriodName = updateTwoDigitYear(newCalendarStartYear.substring(2, 4), oldCalendarStartYear.substring(2, 4), fiscalPeriodName);
        }

        accountingPeriod.setUniversityFiscalPeriodName(fiscalPeriodName);

        // increment period end date by one year
        accountingPeriod.setUniversityFiscalPeriodEndDate(addYearToDate(accountingPeriod.getUniversityFiscalPeriodEndDate()));

        // set status to closed
        accountingPeriod.setActive(false);
    }

    /**
     * Retrieves all Accounting Period records for the first copied fiscal year and make active
     * 
     * @see org.kuali.kfs.coa.batch.dataaccess.impl.FiscalYearMakerHelperImpl#performCustomProcessing(java.lang.Integer)
     */
    @Override
    public void performCustomProcessing(Integer baseFiscalYear, boolean firstCopyYear) {
        if (!firstCopyYear) {
            return;
        }
        Collection<AccountingPeriod> accountingPeriods = businessObjectService.findMatching(AccountingPeriod.class,Collections.singletonMap(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, baseFiscalYear + 1));
        for (AccountingPeriod accountingPeriod : accountingPeriods) {
            accountingPeriod.setActive(true);
            businessObjectService.save(accountingPeriod);
        }
    }

    /**
     * Adds one year to the given date
     * 
     * @param inDate date to increment
     * @return Date incoming date plus one year
     */
    protected java.sql.Date addYearToDate(Date inDate) {
        GregorianCalendar currentCalendarDate = new GregorianCalendar();
        currentCalendarDate.clear();

        currentCalendarDate.setTimeInMillis(inDate.getTime());
        currentCalendarDate.add(GregorianCalendar.YEAR, 1);

        return new Date(currentCalendarDate.getTimeInMillis());
    }

    /**
     * this routine is provided to update string fields which contain two-digit years that need to be updated for display. it is
     * very specific, but it's necessary. "two-digit year" means the two numeric characters preceded by a non-numeric character.
     * 
     * @param newYear
     * @param oldYear
     * @param currentString
     * @return the updated string for a two digit year
     */
    protected String updateTwoDigitYear(String newYear, String oldYear, String currentString) {
        // group 1 is the bounded by the outermost set of parentheses
        // group 2 is the first inner set
        // group 3 is the second inner set--a two-digit year at the beginning of the line
        String regExpString = "(([^0-9]{1}" + oldYear + ")|^(" + oldYear + "))";
        Pattern pattern = Pattern.compile(regExpString);
        Matcher matcher = pattern.matcher(currentString);

        // start looking for a match
        boolean matched = matcher.find();
        if (!matched) {
            // just return if nothing is found
            return currentString;
        }

        // we found something
        // we have to process it
        String returnString = currentString;
        StringBuffer outString = new StringBuffer();
        // is there a match at the beginning of the line (a match with group 3)?
        if (matcher.group(3) != null) {
            // there is a two-digit-year string at the beginning of the line
            // we want to replace it
            matcher.appendReplacement(outString, newYear);
            // find the next match if there is one
            matched = matcher.find();
        }

        while (matched) {
            // the new string will no longer match with group 3
            // if there is still a match, it will be with group 2
            // now we have to prefix the new year string with the same
            // non-numeric character as the next match (hyphen, space, whatever)
            String newYearString = matcher.group(2).substring(0, 1) + newYear;
            matcher.appendReplacement(outString, newYearString);
            matched = matcher.find();
        }

        // dump whatever detritus is left into the new string
        matcher.appendTail(outString);

        return outString.toString();
    }

}
