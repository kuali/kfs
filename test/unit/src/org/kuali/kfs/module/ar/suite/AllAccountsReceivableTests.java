/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.suite;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.kuali.kfs.module.ar.batch.CustomerLoadBusinessRulesTest;
import org.kuali.kfs.module.ar.batch.CustomerLoadCSVInputFileTypeTest;
import org.kuali.kfs.module.ar.batch.CustomerLoadDigesterTest;
import org.kuali.kfs.module.ar.batch.CustomerLoadXMLSchemaTest;
import org.kuali.kfs.module.ar.batch.vo.CustomerLoadDigesterConverterTest;
import org.kuali.kfs.module.ar.businessobject.lookup.CustomerAgingReportLookupableHelperServiceImplTest;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocumentGeneralLedgerPostingTest;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocumentTest;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentServiceTest;
import org.kuali.kfs.module.ar.document.service.CustomerCreditMemoDetailServiceTest;
import org.kuali.kfs.module.ar.document.service.CustomerCreditMemoDocumentServiceTest;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailServiceTest;
import org.kuali.kfs.module.ar.document.validation.impl.CashControlDocumentRuleTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerCreditMemoDocumentRuleTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceBilledByChartOfAccountsCodeValidationTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceBilledByOrganizationCodeValidationTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceCustomerAddressValidationTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceCustomerNumberValidationTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceDetailAmountValidationTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceDetailChartCodeReceivableValidationTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceDetailDiscountGreaterThanParentValidationTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceDetailItemCodeValidationTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceDetailItemQuantityValidationTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceDetailParentLessThanDiscountValidationTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceDetailSubFundGroupReceivableValidationTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceDetailSystemInformationDiscountValidationTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceDetailUnitOfMeasureValidationTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceDetailUnitPriceValidationTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceDueDateValidationTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceItemCodeRuleTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceNumberOfInvoiceDetailsValidationTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceReceivableAccountNumberValidationTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceReceivableChartOfAccountsCodeValidationTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceReceivableFinancialObjectCodeValidationTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceReceivableFinancialSubObjectCodeValidationTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceReceivableProjectCodeValidationTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceReceivableSubAccountNumberValidationTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerInvoiceWriteoffDocumentRuleTest;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerRuleTest;
import org.kuali.kfs.module.ar.document.validation.impl.OrganizationAccountingDefaultRuleTest;

/**
 * Runs all the tests in the AR test suite.
 */
public class AllAccountsReceivableTests {

    /**
     * Returns a suite of all the tests in AR...except, of course, for those tests that were never
     * added to this class.
     * @return a Test suite with most all AR tests
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();

        suite.addTestSuite(PaymentApplicationDocumentTest.class);
        suite.addTestSuite(CashControlDocumentRuleTest.class);
        suite.addTestSuite(CustomerInvoiceItemCodeRuleTest.class);
        suite.addTestSuite(CustomerRuleTest.class);
        suite.addTestSuite(OrganizationAccountingDefaultRuleTest.class);
        suite.addTestSuite(CashControlDocumentServiceTest.class);
        suite.addTestSuite(CustomerInvoiceDetailServiceTest.class);
        suite.addTestSuite(CustomerInvoiceDocumentGeneralLedgerPostingTest.class);
        suite.addTestSuite(CustomerInvoiceBilledByChartOfAccountsCodeValidationTest.class);
        suite.addTestSuite(CustomerInvoiceBilledByOrganizationCodeValidationTest.class);
        suite.addTestSuite(CustomerInvoiceCustomerAddressValidationTest.class);
        suite.addTestSuite(CustomerInvoiceCustomerNumberValidationTest.class);
        suite.addTestSuite(CustomerInvoiceDetailAmountValidationTest.class);
        suite.addTestSuite(CustomerInvoiceDetailChartCodeReceivableValidationTest.class);
        suite.addTestSuite(CustomerInvoiceDetailDiscountGreaterThanParentValidationTest.class);
        suite.addTestSuite(CustomerInvoiceDetailItemCodeValidationTest.class);
        suite.addTestSuite(CustomerInvoiceDetailItemQuantityValidationTest.class);
        suite.addTestSuite(CustomerInvoiceDetailParentLessThanDiscountValidationTest.class);
        suite.addTestSuite(CustomerInvoiceDetailSubFundGroupReceivableValidationTest.class);
        suite.addTestSuite(CustomerInvoiceDetailUnitOfMeasureValidationTest.class);
        suite.addTestSuite(CustomerInvoiceDetailUnitPriceValidationTest.class);
        suite.addTestSuite(CustomerInvoiceDueDateValidationTest.class);
        suite.addTestSuite(CustomerInvoiceNumberOfInvoiceDetailsValidationTest.class);
        suite.addTestSuite(CustomerInvoiceReceivableAccountNumberValidationTest.class);
        suite.addTestSuite(CustomerInvoiceReceivableChartOfAccountsCodeValidationTest.class);
        suite.addTestSuite(CustomerInvoiceReceivableFinancialObjectCodeValidationTest.class);
        suite.addTestSuite(CustomerInvoiceReceivableFinancialSubObjectCodeValidationTest.class);
        suite.addTestSuite(CustomerInvoiceReceivableProjectCodeValidationTest.class);
        suite.addTestSuite(CustomerInvoiceReceivableSubAccountNumberValidationTest.class);
        suite.addTestSuite(CustomerInvoiceDetailSystemInformationDiscountValidationTest.class);
        suite.addTestSuite(CustomerCreditMemoDetailServiceTest.class);
        suite.addTestSuite(CustomerCreditMemoDocumentServiceTest.class);
        suite.addTestSuite(CustomerCreditMemoDocumentRuleTest.class);
        suite.addTestSuite(CustomerInvoiceWriteoffDocumentRuleTest.class);
        suite.addTestSuite(CustomerAgingReportLookupableHelperServiceImplTest.class);

        //  customer batch load tests
        suite.addTestSuite(CustomerLoadDigesterTest.class);
        suite.addTestSuite(CustomerLoadDigesterConverterTest.class);
        suite.addTestSuite(CustomerLoadBusinessRulesTest.class);
        suite.addTestSuite(CustomerLoadXMLSchemaTest.class);
        suite.addTestSuite(CustomerLoadCSVInputFileTypeTest.class);
        return suite;
    }

    /**
     * Runs all the tests in the all test test suite
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}
