/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectLevel;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * An object level associated with a contracts & grants category
 */
public class CostCategoryObjectLevel extends PersistableBusinessObjectBase implements CostCategoryDetail {
    private String categoryCode;
    private String chartOfAccountsCode;
    private String financialObjectLevelCode;
    private boolean active;

    private Chart chart;
    private ObjectLevel objectLevel;

    @Override
    public String getCategoryCode() {
        return categoryCode;
    }
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }
    @Override
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }
    public String getFinancialObjectLevelCode() {
        return financialObjectLevelCode;
    }
    public void setFinancialObjectLevelCode(String financialObjectLevelCode) {
        this.financialObjectLevelCode = financialObjectLevelCode;
    }
    public Chart getChart() {
        return chart;
    }
    /**
     * @deprecated only ORM should call this method
     */
    @Deprecated
    public void setChart(Chart chart) {
        this.chart = chart;
    }
    public ObjectLevel getObjectLevel() {
        return objectLevel;
    }
    /**
     * @deprecated only ORM should call this method
     */
    @Deprecated
    public void setObjectLevel(ObjectLevel objectLevel) {
        this.objectLevel = objectLevel;
    }
    @Override
    public boolean isActive() {
        return active;
    }
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
}
