/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.gl.web.struts.action;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.service.SequenceAccessorService;
import org.kuali.core.web.struts.form.LookupForm;
import org.kuali.module.gl.web.struts.form.LookupResultsSelectable;
import org.kuali.core.web.ui.ResultRow;

/**
 * This class serves as the struts action for implementing multiple value lookups
 */
public interface LookupDisplayTagSurrogate {    
    
    /**
     * This method performs the lookup and returns a collection of lookup items.  Also initializes values in the form
     * that will allow the multiple value lookup page to render
     * 
     * @param selectable <code>{@link LookupResultsSelectable}</code> since checkboxes are used to select multiple things
     * @param form an instance of <code>{@link LookupForm}</code>
     * @param resultTable a list of result rows (used to generate what's shown in the UI).  This list will be modified by this method
     * @param maxRowsPerPage
     * @param bounded whether the results will be bounded
     * @return the list of result BOs, possibly qbounded by size
     */
    public Collection performMultipleValueLookup(LookupResultsSelectable selectable, LookupForm form, List<ResultRow> resultTable, boolean bounded);
    
    /**
     * This method performs the operations necessary for a multiple value lookup to switch to another page of results and rerender the page
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
     * This method performs the operations necessary for a multiple value lookup keep track of which results have been selected to be returned
     * to the calling document.  Note, this method does not actually requery for the results.
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
     * This method performs the operations necessary for a multiple value lookup to export the rows via display tag
     * 
     * Note: this method assumes that the export will be opened in a new browser window, therefore, persisting the selected
     * checkboxes will not be needed.
     * 
     * @param multipleValueLookupForm
     * @return a list of result rows, to be used by display tag to render the results
     */
    public List<ResultRow> prepareToExport(LookupResultsSelectable selectable);
    
    
    /**
     * This method performs the operations necessary for a multiple value lookup to select all of the results and rerender the page
     * @param multipleValueLookupForm
     * @param maxRowsPerPage
     * @return a list of result rows, used by the UI to render the page
     */
    public List<ResultRow> selectAll(LookupResultsSelectable selectable, int maxRowsPerPage);
    
    /**
     * This method performs the operations necessary for a multiple value lookup to unselect all of the results and rerender the page
     * @param multipleValueLookupForm
     * @param maxRowsPerPage
     * @return a list of result rows, used by the UI to render the page
     */
    public List<ResultRow> unselectAll(LookupResultsSelectable selectable, int maxRowsPerPage);
    
    /**
     * This method computes the max number of rows that should be rendered per page for a multiple value lookup.
     * 
     * This method first looks for an application parameter in FS_PARM_T, group SYSTEM, multipleValueLookupResultsPerPage
     * 
     * if someone wants to implement something where a user can decide how many results to display per page, 
     * this method is the place to do it.  Make this method read form values to determine the max rows per page based on the user inputs
     * 
     * @see org.kuali.kfs.KFSConstants.SystemGroupParameterNames#MULTIPLE_VALUE_LOOKUP_RESULTS_PER_PAGE
     * @see #DEFAULT_MAX_ROWS_PER_PAGE
     * @param multipleValueLookupForm the form
     * @return
     */
    public int getMaxRowsPerPage(LookupResultsSelectable selectable);
}
