/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.config;

import org.kuali.rice.core.api.config.module.RunMode;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.framework.config.module.ModuleConfigurer;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorInternal;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.KRADConstants;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SmartApplicationListener;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KRADConfigurer extends ModuleConfigurer implements SmartApplicationListener {

    private DataSource applicationDataSource;

    private boolean includeKnsSpringBeans;

    private static final String KRAD_SPRING_BEANS_PATH = "classpath:org/kuali/rice/krad/config/KRADSpringBeans.xml";
    private static final String KNS_SPRING_BEANS_PATH = "classpath:org/kuali/rice/kns/config/KNSSpringBeans.xml";

    public KRADConfigurer() {
        // TODO really the constant value should be "krad" but there's some work to do in order to make
        // that really work, see KULRICE-6532
        super(KRADConstants.KR_MODULE_NAME);
        setValidRunModes(Arrays.asList(RunMode.LOCAL));
        setIncludeKnsSpringBeans(true);
    }

    @Override
    public void addAdditonalToConfig() {
        configureDataSource();
    }

    @Override
    public List<String> getPrimarySpringFiles() {
        final List<String> springFileLocations = new ArrayList<String>();
        springFileLocations.add(KRAD_SPRING_BEANS_PATH);

        if (isIncludeKnsSpringBeans()) {
            springFileLocations.add(KNS_SPRING_BEANS_PATH);
        }

        return springFileLocations;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ContextRefreshedEvent) {
            loadDataDictionary();
            publishDataDictionaryComponents();
        }
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> aClass) {
        return true;
    }

    @Override
    public boolean supportsSourceType(Class<?> aClass) {
        return true;
    }

    @Override
    public int getOrder() {
        // return a lower value which will give the data dictionary indexing higher precedence since DD indexing should
        // be started as soon as it can be
        return -1000;
    }

    /**
     * Used to "poke" the Data Dictionary again after the Spring Context is initialized.  This is to
     * allow for modules loaded with KualiModule after the KNS has already been initialized to work.
     *
     * Also initializes the DateTimeService
     */
    protected void loadDataDictionary() {
        if (isLoadDataDictionary()) {
            LOG.info("KRAD Configurer - Loading DD");
            DataDictionaryService dds = KRADServiceLocatorWeb.getDataDictionaryService();
            if (dds == null) {
                dds = (DataDictionaryService) GlobalResourceLoader
                        .getService(KRADServiceLocatorWeb.DATA_DICTIONARY_SERVICE);
            }
            //get from datastore
            //dds.getDataDictionary().parseDataDictionaryFromDatastore(false);
            //if datastore.size == 0 {
            dds.getDataDictionary().parseDataDictionaryConfigurationFiles(false);
            dds.getDataDictionary().persistDataDictionaryToDatastore();
            //}

            if (isValidateDataDictionary()) {
                LOG.info("KRAD Configurer - Validating DD");
                dds.getDataDictionary().validateDD(isValidateDataDictionaryEboReferences());
            }

            // KULRICE-4513 After the Data Dictionary is loaded and validated, perform Data Dictionary bean overrides.
            dds.getDataDictionary().performBeanOverrides();
        }
    }

    protected void publishDataDictionaryComponents() {
        if (isComponentPublishingEnabled()) {
            long delay = getComponentPublishingDelay();
            LOG.info("Publishing of Data Dictionary components is enabled, scheduling publish after " + delay + " millisecond delay");
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            try {
                scheduler.schedule(new Runnable() {
                    @Override
                    public void run() {
                        long start = System.currentTimeMillis();
                        LOG.info("Executing scheduled Data Dictionary component publishing...");
                        try {
                            KRADServiceLocatorInternal.getDataDictionaryComponentPublisherService().publishAllComponents();
                        } catch (RuntimeException e) {
                            LOG.error("Failed to publish data dictionary components.", e);
                            throw e;
                        } finally {
                            long end = System.currentTimeMillis();
                            LOG.info("... finished scheduled execution of Data Dictionary component publishing.  Took " + (end-start) + " milliseconds");
                        }
                    }
                }, delay, TimeUnit.MILLISECONDS);
            } finally {
                scheduler.shutdown();
            }
        }
    }

    @Override
    public boolean hasWebInterface() {
        return true;
    }

    /**
     * Returns true - KNS UI should always be included.
     *
     * @see org.kuali.rice.core.framework.config.module.ModuleConfigurer#shouldRenderWebInterface()
     */
    @Override
    public boolean shouldRenderWebInterface() {
        return true;
    }



    public boolean isLoadDataDictionary() {
        return ConfigContext.getCurrentContextConfig().getBooleanProperty("load.data.dictionary", true);
    }

    public boolean isValidateDataDictionary() {
        return ConfigContext.getCurrentContextConfig().getBooleanProperty("validate.data.dictionary", false);
    }

    public boolean isValidateDataDictionaryEboReferences() {
        return ConfigContext.getCurrentContextConfig().getBooleanProperty("validate.data.dictionary.ebo.references",
                false);
    }

    public boolean isComponentPublishingEnabled() {
        return ConfigContext.getCurrentContextConfig().getBooleanProperty(
                KRADConstants.Config.COMPONENT_PUBLISHING_ENABLED, false);
    }

    public long getComponentPublishingDelay() {
        return ConfigContext.getCurrentContextConfig().getNumericProperty(KRADConstants.Config.COMPONENT_PUBLISHING_DELAY, 0);
    }

    /**
     * Used to "poke" the Data Dictionary again after the Spring Context is initialized.  This is to
     * allow for modules loaded with KualiModule after the KNS has already been initialized to work.
     *
     * Also initializes the DateTimeService
     */
    protected void configureDataSource() {
        if (getApplicationDataSource() != null) {
            ConfigContext.getCurrentContextConfig()
                    .putObject(KRADConstants.KRAD_APPLICATION_DATASOURCE, getApplicationDataSource());
        }
    }

    public DataSource getApplicationDataSource() {
        return this.applicationDataSource;
    }

    public void setApplicationDataSource(DataSource applicationDataSource) {
        this.applicationDataSource = applicationDataSource;
    }

    /**
     * Indicates whether the legacy KNS module should be included which will include
     * the KNS spring beans file
     *
     * @return boolean true if kns should be supported, false if not
     */
    public boolean isIncludeKnsSpringBeans() {
        return includeKnsSpringBeans;
    }

    /**
     * Setter for the include kns support indicator
     *
     * @param includeKnsSpringBeans
     */
    public void setIncludeKnsSpringBeans(boolean includeKnsSpringBeans) {
        this.includeKnsSpringBeans = includeKnsSpringBeans;
    }
}
