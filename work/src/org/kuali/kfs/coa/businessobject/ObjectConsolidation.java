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

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class ObjectConsolidation extends PersistableBusinessObjectBase implements MutableInactivatable {

    /**
     * Default no-arg constructor.
     */
    public ObjectConsolidation() {

    }

    private String chartOfAccountsCode;
    private String finConsolidationObjectCode;
    private String finConsolidationObjectName;
    private String finConsolidationObjShortName;
    private boolean active;
    private String financialReportingSortCode;
    
    private Chart chartOfAccounts;
    
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
     * @return Returns the finConsolidationObjectName
     */
    public String getFinConsolidationObjectName() {
        return finConsolidationObjectName;
    }

    /**
     * Sets the finConsolidationObjectName attribute.
     * 
     * @param finConsolidationObjectName The finConsolidationObjectName to set.
     */
    public void setFinConsolidationObjectName(String finConsolidationObjectName) {
        this.finConsolidationObjectName = finConsolidationObjectName;
    }

    /**
     * Gets the finConsolidationObjShortName attribute.
     * 
     * @return Returns the finConsolidationObjShortName
     */
    public String getFinConsolidationObjShortName() {
        return finConsolidationObjShortName;
    }

    /**
     * Sets the finConsolidationObjShortName attribute.
     * 
     * @param finConsolidationObjShortName The finConsolidationObjShortName to set.
     */
    public void setFinConsolidationObjShortName(String finConsolidationObjShortName) {
        this.finConsolidationObjShortName = finConsolidationObjShortName;
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("finConsolidationObjectCode", this.finConsolidationObjectCode);

        return m;
    }

}
