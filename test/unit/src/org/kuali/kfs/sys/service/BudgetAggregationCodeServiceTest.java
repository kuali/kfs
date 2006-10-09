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

import static org.kuali.core.util.SpringServiceLocator.getKualiCodeService;

import org.kuali.module.chart.bo.codes.BudgetAggregationCode;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

/**
 * This class tests the BudgetAggregationCode service.
 * 
 * 
 */
@WithTestSpringContext
public class BudgetAggregationCodeServiceTest extends KualiTestBase {

    /**
     * Performs all tests for this service.
     */
    public void testLookupByCode_code_known() {
        BudgetAggregationCode bac = (BudgetAggregationCode) getKualiCodeService().getByCode(BudgetAggregationCode.class, TestConstants.Data5.BUDGET_AGGREGATION_CODE1);
        assertNotNull("Should be a valid object.", bac);
        assertEquals("Known-good code results in expected returned Name.", TestConstants.Data5.BUDGET_AGGREGATION_NAME1, bac.getName());
    }

    public void testLookupByCode_code_unknown() {
        BudgetAggregationCode bac = (BudgetAggregationCode) getKualiCodeService().getByCode(BudgetAggregationCode.class, TestConstants.Data5.FEDERAL_FUNDED_CODE_BAD);
        assertNull("Known-bad code should return null object.", bac);
    }

    public void testLookupByCode_code_blank() {
        BudgetAggregationCode bac = (BudgetAggregationCode) getKualiCodeService().getByCode(BudgetAggregationCode.class, "");
        assertNull("Known-empty code returns null object.", bac);
    }

    public void testLookupByCode_code_null() {
        BudgetAggregationCode bac = (BudgetAggregationCode) getKualiCodeService().getByCode(BudgetAggregationCode.class, null);
        assertNull("Known-null code returns null object.", bac);
    }

    public void testLookupByName_name_known() {
        BudgetAggregationCode bac = (BudgetAggregationCode) getKualiCodeService().getByName(BudgetAggregationCode.class, TestConstants.Data5.BUDGET_AGGREGATION_NAME1);
        assertNotNull("Should be a valid object.", bac);
        assertEquals("Known-good name results in expected returned code.", TestConstants.Data5.BUDGET_AGGREGATION_CODE1, bac.getCode());
    }

    public void testLookupByName_name_unknown() {
        BudgetAggregationCode bac = (BudgetAggregationCode) getKualiCodeService().getByName(BudgetAggregationCode.class, "This is not a valid code description in this table.");
        assertNull("Known-bad code returns null object.", bac);
    }

    public void testLookupByCode_name_blank() {
        BudgetAggregationCode bac = (BudgetAggregationCode) getKualiCodeService().getByName(BudgetAggregationCode.class, "");
        assertNull("Known-empty name returns null object.", bac);
    }

    public void testLookupByCode_name_null() {
        BudgetAggregationCode bac = (BudgetAggregationCode) getKualiCodeService().getByName(BudgetAggregationCode.class, null);
        assertNull("Known-null name returns null object.", bac);
    }

    public void testActive() {
        // test known-good active code
        BudgetAggregationCode bac = (BudgetAggregationCode) getKualiCodeService().getByCode(BudgetAggregationCode.class, TestConstants.Data5.BUDGET_AGGREGATION_CODE1);
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
            BudgetAggregationCode O = (BudgetAggregationCode) getKualiCodeService().getByCode(BudgetAggregationCode.class, TestConstants.Data5.BUDGET_AGGREGATION_CODE1);
            BudgetAggregationCode C = (BudgetAggregationCode) getKualiCodeService().getByCode(BudgetAggregationCode.class, TestConstants.Data5.BUDGET_AGGREGATION_CODE2);
            BudgetAggregationCode L = (BudgetAggregationCode) getKualiCodeService().getByCode(BudgetAggregationCode.class, TestConstants.Data5.BUDGET_AGGREGATION_CODE1);
            tsStop = System.currentTimeMillis();
            long diff = tsStop - tsStart;
            if (firstTime == -1000) {
                firstTime = diff;
            }
            assertTrue(diff <= firstTime);
        }
    }
}
