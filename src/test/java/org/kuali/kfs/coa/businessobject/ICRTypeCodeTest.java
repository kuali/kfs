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
package org.kuali.kfs.coa.businessobject;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * Tests of the ICRTypeCode BO.
 */
@ConfigureContext
public class ICRTypeCodeTest extends KualiTestBase {

    /**
     * The isActive method should always return true, at least until a phase 2 task adds active indicators to all BOs.
     */
    public void testIsActive() {
        IndirectCostRecoveryType bo = (IndirectCostRecoveryType) (SpringContext.getBean(BusinessObjectService.class).findAll(IndirectCostRecoveryType.class).toArray()[0]);
        assertEquals(true, bo.isActive());
    }
}
