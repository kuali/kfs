/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.service;

import java.util.Collection;
import java.util.Iterator;

import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.labor.bo.PositionObjectBenefit;
import org.kuali.module.labor.service.impl.LaborPositionObjectBenefitServiceImpl;
import org.kuali.test.ConfigureContext;
import org.kuali.test.KualiTestConstants.TestConstants.PositionObjectTestData;

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
        laborPositionObjectBenefitService = SpringServiceLocator.getLaborPositionObjectBenefitService();
    }

    /**
     * Test testGetPositionObjectBenefits_valid() for valid position object benefits
     * 
     * @throws Exception
     */
    public void testGetPositionObjectBenefits_valid() throws Exception {
        Collection<PositionObjectBenefit> results = laborPositionObjectBenefitService.getPositionObjectBenefits(Integer.valueOf(PositionObjectTestData.UNIVERSITY_FISCAL_YEAR), PositionObjectTestData.CHART_OF_ACCOUNTS_CODE, PositionObjectTestData.FINANCIAL_OBJECT_CODE);
        super.assertNotNull("Expected position object parameters to return not null.", results);

        for (Iterator iter = results.iterator(); iter.hasNext();) {
            PositionObjectBenefit element = (PositionObjectBenefit) iter.next();
            super.assertEquals(element.getUniversityFiscalYear().intValue(), Integer.valueOf(PositionObjectTestData.UNIVERSITY_FISCAL_YEAR).intValue());
            super.assertEquals(element.getChartOfAccountsCode(), PositionObjectTestData.CHART_OF_ACCOUNTS_CODE);
            super.assertEquals(element.getFinancialObjectCode(), PositionObjectTestData.FINANCIAL_OBJECT_CODE);
        }
    }
}
