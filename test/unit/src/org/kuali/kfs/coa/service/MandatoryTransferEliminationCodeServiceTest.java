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

import static org.kuali.core.util.SpringServiceLocator.getKualiCodeService;

import org.kuali.module.chart.bo.codes.MandatoryTransferEliminationCode;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

/**
 * This class tests the MandatoryTransferEliminationCode service.
 * 
 * 
 */
@WithTestSpringContext
public class MandatoryTransferEliminationCodeServiceTest extends KualiTestBase {

    private MandatoryTransferEliminationCode knowGood;
    private static final String GOOD_CODE="N";
    private static final String GOOD_NAME="NONE";
    private static final String BAD_CODE = "X";
    private static final String BAD_NAME = "BAD";

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
        MandatoryTransferEliminationCode mtec = (MandatoryTransferEliminationCode) getKualiCodeService().getByCode(MandatoryTransferEliminationCode.class, GOOD_CODE);
        assertEquals("Known-good code results in expected returned Name.", GOOD_NAME, mtec.getName());
    }

    public void testByName_valid_name() {
        // test known-good byName
        MandatoryTransferEliminationCode mtec = (MandatoryTransferEliminationCode) getKualiCodeService().getByName(MandatoryTransferEliminationCode.class, GOOD_NAME);
        assertEquals("Known-good name results in expected returned code.", GOOD_CODE, mtec.getCode());
    }

    public void stestByCode_invalid_name() {
        // test known-bad byCode
        MandatoryTransferEliminationCode mtec = (MandatoryTransferEliminationCode) getKualiCodeService().getByCode(MandatoryTransferEliminationCode.class, BAD_CODE);
        assertNull("Known-bad code returns null object.", mtec);
    }

    public void testByName_invalid_name() {
        // test known-bad byName
        MandatoryTransferEliminationCode mtec = (MandatoryTransferEliminationCode) getKualiCodeService().getByName(MandatoryTransferEliminationCode.class, BAD_NAME);
        assertNull("Known-bad code returns null object.", mtec);
    }

    public void testByCode_empty() {
        // test known-bad byCode
        MandatoryTransferEliminationCode mtec = (MandatoryTransferEliminationCode) getKualiCodeService().getByCode(MandatoryTransferEliminationCode.class, "");
        assertNull("Known-empty code returns null object.", mtec);
    }

    public void testByCode_null() {
        // test known-bad byCode
        MandatoryTransferEliminationCode mtec = (MandatoryTransferEliminationCode) getKualiCodeService().getByCode(MandatoryTransferEliminationCode.class, null);
        assertNull("Known-null code returns null object.", mtec);
    }

    public void testSave() {
        String oldName;
        String newName;

        // get the existing value
        MandatoryTransferEliminationCode mtec = (MandatoryTransferEliminationCode) getKualiCodeService().getByCode(MandatoryTransferEliminationCode.class, GOOD_CODE);

        // cache the old value, create a new value, and modify the object
        oldName = mtec.getName();
        newName = oldName + "2";
        mtec.setName(newName);

        // attempt to save the modified object
        getKualiCodeService().save(mtec);

        // open the object byCode() and confirm that the changes were saved
        mtec = null;
        mtec = (MandatoryTransferEliminationCode) getKualiCodeService().getByCode(MandatoryTransferEliminationCode.class, GOOD_CODE);
        assertEquals("Changes to the document were not persisted to the database.", newName, mtec.getName());

        // revert back to the old name if it worked
        mtec.setName(oldName);
        getKualiCodeService().save(mtec);


        mtec = null;
        mtec = (MandatoryTransferEliminationCode) getKualiCodeService().getByCode(MandatoryTransferEliminationCode.class, GOOD_CODE);
        assertEquals("Changes to the document were not persisted to the database.", oldName, mtec.getName());
    }

    public void testActive() {

        // test known-good active code
        MandatoryTransferEliminationCode mtec = (MandatoryTransferEliminationCode) getKualiCodeService().getByCode(MandatoryTransferEliminationCode.class, GOOD_CODE);
        assertEquals("The active code associated with this field is incorrect", true, mtec.isActive());

    }

    private void validateTestFixtures() {
        MandatoryTransferEliminationCode code = (MandatoryTransferEliminationCode) getKualiCodeService().getByCode(MandatoryTransferEliminationCode.class, GOOD_CODE);
        assertEquals(GOOD_CODE,code.getCode());
        assertEquals(GOOD_NAME,code.getName());

        code = (MandatoryTransferEliminationCode) getKualiCodeService().getByName(MandatoryTransferEliminationCode.class, GOOD_NAME);
        assertEquals(GOOD_CODE,code.getCode());
        assertEquals(GOOD_NAME,code.getName());
        
        code = (MandatoryTransferEliminationCode) getKualiCodeService().getByCode(MandatoryTransferEliminationCode.class, BAD_CODE);
        assertNull(code);
        code = (MandatoryTransferEliminationCode) getKualiCodeService().getByName(MandatoryTransferEliminationCode.class, BAD_CODE);
        assertNull(code);
    }
}
