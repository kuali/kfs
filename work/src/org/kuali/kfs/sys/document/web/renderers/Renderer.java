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

/**
 * The contract needed by renderers of all stripes to render controls
 */
public interface Renderer {
    
    /**
     * Asks this renderer to render
     * @param pageContext the JSP page context to render to
     * @param parentTag the tag that is the "parent" of this rendering
     * @param businessObject the business object being rendered
     */
    public abstract void render(PageContext pageContext, Tag parentTag) throws JspException;
    
    /**
     * If this object is held in a pool, this method is called before the object is returned to
     * the pool, so that the object can be cleaned up for the next use.
     */
    public abstract void clear();
}
