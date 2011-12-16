/*
 * Copyright 2006 The Kuali Foundation
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

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Utility class that builds test suites dynamically.
 * 
 * @see org.kuali.kfs.suite.ContextConfiguredSuite
 * @see org.kuali.test.suite.ShouldCommitTransactionsSuite
 * @see org.kuali.kfs.suite.CrossSectionSuite
 */
public class TestSuiteBuilder {

    public static final NullCriteria NULL_CRITERIA = new NullCriteria();

    private static final Class<TestSuiteBuilder> THIS_CLASS = TestSuiteBuilder.class;
    private static final String ROOT_PACKAGE = "org.kuali";

    /**
     * Scans *Test.class files under org.kuali for matches against the given strategies.
     * 
     * @param classCriteria strategy for whether to include a given TestCase in the suite. If included, a test class acts like a
     *        sub-suite to include all its test methods. Classes not included may still include methods individually.
     * @param methodCriteria strategy for whether to include a given test method in the suite, if the whole class was not included.
     * @return a TestSuite containing the specified tests
     * @throws java.io.IOException if the directory containing this class file cannot be scanned for other test class files
     * @throws Exception if either of the given criteria throw it
     */
    public static TestSuite build(ClassCriteria classCriteria, MethodCriteria methodCriteria) throws Exception {
        TestSuite suite = new TestSuite();
        for (Class<? extends TestCase> t : constructTestClasses(scanTestClassNames(getTestRootPackageDir()))) {
            if (t.isAnnotationPresent(Exclude.class)) {
                continue; // don't consider any methods of this test either
            }
            if (classCriteria.includes(t)) {
                suite.addTestSuite(t);
            }
            else {
                for (Method m : t.getMethods()) {
                    if (isTestMethod(m) && methodCriteria.includes(m)) {
                        suite.addTest(TestSuite.createTest(t, m.getName()));
                    }
                }
            }
        }
        suite.setName(getDefaultName());
        return suite;
    }

    /**
     * @return the name of the class calling this class
     */
    private static String getDefaultName() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        return stack[4].getClassName();
    }

    private static boolean isTestMethod(Method method) {
        return method.getName().startsWith("test") && method.getReturnType().equals(void.class) && method.getParameterTypes().length == 0;
    }

    private static ArrayList<Class<? extends TestCase>> constructTestClasses(ArrayList<String> testClassNames) {
        ArrayList<Class<? extends TestCase>> classes = new ArrayList<Class<? extends TestCase>>();
        for (String name : testClassNames) {
            try {
                classes.add(Class.forName(name).asSubclass(TestCase.class));
            }
            catch (ClassCastException e) {
                // Ignore this class.
                // Its name ends with Test but it doesn't extend TestCase, so it's not really a test class.
                // E.g., production class GenesisTest is put in build/test/classes by build.xml make-tests target.
            }
            catch (ClassNotFoundException e) {
                throw new AssertionError(e); // impossible because the .class file was under a classloader directory
            }
        }
        return classes;
    }

    /**
     * @param testRootPackageDir the directory of the ROOT_PACKAGE containing test classes
     * @return the list of fully qualified class names under that directory for each file name ending in "Test.class"
     * @throws java.io.IOException if that directory cannot be scanned
     */
    private static ArrayList<String> scanTestClassNames(File testRootPackageDir) throws IOException {
        if(!testRootPackageDir.getCanonicalPath().endsWith(ROOT_PACKAGE.replace('.', File.separatorChar))) {
            throw new AssertionError();
        }
        ArrayList<String> testClassNames = new ArrayList<String>();
        LinkedList<File> dirs = new LinkedList<File>();
        dirs.add(testRootPackageDir);
        final int lengthOfPathToRootPackageDir = testRootPackageDir.getCanonicalPath().length() - ROOT_PACKAGE.length();
        while (!dirs.isEmpty()) {
            File currentDir = dirs.removeFirst();
            LinkedList<File> subdirs = new LinkedList<File>();
            for (File f : currentDir.listFiles()) {
                if (f.isDirectory()) {
                    subdirs.addLast(f);
                }
                else {
                    if (f.isFile() && f.getName().endsWith("Test.class")) {
                        String className = f.getCanonicalPath().substring(lengthOfPathToRootPackageDir).replace(File.separatorChar, '.');
                        testClassNames.add(className.substring(0, className.length() - ".class".length()));
                    }
                }
            }
            // implement depth-first directory traversal to correspond to Ant's junitreport, without using recursion
            subdirs.addAll(dirs);
            dirs = subdirs;
        }
        return testClassNames;
    }

    /**
     * @return the parent of the directory containing this test class file
     */
    private static File getTestRootPackageDir() {
        try {
            return new File( new File( THIS_CLASS.getProtectionDomain().getCodeSource().getLocation().toURI() ), "org/kuali" );
        }
        catch (URISyntaxException e) {
            throw new AssertionError(e); // if the classloader doesn't always return the "file:" protocol, then this method needs
                                            // to be changed
        }
    }

    /**
     * Unconditionally excludes the annotated test class (and all its methods) from any suite built by this class. This is useful
     * with negative matching strategies, e.g., all test methods without the {@code @ShouldCommitTransactions} annotation, except
     * the ones in ContinuousIntegrationStartup or ContinuousIntegrationShutdown.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public static @interface Exclude {
        // no elements
    }

    /**
     * A Strategy pattern for choosing which test classes to include in a suite. A test class acts like a sub-suite to include all
     * its test methods. For test classes that do not match, test methods can still be included individually.
     */
    public static interface ClassCriteria {

        /**
         * @param testClass a TestCase to consider for the suite
         * @return whether it should be included as a sub-suite
         * @throws Exception if necessary
         */
        boolean includes(Class<? extends TestCase> testClass) throws Exception;
    }

    /**
     * A Strategy pattern for choosing which test methods to include individually in a suite. This is not used if the method's whole
     * TestCase was included.
     */
    public static interface MethodCriteria {

        /**
         * @param testMethod a test method to consider for the suite. The method name starts with "test", takes no parameters, and
         *        returns void.
         * @return whether it should be included
         * @throws Exception if necessary
         */
        boolean includes(Method testMethod) throws Exception;
    }

    /**
     * A Singleton NullObject pattern that can be passed as the other strategy when using only one strategy. This works for either
     * strategy. Using this for both strategies will build an empty suite.
     */
    private static class NullCriteria implements ClassCriteria, MethodCriteria {

        public boolean includes(Class<? extends TestCase> testClass) {
            return false;
        }

        public boolean includes(Method testMethod) {
            return false;
        }
    }

    /**
     * A Decorator pattern to negate the strategy of a ClassCriteria.
     */
    public static class NegatingClassCriteria implements ClassCriteria {

        private final ClassCriteria decorated;

        public NegatingClassCriteria(ClassCriteria decorated) {
            this.decorated = decorated;
        }

        public boolean includes(Class<? extends TestCase> testClass) throws Exception {
            return !decorated.includes(testClass);
        }
    }

    /**
     * A Decorator pattern to negate the strategy of a MethodCriteria.
     */
    public static class NegatingMethodCriteria implements MethodCriteria {

        private final MethodCriteria decorated;

        public NegatingMethodCriteria(MethodCriteria decorated) {
            this.decorated = decorated;
        }

        public boolean includes(Method method) throws Exception {
            return !decorated.includes(method);
        }
    }
}
