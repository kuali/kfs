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

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.bo.SufficientFundRebuild;

/**
 * @author Kuali Nervous System Team ()
 */
public class ObjLevel extends BusinessObjectBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ObjLevel.class);

    private String chartOfAccountsCode;
    private String financialObjectLevelCode;
    private String financialObjectLevelName;
    private String financialObjectLevelShortNm;
    private boolean financialObjectLevelActiveIndicator;
    private String financialReportingSortCode;
    private String financialConsolidationObjectCode;

    private ObjectCons financialConsolidationObject;
    private Chart chartOfAccounts;


    /**
     * Constructs a ObjLevel.java.
     * 
     */
    public ObjLevel() {
        super();
        this.financialConsolidationObject = new ObjectCons();
    }

    /**
     * Gets the financialObjectLevelCode attribute.
     * 
     * @return - Returns the financialObjectLevelCode
     * 
     */
    public String getFinancialObjectLevelCode() {
        return financialObjectLevelCode;
    }

    /**
     * Sets the financialObjectLevelCode attribute.
     * 
     * @param financialObjectLevelCode The financialObjectLevelCode to set.
     * 
     */
    public void setFinancialObjectLevelCode(String financialObjectLevelCode) {
        this.financialObjectLevelCode = financialObjectLevelCode;
    }

    /**
     * Gets the financialObjectLevelName attribute.
     * 
     * @return - Returns the financialObjectLevelName
     * 
     */
    public String getFinancialObjectLevelName() {
        return financialObjectLevelName;
    }

    /**
     * Sets the financialObjectLevelName attribute.
     * 
     * @param financialObjectLevelName The financialObjectLevelName to set.
     * 
     */
    public void setFinancialObjectLevelName(String financialObjectLevelName) {
        this.financialObjectLevelName = financialObjectLevelName;
    }

    /**
     * Gets the financialObjectLevelShortNm attribute.
     * 
     * @return - Returns the financialObjectLevelShortNm
     * 
     */
    public String getFinancialObjectLevelShortNm() {
        return financialObjectLevelShortNm;
    }

    /**
     * Sets the financialObjectLevelShortNm attribute.
     * 
     * @param financialObjectLevelShortNm The financialObjectLevelShortNm to set.
     * 
     */
    public void setFinancialObjectLevelShortNm(String financialObjectLevelShortNm) {
        this.financialObjectLevelShortNm = financialObjectLevelShortNm;
    }

    /**
     * Gets the financialObjectLevelActiveIndicator attribute.
     * 
     * @return - Returns the financialObjectLevelActiveIndicator
     * 
     */
    public boolean isFinancialObjectLevelActiveIndicator() {
        return financialObjectLevelActiveIndicator;
    }

    /**
     * Sets the financialObjectLevelActiveIndicator attribute.
     * 
     * @param financialObjectLevelActiveIndicator The financialObjectLevelActiveIndicator to set.
     * 
     */
    public void setFinancialObjectLevelActiveIndicator(boolean financialObjectLevelActiveIndicator) {
        this.financialObjectLevelActiveIndicator = financialObjectLevelActiveIndicator;
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


    public String getConsolidatedObjectCode() {
        return financialConsolidationObject.getFinancialReportingSortCode();
    }

    /**
     * Gets the financialConsolidationObject attribute.
     * 
     * @return - Returns the financialConsolidationObject
     * 
     */
    public ObjectCons getFinancialConsolidationObject() {
        return financialConsolidationObject;
    }

    /**
     * Sets the financialConsolidationObject attribute.
     * 
     * @param financialConsolidationObject The financialConsolidationObject to set.
     * 
     */
    public void setFinancialConsolidationObject(ObjectCons financialConsolidationObject) {
        this.financialConsolidationObject = financialConsolidationObject;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
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
     * @return Returns the financialConsolidationObjectCode.
     */
    public String getFinancialConsolidationObjectCode() {
        return financialConsolidationObjectCode;
    }

    /**
     * @param financialConsolidationObjectCode The financialConsolidationObjectCode to set.
     */
    public void setFinancialConsolidationObjectCode(String financialConsolidationObjectCode) {
        this.financialConsolidationObjectCode = financialConsolidationObjectCode;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("financialObjectLevelCode", this.financialObjectLevelCode);

        return m;
    }

    public void setChartOfAccountsCode(String chart) {
        this.chartOfAccountsCode = chart;
    }

    @Override
    public void beforeUpdate(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        super.beforeUpdate(persistenceBroker);
        try {
            // KULCOA-549: update the sufficient funds table
            // get the current data from the database
            BusinessObjectService boService = SpringServiceLocator.getBusinessObjectService();
            ObjLevel originalObjLevel = (ObjLevel) boService.retrieve(this);

            if (originalObjLevel != null) {
                if (!originalObjLevel.getFinancialConsolidationObjectCode().equals(getFinancialConsolidationObjectCode())) {
                    SufficientFundRebuild sfr = new SufficientFundRebuild();
                    sfr.setAccountFinancialObjectTypeCode(SufficientFundRebuild.REBUILD_OBJECT);
                    sfr.setChartOfAccountsCode(originalObjLevel.getChartOfAccountsCode());
                    sfr.setAccountNumberFinancialObjectCode(originalObjLevel.getFinancialConsolidationObjectCode());
                    if (boService.retrieve(sfr) == null) {
                        persistenceBroker.store(sfr);
                    }
                    sfr = new SufficientFundRebuild();
                    sfr.setAccountFinancialObjectTypeCode(SufficientFundRebuild.REBUILD_OBJECT);
                    sfr.setChartOfAccountsCode(getChartOfAccountsCode());
                    sfr.setAccountNumberFinancialObjectCode(getFinancialConsolidationObjectCode());
                    if (boService.retrieve(sfr) == null) {
                        persistenceBroker.store(sfr);
                    }
                }
            }
        }
        catch (Exception ex) {
            LOG.error("Problem updating sufficient funds rebuild table: ", ex);
        }
    }

}