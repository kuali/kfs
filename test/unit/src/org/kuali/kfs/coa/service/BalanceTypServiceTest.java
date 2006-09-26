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

import java.util.HashMap;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.test.KualiTestBaseWithFixtures;
import org.kuali.test.WithTestSpringContext;

/**
 * This class tests the BalanceType service.
 * 
 * @author Kuali Nervous System Team ()
 */
@WithTestSpringContext
public class BalanceTypServiceTest extends KualiTestBaseWithFixtures {
    BusinessObjectService businessObjectService;

    private static final boolean ACTIVE = true;
    private static final boolean BAL_TYPE_ENCUMB = true;
    private static final String BAL_TYPE_CODE = "ZZ";
    private static final String BAL_TYPE_NAME = "Z NAME";
    private static final String GUID = "123456789012345678901234567890123456";
    private static final Long VER_NBR = new Long(1);
    private static final boolean OFFSET_GEN = false;
    private static final String SHORT_NAME = "Z SHORT";

    private static final String ACTUAL_BAL_TYPE_CODE = "AC";

    /**
     * Performs setup operations before tests are executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        businessObjectService = SpringServiceLocator.getBusinessObjectService();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        businessObjectService = null;
    }

    public void testCreateLookupDelete1() {
        // create
        BalanceTyp bal = new BalanceTyp();
        bal.setActive(true);
        bal.setFinBalanceTypeEncumIndicator(true);
        bal.setCode(BAL_TYPE_CODE);
        bal.setName(BAL_TYPE_NAME);
        bal.setObjectId(GUID);
        bal.setFinancialOffsetGenerationIndicator(OFFSET_GEN);
        bal.setFinancialBalanceTypeShortNm(SHORT_NAME);
        bal.setVersionNumber(VER_NBR);

        businessObjectService.save(bal);

        // lookup
        HashMap map = new HashMap();
        map.put("code", BAL_TYPE_CODE);
        BalanceTyp bal2 = (BalanceTyp) businessObjectService.findByPrimaryKey(BalanceTyp.class, map);
        assertNotNull("Should be a valid object.", bal2);
        assertEquals("Known-good code results in expected returned Name.", BAL_TYPE_NAME, bal2.getName());

        // delete
        businessObjectService.delete(bal2);

        // try to lookup again
        map = new HashMap();
        map.put("code", BAL_TYPE_CODE);
        BalanceTyp bal3 = (BalanceTyp) businessObjectService.findByPrimaryKey(BalanceTyp.class, map);
        assertNull("Shouldn't be a valid object.", bal3);
    }

    /*
     * Disable this test because no data in database yet RO 9-22-05
     * 
     * public void testActualBalanceTypeLookup() { //test known-good byCode BalanceTyp bal =
     * SpringServiceLocator.getBalanceTypService().getActualBalanceTyp(); assertNotNull("Should be a valid object.", bal);
     * assertEquals(ACTUAL_BAL_TYPE_CODE, bal.getCode()); }
     */
}
