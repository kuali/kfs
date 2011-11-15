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

import javax.servlet.ServletContextEvent;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.kuali.rice.core.web.listener.KualiInitializeListener;

public class KFSInitializeListener extends KualiInitializeListener {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KFSInitializeListener.class);
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        super.contextInitialized(sce);
        
        // the super implementation above will handle the loading of Spring
        SpringContext.applicationContext = getContext();
        SpringContext.initMemoryMonitor();
        SpringContext.initMonitoringThread();
        SpringContext.initScheduler();

//        MessageFetcher messageFetcher = new MessageFetcher((Integer) null);
//        SpringContext.getBean(KSBThreadPool.class).execute(messageFetcher);
//        for (String jstlConstantsClassname : PropertyLoadingFactoryBean.getBaseListProperty(JSTL_CONSTANTS_CLASSNAMES_KEY)) {
//            try {
//                Class<?> jstlConstantsClass = Class.forName(jstlConstantsClassname);
//                Object jstlConstantsObj = jstlConstantsClass.newInstance();
//                sce.getServletContext().setAttribute(jstlConstantsClass.getSimpleName(), jstlConstantsObj);
//                if (jstlConstantsClassname.equals(JSTL_CONSTANTS_MAIN_CLASS)) {
//                    sce.getServletContext().setAttribute(JSTL_MAIN_CLASS_CONTEXT_NAME, jstlConstantsObj);
//                }
//            }
//            catch (Exception e) {
//                LOG.warn("Unable to load jstl constants class: " + jstlConstantsClassname, e);
//            }
//        }
//        LOG.info("Starting " + getClass().getName() + "...");
//        ServletContext context = sce.getServletContext();
//
//        // publish application constants into JSP app context with name "Constants"
//        context.setAttribute("Constants", new JSTLConstants(KRADConstants.class));
//
//        // publish application constants into JSP app context with name "Constants"
//        context.setAttribute("RiceConstants", new JSTLConstants(RiceConstants.class));
//
//        // publish application constants into JSP app context with name "Constants"
//        context.setAttribute("KewApiConstants", new JSTLConstants(KewApiConstants.class));
//
//        // publish configuration properties into JSP app context with name "ConfigProperties"
//        context.setAttribute("ConfigProperties", new ConfigProperties());
//
//        // publish dataDictionary property Map into JSP app context with name "DataDictionary"
//        context.setAttribute("DataDictionary", SpringContext.getBean(DataDictionaryService.class).getDataDictionaryMap());
//
//        // public AuthorizationConstants property Map into JSP app context with name "AuthorizationConstants"
//        context.setAttribute("AuthorizationConstants", new AuthorizationConstants());
//
//        // publish property constants into JSP app context with name "PropertyConstants"
//        context.setAttribute("PropertyConstants", new KRADPropertyConstants());
    
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        SpringContext.close();
        SpringContext.applicationContext = null;
        super.contextDestroyed(sce);
    }
    
}
