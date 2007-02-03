/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl.util;


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
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (!(object instanceof Summary))
            return false;

        Summary that = (Summary) object;
        return this.description.equals(that.getDescription());
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