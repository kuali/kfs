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

/**
 * Don't you love it when you've got to write silly code, just because of some arbitrary rule within the
 * system?  Don't you love it even more when _you_ are the one who created that arbitrary rule?  All
 * tag rendering occurs through Renderer implementations - even Strings, okay?
 */
public class StringRenderer implements Renderer {
    private String stringToRender;
    
    /**
     * Clears out the stringToRender
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#clear()
     */
    public void clear() {
        stringToRender = null;
    }

    /**
     * Renders stringToRender
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        if (stringToRender != null) {
            try {
                pageContext.getOut().write(stringToRender);
            }
            catch (IOException ioe) {
                new JspException("Difficulty rendering...oh dear...difficulty rendering a plain String.  That's just sad.", ioe);
            }
        }
    }

    /**
     * Gets the stringToRender attribute. 
     * @return Returns the stringToRender.
     */
    public String getStringToRender() {
        return stringToRender;
    }

    /**
     * Sets the stringToRender attribute value.
     * @param stringToRender The stringToRender to set.
     */
    public void setStringToRender(String stringToRender) {
        this.stringToRender = stringToRender;
    }

}
