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

import org.apache.commons.lang.StringUtils;
import org.apache.struts.taglib.html.HiddenTag;
import org.springframework.web.util.HtmlUtils;

/**
 * Renders the dynamic label portion of a field
 */
public class DynamicNameLabelRenderer implements Renderer {
    private String fieldName = null;
    private String fieldValue = null;
    private HiddenTag valuePersistingTag = new HiddenTag();
    
    /**
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#clear()
     */
    public void clear() {
        fieldName = null;
        fieldValue = null;
        
        valuePersistingTag.setPageContext(null);
        valuePersistingTag.setParent(null);
        valuePersistingTag.setProperty(null);
        valuePersistingTag.setValue(null);
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag, org.kuali.rice.krad.bo.BusinessObject)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            out.write("<br />");
            out.write("<div id=\""+fieldName+".div\" class=\"fineprint\">");
            if (!StringUtils.isBlank(fieldValue)) {
                out.write(fieldValue);
            }
            out.write("</div>");
            
            if (!StringUtils.isBlank(fieldValue)) {
                renderValuePersistingTag(pageContext, parentTag);
            }
        }
        catch (IOException ioe) {
            throw new JspException("Difficulty rendering a dynamic field label", ioe);
        }
    }
    
    /**
     * If the value is present, renders that value in a tag
     * @param pageContext the page context to render to
     * @param parentTag the tag requesting all this rendering
     */
    protected void renderValuePersistingTag(PageContext pageContext, Tag parentTag) throws JspException {
        valuePersistingTag.setPageContext(pageContext);
        valuePersistingTag.setParent(parentTag);
        valuePersistingTag.setProperty(fieldName);
        valuePersistingTag.setValue(fieldValue);
        
        valuePersistingTag.doStartTag();
        valuePersistingTag.doEndTag();
    }

    /**
     * Gets the fieldName attribute. 
     * @return Returns the fieldName.
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Sets the fieldName attribute value.
     * @param fieldName The fieldName to set.
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Gets the fieldValue attribute. 
     * @return Returns the fieldValue.
     */
    public String getFieldValue() {
        return fieldValue;
    }

    /**
     * Sets the fieldValue attribute value.
     * @param fieldValue The fieldValue to set.
     */
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }
}
