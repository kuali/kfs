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
