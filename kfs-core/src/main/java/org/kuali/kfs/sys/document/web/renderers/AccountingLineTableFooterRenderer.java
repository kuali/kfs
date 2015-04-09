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
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

/**
 * Renders the footer of an accounting line table
 */
public class AccountingLineTableFooterRenderer implements Renderer {

    /**
     * There's nothing to clear for pooling for this renderer
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#clear()
     */
    public void clear() {
        // naught to do
    }

    /**
     * Renders the table footer
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        JspWriter out = pageContext.getOut();
        
        try {
            out.write(buildTableEnd());
            out.write(buildKualiElementsNotifier());
            out.write(buildDivEnd());
        }
        catch (IOException ioe) {
            throw new JspException("Difficulty rendering accounting line table footer", ioe);
        }
    }

    /**
     * Builds the closing of the table
     * @return the closing of the table expressed in HTML
     */
    protected String buildTableEnd() {
        return "</table>\n";
    }
    
    /**
     * Builds the script that figures out all the KualiForm.eleemnts stuff
     * @return that strange script, expressed in HTML
     */
    protected String buildKualiElementsNotifier() {
        StringBuilder notifier = new StringBuilder();
        notifier.append("<SCRIPT type=\"text/javascript\">\n");
        notifier.append("\tvar kualiForm = document.forms['KualiForm'];\n");
        notifier.append("\tvar kualiElements = kualiForm.elements;\n");
        notifier.append("</SCRIPT>\n");
        return notifier.toString();
    }
    
    /**
     * Builds the close of the tab-container div
     * @return the close of the div expressed as HTML
     */
    protected String buildDivEnd() {
        return "</div>\n";
    }
}
