/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
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
