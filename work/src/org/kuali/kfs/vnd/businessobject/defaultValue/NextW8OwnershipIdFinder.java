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
package org.kuali.kfs.vnd.businessobject.defaultValue;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * Finds the next value in the sequence for W8TypeOwnership records
 */
public class NextW8OwnershipIdFinder implements ValueFinder {
    private static volatile SequenceAccessorService sas;

    /**
     * Pulls the next value from the PUR_VNDR_W8_OWNRSHP_ID_SEQ sequence
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    @Override
    public String getValue() {
        final Long nextValue = getSequenceAccessorService().getNextAvailableSequenceNumber(KFSConstants.W8_OWNERSHIP_SEQUENCE_NAME);
        return nextValue.toString();
    }

    public static SequenceAccessorService getSequenceAccessorService() {
        if (sas == null) {
            sas = SpringContext.getBean(SequenceAccessorService.class);
        }
        return sas;
    }
}
