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
package org.kuali.kfs.module.cam.businessobject.options;


import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;


public class AssetDepreciationConventionValuesFinder extends KeyValuesBase {

    /**
    * Constructs a AssetDepreciationConventionValuesFinder.java.
    */
    public AssetDepreciationConventionValuesFinder() {
        super();
    }

    /**
    * Builds a collection of possible values to be selected from. These values are used to build out a drop down list for user
    * selection.
    * 
    * @return A list of KeyValue objects.
    * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
    */
    public List getKeyValues() {

        List<KeyValue> notificationValuesPairList = new ArrayList(4);

        notificationValuesPairList.add(new ConcreteKeyValue("FY", " Full Year"));
        notificationValuesPairList.add(new ConcreteKeyValue("HY", " Half Year"));

        return notificationValuesPairList;
    }

}
