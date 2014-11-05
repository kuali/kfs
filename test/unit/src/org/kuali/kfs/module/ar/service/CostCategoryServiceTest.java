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
package org.kuali.kfs.module.ar.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.ar.businessobject.CostCategory;
import org.kuali.kfs.module.ar.businessobject.CostCategoryDetail;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectCode;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectLevel;
import org.kuali.kfs.module.ar.dataaccess.CostCategoryDao;
import org.kuali.kfs.module.ar.service.impl.CostCategoryServiceImpl;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;

/**
 * Tests the CostCategoryService
 */
@ConfigureContext(session = khuntley)
public class CostCategoryServiceTest extends KualiTestBase {
    protected CostCategoryService costCategoryService;

    @Override
    public void setUp() {
        costCategoryService = new CostCategoryServiceImpl();
        ((CostCategoryServiceImpl)costCategoryService).setCostCategoryDao(SpringContext.getBean(CostCategoryDao.class));
    }

    public void testObjectCodeUniqueness() {
        CostCategoryObjectCode test1 = buildCostCategoryObjectCode("4000");
        CostCategoryDetail test1ConflictingDetail = costCategoryService.isCostCategoryObjectCodeUnique(test1);
        assertNotNull("YYYY-BL-4000 should not be unique", test1ConflictingDetail);

        CostCategoryObjectCode test2 = buildCostCategoryObjectCode("2000");
        CostCategoryDetail test2ConflictingDetail = costCategoryService.isCostCategoryObjectCodeUnique(test2);
        assertNotNull("YYYY-BL-2000 should not be unique", test1ConflictingDetail);

        CostCategoryObjectCode test3 = buildCostCategoryObjectCode("9041");
        CostCategoryDetail test3ConflictingDetail = costCategoryService.isCostCategoryObjectCodeUnique(test3);
        assertNull("YYYY-BL-9041 should be unique", test3ConflictingDetail);
    }

    protected CostCategoryObjectCode buildCostCategoryObjectCode(String financialObjectCode) {
        CostCategoryObjectCode test = new CostCategoryObjectCode();
        test.setCategoryCode("YYYY");
        test.setChartOfAccountsCode("BL");
        test.setFinancialObjectCode(financialObjectCode);
        test.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE_CURRENT);
        return test;
    }

    public void testObjectLevelUniqueness() {
        CostCategoryObjectLevel test1 = buildCostCategoryObjectLevel("ACSA");
        CostCategoryDetail test1ConflictingDetail = costCategoryService.isCostCategoryObjectLevelUnique(test1);
        assertNotNull("Cost Category Object Level YYYY-BL-ACSA is not unique", test1ConflictingDetail);

        CostCategoryObjectLevel test2 = buildCostCategoryObjectLevel("TAX");
        CostCategoryDetail test2ConflictingDetail = costCategoryService.isCostCategoryObjectLevelUnique(test2);
        assertNull("Cost Category Object Level YYYY-BL-TAX is unique", test2ConflictingDetail);
    }

    protected CostCategoryObjectLevel buildCostCategoryObjectLevel(String objectLevelCode) {
        CostCategoryObjectLevel test = new CostCategoryObjectLevel();
        test.setCategoryCode("YYYY");
        test.setChartOfAccountsCode("BL");
        test.setFinancialObjectLevelCode(objectLevelCode);
        return test;
    }

    public void testFindCostCategory() {
        final CostCategory costCategory1 = costCategoryService.getCostCategoryForObjectCode(TestUtils.getFiscalYearForTesting(), "BL", "4000");
        assertNotNull("There should be a cost category for object code "+TestUtils.getFiscalYearForTesting()+"-BL-4000",costCategory1);
        final CostCategory costCategory2 = costCategoryService.getCostCategoryForObjectCode(TestUtils.getFiscalYearForTesting(), "BL", "ZZZZ");
        assertNull("There should NOT be a cost category for object code "+TestUtils.getFiscalYearForTesting()+"-BL-ZZZZ", costCategory2);
    }
}
