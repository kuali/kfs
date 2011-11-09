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
 * Abstract class which contains functionality of table joining layout elements that will eventually render fields
 */
public abstract class FieldTableJoining implements TableJoining, RenderableElement {

    /**
     * Always returns 1 - any field can live within 1 row.
     * @see org.kuali.kfs.sys.document.web.AccountingLineViewRenderableElement#getRequestedRowCount()
     */
    public int getRequestedRowCount() {
        return 1;
    }

    /**
     * Creates a table cell that encapsulates this field
     * @return the created table cell
     */
    protected AccountingLineTableCell createTableCell() {
        AccountingLineTableCell cell = new AccountingLineTableCell();
        cell.addRenderableElement(this);
        return cell;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineViewRenderableElement#joinTable(java.util.List)
     */
    public void joinTable(List<AccountingLineTableRow> rows) {
        AccountingLineTableCell cell = createTableCell();
        cell.setRowSpan(rows.size());
        rows.get(0).addCell(cell);
    }

    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#removeAllActionBlocks()
     */
    public void removeAllActionBlocks() {
        // do nothing - fields don't really have child action blocks (and action blocks which 
        // extend this method can't really do much of anything)
    }

    /**
     * Default: assumes the field is not hidden
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isHidden()
     */
    public boolean isHidden() {
        return false;
    }

    /**
     * We're going to go out on a limb and bet that this isn't an action block
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isActionBlock()
     */
    public boolean isActionBlock() {
        return false;
    }

    /**
     * Joins ths field to the header row, spans the regular row
     * @see org.kuali.kfs.sys.document.web.TableJoining#joinRow(org.kuali.kfs.sys.document.web.AccountingLineTableRow, org.kuali.kfs.sys.document.web.AccountingLineTableRow)
     */
    public void joinRow(AccountingLineTableRow headerLabelRow, AccountingLineTableRow row) {
        AccountingLineTableCell cell = createTableCell();
        cell.setRowSpan(2);
        headerLabelRow.addCell(cell);
    }

    /**
     * This is a field.  It's never empty.
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isEmpty()
     */
    public boolean isEmpty() {
        return false;
    }

    /**
     * This method really doesn't do much - it assumes there are no child fields to remove
     * @see org.kuali.kfs.sys.document.web.TableJoining#removeUnviewableBlocks()
     */
    public void removeUnviewableBlocks(Set<String> unviewableBlocks) {
        // take a nap
    }
    
    /**
     * Sets this to read only if possible
     * @see org.kuali.kfs.sys.document.web.TableJoining#readOnlyizeReadOnlyBlocks(java.util.Set)
     */
    public void readOnlyizeReadOnlyBlocks(Set<String> readOnlyBlocks) {
        if (this instanceof ReadOnlyable && readOnlyBlocks.contains(getName())) {
            ((ReadOnlyable)this).readOnlyize();
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#setEditableBlocks(java.util.Set)
     */
    public void setEditableBlocks(Set<String> editableBlocks) {
        if (this instanceof ReadOnlyable && editableBlocks.contains(getName())) {
            ((ReadOnlyable)this).setEditable();
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#performFieldTransformation(org.kuali.kfs.sys.document.service.AccountingLineFieldRenderingTransformation, org.kuali.kfs.sys.businessobject.AccountingLine, java.util.Map, java.util.Map)
     */
    public void performFieldTransformations(List<AccountingLineFieldRenderingTransformation> fieldTransformations, AccountingLine accountingLine, Map unconvertedValues) {
        // don't do nothing - we don't have no child fields        
    }
    
}
