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

import org.kuali.core.service.KualiCodeService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.codes.MandatoryTransferEliminationCode;
import org.kuali.test.KualiTestBaseWithSpring;
import org.kuali.test.WithTestSpringContext;

/**
 * This class tests the MandatoryTransferEliminationCode service.
 * 
 * @author Kuali Nervous System Team ()
 */
@WithTestSpringContext
public class MandatoryTransferEliminationCodeServiceTest extends KualiTestBaseWithSpring {

    MandatoryTransferEliminationCode mtec;
    KualiCodeService kualiCodeService;
    private static final String N_CODE_DESCR = "None";

    /**
     * Performs setup operations before tests are executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        kualiCodeService = SpringServiceLocator.getKualiCodeService();
    }

    /**
     * Performs miscellaneous tests for this service.
     */
    public void testMiscellaneous() {
        // test known-good byCode
        mtec = null;
        mtec = (MandatoryTransferEliminationCode) kualiCodeService.getByCode(MandatoryTransferEliminationCode.class, "N");
        assertEquals("Known-good code results in expected returned Name.", N_CODE_DESCR, mtec.getName());

        // test known-good byName
        mtec = null;
        mtec = (MandatoryTransferEliminationCode) kualiCodeService.getByName(MandatoryTransferEliminationCode.class, N_CODE_DESCR);
        assertEquals("Known-good name results in expected returned code.", "N", mtec.getCode());

        // test known-bad byCode
        mtec = null;
        mtec = (MandatoryTransferEliminationCode) kualiCodeService.getByCode(MandatoryTransferEliminationCode.class, "A");
        assertNull("Known-bad code returns null object.", mtec);

        // test known-bad byName
        mtec = null;
        mtec = (MandatoryTransferEliminationCode) kualiCodeService.getByName(MandatoryTransferEliminationCode.class, "This is not a valid code description in this table.");
        assertNull("Known-bad code returns null object.", mtec);

        // test known-bad byCode
        mtec = null;
        mtec = (MandatoryTransferEliminationCode) kualiCodeService.getByCode(MandatoryTransferEliminationCode.class, "");
        assertNull("Known-empty code returns null object.", mtec);

        // test known-bad byCode
        mtec = null;
        mtec = (MandatoryTransferEliminationCode) kualiCodeService.getByCode(MandatoryTransferEliminationCode.class, null);
        assertNull("Known-null code returns null object.", mtec);
    }

    public void testSave() {
        String oldName;
        String newName;

        // get the existing value
        mtec = null;
        mtec = (MandatoryTransferEliminationCode) kualiCodeService.getByCode(MandatoryTransferEliminationCode.class, "N");

        // cache the old value, create a new value, and modify the object
        oldName = mtec.getName();
        newName = oldName + "2";
        mtec.setName(newName);

        // attempt to save the modified object
        kualiCodeService.save(mtec);

        // open the object byCode() and confirm that the changes were saved
        mtec = null;
        mtec = (MandatoryTransferEliminationCode) kualiCodeService.getByCode(MandatoryTransferEliminationCode.class, "N");
        assertEquals("Changes to the document were not persisted to the database.", newName, mtec.getName());

        // revert back to the old name if it worked
        mtec.setName(oldName);
        kualiCodeService.save(mtec);


        mtec = null;
        mtec = (MandatoryTransferEliminationCode) kualiCodeService.getByCode(MandatoryTransferEliminationCode.class, "N");
        assertEquals("Changes to the document were not persisted to the database.", oldName, mtec.getName());
    }

    public void testActive() {

        // test known-good active code
        mtec = null;
        mtec = (MandatoryTransferEliminationCode) kualiCodeService.getByCode(MandatoryTransferEliminationCode.class, "N");
        assertEquals("The active code associated with this field is incorrect", true, mtec.isActive());

    }
}
