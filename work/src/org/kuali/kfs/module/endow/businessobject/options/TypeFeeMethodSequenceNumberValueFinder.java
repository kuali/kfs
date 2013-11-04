/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject.options;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * This class defines a values finder for Type Fee Method.
 */
public class TypeFeeMethodSequenceNumberValueFinder implements ValueFinder {
    public static Integer maxSequenceNumber = EndowConstants.ONE.intValue();
    
    /**
     * Returns the default value for this ValueFinder
     * @return a String with the default key
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    public String getValue() {        
        return getMaxSequenceNumber().toString();
    }
    
    /**
     * This method gets the maxSequenceNumber
     * 
     * @param return maxSequenceNumber
     */    
    public Integer getMaxSequenceNumber() {
        Integer sequenceNumberForNewLine = maxSequenceNumber;
        maxSequenceNumber += EndowConstants.ONE.intValue();
        return sequenceNumberForNewLine;
    }

    /**
     * This method sets the maxSequenceNumber
     * 
     * @param maxSequenceNumber
     */
    public void setMaxSequenceNumber(Integer newSequenceNumber) {
       maxSequenceNumber = newSequenceNumber;
    }    
    
}
