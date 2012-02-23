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

import org.apache.struts.taglib.html.HiddenTag;
import org.springframework.web.util.HtmlUtils;

/**
 * Renders a hidden field
 */
public class HiddenRenderer extends FieldRendererBase {
    private HiddenTag tag = new HiddenTag();

    /**
     * Resets the field on the following values on the tag: the page context, the parent tag, the property, and the value
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#clear()
     */
    @Override
    public void clear() {
        super.clear();
        tag.setPageContext(null);
        tag.setParent(null);
        tag.setProperty(null);
        tag.setValue(null);
    }

    /**
     * Renders the hidden field using a Struts html:hidden tag
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        tag.setPageContext(pageContext);
        tag.setParent(parentTag);
        tag.setProperty(getFieldName());
        if (getField().isSecure()) {
            tag.setValue(getField().getEncryptedValue());
        } else {
            tag.setValue(getField().getPropertyValue());
        }
        tag.setStyleId(getFieldName());
        tag.setWrite(false);
        tag.doStartTag();
        tag.doEndTag();
    }

    /**
     * You can't even see me...you think I got a quickfinder?
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRenderer#renderQuickfinder()
     */
    public boolean renderQuickfinder() {
        return false;
    }

}
