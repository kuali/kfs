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
import org.kuali.kfs.coa.businessobject.ObjectConsolidation;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class CostCategoryObjectConsolidation extends PersistableBusinessObjectBase implements CostCategoryDetail {
    private String categoryCode;
    private String chartOfAccountsCode;
    private String finConsolidationObjectCode;

    private Chart chart;
    private ObjectConsolidation objectConsolidation;

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
    public String getFinConsolidationObjectCode() {
        return finConsolidationObjectCode;
    }
    public void setFinConsolidationObjectCode(String finConsolidationObjectCode) {
        this.finConsolidationObjectCode = finConsolidationObjectCode;
    }
    public Chart getChart() {
        return chart;
    }
    public void setChart(Chart chart) {
        this.chart = chart;
    }
    public ObjectConsolidation getObjectConsolidation() {
        return objectConsolidation;
    }
    public void setObjectConsolidation(ObjectConsolidation objectConsolidation) {
        this.objectConsolidation = objectConsolidation;
    }
}