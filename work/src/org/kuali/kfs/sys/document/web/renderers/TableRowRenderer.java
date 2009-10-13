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

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.kuali.kfs.sys.document.web.AccountingLineTableRow;

/**
 * Renders a row within a table
 */
public class TableRowRenderer implements Renderer {
    private AccountingLineTableRow row;

    /**
     * Resets the table row.
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#clear()
     */
    public void clear() {
        row = null;
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            if (row.getChildCellCount() > 0) {
                out.write(buildBeginningRowTag());
                row.renderChildrenCells(pageContext, parentTag);
                out.write(buildEndingRowTag());
            }
        }
        catch (IOException ioe) {
            throw new JspException("Could not render table row", ioe);
        }
    }
    
    /**
     * 
     * @return
     */
    protected String buildBeginningRowTag() {
        return "<tr>";
    }
    
    /**
     * 
     * @return
     */
    protected String buildEndingRowTag() {
        return "</tr>";
    }

    /**
     * Gets the row attribute. 
     * @return Returns the row.
     */
    public AccountingLineTableRow getRow() {
        return row;
    }

    /**
     * Sets the row attribute value.
     * @param row The row to set.
     */
    public void setRow(AccountingLineTableRow row) {
        this.row = row;
    }
}
