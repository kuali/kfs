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

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.kuali.rice.kns.web.ui.Field;

/**
 * Methods needed by elements of accounting lines that plan on rendering themselves
 */
public interface RenderableElement {
    /**
     * Determines if this element is hidden or not
     * @return true if hidden, false otherwise
     */
    public abstract boolean isHidden();
    
    /**
     * Determines if this element is an action block or not
     * @return true if this is an action block, false otherwise
     */
    public abstract boolean isActionBlock();
    
    /**
     * Is this renderable element empty of any truly renderable content?
     * @return true if it should not be rendered, false otherwise
     */
    public abstract boolean isEmpty();
    
    /**
     * Renders this element
     * @param pageContext the context to render to
     * @param parentTag the parent tag that is requesting this rendering
     * @param renderingContext the context about the accounting line that this element would end up rendering
     */
    public abstract void renderElement(PageContext pageContext, Tag parentTag, AccountingLineRenderingContext renderingContext) throws JspException;
    
    /**
     * Asks that the renderable element appends any field names it knows of to the given list; this is so that proper quick finders can be generated
     * and population accomplished when fields themselves are rendered
     * @param fieldNames the List of fields to append fields to
     */
    public abstract void appendFields(List<Field> fields);
    
    /**
     * Allows the arbitrarily high tab index to be set for controls
     * @param reallyHighIndex a really high index for elements who should not be tabbed to
     */
    public abstract void populateWithTabIndexIfRequested(int reallyHighIndex);
}
