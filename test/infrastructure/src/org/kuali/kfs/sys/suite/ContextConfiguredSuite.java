/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
