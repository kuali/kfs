/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.cam.utils;

import org.kuali.kfs.module.cam.util.KualiDecimalUtils;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class KualiDecimalUtilsTest extends KualiTestBase {

    public void testAllocateByQuantity() throws Exception {

        KualiDecimal[] allocationResults = KualiDecimalUtils.allocateByQuantity(new KualiDecimal(10.00),3);

        assertEquals(allocationResults.length, 3);
        assertEquals(allocationResults[0], new KualiDecimal(3.33));
        assertEquals(allocationResults[1], new KualiDecimal(3.33));
        assertEquals(allocationResults[2], new KualiDecimal(3.34));

        allocationResults = KualiDecimalUtils.allocateByQuantity(new KualiDecimal(10.00), 15);

        assertEquals(allocationResults.length, 15);
        assertEquals(allocationResults[0], new KualiDecimal(0.67));
        assertEquals(allocationResults[1], new KualiDecimal(0.67));
        assertEquals(allocationResults[2], new KualiDecimal(0.67));
        assertEquals(allocationResults[3], new KualiDecimal(0.67));
        assertEquals(allocationResults[4], new KualiDecimal(0.67));
        assertEquals(allocationResults[5], new KualiDecimal(0.67));
        assertEquals(allocationResults[6], new KualiDecimal(0.67));
        assertEquals(allocationResults[7], new KualiDecimal(0.67));
        assertEquals(allocationResults[8], new KualiDecimal(0.67));
        assertEquals(allocationResults[9], new KualiDecimal(0.67));
        assertEquals(allocationResults[10], new KualiDecimal(0.66));
        assertEquals(allocationResults[11], new KualiDecimal(0.66));
        assertEquals(allocationResults[12], new KualiDecimal(0.66));
        assertEquals(allocationResults[13], new KualiDecimal(0.66));
        assertEquals(allocationResults[14], new KualiDecimal(0.66));

        allocationResults = KualiDecimalUtils.allocateByQuantity(new KualiDecimal(5.00), 7);

        assertEquals(allocationResults.length, 7);
        assertEquals(allocationResults[0], new KualiDecimal(0.71));
        assertEquals(allocationResults[1], new KualiDecimal(0.71));
        assertEquals(allocationResults[2], new KualiDecimal(0.71));
        assertEquals(allocationResults[3], new KualiDecimal(0.71));
        assertEquals(allocationResults[4], new KualiDecimal(0.72));
        assertEquals(allocationResults[5], new KualiDecimal(0.72));
        assertEquals(allocationResults[6], new KualiDecimal(0.72));
    }

    public void testAllocateByRatio() throws Exception {
        KualiDecimal[] values = KualiDecimalUtils.allocateByRatio(new KualiDecimal(13), new double[] { 0.533333 });
        assertNotNull(values);
        assertEquals(1, values.length);
        assertEquals(new KualiDecimal(6.94), values[0]);
        values = KualiDecimalUtils.allocateByRatio(new KualiDecimal(13), new double[] { 0.2820512821, 0.333333333, 0.384615385 });
        assertNotNull(values);
        assertEquals(3, values.length);
        assertEquals(new KualiDecimal(3.67), values[0]);
        assertEquals(new KualiDecimal(4.33), values[1]);
        assertEquals(new KualiDecimal(5), values[2]);
        assertEquals(new KualiDecimal(13), values[2].add(values[1]).add(values[0]));
    }

    public void testAllocateByQuantityExtremeCases() throws Exception {
        // test with large amount
        String input = "2956900.56";
        KualiDecimal originalAmount = new KualiDecimal(input);
        KualiDecimal allocatedAmount = KualiDecimalUtils.allocateByQuantity(originalAmount, 1)[0];
        assertEquals(originalAmount, allocatedAmount);

        // test with big divisor thus small divided amount
        KualiDecimal totalAmount = new KualiDecimal(99);
        int divisor = 10000;
        KualiDecimal[] allocationResults = KualiDecimalUtils.allocateByQuantity(totalAmount, divisor);
        // first 9900 elements each gets 1c
        KualiDecimal cent = new KualiDecimal(0.01);
        for (int i=0; i < divisor - 100; i++) {
            assertEquals(allocationResults[i], cent);
        }
        // last 100 elements each gets 0
        for (int i = divisor-100; i < divisor; i++) {
            assertEquals(allocationResults[i], KualiDecimal.ZERO);
        }
    }

    public void testAllocateByRatioExtremeCases() throws Exception {
        // test with large amount
        String input = "2956900.56";
        KualiDecimal originalAmount = new KualiDecimal(input);
        double[] ratio = new double[]{1};
        KualiDecimal allocatedAmount = KualiDecimalUtils.allocateByRatio(originalAmount, ratio)[0];
        assertEquals(originalAmount, allocatedAmount);

        // test with big number of ratios thus small allocated amount
        KualiDecimal totalAmount = new KualiDecimal(99.00);
        double[] ratios = new double[10000];
        for (int i=0; i < ratios.length; i++) {
            ratios[i] = 0.0001;
        }
        KualiDecimal[] allocationResults = KualiDecimalUtils.allocateByRatio(totalAmount, ratios);
        // first 9900 elements each has 1c
        KualiDecimal cent = new KualiDecimal(0.01);
        for (int i=0; i < ratios.length - 100; i++) {
            assertEquals(allocationResults[i], cent);
        }
        // last 100 elements each has 0
        for (int i = ratios.length - 100; i < ratios.length; i++) {
            assertEquals(allocationResults[i], KualiDecimal.ZERO);
        }
    }

}
