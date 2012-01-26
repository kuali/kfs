/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.coa.service;

import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;

/**
 * This class tests the OffsetDefinition service.
 */
@ConfigureContext
public class OffsetDefinitionServiceTest extends KualiTestBase {

    public void testValidateAccount() {
        OffsetDefinition offsetDefinition = null;
        offsetDefinition = SpringContext.getBean(OffsetDefinitionService.class).getByPrimaryId(TestUtils.getFiscalYearForTesting(), "BA", "IB", "AC");
        assertNotNull("offset object code not found in FY: " + TestUtils.getFiscalYearForTesting(), offsetDefinition.getFinancialObject());
        assertEquals("offset object code should have been 8000 in FY: " + TestUtils.getFiscalYearForTesting(), "8000", offsetDefinition.getFinancialObject().getFinancialObjectCode());

        offsetDefinition = null;
        offsetDefinition = SpringContext.getBean(OffsetDefinitionService.class).getByPrimaryId(TestUtils.getFiscalYearForTesting(), "XX", "XX", "XX");
        assertNull("No offset definition should have been retrieved: " + offsetDefinition,offsetDefinition);
    }
}
