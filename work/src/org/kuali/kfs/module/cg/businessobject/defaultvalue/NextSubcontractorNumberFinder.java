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
package org.kuali.module.cg.lookup.valuefinder;

import org.kuali.core.lookup.valueFinder.ValueFinder;
import org.kuali.core.service.SequenceAccessorService;
import org.kuali.kfs.context.SpringContext;

/**
 * Gets the next subcontract number from the database sequence.
 */
public class NextSubcontractorNumberFinder implements ValueFinder {

    /**
     * @see org.kuali.core.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        return getLongValue().toString();
    }

    /**
     * Gets the next sequence number value as a Long.
     * 
     * @return
     */
    public static Long getLongValue() {
        // no constant because this is the only place the sequence name is used
        Long nextVal = SpringContext.getBean(SequenceAccessorService.class).getNextAvailableSequenceNumber("CG_SUBCNR_NBR_SEQ");
        return nextVal;
    }

    /**
     * Gets the next sequence number value as a String.
     */
    public static String getStringValue() {
        return getLongValue().toString();
    }

}
