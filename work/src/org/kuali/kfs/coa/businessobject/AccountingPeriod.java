/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.chart.bo;

import java.sql.Date;
import java.util.Calendar;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.bo.user.Options;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.SpringServiceLocator;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AccountingPeriod extends BusinessObjectBase {

    private Integer universityFiscalYear;
    private String universityFiscalPeriodCode;
    private String universityFiscalPeriodName;
    private String universityFiscalPeriodStatusCode; // TODO - should this be another bo?
    private boolean budgetRolloverIndicator;

    private Date universityFiscalPeriodEndDate;
    private Options options;

    /**
     * Default constructor.
     */
    public AccountingPeriod() {

    }

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return - Returns the universityFiscalYear
     * 
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     * 
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }


    /**
     * Gets the universityFiscalPeriodCode attribute.
     * 
     * @return - Returns the universityFiscalPeriodCode
     * 
     */
    public String getUniversityFiscalPeriodCode() {
        return universityFiscalPeriodCode;
    }

    /**
     * Sets the universityFiscalPeriodCode attribute.
     * 
     * @param universityFiscalPeriodCode The universityFiscalPeriodCode to set.
     * 
     */
    public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
        this.universityFiscalPeriodCode = universityFiscalPeriodCode;
    }


    /**
     * Gets the universityFiscalPeriodName attribute.
     * 
     * @return - Returns the universityFiscalPeriodName
     * 
     */
    public String getUniversityFiscalPeriodName() {
        return universityFiscalPeriodName;
    }

    /**
     * Sets the universityFiscalPeriodName attribute.
     * 
     * @param universityFiscalPeriodName The universityFiscalPeriodName to set.
     * 
     */
    public void setUniversityFiscalPeriodName(String universityFiscalPeriodName) {
        this.universityFiscalPeriodName = universityFiscalPeriodName;
    }


    /**
     * Gets the universityFiscalPeriodStatusCode attribute.
     * 
     * @return - Returns the universityFiscalPeriodStatusCode
     * 
     */
    public String getUniversityFiscalPeriodStatusCode() {
        return universityFiscalPeriodStatusCode;
    }

    /**
     * Sets the universityFiscalPeriodStatusCode attribute.
     * 
     * @param universityFiscalPeriodStatusCode The universityFiscalPeriodStatusCode to set.
     * 
     */
    public void setUniversityFiscalPeriodStatusCode(String universityFiscalPeriodStatusCode) {
        this.universityFiscalPeriodStatusCode = universityFiscalPeriodStatusCode;
    }


    /**
     * Gets the budgetRolloverIndicator attribute.
     * 
     * @return - Returns the budgetRolloverIndicator
     * 
     */
    public boolean isBudgetRolloverIndicator() {
        return budgetRolloverIndicator;
    }


    /**
     * Sets the budgetRolloverIndicator attribute.
     * 
     * @param budgetRolloverIndicator The budgetRolloverIndicator to set.
     * 
     */
    public void setBudgetRolloverIndicator(boolean budgetRolloverIndicator) {
        this.budgetRolloverIndicator = budgetRolloverIndicator;
    }


    /**
     * Gets the universityFiscalPeriodEndDate attribute.
     * 
     * @return - Returns the universityFiscalPeriodEndDate
     * 
     */
    public Date getUniversityFiscalPeriodEndDate() {
        return universityFiscalPeriodEndDate;
    }

    /**
     * Sets the universityFiscalPeriodEndDate attribute.
     * 
     * @param universityFiscalPeriodEndDate The universityFiscalPeriodEndDate to set.
     * 
     */
    public void setUniversityFiscalPeriodEndDate(Date universityFiscalPeriodEndDate) {
        this.universityFiscalPeriodEndDate = universityFiscalPeriodEndDate;
    }

    /**
     * @return Returns the options.
     */
    public Options getOptions() {
        return options;
    }

    /**
     * @param options The options to set.
     * @deprecated
     */
    public void setOptions(Options options) {
        this.options = options;
    }

    /**
     * This method returns the month that this period represents
     * 
     * @return the actual month (1 - 12) that this period represents
     */
    public int getMonth() {
        DateTimeService dateTimeService = SpringServiceLocator.getDateTimeService();
        Calendar cal = dateTimeService.getCalendar(new Date(this.universityFiscalPeriodEndDate.getTime()));
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("universityFiscalYear", ""+this.universityFiscalYear);
        m.put("universityFiscalPeriodCode", this.universityFiscalPeriodCode);
        return m;
    }


}
