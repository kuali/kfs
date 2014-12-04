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
