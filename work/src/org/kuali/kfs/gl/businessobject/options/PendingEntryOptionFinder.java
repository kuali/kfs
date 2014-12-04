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
package org.kuali.kfs.gl.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.gl.Constant;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * An implementation of ValueFinder that allows inquirers to include no pending entries, approved pending entries,
 * or all pending entries in the results of their lookup
 */
public class PendingEntryOptionFinder extends KeyValuesBase implements ValueFinder {

    /**
     * Returns a list of key/value pairs for this ValueFinder, in this case no pending entries, approved pending entries, and all pending entries
     * @return a List of key/value pairs to populate a control
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        List labels = new ArrayList();
        labels.add(new ConcreteKeyValue(Constant.NO_PENDING_ENTRY, Constant.NO_PENDING_ENTRY));
        labels.add(new ConcreteKeyValue(Constant.APPROVED_PENDING_ENTRY, Constant.APPROVED_PENDING_ENTRY));
        labels.add(new ConcreteKeyValue(Constant.ALL_PENDING_ENTRY, Constant.ALL_PENDING_ENTRY));

        return labels;
    }

    /**
     * Returns the default value for this ValueFinder, in this case no pending entries
     * @return the key of the default value
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    public String getValue() {
        return Constant.NO_PENDING_ENTRY;
    }
}
