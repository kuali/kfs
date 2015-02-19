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
package org.kuali.kfs.gl.batch.service.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * The reconciliation information corresponding to a whole file
 */
public class ReconciliationBlock {
    public ReconciliationBlock() {
        columns = new ArrayList<ColumnReconciliation>();
    }

    private String tableId;
    private int rowCount;
    private List<ColumnReconciliation> columns;

    /**
     * Gets the columns attribute. Do not modify the list or its contents.
     * 
     * @return Returns the columns.
     */
    public List<ColumnReconciliation> getColumns() {
        return columns;
    }

    /**
     * Adds a column reconciliation definition
     * 
     * @param column
     */
    public void addColumn(ColumnReconciliation column) {
        columns.add(column);
    }

    /**
     * Gets the rowCount attribute.
     * 
     * @return Returns the rowCount.
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * Sets the rowCount attribute value.
     * 
     * @param rowCount The rowCount to set.
     */
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * Gets the tableId attribute.
     * 
     * @return Returns the tableId.
     */
    public String getTableId() {
        return tableId;
    }

    /**
     * Sets the tableId attribute value.
     * 
     * @param tableId The tableId to set.
     */
    public void setTableId(String tableId) {
        this.tableId = tableId;
    }
}
