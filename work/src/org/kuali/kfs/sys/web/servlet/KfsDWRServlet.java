/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.sys.web.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.Container;
import org.directwebremoting.extend.Configurator;
import org.directwebremoting.impl.DwrXmlConfigurator;
import org.directwebremoting.impl.StartupUtil;
import org.directwebremoting.servlet.DwrServlet;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.spring.NamedOrderedListBean;
import org.springframework.core.io.DefaultResourceLoader;

public class KfsDWRServlet extends DwrServlet {
    /**
     * 
     */
    private static final long serialVersionUID = -3903455224197903186L;

    protected static final String CLASSPATH_RESOURCE_PREFIX = "classpath.resource.prefix";
    public static List<String> HACK_ADDITIONAL_FILES = new ArrayList<String>();

    private Boolean springBasedConfigPath;

    /**
     * This method calls the super version then loads the dwr config file
     * specified in the loaded module definitions.
     * 
     * @see uk.ltd.getahead.dwr.DWRServlet#configure(javax.servlet.ServletConfig,
     *      uk.ltd.getahead.dwr.Configuration)
     */
    protected List<NamedOrderedListBean> getDwrNamedOrderedListBeans(String listName) {
        List <NamedOrderedListBean> dwrListBeans = new ArrayList<NamedOrderedListBean>();
        Map<String, NamedOrderedListBean> namedOrderedListBeans = SpringContext.getBeansOfType(NamedOrderedListBean.class);
         for (NamedOrderedListBean nameOrderedListBean : namedOrderedListBeans.values()) {
            if (nameOrderedListBean.getName().equals(listName)) {
                dwrListBeans.add((NamedOrderedListBean) nameOrderedListBean);
            }
        }
        return dwrListBeans;
    }
    
    protected DwrXmlConfigurator generateConfigurator(DefaultResourceLoader resourceLoader, String scriptConfigurationFilePath ) throws ServletException {
        try {
            InputStream is = resourceLoader.getResource(scriptConfigurationFilePath).getInputStream();
            DwrXmlConfigurator dwrXmlConfigurator = new DwrXmlConfigurator();
            dwrXmlConfigurator.setInputStream(is);
            return dwrXmlConfigurator;
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
    @Override
    protected void configureContainer(Container container, ServletConfig servletConfig) throws ServletException, IOException {       
        List<Configurator> configurators = new ArrayList<Configurator>();
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
        String classpathResourcePrefix = KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(CLASSPATH_RESOURCE_PREFIX);
        for (NamedOrderedListBean namedOrderedListBean : this.getDwrNamedOrderedListBeans(KFSConstants.SCRIPT_CONFIGURATION_FILES_LIST_NAME)) {
            for (String scriptConfigurationFilePath : namedOrderedListBean.getList()) {
                if (getSpringBasedConfigPath()) {
                      try {
                        configurators.add( this.generateConfigurator(resourceLoader, scriptConfigurationFilePath));                       
                       } catch (Exception e) {
                        throw new ServletException(e);
                    }
                } 
            }
        }
        
        KualiModuleService kmi = SpringContext.getBean(KualiModuleService.class);
        List<ModuleService> modules = kmi.getInstalledModuleServices();
        
        for (ModuleService moduleService : modules) {
            for (String scriptConfigurationFilePath : moduleService.getModuleConfiguration().getScriptConfigurationFilePaths()) {
                if (!StringUtils.isBlank(scriptConfigurationFilePath))
                     try {
                         configurators.add( this.generateConfigurator(resourceLoader, scriptConfigurationFilePath));       
                    } catch (Exception e) {
                        throw new ServletException(e);
                    }
              }    
        }
        
        for (String configFile : HACK_ADDITIONAL_FILES) {
             try {
                String scriptConfigurationFilePath = classpathResourcePrefix + configFile;
                configurators.add( this.generateConfigurator(resourceLoader, scriptConfigurationFilePath)); 
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
        try
        {
            super.configureContainer(container, servletConfig);
            StartupUtil.configure(container, configurators);
        }
        catch (IOException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new ServletException(ex);
        }
    }

    public Boolean getSpringBasedConfigPath() {
        return springBasedConfigPath;
    }

    public void setSpringBasedConfigPath(Boolean springBasedConfigPath) {
        this.springBasedConfigPath = springBasedConfigPath;
    }

    /**
     * @see javax.servlet.GenericServlet#init()
     */

    @Override
    public void init() throws ServletException {
        setSpringBasedConfigPath(new Boolean(this.getInitParameter("springpath")));
        super.init();
    }

}
