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
package org.kuali.core.service;

import java.text.DateFormat;
import java.util.Date;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.codes.FederalFundedCode;
import org.kuali.test.KualiTestBaseWithFixtures;

/**
 * This class tests the FederalFundedCode service.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class FederalFundedCodeServiceTest extends KualiTestBaseWithFixtures {

    private FederalFundedCode ffc;
    private KualiCodeService kualiCodeService;
    private String timestamp;

    /**
     * Performs setup operations before tests are executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        kualiCodeService = SpringServiceLocator.getKualiCodeService();
        timestamp = DateFormat.getDateInstance().format(new Date());
    }

    /**
     * Performs all tests for this service.
     */
    public void testGetByCode_valid_code() {
        ffc = null;
        ffc = (FederalFundedCode) kualiCodeService.getByCode(FederalFundedCode.class, TestConstants.Data5.FEDERAL_FUNDED_CODE1);
        assertNotNull(ffc);
        assertEquals("Known-good code results in expected returned Name.", TestConstants.Data5.FEDERAL_FUNDED_NAME1, ffc.getName());
    }

    public void testGeyByName_valid_name() {
        ffc = null;
        ffc = (FederalFundedCode) kualiCodeService.getByName(FederalFundedCode.class, TestConstants.Data5.FEDERAL_FUNDED_NAME1);
        assertEquals("Known-good name results in expected returned code.", TestConstants.Data5.FEDERAL_FUNDED_CODE1, ffc.getCode());
    }

    public void testGetByCode_invalid_code() {
        ffc = null;
        ffc = (FederalFundedCode) kualiCodeService.getByCode(FederalFundedCode.class, TestConstants.Data5.FEDERAL_FUNDED_CODE_BAD);
        assertNull("Known-bad code returns null object.", ffc);
    }

    public void testGetByName_invalid_name() {
        ffc = null;
        ffc = (FederalFundedCode) kualiCodeService.getByName(FederalFundedCode.class, TestConstants.Data5.FEDERAL_FUNDED_NAME_BAD);
        assertNull("Known-bad code returns null object.", ffc);
    }

    public void testGetByCode_blank_code() {
        ffc = null;
        ffc = (FederalFundedCode) kualiCodeService.getByCode(FederalFundedCode.class, "");
        assertNull("Known-empty code returns null object.", ffc);
    }

    public void testGetByCode_null_code() {
        ffc = null;
        ffc = (FederalFundedCode) kualiCodeService.getByCode(FederalFundedCode.class, null);
        assertNull("Known-null code returns null object.", ffc);
    }

    /*
     * 
     * This method was removed because: 1. Adding new FederalFundedCodes is extremely rare-- this test is not particularly valuable
     * 2. The old test was broken in several ways: a. it did not clean up after itself ala dbunit b. the resolution of the timestamp
     * to modify the name was too low-- the test could only be run successfully once per day c. the final assertion ignored the fact
     * that the new name had been intentionally altered-- it expected the old name to still be there d. the deep copied object had
     * the same primary key as the one intended to replace it
     * 
     * public void testSave() { ffc = null; FederalFundedCode result = null; try { String newName = null; // get the existing value
     * 
     * ffc = (FederalFundedCode) kualiCodeService.getByCode(FederalFundedCode.class, TestConstants.Data5.FEDERAL_FUNDED_CODE1); //
     * cache the old value, create a new value, and modify the object result = (FederalFundedCode)ObjectUtils.deepCopy(ffc) ;
     * newName = ffc.getName() + ":"+timestamp; result.setName(newName); // attempt to save the modified object
     * kualiCodeService.save(result); // open the object byCode() and confirm that the changes were saved result =
     * (FederalFundedCode) kualiCodeService.getByCode(FederalFundedCode.class, TestConstants.Data5.FEDERAL_FUNDED_CODE1);
     * assertEquals("Changes to the document were not persisted to the database.", newName, ffc.getName()); } finally { if (ffc !=
     * null) { kualiCodeService.save(ffc);
     * 
     * result = (FederalFundedCode) kualiCodeService.getByCode(FederalFundedCode.class, TestConstants.Data5.FEDERAL_FUNDED_CODE1);
     * assertEquals("Changes to the document were not persisted to the database.", ffc.getName(), result.getName()); } } }
     */

    public void testActive() {
        // test known-good active code
        ffc = null;
        ffc = (FederalFundedCode) kualiCodeService.getByCode(FederalFundedCode.class, TestConstants.Data5.FEDERAL_FUNDED_CODE1);
        assertTrue("The active code associated with this field is incorrect", ffc.isActive());

    }
}
