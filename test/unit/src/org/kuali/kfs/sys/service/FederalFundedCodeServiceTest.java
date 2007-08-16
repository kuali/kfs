/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.core.service;

import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.KualiCodeService;
import org.kuali.module.chart.bo.codes.FederalFundedCode;
import org.kuali.test.ConfigureContext;

/**
 * This class tests the FederalFundedCode service.
 * 
 * 
 */

@ConfigureContext
public class FederalFundedCodeServiceTest extends KualiTestBase {

    private FederalFundedCode ffc;

    /**
     * Performs all tests for this service.
     */
    public void testGetByCode_valid_code() {
        ffc = null;
        ffc = (FederalFundedCode) SpringContext.getBean(KualiCodeService.class).getByCode(FederalFundedCode.class, TestConstants.Data5.FEDERAL_FUNDED_CODE1);
        assertNotNull(ffc);
        assertEquals("Known-good code results in expected returned Name.", TestConstants.Data5.FEDERAL_FUNDED_NAME1, ffc.getName());
    }

    public void testGetByName_valid_name() {
        ffc = null;
        ffc = (FederalFundedCode) SpringContext.getBean(KualiCodeService.class).getByName(FederalFundedCode.class, TestConstants.Data5.FEDERAL_FUNDED_NAME1);
        assertEquals("Known-good name results in expected returned code.", TestConstants.Data5.FEDERAL_FUNDED_CODE1, ffc.getCode());
    }

    public void testGetByCode_invalid_code() {
        ffc = null;
        ffc = (FederalFundedCode) SpringContext.getBean(KualiCodeService.class).getByCode(FederalFundedCode.class, TestConstants.Data5.FEDERAL_FUNDED_CODE_BAD);
        assertNull("Known-bad code returns null object.", ffc);
    }

    public void testGetByName_invalid_name() {
        ffc = null;
        ffc = (FederalFundedCode) SpringContext.getBean(KualiCodeService.class).getByName(FederalFundedCode.class, TestConstants.Data5.FEDERAL_FUNDED_NAME_BAD);
        assertNull("Known-bad name returns null object.", ffc);
    }

    public void testGetByCode_blank_code() {
        ffc = null;
        ffc = (FederalFundedCode) SpringContext.getBean(KualiCodeService.class).getByCode(FederalFundedCode.class, "");
        assertNull("Known-empty code returns null object.", ffc);
    }

    public void testGetByCode_null_code() {
        ffc = null;
        ffc = (FederalFundedCode) SpringContext.getBean(KualiCodeService.class).getByCode(FederalFundedCode.class, null);
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
     * public void testSave() { ffc = null; FederalFundedCode result = null;  String timestamp = DateFormat.getDateInstance().format(new Date()); 
     * try { String newName = null; // get the existing value
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
        ffc = (FederalFundedCode) SpringContext.getBean(KualiCodeService.class).getByCode(FederalFundedCode.class, TestConstants.Data5.FEDERAL_FUNDED_CODE1);
        assertTrue("The active code associated with this field is incorrect", ffc.isActive());

    }
}
