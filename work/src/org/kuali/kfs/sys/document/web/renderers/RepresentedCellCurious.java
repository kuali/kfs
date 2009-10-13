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

public interface RepresentedCellCurious {
    
    /**
     * set the property name of the represented cell in the the accounting line table
     * 
     * @param representedCellPropertyName the given property name of the represented cell in the the accounting line table
     */
    public abstract void setRepresentedCellPropertyName(String representedCellPropertyName);
    
    /**
     * get the property name of the represented cell in the the accounting line table
     */
    public abstract String getRepresentedCellPropertyName();

    /**
     * set the column number of the represented cell in the the accounting line table
     * 
     * @param columnNumberOfRepresentedCell the given column number of the represented cell in the the accounting line table
     */
    public abstract void setColumnNumberOfRepresentedCell(int columnNumberOfRepresentedCell);
    
    /**
     * get the column number of the represented cell in the the accounting line table 
     */
    public abstract int getColumnNumberOfRepresentedCell();
}
