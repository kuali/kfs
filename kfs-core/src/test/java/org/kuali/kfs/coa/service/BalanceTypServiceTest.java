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

import java.util.HashMap;

import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class tests the BalanceType service.
 */
@ConfigureContext
public class BalanceTypServiceTest extends KualiTestBase {
    private static final boolean ACTIVE = true;
    private static final boolean BAL_TYPE_ENCUMB = true;
    private static final String BAL_TYPE_CODE = "ZZ";
    private static final String BAL_TYPE_NAME = "Z NAME";
    private static final String GUID = "123456789012345678901234567890123456";
    private static final Long VER_NBR = new Long(1);
    private static final boolean OFFSET_GEN = false;
    private static final String SHORT_NAME = "Z SHORT";

    private static final String ACTUAL_BAL_TYPE_CODE = "AC";

    public void testCreateLookupDelete1() {
        // create
        BalanceType bal = new BalanceType();
        bal.setActive(true);
        bal.setFinBalanceTypeEncumIndicator(true);
        bal.setCode(BAL_TYPE_CODE);
        bal.setName(BAL_TYPE_NAME);
        bal.setObjectId(GUID);
        bal.setFinancialOffsetGenerationIndicator(OFFSET_GEN);
        bal.setFinancialBalanceTypeShortNm(SHORT_NAME);
        bal.setVersionNumber(VER_NBR);

        SpringContext.getBean(BusinessObjectService.class).save(bal);

        // lookup
        HashMap map = new HashMap();
        map.put("code", BAL_TYPE_CODE);
        BalanceType bal2 = (BalanceType) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BalanceType.class, map);
        assertNotNull("Should be a valid object.", bal2);
        assertEquals("Known-good code results in expected returned Name.", BAL_TYPE_NAME, bal2.getName());

        // delete
        SpringContext.getBean(BusinessObjectService.class).delete(bal2);

        // try to lookup again
        map = new HashMap();
        map.put("code", BAL_TYPE_CODE);
        BalanceType bal3 = (BalanceType) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BalanceType.class, map);
        assertNull("Shouldn't be a valid object.", bal3);
    }

    /*
     * Disable this test because no data in database yet RO 9-22-05 public void testActualBalanceTypeLookup() { //test known-good
     * byCode BalanceTyp bal = SpringContext.getBean(BalanceTypService.class).getActualBalanceTyp(); assertNotNull("Should be a
     * valid object.", bal); assertEquals(ACTUAL_BAL_TYPE_CODE, bal.getCode()); }
     */
}
