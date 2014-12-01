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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.kuali.kfs.sys.document.web.renderers.TableRenderer;
import org.kuali.rice.kns.web.ui.Field;

/**
 * An inner table inside a table cell.
 */
public class AccountingLineTable implements RenderableElement {
    private List<AccountingLineTableRow> rows;
    private AccountingLineRenderingContext renderingContext;

    /**
     * Gets the rows attribute. 
     * @return Returns the rows.
     */
    public List<AccountingLineTableRow> getRows() {
        return rows;
    }

    /**
     * Sets the rows attribute value.
     * @param rows The rows to set.
     */
    public void setRows(List<AccountingLineTableRow> rows) {
        this.rows = rows;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isHidden()
     */
    public boolean isHidden() {
        for(AccountingLineTableRow row : rows) {
            if (!row.isHidden()) {
                return false;
            }
        }
        return true;
    }

    /**
     * This is not an action block
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isActionBlock()
     */
    public boolean isActionBlock() {
        return false;
    }
    
    /**
     * Determines if this table is empty of any renderable elements
     * @return true if this is empty, false otherwise
     */
    public boolean isEmpty() {
        for (AccountingLineTableRow row : rows) {
            if (!row.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#renderElement(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void renderElement(PageContext pageContext, Tag parentTag, AccountingLineRenderingContext renderingContext) throws JspException {
        TableRenderer renderer = new TableRenderer();
        this.renderingContext = renderingContext;
        renderer.setTable(this);
        renderer.render(pageContext, parentTag);
        renderer.clear();
        this.renderingContext = null;
    }
    
    /**
     * Requests that this table render all of its children rows
     * @param pageContext the page context to render to
     * @param parentTag the parent tag requesting the rendering
     * @param accountingLine accounting line getting rendered
     * @param accountingLineProperty property to the accounting line
     * @throws JspException thrown when some sort of thing goes wrong
     */
    public void renderChildrenRows(PageContext pageContext, Tag parentTag) throws JspException {
        for (AccountingLineTableRow row : rows) {
            row.renderElement(pageContext, parentTag, renderingContext);
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#appendFieldNames(java.util.List)
     * 
     * KRAD Conversion: Customization of the fields - No use of data dictionary
     */
    public void appendFields(List<Field> fields) {
        for (AccountingLineTableRow row : rows) {
            row.appendFields(fields);
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#populateWithTabIndexIfRequested(int[], int)
     */
    public void populateWithTabIndexIfRequested(int reallyHighIndex) {
        for (AccountingLineTableRow row : rows) {
            row.populateWithTabIndexIfRequested(reallyHighIndex);
        }
    }
    
    /**
     * Adds a row to the bottom of this table's list of rows
     * @param row the row to add
     */
    public void addRow(AccountingLineTableRow row) {
        if (rows == null) {
            rows = new ArrayList<AccountingLineTableRow>();
        }
        rows.add(row);
    }
}
