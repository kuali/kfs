/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.kfs.module.tem.test.infrastructure;

import java.lang.reflect.Method;
import java.net.URL;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunListener;
import org.kuali.kfs.module.tem.test.infrastructure.lifecycle.TemUnitTestSeleniumLifecycle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;

import com.thoughtworks.selenium.Selenium;

@RunWith(TemSeleniumTestRunner.class)
public class TemSeleniumTestBase extends Assert implements TemUnitTestMethodAware {
    
    public static Logger LOG = Logger.getLogger(TemSeleniumTestBase.class);
    
    protected static WebDriver driver;
    protected static Selenium selenium;

    private static TemUnitTestSeleniumLifecycle LIFECYCLE = new TemUnitTestSeleniumLifecycle();
    private static RunListener RUN_LISTENER = new TemUnitTestRunListener(LIFECYCLE);

    private long startTime;
    private long totalMem;
    private long freeMem;
    
    private final String memStatFormat = "[%1$-7s] total: %2$10d, free: %3$10d";

    private Method method;
    
    protected boolean transactional = true;
    

    @BeforeClass
    public static final void seleniumBeforeClass() {
        if (!LIFECYCLE.isPerSuiteStarted()) {
            LIFECYCLE.startPerSuite();
            driver = LIFECYCLE.getWebDriver();
    		selenium = new WebDriverBackedSelenium(driver, "http://localhost:8080/");
        }
        LIFECYCLE.startPerClass();
    }
    
    @AfterClass
    public static final void seleniumAfterClass() {
        LIFECYCLE.stopPerClass();
    }
    
    @Before
    public void seleniumBeforeTest() {
        logBeforeRun();
        LIFECYCLE.startPerTest(transactional);
    }
    
    @After
    public void seleniumAfterTest() {
        LIFECYCLE.stopPerTest();
        logAfterRun();
    }
    
    /**
     * This method is called by the <code>KCUnitTestRunner</code> and passes the method being called so the required lifecycles can
     * be determined.
     * 
     * @param method the <code>Method</code> being called by the current test
     * 
     * @see org.kuali.kfs.module.tem.test.infrastructure.TemUnitTestMethodAware#setTestMethod(java.lang.reflect.Method)
     */
    public void setTestMethod(Method method) {
        this.method = method;
    }
        
    /**
     * This method returns the <code>RunListener</code> needed to ensure the KC persistent lifecycles shut down properly
     * @return the RunListener responsible for shutting down all KC persistent lifecycles
     */
    public static RunListener getRunListener() {
        return RUN_LISTENER;
    }
    

    /**
     * This method is the canonical <code>@Before</code> method, included here to maintain compatibility with existing subclasses
     * calling <code>super.setUp()</code>.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        // no-op
    }

    /**
     * This method is the canonical <code>@After</code> method, included here to maintain compatibility with existing subclasses
     * calling <code>super.tearDown()</code>.
     * 
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        // no-op
    }

    protected void logBeforeRun() {
        if (LOG.isInfoEnabled()) {
            statsBegin();
        }
        LOG.info("##############################################################");
        LOG.info("# Starting test " + getFullTestName() + "...");
        LOG.info("##############################################################");
    }

    protected void logAfterRun() {
        LOG.info("##############################################################");
        LOG.info("# ...finished test " + getFullTestName());
        if (LOG.isInfoEnabled()) {
            for (String stat : statsEnd()) {
                LOG.info("# " + stat);
            }
        }
        LOG.info("##############################################################");
    }
    
    private void statsBegin() {
        startTime = System.currentTimeMillis();
        totalMem = Runtime.getRuntime().totalMemory();
        freeMem = Runtime.getRuntime().freeMemory();
    }

    protected String[] statsEnd() {
        long currentTime = System.currentTimeMillis();
        long currentTotalMem = Runtime.getRuntime().totalMemory();
        long currentFreeMem = Runtime.getRuntime().freeMemory();
        return new String[]{
                String.format(memStatFormat, "MemPre", totalMem, freeMem),
                String.format(memStatFormat, "MemPost", currentTotalMem, currentFreeMem),
                String.format(memStatFormat, "MemDiff", totalMem-currentTotalMem, freeMem-currentFreeMem),
                String.format("[ElapsedTime] %1$d ms", currentTime-startTime)
        };
    }
    
    protected String getFullTestName() {
        return getClass().getSimpleName() + "." + method.getName();
    }
   
    /**
     * Gets the path of a given class file.
     * @param clazz the class
     * @return the path
     */
    protected String getFilePath(Class<?> clazz) {
        URL fileUrl = getClass().getResource("/" + clazz.getCanonicalName().replaceAll("\\.", "/") + ".class");
        assertNotNull(fileUrl);
        return fileUrl.getPath();
    }
}