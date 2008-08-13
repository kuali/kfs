/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
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

import org.kuali.core.web.ui.Field;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineViewLinesDefinition;
import org.kuali.kfs.sys.document.service.AccountingLineFieldRenderingTransformation;

/**
 * Represents the rendering for a bunch of elements within the accounting line view
 */
public class AccountingLineViewLines implements TableJoining, ReadOnlyable {
    private List<AccountingLineViewLine> elements;
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
    public List<AccountingLineViewLine> getElements() {
        return elements;
    }
    /**
     * Sets the elements attribute value.
     * @param elements The elements to set.
     */
    public void setElements(List<AccountingLineViewLine> lines) {
        this.elements = lines;
    }
    
    /**
     * The interesting implementation...how many does it need?  Let's see here...one for each child row...
     * yes...that's right, one table row for each child row
     * @see org.kuali.kfs.sys.document.web.AccountingLineViewRenderableElement#getRequestedRowCount()
     */
    public int getRequestedRowCount() {
        int sum = 0;
        for (AccountingLineViewLine line : elements) {
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
        for (AccountingLineViewLine line : elements) {
            if (line.getRequestedRowCount() == 2) {
                line.joinRow(rows.get(count), rows.get(count+1));
                
                int shorterThanMax = maxExpectedLineWidth - line.getDisplayingFieldWidth();
                if (shorterThanMax > 0) {
                    PlaceHoldingLayoutElement placeHolder = new PlaceHoldingLayoutElement(shorterThanMax);
                    placeHolder.joinRow(rows.get(count), rows.get(count+1));
                }
                
                count += 2;
            } else {
                line.joinRow(rows.get(count), null);
                
                int shorterThanMax = maxExpectedLineWidth - line.getDisplayingFieldWidth();
                if (shorterThanMax > 0) {
                    PlaceHoldingLayoutElement placeHolder = new PlaceHoldingLayoutElement(shorterThanMax);
                    placeHolder.joinRow(rows.get(count), null);
                }
                
                count += 1;
            }
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.ReadOnlyable#readOnlyize()
     */
    public void readOnlyize() {
        for (AccountingLineViewLine line : elements) {
            line.readOnlyize();
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.ReadOnlyable#isReadOnly()
     */
    public boolean isReadOnly() {
        for (AccountingLineViewLine line : elements) {
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
        for (AccountingLineViewLine line : elements) {
            line.removeAllActionBlocks();
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#removeUnviewableBlocks(java.util.Set)
     */
    public void removeUnviewableBlocks(Set<String> unviewableBlocks) {
        Set<AccountingLineViewLine> linesToRemove = new HashSet<AccountingLineViewLine>();
        for (AccountingLineViewLine line : elements) {
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
    public void performFieldTransformations(List<AccountingLineFieldRenderingTransformation> fieldTransformations, AccountingLine accountingLine, Map editModes, Map unconvertedValues) {
        for (AccountingLineViewLine line : elements) {
            line.performFieldTransformations(fieldTransformations, accountingLine, editModes, unconvertedValues);
        }
    }
    
    /**
     * @return the maximum expected width of any of the child line elements in cells
     */
    public int getMaxExpectedLineWidth() {
        int maxWidth = 0;
        for (AccountingLineViewLine line: elements) {
            int width = line.getDisplayingFieldWidth();
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        return maxWidth;
    }
}
