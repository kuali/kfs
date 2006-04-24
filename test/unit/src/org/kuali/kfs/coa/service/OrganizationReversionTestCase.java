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
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.OrganizationReversion;
import org.kuali.test.KualiTestBaseWithFixtures;

/**
 * This class...
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class OrganizationReversionTestCase extends KualiTestBaseWithFixtures {
    
    public void testGetByPrimaryKey() throws Exception {

        OrganizationReversionService organizationReversionService = SpringServiceLocator.getOrganizationReversionService();
        assertNotNull("Service shouldn't be null",organizationReversionService);

        Integer fiscalYear = new Integer("1997");
        
        Account account = new Account();
        account.setChartOfAccountsCode("BL");
        account.setOrganizationCode("test");
        
        OrganizationReversion notexist = organizationReversionService.getByFiscalYearAndAccount(fiscalYear, account);
        assertNull("01/01/1901 shouldn't exist in table", notexist);
        
        account.setOrganizationCode("PSY");
        OrganizationReversion exist = organizationReversionService.getByFiscalYearAndAccount(fiscalYear, account);
        assertNotNull("08/14/1993 should exist in table", exist);
        
      }
}
