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
package org.kuali.kfs.module.tem.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

public class AttendeeTypeValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {

        List<KeyValue> attendeeType = new ArrayList<KeyValue>();
        attendeeType.add(new ConcreteKeyValue(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING));
        attendeeType.add(new ConcreteKeyValue("BG", "Business Guest"));
        attendeeType.add(new ConcreteKeyValue("EE", "Employee"));
        attendeeType.add(new ConcreteKeyValue("SP", "Spouse/Partner"));
        attendeeType.add(new ConcreteKeyValue("ST", "Student"));
        attendeeType.add(new ConcreteKeyValue("OT", "Other"));
        return attendeeType;
    }
}
