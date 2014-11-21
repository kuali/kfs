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
package org.kuali.kfs.sec.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sec.SecConstants.NonSecurityAttributeNames;
import org.kuali.kfs.sec.SecConstants.SecurityAttributeNames;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;


/**
 * Returns list of attribute names
 */
public class AttributeNameFinder extends KeyValuesBase {

    protected static final List<KeyValue> OPTIONS = new ArrayList<KeyValue>();
    static {
        OPTIONS.add(new ConcreteKeyValue(SecurityAttributeNames.ACCOUNT, SecurityAttributeNames.ACCOUNT));
        OPTIONS.add(new ConcreteKeyValue(SecurityAttributeNames.CHART, SecurityAttributeNames.CHART));
        OPTIONS.add(new ConcreteKeyValue(NonSecurityAttributeNames.OBJECT_CODE, NonSecurityAttributeNames.OBJECT_CODE));
        OPTIONS.add(new ConcreteKeyValue(SecurityAttributeNames.OBJECT_CONSOLIDATION, SecurityAttributeNames.OBJECT_CONSOLIDATION));
        OPTIONS.add(new ConcreteKeyValue(SecurityAttributeNames.OBJECT_LEVEL, SecurityAttributeNames.OBJECT_LEVEL));
        OPTIONS.add(new ConcreteKeyValue(SecurityAttributeNames.ORGANIZATION, SecurityAttributeNames.ORGANIZATION));
        OPTIONS.add(new ConcreteKeyValue(SecurityAttributeNames.PROJECT_CODE, SecurityAttributeNames.PROJECT_CODE));
        OPTIONS.add(new ConcreteKeyValue(SecurityAttributeNames.SUB_ACCOUNT, SecurityAttributeNames.SUB_ACCOUNT));
        OPTIONS.add(new ConcreteKeyValue(NonSecurityAttributeNames.SUB_OBJECT_CODE, NonSecurityAttributeNames.SUB_OBJECT_CODE));
    }
    
    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        return OPTIONS;
    }
}
