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
package org.kuali.module.gl;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.kuali.module.gl.batch.BalanceForwardStepTest;
import org.kuali.module.gl.batch.CollectorStepTest;
import org.kuali.module.gl.batch.FileEnterpriseFeederTest;
import org.kuali.module.gl.batch.ForwardEncumbranceTest;
import org.kuali.module.gl.batch.PurgeTest;
import org.kuali.module.gl.bo.OriginEntryTest;
import org.kuali.module.gl.dao.ojb.BalanceTestDaoOjb;
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
public class AllTests {

    /**
     * Constructs a AllTests instance
     */
    public AllTests() {
        super();
    }

    /**
     * Returns a suite of all the tests in GL...except, of course, for those tests that were never
     * added to this class.
     * @return a Test suite with most all GL tests
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();

        // org.kuali.module.gl
        suite.addTestSuite(GLConstantsTest.class);

        // org.kuali.module.gl.batch
        suite.addTestSuite(BalanceForwardStepTest.class);
        suite.addTestSuite(CollectorStepTest.class);
        suite.addTestSuite(FileEnterpriseFeederTest.class);
        suite.addTestSuite(ForwardEncumbranceTest.class);
        suite.addTestSuite(PurgeTest.class);

        // org.kuali.module.gl.bo
        suite.addTestSuite(OriginEntryTest.class);

        // org.kuali.module.gl.dao.ojb
        suite.addTestSuite(TestUniversityDateDao.class);
        suite.addTestSuite(TestUnitTestSqlDao.class);

        // org.kuali.module.gl.service
        suite.addTestSuite(CollectorServiceTest.class);
        suite.addTestSuite(GeneralLedgerPendingEntryServiceTest.class);
        suite.addTestSuite(NightlyOutServiceTest.class);
        suite.addTestSuite(PosterServiceTest.class);
        suite.addTestSuite(ReportServiceTest.class);
        suite.addTestSuite(RunDateServiceTest.class);
        suite.addTestSuite(ScrubberFlexibleOffsetTest.class);
        suite.addTestSuite(ScrubberServiceTest.class);
        suite.addTestSuite(SufficientFundsRebuilderServiceTest.class);
        suite.addTestSuite(SufficientFundsServiceTest.class);

        // org.kuali.module.gl.service.impl.orgreversion
        suite.addTestSuite(OrganizationReversionCategoryTest.class);
        suite.addTestSuite(OrganizationReversionLogicTest.class);

        // org.kuali.module.gl.util
        suite.addTestSuite(OJBUtilityTest.class);

        // org.kuali.module.gl.web.lookupable
        suite.addTestSuite(AccountBalanceLookupableHelperServiceTest.class);
        suite.addTestSuite(BalanceLookupableHelperServiceTest.class);
        suite.addTestSuite(EntryLookupableHelperServiceTest.class);
        suite.addTestSuite(PendingLedgerServiceHelperServiceTest.class);

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
