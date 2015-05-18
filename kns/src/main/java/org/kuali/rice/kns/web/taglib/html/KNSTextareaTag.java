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
package org.kuali.rice.kns.web.taglib.html;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.taglib.html.TextareaTag;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.pojo.PojoForm;

import javax.servlet.jsp.JspException;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KNSTextareaTag extends TextareaTag {

	/**
	 * @see org.apache.struts.taglib.html.BaseInputTag#doEndTag()
	 */
	@Override
    public int doEndTag() throws JspException {
        int returnVal = super.doEndTag();
        if (!getDisabled() && !getReadonly()) {
        	String name = prepareName();
        	if (StringUtils.isNotBlank(name)) {
	        	ActionForm form = WebUtils.getKualiForm(pageContext);
	            if(form!=null && form instanceof PojoForm) {
	            	((PojoForm) form).registerEditableProperty(name);
	            }
        	}
        }
        return returnVal;
    }
}
