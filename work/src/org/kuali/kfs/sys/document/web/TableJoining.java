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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.core.web.ui.Field;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.service.AccountingLineFieldRenderingTransformation;

/**
 * An interface which specifies the behaviors needed from layout elements to join tables
 */
public interface TableJoining {
    
    /**
     * Requests that this layout element property join a number of rows which will make up a table 
     * @param rows the rows to join
     */
    public abstract void joinTable(List<AccountingLineTableRow> rows);
    
    /**
     * Requests that this element join a table row
     * @param row the row to join
     */
    public abstract void joinRow(AccountingLineTableRow headerLabelRow, AccountingLineTableRow row);
    
    /**
     * The minimum number of rows this element needs if it is going to join a table
     * @return the minimum number of rows
     */
    public abstract int getRequestedRowCount();
    
    /**
     * Returns the name of this table joining element
     * @return the name of this table joining element
     */
    public abstract String getName();
    
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
     * Performs a transformations on any fields this TableJoining layout element knows about
     * @param fieldTransformation a List of field transformations to perform on this element
     * @param accountingLine the accounting line which is being rendering during the transformation
     * @param editModes the edit modes of the document currently
     * @param unconvertedValues any unconverted values from the form
     */
    public abstract void performFieldTransformations(List<AccountingLineFieldRenderingTransformation> fieldTransformations, AccountingLine accountingLine, Map editModes, Map unconvertedValues);
}
