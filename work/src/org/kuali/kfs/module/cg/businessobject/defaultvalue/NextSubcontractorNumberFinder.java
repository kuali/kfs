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
package org.kuali.kfs.module.cg.businessobject.defaultvalue;

import org.kuali.kfs.module.cg.businessobject.SubContractor;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * Gets the next subcontract number from the database sequence.
 */
public class NextSubcontractorNumberFinder implements ValueFinder {

    /**
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
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
        SequenceAccessorService sas = SpringContext.getBean(SequenceAccessorService.class);        
        return sas.getNextAvailableSequenceNumber("CG_SUBCNR_NBR_SEQ", SubContractor.class);
    }

    /**
     * Gets the next sequence number value as a String.
     */
    public static String getStringValue() {
        return getLongValue().toString();
    }

}
