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
 * An interface which specifies the behaviors needed from layout elements to join tables
 */
public interface TableJoining extends ElementNamable{
    
    /**
     * Requests that this layout element property join a number of rows which will make up a table 
     * @param rows the rows to join
     * @param headerRowCount the number of header rows
     */
    public abstract void joinTable(List<AccountingLineTableRow> rows);
    
    /**
     * Requests that this element join a table row
     * @param headerLabelRow the header row which can be joined
     * @param row the row which can be joined
     */
    public abstract void joinRow(AccountingLineTableRow headerLabelRow, AccountingLineTableRow row);
    
    /**
     * The minimum number of rows this element needs if it is going to join a table
     * @return the minimum number of rows
     */
    public abstract int getRequestedRowCount();
    
    /**
     * Removes any action blocks from the given element
     */
    public abstract void removeAllActionBlocks();
    
    /**
     * Removes any unviewable blocks within this this table joining element
     * @param unviewableBlocks a Set of the names of blocks that should not be rendered
     */
    public abstract void removeUnviewableBlocks(Set<String> unviewableBlocks);
    
    /**
     * Instructs the element to make any child readOnlyizable blocks named within the given Set to read only
     * @param readOnlyBlocks the names of blocks to make read only
     */
    public abstract void readOnlyizeReadOnlyBlocks(Set<String> readOnlyBlocks);
    
    /**
     * Instructs the element to make any child readOnlyizable blocks named within the given Set to read only
     * @param readOnlyBlocks the names of blocks to make read only
     */
    public abstract void setEditableBlocks(Set<String> editableBlocks);

    /**
     * Performs a transformations on any fields this TableJoining layout element knows about
     * @param accountingDocument the document the field of the accounting line is associated with
     * @param fieldTransformation a List of field transformations to perform on this element
     * @param accountingLine the accounting line which is being rendering during the transformation
     * @param unconvertedValues any unconverted values from the form
     */
    public abstract void performFieldTransformations(List<AccountingLineFieldRenderingTransformation> fieldTransformations, AccountingLine accountingLine, Map unconvertedValues);
}
