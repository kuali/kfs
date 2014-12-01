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

import org.kuali.kfs.sys.document.web.renderers.StringRenderer;

/**
 * A renderable element which renders a literal label
 */
public class LiteralHeaderLabel extends HeaderLabel {
    private String literalLabel;
    
    /**
     * Constructs a LiteralHeaderLabel, forcing the literalLabel to be passed in
     * @param literalLabel the literal label to render
     */
    public LiteralHeaderLabel(String literalLabel) {
        this.literalLabel = literalLabel;
    }
    
    /**
     * Uses StringRenderer to render the label
     * @see org.kuali.kfs.sys.document.web.RenderableElement#renderElement(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag, org.kuali.kfs.sys.document.web.AccountingLineRenderingContext)
     */
    public void renderElement(PageContext pageContext, Tag parentTag, AccountingLineRenderingContext renderingContext) throws JspException {
        StringRenderer renderer = new StringRenderer();
        renderer.setStringToRender(literalLabel);
        renderer.render(pageContext, parentTag);
        renderer.clear();
    }

}
