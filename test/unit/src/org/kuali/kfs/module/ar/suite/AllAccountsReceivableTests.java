/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.ar;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.kuali.module.ar.document.PaymentApplicationDocumentTest;
import org.kuali.module.ar.rules.CashControlDocumentRuleTest;
import org.kuali.module.ar.rules.CustomerInvoiceDocumentRuleTest;
import org.kuali.module.ar.rules.CustomerInvoiceItemCodeRuleTest;
import org.kuali.module.ar.rules.CustomerRuleTest;
import org.kuali.module.ar.rules.OrganizationAccountingDefaultRuleTest;
import org.kuali.module.ar.rules.SystemInformationRuleTest;
import org.kuali.module.ar.service.CashControlDocumentServiceTest;
import org.kuali.module.ar.service.CustomerInvoiceDetailServiceTest;
import org.kuali.module.ar.service.CustomerInvoiceDocumentGeneralLedgerPostingTest;

/**
 * Runs all the tests in the GL test suite.
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
        suite.addTestSuite(CustomerInvoiceDocumentRuleTest.class);
        suite.addTestSuite(CustomerInvoiceItemCodeRuleTest.class);
        suite.addTestSuite(CustomerRuleTest.class);
        suite.addTestSuite(OrganizationAccountingDefaultRuleTest.class);
        suite.addTestSuite(SystemInformationRuleTest.class);
        suite.addTestSuite(CashControlDocumentServiceTest.class);
        suite.addTestSuite(CustomerInvoiceDetailServiceTest.class);
        suite.addTestSuite(CustomerInvoiceDocumentGeneralLedgerPostingTest.class);

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
