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
