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
package org.kuali.kfs.fp.businessobject;

import org.kuali.kfs.sys.context.KualiTestBase;

/**
 * This class...
 */
public class CashDetailTypeCodeTest extends KualiTestBase {
    private CashDetailTypeCode cdtc = null;
    public static final boolean ACTIVE_IND = true;
    public static final String GUID = "123456789012345678901234567890123456";
    public static final String NAME = "NAME";
    public static final String CODE = "CODE";
    public static final Long VER_NBR = new Long(1);
    public static final String DESCRIPTION = "Description";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        cdtc = new CashDetailTypeCode();
        cdtc.setActive(ACTIVE_IND);
        cdtc.setCode(CODE);
        cdtc.setName(NAME);
        cdtc.setObjectId(GUID);
        cdtc.setVersionNumber(VER_NBR);
        cdtc.setDescription(DESCRIPTION);
    }

    public void testCashDetailTypePojo() {
        assertEquals(ACTIVE_IND, cdtc.isActive());
        assertEquals(CODE, cdtc.getCode());
        assertEquals(NAME, cdtc.getName());
        assertEquals(VER_NBR, cdtc.getVersionNumber());
        assertEquals(GUID, cdtc.getObjectId());
        assertEquals(DESCRIPTION, cdtc.getDescription());
    }
}
