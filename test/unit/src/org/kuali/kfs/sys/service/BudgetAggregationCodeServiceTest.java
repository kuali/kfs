/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.service;

import static org.kuali.kfs.util.SpringServiceLocator.getKualiCodeService;

import org.kuali.kfs.context.KualiTestBase;
import org.kuali.module.chart.bo.codes.BudgetAggregationCode;
import org.kuali.test.RequiresSpringContext;

/**
 * This class tests the BudgetAggregationCode service.
 * 
 * 
 */
@RequiresSpringContext
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
     * 
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
