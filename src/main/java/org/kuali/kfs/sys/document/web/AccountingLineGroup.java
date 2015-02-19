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
