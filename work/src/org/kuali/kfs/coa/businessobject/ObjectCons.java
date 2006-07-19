package org.kuali.module.chart.bo;

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

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ObjectCons extends BusinessObjectBase {

    /**
     * Default no-arg constructor.
     */
    public ObjectCons() {

    }

    private String chartOfAccountsCode;
    private String finConsolidationObjectCode;
    private String finConsolidationObjectName;
    private String finConsolidationObjShortName;
    private boolean finConsolidationObjActiveIndicator;
    private String financialReportingSortCode;
    private String financialEliminationsObjectCode;

    private Chart chartOfAccounts;
    private ObjectCodeCurrent financialEliminationsObject;

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the financialEliminationsObjectCode attribute.
     * 
     * @return Returns the financialEliminationsObjectCode.
     */
    public String getFinancialEliminationsObjectCode() {
        return financialEliminationsObjectCode;
    }

    /**
     * Sets the financialEliminationsObjectCode attribute value.
     * 
     * @param financialEliminationsObjectCode The financialEliminationsObjectCode to set.
     */
    public void setFinancialEliminationsObjectCode(String financialEliminationsObjectCode) {
        this.financialEliminationsObjectCode = financialEliminationsObjectCode;
    }

    /**
     * Gets the finConsolidationObjectCode attribute.
     * 
     * @return Returns the finConsolidationObjectCode.
     */
    public String getFinConsolidationObjectCode() {
        return finConsolidationObjectCode;
    }

    /**
     * Sets the finConsolidationObjectCode attribute value.
     * 
     * @param finConsolidationObjectCode The finConsolidationObjectCode to set.
     */
    public void setFinConsolidationObjectCode(String finConsolidationObjectCode) {
        this.finConsolidationObjectCode = finConsolidationObjectCode;
    }

    /**
     * Gets the finConsolidationObjectName attribute.
     * 
     * @return - Returns the finConsolidationObjectName
     * 
     */
    public String getFinConsolidationObjectName() {
        return finConsolidationObjectName;
    }

    /**
     * Sets the finConsolidationObjectName attribute.
     * 
     * @param finConsolidationObjectName The finConsolidationObjectName to set.
     * 
     */
    public void setFinConsolidationObjectName(String finConsolidationObjectName) {
        this.finConsolidationObjectName = finConsolidationObjectName;
    }

    /**
     * Gets the finConsolidationObjShortName attribute.
     * 
     * @return - Returns the finConsolidationObjShortName
     * 
     */
    public String getFinConsolidationObjShortName() {
        return finConsolidationObjShortName;
    }

    /**
     * Sets the finConsolidationObjShortName attribute.
     * 
     * @param finConsolidationObjShortName The finConsolidationObjShortName to set.
     * 
     */
    public void setFinConsolidationObjShortName(String finConsolidationObjShortName) {
        this.finConsolidationObjShortName = finConsolidationObjShortName;
    }

    /**
     * Gets the finConsolidationObjActiveIndicator attribute.
     * 
     * @return - Returns the finConsolidationObjActiveIndicator
     * 
     */
    public boolean isFinConsolidationObjActiveIndicator() {
        return finConsolidationObjActiveIndicator;
    }

    /**
     * Sets the finConsolidationObjActiveIndicator attribute.
     * 
     * @param finConsolidationObjActiveIndicator The finConsolidationObjActiveIndicator to set.
     * 
     */
    public void setFinConsolidationObjActiveIndicator(boolean finConsolidationObjActiveIndicator) {
        this.finConsolidationObjActiveIndicator = finConsolidationObjActiveIndicator;
    }

    /**
     * Gets the financialReportingSortCode attribute.
     * 
     * @return - Returns the financialReportingSortCode
     * 
     */
    public String getFinancialReportingSortCode() {
        return financialReportingSortCode;
    }

    /**
     * Sets the financialReportingSortCode attribute.
     * 
     * @param financialReportingSortCode The financialReportingSortCode to set.
     * 
     */
    public void setFinancialReportingSortCode(String financialReportingSortCode) {
        this.financialReportingSortCode = financialReportingSortCode;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return - Returns the chartOfAccounts
     * 
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the financialEliminationsObject attribute. 
     * @return Returns the financialEliminationsObject.
     */
    public ObjectCodeCurrent getFinancialEliminationsObject() {
        return financialEliminationsObject;
    }

    /**
     * Sets the financialEliminationsObject attribute value.
     * @param financialEliminationsObject The financialEliminationsObject to set.
     * @deprecated
     */
    public void setFinancialEliminationsObject(ObjectCodeCurrent financialEliminationsObject) {
        this.financialEliminationsObject = financialEliminationsObject;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("finConsolidationObjectCode", this.finConsolidationObjectCode);

        return m;
    }

}
