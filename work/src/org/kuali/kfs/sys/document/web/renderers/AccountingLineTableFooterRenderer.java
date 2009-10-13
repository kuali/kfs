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
