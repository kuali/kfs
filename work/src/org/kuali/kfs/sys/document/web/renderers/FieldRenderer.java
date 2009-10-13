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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.kuali.rice.kns.web.ui.Field;

/**
 * More detailed contract for renderers which render fields
 */
public interface FieldRenderer extends Renderer {
    
    /**
     * On the renderer, sets the field to render
     * @param field the field to render
     */
    public abstract void setField(Field field);
    
    /**
     * On the renderer, sets the name of the dynamic name label
     * @param label the label to set
     */
    public abstract void setDynamicNameLabel(String label);
    
    /**
     * Sets that this renderer should display as in error
     * @param error true if renderer should display as in error, false otherwise
     */
    public abstract void setShowError(boolean error);
    
    /**
     * Tells the renderer to render a quickfinder or not
     * @return true if a quick finder should be rendered, false otherwise
     */
    public abstract boolean renderQuickfinder();
    
    /**
     * Sets the tab index the field being rendered should use - if it never ever wants to get tabbed to
     * @param tabIndex a tab index no human will have the patience to reach
     */
    public abstract void setArbitrarilyHighTabIndex(int tabIndex);
    
    /**
     * Renders the opening of a no-wrap span
     * @param pageContext the page contex to render to
     * @param parentTag the tag requesting all of this rendering
     * @throws JspException thrown if something goes wrong in rendering
     */
    public abstract void openNoWrapSpan(PageContext pageContext, Tag parentTag) throws JspException;
    
    /**
     * Renders the closing of a no wrap span
     * @param pageContext the page contex to render to
     * @param parentTag the tag requesting all of this rendering
     * @throws JspException thrown if something goes wrong in rendering
     */
    public abstract void closeNoWrapSpan(PageContext pageContext, Tag parentTag) throws JspException;
    
    /**
     * Overrides the onBlur setting for this renderer
     * @param onBlur the onBlur value to set and return from buildOnBlur
     */
    public abstract void overrideOnBlur(String onBlur);
    
    /**
     * Sets the accessible title of the current field 
     * @param accessibleTitle the given the accessible title 
     */
    public abstract void setAccessibleTitle(String accessibleTitle);
}
