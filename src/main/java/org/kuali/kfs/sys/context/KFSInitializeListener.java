/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
