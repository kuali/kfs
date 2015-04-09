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
package org.kuali.kfs.module.ld.service;

import java.util.Collection;
import java.util.Iterator;

import org.kuali.kfs.module.ld.businessobject.PositionObjectBenefit;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KualiTestConstants.TestConstants.PositionObjectTestData;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * JUnit test for LaborPositionObjectBenefitService
 */
@ConfigureContext
public class LaborPositionObjectBenefitServiceTest extends KualiTestBase {

    private LaborPositionObjectBenefitService laborPositionObjectBenefitService;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        laborPositionObjectBenefitService = SpringContext.getBean(LaborPositionObjectBenefitService.class);
    }

    /**
     * Test testGetPositionObjectBenefits_valid() for valid position object benefits
     * 
     * @throws Exception
     */
    public void testGetPositionObjectBenefits_valid() throws Exception {
        Collection<PositionObjectBenefit> results = laborPositionObjectBenefitService.getActivePositionObjectBenefits(Integer.valueOf(PositionObjectTestData.UNIVERSITY_FISCAL_YEAR), PositionObjectTestData.CHART_OF_ACCOUNTS_CODE, PositionObjectTestData.FINANCIAL_OBJECT_CODE);
        super.assertNotNull("Expected position object parameters to return not null.", results);

        for (Iterator iter = results.iterator(); iter.hasNext();) {
            PositionObjectBenefit element = (PositionObjectBenefit) iter.next();
            super.assertEquals(element.getUniversityFiscalYear().intValue(), Integer.valueOf(PositionObjectTestData.UNIVERSITY_FISCAL_YEAR).intValue());
            super.assertEquals(element.getChartOfAccountsCode(), PositionObjectTestData.CHART_OF_ACCOUNTS_CODE);
            super.assertEquals(element.getFinancialObjectCode(), PositionObjectTestData.FINANCIAL_OBJECT_CODE);
        }
    }
}
