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
package org.kuali.module.cg.lookup.valueFinder;

import org.kuali.core.lookup.valueFinder.ValueFinder;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.kfs.bo.OriginationCode;

/**
 * Returns the next Proposal number available.
 * 
 * 
 */
public class NextProposalNumberFinder implements ValueFinder {

    /**
     * @see org.kuali.core.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        return getLongValue().toString();
    }
    
    public static Long getLongValue() {
        // no constant because this is the only place the sequence name is used
        return SpringServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("CGPRPSL_NBR_SEQ");
    }
}
