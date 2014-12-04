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
