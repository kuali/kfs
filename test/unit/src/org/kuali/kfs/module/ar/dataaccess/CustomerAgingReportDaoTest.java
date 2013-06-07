/*
 * Copyright 2012 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.dataaccess;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.module.ar.businessobject.CollectorHierarchy;
import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This class tests the methods of CollectorHierarchyDao.
 */
@ConfigureContext(session = khuntley)
public class CustomerAgingReportDaoTest extends KualiTestBase {

    private CustomerAgingReportDao customerAgingReportDao;

    private static final String CHART1 = "UA";
    private static final String CHART2 = "BL";
    private static final String ORGANIZATION = "VPIT";
    private static final String CUSTOMER_NUMBER = "ABB2";
    private static final String ACCOUNT_NUMBER = "1031400";

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        // setting up Customer Invoice Document.
        CustomerInvoiceDetailFixture customerInvoiceDetailFixture1 = CustomerInvoiceDetailFixture.BASE_CUSTOMER_INVOICE_DETAIL;
        CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER_WITH_BILLING_INFO.createCustomerInvoiceDocument(new CustomerInvoiceDetailFixture[] {customerInvoiceDetailFixture1});
        customerAgingReportDao = SpringContext.getBean(CustomerAgingReportDao.class);
    }

    /**
     * This method tests the method FindAppliedAmountByAccount of CustomerAgingReportDao class.
     */
    public void testFindAppliedAmountByAccount() {
        Map<String, KualiDecimal> map = customerAgingReportDao.findAppliedAmountByAccount(CHART2, ACCOUNT_NUMBER, null, null);
        assertNotNull(map);
    }

    /**
     * This method tests the method findAppliedAmountByBillingChartAndOrg of CustomerAgingReportDao class.
     */
    public void testFindAppliedAmountByBillingChartAndOrg() {
        Map<String, KualiDecimal> map = customerAgingReportDao.findAppliedAmountByBillingChartAndOrg(CHART1, ORGANIZATION, null, null);
        assertNotNull(map);
    }

    /**
     * This method tests the method findAppliedAmountByProcessingChartAndOrg of CustomerAgingReportDao class.
     */
    public void testFindAppliedAmountByProcessingChartAndOrg() {
        Map<String, KualiDecimal> map = customerAgingReportDao.findAppliedAmountByProcessingChartAndOrg(CHART1, ORGANIZATION, null, null);
        assertNotNull(map);
    }

    /**
     * This method tests the method findDiscountAmountByAccount of CustomerAgingReportDao class.
     */
    public void testFindDiscountAmountByAccount() {
        Map<String, KualiDecimal> map = customerAgingReportDao.findDiscountAmountByAccount(CHART2, ACCOUNT_NUMBER, null, null);
        assertNotNull(map);
    }

    /**
     * This method tests the method findDiscountAmountByBillingChartAndOrg of CustomerAgingReportDao class.
     */
    public void testFindDiscountAmountByBillingChartAndOrg() {
        Map<String, KualiDecimal> map = customerAgingReportDao.findDiscountAmountByBillingChartAndOrg(CHART1, ORGANIZATION, null, null);
        assertNotNull(map);
    }

    /**
     * This method tests the method findDiscountAmountByProcessingChartAndOrg of CustomerAgingReportDao class.
     */
    public void testFindDiscountAmountByProcessingChartAndOrg() {
        Map<String, KualiDecimal> map = customerAgingReportDao.findDiscountAmountByProcessingChartAndOrg(CHART1, ORGANIZATION, null, null);
        assertNotNull(map);
    }

    /**
     * This method tests the method findInvoiceAmountByAccount of CustomerAgingReportDao class.
     */
    public void testFindInvoiceAmountByAccount() {
        Map<String, KualiDecimal> map = customerAgingReportDao.findInvoiceAmountByAccount(CHART2, ACCOUNT_NUMBER, null, null);
        assertNotNull(map);
    }

    /**
     * This method tests the method findInvoiceAmountByBillingChartAndOrg of CustomerAgingReportDao class.
     */
    public void testFindInvoiceAmountByBillingChartAndOrg() {
        Map<String, KualiDecimal> map = customerAgingReportDao.findInvoiceAmountByBillingChartAndOrg(CHART1, ORGANIZATION, null, null);
        assertNotNull(map);
    }

    /**
     * This method tests the method findInvoiceAmountByProcessingChartAndOrg of CustomerAgingReportDao class.
     */
    public void testFindInvoiceAmountByProcessingChartAndOrg() {
        Map<String, KualiDecimal> map = customerAgingReportDao.findInvoiceAmountByProcessingChartAndOrg(CHART1, ORGANIZATION, null, null);
        assertNotNull(map);
    }

    /**
     * This method tests the method findWriteOffAmountByCustomerNumber of CustomerAgingReportDao class.
     */
    public void testFindWriteOffAmountByCustomerNumber() {
        KualiDecimal writeOffAmt = customerAgingReportDao.findWriteOffAmountByCustomerNumber(CUSTOMER_NUMBER);
        assertNotNull(writeOffAmt);
    }
}
