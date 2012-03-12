/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.sys.businessobject;

import java.util.Comparator;

import org.kuali.rice.krad.util.ObjectUtils;

/**
 * The standard comparator for AccountingLine objects
 */
public class AccountingLineComparator implements Comparator<AccountingLine> {
    
    /**
     * Compares two accounting lines based on their sequence number
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(AccountingLine rosencrantz, AccountingLine guildenstern) {
        if (ObjectUtils.isNotNull(rosencrantz) && ObjectUtils.isNotNull(guildenstern))
            if (ObjectUtils.isNotNull(rosencrantz.getSequenceNumber()) && ObjectUtils.isNotNull(guildenstern.getSequenceNumber()))
                return rosencrantz.getSequenceNumber().compareTo(guildenstern.getSequenceNumber());
        return 0;
    }
    
}
