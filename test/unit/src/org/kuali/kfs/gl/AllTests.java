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

import org.kuali.module.gl.batch.PurgeTest;
import org.kuali.module.gl.bo.OriginEntryTest;
import org.kuali.module.gl.dao.ojb.TestUnitTestSqlDao;
import org.kuali.module.gl.dao.ojb.TestUniversityDateDao;
import org.kuali.module.gl.service.GeneralLedgerPendingEntryServiceTest;
import org.kuali.module.gl.service.NightlyOutServiceTest;
import org.kuali.module.gl.service.PosterServiceTest;
import org.kuali.module.gl.service.ScrubberFlexibleOffsetTest;
import org.kuali.module.gl.service.ScrubberServiceTest;
import org.kuali.module.gl.service.SufficientFundsRebuilderServiceTest;
import org.kuali.module.gl.util.OJBUtilityTest;

/**
 * 
 * 
 */
public class AllTests {

    public AllTests() {
        super();
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();

        // org.kuali.module.gl.batch
        suite.addTestSuite(PurgeTest.class);

        // org.kuali.module.gl.bo
        suite.addTestSuite(OriginEntryTest.class);

        // org.kuali.module.gl.dao.ojb
        suite.addTestSuite(TestUniversityDateDao.class);
        suite.addTestSuite(TestUnitTestSqlDao.class);

        // org.kuali.module.gl.service
        suite.addTestSuite(GeneralLedgerPendingEntryServiceTest.class);
        suite.addTestSuite(NightlyOutServiceTest.class);
        suite.addTestSuite(PosterServiceTest.class);
        suite.addTestSuite(ScrubberFlexibleOffsetTest.class);
        suite.addTestSuite(ScrubberServiceTest.class);
        suite.addTestSuite(SufficientFundsRebuilderServiceTest.class);

        // org.kuali.module.gl.util
        suite.addTestSuite(OJBUtilityTest.class);

        return suite;
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}
