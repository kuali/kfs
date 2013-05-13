/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCodeCurrent;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class GLLink extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String endowmentTransactionCode;
    private String chartCode;
    private String object;
    private boolean active;

    private EndowmentTransactionCode endowmentTransactionRef;
    private Chart chart;
    private ObjectCodeCurrent financialObjectCode;

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(EndowPropertyConstants.GL_LINK_ETRAN_CD, this.endowmentTransactionCode);
        m.put(EndowPropertyConstants.GL_LINK_CHART_CD, this.chartCode);
        return m;
    }

    /**
     * Gets the chart.
     * 
     * @return chart
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * Sets the chart.
     * 
     * @param chart
     */
    @Deprecated
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    /**
     * Gets the chartCode.
     * 
     * @return chartCode
     */
    public String getChartCode() {
        return chartCode;
    }

    /**
     * Sets the chartCode.
     * 
     * @param chartCode
     */
    public void setChartCode(String chartCode) {
        this.chartCode = chartCode;
    }

    /**
     * Gets the endowmentTransactionRef.
     * 
     * @return endowmentTransactionRef
     */
    public EndowmentTransactionCode getEndowmentTransactionRef() {
        return endowmentTransactionRef;
    }

    /**
     * Sets the endowmentTransactionRef.
     * 
     * @param endowmentTransaction
     */
    @Deprecated
    public void setEndowmentTransactionCode(EndowmentTransactionCode endowmentTransaction) {
        this.endowmentTransactionRef = endowmentTransaction;
    }

    /**
     * Gets the endowmentTransactionCode.
     * 
     * @return endowmentTransactionCode
     */
    public String getEndowmentTransactionCode() {
        return endowmentTransactionCode;
    }

    /**
     * Sets the endowmentTransactionCode.
     * 
     * @param endowmentTransactionCode
     */
    public void setEndowmentTransactionCode(String endowmentTransactionCode) {
        this.endowmentTransactionCode = endowmentTransactionCode;
    }

    /**
     * Gets the financialObjectCode.
     * 
     * @return financialObjectCode
     */
    public ObjectCodeCurrent getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode.
     * 
     * @param financialObjectCode
     */
    @Deprecated
    public void setFinancialObjectCode(ObjectCodeCurrent financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the object.
     * 
     * @return object
     */
    public String getObject() {
        return object;
    }

    /**
     * Sets the object.
     * 
     * @param object
     */
    public void setObject(String object) {
        this.object = object;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#isActive()
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#setActive(boolean)
     */
    public void setActive(boolean active) {
        this.active = active;

    }

}
