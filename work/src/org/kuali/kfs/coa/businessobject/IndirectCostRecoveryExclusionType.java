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

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.module.chart.bo.codes.ICRTypeCode;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class IndirectCostRecoveryExclusionType extends BusinessObjectBase {

    private String accountIndirectCostRecoveryTypeCode;
    private String chartOfAccountsCode;
    private String financialObjectCode;
    private Chart chart;
    private ICRTypeCode indirectCostRecoveryType;
    private ObjectCode objectCodeCurrent;

    public IndirectCostRecoveryExclusionType() {
        super();
    }

    /**
     * Gets the accountIndirectCostRecoveryTypeCode attribute.
     * 
     * @return - Returns the accountIndirectCostRecoveryTypeCode
     * 
     */
    public String getAccountIndirectCostRecoveryTypeCode() {
        return accountIndirectCostRecoveryTypeCode;
    }

    /**
     * Sets the accountIndirectCostRecoveryTypeCode attribute.
     * 
     * @param accountIndirectCostRecoveryTypeCode The accountIndirectCostRecoveryTypeCode to set.
     * 
     */
    public void setAccountIndirectCostRecoveryTypeCode(String accountIndirectCostRecoveryTypeCode) {
        this.accountIndirectCostRecoveryTypeCode = accountIndirectCostRecoveryTypeCode;
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
     * Gets the financialObjectCode attribute.
     * 
     * @return - Returns the financialObjectCode
     * 
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     * 
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
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
     * @return Returns the indirectCostRecoveryType.
     */
    public ICRTypeCode getIndirectCostRecoveryType() {
        return indirectCostRecoveryType;
    }

    /**
     * @param indirectCostRecoveryType The indirectCostRecoveryType to set.
     * @deprecated
     */
    public void setIndirectCostRecoveryType(ICRTypeCode indirectCostRecoveryType) {
        this.indirectCostRecoveryType = indirectCostRecoveryType;
    }

    /**
     * @return Returns the objectCode.
     */
    public ObjectCode getObjectCodeCurrent() {
        return objectCodeCurrent;
    }

    /**
     * @param objectCode The objectCode to set.
     * @deprecated
     */
    public void setObjectCodeCurrent(ObjectCode objectCodeCurrent) {
        this.objectCodeCurrent = objectCodeCurrent;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("accountIndirectCostRecoveryTypeCode", this.accountIndirectCostRecoveryTypeCode);
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("financialObjectCode", this.financialObjectCode);
        return m;
    }


}
