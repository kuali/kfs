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
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.test.KualiTestBaseWithFixtures;
import org.kuali.test.WithTestSpringContext;

/**
 * This class tests the OffsetDefinition service.
 * 
 * @author Kuali Nervous System Team ()
 */
@WithTestSpringContext
public class OffsetDefinitionServiceTest extends KualiTestBaseWithFixtures {
    private OffsetDefinitionService offsetDefinitionService;

    protected void setUp() throws Exception {
        super.setUp();

        offsetDefinitionService = SpringServiceLocator.getOffsetDefinitionService();
    }

    public void testValidateAccount() {
        OffsetDefinition offsetDefinition = null;
        offsetDefinition = offsetDefinitionService.getByPrimaryId(new Integer(2004), "BA", "IB", "AC");
        assertNotNull("offset object code not found", offsetDefinition.getFinancialObject());
        assertEquals("offset object code should have been 8000", "8000", offsetDefinition.getFinancialObject().getFinancialObjectCode());

        offsetDefinition = null;
        offsetDefinition = offsetDefinitionService.getByPrimaryId(new Integer(2004), "XX", "XX", "XX");
        assertNull(offsetDefinition);
    }
}
