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
