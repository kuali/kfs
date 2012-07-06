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
package org.kuali.kfs.module.tem.context;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.web.listener.JstlConstantsInitListener;
import org.kuali.rice.ksb.messaging.MessageFetcher;

import org.kuali.kfs.sys.context.SpringContext;

/**
 * Implements the {@link ServletContextListener} to add a TEM-specific initialization of the web application context.
 * The initialization includes a proxy to the {@link ParameterService} which allows the use of system parameters
 * from JSP
 *
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
public class ContextInitListener extends JstlConstantsInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        
        // publish application constants into JSP app context with name "Tem"
        context.setAttribute("Tem", buildParameterMap());
    }

    protected Map<String, Object> buildParameterMap() {
        final Map<String, Object> retval = new HashMap<String, Object>();
        for (final String component : new String[] {"TravelAuthorization", "TravelReimbursement", "Document"}) {
            retval.put(component, buildParameterMap(component));
        }

        return retval;
    }

    protected Map<String, Object> buildParameterMap(final String component) {
        final InvocationHandler handler = new InvocationHandler() {
                @Override
                public Object invoke(final Object proxy, final Method method, final Object[] args) {
                    final String paramName = args[0] + "";
                    System.out.println("Proxying " + method.getName());
                    System.out.println("Searching for " + paramName + " in " + component);
                    if ("get".equalsIgnoreCase(method.getName())) {
                        return getParameterService().getParameterValue("KFS-TEM", component, paramName);
                    }
                    if ("contains".equalsIgnoreCase(method.getName())) {
                        try {
                            return new Boolean(getParameterService().getParameterValue("KFS-TEM", component, paramName) != null);
                        }
                        catch (Exception e) {
                            return new Boolean(false);
                        }                        
                    }
                    if (method.getName().toLowerCase().startsWith("put")
                        || "clear".equalsIgnoreCase(method.getName())
                        || "keySet".equalsIgnoreCase(method.getName())
                        || "values".equalsIgnoreCase(method.getName())
                        || "entrySet".equalsIgnoreCase(method.getName())
                        || "size".equalsIgnoreCase(method.getName())) {
                        throw new UnsupportedOperationException("Cannot modify this map");
                    }

                    return null;
                }
                
                protected ParameterService getParameterService() {
                    return SpringContext.getBean(ParameterService.class);
                }
            };
        final Map<String, Object> retval = (Map<String, Object>) 
            Proxy.newProxyInstance(Map.class.getClassLoader(),
                                   new Class[] { Map.class },
                                   handler);
        return retval;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}

}
