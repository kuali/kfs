/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.fixture;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum ObjectCodeFixture {
    OBJECT_CODE_NON_BUDGET_OBJECT_CODE("BL", "3500"), OBJECT_CODE_BUDGETED_OBJECT_CODE("BL", "3000");

    public final Integer universityFiscalYear;
    public final String chartOfAccountsCode;
    public final String financialObjectCode;

    private ObjectCodeFixture(String chartOfAccountsCode, String financialObjectCode) {
        this.universityFiscalYear = TestUtils.getFiscalYearForTesting();
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
