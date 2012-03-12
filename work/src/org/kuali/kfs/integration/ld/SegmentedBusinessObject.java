/*
 * Copyright 2007-2008 The Kuali Foundation
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
