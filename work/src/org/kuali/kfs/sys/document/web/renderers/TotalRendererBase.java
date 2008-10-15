/*
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
package org.kuali.kfs.sys.document.web.renderers;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

public abstract class TotalRendererBase implements Renderer, CellCountCurious {
    private int cellCount = 0;

    public void clear() {
        cellCount = 0;
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
}
