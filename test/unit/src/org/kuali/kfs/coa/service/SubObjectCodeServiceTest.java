/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.chart.service;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.test.KualiTestBaseWithFixtures;
import org.kuali.test.WithTestSpringContext;

/**
 * This class tests the SubObjectCode service.
 * 
 * @author Kuali Nervous System Team ()
 */
@WithTestSpringContext
public class SubObjectCodeServiceTest extends KualiTestBaseWithFixtures {

    private SubObjectCodeService subObjectCodeService;

    /*
     * @see KualiTestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        subObjectCodeService = SpringServiceLocator.getSubObjectCodeService();
    }

    /**
     * Test that the service returns null if any of the parameters are empty.
     */
    public void testEmptyParam() {
        SubObjCd resultSubObjectCode = subObjectCodeService.getByPrimaryId(new Integer(0), TestConstants.Data4.CHART_CODE, TestConstants.Data4.ACCOUNT, TestConstants.Data4.OBJECT_CODE, TestConstants.Data4.SUBOBJECT_CODE);
        assertNull(resultSubObjectCode);

        resultSubObjectCode = subObjectCodeService.getByPrimaryId(TestConstants.Data4.POSTING_YEAR, "", TestConstants.Data4.ACCOUNT, TestConstants.Data4.OBJECT_CODE, TestConstants.Data4.SUBOBJECT_CODE);
        assertNull(resultSubObjectCode);

        resultSubObjectCode = subObjectCodeService.getByPrimaryId(TestConstants.Data4.POSTING_YEAR, TestConstants.Data4.CHART_CODE, "", TestConstants.Data4.OBJECT_CODE, TestConstants.Data4.SUBOBJECT_CODE);
        assertNull(resultSubObjectCode);

        resultSubObjectCode = subObjectCodeService.getByPrimaryId(TestConstants.Data4.POSTING_YEAR, TestConstants.Data4.CHART_CODE, TestConstants.Data4.ACCOUNT, "", TestConstants.Data4.SUBOBJECT_CODE);
        assertNull(resultSubObjectCode);

        resultSubObjectCode = subObjectCodeService.getByPrimaryId(TestConstants.Data4.POSTING_YEAR, TestConstants.Data4.CHART_CODE, TestConstants.Data4.ACCOUNT, TestConstants.Data4.OBJECT_CODE, "");
        assertNull(resultSubObjectCode);
    }

    /**
     * Test that the service returns the correct results based on the given pararmeters.
     */
    public void testService() {
        SubObjCd resultSubObjectCode = null;

        resultSubObjectCode = subObjectCodeService.getByPrimaryId(TestConstants.Data4.POSTING_YEAR, TestConstants.Data4.CHART_CODE, TestConstants.Data4.ACCOUNT, TestConstants.Data4.OBJECT_CODE, TestConstants.Data4.SUBOBJECT_CODE);
        assertNotNull(resultSubObjectCode);
        assertTrue(resultSubObjectCode.isFinancialSubObjectActiveIndicator());

    }
}
