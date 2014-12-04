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
