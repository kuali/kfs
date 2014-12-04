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
package org.kuali.kfs.module.endow.businessobject.defaultvalue;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.valuefinder.ValueFinder;

public class NextKEMIDFinder implements ValueFinder {

    /**
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    public String getValue() {
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        String kemidValueSystemParam = parameterService.getParameterValueAsString(KEMID.class, EndowParameterKeyConstants.KEMID_VALUE);

        if (EndowConstants.KemidValueOptions.AUTOMATIC.equals(kemidValueSystemParam)) {
            return getLongValue().toString();
        }
        else
            return KFSConstants.EMPTY_STRING;
    }

    /**
     * Gets the next sequence number as a long.
     * 
     * @return next sequence number as a long
     */
    public static Long getLongValue() {

        SequenceAccessorService sequenceAccessorService = SpringContext.getBean(SequenceAccessorService.class);
        return sequenceAccessorService.getNextAvailableSequenceNumber(EndowConstants.Sequences.END_KEMID_SEQ, KEMID.class);
    }

}
