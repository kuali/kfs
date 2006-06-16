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
package org.kuali.module.gl;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.kuali.module.gl.batch.PurgeTest;
import org.kuali.module.gl.bo.OriginEntryTest;
import org.kuali.module.gl.dao.ojb.SufficientFundsDaoTest;
import org.kuali.module.gl.dao.ojb.TestUnitTestSqlDao;
import org.kuali.module.gl.dao.ojb.TestUniversityDateDao;
import org.kuali.module.gl.service.GeneralLedgerPendingEntryServiceTest;
import org.kuali.module.gl.service.NightlyOutServiceTest;
import org.kuali.module.gl.service.PosterServiceTest;
import org.kuali.module.gl.service.ScrubberFlexibleOffsetTest;
import org.kuali.module.gl.service.ScrubberServiceTest;
import org.kuali.module.gl.service.SufficientFundRebuildServiceTest;
import org.kuali.module.gl.service.SufficientFundsRebuilderServiceTest;
import org.kuali.module.gl.service.SufficientFundsServiceTest;
import org.kuali.module.gl.util.BusinessObjectHandlerTest;

/**
 * @author jsissom
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
        suite.addTestSuite(SufficientFundsDaoTest.class);
        suite.addTestSuite(TestUniversityDateDao.class);
        suite.addTestSuite(TestUnitTestSqlDao.class);

        // org.kuali.module.gl.service
        suite.addTestSuite(GeneralLedgerPendingEntryServiceTest.class);
        suite.addTestSuite(NightlyOutServiceTest.class);
        suite.addTestSuite(PosterServiceTest.class);
        suite.addTestSuite(ScrubberFlexibleOffsetTest.class);
        suite.addTestSuite(ScrubberServiceTest.class);
        suite.addTestSuite(SufficientFundRebuildServiceTest.class);
        suite.addTestSuite(SufficientFundsRebuilderServiceTest.class);
        suite.addTestSuite(SufficientFundsServiceTest.class);

        // org.kuali.module.gl.util
        suite.addTestSuite(BusinessObjectHandlerTest.class);

        return suite;
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}
