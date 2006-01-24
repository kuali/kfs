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
package org.kuali.core.service;

import java.text.DateFormat;
import java.util.Date;

import org.kuali.core.bo.KualiSystemCode;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.codes.BudgetAggregationCode;
import org.kuali.test.KualiTestBaseWithSpring;

/**
 * This class tests the BudgetAggregationCode service.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetAggregationCodeServiceTest extends KualiTestBaseWithSpring {

    private BudgetAggregationCode bac;
    private KualiCodeService kualiCodeService;
    private String timestamp;

    /**
     * Performs setup operations before tests are executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        kualiCodeService = SpringServiceLocator.getKualiCodeService();
        timestamp = DateFormat.getDateInstance().format(new Date());
    }

    /**
     * Performs all tests for this service.
     */
    public void testLookupByCode_code_known() {
        bac = null;
        bac = (BudgetAggregationCode) kualiCodeService.getByCode(BudgetAggregationCode.class,
                TestConstants.Data5.BUDGET_AGGREGATION_CODE1);
        assertNotNull("Should be a valid object.", bac);
        assertEquals("Known-good code results in expected returned Name.", TestConstants.Data5.BUDGET_AGGREGATION_NAME1, 
                bac.getName());
    }

    public void testLookupByCode_code_unknown() {
        bac = null;
        bac = (BudgetAggregationCode) kualiCodeService.getByCode(BudgetAggregationCode.class,
                TestConstants.Data5.FEDERAL_FUNDED_CODE_BAD);
        assertNull("Known-bad code should return null object.", bac);
    }

    public void testLookupByCode_code_blank() {
        bac = null;
        bac = (BudgetAggregationCode) kualiCodeService.getByCode(BudgetAggregationCode.class, "");
        assertNull("Known-empty code returns null object.", bac);
    }

    public void testLookupByCode_code_null() {
        bac = null;
        bac = (BudgetAggregationCode) kualiCodeService.getByCode(BudgetAggregationCode.class, null);
        assertNull("Known-null code returns null object.", bac);
    }

    public void testLookupByName_name_known() {
        KualiSystemCode bac = null;
        Object result=kualiCodeService.getByName(BudgetAggregationCode.class,TestConstants.Data5.BUDGET_AGGREGATION_NAME1);
        bac = (KualiSystemCode) result;
        assertNotNull("Should be a valid object.", bac);
        assertEquals("Known-good name results in expected returned code.", TestConstants.Data5.BUDGET_AGGREGATION_CODE1, bac
                .getCode());
    }

    public void testLookupByName_name_unknown() {
        bac = null;
        bac = (BudgetAggregationCode) kualiCodeService.getByName(BudgetAggregationCode.class,
                "This is not a valid code description in this table.");
        assertNull("Known-bad code returns null object.", bac);
    }

    public void testLookupByCode_name_blank() {
        bac = null;
        bac = (BudgetAggregationCode) kualiCodeService.getByName(BudgetAggregationCode.class, "");
        assertNull("Known-empty name returns null object.", bac);
    }

    public void testLookupByCode_name_null() {
        bac = null;
        bac = (BudgetAggregationCode) kualiCodeService.getByName(BudgetAggregationCode.class, null);
        assertNull("Known-null name returns null object.", bac);
    }

    public void testActive() {

        // test known-good active code
        bac = null;
        bac = (BudgetAggregationCode) kualiCodeService.getByCode(BudgetAggregationCode.class,
                TestConstants.Data5.BUDGET_AGGREGATION_CODE1);
        assertEquals("The active code associated with this field is incorrect", true, bac.isActive());

    }

    /**
     * This tests the caching mechanism by looping five times through some get calls. The first time through, the data should be
     * retrieved from the DB. Every time after that, the object should be retrieved from the cache therefore time to retrieve should
     * be less than or equal to.
     * 
     * @author Aaron Godert (ag266@cornell.edu)
     */
    public void testCache() {
        long tsStart;
        long tsStop;

        for (int i = 0; i < 5; i++) {
            long firstTime = -1000;
            tsStart = System.currentTimeMillis();
            BudgetAggregationCode O = (BudgetAggregationCode) kualiCodeService.getByCode(BudgetAggregationCode.class,
                    TestConstants.Data5.BUDGET_AGGREGATION_CODE1);
            BudgetAggregationCode C = (BudgetAggregationCode) kualiCodeService.getByCode(BudgetAggregationCode.class,
                    TestConstants.Data5.BUDGET_AGGREGATION_CODE2);
            BudgetAggregationCode L = (BudgetAggregationCode) kualiCodeService.getByCode(BudgetAggregationCode.class,
                    TestConstants.Data5.BUDGET_AGGREGATION_CODE1);
            tsStop = System.currentTimeMillis();
            long diff = tsStop - tsStart;
            if (firstTime == -1000) {
                firstTime = diff;
            }
            assertTrue(diff <= firstTime);
        }
    }
}
