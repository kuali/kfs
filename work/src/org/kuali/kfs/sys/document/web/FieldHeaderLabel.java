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
package org.kuali.kfs.sys.document.web;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.document.web.renderers.LabelRenderer;

/**
 * A class which represents a renderable header label for an input
 */
public class FieldHeaderLabel extends HeaderLabel {
    private HeaderLabelPopulating headerLabelPopulator;
    private String label;
    private boolean readOnly = false;
    private boolean required = false;
    private String labelFor;
    private String fullClassNameForHelp;
    private String attributeEntryForHelp;
    
    /**
     * Constructs a FieldHeaderLabel, forcing an implementation of HeaderLabelPopulating to be passed in
     * @param headerLabelPopulator the populator who will populate this label when the time has come
     */
    public FieldHeaderLabel(HeaderLabelPopulating headerLabelPopulator) {
        this.headerLabelPopulator = headerLabelPopulator;
    }

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
     * @see org.kuali.kfs.sys.document.web.RenderableElement#renderElement(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag, org.kuali.kfs.sys.document.web.AccountingLineRenderingContext)
     */
    public void renderElement(PageContext pageContext, Tag parentTag, AccountingLineRenderingContext renderingContext) throws JspException {
        headerLabelPopulator.populateHeaderLabel(this, renderingContext);
        LabelRenderer renderer = new LabelRenderer();
        renderer.setLabel(label);
        renderer.setRequired(required);
        renderer.setReadOnly(readOnly);
        renderer.setLabelFor(labelFor);
        if (!StringUtils.isBlank(fullClassNameForHelp)) {
            renderer.setFullClassNameForHelp(fullClassNameForHelp);
        }
        if (!StringUtils.isBlank(attributeEntryForHelp)) {
            renderer.setAttributeEntryForHelp(attributeEntryForHelp);
        }
        renderer.render(pageContext, parentTag);
        renderer.clear();
    }

}
