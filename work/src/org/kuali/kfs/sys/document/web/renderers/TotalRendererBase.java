/*
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
