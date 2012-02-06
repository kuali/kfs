/*
 * Copyright 2006 The Kuali Foundation
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
public class IndirectCostRecoveryExclusionType extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String accountIndirectCostRecoveryTypeCode;
    private String chartOfAccountsCode;
    private String financialObjectCode;
    private boolean active; 
    
    private Chart chart;
    private IndirectCostRecoveryType indirectCostRecoveryType;
    private ObjectCode objectCodeCurrent;

    public IndirectCostRecoveryExclusionType() {
        super();
    }

    /**
     * Gets the accountIndirectCostRecoveryTypeCode attribute.
     * 
     * @return Returns the accountIndirectCostRecoveryTypeCode
     */
    public String getAccountIndirectCostRecoveryTypeCode() {
        return accountIndirectCostRecoveryTypeCode;
    }

    /**
     * Sets the accountIndirectCostRecoveryTypeCode attribute.
     * 
     * @param accountIndirectCostRecoveryTypeCode The accountIndirectCostRecoveryTypeCode to set.
     */
    public void setAccountIndirectCostRecoveryTypeCode(String accountIndirectCostRecoveryTypeCode) {
        this.accountIndirectCostRecoveryTypeCode = accountIndirectCostRecoveryTypeCode;
    }


    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the active attribute. 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the chart attribute.
     * 
     * @return Returns the chart
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
    public IndirectCostRecoveryType getIndirectCostRecoveryType() {
        return indirectCostRecoveryType;
    }

    /**
     * @param indirectCostRecoveryType The indirectCostRecoveryType to set.
     * @deprecated
     */
    public void setIndirectCostRecoveryType(IndirectCostRecoveryType indirectCostRecoveryType) {
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("accountIndirectCostRecoveryTypeCode", this.accountIndirectCostRecoveryTypeCode);
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("financialObjectCode", this.financialObjectCode);
        return m;
    }


}
