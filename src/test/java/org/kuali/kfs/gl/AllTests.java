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
package org.kuali.kfs.gl;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.kuali.kfs.gl.batch.BalanceForwardStepTest;
import org.kuali.kfs.gl.batch.CollectorStepTest;
import org.kuali.kfs.gl.batch.FileEnterpriseFeederTest;
import org.kuali.kfs.gl.batch.ForwardEncumbranceTest;
import org.kuali.kfs.gl.batch.PurgeTest;
import org.kuali.kfs.gl.batch.YearEndFlexibleOffsetTest;
import org.kuali.kfs.gl.batch.service.CollectorServiceTest;
import org.kuali.kfs.gl.batch.service.OrganizationReversionCategoryTest;
import org.kuali.kfs.gl.batch.service.PosterServiceTest;
import org.kuali.kfs.gl.batch.service.RunDateServiceTest;
import org.kuali.kfs.gl.batch.service.SufficientFundsAccountUpdateServiceTest;
import org.kuali.kfs.gl.businessobject.OriginEntryTest;
import org.kuali.kfs.gl.businessobject.lookup.AccountBalanceLookupableHelperServiceTest;
import org.kuali.kfs.gl.businessobject.lookup.BalanceLookupableHelperServiceTest;
import org.kuali.kfs.gl.businessobject.lookup.EntryLookupableHelperServiceTest;
import org.kuali.kfs.gl.service.NightlyOutServiceTest;
import org.kuali.kfs.gl.service.OrganizationReversionLogicTest;
import org.kuali.kfs.gl.service.ScrubberFlexibleOffsetTest;
import org.kuali.kfs.gl.service.ScrubberServiceTest;
import org.kuali.kfs.gl.service.SufficientFundsServiceTest;
import org.kuali.kfs.sys.dataaccess.TestUnitTestSqlDao;
import org.kuali.kfs.sys.dataaccess.TestUniversityDateDao;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryServiceTest;
import org.kuali.kfs.sys.service.PendingLedgerServiceHelperServiceTest;

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

        // org.kuali.kfs.gl
        suite.addTestSuite(GLConstantsTest.class);

        // org.kuali.kfs.gl.batch
        suite.addTestSuite(BalanceForwardStepTest.class);
        suite.addTestSuite(CollectorStepTest.class);
        suite.addTestSuite(FileEnterpriseFeederTest.class);
        suite.addTestSuite(ForwardEncumbranceTest.class);
        suite.addTestSuite(PurgeTest.class);
        suite.addTestSuite(YearEndFlexibleOffsetTest.class);

        // org.kuali.kfs.gl.businessobject
        suite.addTestSuite(OriginEntryTest.class);

        // org.kuali.module.gl.dao.ojb
        suite.addTestSuite(TestUniversityDateDao.class);
        suite.addTestSuite(TestUnitTestSqlDao.class);

        // org.kuali.module.gl.service
        suite.addTestSuite(CollectorServiceTest.class);
        suite.addTestSuite(GeneralLedgerPendingEntryServiceTest.class);
        suite.addTestSuite(NightlyOutServiceTest.class);
        suite.addTestSuite(PosterServiceTest.class);
        suite.addTestSuite(RunDateServiceTest.class);
        suite.addTestSuite(ScrubberFlexibleOffsetTest.class);
        suite.addTestSuite(ScrubberServiceTest.class);
        suite.addTestSuite(SufficientFundsAccountUpdateServiceTest.class);
        suite.addTestSuite(SufficientFundsServiceTest.class);

        // org.kuali.module.gl.service.impl.orgreversion
        suite.addTestSuite(OrganizationReversionCategoryTest.class);
        suite.addTestSuite(OrganizationReversionLogicTest.class);

        // org.kuali.module.gl.util
        suite.addTestSuite(OJBUtilityTest.class);

        // org.kuali.kfs.gl.businessobject.lookup
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
