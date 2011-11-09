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
import org.kuali.kfs.sys.document.datadictionary.AccountingLineViewLineDefinition;
import org.kuali.kfs.sys.document.service.AccountingLineFieldRenderingTransformation;

/**
 * Represents a single table row to be rendered as part of an accounting line view.
 */
public class AccountingLineViewLine implements ReadOnlyable, AccountingLineViewLineFillingElement {
    private List<RenderableElement> elements;
    private AccountingLineViewLineDefinition definition;
 
    /**
     * Gets the definition attribute. 
     * @return Returns the definition.
     */
    public AccountingLineViewLineDefinition getDefinition() {
        return definition;
    }
    /**
     * Sets the definition attribute value.
     * @param definition The definition to set.
     */
    public void setDefinition(AccountingLineViewLineDefinition definition) {
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
    public List<RenderableElement> getElements() {
        return elements;
    }
    /**
     * Sets the elements attribute value.
     * @param elements The elements to set.
     */
    public void setElements(List<RenderableElement> fields) {
        this.elements = fields;
    }
    
    /**
     * Gets the number of actual rows requested (1)
     * @see org.kuali.kfs.sys.document.web.AccountingLineViewRenderableElement#getRequestedRowCount()
     */
    public int getRequestedRowCount() {
        return getMaxRequestedRowCount();
    }
    
    /**
     * Determines the max requested row count in the line. 
     * @return the number of rows to create for this line
     */
    protected int getMaxRequestedRowCount() {
        for (RenderableElement element : elements) {
            if (element instanceof TableJoiningWithHeader && !element.isHidden()) {
                return 2;
            }
        }
        return 1;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#joinRow(org.kuali.kfs.sys.document.web.AccountingLineTableRow)
     */
    public void joinRow(AccountingLineTableRow headerRow, AccountingLineTableRow row) {
       for (RenderableElement element : elements) {
           if (element instanceof TableJoining) {
               ((TableJoining)element).joinRow(headerRow, row);
           } else {
               headerRow.addCell(createTableCellForNonTableJoining(element));
           }
       }
    }
    
    /**
     * Creates a table cell to wrap the given rendering element
     * @param element the element to wrap
     * @return a table cell wrapping that element
     */
    protected AccountingLineTableCell createTableCellForNonTableJoining(RenderableElement element) {
        AccountingLineTableCell cell = new AccountingLineTableCell();
        cell.addRenderableElement(element);
        cell.setRowSpan(getRequestedRowCount());
        return cell;
    }
    
    /**
     * Always throws an exception - a line may only join a table through a parent lines element
     * @see org.kuali.kfs.sys.document.web.TableJoining#joinTable(java.util.List)
     */
    public void joinTable(List<AccountingLineTableRow> rows) {
        throw new IllegalStateException("Line elements may not join a table directly; the specified rendering is incorrect");
    }
    
    /**
     * This element should be padded out
     * @see org.kuali.kfs.sys.document.web.AccountingLineViewLineFillingElement#stretchToFillLine()
     */
    public boolean shouldStretchToFillLine() {
        return false;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#readOnlyize()
     */
    public void readOnlyize() {
        for (RenderableElement element : elements) {
            if (element instanceof ReadOnlyable) {
                ((ReadOnlyable)element).readOnlyize();
            }
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.ReadOnlyable#isReadOnly()
     */
    public boolean isReadOnly() {
        for (RenderableElement element : elements) {
            if (element instanceof ReadOnlyable && !((ReadOnlyable)element).isReadOnly()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#removeAllActionBlocks()
     */
    public void removeAllActionBlocks() {
        Set<RenderableElement> actionBlocksToRemove = new HashSet<RenderableElement>();
        for (RenderableElement element : elements) {
            if (element.isActionBlock()) {
                actionBlocksToRemove.add(element);
            }
        }
        elements.removeAll(actionBlocksToRemove);
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#removeUnviewableBlocks()
     */
    public void removeUnviewableBlocks(Set<String> unviewableBlocks) {
        Set<RenderableElement> elementsToRemove = new HashSet<RenderableElement>();
        for (RenderableElement element : elements) {
            if (element instanceof TableJoining) {
                if (unviewableBlocks.contains(((TableJoining)element).getName())) {
                    elementsToRemove.add(element);
                } else {
                    ((TableJoining)element).removeUnviewableBlocks(unviewableBlocks);
                }
            }
        }
        elements.removeAll(elementsToRemove);
    }
    
    /**
     * Shuffles responsibility on to any TableJoining children
     * @see org.kuali.kfs.sys.document.web.TableJoining#readOnlyizeReadOnlyBlocks(java.util.Set)
     */
    public void readOnlyizeReadOnlyBlocks(Set<String> readOnlyBlocks) {
        for (RenderableElement element : elements) {
            if (element instanceof TableJoining) {
                ((TableJoining)element).readOnlyizeReadOnlyBlocks(readOnlyBlocks);
            }
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#performFieldTransformation(org.kuali.kfs.sys.document.service.AccountingLineFieldRenderingTransformation, org.kuali.kfs.sys.businessobject.AccountingLine, java.util.Map, java.util.Map)
     */
    public void performFieldTransformations(List<AccountingLineFieldRenderingTransformation> fieldTransformations, AccountingLine accountingLine, Map unconvertedValues) {
        for (RenderableElement element : elements) {
            if (element instanceof TableJoining) {
                ((TableJoining)element).performFieldTransformations(fieldTransformations, accountingLine, unconvertedValues);
            }
        }
    }
    
    /**
     * Finds the number of table cells this line expects to take up
     * @return the number of displayed table cells this line expects to render as
     */
    public int getDisplayingFieldWidth() {
        int count = 0;
        for (RenderableElement element : elements) {
            if (!element.isHidden()) {
                if (element instanceof AccountingLineViewField && ((AccountingLineViewField)element).getColSpanOverride() > 1) {
                    count += ((AccountingLineViewField)element).getColSpanOverride();
                } else {
                    count += 1;
                }
            }
        }
        return count;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.ReadOnlyable#setEditable()
     */
    public void setEditable() {
        for (RenderableElement element : elements) {
            if (element instanceof ReadOnlyable) {
                ((ReadOnlyable)element).setEditable();
            }
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#setEditableBlocks(java.util.Set)
     */
    public void setEditableBlocks(Set<String> editableBlocks) {
        for (RenderableElement element : elements) {
            if (element instanceof TableJoining) {
                ((TableJoining)element).setEditableBlocks(editableBlocks);
            }
        }
    }
}
