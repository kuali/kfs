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

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

/**
 * Renders a field as a text field with a date picker
 */
public class DateRenderer extends TextRenderer {

    /**
     * 
     * @see org.kuali.kfs.sys.document.web.renderers.TextRenderer#clear()
     */
    @Override
    public void clear() {
        super.clear();
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.web.renderers.TextRenderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag, org.kuali.rice.krad.bo.BusinessObject)
     */
    @Override
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        super.render(pageContext, parentTag);
        
        JspWriter out = pageContext.getOut();
        try {
            out.write(buildDateImage());
            out.write(buildDateJavascript());
        }
        catch (IOException ioe) {
            throw new JspException("Difficulty rendering date picker", ioe);
        }
    }
    
    /**
     * Builds the image for the icon of the date component
     * @return the HTML for the image icon for the date component
     */
    protected String buildDateImage() {
        StringBuilder dateImage = new StringBuilder();
        dateImage.append("<img src=\"");
        dateImage.append(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("kr.externalizable.images.url")); 
        dateImage.append("cal.gif\" ");
        dateImage.append("id=\"");
        dateImage.append(getFieldName());
        dateImage.append("_datepicker\" ");
        dateImage.append("style=\"cursor: pointer;\" ");
        dateImage.append("title=\"Date selector for ");
        dateImage.append(getField().getFieldLabel());
        dateImage.append("\" ");
        dateImage.append("alt=\"Date selector for ");
        dateImage.append(this.getAccessibleTitle());
        dateImage.append("\" ");
        dateImage.append("onmouseover=\"this.style.backgroundColor='red';\" ");
        dateImage.append("onmouseout=\"this.style.backgroundColor='transparent';\"");
        dateImage.append(" />\n");
        return dateImage.toString();
    }
    
    /**
     * Builds the JavaScript portion of the date picker
     * @return the HTML for the javascript to make the date component work
     */
    protected String buildDateJavascript() {
        StringBuilder dateJavascript = new StringBuilder();
        dateJavascript.append("<script type=\"text/javascript\">\n");
        dateJavascript.append("Calendar.setup(\n"); 
        dateJavascript.append("  {\n"); 
        dateJavascript.append(" inputField : \"");
        dateJavascript.append(getFieldName());
        dateJavascript.append("\", // ID of the input field\n");
        dateJavascript.append(" ifFormat : \"%m/%d/%Y\", // the date format\n"); 
        dateJavascript.append(" button : \"");
        dateJavascript.append(getFieldName());
        dateJavascript.append("_datepicker\" // ID of the button\n"); 
        dateJavascript.append("  }\n"); 
        dateJavascript.append(");\n");
        dateJavascript.append("</script>");
        
        return dateJavascript.toString();
    }

    /**
     * Overridden to do nothing - date fields never need quick finders
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRendererBase#renderQuickFinderIfNecessary(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag, org.kuali.rice.krad.bo.BusinessObject)
     */
    @Override
    protected void renderQuickFinderIfNecessary(PageContext pageContext, Tag parentTag) throws JspException {
        // do nothing
    }

    
}
