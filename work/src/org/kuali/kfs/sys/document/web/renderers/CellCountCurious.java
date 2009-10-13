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

/**
 * A contract for those Renders which are curious about how many cells the total width of the table is
 */
public interface CellCountCurious {
    
    /**
     * This method lets the object demanding the rendering to set the total number of cells that the
     * table will be rendered as 
     * @param cellCount the width, measured in table cells, of the accounting line table
     */
    public abstract void setCellCount(int cellCount);
}
