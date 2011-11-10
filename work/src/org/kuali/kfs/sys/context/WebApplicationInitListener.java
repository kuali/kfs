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
package org.kuali.kfs.sys.context;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.authorization.AuthorizationConstants;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.ksb.messaging.MessageFetcher;
import org.kuali.rice.ksb.messaging.threadpool.KSBThreadPool;

public class WebApplicationInitListener implements ServletContextListener {
    private static final String JSTL_CONSTANTS_CLASSNAMES_KEY = "jstl.constants.classnames";
    private static final String JSTL_CONSTANTS_MAIN_CLASS = "jstl.constants.main.class";
    private static final String JSTL_MAIN_CLASS_CONTEXT_NAME = "Constants";
    private static Logger LOG;

    public void contextInitialized(ServletContextEvent sce) {
        Log4jConfigurer.configureLogging(true);
        LOG = Logger.getLogger(WebApplicationInitListener.class);
        SpringContext.initializeApplicationContext();
        MessageFetcher messageFetcher = new MessageFetcher((Integer) null);
        SpringContext.getBean(KSBThreadPool.class).execute(messageFetcher);
        for (String jstlConstantsClassname : PropertyLoadingFactoryBean.getBaseListProperty(JSTL_CONSTANTS_CLASSNAMES_KEY)) {
            try {
                Class<?> jstlConstantsClass = Class.forName(jstlConstantsClassname);
                Object jstlConstantsObj = jstlConstantsClass.newInstance();
                sce.getServletContext().setAttribute(jstlConstantsClass.getSimpleName(), jstlConstantsObj);
                if (jstlConstantsClassname.equals(JSTL_CONSTANTS_MAIN_CLASS)) {
                    sce.getServletContext().setAttribute(JSTL_MAIN_CLASS_CONTEXT_NAME, jstlConstantsObj);
                }
            }
            catch (Exception e) {
                LOG.warn("Unable to load jstl constants class: " + jstlConstantsClassname, e);
            }
        }
        LOG.info("Starting " + getClass().getName() + "...");
        ServletContext context = sce.getServletContext();

        // publish application constants into JSP app context with name "Constants"
        context.setAttribute("Constants", new KRADConstants());

        // publish application constants into JSP app context with name "Constants"
        context.setAttribute("RiceConstants", new RiceConstants());

        // publish application constants into JSP app context with name "Constants"
        context.setAttribute("KewApiConstants", new KewConstants());

        // publish configuration properties into JSP app context with name "ConfigProperties"
        context.setAttribute("ConfigProperties", new ConfigProperties());

        // publish dataDictionary property Map into JSP app context with name "DataDictionary"
        context.setAttribute("DataDictionary", SpringContext.getBean(DataDictionaryService.class).getDataDictionaryMap());

        // public AuthorizationConstants property Map into JSP app context with name "AuthorizationConstants"
        context.setAttribute("AuthorizationConstants", new AuthorizationConstants());

        // publish property constants into JSP app context with name "PropertyConstants"
        context.setAttribute("PropertyConstants", new KRADPropertyConstants());
        LOG.info("Finished web application context initialization");
    }

    public void contextDestroyed(ServletContextEvent sce) {
        LOG.debug("Started web application context destruction");
        try {
            SpringContext.close();
        }
        catch (Exception ex) {
            LOG.error("Unable to close down Spring Context.", ex);
        }
    }
}
