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
package org.kuali.kfs.sys.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Represents a specific university date
 * 
 */
public class UniversityDate extends PersistableBusinessObjectBase implements FiscalYearBasedBusinessObject {
    static final long serialVersionUID = 2587833750168955556L;

    public static final String CACHE_NAME = KFSConstants.APPLICATION_NAMESPACE_CODE + "/" + "UniversityDate";
    
    private Date universityDate;
    private Integer universityFiscalYear;
    private String universityFiscalAccountingPeriod;

    private AccountingPeriod accountingPeriod;
    private SystemOptions options;

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("universityDate", getUniversityDate());
        return map;
    }

    /**
     * @return Returns the universityDate.
     */
    public Date getUniversityDate() {
        return universityDate;
    }

    /**
     * @param universityDate The universityDate to set.
     */
    public void setUniversityDate(Date universityDate) {
        this.universityDate = universityDate;
    }

    /**
     * @return Returns the universityFiscalAccountingPeriod.
     */
    public String getUniversityFiscalAccountingPeriod() {
        return universityFiscalAccountingPeriod;
    }

    /**
     * @param universityFiscalAccountingPeriod The universityFiscalAccountingPeriod to set.
     */
    public void setUniversityFiscalAccountingPeriod(String universityFiscalAccountingPeriod) {
        this.universityFiscalAccountingPeriod = universityFiscalAccountingPeriod;
    }

    /**
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * @return Returns the accountingPeriod.
     */
    public AccountingPeriod getAccountingPeriod() {
        return accountingPeriod;
    }

    /**
     * @param accountingPeriod The accountingPeriod to set.
     * @deprecated
     */
    public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
        this.accountingPeriod = accountingPeriod;
    }

    /**
     * Gets the options attribute.
     * 
     * @return Returns the options.
     */
    public SystemOptions getOptions() {
        return options;
    }

    /**
     * Sets the options attribute value.
     * 
     * @param options The options to set.
     * @deprecated
     */
    public void setOptions(SystemOptions options) {
        this.options = options;
    }

}
