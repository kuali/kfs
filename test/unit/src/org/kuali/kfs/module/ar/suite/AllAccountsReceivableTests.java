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
import org.kuali.module.ar.service.CustomerInvoiceDocumentGeneralLedgerPostingHelperTest;
import org.kuali.module.gl.batch.BalanceForwardStepTest;
import org.kuali.module.gl.batch.CollectorStepTest;
import org.kuali.module.gl.batch.FileEnterpriseFeederTest;
import org.kuali.module.gl.batch.ForwardEncumbranceTest;
import org.kuali.module.gl.batch.PurgeTest;
import org.kuali.module.gl.batch.YearEndFlexibleOffsetTest;
import org.kuali.module.gl.bo.OriginEntryTest;
import org.kuali.module.gl.dao.ojb.TestUnitTestSqlDao;
import org.kuali.module.gl.dao.ojb.TestUniversityDateDao;
import org.kuali.module.gl.service.CollectorServiceTest;
import org.kuali.module.gl.service.GeneralLedgerPendingEntryServiceTest;
import org.kuali.module.gl.service.NightlyOutServiceTest;
import org.kuali.module.gl.service.PosterServiceTest;
import org.kuali.module.gl.service.ReportServiceTest;
import org.kuali.module.gl.service.RunDateServiceTest;
import org.kuali.module.gl.service.ScrubberFlexibleOffsetTest;
import org.kuali.module.gl.service.ScrubberServiceTest;
import org.kuali.module.gl.service.SufficientFundsRebuilderServiceTest;
import org.kuali.module.gl.service.SufficientFundsServiceTest;
import org.kuali.module.gl.service.impl.orgreversion.OrganizationReversionCategoryTest;
import org.kuali.module.gl.service.impl.orgreversion.OrganizationReversionLogicTest;
import org.kuali.module.gl.util.OJBUtilityTest;
import org.kuali.module.gl.web.lookupable.AccountBalanceLookupableHelperServiceTest;
import org.kuali.module.gl.web.lookupable.BalanceLookupableHelperServiceTest;
import org.kuali.module.gl.web.lookupable.EntryLookupableHelperServiceTest;
import org.kuali.module.gl.web.lookupable.PendingLedgerServiceHelperServiceTest;

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
        suite.addTestSuite(CustomerInvoiceDocumentGeneralLedgerPostingHelperTest.class);

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
