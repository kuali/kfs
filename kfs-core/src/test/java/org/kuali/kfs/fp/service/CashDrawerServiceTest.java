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
package org.kuali.kfs.fp.service;

import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class tests the Check service.
 */
@ConfigureContext
public class CashDrawerServiceTest extends KualiTestBase {
    private static final String BLANK_CAMPUS_CODE = "";
    private static final String VALID_CAMPUS_CODE = "KO";
    private static final String BLANK_DOC_ID = "    ";
    private static final String VALID_DOC_ID = "1234";
    private static final String OTHER_DOC_ID = "4321";

    /**
     * 
     * This method tests that calling the openCashDrawer method on a CashDrawerService with a blank workgroup name 
     * generates an error.
     */
    public final void testOpenCashDrawer_blankWorkgroup() {
        boolean failedAsExpected = false;

        try {
            SpringContext.getBean(CashDrawerService.class).openCashDrawer(BLANK_CAMPUS_CODE, VALID_DOC_ID);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    /**
     * 
     * This method tests that calling the openCashDrawer method on a CashDrawerService with a blank document id generates
     * an error.
     */
    public final void testOpenCashDrawer_blankDocId() {
        boolean failedAsExpected = false;

        try {
            SpringContext.getBean(CashDrawerService.class).openCashDrawer(VALID_CAMPUS_CODE, BLANK_DOC_ID);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    /**
     * 
     * This method tests the openCashDrawer method under valid conditions.
     */
    public final void testOpenCashDrawer() {
        final String workgroup = VALID_CAMPUS_CODE;

        // open it
        SpringContext.getBean(CashDrawerService.class).openCashDrawer(workgroup, VALID_DOC_ID);

        // make sure it is open
        CashDrawer drawer = SpringContext.getBean(CashDrawerService.class).getByCampusCode(workgroup);
        assertEquals(KFSConstants.CashDrawerConstants.STATUS_OPEN, drawer.getStatusCode());
        assertEquals(VALID_DOC_ID, drawer.getReferenceFinancialDocumentNumber());
    }

    /**
     * 
     * This method tests that calling the closeCashDrawer method on a CashDrawerService with a blank workgroup name 
     * generates an error.
     */
    public final void testCloseCashDrawer_blankWorkgroup() {
        boolean failedAsExpected = false;

        try {
            SpringContext.getBean(CashDrawerService.class).closeCashDrawer(BLANK_CAMPUS_CODE);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    /**
     * 
     * This method tests the closeCashDrawer method under valid conditions.
     */
    public final void testCloseCashDrawer_existent() {
        final String workgroup = VALID_CAMPUS_CODE;

        // close it
        SpringContext.getBean(CashDrawerService.class).closeCashDrawer(workgroup);

        // make sure it is closed
        CashDrawer drawer = SpringContext.getBean(CashDrawerService.class).getByCampusCode(workgroup);
        assertEquals(KFSConstants.CashDrawerConstants.STATUS_CLOSED, drawer.getStatusCode());
        assertNull(drawer.getReferenceFinancialDocumentNumber());
    }

    /**
     * 
     * This method tests that calling the lockCashDrawer method on a CashDrawerService with a blank workgroup name 
     * generates an error.
     */
    public final void testLockCashDrawer_blankWorkgroup() {
        boolean failedAsExpected = false;

        try {
            SpringContext.getBean(CashDrawerService.class).lockCashDrawer(BLANK_CAMPUS_CODE, VALID_DOC_ID);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    /**
     * 
     * This method tests that calling the lockCashDrawer method on a CashDrawerService, when the cash drawer 
     * is closed, an error is generated.
     */
    public final void testLockCashDrawer_closed() {
        final String workgroup = VALID_CAMPUS_CODE;

        // close it
        SpringContext.getBean(CashDrawerService.class).closeCashDrawer(workgroup);

        // lock it
        boolean failedAsExpected = false;
        try {
            SpringContext.getBean(CashDrawerService.class).lockCashDrawer(workgroup, VALID_DOC_ID);
        }
        catch (IllegalStateException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    /**
     * 
     * This method tests that calling the lockCashDrawer method on a CashDrawerService, when the cash drawer 
     * is already locked, an error is generated.
     */
    public final void testLockCashDrawer_alreadyLocked() {
        final String workgroup = VALID_CAMPUS_CODE;

        // open it
        SpringContext.getBean(CashDrawerService.class).openCashDrawer(workgroup, VALID_DOC_ID);

        // lock it twice
        SpringContext.getBean(CashDrawerService.class).lockCashDrawer(workgroup, VALID_DOC_ID);

        boolean failedAsExpected = false;
        try {
            SpringContext.getBean(CashDrawerService.class).lockCashDrawer(workgroup, VALID_DOC_ID);
        }
        catch (IllegalStateException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    /**
     * 
     * This method tests that calling the lockCashDrawer method on a CashDrawerService, when the cash drawer 
     * is already opened by another document, an error is generated.
     */
    public final void testLockCashDrawer_openedByDifferentDocument() {
        final String workgroup = VALID_CAMPUS_CODE;

        // open it
        SpringContext.getBean(CashDrawerService.class).openCashDrawer(workgroup, OTHER_DOC_ID);

        // lock it
        boolean failedAsExpected = false;
        try {
            SpringContext.getBean(CashDrawerService.class).lockCashDrawer(workgroup, VALID_DOC_ID);
        }
        catch (IllegalStateException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    /**
     * 
     * This method tests the lockCashDrawer method under valid conditions.
     */
    public final void testLockCashDrawer_open() {
        final String workgroup = VALID_CAMPUS_CODE;

        // open it
        SpringContext.getBean(CashDrawerService.class).openCashDrawer(workgroup, VALID_DOC_ID);

        // lock it
        SpringContext.getBean(CashDrawerService.class).lockCashDrawer(workgroup, VALID_DOC_ID);

        // verify that it is locked
        CashDrawer drawer = SpringContext.getBean(CashDrawerService.class).getByCampusCode(workgroup);
        assertEquals(KFSConstants.CashDrawerConstants.STATUS_LOCKED, drawer.getStatusCode());
        assertEquals(VALID_DOC_ID, drawer.getReferenceFinancialDocumentNumber());
    }

    /**
     * 
     * This method tests that calling the unlockCashDrawer method on a CashDrawerService with a blank workgroup name 
     * generates an error.
     */
    public final void testUnlockCashDrawer_blankWorkgroup() {
        boolean failedAsExpected = false;

        try {
            SpringContext.getBean(CashDrawerService.class).unlockCashDrawer(BLANK_CAMPUS_CODE, VALID_DOC_ID);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    /**
     * 
     * This method tests that calling the lockCashDrawer method on a CashDrawerService, when the cash drawer is already open,
     * an error is generated.
     */
    public final void testUnlockCashDrawer_open() {
        final String workgroup = VALID_CAMPUS_CODE;

        // open it
        SpringContext.getBean(CashDrawerService.class).openCashDrawer(workgroup, VALID_DOC_ID);

        // unlock it
        boolean failedAsExpected = false;
        try {
            SpringContext.getBean(CashDrawerService.class).unlockCashDrawer(workgroup, VALID_DOC_ID);
        }
        catch (IllegalStateException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    /**
     * 
     * This method tests the unlockCashDrawer method under valid conditions.
     */
    public final void testUnlockCashDrawer_locked() {
        final String workgroup = VALID_CAMPUS_CODE;

        // lock it
        SpringContext.getBean(CashDrawerService.class).openCashDrawer(workgroup, VALID_DOC_ID);
        SpringContext.getBean(CashDrawerService.class).lockCashDrawer(workgroup, VALID_DOC_ID);

        // unlock it
        SpringContext.getBean(CashDrawerService.class).unlockCashDrawer(workgroup, VALID_DOC_ID);

        // verify that it is unlocked
        CashDrawer drawer = SpringContext.getBean(CashDrawerService.class).getByCampusCode(workgroup);
        assertEquals(KFSConstants.CashDrawerConstants.STATUS_OPEN, drawer.getStatusCode());
        assertEquals(VALID_DOC_ID, drawer.getReferenceFinancialDocumentNumber());
    }

    /**
     * 
     * This method tests that calling the unlockCashDrawer method on a CashDrawerService, when the cash drawer 
     * is already locked by another document, an error is generated.
     */
    public final void testUnlockCashDrawer_lockedByDifferentDocumentId() {
        final String workgroup = VALID_CAMPUS_CODE;

        // lock it
        SpringContext.getBean(CashDrawerService.class).openCashDrawer(workgroup, OTHER_DOC_ID);
        SpringContext.getBean(CashDrawerService.class).lockCashDrawer(workgroup, OTHER_DOC_ID);

        // unlock it
        boolean failedAsExpected = false;
        try {
            SpringContext.getBean(CashDrawerService.class).unlockCashDrawer(workgroup, VALID_DOC_ID);
        }
        catch (IllegalStateException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    /**
     * 
     * This method tests that trying to retrieve a cash drawer by a workgroup name, when the workgroup name provided is 
     * blank, will generate an error.
     */
    public final void testGetByCampusCode_blankWorkgroup() {
        boolean failedAsExpected = false;

        try {
            SpringContext.getBean(CashDrawerService.class).getByCampusCode("  ");
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    /**
     * 
     * This method tests that trying to retrieve a cash drawer by a workgroup name, when the workgroup does not exist, 
     * will generate an error.
     */
    public final void testGetByCampusCode_nonexistentWorkgroup() {
        CashDrawer d = SpringContext.getBean(CashDrawerService.class).getByCampusCode("foo");

        assertNull(d);
    }

    /**
     * 
     * This method tests the getByCampusCode method under valid conditions.
     */
    public final void testGetByCampusCode_existingWorkgroup() {
        final String workgroup = VALID_CAMPUS_CODE;

        CashDrawer d = SpringContext.getBean(CashDrawerService.class).getByCampusCode(workgroup);

        assertNotNull(d);
        assertEquals(workgroup, d.getCampusCode());
        assertEquals(KFSConstants.CashDrawerConstants.STATUS_CLOSED, d.getStatusCode());
    }


    // utility methods

    /**
     * 
     * This method performs the necessary steps to create a new cash drawer in the database for the given campus code.
     * @param campusCode The code of the campus of the cash drawer being created.
     */
    private void createCashDrawer(String campusCode) {
        CashDrawer cd = new CashDrawer();
        cd.setCampusCode(campusCode);
        cd.setStatusCode(KFSConstants.CashDrawerConstants.STATUS_CLOSED);
        SpringContext.getBean(BusinessObjectService.class).save(cd);
    }
}
