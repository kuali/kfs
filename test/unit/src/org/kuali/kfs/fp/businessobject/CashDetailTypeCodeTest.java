/*
 * Copyright 2005-2006 The Kuali Foundation
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
