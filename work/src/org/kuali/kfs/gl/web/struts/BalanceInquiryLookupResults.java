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
package org.kuali.module.gl.web.struts.form;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.web.struts.form.LookupForm;
import org.kuali.core.web.struts.form.KualiTableRenderFormMetadata;
import org.kuali.kfs.KFSConstants;

/**
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
     * The number of rows that match the query criteria or
     *  the max results limit size (if applicable), whichever is less
     */
    private int resultsLimitedSize;
    
    /**
     * when the looked results screen was rendered, the index of the column that the results were sorted on.  -1 for unknown, index numbers
     * starting at 0
     */
    private String previouslySortedColumnIndex;
    
    /**
     * Comment for <code>columnToSortIndex</code>
     */
    private int columnToSortIndex;
    
    /**
     * the name of the collection being looked up by the calling page.  This value will be returned unmodified to the 
     * calling page (indicated by super.getBackLocation()), which should use it to determine in which collection the 
     * selected results will be returned.
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
     * The object IDs that are selected after the struts action is complete; the obj IDs in the keys of this Map will be considered checked in the UI
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
     * @return
     */
    public String getCompositeSelectedObjectIds() {
        return LookupUtils.convertSetOfObjectIdsToString(getCompositeObjectIdMap().keySet());
    }

    protected Set<String> parsePreviouslySelectedObjectIds(HttpServletRequest request) {
        String previouslySelectedObjectIds = request.getParameter(KFSConstants.MULTIPLE_VALUE_LOOKUP_PREVIOUSLY_SELECTED_OBJ_IDS_PARAM);
        return LookupUtils.convertStringOfObjectIdsToSet(previouslySelectedObjectIds);
    }
    
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
     * 
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
     * Parses the method to call parameter passed in as a post parameter
     * 
     * The parameter should be something like methodToCall.sort.1.(::;true;::).x, this method will return the value
     * between (::; and ;::) as a boolean
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
    
    public int getViewedPageNumber() {
        return tableMetadata.getViewedPageNumber();
    }

    public void setViewedPageNumber(int pageNumberBeingViewedForMultivalueLookups) {
        tableMetadata.setViewedPageNumber(pageNumberBeingViewedForMultivalueLookups);
    }

    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    public void setLookupResultsSequenceNumber(String lookupResultSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultSequenceNumber;
    }
    
    public int getTotalNumberOfPages() {
        return tableMetadata.getTotalNumberOfPages();
    }

    public void setTotalNumberOfPages(int totalNumberOfPages) {
        tableMetadata.setTotalNumberOfPages(totalNumberOfPages);
    }

    public int getFirstRowIndex() {
        return tableMetadata.getFirstRowIndex();
    }

    public void setFirstRowIndex(int firstRowIndex) {
        tableMetadata.setFirstRowIndex(firstRowIndex);
    }

    public int getLastRowIndex() {
        return tableMetadata.getLastRowIndex();
    }

    public void setLastRowIndex(int lastRowIndex) {
        tableMetadata.setLastRowIndex(lastRowIndex);
    }

    public int getSwitchToPageNumber() {
        return tableMetadata.getSwitchToPageNumber();
    }

    protected void setSwitchToPageNumber(int switchToPageNumber) {
        tableMetadata.setSwitchToPageNumber(switchToPageNumber);
    }

    public Set<String> getPreviouslySelectedObjectIdSet() {
        return previouslySelectedObjectIdSet;
    }

    public void setPreviouslySelectedObjectIdSet(Set<String> previouslySelectedObjectIds) {
        this.previouslySelectedObjectIdSet = previouslySelectedObjectIds;
    }

    public Set<String> getSelectedObjectIdSet() {
        return selectedObjectIdSet;
    }

    public void setSelectedObjectIdSet(Set<String> selectedObjectIdSet) {
        this.selectedObjectIdSet = selectedObjectIdSet;
    }

    public Set<String> getDisplayedObjectIdSet() {
        return displayedObjectIdSet;
    }

    public void setDisplayedObjectIdSet(Set<String> displayedObjectIdSet) {
        this.displayedObjectIdSet = displayedObjectIdSet;
    }

    public Map<String, String> getCompositeObjectIdMap() {
        return compositeObjectIdMap;
    }

    public void setCompositeObjectIdMap(Map<String, String> compositeObjectIdMap) {
        this.compositeObjectIdMap = compositeObjectIdMap;
    }

    public int getColumnToSortIndex() {
        return columnToSortIndex;
    }

    public void setColumnToSortIndex(int columnToSortIndex) {
        this.columnToSortIndex = columnToSortIndex;
    }

    public String getPreviouslySortedColumnIndex() {
        return previouslySortedColumnIndex;
    }

    public void setPreviouslySortedColumnIndex(String previouslySortedColumnIndex) {
        this.previouslySortedColumnIndex = previouslySortedColumnIndex;
    }

    /**
     * gets the name of the collection being looked up by the calling page.  This value will be returned unmodified to the 
     * calling page (indicated by super.getBackLocation()), which should use it to determine in which collection the 
     * selected results will be returned.
     * 
     * @return
     */
    public String getLookedUpCollectionName() {
        return lookedUpCollectionName;
    }

    /**
     * sets the name of the collection being looked up by the calling page.  This value will be returned unmodified to the 
     * calling page (indicated by super.getBackLocation()), which should use it to determine in which collection the 
     * selected results will be returned
     * 
     * @param lookedUpCollectionName
     */
    public void setLookedUpCollectionName(String lookedUpCollectionName) {
        this.lookedUpCollectionName = lookedUpCollectionName;
    }

    public int getResultsActualSize() {
        return resultsActualSize;
    }

    public void setResultsActualSize(int resultsActualSize) {
        this.resultsActualSize = resultsActualSize;
    }

    public int getResultsLimitedSize() {
        return resultsLimitedSize;
    }

    public void setResultsLimitedSize(int resultsLimitedSize) {
        this.resultsLimitedSize = resultsLimitedSize;
    }
    
    public void jumpToFirstPage(int listSize, int maxRowsPerPage) {
        tableMetadata.jumpToFirstPage(listSize, maxRowsPerPage);
    }
    
    public void jumpToLastPage(int listSize, int maxRowsPerPage) {
        tableMetadata.jumpToLastPage(listSize, maxRowsPerPage);
    }
    
    public void jumpToPage(int pageNumber, int listSize, int maxRowsPerPage) {
        tableMetadata.jumpToPage(pageNumber, listSize, maxRowsPerPage);
    }
    
}
