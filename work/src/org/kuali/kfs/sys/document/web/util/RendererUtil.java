/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document.web.util;

import javax.servlet.jsp.PageContext;

import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.pojo.PojoFormBase;

/**
 * This class contains utility methods to help render accounting line elements
 */
public class RendererUtil {
    /**
     * Registers a property name as on a form/JSP page as editable so that when the next request is submitted,
     * the property will be properly populated on the form
     * 
     * @param pageContext
     * @param editablePropertyName
     */
    public static void registerEditableProperty(PageContext pageContext, String editablePropertyName) {
        PojoFormBase form = null;
        if (pageContext.getRequest().getAttribute(WebUtils.KEY_KUALI_FORM_IN_SESSION) != null) {
            form = (PojoFormBase) pageContext.getRequest().getAttribute(WebUtils.KEY_KUALI_FORM_IN_SESSION);
        }
        else if (pageContext.getSession().getAttribute(WebUtils.KEY_KUALI_FORM_IN_SESSION) != null) {
            form = (PojoFormBase) pageContext.getSession().getAttribute(WebUtils.KEY_KUALI_FORM_IN_SESSION);
        }
        else {
            form = (PojoFormBase) KNSGlobalVariables.getKualiForm();
        }
        
        if (form != null) {
            form.registerEditableProperty(editablePropertyName);
        }
    }
}
