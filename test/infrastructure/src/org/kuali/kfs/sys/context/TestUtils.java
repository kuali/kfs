/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.sys.context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.suite.AnnotationTestSuite;
import org.kuali.kfs.sys.suite.PreCommitSuite;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.springframework.aop.framework.ProxyFactory;

/**
 * This class provides utility methods for use during manual testing.
 */
@AnnotationTestSuite(PreCommitSuite.class)
public class TestUtils {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TestUtils.class);
    private static Integer fiscalYearForTesting;
    private static String periodCodeForTesting;

    private static final String PLACEHOLDER_FILENAME = "placeholder.txt";
    
    private static ParameterService parameterService;

    public static ParameterService getParameterService() {
        if ( parameterService == null ) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }
    
    /**
     * This sets a given system parameter and clears the method cache for retrieving the parameter.
     */
    public static void setSystemParameter(Class componentClass, String parameterName, String parameterText) {
        // check that we are in a test that is set to roll-back the transaction
        Exception ex = new Exception();
        ex.fillInStackTrace();
        Boolean willCommit = null;
        // loop over the stack trace
        for ( StackTraceElement ste : ex.getStackTrace() ) {
            try {
                Class clazz = Class.forName( ste.getClassName() );
                // for efficiency, only check classes that extend from KualiTestBase
                if ( KualiTestBase.class.isAssignableFrom(clazz) ) {
                    //System.err.println( "Checking Method: " + ste.toString() );
                    // check the class-level annotation to set the default for test methods in that class
                    ConfigureContext a = (ConfigureContext)clazz.getAnnotation(ConfigureContext.class);
                    if ( a != null ) {
                        willCommit = a.shouldCommitTransactions();
                    }
                    // now, check the method-level annotation
                    try {
                        Method m = clazz.getMethod(ste.getMethodName(), (Class[])null);
                        // if the method-level annotation is present, it overrides the class-level annotation
                        a = (ConfigureContext)m.getAnnotation(ConfigureContext.class);
                        if ( a != null ) {
                            willCommit = a.shouldCommitTransactions();
                        }
                    } catch ( NoSuchMethodException e ) {
                        // do nothing
                        
                    }
                }
            } catch ( Exception e ) {
                LOG.error( "Error checking stack trace element: " + ste.toString(), e );
            }
        }
        if ( willCommit == null || willCommit ) {
            throw new RuntimeException( "Attempt to set system parameter in unit test set to commit database changes.");
        }

        Parameter parameter = getParameterService().getParameter(componentClass, parameterName);
        Parameter.Builder newParm = Parameter.Builder.create(parameter);
        newParm.setValue(parameterText);
        getParameterService().updateParameter(newParm.build());
    }
    
    /**
     * Returns an invoked instance for the serviceName passed. This uses SpringContext.getService but doesn't return the proxy. Should only
     * be used for unit testing purposes
     * @param serviceName service to return
     * @throws Exception
     */
    public static Object getUnproxiedService(String serviceName) throws Exception {
        Object service = SpringContext.getService(serviceName);
        if ( service == null ) {
            return null;
        }
        try {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(service);
            Field privateAdvisedField = invocationHandler.getClass().getDeclaredField("advised");
            privateAdvisedField.setAccessible(true);
            ProxyFactory proxyFactory = (ProxyFactory) privateAdvisedField.get(invocationHandler);
            return proxyFactory.getTargetSource().getTarget();
        } catch ( IllegalArgumentException ex ) {
            return service;
        } catch ( NoSuchFieldException ex ) {
            LOG.error( "Problem obtaining advised object: " + ex.getMessage() );
            LOG.error( "Invocation Handler: " + Proxy.getInvocationHandler(service) );
            return service;
        }
    }
    
    /**
     * Writes an array to a file.  Useful for GL / LD poster file handling.
     * @param filePath file and path to write
     * @param inputTransactions data to write to pathname
     * @throws IllegalArgumentException if file already exists
     */
    public static void writeFile(String filePath, String[] inputTransactions) {
        File file = new File(filePath);
        
        if (file.exists()) {
            if(!file.delete()) {
                throw new RuntimeException("Attempt to overwrite " + file.getName() + " failed.");
            }
        }
        
        PrintStream outputFileStream = null;
        try {
            outputFileStream = new PrintStream(file);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        
        for (String line: inputTransactions){
            outputFileStream.printf("%s\n", line);
        }
        outputFileStream.close();
    }
    
    /**
     * Deletes all files from a directory except PLACEHOLDER_FILENAME.
     * @param path of the directory to empty
     */
    public static void deleteFilesInDirectory(String pathname) {
        FilenameFilter filenameFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (!name.equals(PLACEHOLDER_FILENAME));
            }
        };
        
        File directory = new File(pathname);
        File[] directoryListing = directory.listFiles(filenameFilter);
        
        if (directoryListing == null) {
            throw new IllegalArgumentException("Directory doesn't exist: " + pathname);
        } else {
            for (int i = 0; i < directoryListing.length; i++) {
                File file = directoryListing[i];
                if(!file.delete()) {
                    throw new RuntimeException("Delete of " + file.getName() + " failed.");
                }
            }
        }
    }
    
    /**
     * Returns a fiscal year for testing.  If the fiscalYearForTesting property is not null, it returns that;
     * otherwise, it runs the current fiscal year
     * @return a fiscal year suitable for testing purposes
     */
    public static Integer getFiscalYearForTesting() {
        if (fiscalYearForTesting == null) {
            fiscalYearForTesting = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        }
        return fiscalYearForTesting;
    }
    
    /**
     * Returns a period code for testing.  If the periodCodeForTesting property is not null, it returns that;
     * otherwise, it runs the current period code
     * @return a period code suitable for testing purposes
     */
    public static String getPeriodCodeForTesting() {
        if (periodCodeForTesting == null) {
            periodCodeForTesting = SpringContext.getBean(UniversityDateService.class).getCurrentUniversityDate().getUniversityFiscalAccountingPeriod();
        }
        return periodCodeForTesting;
    }
}
