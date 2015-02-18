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

    /**
     * When called, this will render an image that, when clicked
     * shows a temporary page to the user with a significantly larger input area
     * so that a detailed message can be composed comfortably for this field.
     */
    public abstract void renderExplodableLink(PageContext pageContext) throws JspException;

}
