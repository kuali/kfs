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
