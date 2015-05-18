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
package org.kuali.rice.kns.util;

/**
 * This class provides utilities to support the rendering of tables in Kuali without using display tag.
 * 
 * Normally, displaytag handles the rendering of Kuali tables on various screens, but
 * there are situations where displaytag is inadequate for the task (e.g. multiple value lookups).
 * In particular, display tag does not handle POSTing of forms when switching between pages and sorting.
 * 
 */
public final class TableRenderUtil {
	
	private TableRenderUtil() {
		throw new UnsupportedOperationException("do not call");
	}
	
    /**
     * Returns the minimum number of pages needed to display a result set of the given page
     * 
     * @param resultSize number of results
     * @param maxRowsPerPage maximum number of rows 
     * 
     * @return
     */
    public static int computeTotalNumberOfPages(int resultSize, int maxRowsPerPage) {
        int numPages = resultSize / maxRowsPerPage;
        if (resultSize % maxRowsPerPage != 0) {
            // partial page
            numPages++;
        }
        return numPages;
    }
    
    /**
     * This method computes the list index of the first row of the given page
     * 
     * @param pageNumber first page is index 0
     * @param resultSize the size of the list being rendered
     * @param maxRowsPerPage max number of rows on a page
     * @return the index in the result list of the first row of the given page 
     */
    public static int computeStartIndexForPage(int pageNumber, int resultSize, int maxRowsPerPage) {
        if (pageNumber < 0 && pageNumber >= computeTotalNumberOfPages(resultSize, maxRowsPerPage)) {
            return -1;
        }
        return pageNumber * maxRowsPerPage;
    }
    
    /**
     * This method computes the index of the last row of the given page
     * 
     * @param pageNumber first page is index 0
     * @param resultSize the size of the list being rendered
     * @param maxRowsPerPage max number of rows on a page
     * @return the index in the result list of the last row of the given page 
     */
    public static int computeLastIndexForPage(int pageNumber, int resultSize, int maxRowsPerPage) {
        int startIndex = computeStartIndexForPage(pageNumber, resultSize, maxRowsPerPage);
        if (startIndex == -1) {
            return -1;
        }
        if (startIndex + maxRowsPerPage - 1 < resultSize) {
            return startIndex + maxRowsPerPage - 1;
        }
        // partial page
        return resultSize - 1;
   }
}
