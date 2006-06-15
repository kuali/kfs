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
import org.kuali.test.KualiTestBaseWithFixtures;

/**
 * This class tests the Check service.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CashDrawerServiceTest extends KualiTestBaseWithFixtures {
    private static final String BLANK_WORKGROUP_NAME = "";
    private static final String VALID_WORKGROUP_NAME = "testWorkgroup";
    private static final String BLANK_DOC_ID = "    ";
    private static final String VALID_DOC_ID = "1234";
    private static final String OTHER_DOC_ID = "4321";


    private CashDrawerService cashDrawerService;
    private BusinessObjectService boService;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        cashDrawerService = SpringServiceLocator.getCashDrawerService();
        boService = SpringServiceLocator.getBusinessObjectService();
    }

    @Override
    protected void tearDown() throws Exception {
        deleteCashDrawer(VALID_WORKGROUP_NAME);

        super.tearDown();
    }


    public final void testOpenCashDrawer_blankWorkgroup() {
        boolean failedAsExpected = false;

        try {
            cashDrawerService.openCashDrawer(BLANK_WORKGROUP_NAME, VALID_DOC_ID);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testOpenCashDrawer_blankDocId() {
        boolean failedAsExpected = false;

        try {
            cashDrawerService.openCashDrawer(VALID_WORKGROUP_NAME, BLANK_DOC_ID);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }


    public final void testOpenCashDrawer_nonexistent() {
        final String workgroup = VALID_WORKGROUP_NAME;

        // make sure it doesn't exist
        deleteCashDrawer(workgroup);

        // open it
        cashDrawerService.openCashDrawer(workgroup, VALID_DOC_ID);

        // verify that it is open
        CashDrawer drawer = cashDrawerService.getByWorkgroupName(workgroup, false);
        assertEquals(Constants.CashDrawerConstants.STATUS_OPEN, drawer.getStatusCode());
        assertEquals(VALID_DOC_ID, drawer.getReferenceFinancialDocumentNumber());
    }

    public final void testOpenCashDrawer() {
        final String workgroup = VALID_WORKGROUP_NAME;

        // make sure it exists
        createCashDrawer(workgroup);

        // open it
        cashDrawerService.openCashDrawer(workgroup, VALID_DOC_ID);

        // make sure it is open
        CashDrawer drawer = cashDrawerService.getByWorkgroupName(workgroup, false);
        assertEquals(Constants.CashDrawerConstants.STATUS_OPEN, drawer.getStatusCode());
        assertEquals(VALID_DOC_ID, drawer.getReferenceFinancialDocumentNumber());
    }


    public final void testCloseCashDrawer_blankWorkgroup() {
        boolean failedAsExpected = false;

        try {
            cashDrawerService.closeCashDrawer(BLANK_WORKGROUP_NAME);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testCloseCashDrawer_nonexistent() {
        final String workgroup = VALID_WORKGROUP_NAME;

        // make sure it doesn't exist
        deleteCashDrawer(workgroup);

        // close it
        cashDrawerService.closeCashDrawer(workgroup);

        // verify that it is closed
        CashDrawer drawer = cashDrawerService.getByWorkgroupName(workgroup, false);
        assertEquals(Constants.CashDrawerConstants.STATUS_CLOSED, drawer.getStatusCode());
        assertNull(drawer.getReferenceFinancialDocumentNumber());
    }

    public final void testCloseCashDrawer_existent() {
        final String workgroup = VALID_WORKGROUP_NAME;

        // make sure it exists
        createCashDrawer(workgroup);

        // close it
        cashDrawerService.closeCashDrawer(workgroup);

        // make sure it is closed
        CashDrawer drawer = cashDrawerService.getByWorkgroupName(workgroup, false);
        assertEquals(Constants.CashDrawerConstants.STATUS_CLOSED, drawer.getStatusCode());
        assertNull(drawer.getReferenceFinancialDocumentNumber());
    }


    public final void testLockCashDrawer_blankWorkgroup() {
        boolean failedAsExpected = false;

        try {
            cashDrawerService.lockCashDrawer(BLANK_WORKGROUP_NAME, VALID_DOC_ID);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testLockCashDrawer_nonexistent() {
        final String workgroup = VALID_WORKGROUP_NAME;

        // make sure it doesn't exist
        deleteCashDrawer(workgroup);

        // lock it
        boolean failedAsExpected = false;
        try {
            cashDrawerService.lockCashDrawer(workgroup, VALID_DOC_ID);
        }
        catch (IllegalStateException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testLockCashDrawer_closed() {
        final String workgroup = VALID_WORKGROUP_NAME;

        // close it
        cashDrawerService.closeCashDrawer(workgroup);

        // lock it
        boolean failedAsExpected = false;
        try {
            cashDrawerService.lockCashDrawer(workgroup, VALID_DOC_ID);
        }
        catch (IllegalStateException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testLockCashDrawer_alreadyLocked() {
        final String workgroup = VALID_WORKGROUP_NAME;

        // open it
        cashDrawerService.openCashDrawer(workgroup, VALID_DOC_ID);

        // lock it twice
        cashDrawerService.lockCashDrawer(workgroup, VALID_DOC_ID);

        boolean failedAsExpected = false;
        try {
            cashDrawerService.lockCashDrawer(workgroup, VALID_DOC_ID);
        }
        catch (IllegalStateException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testLockCashDrawer_openedByDifferentDocument() {
        final String workgroup = VALID_WORKGROUP_NAME;

        // open it
        cashDrawerService.openCashDrawer(workgroup, OTHER_DOC_ID);

        // lock it
        boolean failedAsExpected = false;
        try {
            cashDrawerService.lockCashDrawer(workgroup, VALID_DOC_ID);
        }
        catch (IllegalStateException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }


    public final void testLockCashDrawer_open() {
        final String workgroup = VALID_WORKGROUP_NAME;

        // open it
        cashDrawerService.openCashDrawer(workgroup, VALID_DOC_ID);

        // lock it
        cashDrawerService.lockCashDrawer(workgroup, VALID_DOC_ID);

        // verify that it is locked
        CashDrawer drawer = cashDrawerService.getByWorkgroupName(workgroup, false);
        assertEquals(Constants.CashDrawerConstants.STATUS_LOCKED, drawer.getStatusCode());
        assertEquals(VALID_DOC_ID, drawer.getReferenceFinancialDocumentNumber());
    }


    public final void testUnlockCashDrawer_blankWorkgroup() {
        boolean failedAsExpected = false;

        try {
            cashDrawerService.unlockCashDrawer(BLANK_WORKGROUP_NAME, VALID_DOC_ID);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testUnlockCashDrawer_nonexistent() {
        final String workgroup = VALID_WORKGROUP_NAME;

        // make sure it doesn't exist
        deleteCashDrawer(workgroup);

        // lock it
        boolean failedAsExpected = false;
        try {
            cashDrawerService.unlockCashDrawer(workgroup, VALID_DOC_ID);
        }
        catch (IllegalStateException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testUnlockCashDrawer_open() {
        final String workgroup = VALID_WORKGROUP_NAME;

        // open it
        cashDrawerService.openCashDrawer(workgroup, VALID_DOC_ID);

        // unlock it
        boolean failedAsExpected = false;
        try {
            cashDrawerService.unlockCashDrawer(workgroup, VALID_DOC_ID);
        }
        catch (IllegalStateException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testUnlockCashDrawer_locked() {
        final String workgroup = VALID_WORKGROUP_NAME;

        // lock it
        cashDrawerService.openCashDrawer(workgroup, VALID_DOC_ID);
        cashDrawerService.lockCashDrawer(workgroup, VALID_DOC_ID);

        // unlock it
        cashDrawerService.unlockCashDrawer(workgroup, VALID_DOC_ID);

        // verify that it is unlocked
        CashDrawer drawer = cashDrawerService.getByWorkgroupName(workgroup, false);
        assertEquals(Constants.CashDrawerConstants.STATUS_OPEN, drawer.getStatusCode());
        assertEquals(VALID_DOC_ID, drawer.getReferenceFinancialDocumentNumber());
    }


    public final void testUnlockCashDrawer_lockedByDifferentDocumentId() {
        final String workgroup = VALID_WORKGROUP_NAME;

        // lock it
        cashDrawerService.openCashDrawer(workgroup, OTHER_DOC_ID);
        cashDrawerService.lockCashDrawer(workgroup, OTHER_DOC_ID);

        // unlock it
        boolean failedAsExpected = false;
        try {
            cashDrawerService.unlockCashDrawer(workgroup, VALID_DOC_ID);
        }
        catch (IllegalStateException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }


    public final void testGetByWorkgroupName_blankWorkgroup() {
        boolean failedAsExpected = false;

        try {
            cashDrawerService.getByWorkgroupName("  ", false);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testGetByWorkgroupName_nonexistentWorkgroup() {
        CashDrawer d = cashDrawerService.getByWorkgroupName("foo", false);

        assertNull(d);
    }

    public final void testGetByWorkgroupName_existingWorkgroup() {
        final String workgroup = VALID_WORKGROUP_NAME;

        createCashDrawer(VALID_WORKGROUP_NAME);

        CashDrawer d = cashDrawerService.getByWorkgroupName(workgroup, false);

        assertNotNull(d);
        assertEquals(workgroup, d.getWorkgroupName());
        assertEquals(Constants.CashDrawerConstants.STATUS_CLOSED, d.getStatusCode());
    }


    public final void testLifeCycle() {
        final String RANDOM_WORKGROUP_NAME = "testWorkgroup-" + new Date().getTime();

        boolean deleteSucceeded = false;

        CashDrawer preExisting = cashDrawerService.getByWorkgroupName(RANDOM_WORKGROUP_NAME, false);
        assertNull(preExisting);

        CashDrawer created = new CashDrawer();
        created.setWorkgroupName(RANDOM_WORKGROUP_NAME);
        created.setStatusCode(Constants.CashDrawerConstants.STATUS_CLOSED);

        CashDrawer retrieved = null;
        try {
            boService.save(created);

            retrieved = cashDrawerService.getByWorkgroupName(RANDOM_WORKGROUP_NAME, false);
            assertNotNull(retrieved);

            // compare
            assertEquals(created.getWorkgroupName(), retrieved.getWorkgroupName());
            assertEquals(created.getStatusCode(), retrieved.getStatusCode());
            assertNull(retrieved.getReferenceFinancialDocumentNumber());
        }
        finally {
            // delete it
            if (retrieved != null) {
                boService.delete(retrieved);
            }
        }

        // verify that the delete succeeded
        retrieved = cashDrawerService.getByWorkgroupName(RANDOM_WORKGROUP_NAME, false);
        assertNull(retrieved);
    }


    // utility methods
    private void deleteCashDrawer(String workgroupName) {
        Map deleteCriteria = new HashMap();
        deleteCriteria.put("workgroupName", workgroupName);
        boService.deleteMatching(CashDrawer.class, deleteCriteria);
    }

    private void createCashDrawer(String workgroupName) {
        deleteCashDrawer(workgroupName);

        CashDrawer cd = new CashDrawer();
        cd.setWorkgroupName(workgroupName);
        cd.setStatusCode(Constants.CashDrawerConstants.STATUS_CLOSED);
        boService.save(cd);
    }
}
