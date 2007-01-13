/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.test.fixtures;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.module.chart.bo.ObjectCode;

public enum ObjectCodeFixture {
    OBJECT_CODE_NON_BUDGET_OBJECT_CODE("BL", "3500", 2004),
    OBJECT_CODE_BUDGETED_OBJECT_CODE("BL", "3000", 2004);

    public final Integer universityFiscalYear;
    public final String chartOfAccountsCode;
    public final String financialObjectCode;

    private ObjectCodeFixture(String chartOfAccountsCode,
                              String financialObjectCode, Integer universityFiscalYear)
    {
        this.universityFiscalYear = universityFiscalYear;
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.financialObjectCode = financialObjectCode;
    }

    public ObjectCode createObjectCode() {
        ObjectCode objectCode = new ObjectCode();
        objectCode.setUniversityFiscalYear(this.universityFiscalYear);
        objectCode.setChartOfAccountsCode(this.chartOfAccountsCode);
        objectCode.setFinancialObjectCode(this.financialObjectCode);
        return objectCode;
    }

    public ObjectCode createObjectCode(BusinessObjectService businessObjectService) {
        return (ObjectCode) businessObjectService.retrieve(this.createObjectCode());
    }
}
