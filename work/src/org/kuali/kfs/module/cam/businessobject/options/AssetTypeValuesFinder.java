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
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.cam.businessobject.AssetType;
import org.kuali.kfs.sys.DynamicCollectionComparator;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * Value Finder for Asset Types.
 */
public class AssetTypeValuesFinder extends KeyValuesBase {

    /**
     * Returns code/description pairs of all Asset Types.
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection<AssetType> assetTypeCodes = boService.findAll(AssetType.class);
        List<KeyValue> keyValueList = new ArrayList<KeyValue>();
        keyValueList.add(new ConcreteKeyValue("", ""));
        for (AssetType at : assetTypeCodes) {
            if(at.isActive()) {
                keyValueList.add(new ConcreteKeyValue(at.getCapitalAssetTypeCode(), at.getCapitalAssetTypeDescription()));
            }
        }

        // sort alphabetically by asset type description
        DynamicCollectionComparator.sort(keyValueList, "value");

        return keyValueList;
    }
}
