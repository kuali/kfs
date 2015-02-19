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
