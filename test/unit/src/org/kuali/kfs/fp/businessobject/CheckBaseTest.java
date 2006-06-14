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


import java.sql.Date;
import java.util.ArrayList;

import org.kuali.core.util.KualiDecimal;
import org.kuali.test.KualiTestBaseWithSpring;

/**
 * This class...
 * 
 * @author Kuali Financial Transactions Red Team (kualidev@oncourse.iu.edu)
 */
public class CheckBaseTest extends KualiTestBaseWithSpring {
    CheckBase crchk = null;
    public static final KualiDecimal AMOUNT = new KualiDecimal("100.27");
    public static final String GUID = "123456789012345678901234567890123456";
    public static final Long VER_NBR = new Long(1);
    public static final Date DATE = new Date(System.currentTimeMillis());
    public static final String CHECK_NUMBER = "123456";
    public static final String DESCRIPTION = "Description 123.";
    public static final String DOC_HDR_ID = "999999";
    public static final Integer SEQ_ID = new Integer(1);

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        crchk = new CheckBase();
        crchk.setAmount(AMOUNT);
        crchk.setCheckDate(DATE);
        crchk.setCheckNumber(CHECK_NUMBER);
        crchk.setDescription(DESCRIPTION);
        crchk.setFinancialDocumentNumber(DOC_HDR_ID);
        crchk.setExtendedAttributeValues(new ArrayList());
        crchk.setInterimDepositAmount(false);
        crchk.setObjectId(GUID);
        crchk.setSequenceId(SEQ_ID);
        crchk.setVersionNumber(VER_NBR);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        crchk = null;
    }

    public void testCashReceiptCheckPojo() {
        assertEquals(AMOUNT, crchk.getAmount());
        assertEquals(DATE, crchk.getCheckDate());
        assertEquals(CHECK_NUMBER, crchk.getCheckNumber());
        assertEquals(DESCRIPTION, crchk.getDescription());
        assertEquals(DOC_HDR_ID, crchk.getFinancialDocumentNumber());
        assertEquals(0, crchk.getExtendedAttributeValues().size());
        assertEquals(false, crchk.isInterimDepositAmount());
        assertEquals(GUID, crchk.getObjectId());
        assertEquals(SEQ_ID, crchk.getSequenceId());
        assertEquals(VER_NBR, crchk.getVersionNumber());
    }
}
