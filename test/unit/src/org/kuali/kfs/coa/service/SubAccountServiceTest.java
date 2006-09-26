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

import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.test.KualiTestBaseWithFixtures;
import org.kuali.test.WithTestSpringContext;

/**
 * This class tests the SubAccount service.
 * 
 * @author Kuali Nervous System Team ()
 */
@WithTestSpringContext
public class SubAccountServiceTest extends KualiTestBaseWithFixtures {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SubAccountServiceTest.class);

    private SubAccountService subAccountService;
    private final static String CHART = "BA";
    private final static String ACCOUNT = "6044900";
    private final static String SUB_ACCOUNT = "ARREC";

    protected void setUp() throws Exception {
        super.setUp();

        setSubAccountService((SubAccountService) SpringServiceLocator.getSubAccountService());
    }

    public void testA21SubAccount() {
        SubAccount sa;

        sa = subAccountService.getByPrimaryId(CHART, ACCOUNT, SUB_ACCOUNT);

        assertTrue("expect to find this sub account: " + CHART + "/" + ACCOUNT + "/" + SUB_ACCOUNT, ObjectUtils.isNotNull(sa));
        A21SubAccount a21 = sa.getA21SubAccount();
        assertTrue("expect this to have a21subaccount", ObjectUtils.isNotNull(a21));
        a21.getIndirectCostRecoveryAccount();
    }

    public void testGetByPrimaryId() throws Exception {
        SubAccount sa = new SubAccount();
        sa.setAccountNumber(ACCOUNT);
        sa.setChartOfAccountsCode(CHART);
        sa.setSubAccountNumber(SUB_ACCOUNT);


        SubAccount retrieved = subAccountService.getByPrimaryId(CHART, ACCOUNT, SUB_ACCOUNT);
        assertTrue("Didn't retrieve sub account", ObjectUtils.isNotNull(retrieved));
        assertEquals("Wrong chart", CHART, retrieved.getChartOfAccountsCode());
        assertEquals("Wrong account", ACCOUNT, retrieved.getAccountNumber());
        assertEquals("Wrong Sub account number", SUB_ACCOUNT, retrieved.getSubAccountNumber());
    }

    /**
     * Sets the subAccountService attribute value.
     * 
     * @param subAccountService The subAccountService to set.
     */
    public void setSubAccountService(SubAccountService subAccountService) {
        this.subAccountService = subAccountService;
    }


}
