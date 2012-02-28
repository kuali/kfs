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

import org.kuali.rice.kns.web.taglib.html.KNSTextareaTag;
import org.springframework.web.util.HtmlUtils;

/**
 * Renders a field as a text area
 */
public class TextAreaRenderer extends FieldRendererBase {
    private KNSTextareaTag tag = new KNSTextareaTag();

    /**
     * Resets the text area tag
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRendererBase#clear()
     */
    public void clear() {
        super.clear();
        tag.setPageContext(null);
        tag.setParent(null);
        tag.setProperty(null);
        tag.setValue(null);
        tag.setTitle(null);
        tag.setRows(null);
        tag.setCols(null);
        tag.setStyleClass(null);
        tag.setStyleId(null);
        tag.setTabindex(null);
    }

    /**
     * Uses the struts html:textarea tag to render a text area
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {     
        tag.setPageContext(pageContext);
        tag.setParent(parentTag);
        tag.setProperty(getFieldName());
        tag.setValue(getField().getPropertyValue());
        tag.setTitle(this.getAccessibleTitle());
        tag.setRows(Integer.toString(getField().getRows()));
        tag.setCols(Integer.toString(getField().getCols()));
        tag.setStyleClass(getField().getStyleClass());
        tag.setStyleId(getFieldName());
        
        tag.doStartTag();
        tag.doEndTag();
        
        renderQuickFinderIfNecessary(pageContext, parentTag);
        
        if (isShowError()) {
            renderErrorIcon(pageContext);
        }
    }

    /**
     * I'll take a quick finder if needed
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRenderer#renderQuickfinder()
     */
    public boolean renderQuickfinder() {
        return true;
    }

}
