/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.dataaccess;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.Map;

import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceWriteoffDocumentService;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;

/**
 * This class tests the methods of CustomerAgingReportDao.
 */
@ConfigureContext(session = khuntley)
public class CustomerAgingReportDaoTest extends KualiTestBase {

    private CustomerAgingReportDao customerAgingReportDao;
    private CustomerInvoiceDocument customerInvoiceDocument;

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
        customerInvoiceDocument = CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER_WITH_BILLING_INFO.createCustomerInvoiceDocument(new CustomerInvoiceDetailFixture[] {customerInvoiceDetailFixture1});
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
    public void testFindWriteOffAmountByCustomerNumber() throws WorkflowException {
        KualiDecimal writeOffAmt = customerAgingReportDao.findWriteOffAmountByCustomerNumber(CUSTOMER_NUMBER);
        assertNull(writeOffAmt);

        CustomerInvoiceWriteoffDocumentService writeoffService = SpringContext.getBean(CustomerInvoiceWriteoffDocumentService.class);
        writeoffService.createCustomerInvoiceWriteoffDocument(customerInvoiceDocument.getDocumentNumber(), "Created by CustomerAgingReportDaoTest.testFindWriteOffAmountByCustomerNumber unit test.");

        writeOffAmt = customerAgingReportDao.findWriteOffAmountByCustomerNumber(CUSTOMER_NUMBER);
        assertNotNull(writeOffAmt);
    }
}
