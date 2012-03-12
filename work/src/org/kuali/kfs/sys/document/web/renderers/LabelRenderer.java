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
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * This renders a label (and not, as I was about to write labels a render).  It's main job
 * is to render header cells on accounting lines.
 */
public class LabelRenderer implements Renderer {
    private boolean required = false;
    private boolean readOnly = false;
    private String label;
    private String fullClassNameForHelp;
    private String attributeEntryForHelp;
    private String labelFor;

    /**
     * Gets the attributeEntryForHelp attribute. 
     * @return Returns the attributeEntryForHelp.
     */
    public String getAttributeEntryForHelp() {
        return attributeEntryForHelp;
    }

    /**
     * Sets the attributeEntryForHelp attribute value.
     * @param attributeEntryForHelp The attributeEntryForHelp to set.
     */
    public void setAttributeEntryForHelp(String attributeEntryForHelp) {
        this.attributeEntryForHelp = attributeEntryForHelp;
    }

    /**
     * Gets the fullClassNameForHelp attribute. 
     * @return Returns the fullClassNameForHelp.
     */
    public String getFullClassNameForHelp() {
        return fullClassNameForHelp;
    }

    /**
     * Sets the fullClassNameForHelp attribute value.
     * @param fullClassNameForHelp The fullClassNameForHelp to set.
     */
    public void setFullClassNameForHelp(String fullClassNameForHelp) {
        this.fullClassNameForHelp = fullClassNameForHelp;
    }

    /**
     * Gets the label attribute. 
     * @return Returns the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label attribute value.
     * @param label The label to set.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Gets the required attribute. 
     * @return Returns the required.
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Sets the required attribute value.
     * @param required The required to set.
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * Gets the readOnly attribute. 
     * @return Returns the readOnly.
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Sets the readOnly attribute value.
     * @param readOnly The readOnly to set.
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * Gets the labelFor attribute. 
     * @return Returns the labelFor.
     */
    public String getLabelFor() {
        return labelFor;
    }

    /**
     * Sets the labelFor attribute value.
     * @param labelFor The labelFor to set.
     */
    public void setLabelFor(String labelFor) {
        this.labelFor = labelFor;
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#clear()
     */
    public void clear() {
        readOnly = false;
        required = false;
        label = null;
        fullClassNameForHelp = null;
        attributeEntryForHelp = null;
        labelFor = null;
    }

    private static String APPLICATION_URL;
    
    protected String getApplicationURL() {
        if ( APPLICATION_URL == null ) {
            APPLICATION_URL = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY);
        }
        return APPLICATION_URL;
    }
    
    /**
     * 
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag, org.kuali.rice.krad.bo.BusinessObject)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        try {
            JspWriter out = pageContext.getOut();
            if (!StringUtils.isBlank(labelFor)) {
                out.write("<label for=\""+labelFor+"\">");
            }
            if (required) {
                out.write(KFSConstants.REQUIRED_FIELD_SYMBOL);
                out.write("&nbsp;");
            }
            if (!StringUtils.isBlank(fullClassNameForHelp) && !StringUtils.isBlank(attributeEntryForHelp)) {
                out.write("<a href=\"");
                out.write(getApplicationURL());
                out.write("/kr/help.do?methodToCall=getAttributeHelpText&amp;businessObjectClassName=");
                out.write(fullClassNameForHelp);
                out.write("&amp;attributeName=");
                out.write(attributeEntryForHelp);
                out.write("\" target=\"_blank\">");
            }
            out.write(label);
            if (!StringUtils.isBlank(fullClassNameForHelp) && !StringUtils.isBlank(attributeEntryForHelp)) {
                out.write("</a>");
            }
            if (!StringUtils.isBlank(labelFor)) {
                out.write("</label>");
            }
        }
        catch (IOException ioe) {
            throw new JspException("Difficulty rendering label", ioe);
        }
    }

}