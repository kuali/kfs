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

package org.kuali.kfs.coa.businessobject;

import java.sql.Date;
import java.util.Calendar;
import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FiscalYearBasedBusinessObject;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class AccountingPeriod extends PersistableBusinessObjectBase implements MutableInactivatable, FiscalYearBasedBusinessObject {

    public static final String CACHE_NAME = KFSConstants.APPLICATION_NAMESPACE_CODE + "/" + "AccountingPeriod";
    
    private Integer universityFiscalYear;
    private String universityFiscalPeriodCode;
    private String universityFiscalPeriodName;
    private boolean active;
    private boolean budgetRolloverIndicator;

    private Date universityFiscalPeriodEndDate;
    private SystemOptions options;

    /**
     * Default constructor.
     */
    public AccountingPeriod() {

    }

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }


    /**
     * Gets the universityFiscalPeriodCode attribute.
     * 
     * @return Returns the universityFiscalPeriodCode
     */
    public String getUniversityFiscalPeriodCode() {
        return universityFiscalPeriodCode;
    }

    /**
     * Sets the universityFiscalPeriodCode attribute.
     * 
     * @param universityFiscalPeriodCode The universityFiscalPeriodCode to set.
     */
    public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
        this.universityFiscalPeriodCode = universityFiscalPeriodCode;
    }


    /**
     * Gets the universityFiscalPeriodName attribute.
     * 
     * @return Returns the universityFiscalPeriodName
     */
    public String getUniversityFiscalPeriodName() {
        return universityFiscalPeriodName;
    }

    /**
     * Sets the universityFiscalPeriodName attribute.
     * 
     * @param universityFiscalPeriodName The universityFiscalPeriodName to set.
     */
    public void setUniversityFiscalPeriodName(String universityFiscalPeriodName) {
        this.universityFiscalPeriodName = universityFiscalPeriodName;
    }


    /**
     * Gets the active attribute.
     * 
     * @return Returns the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Gets the budgetRolloverIndicator attribute.
     * 
     * @return Returns the budgetRolloverIndicator
     */
    public boolean isBudgetRolloverIndicator() {
        return budgetRolloverIndicator;
    }


    /**
     * Sets the budgetRolloverIndicator attribute.
     * 
     * @param budgetRolloverIndicator The budgetRolloverIndicator to set.
     */
    public void setBudgetRolloverIndicator(boolean budgetRolloverIndicator) {
        this.budgetRolloverIndicator = budgetRolloverIndicator;
    }


    /**
     * Gets the universityFiscalPeriodEndDate attribute.
     * 
     * @return Returns the universityFiscalPeriodEndDate
     */
    public Date getUniversityFiscalPeriodEndDate() {
        return universityFiscalPeriodEndDate;
    }

    /**
     * Sets the universityFiscalPeriodEndDate attribute.
     * 
     * @param universityFiscalPeriodEndDate The universityFiscalPeriodEndDate to set.
     */
    public void setUniversityFiscalPeriodEndDate(Date universityFiscalPeriodEndDate) {
        this.universityFiscalPeriodEndDate = universityFiscalPeriodEndDate;
    }

    /**
     * Determine if the current account period is open
     * 
     * @return true if the accounting period is open; otherwise, false
     */
    public boolean isOpen() {
        return this.isActive();
    }

    /**
     * @return Returns the options.
     */
    public SystemOptions getOptions() {
        return options;
    }

    /**
     * @param options The options to set.
     * @deprecated
     */
    public void setOptions(SystemOptions options) {
        this.options = options;
    }

    /**
     * This method returns the month that this period represents
     * 
     * @return the actual month (1 - 12) that this period represents
     */
    public int getMonth() {
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        Calendar cal = dateTimeService.getCalendar(new Date(this.universityFiscalPeriodEndDate.getTime()));
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("universityFiscalYear", "" + this.universityFiscalYear);
        m.put("universityFiscalPeriodCode", this.universityFiscalPeriodCode);
        return m;
    }

    /**
     * generates a hash code for this accounting period, based on the primary keys of the AccountingPeriod BusinesObject: university
     * fiscal year and university fiscal period code
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((universityFiscalPeriodCode == null) ? 0 : universityFiscalPeriodCode.hashCode());
        result = PRIME * result + ((universityFiscalYear == null) ? 0 : universityFiscalYear.hashCode());
        return result;
    }

    /**
     * determines if two accounting periods are equal, based on the primary keys of the AccountingPeriod BusinesObject: university
     * fiscal year and university fiscal period code
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        // this method was added so that
        // org.kuali.kfs.fp.document.web.struts.AuxiliaryVoucherForm.populateAccountingPeriodListForRendering works properly
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AccountingPeriod other = (AccountingPeriod) obj;
        if (universityFiscalPeriodCode == null) {
            if (other.universityFiscalPeriodCode != null)
                return false;
        }
        else if (!universityFiscalPeriodCode.equals(other.universityFiscalPeriodCode))
            return false;
        if (universityFiscalYear == null) {
            if (other.universityFiscalYear != null)
                return false;
        }
        else if (!universityFiscalYear.equals(other.universityFiscalYear))
            return false;
        return true;
    }
}
