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
package org.kuali.module.chart.service;

import static org.kuali.kfs.util.SpringServiceLocator.*;

import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

/**
 * This class tests the OffsetDefinition service.
 * 
 * 
 */
@WithTestSpringContext
public class OffsetDefinitionServiceTest extends KualiTestBase {

    public void testValidateAccount() {
        OffsetDefinition offsetDefinition = null;
        offsetDefinition = getOffsetDefinitionService().getByPrimaryId(new Integer(2004), "BA", "IB", "AC");
        assertNotNull("offset object code not found", offsetDefinition.getFinancialObject());
        assertEquals("offset object code should have been 8000", "8000", offsetDefinition.getFinancialObject().getFinancialObjectCode());

        offsetDefinition = null;
        offsetDefinition = getOffsetDefinitionService().getByPrimaryId(new Integer(2004), "XX", "XX", "XX");
        assertNull(offsetDefinition);
    }
}
