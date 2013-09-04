/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.service;

import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

@ConfigureContext
public class TravelExpenseServiceTest extends KualiTestBase {
    public void testGetExpenseType() {
        final TravelExpenseService travelExpenseService = SpringContext.getBean(TravelExpenseService.class);

        // monkeys are easy.  let's try monkeys first
        final ExpenseTypeObjectCode monkeyETOC1 = travelExpenseService.getExpenseType("M", "TA", "IN", "EMP");
        assertEquals("M-TA-EMP-IN found correct monkey expense type object code", "6000", monkeyETOC1.getFinancialObjectCode());
        final ExpenseTypeObjectCode monkeyETOC2 = travelExpenseService.getExpenseType("M", "ENT", "IN", "EMP");
        assertEquals("M-ENT-EMP-IN found correct monkey expense type object code", "6000", monkeyETOC2.getFinancialObjectCode());
        final ExpenseTypeObjectCode monkeyETOC3 = travelExpenseService.getExpenseType("M", "TA", "OUT", "EMP");
        assertEquals("M-TA-EMP-OUT found correct monkey expense type object code", "6000", monkeyETOC3.getFinancialObjectCode());
        final ExpenseTypeObjectCode monkeyETOC4 = travelExpenseService.getExpenseType("M", "TA", "OUT", "NON");
        assertEquals("M-TA-NON-OUT found correct monkey expense type object code", "6000", monkeyETOC4.getFinancialObjectCode());

        // giraffes are a bit harder
        final ExpenseTypeObjectCode giraffeETOC1 = travelExpenseService.getExpenseType("G", "TA", "OUT", "NON");
        assertEquals("G-TA-NON-OUT found correct giraffe expense type object code", "6002", giraffeETOC1.getFinancialObjectCode());
        final ExpenseTypeObjectCode giraffeETOC2 = travelExpenseService.getExpenseType("G", "ENT", "OUT", "NON");
        assertEquals("G-ENT-NON-OUT found correct giraffe expense type object code", "6001", giraffeETOC2.getFinancialObjectCode());
        final ExpenseTypeObjectCode giraffeETOC3 = travelExpenseService.getExpenseType("G", "TA", "IN", "EMP");
        assertEquals("G-TA-EMP-IN found correct giraffe expense type object code", "6003", giraffeETOC3.getFinancialObjectCode());
        final ExpenseTypeObjectCode giraffeETOC4 = travelExpenseService.getExpenseType("G", "TA", "IN", "NON");
        assertEquals("G-TA-NON-IN found correct giraffe expense type object code", "6005", giraffeETOC4.getFinancialObjectCode());
        final ExpenseTypeObjectCode giraffeETOC5 = travelExpenseService.getExpenseType("G", "TA", "OUT", "EMP");
        assertEquals("G-TA-EMP-OUT found correct giraffe expense type object code", "6004", giraffeETOC5.getFinancialObjectCode());
        final ExpenseTypeObjectCode giraffeETOC6 = travelExpenseService.getExpenseType("G", "ENT", "IN", "EMP");
        assertEquals("G-ENT-EMP-IN found correct giraffe expense type object code", "6001", giraffeETOC6.getFinancialObjectCode());
    }
}
