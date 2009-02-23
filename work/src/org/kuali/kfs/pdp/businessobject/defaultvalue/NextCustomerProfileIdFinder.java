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
package org.kuali.kfs.pdp.businessobject.defaultvalue;

import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.valueFinder.ValueFinder;
import org.kuali.rice.kns.service.SequenceAccessorService;

/**
 * Returns the next ACH Account identifier available.
 */
public class NextCustomerProfileIdFinder implements ValueFinder {

    /**
     * @see org.kuali.rice.kns.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        return getLongValue().toString();
    }

    /**
     * Get the next sequence number value as a Long.
     * 
     * @return Long
     */
    public static Long getLongValue() {
        return SpringContext.getBean(SequenceAccessorService.class).getNextAvailableSequenceNumber(PdpConstants.PDP_CUST_ID_SEQUENCE_NAME);
    }
}
