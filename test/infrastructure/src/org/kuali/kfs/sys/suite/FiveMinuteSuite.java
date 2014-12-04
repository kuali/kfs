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
package org.kuali.kfs.sys.suite;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This is a plain old JUnit suite of suites that can be run within a reasonable amount of time. IDEs or Ant can run this class as
 * JUnit tests.
 */
public class FiveMinuteSuite {
    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite(FiveMinuteSuite.class.getName());
        suite.addTest(CrossSectionSuite.suite());
        suite.addTest(PreCommitSuite.suite());
        suite.addTest(ContextConfiguredSuite.Not.suite());
        return suite;
    }
}
