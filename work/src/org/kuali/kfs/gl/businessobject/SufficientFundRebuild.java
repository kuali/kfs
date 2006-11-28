/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/gl/businessobject/SufficientFundRebuild.java,v $
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

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.module.chart.bo.Chart;

/**
 * 
 */
public class SufficientFundRebuild extends BusinessObjectBase {

    public static final String REBUILD_ACCOUNT = "A";
    public static final String REBUILD_OBJECT = "O";

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
     * @return Returns the chartOfAccountsCode
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
     * @return Returns the accountFinancialObjectTypeCode
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
     * @return Returns the accountNumberFinancialObjectCode
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
     * @return Returns the chart
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
