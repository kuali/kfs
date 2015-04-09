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

import org.kuali.kfs.coa.businessobject.OrganizationReversion;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * This class...
 */
public class OrganizationReversionTestCase extends KualiTestBase {

    public void testGetByPrimaryKey() throws Exception {
        OrganizationReversionService organizationReversionService = SpringContext.getBean(OrganizationReversionService.class);
        assertNotNull("Service shouldn't be null", organizationReversionService);

        Integer fiscalYear = new Integer("2004");

        OrganizationReversion notexist = organizationReversionService.getByPrimaryId(fiscalYear, "BL", "TEST");
        assertNull("BL-TEST org reversion shouldn't exist in table", notexist);

        OrganizationReversion exist = organizationReversionService.getByPrimaryId(fiscalYear, "BL", "PSY");
        assertNotNull("BL-PSY should exist in table", exist);

    }
}
