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
package org.kuali.kfs.gl.web.struts;

import java.util.Collection;
import java.util.List;

import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.ResultRow;

/**
 * This class serves as the struts action for implementing multiple value lookups
 */
public interface LookupDisplayTagSurrogate {

    /**
     * This method performs the lookup and returns a collection of lookup items. Also initializes values in the form that will allow
     * the multiple value lookup page to render
     * 
     * @param selectable <code>{@link LookupResultsSelectable}</code> since checkboxes are used to select multiple things
     * @param form an instance of <code>{@link LookupForm}</code>
     * @param resultTable a list of result rows (used to generate what's shown in the UI). This list will be modified by this method
     * @param maxRowsPerPage
     * @param bounded whether the results will be bounded
     * @return the list of result BOs, possibly qbounded by size
     */
    public Collection performMultipleValueLookup(LookupResultsSelectable selectable, LookupForm form, List<ResultRow> resultTable, boolean bounded);

    /**
     * This method performs the operations necessary for a multiple value lookup to switch to another page of results and rerender
     * the page
     * 
     * @param multipleValueLookupForm
     * @param maxRowsPerPage
     * @return a list of result rows, used by the UI to render the page
     */
    public List<ResultRow> switchToPage(LookupResultsSelectable selectable, int maxRowsPerPage);

    /**
     * This method performs the operations necessary for a multiple value lookup to sort results and rerender the page
     * 
     * @param multipleValueLookupForm
     * @param maxRowsPerPage
     * @return a list of result rows, used by the UI to render the page
     */
    public List<ResultRow> sort(LookupResultsSelectable selectable, int maxRowsPerPage);

    /**
     * This method performs the operations necessary for a multiple value lookup keep track of which results have been selected to
     * be returned to the calling document. Note, this method does not actually requery for the results.
     * 
     * @param multipleValueLookupForm
     */
    public void prepareToReturnSelectedResultBOs(LookupResultsSelectable selectable);

    /**
     * This method performs the operations necessary for a multiple value lookup to return no results to the calling page
     * 
     * @param multipleValueLookupForm
     */
    public void prepareToReturnNone(LookupResultsSelectable selectable);

    /**
     * This method performs the operations necessary for a multiple value lookup to export the rows via display tag Note: this
     * method assumes that the export will be opened in a new browser window, therefore, persisting the selected checkboxes will not
     * be needed.
     * 
     * @param multipleValueLookupForm
     * @return a list of result rows, to be used by display tag to render the results
     */
    public List<ResultRow> prepareToExport(LookupResultsSelectable selectable);


    /**
     * This method performs the operations necessary for a multiple value lookup to select all of the results and rerender the page
     * 
     * @param multipleValueLookupForm
     * @param maxRowsPerPage
     * @return a list of result rows, used by the UI to render the page
     */
    public List<ResultRow> selectAll(LookupResultsSelectable selectable, int maxRowsPerPage);

    /**
     * This method performs the operations necessary for a multiple value lookup to unselect all of the results and rerender the
     * page
     * 
     * @param multipleValueLookupForm
     * @param maxRowsPerPage
     * @return a list of result rows, used by the UI to render the page
     */
    public List<ResultRow> unselectAll(LookupResultsSelectable selectable, int maxRowsPerPage);

    /**
     * This method computes the max number of rows that should be rendered per page for a multiple value lookup. This method first
     * looks for an application parameter in FS_PARM_T, group SYSTEM, multipleValueLookupResultsPerPage if someone wants to
     * implement something where a user can decide how many results to display per page, this method is the place to do it. Make
     * this method read form values to determine the max rows per page based on the user inputs
     * 
     * @see org.kuali.kfs.sys.KFSConstants.SystemGroupParameterNames#MULTIPLE_VALUE_LOOKUP_RESULTS_PER_PAGE
     * @see #DEFAULT_MAX_ROWS_PER_PAGE
     * @param multipleValueLookupForm the form
     * @return
     */
    public int getMaxRowsPerPage(LookupResultsSelectable selectable);
}
