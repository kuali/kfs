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
package org.kuali.module.financial.bo;

import java.util.ArrayList;

import org.kuali.test.KualiTestBase;

/**
 * This class...
 * 
 * @author Kuali Financial Transactions Red Team ()
 */
public class CashDetailTypeCodeTest extends KualiTestBase {
    CashDetailTypeCode cdtc = null;
    public static final boolean ACTIVE_IND = true;
    public static final String GUID = "123456789012345678901234567890123456";
    public static final String NAME = "NAME";
    public static final String CODE = "CODE";
    public static final Long VER_NBR = new Long(1);
    public static final String DESCRIPTION = "Description";

    /**
     * Constructs a CashDetailTypeCodeTest.java.
     */
    public CashDetailTypeCodeTest() {
        super();
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        cdtc = new CashDetailTypeCode();
        cdtc.setActive(ACTIVE_IND);
        cdtc.setCode(CODE);
        cdtc.setExtendedAttributeValues(new ArrayList());
        cdtc.setName(NAME);
        cdtc.setObjectId(GUID);
        cdtc.setVersionNumber(VER_NBR);
        cdtc.setDescription(DESCRIPTION);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        cdtc = null;
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
