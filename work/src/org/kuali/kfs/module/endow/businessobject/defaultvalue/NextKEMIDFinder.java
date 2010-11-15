/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject.defaultvalue;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.valueFinder.ValueFinder;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.SequenceAccessorService;

public class NextKEMIDFinder implements ValueFinder {

    /**
     * @see org.kuali.rice.kns.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        String kemidValueSystemParam = parameterService.getParameterValue(KEMID.class, EndowParameterKeyConstants.KEMID_VALUE);

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
