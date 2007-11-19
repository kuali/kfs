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
package org.kuali.module.gl.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.bo.Options;
import org.kuali.module.chart.bo.AccountingPeriod;

/**
 * Represents a specific university date
 * 
 */
public class UniversityDate extends PersistableBusinessObjectBase {
    static final long serialVersionUID = 2587833750168955556L;

    private Date universityDate;
    private Integer universityFiscalYear;
    private String universityFiscalAccountingPeriod;

    private AccountingPeriod accountingPeriod;
    private Options options;

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
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
    public Options getOptions() {
        return options;
    }

    /**
     * Sets the options attribute value.
     * 
     * @param options The options to set.
     * @deprecated
     */
    public void setOptions(Options options) {
        this.options = options;
    }

}