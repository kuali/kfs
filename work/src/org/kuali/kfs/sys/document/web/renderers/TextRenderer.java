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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.web.taglib.html.KNSTextTag;
import org.springframework.web.util.HtmlUtils;

/**
 * Represents a field rendered as a text field
 */
public class TextRenderer extends FieldRendererBase {
    private KNSTextTag tag = new KNSTextTag();

    /**
     * cleans up the html:text tag 
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRendererBase#clear()
     */
    @Override
    public void clear() {
        super.clear();
        tag.setProperty(null);
        tag.setTitle(null);
        tag.setSize(null);
        tag.setMaxlength(null);
        tag.setOnblur(null);
        tag.setStyleClass(null);
        tag.setValue(null);
        tag.setStyleId(null);
        tag.setTabindex(null);
    }

    /**
     * Uses a struts html:text tag to render this field
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        tag.setPageContext(pageContext);
        tag.setParent(parentTag);
        tag.setProperty(getFieldName());
        tag.setTitle(getAccessibleTitle());
        tag.setSize(getFieldSize());
        //tag.setTabIndex();
        tag.setMaxlength(getFieldMaxLength());
        final String onBlur = buildOnBlur();
        if (!StringUtils.isBlank(onBlur)) {
            tag.setOnblur(buildOnBlur());
        }
        tag.setStyleClass(getField().getStyleClass());

        tag.setValue(getField().getPropertyValue());
        tag.setStyleId(getFieldName());
        
        tag.doStartTag();
        tag.doEndTag();
        
        renderQuickFinderIfNecessary(pageContext, parentTag);
        
        if (isShowError()) {
            renderErrorIcon(pageContext);
        }
    }
    
    /**
     * Determines the max length of the field
     * @return the max length of the field, formatted to a string
     */
    protected String getFieldMaxLength() {
        return Integer.toString(getField().getMaxLength());
    }

    /**
     * Determines the size of the field
     * @return the size of the field, formatted as a String
     */
    protected String getFieldSize() {
        return Integer.toString(getField().getSize());
    }

    /**
     * Yes, I'd like a quickfinder please
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRenderer#renderQuickfinder()
     */
    public boolean renderQuickfinder() {
        return true;
    }
    
}
