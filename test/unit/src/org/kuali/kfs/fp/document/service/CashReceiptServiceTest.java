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

import org.kuali.Constants;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.test.KualiTestBaseWithFixtures;

public class CashReceiptServiceTest extends KualiTestBaseWithFixtures {
    private static final String KNOWN_CAMPUS_CD = Constants.CashReceiptConstants.TEST_CASH_RECEIPT_CAMPUS_LOCATION_CODE;
    private static final String KNOWN_UNIT_NAME = Constants.CashReceiptConstants.TEST_CASH_RECEIPT_VERIFICATION_UNIT;

    private static final String UNKNOWN_CAMPUS_CD = Constants.CashReceiptConstants.DEFAULT_CASH_RECEIPT_CAMPUS_LOCATION_CODE;
    private static final String UNKNOWN_UNIT_NAME = Constants.CashReceiptConstants.DEFAULT_CASH_RECEIPT_VERIFICATION_UNIT;


    CashReceiptService crs;

    protected void setUp() throws Exception {
        super.setUp();

        crs = SpringServiceLocator.getCashReceiptService();
    }


    public final void testGetCampusCodeForCashReceiptVerificationUnit_blankVerificationUnit() {
        boolean failedAsExpected = false;

        try {
            crs.getCampusCodeForCashReceiptVerificationUnit(" ");
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testGetCampusCodeForCashReceiptVerificationUnit_unknownVerificationUnit() {
        String returnedCode = crs.getCampusCodeForCashReceiptVerificationUnit(UNKNOWN_UNIT_NAME);

        // TODO: once we start doing lookups, this test should be verifying that looking up with an unknown unit name returns null
        assertNotNull(returnedCode);
        assertEquals(UNKNOWN_CAMPUS_CD, returnedCode);
    }

    public final void testGetCampusCodeForCashReceiptVerificationUnit_knownVerificationUnit() {
        String returnedCode = crs.getCampusCodeForCashReceiptVerificationUnit(KNOWN_UNIT_NAME);

        assertNotNull(returnedCode);
        assertEquals(KNOWN_CAMPUS_CD, returnedCode);
    }


    public final void testGetCashReceiptVerificationUnitForCampusCode_blankCampusCode() {
        boolean failedAsExpected = false;

        try {
            crs.getCashReceiptVerificationUnitForCampusCode(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testGetCashReceiptVerificationUnitForCampusCode_unknownCampusCode() {
        String returnedUnit = crs.getCashReceiptVerificationUnitForCampusCode(UNKNOWN_CAMPUS_CD);

        // TODO: once we start doing lookups, this test should be verifying that lookup up with an unknown campus code returns null
        assertNotNull(returnedUnit);
        assertEquals(UNKNOWN_UNIT_NAME, returnedUnit);
    }

    public final void testGetCashReceiptVerificationUnitForCampusCode_knownCampusCode() {
        String returnedUnit = crs.getCashReceiptVerificationUnitForCampusCode(KNOWN_CAMPUS_CD);

        assertNotNull(returnedUnit);
        assertEquals(KNOWN_UNIT_NAME, returnedUnit);
    }


    public final void testGetCashReceiptVerificationUnit_nullUser() {
        boolean failedAsExpected = false;

        try {
            crs.getCashReceiptVerificationUnit(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testGetCashReceiptVerificationUnit_validUser() {
        String expectedUnit = Constants.CashReceiptConstants.DEFAULT_CASH_RECEIPT_VERIFICATION_UNIT;

        String unit = crs.getCashReceiptVerificationUnit(GlobalVariables.getUserSession().getKualiUser());
        assertEquals(expectedUnit, unit);
    }
    // TODO: once we start doing actual lookups, add a test for (user not in any verification unit)
}
