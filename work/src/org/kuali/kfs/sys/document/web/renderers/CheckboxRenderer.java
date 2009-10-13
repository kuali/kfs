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
package org.kuali.kfs.sys.document.web.renderers;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.kuali.rice.kns.web.taglib.html.KNSCheckboxTag;

/**
 * Renders a field as a checkbox control
 */
public class CheckboxRenderer extends FieldRendererBase {
    private KNSCheckboxTag checkboxTag = new KNSCheckboxTag();

    /**
     * 
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRendererBase#clear()
     */
    @Override
    public void clear() {
        super.clear();
        checkboxTag.setProperty(null);
        checkboxTag.setTitle(null);
        checkboxTag.setOnblur(null);
        checkboxTag.setStyleId(null);
        checkboxTag.setPageContext(null);
        checkboxTag.setParent(null);
        checkboxTag.setValue(null);
        checkboxTag.setTabindex(null);
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        renderCheckboxTag(pageContext, parentTag);
        if (isShowError()) {
            renderErrorIcon(pageContext);
        }
    }

    /**
     * Renders the checkbox portion of this checkbox tag
     * @param pageContext the page context to render to
     * @param parentTag the parent tag requesting all this rendering
     * @param propertyPrefix the property from the form to the business object
     */
    protected void renderCheckboxTag(PageContext pageContext, Tag parentTag) throws JspException {
        checkboxTag.setPageContext(pageContext);
        checkboxTag.setParent(parentTag);
        checkboxTag.setProperty(getFieldName());
        checkboxTag.setTitle(this.getAccessibleTitle());
        checkboxTag.setOnblur(this.buildOnBlur());
        checkboxTag.setStyleId(getFieldName());
        
        checkboxTag.setPageContext(pageContext);
        checkboxTag.setParent(parentTag);
        
        checkboxTag.doStartTag();
        checkboxTag.doEndTag();
    }

    /**
     * I'm not really into quick finders
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRenderer#renderQuickfinder()
     */
    public boolean renderQuickfinder() {
        return false;
    }
    
}
