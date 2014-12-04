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

import junit.framework.TestSuite;

/**
 * A small set of tests covering a broad (but scattered) range of functionality in various modules of Kuali. These may not be the
 * fastest tests, but their limited number allows them to be run more frequently than all the tests.
 * <p>
 * This is the suite of all test classes or methods listing this class in a {@link org.kuali.kfs.suite.AnnotationTestSuite}
 * annotation. IDEs or Ant can run this as JUnit tests. To see the members of this suite, e.g., for a usage example, do a usage
 * search on this .class (or run this suite in JUnit).
 */
public class CrossSectionSuite extends AnnotationTestSuite.Superclass {
    public static TestSuite suite() throws Exception {
        return new CrossSectionSuite().getSuite();
    }

    /**
     * This nested class is the suite of all test methods not in the enclosing suite class. IDEs or Ant can run this nested class as
     * JUnit tests.
     */
    public static class Not {
        public static TestSuite suite() throws Exception {
            return new CrossSectionSuite().getNegativeSuite();
        }
    }
}
