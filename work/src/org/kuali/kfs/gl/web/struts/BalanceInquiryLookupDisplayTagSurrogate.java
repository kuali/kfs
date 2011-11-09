/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.gl.web.struts;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.LookupResultsService;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * This class serves as the struts action for implementing multiple value lookups
 */
public class BalanceInquiryLookupDisplayTagSurrogate implements LookupDisplayTagSurrogate {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceInquiryLookupDisplayTagSurrogate.class);

    /**
     * If there is no app param defined for the # rows/page, then this value will be used for the default
     * 
     * @see KualiMultipleValueLookupAction#getMaxRowsPerPage(MultipleValueLookupForm)
     */
    public static final int DEFAULT_MAX_ROWS_PER_PAGE = 50;

    /**
     * @see LookupDisplayTagSurrogate#performMultipleValueLookup(LookupResultsSelectable,LookupForm,List,boolean)
     *
     * KRAD Conversion: Lookupable performs customization of the results setting the sort order. 
     * 
     * Fields are in data dictionary for bo Balance.
     */
    public Collection performMultipleValueLookup(LookupResultsSelectable selectable, LookupForm form, List<ResultRow> resultTable, boolean bounded) {
        Lookupable lookupable = form.getLookupable();
        Collection displayList = lookupable.performLookup(form, resultTable, bounded);

        List defaultSortColumns = lookupable.getDefaultSortColumns();
        if (defaultSortColumns != null && !defaultSortColumns.isEmpty() && resultTable != null && !resultTable.isEmpty()) {
            // there's a default sort order, just find the first sort column, and we can't go wrong
            String firstSortColumn = (String) defaultSortColumns.get(0);

            // go thru the first result row to find the index of the column (more efficient than calling lookupable.getColumns since
            // we don't have to recreate column list)
            int firstSortColumnIdx = -1;
            List<Column> columnsForFirstResultRow = resultTable.get(0).getColumns();
            for (int i = 0; i < columnsForFirstResultRow.size(); i++) {
                if (StringUtils.equals(firstSortColumn, columnsForFirstResultRow.get(i).getPropertyName())) {
                    firstSortColumnIdx = i;
                    break;
                }
            }
            selectable.setColumnToSortIndex(firstSortColumnIdx);
        }
        else {
            // don't know how results were sorted, so we just say -1
            selectable.setColumnToSortIndex(-1);
        }

        // we just performed the lookup, so we're on the first page (indexed from 0)
        selectable.jumpToFirstPage(resultTable.size(), getMaxRowsPerPage(selectable));

        SequenceAccessorService sequenceAccessorService = SpringContext.getBean(SequenceAccessorService.class);
        String lookupResultsSequenceNumber = String.valueOf(sequenceAccessorService.getNextAvailableSequenceNumber(KRADConstants.LOOKUP_RESULTS_SEQUENCE));
        selectable.setLookupResultsSequenceNumber(lookupResultsSequenceNumber);
        try {
            LookupResultsService lookupResultsService = SpringContext.getBean(LookupResultsService.class);
            lookupResultsService.persistResultsTable(lookupResultsSequenceNumber, resultTable, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }
        catch (Exception e) {
            LOG.error("error occured trying to persist multiple lookup results", e);
            throw new RuntimeException("error occured trying to persist multiple lookup results");
        }

        // since new search, nothing's checked
        selectable.setCompositeObjectIdMap(new HashMap<String, String>());

        return displayList;
    }

    /**
     * @see LookupDisplayTagSurrogate#switchToPage(LookupResultsSelectable,int)
     * 
     * KRAD Conversion: Lookupable performs retrieving the data.
     * 
     * Fields are in data dictionary for bo Balance.
     */
    public List<ResultRow> switchToPage(LookupResultsSelectable selectable, int maxRowsPerPage) {
        String lookupResultsSequenceNumber = selectable.getLookupResultsSequenceNumber();

        List<ResultRow> resultTable = null;
        try {
            resultTable = SpringContext.getBean(LookupResultsService.class).retrieveResultsTable(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }
        catch (Exception e) {
            LOG.error("error occured trying to retrieve multiple lookup results", e);
            throw new RuntimeException("error occured trying to retrieve multiple lookup results");
        }

        selectable.jumpToPage(selectable.getSwitchToPageNumber(), resultTable.size(), maxRowsPerPage);

        selectable.setColumnToSortIndex(Integer.parseInt(selectable.getPreviouslySortedColumnIndex()));
        selectable.setCompositeObjectIdMap(LookupUtils.generateCompositeSelectedObjectIds(selectable.getPreviouslySelectedObjectIdSet(), selectable.getDisplayedObjectIdSet(), selectable.getSelectedObjectIdSet()));
        return resultTable;
    }

    /**
     * @see LookupDisplayTagSurrogate#sort(LookupResultsSelectable,int)
     * 
     * KRAD Conversion: Lookupable performs retrieving the data and reverses the data if need to be sorted.
     * 
     * Fields are in data dictionary for bo Balance.
     */
    public List<ResultRow> sort(LookupResultsSelectable selectable, int maxRowsPerPage) {
        String lookupResultsSequenceNumber = selectable.getLookupResultsSequenceNumber();

        LookupResultsService lookupResultsService = SpringContext.getBean(LookupResultsService.class);

        List<ResultRow> resultTable = null;
        try {
            resultTable = lookupResultsService.retrieveResultsTable(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }
        catch (Exception e) {
            LOG.error("error occured trying to retrieve multiple lookup results", e);
            throw new RuntimeException("error occured trying to retrieve multiple lookup results");
        }

        int columnToSortOn = selectable.getColumnToSortIndex();
        int columnCurrentlySortedOn = Integer.parseInt(selectable.getPreviouslySortedColumnIndex());

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
            lookupResultsService.persistResultsTable(lookupResultsSequenceNumber, resultTable, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }
        catch (Exception e) {
            LOG.error("error occured trying to persist multiple lookup results", e);
            throw new RuntimeException("error occured trying to persist multiple lookup results");
        }

        // we just performed the sort, so go back to first page
        selectable.jumpToFirstPage(resultTable.size(), maxRowsPerPage);

        selectable.setCompositeObjectIdMap(LookupUtils.generateCompositeSelectedObjectIds(selectable.getPreviouslySelectedObjectIdSet(), selectable.getDisplayedObjectIdSet(), selectable.getSelectedObjectIdSet()));
        return resultTable;
    }

    /**
     * @see LookupDisplayTagSurrogate#prepareToReturnSelectedResultBOs(LookupResultsSelectable)
     */
    public void prepareToReturnSelectedResultBOs(LookupResultsSelectable selectable) {
        String lookupResultsSequenceNumber = selectable.getLookupResultsSequenceNumber();
        if (StringUtils.isBlank(lookupResultsSequenceNumber)) {
            // pressed return before searching
            return;
        }
        Map<String, String> compositeObjectIdMap = LookupUtils.generateCompositeSelectedObjectIds(selectable.getPreviouslySelectedObjectIdSet(), selectable.getDisplayedObjectIdSet(), selectable.getSelectedObjectIdSet());
        Set<String> compositeObjectIds = compositeObjectIdMap.keySet();
        try {
            LookupResultsService lookupResultsService = SpringContext.getBean(LookupResultsService.class);
            lookupResultsService.persistSelectedObjectIds(lookupResultsSequenceNumber, compositeObjectIds, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }
        catch (Exception e) {
            LOG.error("error occured trying to retrieve selected multiple lookup results", e);
            throw new RuntimeException("error occured trying to retrieve selected multiple lookup results");
        }
    }

    /**
     * @see LookupDisplayTagSurrogate#prepareToReturnNone(LookupResultsSelectable)
     */
    public void prepareToReturnNone(LookupResultsSelectable selectable) {
        String lookupResultsSequenceNumber = selectable.getLookupResultsSequenceNumber();
        try {
            if (StringUtils.isNotBlank(lookupResultsSequenceNumber)) {
                // we're returning nothing, so we try to get rid of stuff
                LookupResultsService lookupResultsService = SpringContext.getBean(LookupResultsService.class);
                lookupResultsService.clearPersistedLookupResults(lookupResultsSequenceNumber);
            }
        }
        catch (Exception e) {
            // not a big deal, continue on and purge w/ a batch job
            LOG.error("error occured trying to clear lookup results seq nbr " + lookupResultsSequenceNumber, e);
        }
    }

    /**
     * @see LookupDisplayTagSurrogate#prepareToExport(LookupResultsSelectable)
     * 
     * KRAD Conversion: Lookupable performs retrieving the data.
     * 
     * Fields are in data dictionary for bo Balance.
     */
    public List<ResultRow> prepareToExport(LookupResultsSelectable selectable) {
        String lookupResultsSequenceNumber = selectable.getLookupResultsSequenceNumber();

        List<ResultRow> resultTable = null;
        try {
            LookupResultsService lookupResultsService = SpringContext.getBean(LookupResultsService.class);
            resultTable = lookupResultsService.retrieveResultsTable(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }
        catch (Exception e) {
            LOG.error("error occured trying to export multiple lookup results", e);
            throw new RuntimeException("error occured trying to export multiple lookup results");
        }
        return resultTable;
    }


    /**
     * @see LookupDisplayTagSurrogate#getMaxRowsPerPage(LookupResultsSelectable)
     * 
     * KRAD Conversion: Lookupable performs customization of the results.
     * 
     * Fields are in data dictionary for bo Balance.
     */
    public List<ResultRow> selectAll(LookupResultsSelectable selectable, int maxRowsPerPage) {
        String lookupResultsSequenceNumber = selectable.getLookupResultsSequenceNumber();

        List<ResultRow> resultTable = null;
        try {
            LookupResultsService lookupResultsService = SpringContext.getBean(LookupResultsService.class);
            resultTable = lookupResultsService.retrieveResultsTable(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }
        catch (Exception e) {
            LOG.error("error occured trying to export multiple lookup results", e);
            throw new RuntimeException("error occured trying to export multiple lookup results");
        }

        Map<String, String> selectedObjectIds = new HashMap<String, String>();
        for (ResultRow row : resultTable) {
            String objId = row.getObjectId();
            selectedObjectIds.put(objId, objId);
        }

        selectable.jumpToPage(selectable.getViewedPageNumber(), resultTable.size(), maxRowsPerPage);
        selectable.setColumnToSortIndex(Integer.parseInt(selectable.getPreviouslySortedColumnIndex()));
        selectable.setCompositeObjectIdMap(selectedObjectIds);

        return resultTable;
    }

    /**
     * @see LookupDisplayTagSurrogate#getMaxRowsPerPage(LookupResultsSelectable)
     * 
     * KRAD Conversion: Lookupable performs customization of the results.
     * 
     * Fields are in data dictionary for bo Balance.
     */
    public List<ResultRow> unselectAll(LookupResultsSelectable selectable, int maxRowsPerPage) {
        String lookupResultsSequenceNumber = selectable.getLookupResultsSequenceNumber();

        List<ResultRow> resultTable = null;
        try {
            LookupResultsService lookupResultsService = SpringContext.getBean(LookupResultsService.class);
            resultTable = lookupResultsService.retrieveResultsTable(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }
        catch (Exception e) {
            LOG.error("error occured trying to export multiple lookup results", e);
            throw new RuntimeException("error occured trying to export multiple lookup results");
        }

        Map<String, String> selectedObjectIds = new HashMap<String, String>();
        // keep map empty since we're not selecting anything

        selectable.jumpToPage(selectable.getViewedPageNumber(), resultTable.size(), maxRowsPerPage);
        selectable.setColumnToSortIndex(Integer.parseInt(selectable.getPreviouslySortedColumnIndex()));
        selectable.setCompositeObjectIdMap(selectedObjectIds);

        return resultTable;
    }

    /**
     * @see LookupDisplayTagSurrogate#getMaxRowsPerPage(LookupResultsSelectable)
     */
    public int getMaxRowsPerPage(LookupResultsSelectable selectable) {
        Integer appMaxRowsPerPage = LookupUtils.getApplicationMaximumSearchResulsPerPageForMultipleValueLookups();
        if (appMaxRowsPerPage == null) {
            LOG.warn("Couldn't find application results per page for MV lookups.  Using default of " + DEFAULT_MAX_ROWS_PER_PAGE);
            appMaxRowsPerPage = new Integer(DEFAULT_MAX_ROWS_PER_PAGE);
        }
        return appMaxRowsPerPage;
    }
}
