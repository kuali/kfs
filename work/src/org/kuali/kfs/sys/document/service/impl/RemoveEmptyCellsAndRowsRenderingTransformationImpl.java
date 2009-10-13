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
package org.kuali.kfs.sys.document.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.sys.document.service.AccountingLineTableTransformation;
import org.kuali.kfs.sys.document.web.AccountingLineTableCell;
import org.kuali.kfs.sys.document.web.AccountingLineTableRow;

/**
 * Rendering transformation which removes any empty cells and rows from a table
 */
public class RemoveEmptyCellsAndRowsRenderingTransformationImpl implements AccountingLineTableTransformation {

    /**
     * Removes any empty cells and rows from this table
     * @param rows the table rows to render
     */
    public void transformRows(List<AccountingLineTableRow> rows) {
        Set<AccountingLineTableRow> rowsToRemove = new HashSet<AccountingLineTableRow>();
        for (AccountingLineTableRow row : rows) {
            removeEmptyCells(row);
            if (row.isEmpty()) {
                rowsToRemove.add(row);
            }
        }
        rows.removeAll(rowsToRemove);
    }
    
    /**
     * Removes all empty cells from a given row
     * @param row the row to remove empty cells from
     */
    public void removeEmptyCells(AccountingLineTableRow row) {
        Set<AccountingLineTableCell> cellsToRemove = new HashSet<AccountingLineTableCell>();
        for (AccountingLineTableCell cell : row.getCells()) {
            if (cell.isEmpty()) {
                cellsToRemove.add(cell);
            }
        }
        row.getCells().removeAll(cellsToRemove);
    }
}
