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

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

public interface LookupResultsSelectable {
    public void populate(HttpServletRequest request);

    /**
     * This method converts the composite object IDs into a String
     * 
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
     * gets the name of the collection being looked up by the calling page. This value will be returned unmodified to the calling
     * page (indicated by super.getBackLocation()), which should use it to determine in which collection the selected results will
     * be returned.
     * 
     * @return String
     */
    public String getLookedUpCollectionName();

    /**
     * sets the name of the collection being looked up by the calling page. This value will be returned unmodified to the calling
     * page (indicated by super.getBackLocation()), which should use it to determine in which collection the selected results will
     * be returned
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
