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

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.SubFundGroup;
import org.kuali.test.KualiTestBaseWithFixtures;
import org.kuali.test.WithTestSpringContext;

/**
 * This class tests the subFundGroup service.
 * 
 * @author Kuali Nervous System Team ()
 */
@WithTestSpringContext
public class SubFundGroupServiceTest extends KualiTestBaseWithFixtures {

    SubFundGroupService subFundGroupService;

    protected void setUp() throws Exception {
        super.setUp();
        subFundGroupService = SpringServiceLocator.getSubFundGroupService();
    }

    public final void testGetByCode_knownCode() {
        // known-good code
        SubFundGroup subFundGroup = subFundGroupService.getByPrimaryId("LOANFD");
        assertEquals("Known code does not produce expected name.", "LOAN FUNDS", subFundGroup.getSubFundGroupDescription());
    }

    public final void testGetByCode_knownCode2() {
        // known-good code
        SubFundGroup subFundGroup = subFundGroupService.getByPrimaryId("CLEAR");
        assertEquals("Known code does not produce expected name.", "CLEARING AND ROTATING FUNDS", subFundGroup.getSubFundGroupDescription());
    }

    public final void testGetByCode_unknownCode() {
        // known-bad code
        SubFundGroup subFundGroup = subFundGroupService.getByPrimaryId("SMELL");
        assertNull("Known-bad code does not produce expected null object.", subFundGroup);
    }

    public final void testGetByChartAndAccount() {
        String chartCode = "BL";
        String accountNumber = "1031420";
        SubFundGroup subFundGroup = subFundGroupService.getByChartAndAccount(chartCode, accountNumber);
        assertNotNull(subFundGroup);
        assertEquals("Foo", "GENFND", subFundGroup.getSubFundGroupCode());
    }

    public final void testGetByName_knownName() {
        // TODO: commented out, because there is no equivalent to getByName on regular business objects
        // known-good name
        // subFundGroup = null;
        // subFundGroup = (subFundGroup) kualiCodeService.getByName(subFundGroup.class, "LOAN FUNDS");
        // assertEquals("Known code does not produce expected name.", "LOANFD", subFundGroup.getCode());
    }

    public final void testGetByName_knownName2() {
        // TODO: commented out, because there is no equivalent to getByName on regular business objects
        // known-good name
        // subFundGroup = null;
        // subFundGroup = (subFundGroup) kualiCodeService.getByName(subFundGroup.class, "CLEARING AND ROTATING FUNDS");
        // assertEquals("Known code does not produce expected name.", "CLEAR", subFundGroup.getCode());
        // assertEquals("Known code's active indicator conversion failed.", true, subFundGroup.isActive());
        // assertEquals("Known code's wage indicator conversion failed.", false, subFundGroup.isWageIndicator());
    }

    public final void testGetByName_unknownName() {
        // TODO: commented out, because there is no equivalent to getByName on regular business objects
        // known-bad name
        // subFundGroup = null;
        // subFundGroup = (subFundGroup) kualiCodeService.getByName(subFundGroup.class, "Smelly Cat");
        // assertNull("Known-bad name does not produce expected null object.", subFundGroup);
    }
}
