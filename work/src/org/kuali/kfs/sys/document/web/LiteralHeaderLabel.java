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
