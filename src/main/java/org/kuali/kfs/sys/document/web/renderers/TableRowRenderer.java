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

import org.kuali.kfs.sys.document.web.AccountingLineTableRow;

/**
 * Renders a row within a table
 */
public class TableRowRenderer implements Renderer {
    private AccountingLineTableRow row;

    /**
     * Resets the table row.
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#clear()
     */
    public void clear() {
        row = null;
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            if (row.getChildCellCount() > 0) {
                out.write(buildBeginningRowTag());
                row.renderChildrenCells(pageContext, parentTag);
                out.write(buildEndingRowTag());
            }
        }
        catch (IOException ioe) {
            throw new JspException("Could not render table row", ioe);
        }
    }
    
    /**
     * 
     * @return
     */
    protected String buildBeginningRowTag() {
        return "<tr>";
    }
    
    /**
     * 
     * @return
     */
    protected String buildEndingRowTag() {
        return "</tr>";
    }

    /**
     * Gets the row attribute. 
     * @return Returns the row.
     */
    public AccountingLineTableRow getRow() {
        return row;
    }

    /**
     * Sets the row attribute value.
     * @param row The row to set.
     */
    public void setRow(AccountingLineTableRow row) {
        this.row = row;
    }
}
