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
package org.kuali.kfs.sys.document.web.renderers;


public abstract class TotalRendererBase implements Renderer, CellCountCurious, RepresentedCellCurious {
    private int cellCount = 0;
    private String representedCellPropertyName;
    private int columnNumberOfRepresentedCell = 0; 

    public void clear() {
        cellCount = 0;
        columnNumberOfRepresentedCell = 0;
        representedCellPropertyName = null;
    }

    /**
     * Gets the cellCount attribute. 
     * @return Returns the cellCount.
     */
    public int getCellCount() {
        return cellCount;
    }

    /**
     * Sets the cellCount attribute value.
     * @param cellCount The cellCount to set.
     */
    public void setCellCount(int cellCount) {
        this.cellCount = cellCount;
    }

    /**
     * Gets the representedCellPropertyName attribute. 
     * @return Returns the representedCellPropertyName.
     */
    public String getRepresentedCellPropertyName() {
        return representedCellPropertyName;
    }

    /**
     * Sets the representedCellPropertyName attribute value.
     * @param representedCellPropertyName The representedCellPropertyName to set.
     */
    public void setRepresentedCellPropertyName(String representedCellPropertyName) {
        this.representedCellPropertyName = representedCellPropertyName;
    }

    /**
     * Gets the columnNumberOfRepresentedCell attribute. 
     * @return Returns the columnNumberOfRepresentedCell.
     */
    public int getColumnNumberOfRepresentedCell() {
        return columnNumberOfRepresentedCell;
    }

    /**
     * Sets the columnNumberOfRepresentedCell attribute value.
     * @param columnNumberOfRepresentedCell The columnNumberOfRepresentedCell to set.
     */
    public void setColumnNumberOfRepresentedCell(int columnNumberOfRepresentedCell) {
        this.columnNumberOfRepresentedCell = columnNumberOfRepresentedCell;
    }

}
