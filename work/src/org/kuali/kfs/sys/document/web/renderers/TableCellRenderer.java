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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.document.web.AccountingLineTableCell;

/**
 * Renders a cell within a table
 */
public class TableCellRenderer implements Renderer {
    private AccountingLineTableCell cell;

    /**
     * Resets the cell to null
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#clear()
     */
    public void clear() {
        this.cell = null;
    }

    /**
     * Renders the table cell as a header cell as well as rendering all children renderable elements of the cell
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            out.write(buildBeginningTag());
            if (cell.hasChildElements()) {
                cell.renderChildrenElements(pageContext, parentTag);
            } else {
                out.write("&nbsp;");
            }
            out.write(buildEndingTag());
        }
        catch (IOException ioe) {
            throw new JspException("Difficulty rendering table cell", ioe);
        }
    }
    
    /**
     * Builds the opening cell tag, ie <td>
     * @return the opening cell tag
     */
    protected String buildBeginningTag() {
        StringBuilder builder = new StringBuilder();
        builder.append("<");
        builder.append(getTagName());
        if (cell.getColSpan() > 1) {
            builder.append(" colspan=\"");
            builder.append(cell.getColSpan());
            builder.append('"');
        }
        if (cell.getRowSpan() > 1) {
            builder.append(" rowspan=\"");
            builder.append(cell.getRowSpan());
            builder.append('"');
        }
        if (verticallyAlignTowardsTop()) {
            builder.append(" valign=\"top\"");
        }
        if (!StringUtils.isBlank(cell.getExtraStyle())) {
            builder.append(" style=\"");
            builder.append(cell.getExtraStyle());
            builder.append("\"");
        } else {
            builder.append(" class=\""+getStyleClass()+"\"");
        }
        builder.append(">\n");
        return builder.toString();
    }
    
    /**
     * Returns what style class to use - using the styleClassOverride of the cell if possible
     * @return the styleClassOverride if it exists, otherwise "infoline"
     */
    protected String getStyleClass() {
        return !StringUtils.isBlank(cell.getStyleClassOverride()) ? cell.getStyleClassOverride() : "infoline";
    }
    
    /**
     * Builds the closing cell tag, ie </td>
     * @return the closing cell tag
     */
    protected String buildEndingTag() {
        StringBuilder builder = new StringBuilder();
        builder.append("</");
        builder.append(getTagName());
        builder.append(">");
        return builder.toString();
    }
    
    /**
     * Returns the name of the cell tag we want to create - in this case, "td"
     * @return the String td, which is the tag name of the tags we want to produce
     */
    protected String getTagName() {
        return "td";
    }

    /**
     * Gets the cell attribute. 
     * @return Returns the cell.
     */
    public AccountingLineTableCell getCell() {
        return cell;
    }

    /**
     * Sets the cell attribute value.
     * @param cell The cell to set.
     */
    public void setCell(AccountingLineTableCell cell) {
        this.cell = cell;
    }
    
    /**
     * Determines if the cell should be veritically aligned to the top
     * @return true if the cell should vertically align to the top; false otherwise
     */
    protected boolean verticallyAlignTowardsTop() {
        return true;
    }
}
