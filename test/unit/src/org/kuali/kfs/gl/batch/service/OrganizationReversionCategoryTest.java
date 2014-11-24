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
package org.kuali.kfs.gl.batch.service;

import java.util.Map;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.OrganizationReversionService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;

/**
 * A test to make sure that canonical object codes are put into the correct Organization Reversion categories
 */
@ConfigureContext
public class OrganizationReversionCategoryTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionCategoryTest.class);

    private OrganizationReversionService organizationReversionService;
    private Map<String, OrganizationReversionCategoryLogic> categories;
    private ObjectCodeService objectCodeService;

    /**
     * Sets up services and initializes the categories for this test.
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();

        organizationReversionService = SpringContext.getBean(OrganizationReversionService.class,"organizationReversionService");
        categories = organizationReversionService.getCategories();
        objectCodeService = SpringContext.getBean(ObjectCodeService.class);
    }

    /**
     * Tests that C01 (Org. Wages) category accepts the right object codes
     */
    public void testC01Reversion() {
        String category = "C01";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic) categories.get(category);
        assertNotNull("Category not found", cat);

        ObjectCode se = objectCodeService.getByPrimaryId(TestUtils.getFiscalYearForTesting(), "BL", "5000");
        assertNotNull("S&E Object code should exist", se);

        assertFalse("S&E is not wages", cat.containsObjectCode(se));

        ObjectCode wages = objectCodeService.getByPrimaryId(TestUtils.getFiscalYearForTesting(), "BL", "3854");
        assertNotNull("Wages Object code should exist", wages);

        assertTrue("Wages object is wages", cat.containsObjectCode(wages));
    }

    /**
     * Tests that C02 (Salary & Fringes) Category accepts the right object codes
     */
    public void testC02Reversion() {
        String category = "C02";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic) categories.get(category);
        assertNotNull("Category not found", cat);

        ObjectCode se = objectCodeService.getByPrimaryId(TestUtils.getFiscalYearForTesting(), "BL", "5000");
        assertNotNull("S&E Object code should exist", se);

        assertFalse("S&E is not salary/fringes", cat.containsObjectCode(se));

        ObjectCode wages = objectCodeService.getByPrimaryId(TestUtils.getFiscalYearForTesting(), "BL", "3854");
        assertNotNull("Wages Object code should exist", wages);

        assertFalse("Wages object is not salary/fringes", cat.containsObjectCode(wages));

        ObjectCode sal = objectCodeService.getByPrimaryId(TestUtils.getFiscalYearForTesting(), "BL", "2000");
        assertNotNull("Salary Object code should exist", sal);

        assertTrue("Wages object is salary/fringes", cat.containsObjectCode(sal));
    }

    /**
     * Tests that C03 (Financial Aid) Category accepts the right object codes
     */
    public void testC03Reversion() {
        String category = "C03";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic) categories.get(category);
        assertNotNull("Category not found", cat);

        ObjectCode se = objectCodeService.getByPrimaryId(TestUtils.getFiscalYearForTesting(), "BL", "5000");
        assertNotNull("S&E Object code should exist", se);

        assertFalse("S&E is not financial aid", cat.containsObjectCode(se));

        ObjectCode fr = objectCodeService.getByPrimaryId(TestUtils.getFiscalYearForTesting(), "BL", "5885");
        assertNotNull("Fee Remission Object code should exist", fr);

        assertTrue("Fee Remission object is financial aid", cat.containsObjectCode(fr));
    }

    /**
     * Tests that C04 (Capital Equip) Category accepts the right object codes
     */
    public void testC04Reversion() {
        String category = "C04";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic) categories.get(category);
        assertNotNull("Category not found", cat);

        ObjectCode se = objectCodeService.getByPrimaryId(TestUtils.getFiscalYearForTesting(), "BL", "5000");
        assertNotNull("S&E Object code should exist", se);

        assertFalse("S&E is not capital equipment", cat.containsObjectCode(se));

        ObjectCode ce = objectCodeService.getByPrimaryId(TestUtils.getFiscalYearForTesting(), "BL", "7677");
        assertNotNull("Capital Equip Object code should exist", ce);

        assertTrue("Art object is capital equipment", cat.containsObjectCode(ce));
    }

    /**
     * Tests that C05 (Reserve) Category accepts the right object codes
     */
    public void testC05Reversion() {
        String category = "C05";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic) categories.get(category);
        assertNotNull("Category not found", cat);

        ObjectCode se = objectCodeService.getByPrimaryId(TestUtils.getFiscalYearForTesting(), "BL", "5000");
        assertNotNull("S&E Object code should exist", se);

        assertFalse("S&E is not reserve", cat.containsObjectCode(se));
        // TODO True test
    }

    /**
     * Tests that C06 (Transfer Out) Category accepts the right object codes
     */
    public void testC06Reversion() {
        String category = "C06";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic) categories.get(category);
        assertNotNull("Category not found", cat);

        ObjectCode se = objectCodeService.getByPrimaryId(TestUtils.getFiscalYearForTesting(), "BL", "5000");
        assertNotNull("S&E Object code should exist", se);

        assertFalse("S&E is not transfer out", cat.containsObjectCode(se));
        // TODO True test
    }

    /**
     * Tests that C07 (Transfer In) Category accepts the right object codes
     */
    public void testC07Reversion() {
        String category = "C07";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic) categories.get(category);
        assertNotNull("Category not found", cat);

        ObjectCode se = objectCodeService.getByPrimaryId(TestUtils.getFiscalYearForTesting(), "BL", "5000");
        assertNotNull("S&E Object code should exist", se);

        assertFalse("S&E is not transfer in", cat.containsObjectCode(se));
        // TODO True test
    }

    /**
     * Tests that C08 (Travel) Category accepts the right object codes
     */
    public void testC08Reversion() {
        String category = "C08";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic) categories.get(category);
        assertNotNull("Category not found", cat);

        ObjectCode se = objectCodeService.getByPrimaryId(TestUtils.getFiscalYearForTesting(), "BL", "5000");
        assertNotNull("S&E Object code should exist", se);

        assertFalse("S&E is not travel", cat.containsObjectCode(se));
        // TODO True test
    }

    /**
     * Tests that C09 (Other Expenses) Category accepts the right object codes
     */
    public void testC09Reversion() {
        String category = "C09";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic) categories.get(category);
        assertNotNull("Category not found", cat);

        ObjectCode se = objectCodeService.getByPrimaryId(TestUtils.getFiscalYearForTesting(), "BL", "5000");
        assertNotNull("S&E Object code should exist", se);

        assertTrue("S&E is S&E", cat.containsObjectCode(se));
        // TODO False test
    }

    /**
     * Tests that C10 (Assess Expend) Category accepts the right object codes
     */
    public void testC10Reversion() {
        String category = "C10";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic) categories.get(category);
        assertNotNull("Category not found", cat);

        ObjectCode se = objectCodeService.getByPrimaryId(TestUtils.getFiscalYearForTesting(), "BL", "5000");
        assertNotNull("S&E Object code should exist", se);

        assertFalse("S&E is not asses expend", cat.containsObjectCode(se));
        // TODO True test
    }

    /**
     * Tests that C11 (Revenue) Category accepts the right object codes
     */
    public void testC11Reversion() {
        String category = "C11";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic) categories.get(category);
        assertNotNull("Category not found", cat);

        ObjectCode se = objectCodeService.getByPrimaryId(TestUtils.getFiscalYearForTesting(), "BL", "5000");
        assertNotNull("S&E Object code should exist", se);

        assertFalse("S&E is not revenue", cat.containsObjectCode(se));
        // TODO True test
    }
}
