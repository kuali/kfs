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
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.service.impl.SubAccountServiceImpl;
import org.kuali.test.KualiTestBaseWithSpring;

/**
 * This class tests the SubAccount service.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class SubAccountServiceTest extends KualiTestBaseWithSpring {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SubAccountServiceTest.class);

  private SubAccountServiceImpl subAccountService;
  
  protected void setUp() throws Exception {
    super.setUp();

    setSubAccountService((SubAccountServiceImpl) SpringServiceLocator.getSubAccountService());
  }
  
  public void testA21SubAccount() {
      SubAccount sa;
      
      sa=subAccountService.getByPrimaryId("BL","6044900","ARREC");
      
      sa.getA21SubAccount().getIndirectCostRecoveryAccount();
  }

  public void testGetByPrimaryId() throws Exception {
    SubAccount sa = new SubAccount();
    sa.setAccountNumber("1234567");
    sa.setChartOfAccountsCode("XX");
    sa.setSubAccountNumber("12345");

    
    SubAccount retrieved = subAccountService.getByPrimaryId("XX","1234567","12345");
    assertNotNull("Didn't retrieve sub account",retrieved);
    assertEquals("Wrong chart","XX",retrieved.getChartOfAccountsCode());
    assertEquals("Wrong account","1234567",retrieved.getAccountNumber());
    assertEquals("Wrong Sub account number","12345",retrieved.getSubAccountNumber());
  }

    /**
     * Sets the subAccountService attribute value.
     * @param subAccountService The subAccountService to set.
     */
    public void setSubAccountService(SubAccountServiceImpl subAccountService) {
        this.subAccountService = subAccountService;
    }

  
}
