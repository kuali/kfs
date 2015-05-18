/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.web.struts.action;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.LookupResultsService;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.web.struts.form.MultipleValueLookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * This class serves as the struts action for implementing multiple value lookups
 */
public class KualiMultipleValueLookupAction extends KualiLookupAction implements KualiTableRenderAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiMultipleValueLookupAction.class);

    /**
     * If there is no app param defined for the # rows/page, then this value
     * will be used for the default
     * 
     * @see KualiMultipleValueLookupAction#getMaxRowsPerPage(MultipleValueLookupForm)
     */
    public static final int DEFAULT_MAX_ROWS_PER_PAGE = 50;


    /**
     * This method performs the search, and will be responsible for persisting the results via the LookupResultsService.
     * This overrides the superclass's search action method b/c of the differences in how the results are generated and it populates
     * certain attributes that are specific to MultipleValueLookupForm
     * 
     * @param mapping
     * @param form must be an instance of MultipleValueLookupForm
     * @param request
     * @param response 
     */
    @Override
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MultipleValueLookupForm multipleValueLookupForm = (MultipleValueLookupForm) form;

        // If this is a new search, clear out the old search results.
        String methodToCall = findMethodToCall(form, request);
        if (methodToCall.equalsIgnoreCase("search")) {
            GlobalVariables.getUserSession().removeObjectsByPrefix(KRADConstants.SEARCH_METHOD);
        }

        Lookupable kualiLookupable = multipleValueLookupForm.getLookupable();
        if (kualiLookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        Collection displayList = new ArrayList();
        ArrayList<ResultRow> resultTable = new ArrayList<ResultRow>();

        // validate search parameters
        kualiLookupable.validateSearchParameters(multipleValueLookupForm.getFields());

        boolean bounded = true;

        displayList = performMultipleValueLookup(multipleValueLookupForm, resultTable, getMaxRowsPerPage(multipleValueLookupForm), bounded);
        if (kualiLookupable.isSearchUsingOnlyPrimaryKeyValues()) {
            multipleValueLookupForm.setSearchUsingOnlyPrimaryKeyValues(true);
            multipleValueLookupForm.setPrimaryKeyFieldLabels(kualiLookupable.getPrimaryKeyFieldLabels());
        }
        else {
            multipleValueLookupForm.setSearchUsingOnlyPrimaryKeyValues(false);
            multipleValueLookupForm.setPrimaryKeyFieldLabels(KRADConstants.EMPTY_STRING);
        }

        //request.setAttribute("reqSearchResultsActualSize", ((CollectionIncomplete) displayList).getActualSizeIfTruncated());

        if ( displayList instanceof CollectionIncomplete ){
            request.setAttribute("reqSearchResultsActualSize", ((CollectionIncomplete) displayList).getActualSizeIfTruncated());
        } else {
            request.setAttribute("reqSearchResultsActualSize", displayList.size() );
        }

        request.setAttribute("reqSearchResults", resultTable);

        //multipleValueLookupForm.setResultsActualSize((int) ((CollectionIncomplete) displayList).getActualSizeIfTruncated().longValue());

        if ( displayList instanceof CollectionIncomplete ){
            multipleValueLookupForm.setResultsActualSize((int) ((CollectionIncomplete) displayList).getActualSizeIfTruncated().longValue());
        } else {
            multipleValueLookupForm.setResultsActualSize(displayList.size());
        }


        multipleValueLookupForm.setResultsLimitedSize(resultTable.size());

        if (request.getParameter(KRADConstants.SEARCH_LIST_REQUEST_KEY) != null) {
            GlobalVariables.getUserSession().removeObject(request.getParameter(KRADConstants.SEARCH_LIST_REQUEST_KEY));
        }
        request.setAttribute(KRADConstants.SEARCH_LIST_REQUEST_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(resultTable, KRADConstants.SEARCH_LIST_KEY_PREFIX));

        request.getParameter(KRADConstants.REFRESH_CALLER);

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * This method switches to another page on a multi-value lookup
     * 
     * @param mapping
     * @param form must be an instance of MultipleValueLookupForm
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public ActionForward switchToPage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MultipleValueLookupForm multipleValueLookupForm = (MultipleValueLookupForm) form;
        List<ResultRow> resultTable = switchToPage(multipleValueLookupForm, getMaxRowsPerPage(multipleValueLookupForm));
        request.setAttribute("reqSearchResults", resultTable);
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * This method sorts a column.  If the page is currently sorted on a certain column,
     * and the same column is selected to be sorted again, then the results will be 
     * reversed.  After the search method is called, it is difficult to determine the sort
     * order of the result table, so no column is considered sorted.  So, after a search, we were
     * to click sort on an already sorted column, it would appear to have no effect.  Subsequent clicks
     * would tell you  
     * 
     * @param mapping
     * @param form must be an instance of MultipleValueLookupForm
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public ActionForward sort(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MultipleValueLookupForm multipleValueLookupForm = (MultipleValueLookupForm) form;
        List<ResultRow> resultTable = sort(multipleValueLookupForm, getMaxRowsPerPage(multipleValueLookupForm));
        request.setAttribute("reqSearchResults", resultTable);
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * This method does the processing necessary to return selected results and sends a redirect back to the lookup caller
     * 
     * @param mapping
     * @param form must be an instance of MultipleValueLookupForm
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward prepareToReturnSelectedResults(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MultipleValueLookupForm multipleValueLookupForm = (MultipleValueLookupForm) form;
        if (StringUtils.isBlank(multipleValueLookupForm.getLookupResultsSequenceNumber())) {
            // no search was executed
            return prepareToReturnNone(mapping, form, request, response);
        }

        prepareToReturnSelectedResultBOs(multipleValueLookupForm);

        // build the parameters for the refresh url
        Properties parameters = new Properties();
        parameters.put(KRADConstants.LOOKUP_RESULTS_BO_CLASS_NAME, multipleValueLookupForm.getBusinessObjectClassName());
        parameters.put(KRADConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER, multipleValueLookupForm.getLookupResultsSequenceNumber());
        parameters.put(KRADConstants.DOC_FORM_KEY, multipleValueLookupForm.getFormKey());
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.RETURN_METHOD_TO_CALL);
        parameters.put(KRADConstants.REFRESH_CALLER, KRADConstants.MULTIPLE_VALUE);
        parameters.put(KRADConstants.ANCHOR, multipleValueLookupForm.getLookupAnchor());
        parameters.put(KRADConstants.LOOKED_UP_COLLECTION_NAME, multipleValueLookupForm.getLookedUpCollectionName());
        if(multipleValueLookupForm.getDocNum() != null){
            parameters.put(KRADConstants.DOC_NUM, multipleValueLookupForm.getDocNum());
        }


        String backUrl = UrlFactory.parameterizeUrl(multipleValueLookupForm.getBackLocation(), parameters);
        return new ActionForward(backUrl, true);
    }

    /**
     * This method selects all results across all pages
     * @param mapping
     * @param form must be an instance of MultipleValueLookupForm
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward selectAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MultipleValueLookupForm multipleValueLookupForm = (MultipleValueLookupForm) form;
        List<ResultRow> resultTable = selectAll(multipleValueLookupForm, getMaxRowsPerPage(multipleValueLookupForm));
        request.setAttribute("reqSearchResults", resultTable);
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * This method unselects all results across all pages
     * 
     * @param mapping
     * @param form must be an instance of MultipleValueLookupForm
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward unselectAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MultipleValueLookupForm multipleValueLookupForm = (MultipleValueLookupForm) form;
        List<ResultRow> resultTable = unselectAll(multipleValueLookupForm, getMaxRowsPerPage(multipleValueLookupForm));
        request.setAttribute("reqSearchResults", resultTable);
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * This method overrides the super class cancel method because it is basically equivalent to clicking prepare to return none 
     * 
     * @see KualiLookupAction#cancel(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)
     */
    @Override
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return prepareToReturnNone(mapping, form, request, response);
    }


    /**
     * This method returns none of the selected results and redirects back to the lookup caller.
     * @param mapping
     * @param form must be an instance of MultipleValueLookupForm
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward prepareToReturnNone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MultipleValueLookupForm multipleValueLookupForm = (MultipleValueLookupForm) form;
        prepareToReturnNone(multipleValueLookupForm);

        // build the parameters for the refresh url
        Properties parameters = new Properties();
        parameters.put(KRADConstants.DOC_FORM_KEY, multipleValueLookupForm.getFormKey());
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.RETURN_METHOD_TO_CALL);
        parameters.put(KRADConstants.REFRESH_CALLER, KRADConstants.MULTIPLE_VALUE);
        parameters.put(KRADConstants.ANCHOR, multipleValueLookupForm.getLookupAnchor());
        if(multipleValueLookupForm.getDocNum() != null){
            parameters.put(KRADConstants.DOC_NUM, multipleValueLookupForm.getDocNum());
        }
        String backUrl = UrlFactory.parameterizeUrl(multipleValueLookupForm.getBackLocation(), parameters);
        return new ActionForward(backUrl, true);
    }

    /**
     * This method prepares to export results.  Note: this method will not look for any rows selected since the last page view, so it is best
     * that exporting opens in a new browser window.
     * 
     * @param mapping
     * @param form must be an instance of MultipleValueLookupForm
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MultipleValueLookupForm multipleValueLookupForm = (MultipleValueLookupForm) form;
        List<ResultRow> resultTable = prepareToExport(multipleValueLookupForm);
        request.setAttribute("reqSearchResults", resultTable);
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * This method performs the lookup and returns a collection of lookup items.  Also initializes values in the form
     * that will allow the multiple value lookup page to render
     * 
     * @param multipleValueLookupForm
     * @param resultTable a list of result rows (used to generate what's shown in the UI).  This list will be modified by this method
     * @param maxRowsPerPage
     * @param bounded whether the results will be bounded
     * @return the list of result BOs, possibly bounded by size
     */
    protected Collection performMultipleValueLookup(MultipleValueLookupForm multipleValueLookupForm, List<ResultRow> resultTable, int maxRowsPerPage, boolean bounded) {
        Lookupable lookupable = multipleValueLookupForm.getLookupable();
        Collection displayList = lookupable.performLookup(multipleValueLookupForm, resultTable, bounded);

        List defaultSortColumns = lookupable.getDefaultSortColumns();
        if (defaultSortColumns != null && !defaultSortColumns.isEmpty() && resultTable != null && !resultTable.isEmpty()) {
            // there's a default sort order, just find the first sort column, and we can't go wrong
            String firstSortColumn = (String) defaultSortColumns.get(0);

            // go thru the first result row to find the index of the column (more efficient than calling lookupable.getColumns since we don't have to recreate column list)
            int firstSortColumnIdx = -1;
            List<Column> columnsForFirstResultRow = resultTable.get(0).getColumns();
            for (int i = 0; i < columnsForFirstResultRow.size(); i++) {
                if (StringUtils.equals(firstSortColumn, columnsForFirstResultRow.get(i).getPropertyName())) {
                    firstSortColumnIdx = i;
                    break;
                }
            }
            multipleValueLookupForm.setColumnToSortIndex(firstSortColumnIdx);
        }
        else {
            // don't know how results were sorted, so we just say -1
            multipleValueLookupForm.setColumnToSortIndex(-1);
        }

        // we just performed the lookup, so we're on the first page (indexed from 0)
        multipleValueLookupForm.jumpToFirstPage(resultTable.size(), maxRowsPerPage);

        SequenceAccessorService sas = KRADServiceLocator.getSequenceAccessorService();
        Long nextSeq = sas.getNextAvailableSequenceNumber(KRADConstants.LOOKUP_RESULTS_SEQUENCE);
        String lookupResultsSequenceNumber = nextSeq.toString();
        multipleValueLookupForm.setLookupResultsSequenceNumber(lookupResultsSequenceNumber);
        try {
            LookupResultsService lookupResultsService = KNSServiceLocator.getLookupResultsService();
            lookupResultsService.persistResultsTable(lookupResultsSequenceNumber, resultTable,
                    GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }
        catch (Exception e) {
            LOG.error("error occured trying to persist multiple lookup results", e);
            throw new RuntimeException("error occured trying to persist multiple lookup results");
        }

        // since new search, nothing's checked
        multipleValueLookupForm.setCompositeObjectIdMap(new HashMap<String, String>());

        return displayList;
    }

    /**
     * This method performs the operations necessary for a multiple value lookup to switch to another page of results and rerender the page
     * @param multipleValueLookupForm
     * @param maxRowsPerPage
     * @return a list of result rows, used by the UI to render the page
     */
    protected List<ResultRow> switchToPage(MultipleValueLookupForm multipleValueLookupForm, int maxRowsPerPage) {
        String lookupResultsSequenceNumber = multipleValueLookupForm.getLookupResultsSequenceNumber();

        List<ResultRow> resultTable = null;
        try {
            resultTable = KNSServiceLocator.getLookupResultsService().retrieveResultsTable(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }
        catch (Exception e) {
            LOG.error("error occured trying to retrieve multiple lookup results", e);
            throw new RuntimeException("error occured trying to retrieve multiple lookup results");
        }

        multipleValueLookupForm.jumpToPage(multipleValueLookupForm.getSwitchToPageNumber(), resultTable.size(), maxRowsPerPage);

        multipleValueLookupForm.setColumnToSortIndex(Integer.parseInt(multipleValueLookupForm.getPreviouslySortedColumnIndex()));
        multipleValueLookupForm.setCompositeObjectIdMap(LookupUtils.generateCompositeSelectedObjectIds(multipleValueLookupForm.getPreviouslySelectedObjectIdSet(),
                multipleValueLookupForm.getDisplayedObjectIdSet(), multipleValueLookupForm.getSelectedObjectIdSet()));
        return resultTable;
    }

    /**
     * This method performs the operations necessary for a multiple value lookup to sort results and rerender the page
     * 
     * @param multipleValueLookupForm
     * @param maxRowsPerPage
     * @return a list of result rows, used by the UI to render the page
     */
    protected List<ResultRow> sort(MultipleValueLookupForm multipleValueLookupForm, int maxRowsPerPage) {
        String lookupResultsSequenceNumber = multipleValueLookupForm.getLookupResultsSequenceNumber();

        LookupResultsService lookupResultsService = KNSServiceLocator.getLookupResultsService();

        List<ResultRow> resultTable = null;
        try {
            resultTable = lookupResultsService.retrieveResultsTable(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }
        catch (Exception e) {
            LOG.error("error occured trying to retrieve multiple lookup results", e);
            throw new RuntimeException("error occured trying to retrieve multiple lookup results");
        }

        int columnToSortOn = multipleValueLookupForm.getColumnToSortIndex();
        int columnCurrentlySortedOn = Integer.parseInt(multipleValueLookupForm.getPreviouslySortedColumnIndex());

        // if columnCurrentlySortedOn is -1, that means that we don't know which column we were originally sorting on
        // after a search, it's hard to tell which of the columns we're sorted on,

        if (columnToSortOn == columnCurrentlySortedOn) {
            // we're already sorted on the same column that the user clicked on, so we reverse the list
            Collections.reverse(resultTable);
        }
        else {
            // sorting on a different column, so we have to sort

            // HACK ALERT for findBestValueComparatorForColumn, since there's no central place to know
            // which comparator we should use to compare values in a column
            Collections.sort(resultTable, new BeanComparator("columns[" + columnToSortOn + "].propertyValue", LookupUtils.findBestValueComparatorForColumn(resultTable, columnToSortOn)));
        }

        // repersist the list
        try {
            lookupResultsService.persistResultsTable(lookupResultsSequenceNumber, resultTable, 
                    GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }
        catch (Exception e) {
            LOG.error("error occured trying to persist multiple lookup results", e);
            throw new RuntimeException("error occured trying to persist multiple lookup results");
        }

        // we just performed the sort, so go back to first page
        multipleValueLookupForm.jumpToFirstPage(resultTable.size(), maxRowsPerPage);

        multipleValueLookupForm.setCompositeObjectIdMap(LookupUtils.generateCompositeSelectedObjectIds(multipleValueLookupForm.getPreviouslySelectedObjectIdSet(),
                multipleValueLookupForm.getDisplayedObjectIdSet(), multipleValueLookupForm.getSelectedObjectIdSet()));
        return resultTable;
    }

    /**
     * This method performs the operations necessary for a multiple value lookup keep track of which results have been selected to be returned
     * to the calling document.  Note, this method does not actually requery for the results.
     * 
     * @param multipleValueLookupForm
     */
    protected void prepareToReturnSelectedResultBOs(MultipleValueLookupForm multipleValueLookupForm) {
        String lookupResultsSequenceNumber = multipleValueLookupForm.getLookupResultsSequenceNumber();
        if (StringUtils.isBlank(lookupResultsSequenceNumber)) {
            // pressed return before searching
            return;
        }
        Map<String, String> compositeObjectIdMap = LookupUtils.generateCompositeSelectedObjectIds(multipleValueLookupForm.getPreviouslySelectedObjectIdSet(),
                multipleValueLookupForm.getDisplayedObjectIdSet(), multipleValueLookupForm.getSelectedObjectIdSet());
        Set<String> compositeObjectIds = compositeObjectIdMap.keySet();
        try {
            LookupResultsService lookupResultsService = KNSServiceLocator.getLookupResultsService();
            lookupResultsService.persistSelectedObjectIds(lookupResultsSequenceNumber, compositeObjectIds,
                    GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }
        catch (Exception e) {
            LOG.error("error occured trying to retrieve selected multiple lookup results", e);
            throw new RuntimeException("error occured trying to retrieve selected multiple lookup results");
        }
    }

    /**
     * This method performs the operations necessary for a multiple value lookup to return no results to the calling page
     * 
     * @param multipleValueLookupForm
     */
    protected void prepareToReturnNone(MultipleValueLookupForm multipleValueLookupForm) {
        String lookupResultsSequenceNumber = multipleValueLookupForm.getLookupResultsSequenceNumber();
        try {
            if (StringUtils.isNotBlank(lookupResultsSequenceNumber)) {
                // we're returning nothing, so we try to get rid of stuff
                LookupResultsService lookupResultsService = KNSServiceLocator.getLookupResultsService();
                lookupResultsService.clearPersistedLookupResults(lookupResultsSequenceNumber);
                multipleValueLookupForm.setLookupResultsSequenceNumber(null);
            }
        }
        catch (Exception e) {
            // not a big deal, continue on and purge w/ a batch job
            LOG.error("error occured trying to clear lookup results seq nbr " + lookupResultsSequenceNumber, e);
        }
    }

    /**
     * This method performs the operations necessary for a multiple value lookup to export the rows via display tag
     * 
     * Note: this method assumes that the export will be opened in a new browser window, therefore, persisting the selected
     * checkboxes will not be needed.
     * 
     * @param multipleValueLookupForm
     * @return a list of result rows, to be used by display tag to render the results
     */
    protected List<ResultRow> prepareToExport(MultipleValueLookupForm multipleValueLookupForm) {
        String lookupResultsSequenceNumber = multipleValueLookupForm.getLookupResultsSequenceNumber();

        List<ResultRow> resultTable = null;
        try {
            LookupResultsService lookupResultsService = KNSServiceLocator.getLookupResultsService();
            resultTable = lookupResultsService.retrieveResultsTable(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }
        catch (Exception e) {
            LOG.error("error occured trying to export multiple lookup results", e);
            throw new RuntimeException("error occured trying to export multiple lookup results");
        }
        return resultTable;
    }


    /**
     * This method performs the operations necessary for a multiple value lookup to select all of the results and rerender the page
     * @param multipleValueLookupForm
     * @param maxRowsPerPage
     * @return a list of result rows, used by the UI to render the page
     */
    protected List<ResultRow> selectAll(MultipleValueLookupForm multipleValueLookupForm, int maxRowsPerPage) {
        String lookupResultsSequenceNumber = multipleValueLookupForm.getLookupResultsSequenceNumber();

        List<ResultRow> resultTable = null;
        try {
            LookupResultsService lookupResultsService = KNSServiceLocator.getLookupResultsService();
            resultTable = lookupResultsService.retrieveResultsTable(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }
        catch (Exception e) {
            LOG.error("error occured trying to export multiple lookup results", e);
            throw new RuntimeException("error occured trying to export multiple lookup results");
        }

        Map<String, String> selectedObjectIds = new HashMap<String, String>();
        for (ResultRow row : resultTable) {
            String objId = row.getObjectId();
            HtmlData.InputHtmlData returnUrl = (HtmlData.InputHtmlData) row.getReturnUrlHtmlData();
            returnUrl.setChecked(HtmlData.InputHtmlData.CHECKBOX_CHECKED_VALUE);
            row.setReturnUrl(returnUrl.constructCompleteHtmlTag());
            if(objId != null){
                selectedObjectIds.put(objId, objId);
            }
        }

        multipleValueLookupForm.jumpToPage(multipleValueLookupForm.getViewedPageNumber(), resultTable.size(), maxRowsPerPage);
        multipleValueLookupForm.setColumnToSortIndex(Integer.parseInt(multipleValueLookupForm.getPreviouslySortedColumnIndex()));
        multipleValueLookupForm.setCompositeObjectIdMap(selectedObjectIds);

        return resultTable;
    }

    @Override
    public ActionForward clearValues(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        MultipleValueLookupForm multipleValueLookupForm = (MultipleValueLookupForm) form;

        // call the following methods to clear the persisted results
        prepareToReturnNone(multipleValueLookupForm);

        return super.clearValues(mapping, form, request, response);
    }

    /**
     * This method performs the operations necessary for a multiple value lookup to unselect all of the results and rerender the page
     * @param multipleValueLookupForm
     * @param maxRowsPerPage
     * @return a list of result rows, used by the UI to render the page
     */
    protected List<ResultRow> unselectAll(MultipleValueLookupForm multipleValueLookupForm, int maxRowsPerPage) {
        String lookupResultsSequenceNumber = multipleValueLookupForm.getLookupResultsSequenceNumber();

        List<ResultRow> resultTable = null;
        try {
            LookupResultsService lookupResultsService = KNSServiceLocator.getLookupResultsService();
            resultTable = lookupResultsService.retrieveResultsTable(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }
        catch (Exception e) {
            LOG.error("error occured trying to export multiple lookup results", e);
            throw new RuntimeException("error occured trying to export multiple lookup results");
        }

        Map<String, String> selectedObjectIds = new HashMap<String, String>();
        // keep map empty since we're not selecting anything

        multipleValueLookupForm.jumpToPage(multipleValueLookupForm.getViewedPageNumber(), resultTable.size(), maxRowsPerPage);
        multipleValueLookupForm.setColumnToSortIndex(Integer.parseInt(multipleValueLookupForm.getPreviouslySortedColumnIndex()));
        multipleValueLookupForm.setCompositeObjectIdMap(selectedObjectIds);

        return resultTable;
    }

    /**
     * This method computes the max number of rows that should be rendered per page for a multiple value lookup.
     * 
     * This method first looks for an application parameter in FS_PARM_T, group SYSTEM, multipleValueLookupResultsPerPage
     * 
     * if someone wants to implement something where a user can decide how many results to display per page, 
     * this method is the place to do it.  Make this method read form values to determine the max rows per page based on the user inputs
     * 
     * @see KRADConstants.SystemGroupParameterNames#MULTIPLE_VALUE_LOOKUP_RESULTS_PER_PAGE
     * @see #DEFAULT_MAX_ROWS_PER_PAGE
     * @param multipleValueLookupForm the form
     * @return
     */
    protected int getMaxRowsPerPage(MultipleValueLookupForm multipleValueLookupForm) {
        Integer appMaxRowsPerPage = LookupUtils.getApplicationMaximumSearchResulsPerPageForMultipleValueLookups();
        if (appMaxRowsPerPage == null) {
            LOG.warn("Couldn't find application results per page for MV lookups.  Using default of " + DEFAULT_MAX_ROWS_PER_PAGE);
            appMaxRowsPerPage = new Integer(DEFAULT_MAX_ROWS_PER_PAGE);
        }
        return appMaxRowsPerPage;
    }
}

