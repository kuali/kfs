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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.service.AccountingLineFieldRenderingTransformation;

/**
 * There are sometimes line elements which have fewer cells than other line elements within
 * a lines element; this element exists to fill those out.  
 */
public class PlaceHoldingLayoutElement implements TableJoiningWithHeader {
    private int colSpan;
    
    /**
     * Constructs a PlaceHoldingLayoutElement, setting the colspan for the element
     * @param colSpan the colspan to set
     */
    public PlaceHoldingLayoutElement(int colSpan) {
        this.colSpan = colSpan;
    }

    /**
     * Returns a header with a non-breaking space
     * @see org.kuali.kfs.sys.document.web.TableJoiningWithHeader#createHeaderLabel()
     */
    public HeaderLabel createHeaderLabel() {
        return new LiteralHeaderLabel("&nbsp;");
    }

    /**
     * The point of this thing is to show up
     * @see org.kuali.kfs.sys.document.web.TableJoiningWithHeader#isHidden()
     */
    public boolean isHidden() {
        return false;
    }

    /**
     * Returns an empty String
     * @see org.kuali.kfs.sys.document.web.TableJoining#getName()
     */
    public String getName() {
        return "";
    }

    /**
     * This only requests one row, not that it really matters.
     * @see org.kuali.kfs.sys.document.web.TableJoining#getRequestedRowCount()
     */
    public int getRequestedRowCount() {
        return 1;
    }

    /**
     * Joins the given row and header
     * @see org.kuali.kfs.sys.document.web.TableJoining#joinRow(org.kuali.kfs.sys.document.web.AccountingLineTableRow, org.kuali.kfs.sys.document.web.AccountingLineTableRow)
     */
    public void joinRow(AccountingLineTableRow headerLabelRow, AccountingLineTableRow row) {
        if (row != null) {
            headerLabelRow.addCell(getLabelCell());
            row.addCell(getPlaceHoldingCell());
        } else {
            headerLabelRow.addCell(getPlaceHoldingCell());
        }
    }

    /**
     * This will likely never be called
     * @see org.kuali.kfs.sys.document.web.TableJoining#joinTable(java.util.List)
     */
    public void joinTable(List<AccountingLineTableRow> rows) {
        AccountingLineTableCell cell = getPlaceHoldingCell();
        cell.setRowSpan(rows.size());
        rows.get(0).addCell(getPlaceHoldingCell());
    }
    
    /**
     * Creates a place holding label cell
     * @param rowSpan the row span the cell should be
     * @return a table cell holding a place holding label cell
     */
    protected AccountingLineTableCell getLabelCell() {
        AccountingLineTableCell cell = new AccountingLineTableCell();
        cell.setColSpan(colSpan);
        cell.setRendersAsHeader(true);
        cell.addRenderableElement(createHeaderLabel());
        return cell;
    }
    
    /**
     * Returns an empty table cell, colspan cells wide
     * @param rowSpan the number of rows this cell should span
     * @return an empty accounting line table cell that will fill up the space
     */
    protected AccountingLineTableCell getPlaceHoldingCell() {
        AccountingLineTableCell cell = new AccountingLineTableCell();
        cell.setColSpan(colSpan);
        cell.addRenderableElement(createHeaderLabel());
        return cell;
    }

    /**
     * No fields to transform
     * @see org.kuali.kfs.sys.document.web.TableJoining#performFieldTransformations(java.util.List, org.kuali.kfs.sys.businessobject.AccountingLine, java.util.Map, java.util.Map)
     */
    public void performFieldTransformations(List<AccountingLineFieldRenderingTransformation> fieldTransformations, AccountingLine accountingLine, Map unconvertedValues) {}

    /**
     * This doesn't have any child blocks
     * @see org.kuali.kfs.sys.document.web.TableJoining#removeAllActionBlocks()
     */
    public void removeAllActionBlocks() {}

    /**
     * This will never remove child blocks
     * @see org.kuali.kfs.sys.document.web.TableJoining#removeUnviewableBlocks(java.util.Set)
     */
    public void removeUnviewableBlocks(Set<String> unviewableBlocks) {}

    /**
     * This will never read onlyize anything
     * @see org.kuali.kfs.sys.document.web.TableJoining#readOnlyizeReadOnlyBlocks(java.util.Set)
     */
    public void readOnlyizeReadOnlyBlocks(Set<String> readOnlyBlocks) {}

    /**
     * Gets the colSpan attribute. 
     * @return Returns the colSpan.
     */
    public int getColSpan() {
        return colSpan;
    }

    /**
     * Sets the colSpan attribute value.
     * @param colSpan The colSpan to set.
     */
    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#setEditableBlocks(java.util.Set)
     */
    public void setEditableBlocks(Set<String> editableBlocks) {}
}
