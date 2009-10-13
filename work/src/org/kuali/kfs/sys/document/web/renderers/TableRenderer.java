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

import org.kuali.kfs.sys.document.web.AccountingLineTable;

/**
 * Renders a table
 */
public class TableRenderer implements Renderer {
    AccountingLineTable table;

    /**
     * Clears out the table
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#clear()
     */
    public void clear() {
        table = null;
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            out.write(buildBeginningTableTag());
            table.renderChildrenRows(pageContext, parentTag);
            out.write(buildEndingTableTag());
        }
        catch (IOException ioe) {
            throw new JspException("Difficulty with rendering inner table", ioe);
        }
    }
    
    /**
     * Builds the opening tag of the table, ie <table class="datatable">
     * @return the String for the opening tag
     */
    protected String buildBeginningTableTag() {
        return "<table class=\"datatable\">";
    }
    
    /**
     * Builds the closing tag of the table, ie </table>
     * @return the String for the closing tag
     */
    protected String buildEndingTableTag() {
        return "</table>";
    }

    /**
     * Gets the table attribute. 
     * @return Returns the table.
     */
    public AccountingLineTable getTable() {
        return table;
    }

    /**
     * Sets the table attribute value.
     * @param table The table to set.
     */
    public void setTable(AccountingLineTable table) {
        this.table = table;
    }
}
