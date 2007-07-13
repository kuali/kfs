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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.kuali.core.web.listener.JstlConstantsInitListener;

public class WebApplicationInitListener extends JstlConstantsInitListener implements ServletContextListener {
    private static final String JSTL_CONSTANTS_CLASSNAMES_KEY = "jstl.constants.classnames";
    private Logger log;
    
    public void contextInitialized(ServletContextEvent sce) {
        Log4jConfigurer.configureLogging();
        log = Logger.getLogger(WebApplicationInitListener.class);
        SpringContext.initializeApplicationContext();
        for (String jstlConstantsClassname : SpringContext.getListConfigurationProperty(JSTL_CONSTANTS_CLASSNAMES_KEY)) {
            try {
                Class jstlConstantsClass = Class.forName(jstlConstantsClassname);
                sce.getServletContext().setAttribute(jstlConstantsClass.getSimpleName(), jstlConstantsClass.newInstance());
            }
            catch (Exception e) {
                log.warn("Unable to load jstl constants class: " + jstlConstantsClassname, e);
            }
        }
        super.contextInitialized(sce);
        log.info("Finished web application context initialization");
    }

    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Started web application context destruction");
        SpringContext.close();
        super.contextDestroyed(sce);
    }
}
