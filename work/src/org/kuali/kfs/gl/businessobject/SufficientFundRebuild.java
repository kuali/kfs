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

package org.kuali.module.gl.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.module.chart.bo.Chart;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class SufficientFundRebuild extends BusinessObjectBase {

    private String chartOfAccountsCode;
    private String accountFinancialObjectTypeCode;
    private String accountNumberFinancialObjectCode;
    private Chart chart;

    /**
     * Default constructor.
     */
    public SufficientFundRebuild() {

    }

    public SufficientFundRebuild(String line) {
        setFromTextFile(line);
    }

    public void setFromTextFile(String line) {

        // Just in case
        line = line + "                   ";

        setChartOfAccountsCode(line.substring(0, 2).trim());
        setAccountFinancialObjectTypeCode(line.substring(2, 3));
        setAccountNumberFinancialObjectCode(line.substring(3, 10).trim());
    }

    public String getLine() {
        StringBuffer sb = new StringBuffer();
        sb.append(getField(2, chartOfAccountsCode));
        sb.append(getField(1, accountFinancialObjectTypeCode));
        sb.append(getField(7, accountNumberFinancialObjectCode));
        return sb.toString();
    }

    private static String SPACES = "          ";

    private String getField(int size, String value) {
        if (value == null) {
            return SPACES.substring(0, size);
        }
        else {
            if (value.length() < size) {
                return value + SPACES.substring(0, size - value.length());
            }
            else {
                return value;
            }
        }
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return - Returns the chartOfAccountsCode
     * 
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     * 
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the accountFinancialObjectTypeCode attribute.
     * 
     * @return - Returns the accountFinancialObjectTypeCode
     * 
     */
    public String getAccountFinancialObjectTypeCode() {
        return accountFinancialObjectTypeCode;
    }

    /**
     * Sets the accountFinancialObjectTypeCode attribute.
     * 
     * @param accountFinancialObjectTypeCode The accountFinancialObjectTypeCode to set.
     * 
     */
    public void setAccountFinancialObjectTypeCode(String accountFinancialObjectTypeCode) {
        this.accountFinancialObjectTypeCode = accountFinancialObjectTypeCode;
    }


    /**
     * Gets the accountNumberFinancialObjectCode attribute.
     * 
     * @return - Returns the accountNumberFinancialObjectCode
     * 
     */
    public String getAccountNumberFinancialObjectCode() {
        return accountNumberFinancialObjectCode;
    }

    /**
     * Sets the accountNumberFinancialObjectCode attribute.
     * 
     * @param accountNumberFinancialObjectCode The accountNumberFinancialObjectCode to set.
     * 
     */
    public void setAccountNumberFinancialObjectCode(String accountNumberFinancialObjectCode) {
        this.accountNumberFinancialObjectCode = accountNumberFinancialObjectCode;
    }


    /**
     * Gets the chart attribute.
     * 
     * @return - Returns the chart
     * 
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * Sets the chart attribute.
     * 
     * @param chart The chart to set.
     * @deprecated
     */
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountFinancialObjectTypeCode", this.accountFinancialObjectTypeCode);
        m.put("accountNumberFinancialObjectCode", this.accountNumberFinancialObjectCode);
        return m;
    }
}
