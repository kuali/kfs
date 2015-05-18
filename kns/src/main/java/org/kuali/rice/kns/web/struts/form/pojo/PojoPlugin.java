/**
 * Copyright 2005-2014 The Kuali Foundation
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
// begin Kuali Foundation modification
package org.kuali.rice.kns.web.struts.form.pojo;

// deleted some imports
// end Kuali Foundation modification


import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;
import org.kuali.rice.kns.web.struts.config.KualiControllerConfig;

import javax.servlet.ServletException;
import java.util.logging.Logger;

/**
 * begin Kuali Foundation modification
 * This class is the POJO Plugin implementation of the PlugIn interface.
 * end Kuali Foundation modification
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
// Kuali Foundation modification: class originally named SL plugin
public class PojoPlugin implements PlugIn {
    static final Logger logger = Logger.getLogger(PojoPlugin.class.getName());

    public static void initBeanUtils() {
        // begin Kuali Foundation modification
        ConvertUtilsBean convUtils = new ConvertUtilsBean();
        PropertyUtilsBean propUtils = new PojoPropertyUtilsBean();
        BeanUtilsBean pojoBeanUtils = new BeanUtilsBean(convUtils, propUtils);

        BeanUtilsBean.setInstance(pojoBeanUtils);
        logger.fine("Initialized BeanUtilsBean with " + pojoBeanUtils);
        // end Kuali Foundation modification
    }

    public PojoPlugin() {
    }

    public void init(ActionServlet servlet, ModuleConfig config) throws ServletException {
        initBeanUtils();
        // override the Struts ControllerConfig with our own wrapper that knows how to
        // dynamically find max file upload size according to Rice run-time settings
        config.setControllerConfig(new KualiControllerConfig(config.getControllerConfig()));
    }

    public void destroy() {
    }
}
