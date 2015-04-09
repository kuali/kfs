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
package org.kuali.kfs.sys.batch.service;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.kuali.kfs.fp.batch.service.PcdoLoadStepTest;
import org.kuali.kfs.gl.batch.CollectorStepTest;
import org.kuali.kfs.gl.batch.service.CollectorServiceTest;
import org.kuali.kfs.sys.batch.BatchInputFileTypeTest;

/**
 * Builds Suite for all batch tests.
 */
public class BatchAllTests {

    /**
     * Suite for all batch screen tests. JUnit TestSuite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("Batch Upload Tests");

        suite.addTestSuite(BatchInputFileServiceTest.class);
        suite.addTestSuite(BatchInputServiceParseTest.class);
        suite.addTestSuite(BatchInputServiceSystemParametersTest.class);
        suite.addTestSuite(BatchInputFileTypeTest.class);
        suite.addTestSuite(PcdoLoadStepTest.class);
        suite.addTestSuite(CollectorStepTest.class);
        suite.addTestSuite(CollectorServiceTest.class);

        return suite;
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}
