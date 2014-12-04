/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
