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

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;

/**
 * The suite of all test classes with the {@link org.kuali.kfs.ConfigureContext} annotation. IDEs or Ant can run this as JUnit
 * tests.
 */
public class ContextConfiguredSuite {

    public static TestSuiteBuilder.ClassCriteria CLASS_CRITERIA = new TestSuiteBuilder.ClassCriteria() {
        public boolean includes(Class<? extends TestCase> testClass) {
            return testClass.isAnnotationPresent(ConfigureContext.class);
        }
    };

    public static TestSuite suite() throws Exception {
        return TestSuiteBuilder.build(CLASS_CRITERIA, TestSuiteBuilder.NULL_CRITERIA);
    }

    /**
     * The suite of all test classes without the {@link org.kuali.kfs.ConfigureContext} annotation. IDEs or Ant can run this nested
     * class as JUnit tests.
     */
    public static class Not {
        public static TestSuite suite() throws Exception {
            return TestSuiteBuilder.build(new TestSuiteBuilder.NegatingClassCriteria(CLASS_CRITERIA), TestSuiteBuilder.NULL_CRITERIA);
        }
    }

    /**
     * The suite of all test classes with the {@link org.kuali.kfs.ConfigureContext} annotation with a {@code session} element
     * other than {@code NO_SESSION}. IDEs or Ant can run this nested class as JUnit tests.
     */
    public static class WithSession {
        public static TestSuite suite() throws Exception {
            TestSuiteBuilder.ClassCriteria classCriteria = new TestSuiteBuilder.ClassCriteria() {
                public boolean includes(Class<? extends TestCase> testClass) {
                    ConfigureContext annotation = testClass.getAnnotation(ConfigureContext.class);
                    return annotation != null && annotation.session() != UserNameFixture.NO_SESSION;
                }
            };
            return TestSuiteBuilder.build(classCriteria, TestSuiteBuilder.NULL_CRITERIA);
        }
    }

    /**
     * The suite of all test classes with the {@link org.kuali.kfs.ConfigureContext} annotation with a {@code session} element of
     * {@code NO_SESSION} (the default). IDEs or Ant can run this nested class as JUnit tests.
     */
    public static class WithoutSession {
        public static TestSuite suite() throws Exception {
            TestSuiteBuilder.ClassCriteria classCriteria = new TestSuiteBuilder.ClassCriteria() {
                public boolean includes(Class<? extends TestCase> testClass) {
                    ConfigureContext annotation = testClass.getAnnotation(ConfigureContext.class);
                    return annotation != null && annotation.session() == UserNameFixture.NO_SESSION;
                }
            };
            return TestSuiteBuilder.build(classCriteria, TestSuiteBuilder.NULL_CRITERIA);
        }
    }
}
