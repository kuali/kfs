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
package org.kuali.kfs.web.struts.action;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.kuali.core.web.struts.action.KualiActionServlet;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.PropertyLoadingFactoryBean;
import org.kuali.rice.core.Core;

/**
 * KFSActionServlet implementation which overrides {@link #getServletConfig()} to filter out the
 * kew struts module from the init parameters in the case where KFS is running against a
 * central workflow instance.
 *
 * @author Eric Westfall
 */
public class KFSActionServlet extends KualiActionServlet {

    @Override
    public ServletConfig getServletConfig() {
        return new KualiActionServletConfig(super.getServletConfig());
    }

    /**
     * A custom ServletConfig implementation which doesn't return the config/en init param
     * whenever KFS is running against a central KEW.  Accomplishes this by implementing custom
     * {@link #getInitParameter(String)} and {@link #getInitParameterNames()} methods.
     */
    private class KualiActionServletConfig implements ServletConfig {

        private static final String KEW_STRUTS_MODULE_PARAM = "config/en";

        private ServletConfig wrapped;

        public KualiActionServletConfig(ServletConfig wrapped) {
            this.wrapped = wrapped;
        }
        public String getInitParameter(String name) {
            if (isUseStandaloneWorkflow() && KEW_STRUTS_MODULE_PARAM.equals(name)) {
                return null;
            }
            return this.wrapped.getInitParameter(name);
        }
        public Enumeration getInitParameterNames() {
            Enumeration initParameterNames = this.wrapped.getInitParameterNames();
            if (isUseStandaloneWorkflow()) {
                List<String> paramNames = new ArrayList<String>();
                while (initParameterNames.hasMoreElements()) {
                    String paramName = (String)initParameterNames.nextElement();
                    if (!KEW_STRUTS_MODULE_PARAM.equals(paramName)) {
                        paramNames.add(paramName);
                    }
                }
                return new IteratorEnumeration(paramNames.iterator());
            }
            return initParameterNames;
        }
        public ServletContext getServletContext() {
            return this.wrapped.getServletContext();
        }
        public String getServletName() {
            return this.wrapped.getServletName();
        }
        private boolean isUseStandaloneWorkflow() {
            return Boolean.valueOf(PropertyLoadingFactoryBean.getBaseProperty(KFSConstants.USE_STANDALONE_WORKFLOW));
        }
    }

}
