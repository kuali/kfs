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

import javax.servlet.ServletContextEvent;

import org.kuali.rice.core.web.listener.KualiInitializeListener;

public class KFSInitializeListener extends KualiInitializeListener {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KFSInitializeListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Log4jConfigurer.configureLogging(true);
        LOG.info( "Initializing Web Context" );
        LOG.info( "Calling KualiInitializeListener.contextInitialized" );
        super.contextInitialized(sce);
        LOG.info( "Completed KualiInitializeListener.contextInitialized" );

        // the super implementation above will handle the loading of Spring

        SpringContext.finishInitializationAfterRiceStartup();
        LOG.info( "Loaded Spring Context from the following locations: " + Arrays.asList( getContext().getConfigLocations() ) );
        
        SpringContext.initMemoryMonitor();
        SpringContext.initMonitoringThread();
        SpringContext.initScheduler();

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
}
