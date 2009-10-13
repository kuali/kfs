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
