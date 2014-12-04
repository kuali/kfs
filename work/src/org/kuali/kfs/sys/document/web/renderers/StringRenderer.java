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
