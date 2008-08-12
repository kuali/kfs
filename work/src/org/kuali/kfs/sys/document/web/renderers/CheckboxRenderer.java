/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.web.renderers;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.taglib.html.CheckboxTag;
import org.apache.struts.taglib.html.HiddenTag;
import org.kuali.kfs.sys.KFSConstants;

/**
 * Renders a field as a checkbox control
 */
public class CheckboxRenderer extends FieldRendererBase {
    private CheckboxTag checkboxTag = new CheckboxTag();
    private HiddenTag annotationTag = new HiddenTag();

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
        
        annotationTag.setPageContext(null);
        annotationTag.setParent(null);
        annotationTag.setProperty(null);
        annotationTag.setValue(null);
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        renderCheckboxTag(pageContext, parentTag);
        renderAnnotationTag(pageContext, parentTag);
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
        checkboxTag.setTitle(getField().getFieldLabel());
        if (hasTabIndex()) {
            checkboxTag.setTabindex(getTabIndex());
        }
        checkboxTag.setOnblur(this.buildOnBlur());
        checkboxTag.setStyleId(getFieldName());
        checkboxTag.setValue(getField().getPropertyValue());
        checkboxTag.setPageContext(pageContext);
        checkboxTag.setParent(parentTag);
        
        checkboxTag.doStartTag();
        checkboxTag.doEndTag();
    }
    
    /**
     * Renders the "annotation" hidden tag as part of this checkbox tag
     * @param pageContext the page context to render to
     * @param parentTag the parent tag requesting all this rendering
     * @throws JspException thrown if something goes wrong
     */
    protected void renderAnnotationTag(PageContext pageContext, Tag parentTag) throws JspException {
        annotationTag.setPageContext(pageContext);
        annotationTag.setParent(parentTag);
        annotationTag.setProperty(getFieldName()+KFSConstants.CHECKBOX_PRESENT_ON_FORM_ANNOTATION);
        annotationTag.setValue(getAnnotationValue());
        
        annotationTag.doStartTag();
        annotationTag.doEndTag();
    }
    
    /**
     * Gets the value of the annotation tag
     * @return the String "present"
     */
    protected String getAnnotationValue() {
        return "present";
    }

    /**
     * I'm not really into quick finders
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRenderer#renderQuickfinder()
     */
    public boolean renderQuickfinder() {
        return false;
    }
    
}
