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
import org.kuali.module.chart.bo.FundGroup;
import org.kuali.test.KualiTestBaseWithFixtures;
import org.kuali.test.WithTestSpringContext;

/**
 * This class tests the FundGroup service.
 * 
 * @author Kuali Nervous System Team ()
 */

@WithTestSpringContext
public class FundGroupServiceTest extends KualiTestBaseWithFixtures {

    KualiCodeService kualiCodeService;

    protected void setUp() throws Exception {
        super.setUp();
        kualiCodeService = SpringServiceLocator.getKualiCodeService();
    }

    public void testGetByCode_knownCode1() {
        FundGroup fundGroup = (FundGroup) kualiCodeService.getByCode(FundGroup.class, "LF");
        assertEquals("Known code does not produce expected name.", "LOAN FUNDS", fundGroup.getName());
    }

    public void testGetByCode_knownName1() {
        FundGroup fundGroup = (FundGroup) kualiCodeService.getByName(FundGroup.class, "LOAN FUNDS");
        assertEquals("Known code does not produce expected name.", "LF", fundGroup.getCode());
    }

    public void testGetByCode_knownCode2() {
        FundGroup fundGroup = (FundGroup) kualiCodeService.getByCode(FundGroup.class, "AF");
        assertEquals("Known code does not produce expected name.", "AGENCY FUNDS", fundGroup.getName());
    }

    public void testGetByCode_knownName2() {
        FundGroup fundGroup = (FundGroup) kualiCodeService.getByName(FundGroup.class, "AGENCY FUNDS");
        assertEquals("Known code does not produce expected name.", "AF", fundGroup.getCode());
    }

    public void testGetByCode_unknownCode() {
        FundGroup fundGroup = (FundGroup) kualiCodeService.getByCode(FundGroup.class, "XX");
        assertNull("Known-bad code does not produce expected null object.", fundGroup);
    }

    public void testGetByName_unknownName() {
        FundGroup fundGroup = (FundGroup) kualiCodeService.getByName(FundGroup.class, "The Cat In the Hat");
        assertNull("Known-bad name does not produce expected null object.", fundGroup);
    }
}
