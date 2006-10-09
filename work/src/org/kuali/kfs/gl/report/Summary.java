/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.util;

/**
 * 
 * @version $Id :$
 */
public class Summary implements Comparable {
    /**
     * This number is used by TransactionReport when sorting the list of Summary objects passed to
     * TransactionReport.generateReport(). Lowest number prints first.
     */
    private int sortOrder;

    /**
     * This is the description that prints for the summary line.
     */
    private String description;

    /**
     * This is the count that displays. FIXME: Make this documentation a bit more clear.
     */
    private long count;

    /**
     * 
     */
    public Summary() {
        super();
    }

    /**
     * 
     * @param sortOrder
     * @param description
     * @param count
     */
    public Summary(int sortOrder, String description, long count) {
        this.sortOrder = sortOrder;
        this.description = description;
        this.count = count;
    }

    public Summary(int sortOrder, String description, Integer count) {
        this.sortOrder = sortOrder;
        this.description = description;
        if (count == null) {
            this.count = 0;
        }
        else {
            this.count = count.longValue();
        }
    }

    /**
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object arg0) {
        if (arg0 instanceof Summary) {
            Summary otherObject = (Summary) arg0;
            Integer otherSort = new Integer(otherObject.getSortOrder());
            Integer thisSort = new Integer(sortOrder);
            return thisSort.compareTo(otherSort);
        }
        else {
            return 0;
        }
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

}
