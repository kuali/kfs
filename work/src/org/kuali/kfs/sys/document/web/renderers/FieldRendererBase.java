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
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Base class for all renderers which render fields
 */
public abstract class FieldRendererBase implements FieldRenderer {
    private Field field;
    private String dynamicNameLabel;
    private int arbitrarilyHighTabIndex = -1;
    private String onBlur;
    private boolean showError;
    private String accessibleTitle;
    private static String riceImageBase;

    /**
     * Sets the field to render
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRenderer#setField(org.kuali.rice.kns.web.ui.Field)
     * 
     * KRAD Conversion - Setting the field - No Use of data dictionary
     */
    public void setField(Field field) {
        this.field = field;
    }
    
    /**
     * Returns the field to render
     * @return the field to render
     * 
     * KRAD Conversion - Getting the field - No Use of data dictionary
     */
    public Field getField() {
        return this.field;
    }
    
    /**
     * @return the name this field should have on the form
     */
    protected String getFieldName() {
        if (!StringUtils.isBlank(field.getPropertyPrefix())) return field.getPropertyPrefix()+"."+field.getPropertyName();
        return field.getPropertyName();
    }

    /**
     * Clears the field
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#clear()
     */
    public void clear() {
        this.field = null;
        this.arbitrarilyHighTabIndex = -1;
        this.onBlur = null;
    }
    
    /**
     * Returns an accessible title for the field being rendered
     * @return an accessible title for the field to render
     */
    protected String getAccessibleTitle() {
        return accessibleTitle;
    }
    
    /**
     * Sets the accessible title of the current field 
     * @param accessibleTitle the given the accessible title 
     */
    public void setAccessibleTitle(String accessibleTitle) {
        this.accessibleTitle = accessibleTitle;
    }
    
    /**
     * Renders a quick finder for the field if one is warranted
     * @param pageContext the page context to render to
     * @param parentTag the parent tag requesting all of this rendering
     * @param businessObjectToRender the business object that will be rendered
     * @throws JspException thrown if something's off
     */
    protected void renderQuickFinderIfNecessary(PageContext pageContext, Tag parentTag) throws JspException {
        if (!StringUtils.isBlank(getField().getQuickFinderClassNameImpl()) && renderQuickfinder()) {
            QuickFinderRenderer renderer = new QuickFinderRenderer();
            renderer.setField(getField());
            renderer.setTabIndex(getQuickfinderTabIndex());
            renderer.setAccessibleTitle(getField().getFieldLabel());
            renderer.render(pageContext, parentTag);
            renderer.clear();
        }
    }
    
    /**
     * Writes the onblur call for the wrapped field
     * @return a value for onblur=
     */
    protected String buildOnBlur() {
        if (onBlur == null) {
            StringBuilder onblur = new StringBuilder();
            if (!StringUtils.isBlank(getField().getWebOnBlurHandler())) {
                onblur.append(getField().getWebOnBlurHandler());
                onblur.append("( this.name");
                if (!StringUtils.isBlank(getDynamicNameLabel())) {
                    onblur.append(", '");
                    onblur.append(getDynamicNameLabel());
                    onblur.append("'");
                }
                onblur.append(" );");
            }
            onBlur = onblur.toString();
        }
        return onBlur;
    }
    
    /**
     * Overrides the onBlur setting for this renderer
     * @param onBlur the onBlur value to set and return from buildOnBlur
     */
    public void overrideOnBlur(String onBlur) {
        this.onBlur = onBlur;
    }
    
    /**
     * @return the dynamic name label field
     */
    protected String getDynamicNameLabel() {
        return dynamicNameLabel;
    }
    
    /** 
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRenderer#setDynamicNameLabel(java.lang.String)
     */
    public void setDynamicNameLabel(String dynamicNameLabel) {
        this.dynamicNameLabel = dynamicNameLabel;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRenderer#setArbitrarilyHighTabIndex(int)
     */
    public void setArbitrarilyHighTabIndex(int tabIndex) {
        this.arbitrarilyHighTabIndex = tabIndex;   
    }
    
    /**
     * @return the tab index the quick finder should use - which, by default, is the arbitrarily high tab index
     */
    protected int getQuickfinderTabIndex() {
        return arbitrarilyHighTabIndex;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRenderer#closeNoWrapSpan()
     */
    public void closeNoWrapSpan(PageContext pageContext, Tag parentTag) throws JspException {
        try {
            pageContext.getOut().write("</span>");
        }
        catch (IOException ioe) {
            throw new JspException("Could not render closing of no-wrap span", ioe);
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRenderer#openNoWrapSpan()
     */
    public void openNoWrapSpan(PageContext pageContext, Tag parentTag) throws JspException {
        try {
            pageContext.getOut().write("<span class=\"nowrap\">");
        }
        catch (IOException ioe) {
            throw new JspException("Could not render opening of no-wrap span", ioe);
        }
    }

    /**
     * Gets the showError attribute. 
     * @return Returns the showError.
     */
    public boolean isShowError() {
        return showError;
    }

    /**
     * Sets the showError attribute value.
     * @param showError The showError to set.
     */
    public void setShowError(boolean showError) {
        this.showError = showError;
    }
    
    /**
     * Renders the error icon
     * @param pageContext the page context to render to
     * @throws IOException thrown if the pageContext cannot be written to
     */
    protected void renderErrorIcon(PageContext pageContext) throws JspException {
        try {
            pageContext.getOut().write(getErrorIconImageTag());
        }
        catch (IOException ioe) {
            throw new JspException("Could not render error icon", ioe);
        }
    }
    
    /**
     * @return the tag for the error icon
     */
    protected String getErrorIconImageTag() {
        return "<img src=\""+getErrorIconImageSrc()+"\" alt=\"error\" />";
    }
    
    /**
     * @return the source of the error icon
     */
    private String getErrorIconImageSrc() {
        return getRiceImageBase()+"errormark.gif";
    }
    
    /**
     * @return the source of rice images
     */
    private String getRiceImageBase() {
        if (riceImageBase == null) {
            riceImageBase = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KRADConstants.EXTERNALIZABLE_IMAGES_URL_KEY);
        }
        return riceImageBase;
    }
}
