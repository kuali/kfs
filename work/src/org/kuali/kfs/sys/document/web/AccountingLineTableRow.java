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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.kuali.kfs.sys.document.web.renderers.TableRowRenderer;
import org.kuali.rice.kns.web.ui.Field;

/**
 * Represents a table row to display in an accounting view table.
 */
public class AccountingLineTableRow implements RenderableElement {
    private List<AccountingLineTableCell> cells;
    private AccountingLineRenderingContext renderingContext;
    
    /**
     * Constructs a AccountingLineTableRow
     */
    public AccountingLineTableRow() {
        cells = new ArrayList<AccountingLineTableCell>();
    }

    /**
     * Gets the cells attribute. 
     * @return Returns the cells.
     */
    public List<AccountingLineTableCell> getCells() {
        return cells;
    }

    /**
     * Sets the cells attribute value.
     * @param cells The cells to set.
     */
    public void setCells(List<AccountingLineTableCell> cells) {
        this.cells = cells;
    }
    
    /**
     * Adds a new table cell to the row
     * @param cell the cell to add to the row
     */
    public void addCell(AccountingLineTableCell cell) {
        cells.add(cell);
    }

    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isHidden()
     */
    public boolean isHidden() {
        for (AccountingLineTableCell cell : cells) {
            if (!cell.isHidden()) {
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
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isEmpty()
     */
    public boolean isEmpty() {
        for (AccountingLineTableCell cell : cells) {
            if (!cell.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#renderElement(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void renderElement(PageContext pageContext, Tag parentTag, AccountingLineRenderingContext renderingContext) throws JspException {
        TableRowRenderer renderer = new TableRowRenderer();
        this.renderingContext = renderingContext;
        renderer.setRow(this);
        renderer.render(pageContext, parentTag);
        renderer.clear();
        this.renderingContext = null;
    }
    
    /**
     * Requests that the row renders all of its children cells
     * @param pageContext the page contex to render to
     * @param parentTag the tag requesting all this rendering
     * @param accountingLine the accounting line to render
     * @param accountingLineProperty the property from the form to the accounting line
     * @throws JspException exception thrown when...something...goes, I don't know...wrong or somethin'
     */
    public void renderChildrenCells(PageContext pageContext, Tag parentTag) throws JspException {
        for (AccountingLineTableCell cell : cells) {
            cell.renderElement(pageContext, parentTag, renderingContext);
        }
    }
    
    /**
     * Returns the number of children cells this row has
     * @return the number of children cells this row has
     */
    public int getChildCellCount() {
        return cells.size();
    }
    
    /**
     * @return returns the number of cells which will actually be rendered (ie, colspans are taken into account)
     */
    public int getChildRenderableCount() {
        int count = 0;
        for (AccountingLineTableCell cell : cells) {
            count += cell.getColSpan();
        }
        return count;
    }
    
    /**
     * Dutifully appends the names of any fields it knows about to the given List of field names
     * @param fieldNames a List of field names to append other names to
     * 
     * KRAD Conversion: Customization of the fields - No use of data dictionary
     */
    public void appendFields(List<Field> fields) {
        for (AccountingLineTableCell cell : cells) {
            cell.appendFields(fields);
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#populateWithTabIndexIfRequested(int[], int)
     */
    public void populateWithTabIndexIfRequested(int reallyHighIndex) {
        for (AccountingLineTableCell cell : cells) {
            cell.populateWithTabIndexIfRequested(reallyHighIndex);
        }
    }
    
    /**
     * Determines whether each cell is safe to remove; if so, simply removes that cell
     * @return true if the row can be safely removed; false otherwise
     */
    public boolean safeToRemove() {
        for (AccountingLineTableCell cell : cells) {
            if (!cell.safeToRemove()) return false;
        }
        return true;
    }
}
