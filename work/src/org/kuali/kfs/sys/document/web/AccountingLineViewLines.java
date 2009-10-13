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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineViewLinesDefinition;
import org.kuali.kfs.sys.document.service.AccountingLineFieldRenderingTransformation;

/**
 * Represents the rendering for a bunch of elements within the accounting line view
 */
public class AccountingLineViewLines implements TableJoining, ReadOnlyable {
    private List<AccountingLineViewLineFillingElement> elements;
    private AccountingLineViewLinesDefinition definition;
    
    /**
     * Gets the definition attribute. 
     * @return Returns the definition.
     */
    public AccountingLineViewLinesDefinition getDefinition() {
        return definition;
    }
    /**
     * Sets the definition attribute value.
     * @param definition The definition to set.
     */
    public void setDefinition(AccountingLineViewLinesDefinition definition) {
        this.definition = definition;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineViewRenderableElementContainer#getName()
     */
    public String getName() {
        return definition.getElementName();
    }
    /**
     * Gets the elements attribute. 
     * @return Returns the elements.
     */
    public List<AccountingLineViewLineFillingElement> getElements() {
        return elements;
    }
    /**
     * Sets the elements attribute value.
     * @param elements The elements to set.
     */
    public void setElements(List<AccountingLineViewLineFillingElement> lines) {
        this.elements = lines;
    }
    
    /**
     * The interesting implementation...how many does it need?  Let's see here...one for each child row...
     * yes...that's right, one table row for each child row
     * @see org.kuali.kfs.sys.document.web.AccountingLineViewRenderableElement#getRequestedRowCount()
     */
    public int getRequestedRowCount() {
        int sum = 0;
        for (AccountingLineViewLineFillingElement line : elements) {
            sum += line.getRequestedRowCount();
        }
        return sum;
    }
    
    /**
     * Throws an exception - lines should never be asked to join rows
     * @see org.kuali.kfs.sys.document.web.TableJoining#joinRow(org.kuali.kfs.sys.document.web.AccountingLineTableRow)
     */
    public void joinRow(AccountingLineTableRow headerRow, AccountingLineTableRow row) {
        throw new IllegalStateException("Error in line rendering algorithm - lines cannot join a single row.");
    }
    
    /**
     * Attempts to have each child line join the rows that have been given
     * @see org.kuali.kfs.sys.document.web.TableJoining#joinTable(java.util.List)
     */
    public void joinTable(List<AccountingLineTableRow> rows) {
        final int maxExpectedLineWidth = getMaxExpectedLineWidth();
        
        int count = 0;
        for (AccountingLineViewLineFillingElement line : elements) {
            AccountingLineTableRow headerRow = rows.get(count);
            
            if (line.getRequestedRowCount() > 1) {
                line.joinRow(headerRow, rows.get(count+1));
                padOutOrStretchCells(line, maxExpectedLineWidth, headerRow, rows.get(count+1));
                
                count += 2;
            } else {
                line.joinRow(headerRow, null);
                padOutOrStretchCells(line, maxExpectedLineWidth, headerRow, null);
                
                count += 1;
            }
        }
    }
    
    /**
     * Either pads out out the given table rows with an empty cell or stretches the cell to fill the whole line 
     * @param line the line joining the table
     * @param maxExpectedLineWidth the expected width, in cell count, of the line
     * @param headerRow the first row to add padding out to
     * @param row the second row to add padding out to - if we're only filling one row, this will be null
     */
    protected void padOutOrStretchCells(AccountingLineViewLineFillingElement line, int maxExpectedLineWidth, AccountingLineTableRow headerRow, AccountingLineTableRow row) {
        final int shorterThanMax = maxExpectedLineWidth - line.getDisplayingFieldWidth();
        if (shorterThanMax > 0) {
            if (line.shouldStretchToFillLine() && headerRow.getChildCellCount() == 1) {
                headerRow.getCells().get(0).setColSpan(maxExpectedLineWidth);
                if (row != null) {
                    row.getCells().get(0).setColSpan(maxExpectedLineWidth);
                }
            } else {
                PlaceHoldingLayoutElement placeHolder = new PlaceHoldingLayoutElement(shorterThanMax);
                placeHolder.joinRow(headerRow, row);
            }
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.ReadOnlyable#readOnlyize()
     */
    public void readOnlyize() {
        for (AccountingLineViewLineFillingElement line : elements) {
            line.readOnlyize();
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.ReadOnlyable#isReadOnly()
     */
    public boolean isReadOnly() {
        for (AccountingLineViewLineFillingElement line : elements) {
            if (!line.isReadOnly()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#removeAllActionBlocks()
     */
    public void removeAllActionBlocks() {
        for (AccountingLineViewLineFillingElement line : elements) {
            line.removeAllActionBlocks();
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#removeUnviewableBlocks(java.util.Set)
     */
    public void removeUnviewableBlocks(Set<String> unviewableBlocks) {
        Set<AccountingLineViewLineFillingElement> linesToRemove = new HashSet<AccountingLineViewLineFillingElement>();
        for (AccountingLineViewLineFillingElement line : elements) {
            if (unviewableBlocks.contains(line.getName())) {
                linesToRemove.add(line);
            } else {
                line.removeUnviewableBlocks(unviewableBlocks);
            }
        }
        elements.removeAll(linesToRemove);
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#performFieldTransformation(org.kuali.kfs.sys.document.service.AccountingLineFieldRenderingTransformation, org.kuali.kfs.sys.businessobject.AccountingLine, java.util.Map, java.util.Map)
     */
    public void performFieldTransformations(List<AccountingLineFieldRenderingTransformation> fieldTransformations, AccountingLine accountingLine, Map unconvertedValues) {
        for (AccountingLineViewLineFillingElement line : elements) {
            line.performFieldTransformations(fieldTransformations, accountingLine, unconvertedValues);
        }
    }
    
    /**
     * @return the maximum expected width of any of the child line elements in cells
     */
    public int getMaxExpectedLineWidth() {
        int maxWidth = 0;
        for (AccountingLineViewLineFillingElement line: elements) {
            int width = line.getDisplayingFieldWidth();
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        return maxWidth;
    }
    
    /**
     * Shuffles the responsibility to the child lines
     * @see org.kuali.kfs.sys.document.web.TableJoining#readOnlyizeReadOnlyBlocks(java.util.Set)
     */
    public void readOnlyizeReadOnlyBlocks(Set<String> readOnlyBlocks) {
        for (AccountingLineViewLineFillingElement line : elements) {
            line.readOnlyizeReadOnlyBlocks(readOnlyBlocks);
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#setEditableBlocks(java.util.Set)
     */
    public void setEditableBlocks(Set<String> editableBlocks) {
        for (AccountingLineViewLineFillingElement line : elements) {
            line.setEditableBlocks(editableBlocks);
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.ReadOnlyable#setEditable()
     */
    public void setEditable() {
        for (AccountingLineViewLineFillingElement line : elements) {
            line.setEditable();
        }       
    }
}
