/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.kfs.sec.businessobject.defaultvalue;

import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.rice.kns.lookup.valueFinder.ValueFinder;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.SequenceAccessorService;


/**
 * Returns the next security definition identifier available.
 */
public class NextSecurityDefinitionIdFinder implements ValueFinder {

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
        SequenceAccessorService sas = KNSServiceLocator.getSequenceAccessorService();
        return sas.getNextAvailableSequenceNumber(SecConstants.SECURITY_DEFINITION_ID_SEQUENCE_NAME);
    }
}
