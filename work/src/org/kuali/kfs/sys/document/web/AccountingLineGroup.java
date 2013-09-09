/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.sys.document.web;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.Tag;


public interface AccountingLineGroup {

    /**
     * Renders the whole of this accounting line group
     *
     * @param pageContext the page context to render to
     * @param parentTag the AccountingLinesTag that is requesting this rendering
     */
    public abstract void renderEverything(PageContext pageContext, Tag parentTag) throws JspException;

    /**
     * Determines if the totals for the accounting line group should be rendered
     * @return true if the totals should be rendered, false otherwise
     */
    public abstract boolean shouldRenderTotals();

    /**
     * Finds the maximum number of cells in the accounting line table row
     *
     * @param rows the rows which are being rendered
     * @return the maximum number of cells to render
     */
    public abstract int getWidthInCells();

    /**
     * Sets the cellCount attribute value.
     *
     * @param cellCount The cellCount to set.
     */
    public abstract void setCellCount(int cellCount);

    /**
     * Sets the importLineOverride attribute value.
     *
     * @param importLineOverride The importLineOverride to set.
     */
    public abstract void setImportLineOverride(JspFragment importLineOverride);

    /**
     * Sets the form's arbitrarily high tab index
     *
     * @param arbitrarilyHighIndex the index to set
     */
    public abstract void setArbitrarilyHighIndex(int arbitrarilyHighIndex);

    /**
     * Gets the errors attribute.
     *
     * @return Returns the errors.
     */
    public abstract List getErrorKeys();

    /**
     * Sets the errors attribute value.
     *
     * @param errors The errors to set.
     */
    public abstract void setErrorKeys(List errors);

    /**
     * Determines if there is more than one editable line in this group; if so, then it allows deleting
     */
    public abstract void updateDeletabilityOfAllLines();

    /**
     * Gets the collectionItemPropertyName attribute.
     * @return Returns the collectionItemPropertyName.
     */
    public abstract String getCollectionItemPropertyName();

}
