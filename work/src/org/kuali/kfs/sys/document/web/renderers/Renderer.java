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
