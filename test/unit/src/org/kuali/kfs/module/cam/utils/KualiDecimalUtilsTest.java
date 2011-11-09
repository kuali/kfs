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

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.util.KualiDecimalUtils;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class KualiDecimalUtilsTest extends KualiTestBase {

    public void testAllocate_Success() throws Exception {
        KualiDecimalUtils kualiDecimalUtils = new KualiDecimalUtils(new KualiDecimal(10.00), CamsConstants.CURRENCY_USD);

        KualiDecimal[] allocationResults = kualiDecimalUtils.allocateByQuantity(3);

        assertEquals(allocationResults.length, 3);
        assertEquals(allocationResults[0], new KualiDecimal(3.33));
        assertEquals(allocationResults[1], new KualiDecimal(3.33));
        assertEquals(allocationResults[2], new KualiDecimal(3.34));

        kualiDecimalUtils = new KualiDecimalUtils(new KualiDecimal(10.00), CamsConstants.CURRENCY_USD);

        allocationResults = kualiDecimalUtils.allocateByQuantity(15);

        assertEquals(allocationResults.length, 15);
        assertEquals(allocationResults[0], new KualiDecimal(0.66));
        assertEquals(allocationResults[1], new KualiDecimal(0.66));
        assertEquals(allocationResults[2], new KualiDecimal(0.66));
        assertEquals(allocationResults[3], new KualiDecimal(0.66));
        assertEquals(allocationResults[4], new KualiDecimal(0.66));
        assertEquals(allocationResults[5], new KualiDecimal(0.67));
        assertEquals(allocationResults[6], new KualiDecimal(0.67));
        assertEquals(allocationResults[7], new KualiDecimal(0.67));
        assertEquals(allocationResults[8], new KualiDecimal(0.67));
        assertEquals(allocationResults[9], new KualiDecimal(0.67));
        assertEquals(allocationResults[10], new KualiDecimal(0.67));
        assertEquals(allocationResults[11], new KualiDecimal(0.67));
        assertEquals(allocationResults[12], new KualiDecimal(0.67));
        assertEquals(allocationResults[13], new KualiDecimal(0.67));
        assertEquals(allocationResults[14], new KualiDecimal(0.67));

        kualiDecimalUtils = new KualiDecimalUtils(new KualiDecimal(5.00), CamsConstants.CURRENCY_USD);

        allocationResults = kualiDecimalUtils.allocateByQuantity(7);

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
        assertEquals(new KualiDecimal(6.93), values[0]);
        assertEquals(6.93d, values[0].doubleValue());
        values = KualiDecimalUtils.allocateByRatio(new KualiDecimal(13), new double[] { 0.2820512821, 0.333333333, 0.384615385 });
        assertNotNull(values);
        assertEquals(3, values.length);
        assertEquals(new KualiDecimal(3.67), values[0]);
        assertEquals(new KualiDecimal(4.33), values[1]);
        assertEquals(new KualiDecimal(5), values[2]);
        assertEquals(new KualiDecimal(13), values[2].add(values[1]).add(values[0]));
    }
}
