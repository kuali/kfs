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
