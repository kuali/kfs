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
package org.kuali.module.kra.service;

import java.sql.Date;
import java.text.SimpleDateFormat;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.bo.BudgetPeriod;
import org.kuali.module.kra.bo.BudgetUser;
import org.kuali.test.KualiTestBaseWithSpring;

/**
 * This class...
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetPersonnelServiceTest extends KualiTestBaseWithSpring {

    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    private BudgetPersonnelService budgetPersonnelService;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        budgetPersonnelService = SpringServiceLocator.getBudgetPersonnelService();
    }

    public void testPeriodFullFiscalYear() throws Exception {
        KualiDecimal periodSalary;
        BudgetUser dummyUser = new BudgetUser();
        // dummyUser.setBaseSalary(new Long(100000));

        BudgetPeriod dummyPeriod = new BudgetPeriod();
        dummyPeriod.setBudgetPeriodBeginDate(new Date(sdf.parse("07/01/2006").getTime()));
        dummyPeriod.setBudgetPeriodEndDate(new Date(sdf.parse("06/30/2007").getTime()));

        // Fiscal year is the same as the period dates
        // periodSalary =
        // budgetPersonnelService.calculatePeriodSalary(dummyUser, dummyPeriod, new KualiDecimal(3));
        // assertEquals(dummyUser.getBaseSalary(), periodSalary);

        // Fiscal year is in the future, relative to the period dates
        // periodSalary =
        // budgetPersonnelService.calculatePeriodSalary(dummyUser, dummyPeriod, new KualiDecimal(3));
        // assertEquals(new KualiDecimal(103000), periodSalary);

        // Fiscal year is in the pase, relative to the period dates
        // periodSalary = budgetPersonnelService.calculatePeriodSalary(dummyUser, dummyPeriod, new KualiDecimal(3));
        // assertEquals(new KualiDecimal(97087.38), periodSalary);
    }
}
