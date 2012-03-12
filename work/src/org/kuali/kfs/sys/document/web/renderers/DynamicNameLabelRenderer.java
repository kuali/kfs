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
