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
package org.kuali.module.financial.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kuali.Constants;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.test.KualiTestBaseWithSpring;

/**
 * This class tests the Check service.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CashDrawerServiceTest extends KualiTestBaseWithSpring {
    private static final String KNOWN_WORKGROUP = Constants.CashReceiptConstants.TEST_CASH_RECEIPT_VERIFICATION_UNIT;
    private static final String PREEXISTING_WORKGROUP = "KUALI_BRSR_BL";
    private static final String UNKNOWN_WORKGROUP = "foo";

    private static final String BLANK_DOC_ID = "    ";
    private static final String VALID_DOC_ID = "1234";


    private CashDrawerService cashDrawerService;
    private BusinessObjectService boService;


    protected void setUp() throws Exception {
        super.setUp();
        cashDrawerService = SpringServiceLocator.getCashDrawerService();
        boService = SpringServiceLocator.getBusinessObjectService();
    }


    public final void testOpenCashDrawer_blankWorkgroup() {
        boolean failedAsExpected = false;

        try {
            cashDrawerService.openCashDrawer("  ", VALID_DOC_ID);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testOpenCashDrawer_blankDocId() {
        boolean failedAsExpected = false;

        try {
            cashDrawerService.openCashDrawer(KNOWN_WORKGROUP, BLANK_DOC_ID);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    private void deleteIfExists(String workgroupName) {
        Map deleteCriteria = new HashMap();
        deleteCriteria.put("workgroupName", workgroupName);
        boService.deleteMatching(CashDrawer.class, deleteCriteria);
    }

    public final void testOpenCashDrawer_nonexistent() {
        // make sure it doesn't exist
        String nonexistentWorkgroup = "testNonWorkgroup";
        deleteIfExists(nonexistentWorkgroup);

        // open it
        cashDrawerService.openCashDrawer(nonexistentWorkgroup, VALID_DOC_ID);

        // verify that it is open
        CashDrawer drawer = cashDrawerService.getByWorkgroupName(nonexistentWorkgroup);
        assertEquals(Constants.CashDrawerConstants.STATUS_OPEN, drawer.getStatusCode());
        assertEquals(VALID_DOC_ID, drawer.getFinancialDocumentReferenceNumber());

        // clean up
        deleteIfExists(nonexistentWorkgroup);
    }

    public final void testOpenCashDrawer() {
        String testWorkgroup = "testWorkgroup2";

        // make sure it is open
        cashDrawerService.openCashDrawer(testWorkgroup, VALID_DOC_ID);
        CashDrawer drawer = cashDrawerService.getByWorkgroupName(testWorkgroup);
        assertEquals(Constants.CashDrawerConstants.STATUS_OPEN, drawer.getStatusCode());
        assertEquals(VALID_DOC_ID, drawer.getFinancialDocumentReferenceNumber());

        // clean up after yourself
        deleteIfExists(testWorkgroup);
    }


    public final void testCloseCashDrawer_blankWorkgroup() {
        boolean failedAsExpected = false;

        try {
            cashDrawerService.closeCashDrawer("  ", VALID_DOC_ID);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testCloseCashDrawer_blankDocId() {
        boolean failedAsExpected = false;

        try {
            cashDrawerService.closeCashDrawer(KNOWN_WORKGROUP, BLANK_DOC_ID);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testCloseCashDrawer_nonexistent() {
        // make sure it doesn't exist
        String nonexistentWorkgroup = "testNonWorkgroup";
        deleteIfExists(nonexistentWorkgroup);

        // open it
        cashDrawerService.closeCashDrawer(nonexistentWorkgroup, VALID_DOC_ID);

        // verify that it is closed
        CashDrawer drawer = cashDrawerService.getByWorkgroupName(nonexistentWorkgroup);
        assertEquals(Constants.CashDrawerConstants.STATUS_CLOSED, drawer.getStatusCode());
        assertEquals(VALID_DOC_ID, drawer.getFinancialDocumentReferenceNumber());

        // clean up
        deleteIfExists(nonexistentWorkgroup);
    }

    public final void testCloseCashDrawer() {
        String testWorkgroup = "testWorkgroup2";

        // make sure it is closed
        cashDrawerService.closeCashDrawer(testWorkgroup, VALID_DOC_ID);
        CashDrawer drawer = cashDrawerService.getByWorkgroupName(testWorkgroup);
        assertEquals(Constants.CashDrawerConstants.STATUS_CLOSED, drawer.getStatusCode());
        assertEquals(VALID_DOC_ID, drawer.getFinancialDocumentReferenceNumber());

        // clean up after yourself
        deleteIfExists(testWorkgroup);
    }


    public final void testGetByWorkgroupName_blankWorkgroup() {
        boolean failedAsExpected = false;

        try {
            cashDrawerService.getByWorkgroupName("  ");
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testGetByWorkgroupName_nonexistentWorkgroup() {
        CashDrawer d = cashDrawerService.getByWorkgroupName(UNKNOWN_WORKGROUP);

        assertNull(d);
    }

    public final void testGetByWorkgroupName_existingWorkgroup() {
        CashDrawer d = cashDrawerService.getByWorkgroupName(PREEXISTING_WORKGROUP);

        assertNotNull(d);
        assertEquals(PREEXISTING_WORKGROUP,d.getWorkgroupName());
    }


    public final void testLifeCycle() {
        final String RANDOM_WORKGROUP_NAME = "testWorkgroup-" + new Date().getTime();

        boolean deleteSucceeded = false;

        CashDrawer preExisting = cashDrawerService.getByWorkgroupName(RANDOM_WORKGROUP_NAME);
        assertNull(preExisting);

        CashDrawer created = new CashDrawer();
        created.setWorkgroupName(RANDOM_WORKGROUP_NAME);
        created.setStatusCode(Constants.CashDrawerConstants.STATUS_CLOSED);

        CashDrawer retrieved = null;
        try {
            CashDrawer saved = cashDrawerService.save(created, VALID_DOC_ID);
            assertNotNull(saved);

            retrieved = cashDrawerService.getByWorkgroupName(RANDOM_WORKGROUP_NAME);
            assertNotNull(retrieved);

            // compare
            assertEquals(created.getWorkgroupName(), saved.getWorkgroupName());
            assertEquals(created.getStatusCode(), saved.getStatusCode());
            assertEquals(VALID_DOC_ID, saved.getFinancialDocumentReferenceNumber());
        }
        finally {
            // delete it
            if (retrieved != null) {
                boService.delete(retrieved);
            }
        }

        // verify that the delete succeeded
        retrieved = cashDrawerService.getByWorkgroupName(RANDOM_WORKGROUP_NAME);
        assertNull(retrieved);
    }
}
