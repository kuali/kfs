/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.chart.service;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.KualiCodeService;
import org.kuali.module.chart.bo.codes.MandatoryTransferEliminationCode;
import org.kuali.test.ConfigureContext;
import org.kuali.test.suite.AnnotationTestSuite;
import org.kuali.test.suite.CrossSectionSuite;

/**
 * This class tests the MandatoryTransferEliminationCode service.
 */
@AnnotationTestSuite(CrossSectionSuite.class)
@ConfigureContext
public class MandatoryTransferEliminationCodeServiceTest extends KualiTestBase {

    private static final String GOOD_CODE = "N";
    private static final String GOOD_NAME = "NEITHER";
    private static final String NONEXISTENT_CODE = "A"; // This code is not in the database. Please do not add it, or you will break
                                                        // this test.
    private static final String NONEXISTENT_NAME = "BAD";

    /**
     * @see org.kuali.test.KualiTestBase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validateTestFixtures();
    }

    /**
     * Performs miscellaneous tests for this service.
     */
    public void testByCode_valid_name() {
        // test known-good byCode
        MandatoryTransferEliminationCode mtec = (MandatoryTransferEliminationCode) SpringContext.getBean(KualiCodeService.class).getByCode(MandatoryTransferEliminationCode.class, GOOD_CODE);
        assertEquals("Known-good code results in expected returned Name.", GOOD_NAME, mtec.getName());
    }

    public void testByName_valid_name() {
        // test known-good byName
        MandatoryTransferEliminationCode mtec = (MandatoryTransferEliminationCode) SpringContext.getBean(KualiCodeService.class).getByName(MandatoryTransferEliminationCode.class, GOOD_NAME);
        assertEquals("Known-good name results in expected returned code.", GOOD_CODE, mtec.getCode());
    }

    public void stestByCode_invalid_name() {
        // test known-bad byCode
        MandatoryTransferEliminationCode mtec = (MandatoryTransferEliminationCode) SpringContext.getBean(KualiCodeService.class).getByCode(MandatoryTransferEliminationCode.class, NONEXISTENT_CODE);
        assertNull("Known-bad code returns null object.", mtec);
    }

    public void testByName_invalid_name() {
        // test known-bad byName
        MandatoryTransferEliminationCode mtec = (MandatoryTransferEliminationCode) SpringContext.getBean(KualiCodeService.class).getByName(MandatoryTransferEliminationCode.class, NONEXISTENT_NAME);
        assertNull("Known-bad code returns null object.", mtec);
    }

    public void testByCode_empty() {
        // test known-bad byCode
        MandatoryTransferEliminationCode mtec = (MandatoryTransferEliminationCode) SpringContext.getBean(KualiCodeService.class).getByCode(MandatoryTransferEliminationCode.class, "");
        assertNull("Known-empty code returns null object.", mtec);
    }

    public void testByCode_null() {
        // test known-bad byCode
        MandatoryTransferEliminationCode mtec = (MandatoryTransferEliminationCode) SpringContext.getBean(KualiCodeService.class).getByCode(MandatoryTransferEliminationCode.class, null);
        assertNull("Known-null code returns null object.", mtec);
    }

    public void testSave() {
        String oldName;
        String newName;

        // get the existing value
        MandatoryTransferEliminationCode mtec = (MandatoryTransferEliminationCode) SpringContext.getBean(KualiCodeService.class).getByCode(MandatoryTransferEliminationCode.class, GOOD_CODE);

        // cache the old value, create a new value, and modify the object
        oldName = mtec.getName();
        newName = oldName + "2";
        mtec.setName(newName);

        // attempt to save the modified object
        SpringContext.getBean(BusinessObjectService.class).save(mtec);

        // open the object byCode() and confirm that the changes were saved
        mtec = null;
        mtec = (MandatoryTransferEliminationCode) SpringContext.getBean(KualiCodeService.class).getByCode(MandatoryTransferEliminationCode.class, GOOD_CODE);
        assertEquals("Changes to the document were not persisted to the database.", newName, mtec.getName());

        // revert back to the old name if it worked
        mtec.setName(oldName);
        SpringContext.getBean(BusinessObjectService.class).save(mtec);


        mtec = null;
        mtec = (MandatoryTransferEliminationCode) SpringContext.getBean(KualiCodeService.class).getByCode(MandatoryTransferEliminationCode.class, GOOD_CODE);
        assertEquals("Changes to the document were not persisted to the database.", oldName, mtec.getName());
    }

    public void testActive() {

        // test known-good active code
        MandatoryTransferEliminationCode mtec = (MandatoryTransferEliminationCode) SpringContext.getBean(KualiCodeService.class).getByCode(MandatoryTransferEliminationCode.class, GOOD_CODE);
        assertEquals("The active code associated with this field is incorrect", true, mtec.isActive());

    }

    private void validateTestFixtures() {
        MandatoryTransferEliminationCode code = (MandatoryTransferEliminationCode) SpringContext.getBean(KualiCodeService.class).getByCode(MandatoryTransferEliminationCode.class, GOOD_CODE);
        assertEquals(GOOD_CODE, code.getCode());
        assertEquals(GOOD_NAME, code.getName());

        code = (MandatoryTransferEliminationCode) SpringContext.getBean(KualiCodeService.class).getByName(MandatoryTransferEliminationCode.class, GOOD_NAME);
        assertEquals(GOOD_CODE, code.getCode());
        assertEquals(GOOD_NAME, code.getName());

        code = (MandatoryTransferEliminationCode) SpringContext.getBean(KualiCodeService.class).getByCode(MandatoryTransferEliminationCode.class, NONEXISTENT_CODE);
        assertNull(code);
        code = (MandatoryTransferEliminationCode) SpringContext.getBean(KualiCodeService.class).getByName(MandatoryTransferEliminationCode.class, NONEXISTENT_CODE);
        assertNull(code);
    }
}
