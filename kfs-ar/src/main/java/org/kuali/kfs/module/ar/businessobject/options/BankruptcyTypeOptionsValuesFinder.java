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
package org.kuali.kfs.module.ar.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.rice.core.api.util.KeyValue; import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * This class returns list of string key value pairs for BankruptcyTypeOptions
 */
public class BankruptcyTypeOptionsValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        List<KeyValue> activeLabels = new ArrayList<KeyValue>();

        activeLabels.add(new ConcreteKeyValue(ArConstants.CHAPTER11_CODE, ArConstants.CHAPTER11));
        activeLabels.add(new ConcreteKeyValue(ArConstants.CHAPTER7_CODE, ArConstants.CHAPTER7));
        activeLabels.add(new ConcreteKeyValue(ArConstants.CHAPTER13_CODE, ArConstants.CHAPTER13));
        activeLabels.add(new ConcreteKeyValue(ArConstants.JUDGMENT_OBTAINED_CODE, ArConstants.JUDGMENT_OBTAINED));

        return activeLabels;
    }
}
