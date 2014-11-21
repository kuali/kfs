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
package org.kuali.kfs.integration.ld;

import java.util.Collection;

/**
 * Labor business object for SegmentedBusinessObject Used mostly for lookups. This interface is useful when segmenting lookup
 * results. For example, If you wanted to split results for amounts in July, August, September, November, etc..., into separate
 * records, this would helpful in doing that.
 * 
 * @see org.kuali.rice.krad.bo.BusinessObject
 */
public interface SegmentedBusinessObject {

    /**
     * Determines whether to apply segments to lookup results
     * 
     * @return boolean
     */
    public boolean isLookupResultsSegmented();

    /**
     * Retrieve a collection of the property names to base the business object segmentation on
     * 
     * @return a collection of the property names to base the business object segmentation on
     */
    public Collection<String> getSegmentedPropertyNames();

    /**
     * Returns String to append to the returned object id
     * 
     * @param segmentedPropertyName - name of the segmented property for the instance
     * @return String that will be used for object id
     */
    public String getAdditionalReturnData(String segmentedPropertyName);
}
