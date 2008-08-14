/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
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
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.web.ui.Field;

/**
 * Base class for all renderers which render fields
 */
public abstract class FieldRendererBase implements FieldRenderer {
    private Field field;
    private DataDictionaryService dataDictionaryService;
    private String dynamicNameLabel;
    private int tabIndex = -1;
    private int arbitrarilyHighTabIndex = -1;

    /**
     * Sets the field to render
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRenderer#setField(org.kuali.rice.kns.web.ui.Field)
     */
    public void setField(Field field) {
        this.field = field;
    }
    
    /**
     * Returns the field to render
     * @return the field to render
     */
    public Field getField() {
        return this.field;
    }
    
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
        this.tabIndex = -1;
        this.arbitrarilyHighTabIndex = -1;
    }
    
    /**
     * Returns an accessible title for the field being rendered
     * @return an accessible title for the field to render
     */
    protected String getAccessibleTitle() {
        return field.getFieldLabel();
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
            if (hasTabIndex()) {
                renderer.setTabIndex(getQuickfinderTabIndex());
            }
            renderer.render(pageContext, parentTag);
            renderer.clear();
        }
    }
    
    /**
     * Writes the onblur call for the wrapped field
     * @return a value for onblur=
     */
    protected String buildOnBlur() {
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
        return onblur.toString();
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
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRenderer#setTabIndex(int)
     */
    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;   
    }
    
    /**
     * Retrieves the set tab index as a String, or, if the tabIndex was never set, returns a null
     * @return the tab index as a String or null
     */
    protected String getTabIndex() {
        if (hasTabIndex()) return Integer.toString(tabIndex); 
        return null;
    }
    
    /**
     * Determines if a tab index has been set for the field being rendered
     * @return true if a tab index has been set and therefore should be rendered; false if not
     */
    protected boolean hasTabIndex() {
        return tabIndex > -1;
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
    
}
