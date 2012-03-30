/*
 * Copyright 2011 The Kuali Foundation.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.xml.namespace.QName;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.Step;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.framework.resourceloader.SpringResourceLoader;
import org.kuali.rice.core.web.listener.KualiInitializeListener;
import org.kuali.rice.coreservice.api.CoreServiceApiServiceLocator;
import org.kuali.rice.coreservice.api.component.Component;
import org.kuali.rice.krad.service.KRADServiceLocatorInternal;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;

public class KFSInitializeListener extends KualiInitializeListener {
    protected static final String KFS_BATCH_STEP_COMPONENT_SET_ID = "STEP:KFS";
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KFSInitializeListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Log4jConfigurer.configureLogging(true);
        LOG.info( "Initializing Web Context" );
        LOG.info( "Calling KualiInitializeListener.contextInitialized" );
        super.contextInitialized(sce);
        LOG.info( "Completed KualiInitializeListener.contextInitialized" );

        // the super implementation above will handle the loading of Spring
//        Collection<SpringResourceLoader> springLoaders = RiceResourceLoaderFactory.getSpringResourceLoaders();
//        GlobalResourceLoader.getResourceLoader( new QName("KFS", "KFS_RICE_SPRING_RESOURCE_LOADER_NAME") );
//        if ( springLoaders == null || springLoaders.isEmpty() ) {
//            GlobalResourceLoader.logAllContents();
//            throw new RuntimeException( "ERROR!!! No SpringResourceLoaders found - unable to initialize KFS SpringContext" );
//        } else {
//            SpringContext.applicationContext = springLoaders.iterator().next().getContext();
//        }

        SpringResourceLoader mainKfsSpringResourceLoader = (SpringResourceLoader)GlobalResourceLoader.getResourceLoader( new QName("KFS", "KFS_RICE_SPRING_RESOURCE_LOADER_NAME") );
        SpringContext.applicationContext = mainKfsSpringResourceLoader.getContext();
        LOG.info( "Loaded Spring Context from the following locations: " + Arrays.asList( getContext().getConfigLocations() ) );
//        GlobalResourceLoader.addResourceLoaderFirst(mainKfsSpringResourceLoader);

        GlobalResourceLoader.logAllContents();

        SpringContext.initMemoryMonitor();
        SpringContext.initMonitoringThread();
        SpringContext.initScheduler();

        // KFS addition - republish all components now - until this point, the KFS DD has not been loaded
        KRADServiceLocatorInternal.getDataDictionaryComponentPublisherService().publishAllComponents();

        // KFS addition - we also publish all our Step classes as components - and these are not in the
        // DD so are not published by the command above
        publishBatchStepComponents();

        // This code below ensured that all messages left from the prior execution are
        // sent upon startup - don't know if we would need these for Rice 2.0
//        MessageFetcher messageFetcher = new MessageFetcher((Integer) null);
//        SpringContext.getBean(KSBThreadPool.class).execute(messageFetcher);

    }

    protected void publishBatchStepComponents() {
        Map<String,Step> steps = SpringContext.getBeansOfType(Step.class);
        List<Component> stepComponents = new ArrayList<Component>( steps.size() );
        for ( Step step : steps.values() ) {
            Step unproxiedStep = (Step) ProxyUtils.getTargetIfProxied(step);
            String namespaceCode = KFSConstants.CoreModuleNamespaces.KFS;
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "Building component for step: " + unproxiedStep.getName() + "(" + unproxiedStep.getClass() + ")" );
            }
            ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(unproxiedStep.getClass());
            if ( moduleService != null ) {
                namespaceCode = moduleService.getModuleConfiguration().getNamespaceCode();
            }
            Component.Builder component = Component.Builder.create(namespaceCode, unproxiedStep.getClass().getSimpleName(), unproxiedStep.getClass().getSimpleName());
            component.setComponentSetId(KFS_BATCH_STEP_COMPONENT_SET_ID);
            component.setActive(true);
            stepComponents.add(component.build());
        }

        CoreServiceApiServiceLocator.getComponentService().publishDerivedComponents(KFS_BATCH_STEP_COMPONENT_SET_ID, stepComponents);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOG.info( "Shutting Down KFS Web Application");
        SpringContext.close();
        SpringContext.applicationContext = null;
        super.contextDestroyed(sce);
    }
}
