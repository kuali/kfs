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

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.web.struts.form.KualiTableRenderFormMetadata;
import org.kuali.rice.kns.web.struts.form.LookupForm;

/**
 * This class is the action form for balance inquiry lookup results
 * 
 */
public class BalanceInquiryLookupResults extends LookupForm implements LookupResultsSelectable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceInquiryLookupResults.class);

    private KualiTableRenderFormMetadata tableMetadata;

    private String lookupResultsSequenceNumber;

    /**
     * The number of rows that match the query criteria
     */
    private int resultsActualSize;

    /**
     * The number of rows that match the query criteria or the max results limit size (if applicable), whichever is less
     */
    private int resultsLimitedSize;

    /**
     * when the looked results screen was rendered, the index of the column that the results were sorted on. -1 for unknown, index
     * numbers starting at 0
     */
    private String previouslySortedColumnIndex;

    /**
     * Comment for <code>columnToSortIndex</code>
     */
    private int columnToSortIndex;

    /**
     * the name of the collection being looked up by the calling page. This value will be returned unmodified to the calling page
     * (indicated by super.getBackLocation()), which should use it to determine in which collection the selected results will be
     * returned.
     */
    private String lookedUpCollectionName;

    /**
     * Those object IDs that were selected before the current page is rendered
     */
    private Set<String> previouslySelectedObjectIdSet;
    /**
     * Those object IDs that are rendered on the current page
     */
    private Set<String> displayedObjectIdSet;
    /**
     * Those object IDs that are selected/checked on the current page
     */
    private Set<String> selectedObjectIdSet;
    /**
     * The object IDs that are selected after the struts action is complete; the obj IDs in the keys of this Map will be considered
     * checked in the UI
     */
    private Map<String, String> compositeObjectIdMap;

    public BalanceInquiryLookupResults() {
        tableMetadata = new KualiTableRenderFormMetadata();
    }

    protected String getMethodToCall(HttpServletRequest request) {
        return request.getParameter(KFSConstants.DISPATCH_REQUEST_PARAMETER);
    }

    /**
     * Named appropriately as it is intended to be called from a <code>{@link LookupForm}</code>
     * 
     * @param request HttpServletRequest
     */
    public void populate(HttpServletRequest request) {
        super.populate(request);

        if (StringUtils.isNotBlank(request.getParameter(KFSConstants.TableRenderConstants.VIEWED_PAGE_NUMBER))) {
            setViewedPageNumber(Integer.parseInt(request.getParameter(KFSConstants.TableRenderConstants.VIEWED_PAGE_NUMBER)));
        }
        else {
            setViewedPageNumber(0); // first page is page 0
        }

        if (KFSConstants.TableRenderConstants.SWITCH_TO_PAGE_METHOD.equals(getMethodToCall(request))) {
            // look for the page number to switch to
            setSwitchToPageNumber(-1);

            // the param we're looking for looks like: methodToCall.switchToPage.1.x , where 1 is the page nbr
            String paramPrefix = KFSConstants.DISPATCH_REQUEST_PARAMETER + "." + KFSConstants.TableRenderConstants.SWITCH_TO_PAGE_METHOD + ".";
            for (Enumeration i = request.getParameterNames(); i.hasMoreElements();) {
                String parameterName = (String) i.nextElement();
                if (parameterName.startsWith(paramPrefix) && parameterName.endsWith(".x")) {
                    String switchToPageNumberStr = StringUtils.substringBetween(parameterName, paramPrefix, ".");
                    setSwitchToPageNumber(Integer.parseInt(switchToPageNumberStr));
                }
            }
            if (getSwitchToPageNumber() == -1) {
                throw new RuntimeException("Couldn't find page number");
            }
        }

        if (KFSConstants.TableRenderConstants.SORT_METHOD.equals(getMethodToCall(request))) {
            setColumnToSortIndex(-1);

            // the param we're looking for looks like: methodToCall.sort.1.x , where 1 is the column to sort on
            String paramPrefix = KFSConstants.DISPATCH_REQUEST_PARAMETER + "." + KFSConstants.TableRenderConstants.SORT_METHOD + ".";
            for (Enumeration i = request.getParameterNames(); i.hasMoreElements();) {
                String parameterName = (String) i.nextElement();
                if (parameterName.startsWith(paramPrefix) && parameterName.endsWith(".x")) {
                    String columnToSortStr = StringUtils.substringBetween(parameterName, paramPrefix, ".");
                    setColumnToSortIndex(Integer.parseInt(columnToSortStr));
                }
            }
            if (getColumnToSortIndex() == -1) {
                throw new RuntimeException("Couldn't find column to sort");
            }
        }

        setPreviouslySelectedObjectIdSet(parsePreviouslySelectedObjectIds(request));
        setSelectedObjectIdSet(parseSelectedObjectIdSet(request));
        setDisplayedObjectIdSet(parseDisplayedObjectIdSet(request));

        setSearchUsingOnlyPrimaryKeyValues(parseSearchUsingOnlyPrimaryKeyValues(request));
        if (isSearchUsingOnlyPrimaryKeyValues()) {
            setPrimaryKeyFieldLabels(getLookupable().getPrimaryKeyFieldLabels());
        }
    }

    /**
     * This method converts the composite object IDs into a String
     * 
     * @return String for composite list of selected object IDs
     */
    public String getCompositeSelectedObjectIds() {
        return LookupUtils.convertSetOfObjectIdsToString(getCompositeObjectIdMap().keySet());
    }

    /**
     * Parses a list of previously selected object IDs
     * 
     * @param request
     * @return Set containing list of previously selected object IDs
     */
    protected Set<String> parsePreviouslySelectedObjectIds(HttpServletRequest request) {
        String previouslySelectedObjectIds = request.getParameter(KFSConstants.MULTIPLE_VALUE_LOOKUP_PREVIOUSLY_SELECTED_OBJ_IDS_PARAM);
        return LookupUtils.convertStringOfObjectIdsToSet(previouslySelectedObjectIds);
    }

    /**
     * Parses a list of selected object IDs
     * 
     * @param request
     * @return Set containing list of selected object IDs
     */
    protected Set<String> parseSelectedObjectIdSet(HttpServletRequest request) {
        Set<String> set = new HashSet<String>();

        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            if (paramName.startsWith(KFSConstants.MULTIPLE_VALUE_LOOKUP_SELECTED_OBJ_ID_PARAM_PREFIX) && StringUtils.isNotBlank(request.getParameter(paramName))) {
                set.add(StringUtils.substringAfter(paramName, KFSConstants.MULTIPLE_VALUE_LOOKUP_SELECTED_OBJ_ID_PARAM_PREFIX));
            }
        }
        return set;
    }

    /**
     * Parses a list of displayed object IDs
     * 
     * @param request
     * @return Set containing list of displayed object IDs
     */
    protected Set<String> parseDisplayedObjectIdSet(HttpServletRequest request) {
        Set<String> set = new HashSet<String>();

        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            if (paramName.startsWith(KFSConstants.MULTIPLE_VALUE_LOOKUP_DISPLAYED_OBJ_ID_PARAM_PREFIX) && StringUtils.isNotBlank(request.getParameter(paramName))) {
                set.add(StringUtils.substringAfter(paramName, KFSConstants.MULTIPLE_VALUE_LOOKUP_DISPLAYED_OBJ_ID_PARAM_PREFIX));
            }
        }
        return set;
    }

    /**
     * Iterates through the request params, looks for the parameter representing the method to call in the format like
     * methodToCall.sort.1.(::;true;::).x, and returns the boolean value in the (::; and ;::) delimiters.
     * 
     * @see MultipleValueLookupForm#parseSearchUsingOnlyPrimaryKeyValues(String)
     * @param request
     * @return
     */
    protected boolean parseSearchUsingOnlyPrimaryKeyValues(HttpServletRequest request) {
        // the param we're looking for looks like: methodToCall.sort.1.(::;true;::).x , we want to parse out the "true" component
        String paramPrefix = KFSConstants.DISPATCH_REQUEST_PARAMETER + "." + getMethodToCall(request) + ".";
        for (Enumeration i = request.getParameterNames(); i.hasMoreElements();) {
            String parameterName = (String) i.nextElement();
            if (parameterName.startsWith(paramPrefix) && parameterName.endsWith(".x")) {
                return parseSearchUsingOnlyPrimaryKeyValues(parameterName);
            }
        }
        // maybe doing an initial search, so no value will be present
        return false;
    }

    /**
     * Parses the method to call parameter passed in as a post parameter The parameter should be something like
     * methodToCall.sort.1.(::;true;::).x, this method will return the value between (::; and ;::) as a boolean
     * 
     * @param methodToCallParam the method to call in a format described above
     * @return the value between the delimiters, false if there are no delimiters
     */
    protected boolean parseSearchUsingOnlyPrimaryKeyValues(String methodToCallParam) {
        String searchUsingOnlyPrimaryKeyValuesStr = StringUtils.substringBetween(methodToCallParam, KFSConstants.METHOD_TO_CALL_PARM12_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM12_RIGHT_DEL);
        if (StringUtils.isBlank(searchUsingOnlyPrimaryKeyValuesStr)) {
            return false;
        }
        return Boolean.parseBoolean(searchUsingOnlyPrimaryKeyValuesStr);
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#getViewedPageNumber()
     */
    public int getViewedPageNumber() {
        return tableMetadata.getViewedPageNumber();
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#setViewedPageNumber(int)
     */
    public void setViewedPageNumber(int pageNumberBeingViewedForMultivalueLookups) {
        tableMetadata.setViewedPageNumber(pageNumberBeingViewedForMultivalueLookups);
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#getLookupResultsSequenceNumber()
     */
    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#setLookupResultsSequenceNumber(java.lang.String)
     */
    public void setLookupResultsSequenceNumber(String lookupResultSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultSequenceNumber;
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#getTotalNumberOfPages()
     */
    public int getTotalNumberOfPages() {
        return tableMetadata.getTotalNumberOfPages();
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#setTotalNumberOfPages(int)
     */
    public void setTotalNumberOfPages(int totalNumberOfPages) {
        tableMetadata.setTotalNumberOfPages(totalNumberOfPages);
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#getFirstRowIndex()
     */
    public int getFirstRowIndex() {
        return tableMetadata.getFirstRowIndex();
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#setFirstRowIndex(int)
     */
    public void setFirstRowIndex(int firstRowIndex) {
        tableMetadata.setFirstRowIndex(firstRowIndex);
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#getLastRowIndex()
     */
    public int getLastRowIndex() {
        return tableMetadata.getLastRowIndex();
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#setLastRowIndex(int)
     */
    public void setLastRowIndex(int lastRowIndex) {
        tableMetadata.setLastRowIndex(lastRowIndex);
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#getSwitchToPageNumber()
     */
    public int getSwitchToPageNumber() {
        return tableMetadata.getSwitchToPageNumber();
    }

    /**
     * This method sets the switchToPageNumber attribute for the metadata
     * 
     * @param switchToPageNumber
     */
    protected void setSwitchToPageNumber(int switchToPageNumber) {
        tableMetadata.setSwitchToPageNumber(switchToPageNumber);
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#getPreviouslySelectedObjectIdSet()
     */
    public Set<String> getPreviouslySelectedObjectIdSet() {
        return previouslySelectedObjectIdSet;
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#setPreviouslySelectedObjectIdSet(java.util.Set)
     */
    public void setPreviouslySelectedObjectIdSet(Set<String> previouslySelectedObjectIds) {
        this.previouslySelectedObjectIdSet = previouslySelectedObjectIds;
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#getSelectedObjectIdSet()
     */
    public Set<String> getSelectedObjectIdSet() {
        return selectedObjectIdSet;
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#setSelectedObjectIdSet(java.util.Set)
     */
    public void setSelectedObjectIdSet(Set<String> selectedObjectIdSet) {
        this.selectedObjectIdSet = selectedObjectIdSet;
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#getDisplayedObjectIdSet()
     */
    public Set<String> getDisplayedObjectIdSet() {
        return displayedObjectIdSet;
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#setDisplayedObjectIdSet(java.util.Set)
     */
    public void setDisplayedObjectIdSet(Set<String> displayedObjectIdSet) {
        this.displayedObjectIdSet = displayedObjectIdSet;
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#getCompositeObjectIdMap()
     */
    public Map<String, String> getCompositeObjectIdMap() {
        return compositeObjectIdMap;
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#setCompositeObjectIdMap(java.util.Map)
     */
    public void setCompositeObjectIdMap(Map<String, String> compositeObjectIdMap) {
        this.compositeObjectIdMap = compositeObjectIdMap;
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#getColumnToSortIndex()
     */
    public int getColumnToSortIndex() {
        return columnToSortIndex;
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#setColumnToSortIndex(int)
     */
    public void setColumnToSortIndex(int columnToSortIndex) {
        this.columnToSortIndex = columnToSortIndex;
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#getPreviouslySortedColumnIndex()
     */
    public String getPreviouslySortedColumnIndex() {
        return previouslySortedColumnIndex;
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#setPreviouslySortedColumnIndex(java.lang.String)
     */
    public void setPreviouslySortedColumnIndex(String previouslySortedColumnIndex) {
        this.previouslySortedColumnIndex = previouslySortedColumnIndex;
    }

    /**
     * gets the name of the collection being looked up by the calling page. This value will be returned unmodified to the calling
     * page (indicated by super.getBackLocation()), which should use it to determine in which collection the selected results will
     * be returned.
     * 
     * @return
     */
    public String getLookedUpCollectionName() {
        return lookedUpCollectionName;
    }

    /**
     * sets the name of the collection being looked up by the calling page. This value will be returned unmodified to the calling
     * page (indicated by super.getBackLocation()), which should use it to determine in which collection the selected results will
     * be returned
     * 
     * @param lookedUpCollectionName
     */
    public void setLookedUpCollectionName(String lookedUpCollectionName) {
        this.lookedUpCollectionName = lookedUpCollectionName;
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#getResultsActualSize()
     */
    public int getResultsActualSize() {
        return resultsActualSize;
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#setResultsActualSize(int)
     */
    public void setResultsActualSize(int resultsActualSize) {
        this.resultsActualSize = resultsActualSize;
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#getResultsLimitedSize()
     */
    public int getResultsLimitedSize() {
        return resultsLimitedSize;
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#setResultsLimitedSize(int)
     */
    public void setResultsLimitedSize(int resultsLimitedSize) {
        this.resultsLimitedSize = resultsLimitedSize;
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#jumpToFirstPage(int, int)
     */
    public void jumpToFirstPage(int listSize, int maxRowsPerPage) {
        tableMetadata.jumpToFirstPage(listSize, maxRowsPerPage);
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#jumpToLastPage(int, int)
     */
    public void jumpToLastPage(int listSize, int maxRowsPerPage) {
        tableMetadata.jumpToLastPage(listSize, maxRowsPerPage);
    }

    /**
     * @see org.kuali.kfs.gl.web.struts.LookupResultsSelectable#jumpToPage(int, int, int)
     */
    public void jumpToPage(int pageNumber, int listSize, int maxRowsPerPage) {
        tableMetadata.jumpToPage(pageNumber, listSize, maxRowsPerPage);
    }

}
