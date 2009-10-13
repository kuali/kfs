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
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineViewHideShowLinesDefinition;
import org.kuali.kfs.sys.document.service.AccountingLineFieldRenderingTransformation;

/**
 * The layout element for a hide/show wrapper of other elements
 */
public class HideShowLayoutElement implements AccountingLineViewLineFillingElement {
    private List<AccountingLineViewLineFillingElement> lines;
    private AccountingLineViewHideShowLinesDefinition definition;

    /**
     * Returns the name of the child element - if that gets removed, then we need to be removed as well
     * @see org.kuali.kfs.sys.document.web.TableJoining#getName()
     */
    public String getName() {
        return definition.getName();
    }

    /**
     * All hide/show elements fit into one row
     * @see org.kuali.kfs.sys.document.web.TableJoining#getRequestedRowCount()
     */
    public int getRequestedRowCount() {
        return 1;
    }

    /**
     * Joins the header label row with a cell that colspans the width of the row and that spans 2
     * @see org.kuali.kfs.sys.document.web.TableJoining#joinRow(org.kuali.kfs.sys.document.web.AccountingLineTableRow, org.kuali.kfs.sys.document.web.AccountingLineTableRow)
     */
    public void joinRow(AccountingLineTableRow headerLabelRow, AccountingLineTableRow row) {
        AccountingLineTableCell cell = new AccountingLineTableCell();
        if (row != null) {
            cell.setRowSpan(2);
        }
        cell.setStyleClassOverride("total-line");
        cell.addRenderableElement(createHideShowBlock((row == null ? 1 : 2)));
        headerLabelRow.addCell(cell);
    }

    /**
     * This layout element should be stretched
     * @see org.kuali.kfs.sys.document.web.AccountingLineViewLineFillingElement#stretchToFillLine()
     */
    public boolean shouldStretchToFillLine() {
        return true;
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.web.TableJoining#joinTable(java.util.List)
     */
    public void joinTable(List<AccountingLineTableRow> rows) {
        throw new IllegalStateException("Line elements may not join a table directly; the specified rendering is incorrect");
    }
    
    /**
     * Creates the hide/show block
     * @param headerRowCount the number of header rows 
     * @return the hide show block
     */
    protected HideShowBlock createHideShowBlock(int headerRowCount) {
        HideShowBlock block = new HideShowBlock();
        List<AccountingLineTableRow> rows = createBlankRows(getRowsRequested());
        
        haveLinesJoinRows(rows, headerRowCount);
        
        block.setContentRows(rows);
        block.setDefinition(definition);
        return block;
    }
    
    /**
     * Returns the total number of rows requested by each child line
     * @return the total number of rows requested
     */
    protected int getRowsRequested() {
        int count = 0;
        for (AccountingLineViewLineFillingElement line : lines) {
            count += line.getRequestedRowCount();
        }
        return count;
    }
    
    /**
     * Creates empty rows to populate the content of the hide/show block
     * @param cellCount the number of rows which will be returned
     * @return a List of empty rows
     */
    protected List<AccountingLineTableRow> createBlankRows(int cellCount) {
        List<AccountingLineTableRow> rows = new ArrayList<AccountingLineTableRow>();
        int count = 0;
        while (count < cellCount) {
            rows.add(new AccountingLineTableRow());
            count += 1;
        }
        return rows;
    }
    
    /**
     * Causes child lines to join the given set of rows
     * @param rows the List of rows which child lines can join
     * @param headerRowCount the number of header rows
     */
    protected void haveLinesJoinRows(List<AccountingLineTableRow> rows, int headerRowCount) {
        int count = 0;
        for (AccountingLineViewLineFillingElement line : lines) {
            
            if (line.getRequestedRowCount() > 1) {
                line.joinRow(rows.get(count), rows.get(count+1));
                count += 2;
            } else {
                line.joinRow(rows.get(count), null);
                count += 1;
            }
        }
    }

    /**
     * Has the inner content perform any field transformations 
     * @see org.kuali.kfs.sys.document.web.TableJoining#performFieldTransformations(java.util.List, org.kuali.kfs.sys.businessobject.AccountingLine, java.util.Map, java.util.Map)
     */
    public void performFieldTransformations(List<AccountingLineFieldRenderingTransformation> fieldTransformations, AccountingLine accountingLine, Map unconvertedValues) {
        for (AccountingLineViewLineFillingElement line : lines) {
            line.performFieldTransformations(fieldTransformations, accountingLine, unconvertedValues);
        }
    }

    /**
     * Has the inner content read onlyize any blocks it needs to
     * @see org.kuali.kfs.sys.document.web.TableJoining#readOnlyizeReadOnlyBlocks(java.util.Set)
     */
    public void readOnlyizeReadOnlyBlocks(Set<String> readOnlyBlocks) {
        for (AccountingLineViewLineFillingElement line : lines) {
            line.readOnlyizeReadOnlyBlocks(readOnlyBlocks);
        }
    }

    /**
     * Shuffles the responsibility off to the inner content
     * @see org.kuali.kfs.sys.document.web.TableJoining#removeAllActionBlocks()
     */
    public void removeAllActionBlocks() {
        for (AccountingLineViewLineFillingElement line : lines) {
            line.removeAllActionBlocks();
        }
    }

    /**
     * Passes the unviewable blocks off the inner content
     * @see org.kuali.kfs.sys.document.web.TableJoining#removeUnviewableBlocks(java.util.Set)
     */
    public void removeUnviewableBlocks(Set<String> unviewableBlocks) {
        List<AccountingLineViewLineFillingElement> unviewableLines = new ArrayList<AccountingLineViewLineFillingElement>();
        for (AccountingLineViewLineFillingElement line : lines) {
            if (unviewableBlocks.contains(line.getName())) {
                unviewableLines.add(line);
            } else {
                line.removeUnviewableBlocks(unviewableBlocks);
            }
        }
        lines.removeAll(unviewableLines);
    }

    /**
     * Gets the lines attribute. 
     * @return Returns the lines.
     */
    public List<AccountingLineViewLineFillingElement> getLines() {
        return lines;
    }

    /**
     * Sets the lines attribute value.
     * @param lines The lines to set.
     */
    public void setLines(List<AccountingLineViewLineFillingElement> lines) {
        this.lines = lines;
    }
    
    /**
     * Adds a single line to this element's list of lines
     * @param line the line to add
     */
    public void addLine(AccountingLineViewLineFillingElement line) {
        if (lines == null) {
            lines = new ArrayList<AccountingLineViewLineFillingElement>();
        }
        lines.add(line);
    }

    /**
     * Gets the definition attribute. 
     * @return Returns the definition.
     */
    public AccountingLineViewHideShowLinesDefinition getDefinition() {
        return definition;
    }

    /**
     * Sets the definition attribute value.
     * @param definition The definition to set.
     */
    public void setDefinition(AccountingLineViewHideShowLinesDefinition definition) {
        this.definition = definition;
    }

    /**
     * Checks that all child lines are read only; if none are, then this must be read only too
     * @see org.kuali.kfs.sys.document.web.ReadOnlyable#isReadOnly()
     */
    public boolean isReadOnly() {
        for (AccountingLineViewLineFillingElement line : lines) {
            if (!line.isReadOnly()) return false;
        }
        return true;
    }

    /**
     * Read-onlyizes child lines
     * @see org.kuali.kfs.sys.document.web.ReadOnlyable#readOnlyize()
     */
    public void readOnlyize() {
        for (AccountingLineViewLineFillingElement line : lines) {
            line.readOnlyize();
        }
    }

    /**
     * Always returns 1; this will appear in one table cell
     * @see org.kuali.kfs.sys.document.web.AccountingLineViewLineFillingElement#getDisplayingFieldWidth()
     */
    public int getDisplayingFieldWidth() {
        return 1;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#setEditableBlocks(java.util.Set)
     */
    public void setEditableBlocks(Set<String> editableBlocks) {
        for (AccountingLineViewLineFillingElement line : lines) {
            line.setEditableBlocks(editableBlocks);
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.web.ReadOnlyable#setEditable()
     */
    public void setEditable() {
        for (AccountingLineViewLineFillingElement line : lines) {
            line.setEditable();
        }
    }
    
}
