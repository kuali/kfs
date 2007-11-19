/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;

import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.util.cache.MethodCacheInterceptor;
import org.kuali.core.util.properties.PropertyTree;
import org.kuali.kfs.service.ParameterService;

/**
 * This class provides utility methods for use during manual testing.
 */

public class TestUtils {
    private static final Log LOG = LogFactory.getLog(KualiTestBase.class);


    /**
     * Disables all scheduled tasks, to make debugging easier.
     */
    public static void disableScheduledTasks() {
        Timer timer = SpringContext.getBean(Timer.class);
        timer.cancel();
    }


    /**
     * Iterates through the given Collection, printing toString of each item in the collection to stderr
     * 
     * @param collection
     */
    public static void dumpCollection(Collection collection) {
        dumpCollection(collection, new ItemStringFormatter());
    }

    /**
     * Iterates through the given Collection, printing f.format() of each item in the collection to stderr
     * 
     * @param collection
     * @param formatter ItemFormatter used to format each item for printing
     */
    public static void dumpCollection(Collection collection, ItemFormatter formatter) {
        LOG.error(formatCollection(collection, formatter));
    }

    /**
     * Suitable for attaching as a detailFormatter in Eclipse
     * 
     * @param collection
     * @param formatter
     * @return String composed of contents of the given Collection, one per line, formatted by the given ItemFormatter
     */
    public static String formatCollection(Collection collection, ItemFormatter formatter) {
        StringBuffer formatted = new StringBuffer("size= ");
        formatted.append(collection.size());

        for (Iterator i = collection.iterator(); i.hasNext();) {
            formatted.append(formatter.format(i.next()));
            formatted.append("\n");
        }

        return formatted.toString();
    }


    /**
     * Iterates through the entries of the given Map, printing toString of each (key,value) to stderr
     * 
     * @param map
     */
    public static void dumpMap(Map map) {
        dumpMap(map, new EntryStringFormatter());
    }

    /**
     * Iterates through the entries of the given Map, printing formatter.format() of each Map.Entry to stderr
     * 
     * @param map
     * @param formatter
     */
    public static void dumpMap(Map map, EntryFormatter formatter) {
        LOG.error(formatMap(map, formatter));
    }

    /**
     * Suitable for attaching as a detailFormatter in Eclipse
     * 
     * @param m
     * @return String composed of contents of the given Map, one entry per line, formatted by the given EntryFormatter
     */
    public static String formatMap(Map map, EntryFormatter formatter) {
        StringBuffer formatted = new StringBuffer("size= ");
        formatted.append(map.size());

        for (Iterator i = map.entrySet().iterator(); i.hasNext();) {
            formatted.append(formatter.format((Map.Entry) i.next()));
            formatted.append("\n");
        }

        return formatted.toString();
    }


    /**
     * Recursively prints the contents of the given PropertyTree to stderr
     * 
     * @param tree
     */
    public static void dumpTree(PropertyTree tree) {
        LOG.error(formatTree(tree));
    }

    /**
     * Suitable for attaching as a detailFormatter in Eclipse
     * 
     * @param tree
     * @return String composed of the contents of the given PropertyTree, one entry per line
     */
    public static String formatTree(PropertyTree tree) {
        StringBuffer formatted = new StringBuffer("total size= " + tree.size());

        formatted.append(formatLevel(tree, 0));

        return formatted.toString();
    }

    private static String formatLevel(PropertyTree tree, int level) {
        StringBuffer formatted = new StringBuffer();

        String prefix = buildIndent(level) + ": ";

        Map children = tree.getDirectChildren();
        for (Iterator i = children.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            formatted.append(prefix);

            String key = (String) e.getKey();
            PropertyTree subtree = (PropertyTree) e.getValue();
            String directValue = subtree.toString();
            if (directValue == null) {
                formatted.append(key);
            }
            else {
                formatted.append("(");
                formatted.append(key);
                formatted.append("=");
                formatted.append(directValue);
                formatted.append(")");
            }
            formatted.append("\n");

            formatted.append(formatLevel(subtree, level + 1));
        }

        return formatted.toString();
    }

    private static String buildIndent(int level) {
        int indentSize = level * 4;
        char[] indent = new char[indentSize];
        for (int i = 0; i < indentSize; ++i) {
            indent[i] = ' ';
        }

        return new String(indent);
    }


    public interface ItemFormatter {
        public String format(Object o);
    }

    public interface EntryFormatter {
        public String format(Map.Entry e);
    }


    private static class ItemStringFormatter implements ItemFormatter {
        public String format(Object o) {
            String result = "<null>";

            if (o != null) {
                result = o.toString();
            }

            return result;
        }
    }

    private static class EntryStringFormatter implements EntryFormatter {
        public String format(Map.Entry e) {
            String key = "<null>";
            String value = "<null>";

            if (e != null) {
                if (e.getKey() != null) {
                    key = e.getKey().toString();
                }
                if (e.getValue() != null) {
                    value = e.getValue().toString();
                }
            }

            return "(" + key + "," + value + ")";
        }
    }


    /**
     * Given a list of classnames of TestCase subclasses, assembles a TestSuite containing all tests within those classes, then runs
     * those tests and logs the results.
     * <p>
     * Created this method so that I could use OptimizeIt, which was asking for a main() method to run.
     * 
     * @param args
     */
    public static void main(String args[]) {
        TestSuite tests = new TestSuite();
        for (int i = 0; i < args.length; ++i) {
            String className = args[i];

            Class testClass = null;
            try {
                testClass = Class.forName(className);

            }
            catch (ClassNotFoundException e) {
                LOG.error("unable to load class '" + className + "'");
            }

            if (testClass != null) {
                tests.addTestSuite(testClass);
            }
        }


        if (tests.countTestCases() == 0) {
            LOG.error("no tests to run, exiting");
        }
        else {
            TestRunner.run(tests);
        }
    }

    /**
     * This sets a given system parameter and clears the method cache for retrieving the parameter.
     */
    public static void setSystemParameter(Class componentClass, String parameterName, String parameterText) throws Exception {
        SpringContext.getBean(ParameterService.class).setParameterForTesting(componentClass, parameterName, parameterText);
    }

    /**
     * Converts an InputStream to a String using UTF-8 encoding.
     * 
     * @param inputStream - InputStream to convert.
     * @return String - converted from InputStream
     * @throws IOException
     */
    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        return IOUtils.toString(inputStream, "UTF-8");
    }

    /**
     * Converts a String to an InputStream using UTF-8 encoding.
     * 
     * @param string - string to convert
     * @return InputStream - converted from the given string
     * @throws IOException
     */
    public static InputStream convertStringToInputStream(String string) throws IOException {
        return IOUtils.toInputStream(string, "UTF-8");
    }

    /**
     * Returns the size of an InputStream by first converting it to an ByteArrayOutputStream and getting the size of it.
     */
    public static int getInputStreamSize(InputStream inputStream) throws IOException {
        ByteArrayOutputStream copiedOutputStream = null;
        IOUtils.copy(inputStream, copiedOutputStream);

        return copiedOutputStream.size();
    }
}
