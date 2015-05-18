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
package org.kuali.rice.kns.web.struts.form;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.util.TableRenderUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * This class holds the metadata necessary to render a table when displaytag is not being used.
 */
public class KualiTableRenderFormMetadata {
    private int viewedPageNumber;
    private int totalNumberOfPages;
    private int firstRowIndex;
    private int lastRowIndex;
    private int switchToPageNumber;

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
    private int previouslySortedColumnIndex;

    /**
     * Comment for <code>columnToSortIndex</code>
     */
    private int columnToSortIndex;

    /**
     * If it is not feasible to use an index for lookup, as with mapped properties in an Map<String, String>, it may be necessary to store a string value
     */
    private String columnToSortName;

    /**
     * When the screen was last rendered, the column name on which it was previously sorted -- this is important for toggling between ascending and descending
     * sort orders
     */
    private String previouslySortedColumnName;

    private boolean sortDescending;

    public KualiTableRenderFormMetadata() {
        sortDescending = false;
    }

    /**
     * Gets the columnToSortIndex attribute.
     * @return Returns the columnToSortIndex.
     */
    public int getColumnToSortIndex() {
        return columnToSortIndex;
    }

    /**
     * Sets the columnToSortIndex attribute value.
     * @param columnToSortIndex The columnToSortIndex to set.
     */
    public void setColumnToSortIndex(int columnToSortIndex) {
        this.columnToSortIndex = columnToSortIndex;
    }

    /**
     * Gets the previouslySortedColumnIndex attribute.
     * @return Returns the previouslySortedColumnIndex.
     */
    public int getPreviouslySortedColumnIndex() {
        return previouslySortedColumnIndex;
    }

    /**
     * Sets the previouslySortedColumnIndex attribute value.
     * @param previouslySortedColumnIndex The previouslySortedColumnIndex to set.
     */
    public void setPreviouslySortedColumnIndex(int previouslySortedColumnIndex) {
        this.previouslySortedColumnIndex = previouslySortedColumnIndex;
    }

    /**
     * Gets the resultsActualSize attribute.
     * @return Returns the resultsActualSize.
     */
    public int getResultsActualSize() {
        return resultsActualSize;
    }

    /**
     * Sets the resultsActualSize attribute value.
     * @param resultsActualSize The resultsActualSize to set.
     */
    public void setResultsActualSize(int resultsActualSize) {
        this.resultsActualSize = resultsActualSize;
    }

    /**
     * Gets the resultsLimitedSize attribute.
     * @return Returns the resultsLimitedSize.
     */
    public int getResultsLimitedSize() {
        return resultsLimitedSize;
    }

    /**
     * Sets the resultsLimitedSize attribute value.
     * @param resultsLimitedSize The resultsLimitedSize to set.
     */
    public void setResultsLimitedSize(int resultsLimitedSize) {
        this.resultsLimitedSize = resultsLimitedSize;
    }

    /**
     * Gets the switchToPageNumber attribute.
     * @return Returns the switchToPageNumber.
     */
    public int getSwitchToPageNumber() {
        return switchToPageNumber;
    }

    /**
     * Sets the switchToPageNumber attribute value.
     * @param switchToPageNumber The switchToPageNumber to set.
     */
    public void setSwitchToPageNumber(int switchToPageNumber) {
        this.switchToPageNumber = switchToPageNumber;
    }

    /**
     * Gets the viewedPageNumber attribute.
     * @return Returns the viewedPageNumber.
     */
    public int getViewedPageNumber() {
        return viewedPageNumber;
    }

    /**
     * Sets the viewedPageNumber attribute value.
     * @param viewedPageNumber The viewedPageNumber to set.
     */
    public void setViewedPageNumber(int viewedPageNumber) {
        this.viewedPageNumber = viewedPageNumber;
    }

    /**
     * Gets the totalNumberOfPages attribute.
     * @return Returns the totalNumberOfPages.
     */
    public int getTotalNumberOfPages() {
        return totalNumberOfPages;
    }

    /**
     * Sets the totalNumberOfPages attribute value.
     * @param totalNumberOfPages The totalNumberOfPages to set.
     */
    public void setTotalNumberOfPages(int totalNumberOfPages) {
        this.totalNumberOfPages = totalNumberOfPages;
    }

    /**
     * Gets the firstRowIndex attribute.
     * @return Returns the firstRowIndex.
     */
    public int getFirstRowIndex() {
        return firstRowIndex;
    }

    /**
     * Sets the firstRowIndex attribute value.
     * @param firstRowIndex The firstRowIndex to set.
     */
    public void setFirstRowIndex(int firstRowIndex) {
        this.firstRowIndex = firstRowIndex;
    }

    /**
     * Gets the lastRowIndex attribute.
     * @return Returns the lastRowIndex.
     */
    public int getLastRowIndex() {
        return lastRowIndex;
    }

    /**
     * Sets the lastRowIndex attribute value.
     * @param lastRowIndex The lastRowIndex to set.
     */
    public void setLastRowIndex(int lastRowIndex) {
        this.lastRowIndex = lastRowIndex;
    }

    /**
     * Gets the sortDescending attribute.
     * @return Returns the sortDescending.
     */
    public boolean isSortDescending() {
        return sortDescending;
    }

    /**
     * Sets the sortDescending attribute value.
     * @param sortDescending The sortDescending to set.
     */
    public void setSortDescending(boolean sortDescending) {
        this.sortDescending = sortDescending;
    }

	/**
	 * @return the columnToSortName
	 */
	public String getColumnToSortName() {
		return this.columnToSortName;
	}

	/**
	 * @param columnToSortName the columnToSortName to set
	 */
	public void setColumnToSortName(String columnToSortName) {
		this.columnToSortName = columnToSortName;
	}

	/**
	 * @return the previouslySortedColumnName
	 */
	public String getPreviouslySortedColumnName() {
		return this.previouslySortedColumnName;
	}

	/**
	 * @param previouslySortedColumnName the previouslySortedColumnName to set
	 */
	public void setPreviouslySortedColumnName(String previouslySortedColumnName) {
		this.previouslySortedColumnName = previouslySortedColumnName;
	}


    /**
     * Sets the paging form parameters to go to the first page of the list
     *
     * @param listSize size of table being rendered
     * @param maxRowsPerPage
     */
    public void jumpToFirstPage(int listSize, int maxRowsPerPage) {
        jumpToPage(0, listSize, maxRowsPerPage);
    }

    /**
     * Sets the paging form parameters to go to the last page of the list
     *
     * @param listSize size of table being rendered
     * @param maxRowsPerPage
     */
    public void jumpToLastPage(int listSize, int maxRowsPerPage) {
        jumpToPage(TableRenderUtil.computeTotalNumberOfPages(listSize, maxRowsPerPage) - 1, listSize, maxRowsPerPage);
    }

    /**
     * Sets the paging form parameters to go to the specified page of the list
     *
     * @param pageNumber first page is 0, must be non-negative.  If the list is not large enough to have the page specified, then
     *   this method will be equivalent to calling jumpToLastPage.
     * @param listSize size of table being rendered
     * @param maxRowsPerPage
     *
     * @see KualiTableRenderFormMetadata#jumpToLastPage(int, int)
     */
    public void jumpToPage(int pageNumber, int listSize, int maxRowsPerPage) {
        int totalPages = TableRenderUtil.computeTotalNumberOfPages(listSize, maxRowsPerPage);
        setTotalNumberOfPages(totalPages);
        if (pageNumber >= totalPages) {
            pageNumber = totalPages - 1;
        }
        setViewedPageNumber(pageNumber);
        setFirstRowIndex(TableRenderUtil.computeStartIndexForPage(pageNumber, listSize, maxRowsPerPage));
        setLastRowIndex(TableRenderUtil.computeLastIndexForPage(pageNumber, listSize, maxRowsPerPage));
    }

    /**
     * Sorts a list on the form according to the form metadata (sortColumName, previouslySortedColumnName)
     *
     * @param memberTableMetadata
     * @param items
     * @param maxRowsPerPage
     * @throws org.kuali.rice.kew.api.exception.WorkflowException
     */
    public void sort(List<?> items, int maxRowsPerPage) {

    	// Don't bother to sort null, empty or singleton lists
    	if (items == null || items.size() <= 1)
    		return;

        String columnToSortOn = getColumnToSortName();

        // Don't bother to sort if no column to sort on is provided
        if (StringUtils.isEmpty(columnToSortOn))
        	return;

        String previouslySortedColumnName = getPreviouslySortedColumnName();

        // We know members isn't null or empty from the check above
    	Object firstItem = items.get(0);
    	// Need to decide if the comparator is for a bean property or a mapped key on the qualififer attribute set
    	Comparator comparator = null;
    	Comparator subComparator = new Comparator<Object>() {

    		public int compare(Object o1, Object o2) {
    			if (o1 == null)
    				return -1;
    			if (o2 == null)
    				return 1;

    			if (o1 instanceof Date && o2 instanceof Date) {
    				Date d1 = (Date)o1;
    				Date d2 = (Date)o2;
    				return d1.compareTo(d2);
    			}

    			String s1 = o1.toString();
    			String s2 = o2.toString();
    			int n1=s1.length(), n2=s2.length();
    			for (int i1=0, i2=0; i1<n1 && i2<n2; i1++, i2++) {
    				char c1 = s1.charAt(i1);
    				char c2 = s2.charAt(i2);
    				if (c1 != c2) {
    					c1 = Character.toUpperCase(c1);
    					c2 = Character.toUpperCase(c2);
    					if (c1 != c2) {
    						c1 = Character.toLowerCase(c1);
    						c2 = Character.toLowerCase(c2);
    						if (c1 != c2) {
    							return c1 - c2;
    						}
    					}
    				}
    			}
    			return n1 - n2;
    		}
    	};
    	// If the columnName is a readable bean property on the first member, then it's safe to say we need a simple bean property comparator,
    	// otherwise it's a mapped property -- syntax for BeanComparator is "name" and "name(key)", respectively
    	if (PropertyUtils.isReadable(firstItem, columnToSortOn))
    		comparator = new BeanComparator(columnToSortOn, subComparator);
    	else
    		comparator = new BeanComparator(new StringBuilder().append("qualifierAsMap(").append(columnToSortOn).append(")").toString(), subComparator);


        // If the user has decided to resort by the same column that the list is currently sorted by, then assume that s/he wants to reverse the order of the sort
        if (!StringUtils.isEmpty(columnToSortOn) && !StringUtils.isEmpty(previouslySortedColumnName) && columnToSortOn.equals(previouslySortedColumnName)) {
            // we're already sorted on the same column that the user clicked on, so we reverse the list
            if (isSortDescending())
                comparator = Collections.reverseOrder(comparator);

            setSortDescending(!isSortDescending());
        } else {
        	// Track which column we're currently sorting, so that the above logic will work on the next sort
        	setPreviouslySortedColumnName(columnToSortOn);
        	setSortDescending(true);
        }

        //if the user is just going between pages no need to sort
        if (getSwitchToPageNumber() == getViewedPageNumber()) {
            Collections.sort(items, comparator);
        }

		jumpToFirstPage(items.size(), maxRowsPerPage);
    }

}