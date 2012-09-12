/*
 * Copyright 2005-2010 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.kfs.module.tem.test.infrastructure.lifecycle;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.resourceloader.RiceResourceLoaderFactory;
import org.kuali.rice.core.resourceloader.SpringResourceLoader;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * This class implements all of the common lifecycle elements of a KC Unit Test
 */
public class TemUnitTestMainLifecycle extends TemUnitTestBaseLifecycle {
    
    public static Logger LOG = Logger.getLogger(TemUnitTestMainLifecycle.class);
    
    protected static final String TEST_CONFIG_XML = "classpath:META-INF/kc-test-config.xml";
    protected static final String DEFAULT_TEST_HARNESS_SPRING_BEANS = "classpath:spring-rice-startup-test.xml";
    protected static final String CONTEXT_NAME = "/kfs-dev";
    protected static final String RELATIVE_WEB_ROOT = "/work/web-root";
    protected static final String DEFAULT_TRANSACTION_MANAGER_NAME = "transactionManager";

    private PlatformTransactionManager transactionManager;
    private SpringResourceLoader loader;
    private int port;
    private TransactionStatus perTestTransactionStatus;
//    private TransactionStatus perClassTransactionStatus;

    /**
     * @see org.kuali.kfs.module.tem.test.infrastructure.lifecycle.TemUnitTestBaseLifecycle#doPerClassStart()
     */
    @Override
    protected void doPerClassStart() throws Throwable {
    }

    /**
     * @see org.kuali.kfs.module.tem.test.infrastructure.lifecycle.TemUnitTestBaseLifecycle#doPerClassStop()
     */
    @Override
    protected void doPerClassStop() throws Throwable {
    }

  
    /**
     * @see org.kuali.kfs.module.tem.test.infrastructure.lifecycle.TemUnitTestBaseLifecycle#doPerSuiteStart()
     */
    @Override
    protected void doPerSuiteStart() throws Throwable {
        try {
            LOG.info("Loading Spring Context...");
            Method method = SpringContext.class.getDeclaredMethod("initializeTestApplicationContext");
            method.setAccessible(true);
            method.invoke(null);
            loader = RiceResourceLoaderFactory.getSpringResourceLoader();
        }
        catch (Throwable e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
            Throwable e1 = e.getCause();
            while (e1 != null) {
                LOG.error("Caused by: ");
                e.printStackTrace();
            }
            throw e;
        }

    }

    /**
     * @see org.kuali.kfs.module.tem.test.infrastructure.lifecycle.TemUnitTestBaseLifecycle#doPerSuiteStop()
     */
    @Override
    protected void doPerSuiteStop() throws Throwable {
        System.out.println("Calling STOP");
        loader.stop();
    }

    /**
     * @see org.kuali.kfs.module.tem.test.infrastructure.lifecycle.TemUnitTestBaseLifecycle#doPerTestStart()
     */
    @Override
    protected void doPerTestStart(boolean transactional) throws Throwable {
        if (transactional) {
            DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
            defaultTransactionDefinition.setTimeout(3600);
            perTestTransactionStatus = getTransactionManager().getTransaction(defaultTransactionDefinition);
        }
        else {
            perTestTransactionStatus = null;
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.test.infrastructure.lifecycle.TemUnitTestBaseLifecycle#doPerTestStop()
     */
    @Override
    protected void doPerTestStop() throws Throwable {
        if (perTestTransactionStatus != null) {
            getTransactionManager().rollback(perTestTransactionStatus);
        }
    }

    /**
     * This method...
     * @return
     */
    private PlatformTransactionManager getTransactionManager() {
        if (transactionManager == null) {
            transactionManager = (PlatformTransactionManager) SpringContext.getService(DEFAULT_TRANSACTION_MANAGER_NAME);
        }
        return transactionManager;
    }
    
    /**
     * This method...
     * @param transactionManager
     */
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public int getPort() {
        return port;
    }

}