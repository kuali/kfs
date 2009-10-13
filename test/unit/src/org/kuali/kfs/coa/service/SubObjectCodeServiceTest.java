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

import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * This class tests the SubObjectCode service.
 */
@ConfigureContext
public class SubObjectCodeServiceTest extends KualiTestBase {

    /**
     * Test that the service returns null if any of the parameters are empty.
     */
    public void testEmptyParam() {
        SubObjectCode resultSubObjectCode = SpringContext.getBean(SubObjectCodeService.class).getByPrimaryId(new Integer(0), TestConstants.Data4.CHART_CODE, TestConstants.Data4.ACCOUNT, TestConstants.Data4.OBJECT_CODE, TestConstants.Data4.SUBOBJECT_CODE);
        assertNull(resultSubObjectCode);

        resultSubObjectCode = SpringContext.getBean(SubObjectCodeService.class).getByPrimaryId(TestConstants.Data4.POSTING_YEAR, "", TestConstants.Data4.ACCOUNT, TestConstants.Data4.OBJECT_CODE, TestConstants.Data4.SUBOBJECT_CODE);
        assertNull(resultSubObjectCode);

        resultSubObjectCode = SpringContext.getBean(SubObjectCodeService.class).getByPrimaryId(TestConstants.Data4.POSTING_YEAR, TestConstants.Data4.CHART_CODE, "", TestConstants.Data4.OBJECT_CODE, TestConstants.Data4.SUBOBJECT_CODE);
        assertNull(resultSubObjectCode);

        resultSubObjectCode = SpringContext.getBean(SubObjectCodeService.class).getByPrimaryId(TestConstants.Data4.POSTING_YEAR, TestConstants.Data4.CHART_CODE, TestConstants.Data4.ACCOUNT, "", TestConstants.Data4.SUBOBJECT_CODE);
        assertNull(resultSubObjectCode);

        resultSubObjectCode = SpringContext.getBean(SubObjectCodeService.class).getByPrimaryId(TestConstants.Data4.POSTING_YEAR, TestConstants.Data4.CHART_CODE, TestConstants.Data4.ACCOUNT, TestConstants.Data4.OBJECT_CODE, "");
        assertNull(resultSubObjectCode);
    }

    /**
     * Test that the service returns the correct results based on the given pararmeters.
     */
    public void testService() {
        SubObjectCode resultSubObjectCode = null;

        resultSubObjectCode = SpringContext.getBean(SubObjectCodeService.class).getByPrimaryId(TestConstants.Data4.POSTING_YEAR, TestConstants.Data4.CHART_CODE, TestConstants.Data4.ACCOUNT, TestConstants.Data4.OBJECT_CODE, TestConstants.Data4.SUBOBJECT_CODE);
        assertNotNull(resultSubObjectCode);
        assertTrue(resultSubObjectCode.isActive());

    }
}
