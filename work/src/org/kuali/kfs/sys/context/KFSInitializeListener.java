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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.xml.namespace.QName;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.api.util.collect.ConstantsMap;
import org.kuali.rice.core.framework.resourceloader.SpringResourceLoader;
import org.kuali.rice.core.web.listener.KualiInitializeListener;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.ConfigProperties;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADPropertyConstants;

public class KFSInitializeListener extends KualiInitializeListener {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KFSInitializeListener.class);

    protected static final String JSTL_CONSTANTS_CLASSNAMES_KEY = "jstl.constants.classnames";
//    protected static final String JSTL_CONSTANTS_MAIN_CLASS = "jstl.constants.main.class";
//    protected static final String JSTL_MAIN_CLASS_CONTEXT_NAME = "Constants";

    protected static Map<String,Class<?>> CONSTANTS_NAME_TO_CLASS_MAP = new HashMap<String, Class<?>>();
    static {
        CONSTANTS_NAME_TO_CLASS_MAP.put( "Constants", KRADConstants.class );
        CONSTANTS_NAME_TO_CLASS_MAP.put( "RiceConstants", RiceConstants.class );
        CONSTANTS_NAME_TO_CLASS_MAP.put( "KewApiConstants", KewApiConstants.class );
        CONSTANTS_NAME_TO_CLASS_MAP.put( "AuthorizationConstants", AuthorizationConstants.class );
        CONSTANTS_NAME_TO_CLASS_MAP.put( "PropertyConstants", KRADPropertyConstants.class );
    }

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

//        SpringResourceLoader mainKfsSpringResourceLoader = (SpringResourceLoader)GlobalResourceLoader.getResourceLoader( new QName("KFS", "KFS_RICE_SPRING_RESOURCE_LOADER_NAME") ); 
//        SpringContext.applicationContext = mainKfsSpringResourceLoader.getContext();
        LOG.info( "Loaded Spring Context from the following locations: " + Arrays.asList( getContext().getConfigLocations() ) );
//        GlobalResourceLoader.addResourceLoaderFirst(mainKfsSpringResourceLoader);
        
        GlobalResourceLoader.logAllContents();
        
        SpringContext.initMemoryMonitor();
        SpringContext.initMonitoringThread();
        SpringContext.initScheduler();

        installWebContextProperties( sce.getServletContext() );
        
        // This code below ensured that all messages left from the prior execution are
        // sent upon startup - don't know if we would need these for Rice 2.0
//        MessageFetcher messageFetcher = new MessageFetcher((Integer) null);
//        SpringContext.getBean(KSBThreadPool.class).execute(messageFetcher);
    
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOG.info( "Shutting Down KFS Web Application");
        SpringContext.close();
        SpringContext.applicationContext = null;
        super.contextDestroyed(sce);
    }
    
    protected void installWebContextProperties( ServletContext context ) {
        LOG.info( "Installing web context properties" );
        for (String jstlConstantsClassname : PropertyLoadingFactoryBean.getBaseListProperty(JSTL_CONSTANTS_CLASSNAMES_KEY)) {
            try {
                Class<?> jstlConstantsClass = Class.forName(jstlConstantsClassname);
                ConstantsMap constantsMap = new ConstantsMap();
                constantsMap.setConstantClass(jstlConstantsClass);
                context.setAttribute(jstlConstantsClass.getSimpleName(), constantsMap);
                LOG.info( "Added Constants Context Property: " + jstlConstantsClass.getSimpleName());
                if ( LOG.isTraceEnabled() ) {
                    LOG.trace( "Created '" + jstlConstantsClass.getSimpleName() + "' Map for Web Layer: " + constantsMap);
                }
//                if (jstlConstantsClassname.equals(JSTL_CONSTANTS_MAIN_CLASS)) {
//                    sce.getServletContext().setAttribute(JSTL_MAIN_CLASS_CONTEXT_NAME, jstlConstantsObj);
//                }
            } catch (Exception e) {
                LOG.warn("Unable to load jstl constants class: " + jstlConstantsClassname, e);
            }
        }
        
        for ( String constantName : CONSTANTS_NAME_TO_CLASS_MAP.keySet() ) {
            // publish application constants into JSP app context with name "Constants"
            ConstantsMap constantsMap = new ConstantsMap();
            constantsMap.setConstantClass(CONSTANTS_NAME_TO_CLASS_MAP.get(constantName));
            context.setAttribute(constantName, constantsMap);
            LOG.info( "Added Constants Context Property: " + constantName);
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( constantName + " Properties: " + constantsMap);
            }
        }

        // publish configuration properties into JSP app context with name "ConfigProperties"
        context.setAttribute("ConfigProperties", new ConfigProperties());
        LOG.info( "Added Constants Context Property: ConfigProperties" );
        if ( LOG.isTraceEnabled() ) {
            LOG.trace( "Created 'ConfigProperties' Map for Web Layer: " + new ConfigProperties());
        }
        // publish dataDictionary property Map into JSP app context with name "DataDictionary"
        context.setAttribute("DataDictionary", SpringContext.getBean(DataDictionaryService.class).getDataDictionaryMap());
        LOG.info( "Added Constants Context Property: DataDictionary" );
        if ( LOG.isTraceEnabled() ) {
            LOG.trace( "Created 'DataDictionary' Map for Web Layer: " + SpringContext.getBean(DataDictionaryService.class).getDataDictionaryMap());
        }

    }
}
