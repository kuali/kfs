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
package org.kuali.module.kra.bo;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.test.KualiTestBaseWithSpring;

/**
 * This class tests methods in BudgetNonpersonnelCopyOverFormHelper.
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class BudgetPeriodTest extends KualiTestBaseWithSpring {

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testBudgetPeriod() {
        assertTrue(true);

        /**
         * @todo stubbed this for creation of static method below. Probably should write some BudgetPeriod test cases at some point
         *       ...
         */
    }

    public static List createBudgetPeriods(int numberOfPeriods) {
        List budgetPeriods = new ArrayList();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        for (int i = 0; i < numberOfPeriods; i++) {
            BudgetPeriod budgetPeriod = new BudgetPeriod();
            budgetPeriod.setBudgetPeriodSequenceNumber(new Integer(i));
            try {
                budgetPeriod.setBudgetPeriodBeginDate(new Date(sdf.parse("07/01/" + 2000 + i).getTime()));
                budgetPeriod.setBudgetPeriodEndDate(new Date(sdf.parse("06/30/" + 2001 + i).getTime()));
            }
            catch (ParseException e) {
                throw new NullPointerException();
            }
            budgetPeriods.add(budgetPeriod);
        }

        return budgetPeriods;
    }
}
