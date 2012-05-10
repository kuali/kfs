/*
 * Copyright 2005-2006 The Kuali Foundation
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

import java.util.LinkedHashMap;

import org.kuali.kfs.gl.businessobject.SufficientFundRebuild;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * 
 */
public class ObjectLevel extends PersistableBusinessObjectBase implements MutableInactivatable {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ObjectLevel.class);

    private String chartOfAccountsCode;
    private String financialObjectLevelCode;
    private String financialObjectLevelName;
    private String financialObjectLevelShortNm;
    private boolean active;
    private String financialReportingSortCode;
    private String financialConsolidationObjectCode;

    private ObjectConsolidation financialConsolidationObject;
    private Chart chartOfAccounts;


    /**
     * Constructs a ObjLevel.java.
     */
    public ObjectLevel() {
        super();
        this.financialConsolidationObject = new ObjectConsolidation();
    }

    /**
     * Gets the financialObjectLevelCode attribute.
     * 
     * @return Returns the financialObjectLevelCode
     */
    public String getFinancialObjectLevelCode() {
        return financialObjectLevelCode;
    }

    /**
     * Sets the financialObjectLevelCode attribute.
     * 
     * @param financialObjectLevelCode The financialObjectLevelCode to set.
     */
    public void setFinancialObjectLevelCode(String financialObjectLevelCode) {
        this.financialObjectLevelCode = financialObjectLevelCode;
    }

    /**
     * Gets the financialObjectLevelName attribute.
     * 
     * @return Returns the financialObjectLevelName
     */
    public String getFinancialObjectLevelName() {
        return financialObjectLevelName;
    }

    /**
     * Sets the financialObjectLevelName attribute.
     * 
     * @param financialObjectLevelName The financialObjectLevelName to set.
     */
    public void setFinancialObjectLevelName(String financialObjectLevelName) {
        this.financialObjectLevelName = financialObjectLevelName;
    }

    /**
     * Gets the financialObjectLevelShortNm attribute.
     * 
     * @return Returns the financialObjectLevelShortNm
     */
    public String getFinancialObjectLevelShortNm() {
        return financialObjectLevelShortNm;
    }

    /**
     * Sets the financialObjectLevelShortNm attribute.
     * 
     * @param financialObjectLevelShortNm The financialObjectLevelShortNm to set.
     */
    public void setFinancialObjectLevelShortNm(String financialObjectLevelShortNm) {
        this.financialObjectLevelShortNm = financialObjectLevelShortNm;
    }

    /**
     * Gets the financialObjectLevelActiveIndicator attribute.
     * 
     * @return Returns the financialObjectLevelActiveIndicator
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the financialObjectLevelActiveIndicator attribute.
     * 
     * @param financialObjectLevelActiveIndicator The financialObjectLevelActiveIndicator to set.
     */
    public void setActive(boolean financialObjectLevelActiveIndicator) {
        this.active = financialObjectLevelActiveIndicator;
    }

    /**
     * Gets the financialReportingSortCode attribute.
     * 
     * @return Returns the financialReportingSortCode
     */
    public String getFinancialReportingSortCode() {
        return financialReportingSortCode;
    }

    /**
     * Sets the financialReportingSortCode attribute.
     * 
     * @param financialReportingSortCode The financialReportingSortCode to set.
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
     * @return Returns the financialConsolidationObject
     */
    public ObjectConsolidation getFinancialConsolidationObject() {
        return financialConsolidationObject;
    }

    /**
     * Sets the financialConsolidationObject attribute.
     * 
     * @param financialConsolidationObject The financialConsolidationObject to set.
     */
    public void setFinancialConsolidationObject(ObjectConsolidation financialConsolidationObject) {
        this.financialConsolidationObject = financialConsolidationObject;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
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

    public void setChartOfAccountsCode(String chart) {
        this.chartOfAccountsCode = chart;
    }

    @Override protected void preUpdate() {
        super.preUpdate();
        try {
            // KULCOA-549: update the sufficient funds table
            // get the current data from the database
            BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
            ObjectLevel originalObjLevel = (ObjectLevel) boService.retrieve(this);

            if (originalObjLevel != null) {
                if (!originalObjLevel.getFinancialConsolidationObjectCode().equals(getFinancialConsolidationObjectCode())) {
                    SufficientFundRebuild sfr = new SufficientFundRebuild();
                    sfr.setAccountFinancialObjectTypeCode(SufficientFundRebuild.REBUILD_OBJECT);
                    sfr.setChartOfAccountsCode(originalObjLevel.getChartOfAccountsCode());
                    sfr.setAccountNumberFinancialObjectCode(originalObjLevel.getFinancialConsolidationObjectCode());
                    if (boService.retrieve(sfr) == null) {
                        boService.save(sfr);
                    }
                    sfr = new SufficientFundRebuild();
                    sfr.setAccountFinancialObjectTypeCode(SufficientFundRebuild.REBUILD_OBJECT);
                    sfr.setChartOfAccountsCode(getChartOfAccountsCode());
                    sfr.setAccountNumberFinancialObjectCode(getFinancialConsolidationObjectCode());
                    if (boService.retrieve(sfr) == null) {
                        boService.save(sfr);
                    }
                }
            }
        }
        catch (Exception ex) {
            LOG.error("Problem updating sufficient funds rebuild table: ", ex);
        }
    }

}
