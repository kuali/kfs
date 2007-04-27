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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

public interface LookupResultsSelectable {
    public void populate(HttpServletRequest request);
    
    /**
     * This method converts the composite object IDs into a String
     * @return String
     */
    public String getCompositeSelectedObjectIds();

    public int getViewedPageNumber();

    public void setViewedPageNumber(int pageNumberBeingViewedForMultivalueLookups);

    public String getLookupResultsSequenceNumber();

    public void setLookupResultsSequenceNumber(String lookupResultSequenceNumber);
    
    public int getTotalNumberOfPages();

    public void setTotalNumberOfPages(int totalNumberOfPages);

    public int getFirstRowIndex();

    public void setFirstRowIndex(int firstRowIndex);

    public int getLastRowIndex();

    public void setLastRowIndex(int lastRowIndex);

    public int getSwitchToPageNumber();

    public Set<String> getPreviouslySelectedObjectIdSet();

    public void setPreviouslySelectedObjectIdSet(Set<String> previouslySelectedObjectIds);

    public Set<String> getSelectedObjectIdSet();

    public void setSelectedObjectIdSet(Set<String> selectedObjectIdSet);

    public Set<String> getDisplayedObjectIdSet();

    public void setDisplayedObjectIdSet(Set<String> displayedObjectIdSet);

    public Map<String, String> getCompositeObjectIdMap();

    public void setCompositeObjectIdMap(Map<String, String> compositeObjectIdMap);

    public int getColumnToSortIndex();

    public void setColumnToSortIndex(int columnToSortIndex);

    public String getPreviouslySortedColumnIndex();

    public void setPreviouslySortedColumnIndex(String previouslySortedColumnIndex);

    /**
     * gets the name of the collection being looked up by the calling page.  This value will be returned unmodified to the 
     * calling page (indicated by super.getBackLocation()), which should use it to determine in which collection the 
     * selected results will be returned.
     * 
     * @return String
     */
    public String getLookedUpCollectionName();

    /**
     * sets the name of the collection being looked up by the calling page.  This value will be returned unmodified to the 
     * calling page (indicated by super.getBackLocation()), which should use it to determine in which collection the 
     * selected results will be returned
     * 
     * @param lookedUpCollectionName
     */
    public void setLookedUpCollectionName(String lookedUpCollectionName);

    public int getResultsActualSize();
    
    public void setResultsActualSize(int resultsActualSize);
    
    public int getResultsLimitedSize();
    
    public void setResultsLimitedSize(int resultsLimitedSize);
    
    public void jumpToFirstPage(int listSize, int maxRowsPerPage);
    
    public void jumpToLastPage(int listSize, int maxRowsPerPage);
    
    public void jumpToPage(int pageNumber, int listSize, int maxRowsPerPage);    
}
